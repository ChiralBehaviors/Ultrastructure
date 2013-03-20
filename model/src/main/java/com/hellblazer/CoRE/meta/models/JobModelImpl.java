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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.JobChronology;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ServiceSequencingAuthorization;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.EntityModel;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.LocationModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.ResourceModel;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

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
            // If you can't get to any other nodes outside the SCC, then it's terminal 
            if (outgoing.size() == 0) {
                return true;
            }
        }
        return false;
    }

    private final EntityManager em;
    private final Kernel        kernel;
    private final EntityModel   entityModel;
    private final LocationModel locationModel;
    private final ResourceModel resourceModel;

    public JobModelImpl(Model model) {
        em = model.getEntityManager();
        kernel = model.getKernel();
        entityModel = model.getEntityModel();
        locationModel = model.getLocationModel();
        resourceModel = model.getResourceModel();
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
            if (log.isDebugEnabled()) {
                log.debug(String.format("Generating implicit jobs for %", job));
            }
            generateImplicitJobs(job);
            for (Job subJob : getInitialSubJobs(job)) {
                changeStatus(subJob, getInitialState(subJob.getService()),
                             "Initially available job (automatically set)");
            }
        }
    }

    @Override
    public void changeStatus(Job job, StatusCode newStatus, String notes) {
        StatusCode oldStatus = job.getStatus();
        if (oldStatus.equals(newStatus)) {
            return;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        job.setStatus(newStatus);
        em.merge(job);
        addJobChronology(job, now, oldStatus, notes);
    }

    @Override
    public void ensureNextStateIsValid(Job job, Entity service,
                                       StatusCode currentStatus,
                                       StatusCode nextStatus)
                                                             throws SQLException {
        if (nextStatus.equals(nextStatus)) {
            return; // Nothing to do
        }
        if (log.isInfoEnabled()) {
            log.info(String.format("Transitioning service %s, job %s, from %s to %s",
                                   job, service, currentStatus, nextStatus));
        }
        if (!getNextStatusCodes(service, currentStatus).contains(nextStatus)) {
            if (kernel.getUnset().equals(currentStatus)) {

                throw new SQLException(
                                       String.format("%s is not set up to come after the special (UNSET) status code for Service %s. Please configure it to be an initial state in the Status Code Sequencing ruleform if this is what you want.",
                                                     nextStatus, service,
                                                     currentStatus));
            }
            throw new SQLException(
                                   String.format("%s is not allowed as a next state for Service %s coming from %s.  Please consult the Status Code Sequencing rules.",
                                                 nextStatus, service,
                                                 currentStatus));
        }
    }

    @Override
    public void generateImplicitJobs(Job job) {
        for (MetaProtocol metaProtocol : getMetaprotocols(job)) {
            for (Protocol protocol : getProtocols(job, metaProtocol)) {
                insertJob(job, protocol);
            }
            if (metaProtocol.getStopOnMatch()) {
                break;
            }
        }
    }

    @Override
    public List<Job> getActiveExplicitJobs() {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_EXPLICIT_JOBS,
                                                    Job.class);
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveJobsFor(Resource resource) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_JOBS_FOR_RESOURCE,
                                                    Job.class);
        query.setParameter(1, resource.getId());
        return query.getResultList();
    }

    @Override
    public List<Job> getActiveSubJobsOf(Job job) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_ACTIVE_SUB_JOBS,
                                                    Job.class);
        query.setParameter(1, job.getService().getId());
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

    public List<Job> getAllActiveSubJobsOfJobAssignedToResource(Job parent,
                                                                Resource resource) {
        List<Job> jobs = new ArrayList<Job>();
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("resource", resource);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOfJobAssignedToResource(parent, resource,
                                                           jobs);
            }
        }
        return jobs;
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ServiceSequencingAuthorization> getChildActions(Job job) {
        TypedQuery<ServiceSequencingAuthorization> query = em.createNamedQuery(ServiceSequencingAuthorization.GET_CHILD_ACTIONS,
                                                                               ServiceSequencingAuthorization.class);
        query.setParameter("service", job.getService());
        query.setParameter("status", job.getStatus());
        List<ServiceSequencingAuthorization> childActions = query.getResultList();
        return childActions;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.JobModel#getChronologyForJob(com.hellblazer.CoRE.animation.Job)
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
    public StatusCode getInitialState(Entity service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATES,
                                                           StatusCode.class);
        query.setParameter(1, service.getId());
        return query.getSingleResult();
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
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_INITIAL_SUB_JOBS,
                                                    Job.class);
        query.setParameter(1, job.getId());
        query.setParameter(2, job.getId());
        query.setParameter(3, kernel.getUnset().getId());
        query.setParameter(4, job.getId());

        return query.getResultList();
    }

    @Override
    public List<MetaProtocol> getMetaprotocols(Job job) {
        TypedQuery<MetaProtocol> query = em.createNamedQuery(MetaProtocol.FOR_JOB,
                                                             MetaProtocol.class);
        query.setParameter("serviceType", job.getService());
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.JobModel#getMostRecentChronologyEntry(com.hellblazer.CoRE.animation.Job)
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
    public List<StatusCode> getNextStatusCodes(Entity service, StatusCode parent) {
        if (parent.equals(kernel.getUnset())) {
            return Arrays.asList(getInitialState(service));
        }
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.GET_NEXT_STATUS_CODES,
                                                           StatusCode.class);
        query.setParameter("service", service);
        query.setParameter("parent", parent);
        return query.getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ServiceSequencingAuthorization> getParentActions(Job job) {
        TypedQuery<ServiceSequencingAuthorization> query = em.createNamedQuery(ServiceSequencingAuthorization.GET_PARENT_ACTIONS,
                                                                               ServiceSequencingAuthorization.class);
        query.setParameter("event", job.getService());
        query.setParameter("status", job.getStatus());
        return query.getResultList();
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public List<Protocol> getProtocols(Job job, MetaProtocol metaProtocol) {
        TypedQuery<Protocol> protocols = em.createNamedQuery(Protocol.GET,
                                                             Protocol.class);
        protocols.setParameter("service",
                               transform(job.getService(),
                                         metaProtocol.getServiceType(),
                                         "service", job));
        protocols.setParameter("requester",
                               transform(job.getRequester(),
                                         metaProtocol.getRequestingResource(),
                                         "requester", job));
        protocols.setParameter("product",
                               transform(job.getProduct(),
                                         metaProtocol.getProductOrdered(),
                                         "product", job));
        protocols.setParameter("deliverTo",
                               transform(job.getDeliverTo(),
                                         metaProtocol.getDeliverTo(),
                                         "deliver to", job));
        protocols.setParameter("deliverFrom",
                               transform(job.getDeliverFrom(),
                                         metaProtocol.getDeliverFrom(),
                                         "deliver from", job));
        return protocols.getResultList();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ServiceSequencingAuthorization> getSiblingActions(Job job) {
        TypedQuery<ServiceSequencingAuthorization> query = em.createNamedQuery(ServiceSequencingAuthorization.GET_SIBLING_ACTIONS,
                                                                               ServiceSequencingAuthorization.class);
        query.setParameter("event", job.getService());
        query.setParameter("status", job.getStatus());
        return query.getResultList();
    }

    @Override
    public Set<StatusCode> getStatusCodesFor(Entity service) {
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

    public List<Job> getTopLevelJobsWithSubJobsAssignedToResource(Resource resource) {
        List<Job> jobs = new ArrayList<Job>();
        for (Job job : getActiveExplicitJobs()) {
            TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                        Job.class);
            query.setParameter("parent", job);
            query.setParameter("resource", resource);
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
    public List<Job> getUnsetSiblings(Job parent, Entity service) {
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
    public boolean hasInitialState(Entity service) {
        TypedQuery<StatusCode> query = em.createNamedQuery(Job.INITIAL_STATES,
                                                           StatusCode.class);
        query.setParameter(1, service.getId());
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean hasScs(Entity service) {
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
    public boolean hasTerminalSCCs(Entity service) throws SQLException {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        for (StatusCode currentCode : getStatusCodesFor(service)) {
            List<StatusCode> codes = getNextStatusCodes(service, currentCode);
            graph.put(currentCode, codes);
        }
        return hasScc(graph);
    }

    /**
     * Insert the new job dedebugd by the protocol
     * 
     * @param parent
     * @param protocol
     */
    @Override
    public void insertJob(Job parent, Protocol protocol) {
        Job job = new Job(kernel.getCoreAnimationSoftware());
        job.setParent(parent);
        job.setAssignTo(resolve(job.getAssignTo(), protocol.getAssignTo()));
        job.setRequester(resolve(job.getRequester(), protocol.getRequester()));
        Entity service = resolve(job.getService(), protocol.getService());
        job.setService(service);
        job.setProduct(resolve(job.getProduct(), protocol.getProduct()));
        job.setMaterial(resolve(job.getMaterial(), protocol.getMaterial()));
        job.setDeliverFrom(resolve(job.getDeliverFrom(),
                                   protocol.getDeliverFrom()));
        job.setDeliverTo(resolve(job.getDeliverTo(), protocol.getDeliverTo()));
        job.setStatus(getInitialState(service));
        em.persist(job);
    }

    @Override
    public boolean isActive(Job job) {
        return !kernel.getUnset().equals(job.getStatus())
               && !isTerminalState(job.getStatus(), job.getService());
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.JobModel#isTerminalState(com.hellblazer.CoRE.animation.StatusCode, com.hellblazer.CoRE.animation.Event)
     */
    @Override
    public boolean isTerminalState(StatusCode sc, Entity service) {
        TypedQuery<Boolean> query = em.createNamedQuery(StatusCode.IS_TERMINAL_STATE,
                                                        Boolean.class);
        query.setParameter(1, service.getId());
        query.setParameter(2, sc.getId());

        return query.getSingleResult();
    }

    @Override
    public boolean isValidNextStatus(Entity service, StatusCode parent,
                                     StatusCode next) {
        TypedQuery<Boolean> query = em.createNamedQuery(StatusCodeSequencing.IS_VALID_NEXT_STATUS,
                                                        Boolean.class);
        query.setParameter(1, service.getId());
        query.setParameter(2, parent.getId());
        query.setParameter(3, next.getId());
        return query.getSingleResult();
    }

    @Override
    public void processChildChanges(Job job) {
        if (log.isInfoEnabled()) {
            log.info(String.format("Processing children of Job %s", job));
        }
        List<ServiceSequencingAuthorization> childActions = getChildActions(job);
        for (ServiceSequencingAuthorization seq : childActions) {
            // This can be merged into the same outer query... just doing quick and dirty now

            // for each child job that is active (not UNSET or terminal) update their status to this one
            // Should probably have a constraint that the given status should be terminal for the event,
            // and that it can be transitioned to from any non-terminal state for that event 
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
    public void processParentChanges(Job job) {
        if (log.isInfoEnabled()) {
            log.info(String.format("Processing parent of Job %s", job));
        }

        for (ServiceSequencingAuthorization seq : getParentActions(job)) {
            if (seq.getSetIfActiveSiblings() || !hasActiveSiblings(job)) {
                // If the parent job has the specified event then, make the change
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
        if (log.isInfoEnabled()) {
            log.info(String.format("Processing siblings of Job %s", job));
        }

        for (ServiceSequencingAuthorization seq : getSiblingActions(job)) {
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
     * @param modifiedEventsFromStatusCodeSequencing2
     * @throws SQLException
     */
    @Override
    public void validateStateGraph(List<Entity> modifiedEvents)
                                                               throws SQLException {
        for (Entity modifiedService : modifiedEvents) {
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

    protected Collection<Job> getAllActiveSubJobsOf(Job job,
                                                    Collection<Job> tally) {
        List<Job> myJobs = getActiveSubJobsOf(job);
        if (tally.addAll(myJobs)) {
            for (Job j : myJobs) {
                getAllActiveSubJobsOf(j, tally);
            }
        }
        return tally;
    }

    protected void getAllActiveSubJobsOfJobAssignedToResource(Job parent,
                                                              Resource resource,
                                                              List<Job> jobs) {
        TypedQuery<Job> query = em.createNamedQuery(Job.GET_SUB_JOBS_ASSIGNED_TO,
                                                    Job.class);
        query.setParameter("parent", parent);
        query.setParameter("resource", resource);
        for (Job subJob : query.getResultList()) {
            if (isActive(subJob)) {
                jobs.add(subJob);
                getAllActiveSubJobsOfJobAssignedToResource(parent, resource,
                                                           jobs);
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
    protected Collection<Job> recursivelyGetActiveOrTerminalSubJobsOf(Job job,
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
     * Resolve the value of an entity, using the original and supplied values
     */
    protected Entity resolve(Entity original, Entity supplied) {
        if (kernel.getSameEntity().equals(supplied)) {
            return original;
        } else if (kernel.getAnyEntity().equals(supplied)) {
            return original;
        } else if (kernel.getOriginalEntity().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    /**
     * Resolve the value of a location, using the original and supplied values
     */
    protected Location resolve(Location original, Location supplied) {
        if (kernel.getSameLocation().equals(supplied)) {
            return original;
        } else if (kernel.getAnyLocation().equals(supplied)) {
            return original;
        } else if (kernel.getOriginalLocation().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    /**
     * Resolve the value of a resource, using the original and supplied values
     */
    protected Resource resolve(Resource original, Resource supplied) {
        if (kernel.getSameResource().equals(supplied)) {
            return original;
        } else if (kernel.getAnyResource().equals(supplied)) {
            return original;
        } else if (kernel.getOriginalResource().equals(supplied)) {
            return original;
        }
        return supplied;
    }

    /**
     * Transform the entity according to the relationship
     * 
     * @param entity
     *            - the entity to transform
     * @param relationship
     *            - the relationship to use for transformation
     * @param type
     *            - the type of entity in the job, used for logging
     * @param job
     *            - the Job, used for logging
     * @return the transformed entity, or null
     */
    protected Entity transform(Entity entity, Relationship relationship,
                               String type, Job job) {
        if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getAnyEntity();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameEntity();
        } else {
            return entityModel.getChild(entity, relationship);
        }
    }

    protected Location transform(Location location, Relationship relationship,
                                 String type, Job job) {
        if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getAnyLocation();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameLocation();
        } else {
            return locationModel.getChild(location, relationship);
        }
    }

    protected Resource transform(Resource resource, Relationship relationship,
                                 String type, Job job) {
        if (kernel.getAnyRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (ANY) for %s for job %s", type,
                                        job));
            }
            return kernel.getAnyResource();
        } else if (kernel.getSameRelationship().equals(relationship)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Using (SAME) for %s for job %s", type,
                                        job));
            }
            return kernel.getSameResource();
        } else {
            return resourceModel.getChild(resource, relationship);
        }
    }
}
