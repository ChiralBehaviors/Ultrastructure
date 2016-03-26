/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.CHILD_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB_CHRONOLOGY;
import static com.chiralbehaviors.CoRE.jooq.Tables.META_PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.PARENT_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.SELF_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.SIBLING_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.STATUS_CODE_SEQUENCING;
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
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.Existential;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.hellblazer.utils.Tuple;

/**
 *
 * @author hhildebrand
 *
 */
public class JobModelImpl implements JobModel {

    private static final Logger  log  = LoggerFactory.getLogger(JobModelImpl.class);
    private static final Integer ZERO = Integer.valueOf(0);

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

    protected final Kernel kernel;
    protected final Model  model;

    public String toString(JobRecord r) {
        return String.format("Job[%s:%s]", model.records()
                                                .resolve(r.getService())
                                                .getName(),
                             model.records()
                                  .resolve(r.getProduct())
                                  .getName());
    }

    public JobModelImpl(Model model) {
        this.model = model;
        kernel = model.getKernel();
    }

    @Override
    public JobRecord changeStatus(JobRecord job, StatusCode newStatus,
                                  String notes) {
        UUID oldStatus = job.getStatus();
        if (oldStatus != null && oldStatus.equals(newStatus.getId())) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("JobRecord status is already set to desired status %s",
                                        job));
            }
            return job;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("%s Setting status %s of %s", notes,
                                    newStatus, job));
        }
        job.setStatus(newStatus.getId());
        log(job, notes);
        return job;
    }

    @Override
    public void createStatusCodeChain(Product service, StatusCode[] codes) {
        for (int i = 0; i < codes.length - 1; i++) {
            model.records()
                 .newStatusCodeSequencing(service, codes[i], codes[i + 1])
                 .insert();
        }
    }

    @Override
    public void createStatusCodeSequencings(Product service,
                                            List<Tuple<StatusCode, StatusCode>> codes) {
        for (Tuple<StatusCode, StatusCode> p : codes) {
            model.records()
                 .newStatusCodeSequencing(service, p.a, p.b)
                 .insert();
        }
    }

    @Override
    public void ensureNextStateIsValid(JobRecord job,
                                       StatusCode nextStatus) throws SQLException {
        StatusCode status = model.records()
                                 .resolve(job.getStatus());
        if (log.isTraceEnabled()) {
            log.trace(String.format("Updating %s, current: %s, next: %s", job,
                                    status, nextStatus));
        }
        Product service = model.records()
                               .resolve(job.getService());
        if (kernel.getUnset()
                  .equals(status)) {
            StatusCode initialState;
            initialState = getInitialState(service);
            if (!nextStatus.equals(initialState)) {
                throw new SQLException(String.format("%s is not allowed as a next state for Service %s coming from %s.  The only allowable state is the initial state of %s  Please consult the Status Code Sequencing rules.",
                                                     nextStatus,
                                                     job.getService(), status,
                                                     initialState));
            }
            return;
        }
        if (!getNextStatusCodes(service, status).contains(nextStatus)) {
            throw new SQLException(String.format("%s is not allowed as a next state for Service %s coming from %s.  Please consult the Status Code Sequencing rules.",
                                                 nextStatus, job.getService(),
                                                 status));
        }
    }

    @Override
    public void ensureValidParentStatus(JobRecord parent) throws SQLException {
        if (!isTerminalState(parent.getStatus(), parent.getService())) {
            throw new SQLException(String.format("'Cannot insert a job because parent %s is in a terminal state %s.'",
                                                 parent, parent.getStatus()));
        }
    }

    @Override
    public void ensureValidServiceAndStatus(Product service,
                                            StatusCode status) throws SQLException {
        //        TypedQuery<Long> query = em.createNamedQuery(StatusCodeSequencing.ENSURE_VALID_SERVICE_STATUS,
        //                                                     Long.class);
        //        query.setParameter("service", service);
        //        query.setParameter("code", status);
        //        if (query.getSingleResult() == 0) {
        //            throw new SQLException(String.format("'service and status must refer to valid combination in StatusCodeSequencing!  %s -> %s is not valid!'",
        //                                                 service, status));
        //        }
    }

    @Override
    public Map<ProtocolRecord, Map<MetaProtocolRecord, List<String>>> findMetaProtocolGaps(JobRecord job) {
        List<MetaProtocolRecord> metaProtocols = getMetaprotocols(job);
        Map<ProtocolRecord, Map<MetaProtocolRecord, List<String>>> gaps = new HashMap<>();

        List<ProtocolRecord> protocols = getProtocolsFor(model.records()
                                                              .resolve(job.getService()));
        for (ProtocolRecord p : protocols) {
            Map<MetaProtocolRecord, List<String>> mpGaps = new HashMap<>();
            for (MetaProtocolRecord mp : metaProtocols) {
                List<String> fieldsMissing = new ArrayList<>();
                if (!pathExists(model.records()
                                     .resolve(job.getAssignTo()),
                                model.records()
                                     .resolve(mp.getAssignTo()),
                                model.records()
                                     .resolve(p.getAssignTo()))) {
                    fieldsMissing.add("AssignTo");
                }
                if (!pathExists(model.records()
                                     .resolve(job.getRequester()),
                                model.records()
                                     .resolve(mp.getRequester()),
                                model.records()
                                     .resolve(p.getRequester()))) {
                    fieldsMissing.add("Requester");
                }
                if (!pathExists(model.records()
                                     .resolve(job.getDeliverTo()),
                                model.records()
                                     .resolve(mp.getDeliverTo()),
                                model.records()
                                     .resolve(p.getDeliverTo()))) {
                    fieldsMissing.add("DeliverTo");
                }
                if (!pathExists(model.records()
                                     .resolve(job.getDeliverFrom()),
                                model.records()
                                     .resolve(mp.getDeliverFrom()),
                                model.records()
                                     .resolve(p.getDeliverFrom()))) {
                    fieldsMissing.add("DeliverFrom");
                }
                if (!pathExists(model.records()
                                     .resolve(job.getProduct()),
                                model.records()
                                     .resolve(mp.getProduct()),
                                model.records()
                                     .resolve(p.getProduct()))) {
                    fieldsMissing.add("Product");
                }
                if (!pathExists(model.records()
                                     .resolve(job.getService()),
                                model.records()
                                     .resolve(mp.getServiceType()),
                                model.records()
                                     .resolve(p.getService()))) {
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
    public Map<ProtocolRecord, List<String>> findProtocolGaps(JobRecord job) {
        Map<ProtocolRecord, List<String>> gaps = new HashMap<>();
        for (ProtocolRecord p : getProtocolsFor(model.records()
                                                     .resolve(job.getService()))) {
            gaps.put(p, findGaps(job, p));
        }
        return gaps;
    }

    @Override
    public List<JobRecord> generateImplicitJobs(JobRecord job) {
        Map<ProtocolRecord, InferenceMap> protocols = getProtocols(job);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Found %s protocols for %s",
                                    protocols.size(), job));
        }
        List<JobRecord> jobs = new ArrayList<JobRecord>();
        for (Entry<ProtocolRecord, InferenceMap> txfm : protocols.entrySet()) {
            ProtocolRecord protocol = txfm.getKey();
            boolean exists = model.create()
                                  .selectCount()
                                  .from(JOB)
                                  .where(JOB.PARENT.equal(job.getParent()))
                                  .and(JOB.PROTOCOL.equal(job.getProtocol()))
                                  .fetchOne()
                                  .value1()
                                  .equals(ZERO);
            if (!exists) {
                jobs.addAll(insert(job, protocol, txfm.getValue()));
            } else {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Not inserting job, as there is an existing job with parent %s from protocol %s",
                                           job, protocol));
                }
            }
        }
        return jobs;
    }

    @Override
    public void generateImplicitJobsForExplicitJobs(JobRecord job) {
        StatusCode statusCode = model.records()
                                     .resolve(job.getStatus());
        if (statusCode.getPropagateChildren()) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Generating implicit jobs for %s",
                                        toString(job)));
            }
            generateImplicitJobs(job);
        } else {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Not generating implicit jobs for: %s",
                                        toString(job)));
            }
        }
    }

    @Override
    public List<JobRecord> getActiveExplicitJobs() {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.TOP_LEVEL_ACTIVE_JOBS,
        //                                                          JobRecord.class);
        //        return query.getResultList();
        return null;
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_ACTIVE_ASSIGNED_TO,
        //                                                          JobRecord.class);
        //        query.setParameter("agency", agency);
        //        return query.getResultList();
        return null;
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency,
                                            List<StatusCode> desiredStates) {
        List<JobRecord> jobs = new ArrayList<>();
        desiredStates.forEach(s -> {
            jobs.addAll(model.create()
                             .selectFrom(JOB)
                             .where(JOB.ASSIGN_TO.equal(agency.getId()))
                             .and(JOB.STATUS.equal(s.getId()))
                             .fetch());
        });
        return jobs;
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency,
                                            StatusCode desiredState) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS,
        //                                                          JobRecord.class);
        //        query.setParameter("agency", agency);
        //        query.setParameter("status", desiredState);
        //        return query.getResultList();
        return null;
    }

    @Override
    public List<JobRecord> getActiveSubJobsForService(JobRecord job,
                                                      Product service) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_ACTIVE_CHILD_JOBS_FOR_SERVICE,
        //                                                          JobRecord.class);
        //        query.setParameter("parent", job);
        //        query.setParameter("service", service);
        //        return query.getResultList();
        return null;
    }

    @Override
    public List<JobRecord> getActiveSubJobsOf(JobRecord job) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_ACTIVE_CHILD_JOBS,
        //                                                          JobRecord.class);
        //        query.setParameter("parent", job);
        //        return query.getResultList();
        return null;
    }

    /**
     * Recursively retrieve all the active or terminated sub jobs of a given job
     *
     * @param job
     * @return
     */
    @Override
    public Collection<JobRecord> getAllActiveOrTerminatedSubJobsOf(JobRecord job) {
        Set<JobRecord> tally = new HashSet<JobRecord>();
        return recursivelyGetActiveOrTerminalSubJobsOf(job, tally);
    }

    @Override
    public Collection<JobRecord> getAllActiveSubJobsOf(JobRecord job) {
        return getAllActiveSubJobsOf(job, new HashSet<JobRecord>());
    }

    @Override
    public List<JobRecord> getAllActiveSubJobsOf(JobRecord parent,
                                                 Agency agency) {
        List<JobRecord> jobs = new ArrayList<JobRecord>();
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_SUB_JOBS_ASSIGNED_TO,
        //                                                          JobRecord.class);
        //        query.setParameter("parent", parent);
        //        query.setParameter("agency", agency);
        //        for (JobRecord subJob : query.getResultList()) {
        //            jobs.add(subJob);
        //            getAllActiveSubJobsOf(subJob, agency);
        //        }
        return jobs;
    }

    @Override
    public void getAllActiveSubJobsOf(JobRecord parent, Agency agency,
                                      List<JobRecord> jobs) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_SUB_JOBS_ASSIGNED_TO,
        //                                                          JobRecord.class);
        //        query.setParameter("parent", parent);
        //        query.setParameter("agency", agency);
        //        for (JobRecord subJob : query.getResultList()) {
        //            jobs.add(subJob);
        //            getAllActiveSubJobsOf(parent, agency, jobs);
        //        }
    }

    @Override
    public Collection<JobRecord> getAllActiveSubJobsOf(JobRecord job,
                                                       Collection<JobRecord> tally) {
        List<JobRecord> myJobs = getActiveSubJobsOf(job);
        if (tally.addAll(myJobs)) {
            for (JobRecord j : myJobs) {
                getAllActiveSubJobsOf(j, tally);
            }
        }
        return tally;
    }

    @Override
    public List<JobRecord> getAllChildren(JobRecord job) {
        List<JobRecord> jobs = getActiveSubJobsOf(job);
        if (jobs == null || jobs.size() == 0) {
            return Collections.emptyList();
        }

        List<JobRecord> children = new LinkedList<>();
        for (JobRecord j : jobs) {
            List<JobRecord> temp = getAllChildren(j);
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
    public List<ChildSequencingAuthorizationRecord> getChildActions(JobRecord job) {
        return model.create()
                    .selectFrom(CHILD_SEQUENCING_AUTHORIZATION)
                    .where(CHILD_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .and(CHILD_SEQUENCING_AUTHORIZATION.PARENT.equal(job.getStatus()))
                    .fetch();
    }

    @Override
    public List<JobRecord> getChildJobsByService(JobRecord parent,
                                                 Product service) {
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.PARENT.equal(parent.getId()))
                    .and(JOB.SERVICE.equal(service.getId()))
                    .fetch();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.JobModel#getChronologyForJob(com.
     * chiralbehaviors. CoRE.jsp.JobRecord)
     */
    @Override
    public List<JobChronologyRecord> getChronologyForJob(JobRecord job) {
        return model.create()
                    .selectFrom(JOB_CHRONOLOGY)
                    .where(JOB_CHRONOLOGY.JOB.equal(job.getId()))
                    .fetch();
    }

    /**
     * Answer the list of the active or terminated direct sub jobs of a given
     * job
     *
     * @param job
     * @return
     */
    @Override
    public List<JobRecord> getDirectActiveOrTerminalSubJobsOf(JobRecord job) {
        //        TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_ACTIVE_OR_TERMINATED_SUB_JOBS,
        //                                                          JobRecord.class);
        //        query.setParameter("parent", job);
        //        query.setParameter("unset", kernel.getUnset());
        //
        //        return query.getResultList();
        return null;
    }

    @Override
    public StatusCode getInitialState(Product service) {
        List<StatusCode> initialStates = getInitialStates(service);
        if (initialStates.size() > 1) {
            throw new IllegalStateException(String.format("Service [%s] has more than one initial state: %s",
                                                          service.getName(),
                                                          initialStates.stream()
                                                                       .map(s -> s.getName())
                                                                       .collect(Collectors.toList())));
        }
        return initialStates.get(0);
    }

    /**
     * Returns the list of initial states of a service
     */
    @Override
    public List<StatusCode> getInitialStates(Product service) {
        Existential sc = EXISTENTIAL;
        return model.create()
                    .selectDistinct(sc.fields())
                    .from(sc)
                    .join(STATUS_CODE_SEQUENCING)
                    .on(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                    .and(STATUS_CODE_SEQUENCING.PARENT.equal(sc.field(EXISTENTIAL.ID)))
                    .andNotExists(model.create()
                                       .select(STATUS_CODE_SEQUENCING.CHILD)
                                       .from(STATUS_CODE_SEQUENCING)
                                       .where(STATUS_CODE_SEQUENCING.CHILD.equal(EXISTENTIAL.ID)))
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .map(r -> model.records()
                                   .resolve(r))
                    .map(r -> (StatusCode) r)
                    .collect(Collectors.toList());
    }

    @Override
    public List<MetaProtocolRecord> getMetaprotocols(JobRecord job) {
        return getMetaProtocolsFor(model.records()
                                        .resolve(job.getService()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.JobModel#getMetaProtocolsFor(com.
     * chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public List<MetaProtocolRecord> getMetaProtocolsFor(Product service) {
        return model.create()
                    .selectFrom(META_PROTOCOL)
                    .where(META_PROTOCOL.SERVICE.equal(service.getId()))
                    .fetch();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getMostRecentChronologyEntry(com
     * .chiralbehaviors .CoRE.jsp.JobRecord)
     */
    @Override
    public JobChronologyRecord getMostRecentChronologyEntry(JobRecord job) {
        List<JobChronologyRecord> c = getChronologyForJob(job);
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
        return model.create()
                    .selectDistinct(EXISTENTIAL.fields())
                    .from(EXISTENTIAL)
                    .join(STATUS_CODE_SEQUENCING)
                    .on(STATUS_CODE_SEQUENCING.PARENT.equal(parent.getId()))
                    .and(EXISTENTIAL.ID.equal(STATUS_CODE_SEQUENCING.CHILD))
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .map(r -> model.records()
                                   .resolve(r))
                    .map(e -> (StatusCode) e)
                    .collect(Collectors.toList());
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<ParentSequencingAuthorizationRecord> getParentActions(JobRecord job) {
        return model.create()
                    .selectFrom(PARENT_SEQUENCING_AUTHORIZATION)
                    .where(PARENT_SEQUENCING_AUTHORIZATION.SERVICE.equal(job.getService()))
                    .and(PARENT_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .fetch();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getParentActions(com.chiralbehaviors
     * .CoRE .product.Product)
     */
    @Override
    public List<ParentSequencingAuthorizationRecord> getParentActions(Product service) {
        return model.create()
                    .selectFrom(PARENT_SEQUENCING_AUTHORIZATION)
                    .where(PARENT_SEQUENCING_AUTHORIZATION.SERVICE.equal(service.getId()))
                    .fetch();
    }

    @Override
    public Map<ProtocolRecord, InferenceMap> getProtocols(JobRecord job) {
        if (job.getStatus() == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("job has no status set %s", job));
            }
            // Bail because, dude.  We haven't even been initialized
            return Collections.emptyMap();
        }
        // First we try for protocols which match the current job
        List<ProtocolRecord> protocols = getProtocolsMatching(job);
        Map<ProtocolRecord, InferenceMap> matches = new LinkedHashMap<>();
        if (!protocols.isEmpty()) {
            for (ProtocolRecord protocol : protocols) {
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

        for (MetaProtocolRecord metaProtocol : getMetaprotocols(job)) {
            Set<Entry<ProtocolRecord, InferenceMap>> infered = getProtocols(job,
                                                                            metaProtocol).entrySet();
            if (infered.size() == 0) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("job has no infered matches %s",
                                            job));
                }
            }
            for (Map.Entry<ProtocolRecord, InferenceMap> transformed : infered) {
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
    public Map<ProtocolRecord, InferenceMap> getProtocols(JobRecord job,
                                                          MetaProtocolRecord metaProtocol) {
        //        Map<ProtocolRecord, InferenceMap> protocols = new LinkedHashMap<>();
        //        for (ProtocolRecord protocol : createMaskQuery(metaProtocol,
        //                                                       job).getResultList()) {
        //            if (!protocols.containsKey(protocol)) {
        //                protocols.put(protocol, map(protocol, metaProtocol));
        //            }
        //        }
        //        return protocols;
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getProtocolsFor(com.chiralbehaviors
     * .CoRE.product.Product)
     */
    @Override
    public List<ProtocolRecord> getProtocolsFor(Product service) {
        return model.create()
                    .selectFrom(PROTOCOL)
                    .where(PROTOCOL.SERVICE.equal(service.getId()))
                    .fetch();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<SelfSequencingAuthorizationRecord> getSelfActions(JobRecord job) {
        return model.create()
                    .selectFrom(SELF_SEQUENCING_AUTHORIZATION)
                    .where(SELF_SEQUENCING_AUTHORIZATION.SERVICE.equal(job.getService()))
                    .and(SELF_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .fetch();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public List<SiblingSequencingAuthorizationRecord> getSiblingActions(JobRecord job) {
        return model.create()
                    .selectFrom(SIBLING_SEQUENCING_AUTHORIZATION)
                    .where(SIBLING_SEQUENCING_AUTHORIZATION.PARENT.equal(job.getService()))
                    .and(SIBLING_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .fetch();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.JobModel#getSiblingActions(com.chiralbehaviors
     * .CoRE .product.Product)
     */
    @Override
    public List<SiblingSequencingAuthorizationRecord> getSiblingActions(Product parent) {
        return model.create()
                    .selectFrom(SIBLING_SEQUENCING_AUTHORIZATION)
                    .where(SIBLING_SEQUENCING_AUTHORIZATION.PARENT.equal(parent.getId()))
                    .fetch();
    }

    @Override
    public Result<StatusCodeSequencingRecord> getStatusCodeSequencingsFor(Product service) {
        return model.create()
                    .selectFrom(STATUS_CODE_SEQUENCING)
                    .where(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                    .fetch();

    }

    @Override
    public List<StatusCode> getStatusCodesFor(Product service) {
        return model.create()
                    .selectDistinct(EXISTENTIAL.fields())
                    .from(EXISTENTIAL)
                    .join(STATUS_CODE_SEQUENCING)
                    .on(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId())
                                                      .and(STATUS_CODE_SEQUENCING.PARENT.equal(EXISTENTIAL.ID)
                                                                                        .or(STATUS_CODE_SEQUENCING.CHILD.equal(EXISTENTIAL.ID))))
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .map(r -> model.records()
                                   .resolve(r))
                    .map(e -> (StatusCode) e)
                    .collect(Collectors.toList());
    }

    @Override
    public List<StatusCode> getTerminalStates(JobRecord job) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectDistinct(EXISTENTIAL.fields())
                    .from(Arrays.asList(seq, EXISTENTIAL))
                    .where(seq.CHILD.equal(EXISTENTIAL.ID))
                    .andNotExists(model.create()
                                       .selectFrom(STATUS_CODE_SEQUENCING)
                                       .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                       .and(STATUS_CODE_SEQUENCING.PARENT.equal(seq.field(STATUS_CODE_SEQUENCING.CHILD))))
                    .and(STATUS_CODE_SEQUENCING.SERVICE.equal(job.getService()))
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .map(r -> model.records()
                                   .resolve(r))
                    .map(r -> (StatusCode) r)
                    .collect(Collectors.toList());
    }

    @Override
    public List<JobRecord> getTopLevelJobs() {
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.PARENT.isNull())
                    .fetch();
    }

    @Override
    public List<JobRecord> getTopLevelJobsWithSubJobsAssignedToAgency(Agency agency) {
        List<JobRecord> jobs = new ArrayList<JobRecord>();
        //        for (JobRecord job : getActiveExplicitJobs()) {
        //            TypedQuery<JobRecord> query = em.createNamedQuery(JobRecord.GET_SUB_JOBS_ASSIGNED_TO,
        //                                                              JobRecord.class);
        //            query.setParameter("parent", job);
        //            query.setParameter("agency", agency);
        //            for (JobRecord subJob : query.getResultList()) {
        //                if (isActive(subJob)) {
        //                    jobs.add(job);
        //                    break;
        //                }
        //            }
        //        }
        return jobs;
    }

    /**
     * @param parentJob
     * @param service
     * @return
     */
    @Override
    public List<JobRecord> getUnsetSiblings(JobRecord parent, Product service) {
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.SERVICE.equal(service.getId()))
                    .and(JOB.STATUS.equal(kernel.getUnset()
                                                .getId()))
                    .and(JOB.PARENT.equal(parent.getId()))
                    .fetch();
    }

    /**
     * @param job
     * @return
     */
    @Override
    public boolean hasActiveSiblings(JobRecord job) {
        //        TypedQuery<Long> query = em.createNamedQuery(JobRecord.HAS_ACTIVE_CHILD_JOBS,
        //                                                     Long.class);
        //        query.setParameter("parent", job.getParent());
        //
        //        return query.getSingleResult() > 0;
        return false;
    }

    @Override
    public boolean hasInitialState(Product service) {
        return getInitialState(service).size() == 1;
    }

    /**
     * @param service
     * @throws SQLException
     */
    @Override
    public boolean hasNonTerminalSCCs(Product service) throws SQLException {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        List<StatusCode> statusCodes = getStatusCodesFor(service);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Status codes for %s: %s",
                                    service.getName(), statusCodes.stream()
                                                                  .map(r -> r.getName())
                                                                  .collect(Collectors.toList())));
        }
        for (StatusCode currentCode : statusCodes) {
            List<StatusCode> codes = getNextStatusCodes(service, currentCode);
            graph.put(currentCode, codes);
        }
        return hasScc(graph);
    }

    @Override
    public boolean hasScs(Product service) {
        return !model.create()
                     .selectCount()
                     .from(STATUS_CODE_SEQUENCING)
                     .where(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                     .fetchOne()
                     .value1()
                     .equals(ZERO);
    }

    /**
     * Insert new jobs defined by the protocol
     *
     * @param parent
     * @param protocol
     * @return the newly created job
     */
    @Override
    public List<JobRecord> insert(JobRecord parent, ProtocolRecord protocol) {
        return insert(parent, protocol, NO_TRANSFORMATION);
    }

    public List<JobRecord> insert(JobRecord parent, ProtocolRecord protocol,
                                  InferenceMap txfm) {
        if (parent.getDepth() > MAXIMUM_JOB_DEPTH) {
            throw new IllegalStateException(String.format("Maximum job depth exceeded.  parent: %s, protocol: %s",
                                                          parent, protocol));
        }
        List<JobRecord> jobs = new ArrayList<>();
        if (protocol.getChildrenRelationship()
                    .equals(kernel.getNotApplicableRelationship()
                                  .getId())) {
            JobRecord job = model.records()
                                 .newJob();
            insert(job, parent, protocol, txfm, model.records()
                                                     .resolve(resolve(txfm.product,
                                                                      model.records()
                                                                           .resolve(protocol.getProduct()),
                                                                      model.records()
                                                                           .resolve(parent.getProduct()),
                                                                      model.records()
                                                                           .resolve(protocol.getChildProduct()))));
            jobs.add(job);
        } else {
            for (ExistentialRuleform child : model.getPhantasmModel()
                                                  .getChildren(model.records()
                                                                    .resolve(parent.getProduct()),
                                                               model.records()
                                                                    .resolve(protocol.getChildrenRelationship()),
                                                               ExistentialDomain.Product)) {
                JobRecord job = model.records()
                                     .newJob();
                insert(job, parent, protocol, txfm, (Product) child);
                jobs.add(job);
            }
        }
        return jobs;
    }

    @Override
    public boolean isActive(JobRecord job) {
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
        return isTerminalState(sc.getId(), service.getId());
    }

    @Override
    public void log(JobRecord job, String notes) {
        if (job.getStatus() == null) {
            job.setStatus(kernel.getUnset()
                                .getId()); // Prophylactic against recursive error disease
        }
        model.records()
             .newJobChronology(job, notes);
    }

    @Override
    public JobRecord newInitializedJob(Product service) {
        JobRecord job = model.records()
                             .newJob();
        job.setDepth(0);
        job.setService(service.getId());
        job.setAssignTo(kernel.getNotApplicableAgency()
                              .getId());
        job.setDeliverFrom(kernel.getNotApplicableLocation()
                                 .getId());
        job.setDeliverTo(kernel.getNotApplicableLocation()
                               .getId());
        job.setProduct(kernel.getNotApplicableProduct()
                             .getId());
        job.setRequester(kernel.getNotApplicableAgency()
                               .getId());
        job.setQuantityUnit(kernel.getNotApplicableUnit()
                                  .getId());
        job.setStatus(kernel.getUnset()
                            .getId());
        job.insert();
        return job;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.JobModel#newInitializedMetaProtocol()
     */
    @Override
    public MetaProtocolRecord newInitializedMetaProtocol(Product service) {
        Relationship any = kernel.getAnyRelationship();
        MetaProtocolRecord mp = model.records()
                                     .newMetaProtocol();
        mp.setService(service.getId());
        mp.setAssignTo(any.getId());
        mp.setDeliverTo(any.getId());
        mp.setDeliverFrom(any.getId());
        mp.setProduct(any.getId());
        mp.setRequester(any.getId());
        mp.setServiceType(kernel.getSameRelationship()
                                .getId());
        mp.setQuantityUnit(any.getId());
        mp.insert();
        return mp;
    }

    @Override
    public ProtocolRecord newInitializedProtocol(Product service) {
        ProtocolRecord protocol = model.records()
                                       .newProtocol();
        protocol.setService(service.getId());
        protocol.setAssignTo(kernel.getNotApplicableAgency()
                                   .getId());
        protocol.setDeliverFrom(kernel.getNotApplicableLocation()
                                      .getId());
        protocol.setDeliverTo(kernel.getNotApplicableLocation()
                                    .getId());
        protocol.setProduct(kernel.getNotApplicableProduct()
                                  .getId());
        protocol.setRequester(kernel.getNotApplicableAgency()
                                    .getId());
        protocol.setQuantityUnit(kernel.getNotApplicableUnit()
                                       .getId());
        protocol.setChildAssignTo(kernel.getSameAgency()
                                        .getId());
        protocol.setChildDeliverFrom(kernel.getSameLocation()
                                           .getId());
        protocol.setChildDeliverTo(kernel.getSameLocation()
                                         .getId());
        protocol.setChildrenRelationship(kernel.getNotApplicableRelationship()
                                               .getId());
        protocol.setChildProduct(kernel.getSameProduct()
                                       .getId());
        protocol.setChildService(kernel.getSameProduct()
                                       .getId());
        protocol.setChildQuantityUnit(kernel.getNotApplicableUnit()
                                            .getId());
        protocol.insert();
        return protocol;
    }

    @Override
    public void processChildSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing children of JobRecord %s",
                                    toString(job)));
        }
        Deque<ChildSequencingAuthorizationRecord> actions = new ArrayDeque<>(getChildActions(job));
        while (!actions.isEmpty()) {
            ChildSequencingAuthorizationRecord auth = actions.pop();
            List<ChildSequencingAuthorizationRecord> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty() && actions.peekFirst()
                                                .getNextChild()
                                                .equals(auth.getNextChild())) {
                grouped.add(actions.pop());
            }
            processChildren(job, grouped);
        }
    }

    @Override
    public void processJobSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing change in JobRecord %s",
                                    toString(job)));
        }
        //process parents last so we can close out child jobs
        processChildSequencing(job);
        processSiblingSequencing(job);
        processSelfSequencing(job);
        processParentSequencing(job);
    }

    @Override
    public void processParentSequencing(JobRecord job) {
        if (job.getParent() == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("No parent of job, not processing parent sequencing: %s",
                                        toString(job)));
            }
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing parent of JobRecord %s",
                                    toString(job)));
        }

        Deque<ParentSequencingAuthorizationRecord> actions = new ArrayDeque<>(getParentActions(job));
        while (!actions.isEmpty()) {
            ParentSequencingAuthorizationRecord auth = actions.pop();
            List<ParentSequencingAuthorizationRecord> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty() && actions.peekFirst()
                                                .getParent()
                                                .equals(auth.getParent())) {
                grouped.add(actions.pop());
            }
            processParents(job, grouped);
        }
    }

    @Override
    public void processSelfSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing self of JobRecord %s",
                                    toString(job)));
        }

        for (SelfSequencingAuthorizationRecord seq : getSelfActions(job)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing %s", seq));
            }
            try {
                ensureNextStateIsValid(job, model.records()
                                                 .resolve(seq.getStatusToSet()));
                changeStatus(job, model.records()
                                       .resolve(seq.getStatusToSet()),
                             "Automatically switching staus via direct communication from sibling jobs");
            } catch (Throwable e) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("invalid self status sequencing %s",
                                            job),
                              e);
                }
                log(job,
                    String.format("error changing status of job of %s to: %s in self sequencing %s\n%s",
                                  job.getId(), seq.getStatusToSet(),
                                  seq.getId(), e));
            }
        }
    }

    @Override
    public void processSiblingSequencing(JobRecord job) {
        if (job.getParent() == null) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("JobRecord does not have a parent, so not processing siblings"));
            }
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing siblings of JobRecord %s",
                                    toString(job)));
        }

        Deque<SiblingSequencingAuthorizationRecord> actions = new ArrayDeque<>(getSiblingActions(job));
        while (!actions.isEmpty()) {
            SiblingSequencingAuthorizationRecord auth = actions.pop();
            List<SiblingSequencingAuthorizationRecord> grouped = new ArrayList<>();
            grouped.add(auth);
            while (!actions.isEmpty() && actions.peekFirst()
                                                .getNextSibling()
                                                .equals(auth.getNextSibling())) {
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
    public void validateStateGraph(Collection<Product> modifiedServices) throws SQLException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("modified services %s",
                                    modifiedServices.stream()
                                                    .map(r -> r.getName())
                                                    .collect(Collectors.toList())));
        }
        for (Product modifiedService : modifiedServices) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Validating state graph for %s",
                                        modifiedService.getName()));
            }
            if (!hasScs(modifiedService)) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("No status code sequencing for %s",
                                            modifiedService.getName()));
                }
                continue;
            }
            if (hasNonTerminalSCCs(modifiedService)) {
                throw new SQLException(String.format("Event '%s' has at least one non terminal SCC defined in its status code graph",
                                                     modifiedService.getName()));
            }
            List<StatusCode> initialStates = getInitialStates(modifiedService);
            if (initialStates.isEmpty()) {
                throw new SQLException(String.format("Event '%s' has no initial state defined in its status code graph",
                                                     modifiedService.getName()));
            }
            if (initialStates.size() > 1) {
                throw new SQLException(String.format("Event '%s' has multiple initial states defined in its status code graph: %s",
                                                     modifiedService.getName(),
                                                     initialStates.stream()
                                                                  .map(s -> s.getName())
                                                                  .collect(Collectors.toList())));
            }
            if (hasNonTerminalSCCs(modifiedService)) {
                throw new SQLException(String.format("Event '%s' has at least one non terminal SCC defined in its status code graph",
                                                     modifiedService.getName()));
            }
        }
    }

    private void copyIntoChild(JobRecord parent, ProtocolRecord protocol,
                               InferenceMap inferred, JobRecord child) {
        child.setAssignTo(resolve(inferred.assignTo, model.records()
                                                          .resolve(protocol.getAssignTo()),
                                  model.records()
                                       .resolve(parent.getAssignTo()),
                                  model.records()
                                       .resolve(protocol.getChildAssignTo())));
        child.setDeliverTo(resolve(inferred.deliverTo, model.records()
                                                            .resolve(protocol.getDeliverTo()),
                                   model.records()
                                        .resolve(parent.getDeliverTo()),
                                   model.records()
                                        .resolve(protocol.getChildDeliverTo())));
        child.setDeliverFrom(resolve(inferred.deliverFrom, model.records()
                                                                .resolve(protocol.getDeliverFrom()),
                                     model.records()
                                          .resolve(parent.getDeliverFrom()),
                                     model.records()
                                          .resolve(protocol.getChildDeliverFrom())));
        child.setService(resolve(false, model.records()
                                             .resolve(protocol.getService()),
                                 model.records()
                                      .resolve(parent.getService()),
                                 model.records()
                                      .resolve(protocol.getChildService())));
        if (inferred.requester || model.records()
                                       .resolve(protocol.getRequester())
                                       .isAnyOrSame()) {
            child.setRequester(parent.getRequester());
        } else {
            child.setRequester(protocol.getRequester());
        }

        if (model.records()
                 .resolve(protocol.getChildQuantityUnit())
                 .isSame()) {
            if (inferred.quantityUnit || model.records()
                                              .resolve(protocol.getQuantityUnit())
                                              .isAny()) {
                child.setQuantity(parent.getQuantity());
                child.setQuantityUnit(parent.getQuantityUnit());
            }
        } else if (model.records()
                        .resolve(protocol.getChildQuantityUnit())
                        .isCopy()) {
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
    //    private TypedQuery<ProtocolRecord> createMaskQuery(MetaProtocolRecord metaprotocol,
    //                                                       JobRecord job) {
    ////        CriteriaBuilder cb = em.getCriteriaBuilder();
    ////        CriteriaQuery<ProtocolRecord> query = cb.createQuery(ProtocolRecord.class);
    ////
    ////        Root<ProtocolRecord> protocol = query.from(ProtocolRecord.class);
    ////
    ////        List<Predicate> masks = new ArrayList<>();
    ////
    ////        // Service gets special handling.  we don't want infinite jobs due to ANY
    ////        if (metaprotocol.getServiceType()
    ////                        .getId()
    ////                        .equals(WellKnownRelationship.SAME.id())) {
    ////            masks.add(cb.equal(protocol.get(AbstractProtocol_.service),
    ////                               job.getService()));
    ////        } else {
    ////            masks.add(protocol.get(AbstractProtocol_.service)
    ////                              .in(inferenceSubquery(job.getService(),
    ////                                                    metaprotocol.getServiceType(),
    ////                                                    Product.class,
    ////                                                    ProductNetwork.class,
    ////                                                    ProductNetwork_.parent,
    ////                                                    ProductNetwork_.child, cb,
    ////                                                    query)));
    ////        }
    ////
    ////        // Deliver From
    ////        addMask(job.getDeliverFrom(), metaprotocol.getDeliverFrom(),
    ////                AbstractProtocol_.deliverFrom, cb, query, protocol, masks);
    ////
    ////        // Deliver To
    ////        addMask(job.getDeliverTo(), metaprotocol.getDeliverTo(),
    ////                AbstractProtocol_.deliverTo, cb, query, protocol, masks);
    ////
    ////        // Product
    ////        addMask(job.getProduct(), metaprotocol.getProduct(),
    ////                AbstractProtocol_.product, cb, query, protocol, masks);
    ////
    ////        // Requester
    ////        addMask(job.getRequester(), metaprotocol.getRequester(),
    ////                AbstractProtocol_.requester, cb, query, protocol, masks);
    ////
    ////        // Assign To
    ////        addMask(job.getAssignTo(), metaprotocol.getAssignTo(),
    ////                AbstractProtocol_.assignTo, cb, query, protocol, masks);
    ////
    ////        // Quqntity Unit
    ////        addMask(job.getQuantityUnit(), metaprotocol.getQuantityUnit(),
    ////                AbstractProtocol_.quantityUnit, cb, query, protocol, masks);
    ////
    ////        query.where(masks.toArray(new Predicate[masks.size()]));
    ////        query.select(protocol)
    ////             .distinct(true);
    ////        TypedQuery<ProtocolRecord> tq = em.createQuery(query);
    ////        return tq;
    //    }

    /**
     * @param job
     * @param p
     * @return
     */
    private List<String> findGaps(JobRecord job, ProtocolRecord p) {
        List<String> missingFields = new LinkedList<>();
        if (!job.getRequester()
                .equals(p.getRequester())) {
            missingFields.add("Requester");
        }
        if (!job.getProduct()
                .equals(p.getProduct())) {
            missingFields.add("Product");
        }
        if (!job.getDeliverTo()
                .equals(p.getDeliverTo())) {
            missingFields.add("DeliverTo");
        }
        if (!job.getDeliverFrom()
                .equals(p.getDeliverFrom())) {
            missingFields.add("DeliverFrom");
        }
        return missingFields;

    }

    private List<ProtocolRecord> getProtocolsMatching(JobRecord job) {
        return model.create()
                    .selectFrom(PROTOCOL)
                    .where(PROTOCOL.SERVICE.equal(job.getService()))
                    .and(PROTOCOL.REQUESTER.equal(job.getRequester()))
                    .and(PROTOCOL.PRODUCT.equal(job.getProduct()))
                    .and(PROTOCOL.DELIVER_TO.equal(job.getDeliverTo()))
                    .and(PROTOCOL.DELIVER_FROM.equal(job.getDeliverFrom()))
                    .and(PROTOCOL.ASSIGN_TO.equal(job.getAssignTo()))
                    .fetch();
    }

    //    private Subquery<RuleForm> inferenceSubquery(ExistentialAttributeRecord attribute,
    //                                                 Relationship relationship,
    //                                                 SingularAttribute<Network, RuleForm> parent,
    //                                                 SingularAttribute<Network, RuleForm> child,
    //                                                 CriteriaBuilder cb,
    //                                                 CriteriaQuery<ProtocolRecord> query) {
    //        Subquery<RuleForm> inference = query.subquery(ruleformClass);
    //        Root<Network> root = inference.from(networkClass);
    //        inference.select(root.get(child));
    //        inference.where(cb.and(cb.equal(root.get(parent), attribute),
    //                               cb.equal(root.get(NetworkRuleform_.relationship),
    //                                        relationship)));
    //        return inference;
    //    }

    private boolean isTerminalState(UUID status, UUID service) {
        return ZERO.equals(model.create()
                                .selectCount()
                                .from(STATUS_CODE_SEQUENCING)
                                .where(STATUS_CODE_SEQUENCING.PARENT.equal(status))
                                .and(STATUS_CODE_SEQUENCING.SERVICE.equal(service))
                                .fetchOne()
                                .value1());
    }

    /**
     * @param relationship
     * @return
     */
    private boolean isTxfm(Relationship relationship) {
        return !WellKnownRelationship.ANY.id()
                                         .equals(relationship.getId())
               && !WellKnownRelationship.SAME.id()
                                             .equals(relationship.getId());
    }

    /**
     * @param protocol
     * @param metaProtocol
     * @return
     */
    @SuppressWarnings("unused")
    private InferenceMap map(ProtocolRecord protocol,
                             MetaProtocolRecord metaProtocol) {
        return new InferenceMap(isTxfm(model.records()
                                            .resolve(metaProtocol.getAssignTo())),
                                isTxfm(model.records()
                                            .resolve(metaProtocol.getDeliverFrom())),
                                isTxfm(model.records()
                                            .resolve(metaProtocol.getDeliverTo())),
                                isTxfm(model.records()
                                            .resolve(metaProtocol.getProduct())),
                                isTxfm(model.records()
                                            .resolve(metaProtocol.getRequester())),
                                isTxfm(model.records()
                                            .resolve(metaProtocol.getQuantityUnit())));
    }

    /**
     * @param mpRelationship
     * @param child
     * @param job
     */
    private boolean pathExists(ExistentialRuleform rf,
                               Relationship mpRelationship,
                               ExistentialRuleform child) {
        if (mpRelationship.isAnyOrSame() || mpRelationship.isNotApplicable()) {
            return true;
        }
        if (!model.getPhantasmModel()
                  .isAccessible(rf, mpRelationship, child)) {
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
    private void processChildren(JobRecord job,
                                 List<ChildSequencingAuthorizationRecord> grouped) {
        if (grouped.isEmpty()) {
            return;
        }
        for (JobRecord child : getActiveSubJobsForService(job, model.records()
                                                                    .resolve(grouped.get(0)
                                                                                    .getNextChild()))) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing child %s", child));
            }
            for (ChildSequencingAuthorizationRecord seq : grouped) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Processing %s", seq));
                }
                try {
                    ensureNextStateIsValid(child, model.records()
                                                       .resolve(seq.getNextChildStatus()));
                    changeStatus(child, model.records()
                                             .resolve(seq.getNextChildStatus()),
                                 "Automatically switching status via direct communication from parent job");
                    if (seq.getReplaceProduct()) {
                        child.setProduct(job.getProduct());
                    }
                    break;
                } catch (Throwable e) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid child status sequencing %s",
                                                child),
                                  e);
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
    private void processParents(JobRecord job,
                                List<ParentSequencingAuthorizationRecord> grouped) {
        for (ParentSequencingAuthorizationRecord seq : grouped) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing %s", seq));
            }
            if (seq.getSetIfActiveSiblings() || !hasActiveSiblings(job)) {
                JobRecord parent = model.records()
                                        .resolveJob(job.getParent());
                if (seq.getParent() == null && seq.getService()
                                                  .equals(parent.getService())) {
                    try {
                        ensureNextStateIsValid(parent, model.records()
                                                            .resolve(seq.getParentStatusToSet()));
                        changeStatus(parent, model.records()
                                                  .resolve(seq.getParentStatusToSet()),
                                     "Automatically switching status via direct communication from child job");
                        if (seq.getReplaceProduct()) {
                            parent.setProduct(job.getProduct());
                        }
                        return;
                    } catch (Throwable e) {
                        //if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid parent status sequencing %s",
                                                job.getParent()),
                                  e);
                        //}
                        log(parent,
                            String.format("error changing status of parent of %s to: %s in parent sequencing %s \n %s",
                                          job.getId(),
                                          seq.getParentStatusToSet(),
                                          seq.getId(), e));
                    }
                    break;
                } else if (seq.getParent()
                              .equals(parent.getService())) {
                    try {
                        ensureNextStateIsValid(parent, model.records()
                                                            .resolve(seq.getParentStatusToSet()));
                        changeStatus(parent, model.records()
                                                  .resolve(seq.getParentStatusToSet()),
                                     "Automatically switching status via direct communication from child job");
                        if (seq.getReplaceProduct()) {
                            parent.setProduct(job.getProduct());
                        }
                    } catch (Throwable e) {
                        //if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid parent status sequencing %s",
                                                job.getParent()),
                                  e);
                        //}
                        log(parent,
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
    private void processSiblings(JobRecord job,
                                 List<SiblingSequencingAuthorizationRecord> grouped) {
        if (grouped.isEmpty()) {
            return;
        }
        for (JobRecord sibling : getActiveSubJobsForService(model.records()
                                                                 .resolve(job.getParent()),
                                                            model.records()
                                                                 .resolve(grouped.get(0)
                                                                                 .getNextSibling()))) {
            if (job.equals(sibling)) {
                break; // we don't operate on the job triggering the processing
            }
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing sibling change for %s",
                                        sibling));
            }
            for (SiblingSequencingAuthorizationRecord seq : grouped) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Processing %s", seq));
                }
                try {
                    ensureNextStateIsValid(sibling, model.records()
                                                         .resolve(seq.getNextSiblingStatus()));
                    changeStatus(sibling, model.records()
                                               .resolve(seq.getNextSiblingStatus()),
                                 "Automatically switching staus via direct communication from sibling jobs");
                    if (seq.getReplaceProduct()) {
                        sibling.setProduct(job.getProduct());
                    }
                    break;
                } catch (Throwable e) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("invalid sibling status sequencing %s",
                                                job),
                                  e);
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
    private Collection<JobRecord> recursivelyGetActiveOrTerminalSubJobsOf(JobRecord job,
                                                                          Collection<JobRecord> tally) {
        List<JobRecord> myJobs = getDirectActiveOrTerminalSubJobsOf(job);
        if (tally.addAll(myJobs)) {
            for (JobRecord sub : myJobs) {
                recursivelyGetActiveOrTerminalSubJobsOf(sub, tally);
            }
        }
        return tally;
    }

    private UUID resolve(boolean inferred, ExistentialRuleform protocol,
                         ExistentialRuleform parent,
                         ExistentialRuleform child) {
        if (child.isSame()) {
            if (inferred || protocol.isAny()) {
                return parent.getId();
            }
            return protocol.getId();
        }
        if (child.isCopy()) {
            return parent.getId();
        }
        return child.getId();
    }

    //    protected void addMask(RuleForm ruleform, Relationship relationship,
    //                           SingularAttribute<AbstractProtocol, RuleForm> column,
    //                           CriteriaBuilder cb,
    //                           CriteriaQuery<ProtocolRecord> query,
    //                           Root<ProtocolRecord> protocol,
    //                           List<Predicate> masks) {
    //        Predicate mask = mask(ruleform, relationship, column, cb, query,
    //                              protocol);
    //        if (mask != null) {
    //            masks.add(mask);
    //        }
    //    }

    protected void insert(JobRecord child, JobRecord parent,
                          ProtocolRecord protocol, InferenceMap txfm,
                          Product product) {
        child.setDepth(parent.getDepth() + 1);
        child.setStatus(kernel.getUnset()
                              .getId());
        child.setParent(parent.getId());
        child.setProtocol(protocol.getId());
        child.setProduct(product.getId());
        copyIntoChild(parent, protocol, txfm, child);
        child.insert();
        log(child, String.format("Inserted from protocol match"));
        if (log.isTraceEnabled()) {
            log.trace(String.format("Inserted job %s\nfrom protocol %s\ntxfm %s",
                                    child, protocol, txfm));
        }
    }

    //    protected Predicate mask(RuleForm ruleform, Relationship relationship,
    //                             SingularAttribute<AbstractProtocol, RuleForm> column,
    //                             CriteriaBuilder cb,
    //                             CriteriaQuery<ProtocolRecord> query,
    //                             Root<ProtocolRecord> protocol) {
    //        if (!relationship.getId()
    //                         .equals(WellKnownRelationship.ANY.id())) {
    //            Predicate mask;
    //            Path<RuleForm> columnPath = protocol.get(column);
    //            if (relationship.getId()
    //                            .equals(WellKnownRelationship.SAME.id())) {
    //                mask = cb.equal(columnPath, ruleform);
    //            } else {
    //                mask = columnPath.in(inferenceSubquery(ruleform, relationship,
    //                                                       ruleform.getRuleformClass(),
    //                                                       ruleform.getNetworkClass(),
    //                                                       ruleform.getNetworkParentAttribute(),
    //                                                       ruleform.getNetworkChildAttribute(),
    //                                                       cb, query));
    //            }
    //            return cb.or(cb.equal(columnPath.get(Ruleform_.id),
    //                                  ruleform.getAnyId()),
    //                         cb.equal(columnPath.get(Ruleform_.id),
    //                                  ruleform.getSameId()),
    //                         cb.equal(columnPath.get(Ruleform_.id),
    //                                  ruleform.getNotApplicableId()),
    //                         mask);
    //        }
    //        return null;
    //    }
}
