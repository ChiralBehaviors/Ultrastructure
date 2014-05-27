/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.event.Job.CHRONOLOGY;
import static java.lang.String.format;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobAttribute;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.ProtocolAttribute;
import com.chiralbehaviors.CoRE.event.Protocol_;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.RuleformIdIterator;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

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

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(JobModelImpl jobModel) throws Exception;
    }

    private static final Logger       log               = LoggerFactory.getLogger(JobModelImpl.class);

    private static final List<String> MODIFIED_SERVICES = new ArrayList<>();

    public static void automatically_generate_implicit_jobs_for_explicit_jobs(final TriggerData triggerData)
                                                                                                            throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.automaticallyGenerateImplicitJobsForExplicitJobs(triggerData.getNew().getString("id"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.automatically_generate_implicit_jobs_for_explicit_jobs";
            }
        });
    }

    public static void ensure_next_state_is_valid(final TriggerData triggerData)
                                                                                throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                log.info("before ensure_next_status_is_valid");
                jobModel.ensureNextStateIsValid(triggerData.getNew().getString("id"),
                                                triggerData.getNew().getString("service"),
                                                triggerData.getOld().getString("status"),
                                                triggerData.getNew().getString("status"));
                log.info("completed ensure_next_status_is_valid");
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.ensure_next_state_is_valid";
            }
        });
    }

    public static void ensure_valid_child_service_and_status(final TriggerData triggerData)
                                                                                           throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus(triggerData.getNew().getString("next_child"),
                                                     triggerData.getNew().getString("next_child_status"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.ensure_valid_child_service_and_status";
            }
        });
    }

    public static void ensure_valid_initial_state(TriggerData triggerData)
                                                                          throws SQLException {
        String statusId = triggerData.getNew().getString("status");
        if (statusId == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Setting status of job to unset (%s)",
                                        statusId));
            }
            triggerData.getNew().updateString("status",
                                              WellKnownStatusCode.UNSET.id());
        }
    }

    public static void ensure_valid_parent_service_and_status(final TriggerData triggerData)
                                                                                            throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus(triggerData.getNew().getString("parent"),
                                                     triggerData.getNew().getString("parent_status_to_set"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.ensure_valid_parent_service_and_status";
            }
        });
    }

    public static void ensure_valid_parent_status(final TriggerData triggerData)
                                                                                throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidParentStatus(triggerData.getNew().getString("parent"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.ensure_valid_child_service_and_status";
            }
        });
    }

    public static void ensure_valid_sibling_service_and_status(final TriggerData triggerData)
                                                                                             throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.ensureValidServiceAndStatus(triggerData.getNew().getString("next_sibling"),
                                                     triggerData.getNew().getString("next_sibling_status"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.ensure_valid_sibling_service_and_status";
            }
        });
    }

    public static String get_initial_state(final String service)
                                                                throws SQLException {
        return execute(new Procedure<String>() {
            @Override
            public String call(JobModelImpl jobModel) throws Exception {
                return jobModel.getInitialState(service);
            }

            @Override
            public String toString() {
                return "JobModel.get_initial_state";
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
    public static Iterator<String> get_status_code_ids_for_service(final String serviceId)
                                                                                          throws SQLException {
        return execute(new Procedure<Iterator<String>>() {
            @Override
            public Iterator<String> call(JobModelImpl jobModel)
                                                               throws Exception {
                return new RuleformIdIterator(
                                              jobModel.getStatusCodeIdsForEvent(serviceId).iterator());
            }

            @Override
            public String toString() {
                return "JobModel.get_status_code_ids_for_service";
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

    public static boolean is_job_active(final String job) throws SQLException {
        return execute(new Procedure<Boolean>() {
            @Override
            public Boolean call(JobModelImpl jobModel) throws Exception {
                return jobModel.isJobActive(job);
            }

            @Override
            public String toString() {
                return "JobModel.is_job_active";
            }
        });
    }

    public static boolean is_terminal_state(final String service,
                                            final String statusCode)
                                                                    throws SQLException {
        return execute(new Procedure<Boolean>() {
            @Override
            public Boolean call(JobModelImpl jobModel) throws Exception {
                return jobModel.isTerminalState(service, statusCode);
            }

            @Override
            public String toString() {
                return "JobModel.is_terminal_state";
            }
        });
    }

    public static void log_inserts_in_job_chronology(final TriggerData triggerData)
                                                                                   throws SQLException {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.logInsertsInJobChronology(triggerData.getNew().getString("id"),
                                                   triggerData.getNew().getString("status"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.log_inserts_in_job_chronology";
            }
        });
    }

    public static void log_modified_product_status_code_sequencing(final TriggerData triggerData)
                                                                                                 throws SQLException {

        MODIFIED_SERVICES.add(triggerData.getNew().getString("service"));
    }

    public static void process_job_change(final TriggerData triggerData)
                                                                        throws SQLException {
        execute(new Procedure<Void>() {
            @Override
            public Void call(JobModelImpl jobModel) throws Exception {
                jobModel.processJobChange(triggerData.getNew().getString("id"));
                return null;
            }

            @Override
            public String toString() {
                return "JobModel.process_job_change";
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

            @Override
            public String toString() {
                return "JobModel.validate_state_graph";
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
        c.setStatus(status);
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

    /**
     * This query will do the work of matching protocols to networks defined by
     * jobs and metaprocols. This is the real deal except it doesn't work in the
     * db triggers for some stupid reason so we're not using it right now.
     * 
     * @param metaprotocol
     * @param job
     * @return
     */
    public TypedQuery<Protocol> createBetterQuery(MetaProtocol metaprotocol,
                                                  Job job) {

        TypedQuery<Protocol> tq = em.createNamedQuery(Protocol.GET_FOR_JOB,
                                                      Protocol.class);
        tq.setParameter(1, job.getDeliverFrom().getPrimaryKey());
        tq.setParameter(2, metaprotocol.getDeliverFrom().getPrimaryKey());
        tq.setParameter(3, job.getDeliverTo().getPrimaryKey());
        tq.setParameter(4, metaprotocol.getDeliverTo().getPrimaryKey());
        tq.setParameter(5, job.getProduct().getPrimaryKey());
        tq.setParameter(6, metaprotocol.getProductOrdered().getPrimaryKey());
        tq.setParameter(7, job.getRequester().getPrimaryKey());
        tq.setParameter(8, metaprotocol.getRequestingAgency().getPrimaryKey());
        tq.setParameter(9, job.getService().getPrimaryKey());
        tq.setParameter(10, kernel.getAnyLocation().getPrimaryKey());
        tq.setParameter(11, kernel.getSameLocation().getPrimaryKey());
        tq.setParameter(12, kernel.getAnyLocation().getPrimaryKey());
        tq.setParameter(13, kernel.getSameLocation().getPrimaryKey());
        tq.setParameter(14, kernel.getAnyProduct().getPrimaryKey());
        tq.setParameter(15, kernel.getSameProduct().getPrimaryKey());
        tq.setParameter(16, kernel.getAnyAgency().getPrimaryKey());
        tq.setParameter(17, kernel.getSameAgency().getPrimaryKey());
        return tq;
    }

    @Override
    public void createStatusCodeChain(Product service, StatusCode[] codes,
                                     Agency updatedBy) {
        for (int i = 0; i < codes.length - 1; i++) {
            em.persist(new StatusCodeSequencing(service, codes[i],
                                                codes[i + 1],
                                                updatedBy));
        }
    }

    @Override
    public void createStatusCodeSequencings(Product service,
                                            List<Tuple<StatusCode, StatusCode>> codes,
                                            Agency updatedBy) {
        for (Tuple<StatusCode, StatusCode> p : codes) {
            em.persist(new StatusCodeSequencing(service, p.a, p.b,
                                                updatedBy));
        }
    }

    @Override
    public void ensureNextStateIsValid(Job job, Product service,
                                       StatusCode currentStatus,
                                       StatusCode nextStatus)
                                                             throws SQLException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Updating %s, current: %s, next: %s", job,
                                    currentStatus, nextStatus));
        }
        if (kernel.getUnset().equals(currentStatus)) {
            StatusCode initialState = getInitialState(service);
            if (!nextStatus.equals(initialState)) {
                throw new SQLException(
                                       String.format("%s is not allowed as a next state for Service %s coming from %s.  The only allowable state is the initial state of %s  Please consult the Status Code Sequencing rules.",
                                                     nextStatus, service,
                                                     currentStatus,
                                                     initialState));
            }
            return;
        }
        if (!getNextStatusCodes(service, currentStatus).contains(nextStatus)) {
            throw new SQLException(
                                   String.format("%s is not allowed as a next state for Service %s coming from %s.  Please consult the Status Code Sequencing rules.",
                                                 nextStatus, service,
                                                 currentStatus));
        }
        if (getTerminalStates(job).contains(nextStatus)) {
            if (!getAllActiveSubJobsOf(job).isEmpty()) {
                throw new SQLException(
                                       String.format("Cannot enter terminal state %s for %s, as subjobs are still active",
                                                     nextStatus, job));
            }
        }
    }

    @Override
    public void ensureValidParentStatus(Job parent) throws SQLException {
        TypedQuery<Boolean> query = em.createNamedQuery(StatusCode.IS_TERMINAL_STATE,
                                                        Boolean.class);
        query.setParameter("service", parent.getService());
        query.setParameter("sc.id", parent.getStatus().getPrimaryKey());
        if (query.getSingleResult()) {
            throw new SQLException(
                                   String.format("'Cannot insert a job because parent %s is in a terminal state %s.'",
                                                 parent, parent.getStatus()));
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
        query.setParameter(1, agency.getPrimaryKey());
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsForService(Job job, Product service) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS_FOR_SERVICE,
                                                    Job.class);
        query.setParameter(1, job.getService().getPrimaryKey());
        query.setParameter(2, job.getPrimaryKey());
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsOf(Job job) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS,
                                                    Job.class);
        query.setParameter(1, job.getPrimaryKey());
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
    public List<Job> getAllActiveSubJobsOf(Job parent, Agency agency) {
        List<Job> jobs = new ArrayList<Job>();
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("agency", agency);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOf(parent, agency, jobs);
            }
        }
        return jobs;
    }

    @Override
    public void getAllActiveSubJobsOf(Job parent, Agency agency, List<Job> jobs) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("agency", agency);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOf(parent, agency, jobs);
            }
        }
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
    public List<JobAttribute> getAttributesForJob(Job job) {
        return em.createNamedQuery(Job.GET_ATTRIBUTES_FOR_JOB,
                                   JobAttribute.class).getResultList();
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
     * com.chiralbehaviors.CoRE.meta.JobModel#getChildActions(com.chiralbehaviors
     * .CoRE .product.Product)
     */
    @Override
    public List<ProductChildSequencingAuthorization> getChildActions(Product parent) {
        TypedQuery<ProductChildSequencingAuthorization> query = em.createNamedQuery(ProductChildSequencingAuthorization.GET_SEQUENCES,
                                                                                    ProductChildSequencingAuthorization.class);
        query.setParameter("parent", parent);
        return query.getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.JobModel#getChronologyForJob(com.
     * chiralbehaviors. CoRE.jsp.Job)
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
        query.setParameter(1, service.getPrimaryKey());
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
        query.setParameter(1, job.getPrimaryKey());
        query.setParameter(2, job.getPrimaryKey());
        query.setParameter(3, kernel.getUnset().getPrimaryKey());
        query.setParameter(4, job.getPrimaryKey());

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
        return getMetaProtocolsFor(job.getService());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.JobModel#getMetaProtocolsFor(com.
     * chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public List<MetaProtocol> getMetaProtocolsFor(Product service) {
        return em.createNamedQuery(MetaProtocol.FOR_JOB, MetaProtocol.class).setParameter("service",
                                                                                          service).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getMostRecentChronologyEntry(com
     * .chiralbehaviors .CoRE.jsp.Job)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getParentActions(com.chiralbehaviors
     * .CoRE .product.Product)
     */
    @Override
    public List<ProductParentSequencingAuthorization> getParentActions(Product service) {
        TypedQuery<ProductParentSequencingAuthorization> query = em.createNamedQuery(ProductParentSequencingAuthorization.GET_SEQUENCES,
                                                                                     ProductParentSequencingAuthorization.class);
        query.setParameter("service", service);
        return query.getResultList();
    }

    @Override
    public List<Protocol> getProtocols(Job job) {
        // First we try for protocols which match the current job
        List<Protocol> protocols = getProtocols(job.getService(),
                                                job.getRequester(),
                                                job.getProduct(),
                                                job.getDeliverTo(),
                                                job.getDeliverFrom());
        if (!protocols.isEmpty()) {
            return protocols;
        }

        protocols = new ArrayList<Protocol>();
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
        // Find protocols which match transformations specified by the meta
        // protocol

        try {
            TypedQuery<Protocol> tq = createQuery(metaProtocol, job);
            return tq.getResultList();
        } catch (NonUniqueResultException e) {
            if (log.isInfoEnabled()) {
                log.info(String.format("non unique transformation of %s, meta protocol %s",
                                       job, metaProtocol));
            }
            return Collections.emptyList();
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getProtocolsFor(com.chiralbehaviors
     * .CoRE.product.Product)
     */
    @Override
    public List<Protocol> getProtocolsFor(Product service) {
        return em.createNamedQuery(Protocol.GET_FOR_SERVICE, Protocol.class).setParameter("service",
                                                                                          service).getResultList();
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getSiblingActions(com.chiralbehaviors
     * .CoRE .product.Product)
     */
    @Override
    public List<ProductSiblingSequencingAuthorization> getSiblingActions(Product parent) {
        TypedQuery<ProductSiblingSequencingAuthorization> query = em.createNamedQuery(ProductSiblingSequencingAuthorization.GET_SEQUENCES,
                                                                                      ProductSiblingSequencingAuthorization.class);
        query.setParameter("parent", parent);
        return query.getResultList();
    }

    @Override
    public Set<StatusCode> getStatusCodesFor(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(StatusCodeSequencing.GET_PARENT_STATUS_CODES_SERVICE,
                                                           StatusCode.class);
        query.setParameter("service", service);
        Set<StatusCode> result = new HashSet<StatusCode>(query.getResultList());
        query = em.createNamedQuery(StatusCodeSequencing.GET_CHILD_STATUS_CODES_SERVICE,
                                    StatusCode.class);
        query.setParameter("service", service);
        result.addAll(query.getResultList());
        return result;
    }

    @Override
    public List<StatusCode> getTerminalStates(Job job) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.GET_TERMINAL_STATES,
                                                           StatusCode.class);
        query.setParameter(1, job.getService().getPrimaryKey());
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
        query.setParameter("service", service);
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
        query.setParameter(1, job.getParent().getPrimaryKey());
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean hasInitialState(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATE,
                                                           StatusCode.class);
        query.setParameter(1, service.getPrimaryKey());
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
        job.setRequester(parent.getRequester());
        job.setProduct(resolve(parent.getProduct(), protocol.getProduct()));
        job.setDeliverFrom(resolve(parent.getDeliverFrom(),
                                   protocol.getDeliverFrom()));
        job.setDeliverTo(resolve(parent.getDeliverTo(), protocol.getDeliverTo()));
        job.setService(protocol.getService());
        job.setStatus(kernel.getUnset());
        em.persist(job);

        for (ProtocolAttribute pAttribute : protocol.getAttributes()) {
            JobAttribute attribute = pAttribute.createJobAttribute();
            attribute.setUpdatedBy(kernel.getCoreAnimationSoftware());
            attribute.setJob(job);
            em.persist(attribute);
        }

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
     * com.chiralbehaviors.CoRE.meta.JobModel#isTerminalState(com.chiralbehaviors
     * .CoRE .jsp.StatusCode, com.chiralbehaviors.CoRE.jsp.Event)
     */
    @Override
    public boolean isTerminalState(StatusCode sc, Product service) {
        TypedQuery<Boolean> query = em.createNamedQuery(StatusCode.IS_TERMINAL_STATE,
                                                        Boolean.class);
        query.setParameter(1, service.getPrimaryKey());
        query.setParameter(2, sc.getPrimaryKey());

        return query.getSingleResult();
    }

    @Override
    public boolean isValidNextStatus(Product service, StatusCode parent,
                                     StatusCode next) {
        TypedQuery<Integer> query = em.createNamedQuery(StatusCodeSequencing.IS_VALID_NEXT_STATUS,
                                                        Integer.class);
        query.setParameter(1, service.getPrimaryKey());
        query.setParameter(2, parent.getPrimaryKey());
        query.setParameter(3, next.getPrimaryKey());
        return query.getSingleResult() > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#logModifiedService(java.lang.Long)
     */
    @Override
    public void logModifiedService(UUID scs) {
        MODIFIED_SERVICES.add(scs.toString());
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
                if (seq.getParent() == null
                    && seq.getService().equals(job.getParent().getService())) {
                    changeStatus(job.getParent(),
                                 seq.getParentStatusToSet(),
                                 String.format("'Automatically switching to %s via direct communication from child job %s",
                                               seq.getParentStatusToSet(), job));
                    break;
                } else if (seq.getParent().equals(job.getParent().getService())) {
                    changeStatus(job.getParent(),
                                 seq.getParentStatusToSet(),
                                 String.format("'Automatically switching to %s via direct communication from child job %s",
                                               seq.getParentStatusToSet(), job));
                    break;
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
    private void automaticallyGenerateImplicitJobsForExplicitJobs(String job) {
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
    private void ensureNextStateIsValid(String job, String service,
                                        String currentStatus, String nextStatus)
                                                                                throws SQLException {
        ensureNextStateIsValid(em.find(Job.class, job),
                               em.find(Product.class, service),
                               em.find(StatusCode.class, currentStatus),
                               em.find(StatusCode.class, nextStatus));
    }

    /**
     * @param object
     * @throws SQLException
     */
    private void ensureValidParentStatus(String parentId) throws SQLException {
        ensureValidParentStatus(em.find(Job.class, parentId));
    }

    private void ensureValidServiceAndStatus(String service, String status)
                                                                           throws SQLException {
        ensureValidServiceAndStatus(em.find(Product.class, service),
                                    em.find(StatusCode.class, status));
    }

    /**
     * @param service
     * @return
     */
    private String getInitialState(String service) {
        return getInitialState(em.find(Product.class, service)).getId();
    }

    /**
     * @param serviceId
     * @return
     */
    private Collection<StatusCode> getStatusCodeIdsForEvent(String serviceId) {
        return getStatusCodesFor(em.find(Product.class, serviceId));
    }

    /**
     * @param job
     * @return
     */
    private boolean isJobActive(String job) {
        return isActive(em.find(Job.class, job));
    }

    /**
     * @param service
     * @param statusCode
     * @return
     */
    private boolean isTerminalState(String service, String statusCode) {
        return isTerminalState(em.find(StatusCode.class, statusCode),
                               em.find(Product.class, service));
    }

    private void logInsertsInJobChronology(String jobId, String statusId) {
        addJobChronology(em.find(Job.class, jobId),
                         new Timestamp(System.currentTimeMillis()),
                         em.find(StatusCode.class, statusId),
                         "Initial insertion of job");
    }

    private void processJobChange(String jobId) {
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
    private void validate_State_Graph(List<String> modifiedServices)
                                                                    throws SQLException {
        List<Product> modified = new ArrayList<Product>(modifiedServices.size());
        for (String id : modifiedServices) {
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
