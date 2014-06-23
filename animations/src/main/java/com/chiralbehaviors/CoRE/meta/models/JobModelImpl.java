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

import static java.lang.String.format;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.openjpa.persistence.QueryImpl;
import org.postgresql.pljava.TriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork_;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork_;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork_;
import com.chiralbehaviors.CoRE.event.AbstractProtocol;
import com.chiralbehaviors.CoRE.event.AbstractProtocol_;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.Protocol_;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.RuleformIdIterator;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetwork_;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.NetworkRuleform_;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetwork_;
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

    private static final Logger        log                = LoggerFactory.getLogger(JobModelImpl.class);
    private static final AtomicBoolean IGNORE_LOG_INSERTS = new AtomicBoolean();
    private static final List<String>  MODIFIED_SERVICES  = new ArrayList<>();

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
                String oldStatus = triggerData.getOld().getString("status");
                String newStatus = triggerData.getNew().getString("status");
                if (oldStatus != null && oldStatus.equals(newStatus)) {
                    //  Not a status change ;)
                    return null;
                }
                jobModel.ensureNextStateIsValid(triggerData.getNew().getString("id"),
                                                triggerData.getNew().getString("service"),
                                                oldStatus, newStatus);
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
        if (IGNORE_LOG_INSERTS.get()) {
            return;
        }
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

    protected final EntityManager em;

    protected final Kernel        kernel;

    public JobModelImpl(Model model) {
        em = model.getEntityManager();
        kernel = model.getKernel();
    }

    @Override
    public void generateImplicitJobsForExplicitJobs(Job job, Agency updatedBy) {
        boolean previous = IGNORE_LOG_INSERTS.compareAndSet(false, true);
        try {
            if (job.getStatus().getPropagateChildren()) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Generating implicit jobs for %s",
                                           job));
                }
                for (Job subJob : generateImplicitJobs(job, updatedBy)) {
                    changeStatus(subJob, getInitialState(subJob.getService()),
                                 null,
                                 "Initially available job (automatically set)");
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Not generating implicit jobs for: %s",
                                            job));
                }
            }
        } finally {
            IGNORE_LOG_INSERTS.set(previous);
        }
    }

    @Override
    public Job changeStatus(Job job, StatusCode newStatus, Agency updatedBy,
                            String notes) {
        StatusCode oldStatus = job.getStatus();
        if (oldStatus != null && oldStatus.equals(newStatus)) {
            if (log.isInfoEnabled()) {
                log.info(String.format("Job status is already set to desired status %s",
                                       job));
            }
            return job;
        }
        if (log.isInfoEnabled()) {
            log.info(String.format("Setting status %s of %s", newStatus, job));
        }
        job._setStatus(newStatus);
        if (updatedBy != null) {
            job.setUpdatedBy(updatedBy);
        }
        logJobChronology(job, notes);
        Job j = em.merge(job);
        return j;
    }

    @Override
    public void createStatusCodeChain(Product service, StatusCode[] codes,
                                      Agency updatedBy) {
        for (int i = 0; i < codes.length - 1; i++) {
            em.persist(new StatusCodeSequencing(service, codes[i],
                                                codes[i + 1], updatedBy));
        }
    }

    @Override
    public void createStatusCodeSequencings(Product service,
                                            List<Tuple<StatusCode, StatusCode>> codes,
                                            Agency updatedBy) {
        for (Tuple<StatusCode, StatusCode> p : codes) {
            em.persist(new StatusCodeSequencing(service, p.a, p.b, updatedBy));
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
            StatusCode initialState;
            try {
                initialState = getInitialState(service);
            } catch (NoResultException e) {
                throw new SQLException(
                                       String.format("%s is not allowed as a next state for Service %s coming from %s.  There is no initial state defined for the service.  Please consult the Status Code Sequencing rules.",
                                                     nextStatus, service,
                                                     currentStatus));
            }
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
    public List<Job> generateImplicitJobs(Job job, Agency updatedBy) {
        Map<Protocol, InferenceMap> protocols = getProtocols(job);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Found %s protocols for %s",
                                    protocols.size(), job));
        }
        List<Job> jobs = new ArrayList<Job>();
        for (Entry<Protocol, InferenceMap> txfm : protocols.entrySet()) {
            jobs.add(insertJob(job, txfm.getKey(), txfm.getValue(), updatedBy));
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
    public List<Job> getAllChildren(Job job) {
        List<Job> jobs = getActiveSubJobsOf(job);
        if (jobs == null || jobs.size() == 0) {
            return Collections.emptyList();
        }

        List<Job> children = new LinkedList<>();
        for (Job j : jobs) {
            List<Job> temp = getAllChildren(j);
            if (temp != null && temp.size() > 0) {
                children.addAll(temp);
            }
        }
        children.addAll(jobs);
        return children;
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

    @Override
    public List<Job> getChildJobsByService(Job parent, Product service) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_CHILD_JOBS_FOR_SERVICE,
                                                    Job.class);

        query.setParameter("parent", parent);
        query.setParameter("service", service);

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
        return em.createNamedQuery(JobChronology.FIND_FOR_JOB,
                                   JobChronology.class).setParameter("job", job).getResultList();
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
    public Map<Protocol, InferenceMap> getProtocols(Job job) {
        if (job.getStatus() == null) {
            // Bail because, dude.  We have even been initialized
            return Collections.emptyMap();
        }
        // First we try for protocols which match the current job
        List<Protocol> protocols = getProtocolsMatching(job);
        Map<Protocol, InferenceMap> matches = new LinkedHashMap<>();
        if (!protocols.isEmpty()) {
            for (Protocol protocol : protocols) {
                matches.put(protocol, NO_TRANSFORMATION);
            }
            return matches;
        }

        if (job.getStatus().getPropagateChildren()) {
            for (MetaProtocol metaProtocol : getMetaprotocols(job)) {
                matches.putAll(getProtocols(job, metaProtocol));
                if (metaProtocol.getStopOnMatch()) {
                    break;
                }
            }
        }
        return matches;
    }

    /**
     * Find protocols which match transformations specified by the meta protocol
     * 
     * @return
     */
    @Override
    public Map<Protocol, InferenceMap> getProtocols(Job job,
                                                    MetaProtocol metaProtocol) {
        Map<Protocol, InferenceMap> protocols = new LinkedHashMap<>();
        for (Protocol protocol : createMaskQuery(metaProtocol, job).getResultList()) {
            protocols.put(protocol, map(protocol, metaProtocol));
        }
        return protocols;
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
        return em.createNamedQuery(Protocol.GET_FOR_SERVICE, Protocol.class).setParameter("requestedService",
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
    public List<StatusCodeSequencing> getStatusCodeSequencingsFor(Product service) {
        TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING,
                                                                     StatusCodeSequencing.class);
        query.setParameter("service", service);

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
    public Job insertJob(Job parent, Protocol protocol, Agency updatedBy) {
        return insertJob(parent, protocol, NO_TRANSFORMATION, updatedBy);
    }

    public Job insertJob(Job parent, Protocol protocol, InferenceMap txfm,
                         Agency updatedBy) {
        boolean previous = IGNORE_LOG_INSERTS.getAndSet(true);
        try {
            Job job = new Job(kernel.getCoreAnimationSoftware());
            job.setParent(parent);
            copyIntoChild(parent, protocol, txfm, job);
            job._setStatus(kernel.getUnset());
            em.persist(job);
            em.flush();
            logJobChronology(job, "Implicit job creation by animation software");

            if (log.isTraceEnabled()) {
                log.trace(String.format("Inserted job %s from protocol %s",
                                        job, protocol));
            }
            return job;
        } finally {
            IGNORE_LOG_INSERTS.set(previous);
        }
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

    @Override
    public void logJobChronology(Job job, String notes) {
        JobChronology entry = new JobChronology(job, notes, job.getUpdatedBy());
        entry.setSequenceNumber(job.nextLogSequence());
        em.persist(entry);
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
    public Job newInitializedJob(Product service, Agency updatedBy) {
        Job job = new Job();
        job.setService(service);
        job.setUpdatedBy(updatedBy);
        job.setAssignTo(kernel.getNotApplicableAgency());
        job.setAssignToAttribute(kernel.getNotApplicableAttribute());
        job.setDeliverFrom(kernel.getNotApplicableLocation());
        job.setDeliverFromAttribute(kernel.getNotApplicableAttribute());
        job.setDeliverTo(kernel.getNotApplicableLocation());
        job.setDeliverToAttribute(kernel.getNotApplicableAttribute());
        job.setProduct(kernel.getNotApplicableProduct());
        job.setProductAttribute(kernel.getNotApplicableAttribute());
        job.setRequester(kernel.getNotApplicableAgency());
        job.setRequesterAttribute(kernel.getNotApplicableAttribute());
        job.setServiceAttribute(kernel.getNotApplicableAttribute());
        job.setQuantityUnit(kernel.getNotApplicableUnit());
        em.persist(job);
        return job;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.JobModel#newInitializedMetaProtocol()
     */
    @Override
    public MetaProtocol newInitializedMetaProtocol(Product service,
                                                   Agency updatedBy) {
        Relationship any = kernel.getAnyRelationship();
        MetaProtocol mp = new MetaProtocol(updatedBy);
        mp.setService(service);
        mp.setAssignTo(any);
        mp.setAssignToAttribute(any);
        mp.setDeliverTo(any);
        mp.setDeliverToAttribute(any);
        mp.setDeliverFrom(any);
        mp.setDeliverFromAttribute(any);
        mp.setProductOrdered(any);
        mp.setProductOrderedAttribute(any);
        mp.setRequestingAgency(any);
        mp.setRequestingAgencyAttribute(any);
        mp.setServiceAttribute(any);
        mp.setServiceType(kernel.getSameRelationship());
        mp.setQuantityUnit(any);
        em.persist(mp);
        return mp;
    }

    @Override
    public Protocol newInitializedProtocol(Product service, Agency updatedBy) {
        Protocol protocol = new Protocol();
        protocol.setService(service);
        protocol.setRequestedService(service);
        protocol.setUpdatedBy(updatedBy);
        protocol.setAssignTo(kernel.getNotApplicableAgency());
        protocol.setAssignToAttribute(kernel.getNotApplicableAttribute());
        protocol.setDeliverFrom(kernel.getNotApplicableLocation());
        protocol.setDeliverFromAttribute(kernel.getNotApplicableAttribute());
        protocol.setDeliverTo(kernel.getNotApplicableLocation());
        protocol.setDeliverToAttribute(kernel.getNotApplicableAttribute());
        protocol.setProduct(kernel.getNotApplicableProduct());
        protocol.setRequestedProduct(kernel.getNotApplicableProduct());
        protocol.setProductAttribute(kernel.getNotApplicableAttribute());
        protocol.setRequester(kernel.getNotApplicableAgency());
        protocol.setRequesterAttribute(kernel.getNotApplicableAttribute());
        protocol.setServiceAttribute(kernel.getNotApplicableAttribute());
        protocol.setQuantityUnit(kernel.getNotApplicableUnit());
        em.persist(protocol);
        return protocol;
    }

    @Override
    public void processChildChanges(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing children of Job %s", job));
        }
        List<ProductChildSequencingAuthorization> childActions = getChildActions(job);
        for (ProductChildSequencingAuthorization seq : childActions) {
            for (Job child : getActiveSubJobsOf(job)) {
                changeStatus(child,
                             seq.getNextChildStatus(),
                             kernel.getCoreAnimationSoftware(),
                             String.format("Automatically switching to %s via direct communication from parent job",
                                           seq.getNextChildStatus().getName()));
                if (seq.isReplaceProduct()) {
                    child.setProduct(job.getProduct());
                }
            }
        }
    }

    @Override
    public void processJobChange(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing change in Job %s", job));
        }
        boolean previous = IGNORE_LOG_INSERTS.getAndSet(false);
        try {
            processChildChanges(job);
            processParentChanges(job);
            processSiblingChanges(job);
        } finally {
            IGNORE_LOG_INSERTS.set(previous);
        }
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
                                 kernel.getCoreAnimationSoftware(),
                                 String.format("'Automatically switching to %s via direct communication from child job",
                                               seq.getParentStatusToSet()));
                    if (seq.isReplaceProduct()) {
                        job.getParent().setProduct(job.getProduct());
                    }
                    break;
                } else if (seq.getParent().equals(job.getParent().getService())) {
                    changeStatus(job.getParent(),
                                 seq.getParentStatusToSet(),
                                 kernel.getCoreAnimationSoftware(),
                                 String.format("'Automatically switching to %s via direct communication from child job",
                                               seq.getParentStatusToSet()));
                    if (seq.isReplaceProduct()) {
                        job.getParent().setProduct(job.getProduct());
                    }
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
                             null,
                             String.format("Automatically switching to %s via direct communication from sibling jobs",
                                           seq.getNextSiblingStatus().getName()));
                if (seq.isReplaceProduct()) {
                    sibling.setProduct(job.getProduct());
                }
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

    private void addMask(Agency agency, Relationship relationship,
                         SingularAttribute<AbstractProtocol, Agency> column,
                         CriteriaBuilder cb, CriteriaQuery<Protocol> query,
                         Root<Protocol> protocol, List<Predicate> masks) {
        mask(agency,
             relationship,
             column,
             cb,
             inferenceSubquery(agency, relationship, Agency.class,
                               AgencyNetwork.class, AgencyNetwork_.parent,
                               AgencyNetwork_.child, cb, query), protocol,
             masks);
    }

    private void addMask(Attribute attribute, Relationship relationship,
                         SingularAttribute<AbstractProtocol, Attribute> column,
                         CriteriaBuilder cb, CriteriaQuery<Protocol> query,
                         Root<Protocol> protocol, List<Predicate> masks) {
        mask(attribute,
             relationship,
             column,
             cb,
             inferenceSubquery(attribute, relationship, Attribute.class,
                               AttributeNetwork.class,
                               AttributeNetwork_.parent,
                               AttributeNetwork_.child, cb, query), protocol,
             masks);
    }

    private void addMask(Location location, Relationship relationship,
                         SingularAttribute<AbstractProtocol, Location> column,
                         CriteriaBuilder cb, CriteriaQuery<Protocol> query,
                         Root<Protocol> protocol, List<Predicate> masks) {
        mask(location,
             relationship,
             column,
             cb,
             inferenceSubquery(location, relationship, Location.class,
                               LocationNetwork.class, LocationNetwork_.parent,
                               LocationNetwork_.child, cb, query), protocol,
             masks);
    }

    private void addMask(Product product, Relationship relationship,
                         SingularAttribute<AbstractProtocol, Product> column,
                         CriteriaBuilder cb, CriteriaQuery<Protocol> query,
                         Root<Protocol> protocol, List<Predicate> masks) {
        mask(product,
             relationship,
             column,
             cb,
             inferenceSubquery(product, relationship, Product.class,
                               ProductNetwork.class, ProductNetwork_.parent,
                               ProductNetwork_.child, cb, query), protocol,
             masks);
    }

    private void addMask(Unit unit, Relationship relationship,
                         SingularAttribute<AbstractProtocol, Unit> column,
                         CriteriaBuilder cb, CriteriaQuery<Protocol> query,
                         Root<Protocol> protocol, List<Predicate> masks) {
        mask(unit,
             relationship,
             column,
             cb,
             inferenceSubquery(unit, relationship, Unit.class,
                               UnitNetwork.class, UnitNetwork_.parent,
                               UnitNetwork_.child, cb, query), protocol, masks);
    }

    /**
     * @param job
     */
    private void automaticallyGenerateImplicitJobsForExplicitJobs(String job) {
        generateImplicitJobsForExplicitJobs(em.find(Job.class, job),
                                            kernel.getCoreAnimationSoftware());
    }

    private void copyIntoChild(Job parent, Protocol protocol,
                               InferenceMap inferred, Job child) {
        if (inferred.assignTo || protocol.getAssignTo().isAnyOrSame()) {
            child.setAssignTo(parent.getAssignTo());
        } else {
            child.setAssignTo(protocol.getAssignTo());
        }
        if (inferred.assignToAttribute
            || protocol.getAssignToAttribute().isAnyOrSame()) {
            child.setAssignToAttribute(parent.getAssignToAttribute());
        } else {
            child.setAssignToAttribute(protocol.getAssignToAttribute());
        }
        if (inferred.deliverTo || protocol.getDeliverTo().isAnyOrSame()) {
            child.setDeliverTo(parent.getDeliverTo());
        } else {
            child.setDeliverTo(protocol.getDeliverTo());
        }
        if (inferred.assignToAttribute
            || protocol.getDeliverToAttribute().isAnyOrSame()) {
            child.setDeliverToAttribute(parent.getDeliverToAttribute());
        } else {
            child.setDeliverToAttribute(protocol.getDeliverToAttribute());
        }
        if (inferred.deliverFrom || protocol.getDeliverFrom().isAnyOrSame()) {
            child.setDeliverFrom(parent.getDeliverFrom());
        } else {
            child.setDeliverFrom(protocol.getDeliverFrom());
        }
        if (inferred.deliverFromAttribute
            || protocol.getDeliverFromAttribute().isAnyOrSame()) {
            child.setDeliverFromAttribute(parent.getDeliverFromAttribute());
        } else {
            child.setDeliverFromAttribute(protocol.getDeliverFromAttribute());
        }
        if (inferred.product || protocol.getProduct().isAnyOrSame()) {
            child.setProduct(parent.getProduct());
        } else {
            child.setProduct(protocol.getProduct());
        }
        if (inferred.productAttribute
            || protocol.getProductAttribute().isAnyOrSame()) {
            child.setProductAttribute(parent.getProductAttribute());
        } else {
            child.setProductAttribute(protocol.getProductAttribute());
        }
        if (inferred.requester || protocol.getRequester().isAnyOrSame()) {
            child.setRequester(parent.getRequester());
        } else {
            child.setRequester(protocol.getRequester());
        }
        if (inferred.requesterAttribute
            || protocol.getRequesterAttribute().isAnyOrSame()) {
            child.setRequesterAttribute(parent.getRequesterAttribute());
        } else {
            child.setRequesterAttribute(protocol.getRequesterAttribute());
        }
        if (inferred.serviceAttribute
            || protocol.getServiceAttribute().isAnyOrSame()) {
            child.setServiceAttribute(parent.getServiceAttribute());
        } else {
            child.setServiceAttribute(protocol.getServiceAttribute());
        }
        if (inferred.quantityUnit || protocol.getQuantityUnit().isAnyOrSame()) {
            child.setQuantityUnit(parent.getQuantityUnit());
            child.setQuantity(parent.getQuantity());
        } else {
            child.setQuantityUnit(protocol.getQuantityUnit());
            child.setQuantity(protocol.getQuantity());
        }

        //This is never transformed, so we always set to the protocol service.
        child.setService(protocol.getService());
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
    private TypedQuery<Protocol> createMaskQuery(MetaProtocol metaprotocol,
                                                 Job job) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Protocol> query = cb.createQuery(Protocol.class);

        Root<Protocol> protocol = query.from(Protocol.class);

        List<Predicate> masks = new ArrayList<>();

        // Service gets special handling.  we don't want infinite jobs due to ANY
        if (metaprotocol.getServiceType().equals(kernel.getSameRelationship())) {
            masks.add(cb.equal(protocol.get(Protocol_.requestedService),
                               job.getService()));
        } else {
            masks.add(protocol.get(Protocol_.requestedService).in(inferenceSubquery(job.getService(),
                                                                                    metaprotocol.getServiceType(),
                                                                                    Product.class,
                                                                                    ProductNetwork.class,
                                                                                    ProductNetwork_.parent,
                                                                                    ProductNetwork_.child,
                                                                                    cb,
                                                                                    query)));
        }

        // Service Attribute
        addMask(job.getServiceAttribute(), metaprotocol.getServiceAttribute(),
                AbstractProtocol_.serviceAttribute, cb, query, protocol, masks);

        // Deliver From
        addMask(job.getDeliverFrom(), metaprotocol.getDeliverFrom(),
                AbstractProtocol_.deliverFrom, cb, query, protocol, masks);

        // Deliver From Attribute
        addMask(job.getDeliverFromAttribute(),
                metaprotocol.getDeliverFromAttribute(),
                AbstractProtocol_.deliverFromAttribute, cb, query, protocol,
                masks);

        // Deliver To
        addMask(job.getDeliverTo(), metaprotocol.getDeliverTo(),
                AbstractProtocol_.deliverTo, cb, query, protocol, masks);

        // Deliver To Attribute
        addMask(job.getDeliverToAttribute(),
                metaprotocol.getDeliverToAttribute(),
                AbstractProtocol_.deliverToAttribute, cb, query, protocol,
                masks);

        // Product
        addMask(job.getProduct(), metaprotocol.getProductOrdered(),
                AbstractProtocol_.product, cb, query, protocol, masks);

        // Product Attribute
        addMask(job.getProductAttribute(),
                metaprotocol.getProductOrderedAttribute(),
                AbstractProtocol_.productAttribute, cb, query, protocol, masks);

        // Requester
        addMask(job.getRequester(), metaprotocol.getRequestingAgency(),
                AbstractProtocol_.requester, cb, query, protocol, masks);

        // Requester Attribute
        addMask(job.getRequesterAttribute(),
                metaprotocol.getRequestingAgencyAttribute(),
                AbstractProtocol_.requesterAttribute, cb, query, protocol,
                masks);

        // Assign To
        addMask(job.getAssignTo(), metaprotocol.getAssignTo(),
                AbstractProtocol_.assignTo, cb, query, protocol, masks);

        // Assign To Attribute
        addMask(job.getAssignToAttribute(),
                metaprotocol.getAssignToAttribute(),
                AbstractProtocol_.assignToAttribute, cb, query, protocol, masks);

        // Quqntity Unit
        addMask(job.getQuantityUnit(), metaprotocol.getQuantityUnit(),
                AbstractProtocol_.quantityUnit, cb, query, protocol, masks);

        query.where(masks.toArray(new Predicate[masks.size()]));
        query.select(protocol);
        TypedQuery<Protocol> tq = em.createQuery(query);
        if (log.isTraceEnabled()) {
            log.trace(String.format("mask query for %s and %s is\n%s", job,
                                    metaprotocol,
                                    tq.unwrap(QueryImpl.class).getQueryString()));
        }
        return tq;
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

    private List<Protocol> getProtocolsMatching(Job job) {
        TypedQuery<Protocol> query = em.createNamedQuery(Protocol.GET,
                                                         Protocol.class);
        query.setParameter("requestedService", job.getService());
        query.setParameter("requester", job.getRequester());
        query.setParameter("product", job.getProduct());
        query.setParameter("deliverTo", job.getDeliverTo());
        query.setParameter("deliverFrom", job.getDeliverFrom());
        query.setParameter("productAttribute", job.getProductAttribute());
        query.setParameter("assignToAttribute", job.getAssignToAttribute());
        query.setParameter("requesterAttribute", job.getRequesterAttribute());
        query.setParameter("deliverToAttribute", job.getDeliverToAttribute());
        query.setParameter("deliverFromAttribute",
                           job.getDeliverFromAttribute());
        return query.getResultList();
    }

    /**
     * @param serviceId
     * @return
     */
    private Collection<StatusCode> getStatusCodeIdsForEvent(String serviceId) {
        return getStatusCodesFor(em.find(Product.class, serviceId));
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Subquery<RuleForm> inferenceSubquery(RuleForm attribute,
                                                                                                                                                      Relationship relationship,
                                                                                                                                                      Class<RuleForm> ruleformClass,
                                                                                                                                                      Class<Network> networkClass,
                                                                                                                                                      SingularAttribute<Network, RuleForm> parent,
                                                                                                                                                      SingularAttribute<Network, RuleForm> child,
                                                                                                                                                      CriteriaBuilder cb,
                                                                                                                                                      CriteriaQuery<Protocol> query) {
        Subquery<RuleForm> inference = query.subquery(ruleformClass);
        Root<Network> root = inference.from(networkClass);
        inference.select(root.get(child));
        inference.where(cb.and(cb.equal(root.get(parent), attribute),
                               cb.equal(root.get(NetworkRuleform_.relationship),
                                        relationship)));
        return inference;
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

    /**
     * @param relationship
     * @return
     */
    private boolean isTxfm(Relationship relationship) {
        return !kernel.getAnyRelationship().equals(relationship)
               && !kernel.getSameRelationship().equals(relationship);
    }

    private void logInsertsInJobChronology(String jobId, String statusId) {
        logJobChronology(em.find(Job.class, jobId), "Initial insertion of job");
    }

    /**
     * @param protocol
     * @param metaProtocol
     * @return
     */
    private InferenceMap map(Protocol protocol, MetaProtocol metaProtocol) {
        return new InferenceMap(
                                isTxfm(metaProtocol.getAssignTo()),
                                isTxfm(metaProtocol.getAssignToAttribute()),
                                isTxfm(metaProtocol.getDeliverFrom()),
                                isTxfm(metaProtocol.getDeliverFromAttribute()),
                                isTxfm(metaProtocol.getDeliverTo()),
                                isTxfm(metaProtocol.getDeliverToAttribute()),
                                isTxfm(metaProtocol.getProductOrdered()),
                                isTxfm(metaProtocol.getProductOrderedAttribute()),
                                isTxfm(metaProtocol.getRequestingAgency()),
                                isTxfm(metaProtocol.getRequestingAgencyAttribute()),
                                isTxfm(metaProtocol.getServiceAttribute()),
                                isTxfm(metaProtocol.getQuantityUnit()));
    }

    private void mask(Agency agency, Relationship relationship,
                      SingularAttribute<AbstractProtocol, Agency> column,
                      CriteriaBuilder cb, Subquery<Agency> agencyInference,
                      Root<Protocol> protocol, List<Predicate> masks) {
        mask(agency, relationship, column, kernel.getAnyAgency(),
             kernel.getSameAgency(), kernel.getNotApplicableAgency(), cb,
             agencyInference, protocol, masks);
    }

    private void mask(Attribute attribute, Relationship relationship,
                      SingularAttribute<AbstractProtocol, Attribute> column,
                      CriteriaBuilder cb,
                      Subquery<Attribute> attributeInference,
                      Root<Protocol> protocol, List<Predicate> masks) {
        mask(attribute, relationship, column, kernel.getAnyAttribute(),
             kernel.getSameAttribute(), kernel.getNotApplicableAttribute(), cb,
             attributeInference, protocol, masks);
    }

    private void mask(Location location, Relationship relationship,
                      SingularAttribute<AbstractProtocol, Location> column,
                      CriteriaBuilder cb, Subquery<Location> locationInference,
                      Root<Protocol> protocol, List<Predicate> masks) {
        mask(location, relationship, column, kernel.getAnyLocation(),
             kernel.getSameLocation(), kernel.getNotApplicableLocation(), cb,
             locationInference, protocol, masks);
    }

    private void mask(Product product, Relationship relationship,
                      SingularAttribute<AbstractProtocol, Product> column,
                      CriteriaBuilder cb, Subquery<Product> productInference,
                      Root<Protocol> protocol, List<Predicate> masks) {
        mask(product, relationship, column, kernel.getAnyProduct(),
             kernel.getSameProduct(), kernel.getNotApplicableProduct(), cb,
             productInference, protocol, masks);
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void mask(RuleForm exist,
                                                                          Relationship relationship,
                                                                          SingularAttribute<AbstractProtocol, RuleForm> column,
                                                                          RuleForm any,
                                                                          RuleForm same,
                                                                          RuleForm notApplicable,
                                                                          CriteriaBuilder cb,
                                                                          Subquery<RuleForm> inference,
                                                                          Root<Protocol> protocol,
                                                                          List<Predicate> masks) {
        if (!relationship.equals(kernel.getAnyRelationship())) {
            Predicate mask;
            Path<RuleForm> columnPath = protocol.get(column);
            if (relationship.equals(kernel.getSameRelationship())) {
                mask = cb.equal(columnPath, exist);
            } else {
                mask = columnPath.in(inference);
            }
            masks.add(cb.or(cb.equal(columnPath, any),
                            cb.equal(columnPath, same),
                            cb.equal(columnPath, notApplicable), mask));
        }
    }

    private void mask(Unit unit, Relationship relationship,
                      SingularAttribute<AbstractProtocol, Unit> column,
                      CriteriaBuilder cb, Subquery<Unit> unitInference,
                      Root<Protocol> protocol, List<Predicate> masks) {
        mask(unit, relationship, column, kernel.getAnyUnit(),
             kernel.getSameUnit(), kernel.getNotApplicableUnit(), cb,
             unitInference, protocol, masks);
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
