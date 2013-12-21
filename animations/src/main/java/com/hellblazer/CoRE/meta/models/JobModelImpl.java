/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static com.hellblazer.CoRE.event.Job.CHRONOLOGY;
import static java.lang.String.format;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.postgresql.pljava.TriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.JobChronology;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.Protocol_;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.RuleformIdIterator;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.AgencyModel;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.LocationModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * 
 * @author hhildebrand
 * 
 */
public class JobModelImpl implements JobModel {
    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new JobModelImpl(new ModelImpl(em)));
        }
    }

    private static interface Procedure<T> {
        T call(JobModelImpl jobModel) throws Exception;
    }

    private static final Logger     log               = LoggerFactory.getLogger(JobModelImpl.class);

    private static final List<Long> MODIFIED_SERVICES = new ArrayList<Long>();

    public static void automatically_generate_implicit_jobs_for_explicit_jobs(final TriggerData triggerData)
                                                                                                            throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.automaticallyGenerateImplicitJobsForExplicitJobs(triggerData.getNew().getLong("id"));
                return null;
            }
        });
    }

    public static void ensure_next_state_is_valid(final TriggerData triggerData)
                                                                                throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureNextStateIsValid(triggerData.getNew().getLong("id"),
                                                triggerData.getNew().getLong("service"),
                                                triggerData.getOld().getLong("status"),
                                                triggerData.getNew().getLong("status"));
                return null;
            }
        });
    }

    public static void ensure_valid_child_service_and_status(final TriggerData triggerData)
                                                                                           throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus((Long) triggerData.getNew().getObject("next_child"),
                                                     (Long) triggerData.getNew().getObject("next_child_status"));
                return null;
            }
        });
    }

    public static void ensure_valid_initial_state(TriggerData triggerData)
                                                                          throws SQLException {
        long statusId = triggerData.getNew().getLong("status");
        if (statusId == 0) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Setting status of job to unset (%s)",
                                        statusId));
            }
            triggerData.getNew().updateLong("status",
                                            WellKnownStatusCode.UNSET.id());
        }
    }

    public static void ensure_valid_parent_service_and_status(final TriggerData triggerData)
                                                                                            throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus((Long) triggerData.getNew().getObject("my_parent"),
                                                     (Long) triggerData.getNew().getObject("parent_status_to_set"));
                return null;
            }
        });
    }

    public static void ensure_valid_sibling_service_and_status(final TriggerData triggerData)
                                                                                             throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus((Long) triggerData.getNew().getObject("next_sibling"),
                                                     (Long) triggerData.getNew().getObject("next_sibling_status"));
                return null;
            }
        });
    }

    public static Long get_initial_state(final Long service)
                                                            throws SQLException {
        return execute(new Procedure<Long>() {
            @Override
            public Long call(JobModelImpl jobModel) throws Exception {
                return jobModel.getInitialState(service);
            }
        });
    }

    /**
     * In database function entry point
     * 
     * @param serviceId
     * @return
     * @throws SQLException
     */
    public static Iterator<Long> get_status_code_ids_for_service(final Long serviceId)
                                                                                      throws SQLException {
        return execute(new Procedure<Iterator<Long>>() {
            @Override
            public Iterator<Long> call(JobModelImpl jobModel) throws Exception {
                return new RuleformIdIterator(
                                              jobModel.getStatusCodeIdsForEvent(serviceId).iterator());
            }
        });
    }

    /**
     * Iterate through all SCCs in the graph, testing each one to see if there
     * are any nodes reachable from the SCC that are not themselves part of the
     * SCC. If there are any, then we won't get "stuck" looping around the SCC
     * forever (there's an escape available). If there are no such nodes, then
     * once you enter the SCC, you're stuck looping around it forever.
     * 
     * @param graph
     * @return
     */
    public static boolean hasScc(Map<StatusCode, List<StatusCode>> graph) {
        for (StatusCode[] scc : new SCC(graph).getStronglyConnectedComponents()) {
            if (log.isDebugEnabled()) {
                log.debug(format("SCC: %s", Arrays.asList(scc)));
            }
            // includes nodes of scc, plus nodes they lead to
            Set<StatusCode> outgoing = new HashSet<StatusCode>();
            for (Object node : scc) {
                outgoing.addAll(graph.get(node));
            }
            if (log.isDebugEnabled()) {
                log.debug(format("Outgoing nodes: %s", outgoing));
            }
            for (StatusCode n : scc) {
                outgoing.remove(n);
            }
            // If you can't get to any other nodes outside the SCC, then it's
            // terminal
            if (outgoing.size() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean is_job_active(final Long job) throws SQLException {
        return execute(new Procedure<Boolean>() {
            @Override
            public Boolean call(JobModelImpl jobModel) throws Exception {
                return jobModel.isJobActive(job);
            }
        });
    }

    public static boolean is_terminal_state(final Long service,
                                            final Long statusCode)
                                                                  throws SQLException {
        return execute(new Procedure<Boolean>() {
            @Override
            public Boolean call(JobModelImpl jobModel) throws Exception {
                return jobModel.isTerminalState(service, statusCode);
            }
        });
    }

    public static void log_inserts_in_job_chronology(final TriggerData triggerData)
                                                                                   throws SQLException {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.logInsertsInJobChronology(triggerData.getNew().getLong("id"),
                                                   triggerData.getNew().getLong("status"));
                return null;
            }
        });
    }

    public static void log_modified_product_status_code_sequencing(final TriggerData triggerData)
                                                                                                 throws SQLException {

        MODIFIED_SERVICES.add(triggerData.getNew().getLong("service"));
    }

    public static void process_job_change(final TriggerData triggerData)
                                                                        throws SQLException {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.processJobChange(triggerData.getNew().getLong("id"));
                return null;
            }
        });
    }

    public static void validate_state_graph(TriggerData triggerData)
                                                                    throws SQLException {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.validateStateGraph();
                return null;
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    private final AgencyModel   agencyModel;
    private final EntityManager em;
    private final Kernel        kernel;
    private final LocationModel locationModel;
    private final ProductModel  productModel;

    public JobModelImpl(Model model) {
        em = model.getEntityManager();
        kernel = model.getKernel();
        productModel = model.getProductModel();
        locationModel = model.getLocationModel();
        agencyModel = model.getAgencyModel();
    }

    @Override
    public void addJobChronology(Job job, Timestamp timestamp,
                                 StatusCode status, String notes) {
        JobChronology c = new JobChronology(kernel.getCoreAnimationSoftware());
        c.setJob(job);
        c.setNotes(notes);
        c.setStatusCode(status);
        c.setTimeStamp(timestamp);
        em.persist(c);
    }

    @Override
    public void automaticallyGenerateImplicitJobsForExplicitJobs(Job job) {
        if (job.getStatus().getPropagateChildren()) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Generating implicit jobs for %s", job));
            }
            for (Job subJob : generateImplicitJobs(job)) {
                changeStatus(subJob, getInitialState(subJob.getService()),
                             "Initially available job (automatically set)");
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Not generating implicit jobs for: %s",
                                        job));
            }
        }
    }

    @Override
    public Job changeStatus(Job job, StatusCode newStatus, String notes) {
        StatusCode oldStatus = job.getStatus();
        if (oldStatus.equals(newStatus)) {
            if (log.isInfoEnabled()) {
                log.info(String.format("Job status is already set to desired status %s",
                                       job));
            }
            return job;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (log.isInfoEnabled()) {
            log.info(String.format("Setting %s status to %s", job, newStatus));
        }
        job.setStatus(newStatus);
        Job j = em.merge(job);
        addJobChronology(job, now, oldStatus, notes);
        return j;
    }

    @Override
    public void ensureNextStateIsValid(Job job, Product service,
                                       StatusCode currentStatus,
                                       StatusCode nextStatus)
                                                             throws SQLException {
        if (kernel.getUnset().equals(currentStatus)) {
            return;
        }
        if (!getNextStatusCodes(service, currentStatus).contains(nextStatus)) {
            throw new SQLException(
                                   String.format("%s is not allowed as a next state for Service %s coming from %s.  Please consult the Status Code Sequencing rules.",
                                                 nextStatus, service,
                                                 currentStatus));
        }
    }

    @Override
    public void ensureValidServiceAndStatus(Product service, StatusCode status)
                                                                               throws SQLException {
        TypedQuery<Long> query = em.createNamedQuery(StatusCodeSequencing.ENSURE_VALID_SERVICE_STATUS,
                                                     Long.class);
        query.setParameter("service", service);
        query.setParameter("code", status);
        if (query.getSingleResult() == 0) {
            throw new SQLException(
                                   String.format("'service and status must refer to valid combination in StatusCodeSequencing!  %s -> %s is not valid!'",
                                                 service, status));
        }
    }

    @Override
    public List<Job> generateImplicitJobs(Job job) {
        List<Protocol> protocols = getProtocols(job);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Found %s protocols for %s",
                                    protocols.size(), job));
        }
        List<Job> jobs = new ArrayList<Job>();
        for (Protocol protocol : protocols) {
            jobs.add(insertJob(job, protocol));
        }
        return jobs;
    }

    @Override
    public List<Job> getActiveExplicitJobs() {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_EXPLICIT_JOBS,
                                                    Job.class);
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveJobsFor(Agency agency) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_JOBS_FOR_AGENCY,
                                                    Job.class);
        query.setParameter(1, agency.getId());
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsForService(Job job, Product service) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS_FOR_SERVICE,
                                                    Job.class);
        query.setParameter(1, job.getService().getId());
        query.setParameter(2, job.getId());
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsOf(Job job) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS,
                                                    Job.class);
        query.setParameter(1, job.getId());
        return query.getResultList();
    }

    /**
     * Recursively retrieve all the active or terminated sub jobs of a given job
     * 
     * @param job
     * @return
     */
    @Override
    public Collection<Job> getAllActiveOrTerminatedSubJobsOf(Job job) {
        Set<Job> tally = new HashSet<Job>();
        return recursivelyGetActiveOrTerminalSubJobsOf(job, tally);
    }

    @Override
    public Collection<Job> getAllActiveSubJobsOf(Job job) {
        return getAllActiveSubJobsOf(job, new HashSet<Job>());
    }

    @Override
    public Collection<Job> getAllActiveSubJobsOf(Job job, Collection<Job> tally) {
        List<Job> myJobs = getActiveSubJobsOf(job);
        if (tally.addAll(myJobs)) {
            for (Job j : myJobs) {
                getAllActiveSubJobsOf(j, tally);
            }
        }
        return tally;
    }

    @Override
    public List<Job> getAllActiveSubJobsOfJobAssignedToAgency(Job parent,
                                                              Agency agency) {
        List<Job> jobs = new ArrayList<Job>();
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("agency", agency);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOfJobAssignedToAgency(parent, agency, jobs);
            }
        }
        return jobs;
    }

    @Override
    public void getAllActiveSubJobsOfJobAssignedToAgency(Job parent,
                                                         Agency agency,
                                                         List<Job> jobs) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("agency", agency);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOfJobAssignedToAgency(parent, agency, jobs);
            }
        }
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ProductChildSequencingAuthorization> getChildActions(Job job) {
        TypedQuery<ProductChildSequencingAuthorization> query = em.createNamedQuery(ProductChildSequencingAuthorization.GET_CHILD_ACTIONS,
                                                                                    ProductChildSequencingAuthorization.class);
        query.setParameter("service", job.getService());
        query.setParameter("status", job.getStatus());
        List<ProductChildSequencingAuthorization> childActions = query.getResultList();
        return childActions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.JobModel#getChronologyForJob(com.hellblazer.
     * CoRE.jsp.Job)
     */
    @Override
    public List<JobChronology> getChronologyForJob(Job job) {
        return em.createNamedQuery(CHRONOLOGY, JobChronology.class).setParameter("job",
                                                                                 job).getResultList();
    }

    /**
     * Answer the list of the active or terminated direct sub jobs of a given
     * job
     * 
     * @param job
     * @return
     */
    @Override
    public List<Job> getDirectActiveOrTerminalSubJobsOf(Job job) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_OR_TERMINATED_SUB_JOBS,
                                                    Job.class);
        query.setParameter("parent", job);
        query.setParameter("unset", kernel.getUnset());

        return query.getResultList();
    }

    @Override
    public StatusCode getInitialState(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATE,
                                                           StatusCode.class);
        query.setParameter(1, service.getId());
        try {
            return query.getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(
                                            String.format("Service %s has multiple initial states",
                                                          service));
        }
    }

    /**
     * Returns a list of initially available sub-jobs (i.e., ones that do not
     * depend on any others having been completed yet) of a given job
     * 
     * @param job
     * @return
     */
    @Override
    public List<Job> getInitialSubJobs(Job job) {
        TypedQuery<Long> query = em.createNamedQuery(Job.GET_INITIAL_SUB_JOBS,
                                                     Long.class);
        query.setParameter(1, job.getId());
        query.setParameter(2, job.getId());
        query.setParameter(3, kernel.getUnset().getId());
        query.setParameter(4, job.getId());

        List<Job> jobs = new ArrayList<Job>();
        for (Long id : query.getResultList()) {
            Job j = em.find(Job.class, id);
            if (j != null) {
                jobs.add(j);
            } else {
                throw new IllegalStateException(
                                                String.format("Cannot find existing job %s",
                                                              id));
            }
        }
        return jobs;
    }

    @Override
    public List<MetaProtocol> getMetaprotocols(Job job) {
        return em.createNamedQuery(MetaProtocol.FOR_JOB, MetaProtocol.class).setParameter("service",
                                                                                          job.getService()).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.JobModel#getMostRecentChronologyEntry(com.hellblazer
     * .CoRE.jsp.Job)
     */
    @Override
    public JobChronology getMostRecentChronologyEntry(Job job) {
        List<JobChronology> c = getChronologyForJob(job);
        if (c.isEmpty()) {
            return null;
        }
        return c.get(c.size() - 1);
    }

    @Override
    public List<StatusCode> getNextStatusCodes(Product service,
                                               StatusCode parent) {
        if (parent.equals(kernel.getUnset())) {
            return Arrays.asList(getInitialState(service));
        }
        return em.createNamedQuery(Job.GET_NEXT_STATUS_CODES, StatusCode.class).setParameter("service",
                                                                                             service).setParameter("parent",
                                                                                                                   parent).getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ProductParentSequencingAuthorization> getParentActions(Job job) {
        return em.createNamedQuery(ProductParentSequencingAuthorization.GET_PARENT_ACTIONS,
                                   ProductParentSequencingAuthorization.class).setParameter("service",
                                                                                            job.getService()).setParameter("status",
                                                                                                                           job.getStatus()).getResultList();
    }

    @Override
    public List<Protocol> getProtocols(Job job) {
        List<Protocol> protocols = new ArrayList<Protocol>();
        if (job.getStatus().getPropagateChildren()) {
            for (MetaProtocol metaProtocol : getMetaprotocols(job)) {
                for (Protocol protocol : getProtocols(job, metaProtocol)) {
                    if (!protocols.contains(protocol)) {
                        protocols.add(protocol);
                    }
                }
                if (metaProtocol.getStopOnMatch()) {
                    break;
                }
            }
        }
        return protocols;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public List<Protocol> getProtocols(Job job, MetaProtocol metaProtocol) {
        // First we try for protocols which match the current job
        List<Protocol> exactMatches = getProtocols(job.getService(),
                                                   job.getRequester(),
                                                   job.getProduct(),
                                                   job.getDeliverTo(),
                                                   job.getDeliverFrom());
        if (!exactMatches.isEmpty()) {
            return exactMatches;
        }

        // Find protocols which match transformations specified by the meta
        // protocol

        return createQuery(metaProtocol, job).getResultList();
    }

    @Override
    public List<Protocol> getProtocols(Product requestedService,
                                       Agency requester, Product product,
                                       Location deliverTo, Location deliverFrom) {
        TypedQuery<Protocol> query = em.createNamedQuery(Protocol.GET,
                                                         Protocol.class);
        query.setParameter("requestedService", requestedService);
        query.setParameter("requester", requester);
        query.setParameter("product", product);
        query.setParameter("deliverTo", deliverTo);
        query.setParameter("deliverFrom", deliverFrom);
        return query.getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ProductSiblingSequencingAuthorization> getSiblingActions(Job job) {
        TypedQuery<ProductSiblingSequencingAuthorization> query = em.createNamedQuery(ProductSiblingSequencingAuthorization.GET_SIBLING_ACTIONS,
                                                                                      ProductSiblingSequencingAuthorization.class);
        query.setParameter("service", job.getService());
        query.setParameter("status", job.getStatus());
        return query.getResultList();
    }

    @Override
    public Set<StatusCode> getStatusCodesFor(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(StatusCodeSequencing.GET_PARENT_STATUS_CODES,
                                                           StatusCode.class);
        query.setParameter("service", service);
        Set<StatusCode> result = new HashSet<StatusCode>(query.getResultList());
        query = em.createNamedQuery(StatusCodeSequencing.GET_CHILD_STATUS_CODES,
                                    StatusCode.class);
        query.setParameter("service", service);
        result.addAll(query.getResultList());
        return result;
    }

    @Override
    public List<StatusCode> getTerminalStates(Job job) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.GET_TERMINAL_STATES,
                                                           StatusCode.class);

        return query.getResultList();
    }

    @Override
    public List<Job> getTopLevelJobs() {
        TypedQuery<Job> query = em.createNamedQuery(Job.TOP_LEVEL_JOBS,
                                                    Job.class);
        return query.getResultList();
    }

    @Override
    public List<Job> getTopLevelJobsWithSubJobsAssignedToAgency(Agency agency) {
        List<Job> jobs = new ArrayList<Job>();
        for (Job job : getActiveExplicitJobs()) {
            TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                        Job.class);
            query.setParameter("parent", job);
            query.setParameter("agency", agency);
            for (Job subJob : query.getResultList()) {
                if (isActive(subJob)) {
                    jobs.add(job);
                    break;
                }
            }
        }
        return jobs;
    }

    /**
     * @param parentJob
     * @param service
     * @return
     */
    @Override
    public List<Job> getUnsetSiblings(Job parent, Product service) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_UNSET_SIBLINGS,
                                                    Job.class);
        query.setParameter("event", service);
        query.setParameter("unset", kernel.getUnset());
        query.setParameter("parent", parent);
        return query.getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public boolean hasActiveSiblings(Job job) {
        Query query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS);
        query.setParameter(1, job.getParent().getId());
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean hasInitialState(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATE,
                                                           StatusCode.class);
        query.setParameter(1, service.getId());
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean hasScs(Product service) {
        Query query = em.createNamedQuery(Job.HAS_SCS);
        query.setParameter("service", service);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    /**
     * @param service
     * @throws SQLException
     */
    @Override
    public boolean hasTerminalSCCs(Product service) throws SQLException {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        for (StatusCode currentCode : getStatusCodesFor(service)) {
            List<StatusCode> codes = getNextStatusCodes(service, currentCode);
            graph.put(currentCode, codes);
        }
        return hasScc(graph);
    }

    /**
     * Insert the new job defined by the protocol
     * 
     * @param parent
     * @param protocol
     * @return the newly created job
     */
    @Override
    public Job insertJob(Job parent, Protocol protocol) {
        Job job = new Job(kernel.getCoreAnimationSoftware());
        job.setParent(parent);
        job.setAssignTo(resolve(parent.getAssignTo(), protocol.getAssignTo()));
        job.setRequester(resolve(parent.getRequester(), protocol.getRequester()));
        job.setProduct(resolve(parent.getProduct(), protocol.getProduct()));
        job.setDeliverFrom(resolve(parent.getDeliverFrom(),
                                   protocol.getDeliverFrom()));
        job.setDeliverTo(resolve(parent.getDeliverTo(), protocol.getDeliverTo()));
        job.setService(protocol.getService());
        job.setStatus(kernel.getUnset());
        /*
         * for (ProtocolAttribute pAttribute : protocol.getAttributes()) {
         * JobAttribute attribute = pAttribute.createJobAttribute();
         * attribute.setUpdatedBy(kernel.getCoreAnimationSoftware());
         * attribute.setJob(job); em.persist(attribute); }
         */
        em.persist(job);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Inserted job %s from protocol %s", job,
                                    protocol));
        }
        return job;
    }

    @Override
    public boolean isActive(Job job) {
        return !kernel.getUnset().equals(job.getStatus())
               && !isTerminalState(job.getStatus(), job.getService());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.JobModel#isTerminalState(com.hellblazer.CoRE
     * .jsp.StatusCode, com.hellblazer.CoRE.jsp.Event)
     */
    @Override
    public boolean isTerminalState(StatusCode sc, Product service) {
        TypedQuery<Boolean> query = em.createNamedQuery(StatusCode.IS_TERMINAL_STATE,
                                                        Boolean.class);
        query.setParameter(1, service.getId());
        query.setParameter(2, sc.getId());

        return query.getSingleResult();
    }

    @Override
    public boolean isValidNextStatus(Product service, StatusCode parent,
                                     StatusCode next) {
        TypedQuery<Integer> query = em.createNamedQuery(StatusCodeSequencing.IS_VALID_NEXT_STATUS,
                                                        Integer.class);
        query.setParameter(1, service.getId());
        query.setParameter(2, parent.getId());
        query.setParameter(3, next.getId());
        return query.getSingleResult() > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.JobModel#logModifiedService(java.lang.Long)
     */
    @Override
    public void logModifiedService(Long scs) {
        MODIFIED_SERVICES.add(scs);
    }

    @Override
    public void processChildChanges(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing children of Job %s", job));
        }
        List<ProductChildSequencingAuthorization> childActions = getChildActions(job);
        for (ProductChildSequencingAuthorization seq : childActions) {
            // This can be merged into the same outer query... just doing quick
            // and dirty now

            // for each child job that is active (not UNSET or terminal) update
            // their status to this one
            // Should probably have a constraint that the given status should be
            // terminal for the event,
            // and that it can be transitioned to from any non-terminal state
            // for that event
            for (Job child : getActiveSubJobsOf(job)) {
                changeStatus(child,
                             seq.getNextChildStatus(),
                             String.format("Automatically switching to %s via direct communication from parent job %s",
                                           seq.getNextChildStatus().getName(),
                                           job));
            }
        }
    }

    @Override
    public void processJobChange(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing change in Job %s", job.getId()));
        }
        processChildChanges(job);
        processParentChanges(job);
        processSiblingChanges(job);
    }

    @Override
    public void processParentChanges(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing parent of Job %s", job));
        }

        for (ProductParentSequencingAuthorization seq : getParentActions(job)) {
            if (seq.getSetIfActiveSiblings() || !hasActiveSiblings(job)) {
                // If the parent job has the specified event then, make the
                // change
                // if the specified event is NULL then set it
                if (seq.getParent() == null
                    || seq.getParent().equals(job.getParent())) {
                    changeStatus(job.getParent(),
                                 seq.getParentStatusToSet(),
                                 String.format("'Automatically switching to %s via direct communication from child job %s",
                                               seq.getParentStatusToSet(), job));
                    break; // quit processing once we've set a status
                }
            }
        }
    }

    @Override
    public void processSiblingChanges(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing siblings of Job %s", job));
        }

        for (ProductSiblingSequencingAuthorization seq : getSiblingActions(job)) {
            for (Job sibling : getUnsetSiblings(job.getParent(),
                                                seq.getNextSibling())) {
                changeStatus(sibling,
                             seq.getNextSiblingStatus(),
                             String.format("Automatically switching to %s via direct communication from sibling job %s",
                                           seq.getNextSiblingStatus().getName(),
                                           job));
            }
        }
    }

    /**
     * @param modifiedServices
     * @throws SQLException
     */
    @Override
    public void validateStateGraph(List<Product> modifiedServices)
                                                                  throws SQLException {
        for (Product modifiedService : modifiedServices) {
            if (modifiedService == null) {
                continue;
            }
            if (!hasScs(modifiedService) || !hasInitialState(modifiedService)) {
                continue;
            }
            if (hasTerminalSCCs(modifiedService)) {
                throw new SQLException(
                                       String.format("Event '%s' has at least one terminal SCC defined in its status code graph",
                                                     modifiedService.getName()));
            }
        }
    }

    /**
     * @param job
     */
    private void automaticallyGenerateImplicitJobsForExplicitJobs(long job) {
        automaticallyGenerateImplicitJobsForExplicitJobs(em.find(Job.class, job));
    }

    private TypedQuery<Protocol> createQuery(MetaProtocol metaProtocol, Job job) {
        Product product = transform(job.getProduct(),
                                    transform(job.getProduct(),
                                              metaProtocol.getProductOrdered(),
                                              "product", job));
        Location deliverFrom = transform(job.getDeliverFrom(),
                                         transform(job.getDeliverFrom(),
                                                   metaProtocol.getDeliverFrom(),
                                                   "deliver from", job));
        Location deliverTo = transform(job.getDeliverTo(),
                                       transform(job.getDeliverTo(),
                                                 metaProtocol.getDeliverTo(),
                                                 "deliver to", job));
        Agency requester = transform(job.getRequester(),
                                     transform(job.getRequester(),
                                               metaProtocol.getRequestingAgency(),
                                               "requester", job));

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Protocol> cQuery = cb.createQuery(Protocol.class);
        Root<Protocol> root = cQuery.from(Protocol.class);
        cQuery.select(root);
        Parameter<Product> requestedServiceParameter = cb.parameter(Product.class);
        Parameter<Product> productParameter = cb.parameter(Product.class);
        Parameter<Agency> requesterParameter = cb.parameter(Agency.class);
        Parameter<Location> deliverFromParameter = cb.parameter(Location.class);
        Parameter<Location> deliverToParameter = cb.parameter(Location.class);

        Predicate predicate = cb.equal(root.get(Protocol_.requestedService),
                                       requestedServiceParameter);
        if (product != null) {
            predicate = cb.and(predicate,
                               cb.or(cb.equal(root.get(Protocol_.product),
                                              productParameter),
                                     cb.equal(root.get(Protocol_.product),
                                              kernel.getAnyProduct()),
                                     cb.equal(root.get(Protocol_.product),
                                              kernel.getSameProduct())));
        }
        if (requester != null) {
            predicate = cb.and(predicate,
                               cb.or(cb.equal(root.get(Protocol_.requester),
                                              requesterParameter),
                                     cb.equal(root.get(Protocol_.requester),
                                              kernel.getAnyAgency()),
                                     cb.equal(root.get(Protocol_.requester),
                                              kernel.getSameAgency())));
        }
        if (deliverFrom != null) {
            predicate = cb.and(predicate,
                               cb.or(cb.equal(root.get(Protocol_.deliverFrom),
                                              deliverFromParameter),
                                     cb.equal(root.get(Protocol_.deliverFrom),
                                              kernel.getAnyLocation()),
                                     cb.equal(root.get(Protocol_.deliverFrom),
                                              kernel.getSameLocation())));
        }
        if (deliverTo != null) {
            predicate = cb.and(predicate,
                               cb.or(cb.equal(root.get(Protocol_.deliverTo),
                                              deliverToParameter),
                                     cb.equal(root.get(Protocol_.deliverTo),
                                              kernel.getAnyLocation()),
                                     cb.equal(root.get(Protocol_.deliverTo),
                                              kernel.getSameLocation())));
        }
        cQuery.where(predicate);

        TypedQuery<Protocol> query = em.createQuery(cQuery);
        query.setParameter(requestedServiceParameter, job.getService());
        if (product != null) {
            query.setParameter(productParameter, product);
        }
        if (deliverFrom != null) {
            query.setParameter(deliverFromParameter, deliverFrom);
        }
        if (deliverTo != null) {
            query.setParameter(deliverToParameter, deliverTo);
        }
        if (requester != null) {
            query.setParameter(requesterParameter, requester);
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("%s, product: %s, deliverFrom: %s, deliverTo: %s, requester: %s",
                                    query, product, deliverFrom, deliverTo,
                                    requester));
        }
        return query;
    }

    /**
     * @param job
     * @param service
     * @param currentStatus
     * @param nextStatus
     * @throws SQLException
     */
    private void ensureNextStateIsValid(long job, long service,
                                        long currentStatus, long nextStatus)
                                                                            throws SQLException {
        ensureNextStateIsValid(em.find(Job.class, job),
                               em.find(Product.class, service),
                               em.find(StatusCode.class, currentStatus),
                               em.find(StatusCode.class, nextStatus));
    }

    private void ensureValidServiceAndStatus(Long service, Long status)
                                                                       throws SQLException {
        ensureValidServiceAndStatus(em.find(Product.class, service),
                                    em.find(StatusCode.class, status));
    }

    /**
     * @param service
     * @return
     */
    private Long getInitialState(Long service) {
        return getInitialState(em.find(Product.class, service)).getId();
    }

    /**
     * @param serviceId
     * @return
     */
    private Collection<StatusCode> getStatusCodeIdsForEvent(Long serviceId) {
        return getStatusCodesFor(em.find(Product.class, serviceId));
    }

    /**
     * @param job
     * @return
     */
    private boolean isJobActive(Long job) {
        return isActive(em.find(Job.class, job));
    }

    /**
     * @param service
     * @param statusCode
     * @return
     */
    private boolean isTerminalState(Long service, Long statusCode) {
        return isTerminalState(em.find(StatusCode.class, statusCode),
                               em.find(Product.class, service));
    }

    private void logInsertsInJobChronology(long jobId, long statusId) {
        addJobChronology(em.find(Job.class, jobId),
                         new Timestamp(System.currentTimeMillis()),
                         em.find(StatusCode.class, statusId),
                         "Initial insertion of job");
    }

    private void processJobChange(long jobId) {
        processJobChange(em.find(Job.class, jobId));
    }

    /**
     * Answer the list of the active or terminated sub jobs of a given job,
     * recursively
     * 
     * @param job
     * @return
     */
    private Collection<Job> recursivelyGetActiveOrTerminalSubJobsOf(Job job,
                                                                    Collection<Job> tally) {
        List<Job> myJobs = getDirectActiveOrTerminalSubJobsOf(job);
        if (tally.addAll(myJobs)) {
            for (Job sub : myJobs) {
                recursivelyGetActiveOrTerminalSubJobsOf(sub, tally);
            }
        }
        return tally;
    }

    /**
     * Resolve the value of a agency, using the original and supplied values
     */
    private Agency resolve(Agency original, Agency supplied) {
        if (kernel.getSameAgency().equals(supplied)) {
            return original;
        } else if (kernel.getNotApplicableAgency().equals(supplied)) {
            return kernel.getNotApplicableAgency();
        } else if (kernel.getAnyAgency().equals(supplied)) {
            return original;
        } else if (kernel.getOriginalAgency().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    /**
     * Resolve the value of a location, using the original and supplied values
     */
    private Location resolve(Location original, Location supplied) {
        if (kernel.getSameLocation().equals(supplied)) {
            return original;
        } else if (kernel.getAnyLocation().equals(supplied)) {
            return original;
        } else if (kernel.getNotApplicableLocation().equals(supplied)) {
            return kernel.getNotApplicableLocation();
        } else if (kernel.getOriginalLocation().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    /**
     * Resolve the value of an product, using the original and supplied values
     */
    private Product resolve(Product original, Product supplied) {
        if (kernel.getSameProduct().equals(supplied)) {
            return original;
        } else if (kernel.getNotApplicableProduct().equals(supplied)) {
            return kernel.getNotApplicableProduct();
        } else if (kernel.getAnyProduct().equals(supplied)) {
            return original;
        } else if (kernel.getOriginalProduct().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    private Agency transform(Agency original, Agency transformed) {
        if (original.equals(kernel.getAnyAgency())
            || original.equals(kernel.getSameAgency())
            || original.equals(kernel.getNotApplicableAgency())) {
            return null;
        }
        if (transformed == null) {
            return original;
        }
        if (transformed.equals(kernel.getSameAgency())) {
            return original;
        }
        if (transformed.equals(kernel.getNotApplicableAgency())
            || transformed.equals(kernel.getAnyAgency())) {
            return null;
        }
        return transformed;
    }

    private Agency transform(Agency agency, Relationship relationship,
                             String type, Job job) {
        if (kernel.getNotApplicableRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (Not Appplicable) for %s for job %s",
                                        type, job));
            }
            return kernel.getNotApplicableAgency();
        } else if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getAnyAgency();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameAgency();
        } else {
            return agencyModel.getSingleChild(agency, relationship);
        }
    }

    private Location transform(Location original, Location transformed) {
        if (original.equals(kernel.getAnyLocation())
            || original.equals(kernel.getSameLocation())
            || original.equals(kernel.getNotApplicableLocation())) {
            return null;
        }
        if (transformed == null) {
            return original;
        }
        if (transformed.equals(kernel.getSameLocation())) {
            return original;
        }
        if (transformed.equals(kernel.getNotApplicableLocation())
            || transformed.equals(kernel.getAnyLocation())) {
            return null;
        }
        return transformed;
    }

    private Location transform(Location location, Relationship relationship,
                               String type, Job job) {
        if (kernel.getNotApplicableRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (Not Applicable) for %s for job %s",
                                        type, job));
            }
            return kernel.getNotApplicableLocation();
        } else if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getNotApplicableLocation();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameLocation();
        } else {
            return locationModel.getSingleChild(location, relationship);
        }
    }

    private Product transform(Product original, Product transformed) {
        if (original.equals(kernel.getAnyProduct())
            || original.equals(kernel.getSameProduct())
            || original.equals(kernel.getNotApplicableProduct())) {
            return null;
        }
        if (transformed == null) {
            return original;
        }
        if (transformed.equals(kernel.getSameProduct())) {
            return original;
        }
        if (transformed.equals(kernel.getNotApplicableProduct())
            || transformed.equals(kernel.getAnyProduct())) {
            return null;
        }
        return transformed;
    }

    /**
     * Transform the product according to the relationship
     * 
     * @param product
     *            - the product to transform
     * @param relationship
     *            - the relationship to use for transformation
     * @param type
     *            - the type of product in the job, used for logging
     * @param job
     *            - the Job, used for logging
     * @return the transformed product, or null
     */
    private Product transform(Product product, Relationship relationship,
                              String type, Job job) {
        if (kernel.getNotApplicableRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (Not Applicable) for %s for job %s",
                                        type, job));
            }
            return kernel.getNotApplicableProduct();
        } else if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getAnyProduct();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameProduct();
        } else {
            return productModel.getSingleChild(product, relationship);
        }
    }

    /**
     * @param modifiedServices
     * @throws SQLException
     */
    private void validate_State_Graph(List<Long> modifiedServices)
                                                                  throws SQLException {
        List<Product> modified = new ArrayList<Product>(modifiedServices.size());
        for (Long id : modifiedServices) {
            modified.add(em.find(Product.class, id));
        }
        validateStateGraph(modified);
    }

    private void validateStateGraph() throws SQLException {
        try {
            validate_State_Graph(MODIFIED_SERVICES);
        } finally {
            MODIFIED_SERVICES.clear();
        }
    }
}
