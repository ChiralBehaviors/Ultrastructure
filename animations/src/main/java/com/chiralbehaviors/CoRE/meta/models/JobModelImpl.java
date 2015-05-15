/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static java.lang.String.format;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform_;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.job.AbstractProtocol;
import com.chiralbehaviors.CoRE.job.AbstractProtocol_;
import com.chiralbehaviors.CoRE.job.Job;
import com.chiralbehaviors.CoRE.job.JobChronology;
import com.chiralbehaviors.CoRE.job.MetaProtocol;
import com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.Protocol;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.NetworkRuleform_;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetwork_;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.hellblazer.utils.Tuple;

/**
 *
 * @author hhildebrand
 *
 */
public class JobModelImpl implements JobModel {

    private static final Logger log = LoggerFactory.getLogger(JobModelImpl.class);

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
            // non terminal
            if (outgoing.size() == 0) {
                return true;
            }
        }
        return false;
    }

    protected final EntityManager em;
    protected final Kernel        kernel;
    protected final Model         model;

    public JobModelImpl(Model model) {
        this.model = model;
        em = model.getEntityManager();
        kernel = model.getKernel();
    }

    @Override
    public Job changeStatus(Job job, StatusCode newStatus, Agency updatedBy,
                            String notes) {
        StatusCode oldStatus = job.getStatus();
        if (oldStatus != null && oldStatus.equals(newStatus)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Job status is already set to desired status %s",
                                        job));
            }
            return job;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("%s Setting status %s of %s", notes,
                                    newStatus, job));
        }
        job.setStatus(newStatus);
        log(job, notes);
        return job;
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
    }

    @Override
    public void ensureValidParentStatus(Job parent) throws SQLException {
        if (!isTerminalState(parent.getStatus(), parent.getService())) {
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
    public Map<Protocol, Map<MetaProtocol, List<String>>> findMetaProtocolGaps(Job job) {
        List<MetaProtocol> metaProtocols = getMetaprotocols(job);
        Map<Protocol, Map<MetaProtocol, List<String>>> gaps = new HashMap<>();

        List<Protocol> protocols = getProtocolsFor(job.getService());
        for (Protocol p : protocols) {
            Map<MetaProtocol, List<String>> mpGaps = new HashMap<>();
            for (MetaProtocol mp : metaProtocols) {
                List<String> fieldsMissing = new ArrayList<>();
                if (!pathExists(job.getAssignTo(), mp.getAssignTo(),
                                p.getAssignTo(), model.getAgencyModel())) {
                    fieldsMissing.add("AssignTo");
                }
                if (!pathExists(job.getRequester(), mp.getRequester(),
                                p.getRequester(), model.getAgencyModel())) {
                    fieldsMissing.add("Requester");
                }
                if (!pathExists(job.getDeliverTo(), mp.getDeliverTo(),
                                p.getDeliverTo(), model.getLocationModel())) {
                    fieldsMissing.add("DeliverTo");
                }
                if (!pathExists(job.getDeliverFrom(), mp.getDeliverFrom(),
                                p.getDeliverFrom(), model.getLocationModel())) {
                    fieldsMissing.add("DeliverFrom");
                }
                if (!pathExists(job.getProduct(), mp.getProduct(),
                                p.getProduct(), model.getProductModel())) {
                    fieldsMissing.add("Product");
                }
                if (!pathExists(job.getService(), mp.getServiceType(),
                                p.getService(), model.getProductModel())) {
                    fieldsMissing.add("Service");
                }
                mpGaps.put(mp, fieldsMissing);
            }
            gaps.put(p, mpGaps);
        }
        return gaps;
    }

    /**
     * Returns a map of all protocols that match job.service and a list of field
     * names specifying which fields on the protocol prevent the protocol from
     * being matched. An empty list means the protocol would be matched if a job
     * were inserted.
     *
     * This method does not take metaprotocols into account.
     *
     * @param job
     * @return
     */
    @Override
    public Map<Protocol, List<String>> findProtocolGaps(Job job) {
        Map<Protocol, List<String>> gaps = new HashMap<>();
        for (Protocol p : getProtocolsFor(job.getService())) {
            gaps.put(p, findGaps(job, p));
        }
        return gaps;
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
            TypedQuery<Long> query = em.createNamedQuery(Job.EXISTING_JOB_WITH_PARENT_AND_PROTOCOL,
                                                         Long.class);
            Protocol protocol = txfm.getKey();
            query.setParameter("parent", job);
            query.setParameter("protocol", protocol);
            if (query.getSingleResult() == 0) {
                jobs.addAll(insert(job, protocol, txfm.getValue()));
            } else {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Not inserting job, as there is an existing job with parent %s from protocol %s",
                                            job, protocol));
                }
            }
        }
        return jobs;
    }

    @Override
    public void generateImplicitJobsForExplicitJobs(Job job, Agency updatedBy) {
        if (job.getStatus().getPropagateChildren()) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Generating implicit jobs for %s", job));
            }
            generateImplicitJobs(job, updatedBy);
        } else {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Not generating implicit jobs for: %s",
                                        job));
            }
        }
    }

    @Override
    public List<Job> getActiveExplicitJobs() {
        TypedQuery<Job> query = em.createNamedQuery(Job.TOP_LEVEL_JOBS,
                                                    Job.class);
        List<Job> active = new ArrayList<>();
        for (Job j : query.getResultList()) {
            if (isActive(j)) {
                active.add(j);
            }
        }
        return active;
    }

    @Override
    public List<Job> getActiveJobsFor(Agency agency) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("agency", agency);
        List<Job> active = new ArrayList<>();
        for (Job j : query.getResultList()) {
            if (isActive(j)) {
                active.add(j);
            }
        }
        return active;
    }

    @Override
    public List<Job> getActiveJobsFor(Agency agency,
                                      List<StatusCode> desiredStates) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES,
                                                    Job.class);
        query.setParameter("agency", agency);
        query.setParameter("statuses", desiredStates);
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveJobsFor(Agency agency, StatusCode desiredState) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS,
                                                    Job.class);
        query.setParameter("agency", agency);
        query.setParameter("status", desiredState);
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsForService(Job job, Product service) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_CHILD_JOBS_FOR_SERVICE,
                                                    Job.class);
        query.setParameter("parent", job);
        query.setParameter("service", service);
        List<Job> active = new ArrayList<>();
        for (Job j : query.getResultList()) {
            if (isActive(j)) {
                active.add(j);
            }
        }
        return active;
    }

    @Override
    public List<Job> getActiveSubJobsOf(Job job) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_CHILD_JOBS,
                                                    Job.class);
        query.setParameter("parent", job);
        List<Job> jobs = new ArrayList<>(query.getResultList().size());
        for (Job j : query.getResultList()) {
            if (isActive(j)) {
                jobs.add(j);
            }
        }
        return jobs;
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
                getAllActiveSubJobsOf(subJob, agency);
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
        query.setParameter("service", service);
        try {
            return query.getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(
                                            String.format("Service %s has multiple initial states: %s",
                                                          service,
                                                          query.getResultList()));
        }
    }

    /**
     * Returns the list of initial states of a service
     */
    @Override
    public List<StatusCode> getInitialStates(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATE,
                                                           StatusCode.class);
        query.setParameter("service", service);
        return query.getResultList();
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
            if (log.isTraceEnabled()) {
                log.trace(String.format("job has no status set %s", job));
            }
            // Bail because, dude.  We haven't even been initialized
            return Collections.emptyMap();
        }
        // First we try for protocols which match the current job
        List<Protocol> protocols = getProtocolsMatching(job);
        Map<Protocol, InferenceMap> matches = new LinkedHashMap<>();
        if (!protocols.isEmpty()) {
            for (Protocol protocol : protocols) {
                if (!matches.containsKey(protocol)) {
                    matches.put(protocol, NO_TRANSFORMATION);
                }
            }
            return matches;
        } else {
            if (log.isTraceEnabled()) {
                log.trace(String.format("job has no direct matches %s", job));
            }
        }

        for (MetaProtocol metaProtocol : getMetaprotocols(job)) {
            Set<Entry<Protocol, InferenceMap>> infered = getProtocols(job,
                                                                      metaProtocol).entrySet();
            if (infered.size() == 0) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("job has no infered matches %s",
                                            job));
                }
            }
            for (Map.Entry<Protocol, InferenceMap> transformed : infered) {
                if (!matches.containsKey(transformed.getKey())) {
                    matches.put(transformed.getKey(), transformed.getValue());
                }
            }
            if (metaProtocol.getStopOnMatch()) {
                break;
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
            if (!protocols.containsKey(protocol)) {
                protocols.put(protocol, map(protocol, metaProtocol));
            }
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
        return em.createNamedQuery(Protocol.GET_FOR_SERVICE, Protocol.class).setParameter("service",
                                                                                          service).getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ProductSelfSequencingAuthorization> getSelfActions(Job job) {
        TypedQuery<ProductSelfSequencingAuthorization> query = em.createNamedQuery(ProductSelfSequencingAuthorization.GET_SELF_ACTIONS,
                                                                                   ProductSelfSequencingAuthorization.class);
        query.setParameter("service", job.getService());
        query.setParameter("status", job.getStatus());
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ProductSiblingSequencingAuthorization> getSiblingActions(Job job) {
        TypedQuery<ProductSiblingSequencingAuthorization> query = em.createNamedQuery(ProductSiblingSequencingAuthorization.GET_SIBLING_ACTIONS,
                                                                                      ProductSiblingSequencingAuthorization.class);
        query.setParameter("parent", job.getService());
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
        query.setParameter("service", job.getService());
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
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_CHILD_JOBS,
                                                    Job.class);
        query.setParameter("parent", job.getParent());
        for (Job j : query.getResultList()) {
            if (isActive(j)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasInitialState(Product service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATE,
                                                           StatusCode.class);
        query.setParameter("service", service);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    /**
     * @param service
     * @throws SQLException
     */
    @Override
    public boolean hasNonTerminalSCCs(Product service) throws SQLException {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        Set<StatusCode> statusCodes = getStatusCodesFor(service);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Status codes for %s: %s",
                                    service.getName(), statusCodes));
        }
        for (StatusCode currentCode : statusCodes) {
            List<StatusCode> codes = getNextStatusCodes(service, currentCode);
            graph.put(currentCode, codes);
        }
        return hasScc(graph);
    }

    @Override
    public boolean hasScs(Product service) {
        Query query = em.createNamedQuery(Job.HAS_SCS);
        query.setParameter("service", service);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    /**
     * Insert new jobs defined by the protocol
     *
     * @param parent
     * @param protocol
     * @return the newly created job
     */
    @Override
    public List<Job> insert(Job parent, Protocol protocol) {
        return insert(parent, protocol, NO_TRANSFORMATION);
    }

    public List<Job> insert(Job parent, Protocol protocol, InferenceMap txfm) {
        if (parent.getDepth() > MAXIMUM_JOB_DEPTH) {
            throw new IllegalStateException(
                                            String.format("Maximum job depth exceeded.  parent: %s, protocol: %s",
                                                          parent, protocol));
        }
        List<Job> jobs = new ArrayList<>();
        if (protocol.getChildrenRelationship().equals(kernel.getNotApplicableRelationship())) {
            Job job = new Job(model.getCurrentPrincipal().getPrincipal());
            insert(job,
                   parent,
                   protocol,
                   txfm,
                   resolve(txfm.product, protocol.getProduct(),
                           parent.getProduct(), protocol.getChildProduct()));
            jobs.add(job);
        } else {
            for (Product child : model.getProductModel().getChildren(parent.getProduct(),
                                                                     protocol.getChildrenRelationship())) {
                Job job = new Job(model.getCurrentPrincipal().getPrincipal());
                insert(job, parent, protocol, txfm, child);
                jobs.add(job);
            }
        }
        return jobs;
    }

    @Override
    public boolean isActive(Job job) {
        return !isTerminalState(job.getStatus(), job.getService());
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
        TypedQuery<Long> query = em.createNamedQuery(StatusCode.IS_TERMINAL_STATE,
                                                     Long.class);
        query.setParameter("service", service);
        query.setParameter("statusCode", sc);

        return query.getSingleResult() > 0;
    }

    @Override
    public void log(Job job, String notes) {
        if (job.getStatus() == null) {
            job.setStatus(kernel.getUnset()); // Prophylactic against recursive error disease
        }
        TypedQuery<Integer> query = em.createNamedQuery(JobChronology.HIGHEST_SEQUENCE_FOR_JOB,
                                                        Integer.class);
        query.setParameter("job", job);
        Integer result = query.getSingleResult();
        int highest = result == null ? 0 : result;
        JobChronology entry = new JobChronology(job, notes, highest + 1);
        em.persist(entry);
    }

    @Override
    public Job newInitializedJob(Product service, Agency updatedBy) {
        Job job = new Job();
        job.setService(service);
        job.setUpdatedBy(updatedBy);
        job.setAssignTo(kernel.getNotApplicableAgency());
        job.setDeliverFrom(kernel.getNotApplicableLocation());
        job.setDeliverTo(kernel.getNotApplicableLocation());
        job.setProduct(kernel.getNotApplicableProduct());
        job.setRequester(kernel.getNotApplicableAgency());
        job.setQuantityUnit(kernel.getNotApplicableUnit());
        job.setStatus(kernel.getUnset());
        em.persist(job);

        JobChronology entry = new JobChronology(job,
                                                "Initial insertion of job", 1);
        em.persist(entry);
        return job;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.JobModel#newInitializedMetaProtocol()
     */
    @Override
    public MetaProtocol newInitializedMetaProtocol(Product service,
                                                   Agency updatedBy) {
        Relationship any = kernel.getAnyRelationship();
        MetaProtocol mp = new MetaProtocol();
        mp.setUpdatedBy(updatedBy);
        mp.setService(service);
        mp.setAssignTo(any);
        mp.setDeliverTo(any);
        mp.setDeliverFrom(any);
        mp.setProduct(any);
        mp.setRequester(any);
        mp.setServiceType(kernel.getSameRelationship());
        mp.setQuantityUnit(any);
        em.persist(mp);
        return mp;
    }

    @Override
    public Protocol newInitializedProtocol(Product service, Agency updatedBy) {
        Protocol protocol = new Protocol();
        protocol.setUpdatedBy(updatedBy);
        protocol.setService(service);
        protocol.setAssignTo(kernel.getNotApplicableAgency());
        protocol.setDeliverFrom(kernel.getNotApplicableLocation());
        protocol.setDeliverTo(kernel.getNotApplicableLocation());
        protocol.setProduct(kernel.getNotApplicableProduct());
        protocol.setRequester(kernel.getNotApplicableAgency());
        protocol.setQuantityUnit(kernel.getNotApplicableUnit());
        protocol.setChildAssignTo(kernel.getSameAgency());
        protocol.setChildDeliverFrom(kernel.getSameLocation());
        protocol.setChildDeliverTo(kernel.getSameLocation());
        protocol.setChildrenRelationship(kernel.getNotApplicableRelationship());
        protocol.setChildProduct(kernel.getSameProduct());
        protocol.setChildService(kernel.getSameProduct());
        protocol.setChildQuantityUnit(kernel.getNotApplicableUnit());
        em.persist(protocol);
        return protocol;
    }

    @Override
    public void processChildSequencing(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing children of Job %s", job));
        }
        Deque<ProductChildSequencingAuthorization> actions = new ArrayDeque<>(
                                                                              getChildActions(job));
        while (!actions.isEmpty()) {
            ProductChildSequencingAuthorization auth = actions.pop();
            List<ProductChildSequencingAuthorization> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty()
                   && actions.peekFirst().getNextChild().equals(auth.getNextChild())) {
                grouped.add(actions.pop());
            }
            processChildren(job, grouped);
        }
    }

    @Override
    public void processJobSequencing(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing change in Job %s", job));
        }
        //process parents last so we can close out child jobs
        processChildSequencing(job);
        processSiblingSequencing(job);
        processSelfSequencing(job);
        processParentSequencing(job);
    }

    @Override
    public void processParentSequencing(Job job) {
        if (job.getParent() == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("No parent of job, not processing parent sequencing: %s",
                                        job));
            }
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing parent of Job %s", job));
        }

        Deque<ProductParentSequencingAuthorization> actions = new ArrayDeque<>(
                                                                               getParentActions(job));
        while (!actions.isEmpty()) {
            ProductParentSequencingAuthorization auth = actions.pop();
            List<ProductParentSequencingAuthorization> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty()
                   && actions.peekFirst().getParent().equals(auth.getParent())) {
                grouped.add(actions.pop());
            }
            processParents(job, grouped);
        }
    }

    @Override
    public void processSelfSequencing(Job job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing self of Job %s", job));
        }

        for (ProductSelfSequencingAuthorization seq : getSelfActions(job)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing %s", seq));
            }
            try {
                ensureNextStateIsValid(job, job.getService(), job.getStatus(),
                                       seq.getStatusToSet());
                changeStatus(job, seq.getStatusToSet(),
                             model.getCurrentPrincipal().getPrincipal(),
                             "Automatically switching staus via direct communication from sibling jobs");
            } catch (Throwable e) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("invalid self status sequencing %s",
                                            job), e);
                }
                log(job,
                    String.format("error changing status of job of %s to: %s in self sequencing %s\n%s",
                                  job.getId(), seq.getStatusToSet(),
                                  seq.getId(), e));
            }
        }
    }

    @Override
    public void processSiblingSequencing(Job job) {
        if (job.getParent() == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Job does not have a parent, so not processing siblings"));
            }
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing siblings of Job %s", job));
        }

        Deque<ProductSiblingSequencingAuthorization> actions = new ArrayDeque<>(
                                                                                getSiblingActions(job));
        while (!actions.isEmpty()) {
            ProductSiblingSequencingAuthorization auth = actions.pop();
            List<ProductSiblingSequencingAuthorization> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty()
                   && actions.peekFirst().getNextSibling().equals(auth.getNextSibling())) {
                grouped.add(actions.pop());
            }
            processSiblings(job, grouped);
        }
    }

    /**
     * @param modifiedServices
     * @throws SQLException
     */
    @Override
    public void validateStateGraph(Collection<Product> modifiedServices)
                                                                        throws SQLException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("modified services %s", modifiedServices));
        }
        for (Product modifiedService : modifiedServices) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Validating state graph for %s",
                                        modifiedService.getName()));
            }
            if (modifiedService == null) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("null modified service!"));
                }
                continue;
            }
            if (!hasScs(modifiedService)) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("No status code sequencing for %s",
                                            modifiedService.getName()));
                }
                continue;
            }
            if (hasNonTerminalSCCs(modifiedService)) {
                throw new SQLException(
                                       String.format("Event '%s' has at least one non terminal SCC defined in its status code graph",
                                                     modifiedService.getName()));
            }
            List<StatusCode> initialStates = getInitialStates(modifiedService);
            if (initialStates.isEmpty()) {
                throw new SQLException(
                                       String.format("Event '%s' has no initial state defined in its status code graph",
                                                     modifiedService.getName()));
            }
            if (initialStates.size() > 1) {
                throw new SQLException(
                                       String.format("Event '%s' has multiple initial state defined in its status code graph: %s",
                                                     modifiedService.getName(),
                                                     initialStates));
            }
            if (hasNonTerminalSCCs(modifiedService)) {
                throw new SQLException(
                                       String.format("Event '%s' has at least one non terminal SCC defined in its status code graph",
                                                     modifiedService.getName()));
            }
        }
    }

    private void copyIntoChild(Job parent, Protocol protocol,
                               InferenceMap inferred, Job child) {
        child.setAssignTo(resolve(inferred.assignTo, protocol.getAssignTo(),
                                  parent.getAssignTo(),
                                  protocol.getChildAssignTo()));
        child.setDeliverTo(resolve(inferred.deliverTo, protocol.getDeliverTo(),
                                   parent.getDeliverTo(),
                                   protocol.getChildDeliverTo()));
        child.setDeliverFrom(resolve(inferred.deliverFrom,
                                     protocol.getDeliverFrom(),
                                     parent.getDeliverFrom(),
                                     protocol.getChildDeliverFrom()));
        child.setService(resolve(false, protocol.getService(),
                                 parent.getService(),
                                 protocol.getChildService()));
        if (inferred.requester || protocol.getRequester().isAnyOrSame()) {
            child.setRequester(parent.getRequester());
        } else {
            child.setRequester(protocol.getRequester());
        }

        if (protocol.getChildQuantityUnit().isSame()) {
            if (inferred.quantityUnit || protocol.getQuantityUnit().isAny()) {
                child.setQuantity(parent.getQuantity());
                child.setQuantityUnit(parent.getQuantityUnit());
            }
        } else if (protocol.getChildQuantityUnit().isCopy()) {
            child.setQuantity(parent.getQuantity());
            child.setQuantityUnit(parent.getQuantityUnit());
        } else {
            child.setQuantity(protocol.getChildQuantity());
            child.setQuantityUnit(protocol.getChildQuantityUnit());
        }
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
            masks.add(cb.equal(protocol.get(AbstractProtocol_.service),
                               job.getService()));
        } else {
            masks.add(protocol.get(AbstractProtocol_.service).in(inferenceSubquery(job.getService(),
                                                                                   metaprotocol.getServiceType(),
                                                                                   Product.class,
                                                                                   ProductNetwork.class,
                                                                                   ProductNetwork_.parent,
                                                                                   ProductNetwork_.child,
                                                                                   cb,
                                                                                   query)));
        }

        // Deliver From
        addMask(job.getDeliverFrom(), metaprotocol.getDeliverFrom(),
                AbstractProtocol_.deliverFrom, cb, query, protocol, masks);

        // Deliver To
        addMask(job.getDeliverTo(), metaprotocol.getDeliverTo(),
                AbstractProtocol_.deliverTo, cb, query, protocol, masks);

        // Product
        addMask(job.getProduct(), metaprotocol.getProduct(),
                AbstractProtocol_.product, cb, query, protocol, masks);

        // Requester
        addMask(job.getRequester(), metaprotocol.getRequester(),
                AbstractProtocol_.requester, cb, query, protocol, masks);

        // Assign To
        addMask(job.getAssignTo(), metaprotocol.getAssignTo(),
                AbstractProtocol_.assignTo, cb, query, protocol, masks);

        // Quqntity Unit
        addMask(job.getQuantityUnit(), metaprotocol.getQuantityUnit(),
                AbstractProtocol_.quantityUnit, cb, query, protocol, masks);

        query.where(masks.toArray(new Predicate[masks.size()]));
        query.select(protocol).distinct(true);
        TypedQuery<Protocol> tq = em.createQuery(query);
        return tq;
    }

    /**
     * @param job
     * @param p
     * @return
     */
    private List<String> findGaps(Job job, Protocol p) {
        List<String> missingFields = new LinkedList<>();
        if (!job.getRequester().equals(p.getRequester())) {
            missingFields.add("Requester");
        }
        if (!job.getProduct().equals(p.getProduct())) {
            missingFields.add("Product");
        }
        if (!job.getDeliverTo().equals(p.getDeliverTo())) {
            missingFields.add("DeliverTo");
        }
        if (!job.getDeliverFrom().equals(p.getDeliverFrom())) {
            missingFields.add("DeliverFrom");
        }
        return missingFields;

    }

    private List<Protocol> getProtocolsMatching(Job job) {
        TypedQuery<Protocol> query = em.createNamedQuery(Protocol.GET,
                                                         Protocol.class);
        query.setParameter("service", job.getService());
        query.setParameter("requester", job.getRequester());
        query.setParameter("product", job.getProduct());
        query.setParameter("deliverTo", job.getDeliverTo());
        query.setParameter("deliverFrom", job.getDeliverFrom());
        query.setParameter("assignTo", job.getAssignTo());
        return query.getResultList();
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
     * @param relationship
     * @return
     */
    private boolean isTxfm(Relationship relationship) {
        return !kernel.getAnyRelationship().equals(relationship)
               && !kernel.getSameRelationship().equals(relationship);
    }

    /**
     * @param protocol
     * @param metaProtocol
     * @return
     */
    private InferenceMap map(Protocol protocol, MetaProtocol metaProtocol) {
        return new InferenceMap(isTxfm(metaProtocol.getAssignTo()),
                                isTxfm(metaProtocol.getDeliverFrom()),
                                isTxfm(metaProtocol.getDeliverTo()),
                                isTxfm(metaProtocol.getProduct()),
                                isTxfm(metaProtocol.getRequester()),
                                isTxfm(metaProtocol.getQuantityUnit()));
    }

    /**
     * @param mpRelationship
     * @param child
     * @param job
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuth extends AttributeAuthorization<RuleForm, Network>, AttributeType extends AttributeValue<RuleForm>> boolean pathExists(RuleForm rf,
                                                                                                                                                                                                                                                     Relationship mpRelationship,
                                                                                                                                                                                                                                                     RuleForm child,
                                                                                                                                                                                                                                                     NetworkedModel<RuleForm, Network, AttributeAuth, AttributeType> netModel) {
        if (mpRelationship.isAnyOrSame() || mpRelationship.isNotApplicable()) {
            return true;
        }
        if (!netModel.isAccessible(rf, mpRelationship, child)) {
            return false;
        }
        return true;
    }

    /**
     * Process the child squencing auths for the job. The authorizations are
     * grouped by the same next child, ordered by sequence number. This method
     * finds the first successful transition from the grouped list and returns
     * after processing that auth
     *
     * @param job
     * @param grouped
     */
    private void processChildren(Job job,
                                 List<ProductChildSequencingAuthorization> grouped) {
        if (grouped.isEmpty()) {
            return;
        }
        for (Job child : getActiveSubJobsForService(job,
                                                    grouped.get(0).getNextChild())) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing child %s", child));
            }
            for (ProductChildSequencingAuthorization seq : grouped) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Processing %s", seq));
                }
                try {
                    ensureNextStateIsValid(child, child.getService(),
                                           child.getStatus(),
                                           seq.getNextChildStatus());
                    changeStatus(child, seq.getNextChildStatus(),
                                 model.getCurrentPrincipal().getPrincipal(),
                                 "Automatically switching status via direct communication from parent job");
                    if (seq.isReplaceProduct()) {
                        child.setProduct(job.getProduct());
                    }
                    break;
                } catch (Throwable e) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid child status sequencing %s",
                                                child), e);
                    }
                    log(child,
                        String.format("error changing status of child of %s to: %s in child sequencing %s\n%s",
                                      job.getId(), seq.getNextChildStatus(),
                                      seq.getId(), e));
                }
            }
        }
    }

    /**
     * Process the parent squencing auths for the job. The authorizations are
     * grouped by the same parent, ordered by sequence number. This method finds
     * the first successful transition from the grouped list and returns after
     * processing that auth
     *
     * @param job
     * @param grouped
     */
    private void processParents(Job job,
                                List<ProductParentSequencingAuthorization> grouped) {
        for (ProductParentSequencingAuthorization seq : grouped) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing %s", seq));
            }
            if (seq.getSetIfActiveSiblings() || !hasActiveSiblings(job)) {
                if (seq.getParent() == null
                    && seq.getService().equals(job.getParent().getService())) {
                    try {
                        ensureNextStateIsValid(job.getParent(),
                                               job.getParent().getService(),
                                               job.getParent().getStatus(),
                                               seq.getParentStatusToSet());
                        changeStatus(job.getParent(),
                                     seq.getParentStatusToSet(),
                                     model.getCurrentPrincipal().getPrincipal(),
                                     "Automatically switching status via direct communication from child job");
                        if (seq.isReplaceProduct()) {
                            job.getParent().setProduct(job.getProduct());
                        }
                        return;
                    } catch (Throwable e) {
                        //if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid parent status sequencing %s",
                                                job.getParent()), e);
                        //}
                        log(job.getParent(),
                            String.format("error changing status of parent of %s to: %s in parent sequencing %s \n %s",
                                          job.getId(),
                                          seq.getParentStatusToSet(),
                                          seq.getId(), e));
                    }
                    break;
                } else if (seq.getParent().equals(job.getParent().getService())) {
                    try {
                        ensureNextStateIsValid(job.getParent(),
                                               job.getParent().getService(),
                                               job.getParent().getStatus(),
                                               seq.getParentStatusToSet());
                        changeStatus(job.getParent(),
                                     seq.getParentStatusToSet(),
                                     model.getCurrentPrincipal().getPrincipal(),
                                     "Automatically switching status via direct communication from child job");
                        if (seq.isReplaceProduct()) {
                            job.getParent().setProduct(job.getProduct());
                        }
                    } catch (Throwable e) {
                        //if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid parent status sequencing %s",
                                                job.getParent()), e);
                        //}
                        log(job.getParent(),
                            String.format("error changing status of parent of %s to: %s in parent sequencing %s\n%s",
                                          job.getId(),
                                          seq.getParentStatusToSet(),
                                          seq.getId(), e));
                    }
                    break;
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Sequencing does not apply %s",
                                                seq));
                    }
                }
            }
        }
    }

    /**
     * Process the sibling squencing auths for the job. The authorizations are
     * grouped by the same next sibling, ordered by sequence number. This method
     * finds the first successful transition from the grouped list and returns
     * after processing that auth
     *
     * @param job
     * @param grouped
     */
    private void processSiblings(Job job,
                                 List<ProductSiblingSequencingAuthorization> grouped) {
        if (grouped.isEmpty()) {
            return;
        }
        for (Job sibling : getActiveSubJobsForService(job.getParent(),
                                                      grouped.get(0).getNextSibling())) {
            if (job.equals(sibling)) {
                break; // we don't operate on the job triggering the processing
            }
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing sibling change for %s",
                                        sibling));
            }
            for (ProductSiblingSequencingAuthorization seq : grouped) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Processing %s", seq));
                }
                try {
                    ensureNextStateIsValid(sibling, sibling.getService(),
                                           sibling.getStatus(),
                                           seq.getNextSiblingStatus());
                    changeStatus(sibling, seq.getNextSiblingStatus(),
                                 model.getCurrentPrincipal().getPrincipal(),
                                 "Automatically switching staus via direct communication from sibling jobs");
                    if (seq.isReplaceProduct()) {
                        sibling.setProduct(job.getProduct());
                    }
                    break;
                } catch (Throwable e) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid sibling status sequencing %s",
                                                job), e);
                    }
                    log(sibling,
                        String.format("error changing status of sibling of %s to: %s in sibling sequencing %s\n%s",
                                      job.getId(), seq.getNextSiblingStatus(),
                                      seq.getId(), e));
                }
            }
        }
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

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> RuleForm resolve(boolean inferred,
                                                                                 RuleForm protocol,
                                                                                 RuleForm parent,
                                                                                 RuleForm child) {
        if (child.isSame()) {
            if (inferred || protocol.isAny()) {
                return parent;
            }
            return protocol;
        }
        if (child.isCopy()) {
            return parent;
        }
        return child;
    }

    protected <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addMask(RuleForm ruleform,
                                                                                                                                Relationship relationship,
                                                                                                                                SingularAttribute<AbstractProtocol, RuleForm> column,
                                                                                                                                CriteriaBuilder cb,
                                                                                                                                CriteriaQuery<Protocol> query,
                                                                                                                                Root<Protocol> protocol,
                                                                                                                                List<Predicate> masks) {
        Predicate mask = mask(ruleform, relationship, column, cb, query,
                              protocol);
        if (mask != null) {
            masks.add(mask);
        }
    }

    protected void insert(Job child, Job parent, Protocol protocol,
                          InferenceMap txfm, Product product) {
        child.setDepth(parent.getDepth() + 1);
        child.setStatus(kernel.getUnset());
        child.setParent(parent);
        child.setProtocol(protocol);
        child.setProduct(product);
        copyIntoChild(parent, protocol, txfm, child);
        em.persist(child);
        log(child, String.format("Inserted from protocol match"));
        if (log.isTraceEnabled()) {
            log.trace(String.format("Inserted job %s\nfrom protocol %s\ntxfm %s",
                                    child, protocol, txfm));
        }
    }

    protected <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Predicate mask(RuleForm ruleform,
                                                                                                                                  Relationship relationship,
                                                                                                                                  SingularAttribute<AbstractProtocol, RuleForm> column,
                                                                                                                                  CriteriaBuilder cb,
                                                                                                                                  CriteriaQuery<Protocol> query,
                                                                                                                                  Root<Protocol> protocol) {
        if (!relationship.equals(kernel.getAnyRelationship())) {
            Predicate mask;
            Path<RuleForm> columnPath = protocol.get(column);
            if (relationship.equals(kernel.getSameRelationship())) {
                mask = cb.equal(columnPath, ruleform);
            } else {
                mask = columnPath.in(inferenceSubquery(ruleform,
                                                       relationship,
                                                       ruleform.getRuleformClass(),
                                                       ruleform.getNetworkClass(),
                                                       ruleform.getNetworkParentAttribute(),
                                                       ruleform.getNetworkChildAttribute(),
                                                       cb, query));
            }
            return cb.or(cb.equal(columnPath.get(Ruleform_.id),
                                  ruleform.getAnyId()),
                         cb.equal(columnPath.get(Ruleform_.id),
                                  ruleform.getSameId()),
                         cb.equal(columnPath.get(Ruleform_.id),
                                  ruleform.getNotApplicableId()), mask);
        }
        return null;
    }
}
