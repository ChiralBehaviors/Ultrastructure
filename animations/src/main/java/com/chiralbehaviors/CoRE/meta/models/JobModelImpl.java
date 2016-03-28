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
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
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

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
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
                log.debug(format("SCC: %s", Arrays.asList(scc)
                                                  .stream()
                                                  .map(r -> r.getName())
                                                  .collect(Collectors.toList())));
            }
            // includes nodes of scc, plus nodes they lead to
            Set<StatusCode> outgoing = new HashSet<StatusCode>();
            for (Object node : scc) {
                outgoing.addAll(graph.get(node));
            }
            if (log.isDebugEnabled()) {
                log.debug(format("Outgoing nodes: %s", outgoing.stream()
                                                               .map(r -> r.getName())
                                                               .collect(Collectors.toList())));
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

    public JobModelImpl(Model model) {
        this.model = model;
        kernel = model.getKernel();
    }

    @Override
    public JobRecord changeStatus(JobRecord job, StatusCode newStatus,
                                  String notes) {
        UUID oldStatus = job.getStatus();
        if (oldStatus != null && oldStatus.equals(newStatus.getId())) {
            return job;
        }
        job.setStatus(newStatus.getId());
        job.update();
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
            log.trace(String.format("Updating %s, current: %s, next: %s",
                                    toString(job), status.getName(),
                                    nextStatus.getName()));
        }
        Product service = model.records()
                               .resolve(job.getService());
        if (kernel.getUnset()
                  .equals(status)) {
            StatusCode initialState;
            initialState = getInitialState(service);
            if (!nextStatus.equals(initialState)) {
                throw new SQLException(String.format("%s is not allowed as a next state for Service %s coming from %s.  The only allowable state is the initial state of %s  Please consult the Status Code Sequencing rules.",
                                                     nextStatus.getName(),
                                                     model.records()
                                                          .existentialName(job.getService()),
                                                     status.getName(),
                                                     initialState.getName()));
            }
            return;
        }
        if (!getNextStatusCodes(service, status).contains(nextStatus)) {
            throw new SQLException(String.format("%s is not allowed as a next state for Service %s coming from %s.  Please consult the Status Code Sequencing rules.",
                                                 nextStatus.getName(),
                                                 model.records()
                                                      .existentialName(job.getService()),
                                                 status.getName()));
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
        if (ZERO.equals(model.create()
                             .selectCount()
                             .from(STATUS_CODE_SEQUENCING)
                             .where(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                             .and(STATUS_CODE_SEQUENCING.PARENT.equal(status.getId())
                                                               .or(STATUS_CODE_SEQUENCING.CHILD.equal(status.getId())))
                             .fetchOne()
                             .value1())) {
            throw new SQLException(String.format("service and status must refer to valid combination in StatusCodeSequencing!  %s -> %s is not valid!",
                                                 service.getName(),
                                                 status.getName()));
        }
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
                                    protocols.size(), toString(job)));
        }
        List<JobRecord> jobs = new ArrayList<JobRecord>();
        for (Entry<ProtocolRecord, InferenceMap> txfm : protocols.entrySet()) {
            ProtocolRecord protocol = txfm.getKey();
            assert protocol != null;
            jobs.addAll(insert(job, protocol, txfm.getValue()));
            //            if (model.create()
            //                     .selectCount()
            //                     .from(JOB)
            //                     .where(JOB.PARENT.equal(job.getParent()))
            //                     .and(JOB.PROTOCOL.equal(job.getProtocol()))
            //                     .fetchOne()
            //                     .value1()
            //                     .equals(ZERO)) {
            //                jobs.addAll(insert(job, protocol, txfm.getValue()));
            //            } else {
            //                if (log.isInfoEnabled()) {
            //                    log.info(String.format("Not inserting job, as there is an existing job with parent %s from protocol %s",
            //                                           toString(job), toString(protocol)));
            //                }
            //            }
        }
        return jobs;
    }

    @Override
    public void generateImplicitJobsForExplicitJobs(JobRecord job) {
        if (((StatusCode) model.records()
                               .resolve(job.getStatus())).getPropagateChildren()) {
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
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.PARENT.isNull())
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.ASSIGN_TO.equal(agency.getId()))
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency,
                                            List<StatusCode> desiredStates) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.ASSIGN_TO.equal(agency.getId()))
                    .and(JOB.STATUS.in(desiredStates.stream()
                                                    .map(s -> s.getId())
                                                    .collect(Collectors.toList())))
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
    }

    @Override
    public List<JobRecord> getActiveJobsFor(Agency agency,
                                            StatusCode desiredState) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.ASSIGN_TO.equal(agency.getId()))
                    .and(JOB.STATUS.equal(desiredState.getId()))
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
    }

    @Override
    public List<JobRecord> getActiveSubJobsForService(JobRecord job,
                                                      Product service) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.SERVICE.equal(service.getId()))
                    .and(JOB.PARENT.equal(job.getId()))
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
    }

    @Override
    public List<JobRecord> getActiveSubJobsOf(JobRecord job) {
        StatusCodeSequencing seq = STATUS_CODE_SEQUENCING.as("seq");
        return model.create()
                    .selectFrom(JOB)
                    .where(JOB.PARENT.equal(job.getId()))
                    .andNotExists(model.create()
                                       .selectFrom(seq)
                                       .where(seq.field(STATUS_CODE_SEQUENCING.CHILD)
                                                 .equal(JOB.STATUS))
                                       .andNotExists(model.create()
                                                          .selectFrom(STATUS_CODE_SEQUENCING)
                                                          .where(STATUS_CODE_SEQUENCING.SERVICE.equal(seq.field(STATUS_CODE_SEQUENCING.SERVICE)))
                                                          .and(STATUS_CODE_SEQUENCING.PARENT.eq(seq.field(STATUS_CODE_SEQUENCING.CHILD)))))
                    .fetch();
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
                    .and(CHILD_SEQUENCING_AUTHORIZATION.PARENT.equal(job.getService()))
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

    @Override
    public List<JobChronologyRecord> getChronologyForJob(JobRecord job) {
        return model.create()
                    .selectFrom(JOB_CHRONOLOGY)
                    .where(JOB_CHRONOLOGY.JOB.equal(job.getId()))
                    .fetch();
    }

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
        StatusCodeSequencing seq2 = STATUS_CODE_SEQUENCING.as("seq2");
        return model.create()
                    .selectDistinct(EXISTENTIAL.fields())
                    .from(EXISTENTIAL)
                    .join(STATUS_CODE_SEQUENCING)
                    .on(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                    .and(STATUS_CODE_SEQUENCING.PARENT.equal(EXISTENTIAL.ID))
                    .andNotExists(model.create()
                                       .select(seq2.field(STATUS_CODE_SEQUENCING.CHILD))
                                       .from(seq2)
                                       .where(seq2.field(STATUS_CODE_SEQUENCING.CHILD)
                                                  .equal(EXISTENTIAL.ID))
                                       .and(seq2.field(STATUS_CODE_SEQUENCING.SERVICE)
                                                .equal(service.getId())))
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

    @Override
    public List<MetaProtocolRecord> getMetaProtocolsFor(Product service) {
        return model.create()
                    .selectFrom(META_PROTOCOL)
                    .where(META_PROTOCOL.SERVICE.equal(service.getId()))
                    .fetch();
    }

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
                    .and(STATUS_CODE_SEQUENCING.CHILD.equal(EXISTENTIAL.ID))
                    .and(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
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
                log.trace(String.format("job has no status set %s",
                                        toString(job)));
            }
            // Bail because, dude.  We haven't even been initialized
            return Collections.emptyMap();
        }
        // First we try for protocols which match the current job
        List<ProtocolRecord> protocols = getProtocolsMatching(job);
        Map<ProtocolRecord, InferenceMap> matches = new LinkedHashMap<>();
        if (log.isTraceEnabled()) {
            log.trace("found {} protocol(s) matching {} | {}", protocols.size(),
                      toString(job), protocols.stream()
                                              .map(p -> toString(p))
                                              .collect(Collectors.toList()));
        }
        if (!protocols.isEmpty()) {
            protocols.stream()
                     .filter(p -> !matches.containsKey(p))
                     .forEach(p -> matches.put(p, NO_TRANSFORMATION));
            return matches;
        }

        List<MetaProtocolRecord> metaprotocols = getMetaprotocols(job);
        if (log.isTraceEnabled()) {
            log.trace("Found {} metaprotocol(s) matching {}",
                      metaprotocols.size(), toString(job));
        }
        for (MetaProtocolRecord metaProtocol : metaprotocols) {
            if (log.isTraceEnabled()) {
                log.trace("Inferring for {} ", toString(job));
            }
            Set<Entry<ProtocolRecord, InferenceMap>> infered = getProtocols(job,
                                                                            metaProtocol).entrySet();
            if (log.isTraceEnabled()) {
                log.trace("Inferred {} protocol(s) for {} using {}",
                          infered.size(), toString(job),
                          toString(metaProtocol));
            }
            infered.stream()
                   .filter(t -> !matches.containsKey(t.getKey()))
                   .forEach(t -> matches.put(t.getKey(), t.getValue()));
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
        Map<ProtocolRecord, InferenceMap> protocols = new LinkedHashMap<>();
        match(metaProtocol, job).forEach(protocol -> {
            protocols.putIfAbsent(protocol, map(protocol, metaProtocol));
        });
        return protocols;
    }

    @Override
    public List<ProtocolRecord> getProtocolsFor(Product service) {
        return model.create()
                    .selectFrom(PROTOCOL)
                    .where(PROTOCOL.SERVICE.equal(service.getId()))
                    .fetch();
    }

    @Override
    public List<SelfSequencingAuthorizationRecord> getSelfActions(JobRecord job) {
        return model.create()
                    .selectFrom(SELF_SEQUENCING_AUTHORIZATION)
                    .where(SELF_SEQUENCING_AUTHORIZATION.SERVICE.equal(job.getService()))
                    .and(SELF_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .fetch();
    }

    @Override
    public List<SiblingSequencingAuthorizationRecord> getSiblingActions(JobRecord job) {
        return model.create()
                    .selectFrom(SIBLING_SEQUENCING_AUTHORIZATION)
                    .where(SIBLING_SEQUENCING_AUTHORIZATION.PARENT.equal(job.getService()))
                    .and(SIBLING_SEQUENCING_AUTHORIZATION.STATUS_CODE.equal(job.getStatus()))
                    .fetch();
    }

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
                    .on(STATUS_CODE_SEQUENCING.SERVICE.equal(service.getId()))
                    .where(STATUS_CODE_SEQUENCING.CHILD.equal(EXISTENTIAL.ID))
                    .or(STATUS_CODE_SEQUENCING.PARENT.equal(EXISTENTIAL.ID))
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

    @Override
    public boolean hasNonTerminalSCCs(Product service) throws SQLException {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        List<StatusCode> statusCodes = getStatusCodesFor(service);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Status codes for [%s]: %s",
                                    service.getName(), statusCodes.stream()
                                                                  .map(r -> r.getName())
                                                                  .collect(Collectors.toList())));
        }
        statusCodes.forEach(currentCode -> {
            List<StatusCode> codes = getNextStatusCodes(service, currentCode);
            graph.put(currentCode, codes);
        });
        if (log.isTraceEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append(String.format("Status code graph for [%s]:\n",
                                     service.getName()));
            graph.entrySet()
                 .forEach(entry -> {
                     buf.append("          ");
                     buf.append(entry.getKey()
                                     .getName());
                     buf.append("->");
                     buf.append(entry.getValue()
                                     .stream()
                                     .map(s -> s.getName())
                                     .collect(Collectors.toList()));
                     buf.append("\n");
                 });
            log.trace(buf.toString());
        }
        assert graph.entrySet()
                    .stream()
                    .allMatch(entry -> entry.getValue()
                                            .stream()
                                            .allMatch(s -> {
                                                if (!graph.containsKey(s)) {
                                                    log.error("Graph is missing entry: [{}] from edges of [{}]",
                                                              s.getName(),
                                                              entry.getKey()
                                                                   .getName());
                                                    return false;
                                                }
                                                return true;
                                            })) : "Invalid graph";
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

    @Override
    public boolean isActive(JobRecord job) {
        return !isTerminalState(job.getStatus(), job.getService());
    }

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
        Integer currentGrain = model.create()
                                    .select(DSL.max(JOB_CHRONOLOGY.SEQUENCE_NUMBER))
                                    .from(JOB_CHRONOLOGY)
                                    .where(JOB_CHRONOLOGY.JOB.equal(job.getId()))
                                    .fetchOne()
                                    .value1();
        JobChronologyRecord sandsOTime = model.records()
                                              .newJobChronology(job, notes);
        sandsOTime.setSequenceNumber(currentGrain == null ? 0
                                                          : currentGrain + 1);
        sandsOTime.insert();
        if (log.isTraceEnabled()) {
            log.trace("logged: {}", toString(sandsOTime));
        }

    }

    public List<ProtocolRecord> match(MetaProtocolRecord metaProtocol,
                                      JobRecord job) {
        SelectQuery<Record> selectQuery = model.create()
                                               .selectQuery();
        selectQuery.addDistinctOn(PROTOCOL.fields());
        selectQuery.addFrom(PROTOCOL);

        addServiceMask(metaProtocol, job, selectQuery);
        addStatusMask(metaProtocol, job, selectQuery);

        addMask(selectQuery, metaProtocol.getDeliverFrom(),
                job.getDeliverFrom(), PROTOCOL.DELIVER_FROM,
                ExistentialDomain.Location);
        addMask(selectQuery, metaProtocol.getDeliverTo(), job.getDeliverTo(),
                PROTOCOL.DELIVER_TO, ExistentialDomain.Location);

        addMask(selectQuery, metaProtocol.getProduct(), job.getProduct(),
                PROTOCOL.PRODUCT, ExistentialDomain.Product);

        addMask(selectQuery, metaProtocol.getRequester(), job.getRequester(),
                PROTOCOL.REQUESTER, ExistentialDomain.Agency);

        addMask(selectQuery, metaProtocol.getAssignTo(), job.getAssignTo(),
                PROTOCOL.ASSIGN_TO, ExistentialDomain.Agency);

        addMask(selectQuery, metaProtocol.getQuantityUnit(),
                job.getQuantityUnit(), PROTOCOL.QUANTITY_UNIT,
                ExistentialDomain.Unit);

        return selectQuery.fetch()
                          .into(ProtocolRecord.class);
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

    @Override
    public MetaProtocolRecord newInitializedMetaProtocol(Product service) {
        Relationship any = kernel.getAnyRelationship();
        MetaProtocolRecord mp = model.records()
                                     .newMetaProtocol();
        mp.setService(service.getId());
        mp.setStatus(WellKnownRelationship.SAME.id());
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
        protocol.setStatus(WellKnownStatusCode.UNSET.id());
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
        protocol.setChildStatus(WellKnownStatusCode.UNSET.id());
        protocol.setChildQuantityUnit(kernel.getNotApplicableUnit()
                                            .getId());
        protocol.insert();
        return protocol;
    }

    @Override
    public void processChildSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing children of %s",
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
        //process parents last so we can close out child jobs
        processChildSequencing(job);
        processSiblingSequencing(job);
        processSelfSequencing(job);
        processParentSequencing(job);
    }

    @Override
    public void processParentSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Processing parent of %s", toString(job)));
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
            log.trace(String.format("Processing self sequencing of %s",
                                    toString(job)));
        }

        for (SelfSequencingAuthorizationRecord seq : getSelfActions(job)) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Processing %s for %s", toString(seq),
                                        toString(job)));
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
                                            toString(job)),
                              e);
                }
                log(job,
                    String.format("error changing status of job of %s to: %s in self sequencing %s\n%s",
                                  toString(job), model.records()
                                                      .resolve(seq.getStatusToSet())
                                                      .getName(),
                                  toString(seq), e));
            }
        }
    }

    @Override
    public void processSiblingSequencing(JobRecord job) {
        if (log.isTraceEnabled()) {
            log.trace("Processing siblings of {}", toString(job));
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

    @Override
    public String toString(JobChronologyRecord r) {
        return String.format("JobChronology[%s {%s:%s} {%s:%s} %s:%s:%s:%s]",
                             r.getNotes(), r.getSequenceNumber(), r.getJob(),
                             model.records()
                                  .existentialName(r.getService()),
                             model.records()
                                  .existentialName(r.getStatus()),
                             model.records()
                                  .existentialName(r.getProduct()),
                             model.records()
                                  .existentialName(r.getAssignTo()),
                             model.records()
                                  .existentialName(r.getRequester()),
                             model.records()
                                  .existentialName(r.getDeliverTo()),
                             model.records()
                                  .existentialName(r.getDeliverFrom()),
                             model.records()
                                  .existentialName(r.getQuantityUnit()));
    }

    @Override
    public String toString(JobRecord r) {
        return String.format("Job[{%s:%s} %d %s %s %s %s %s %s]",
                             model.records()
                                  .existentialName(r.getService()),
                             model.records()
                                  .existentialName(r.getStatus()),
                             r.getQuantity(), model.records()
                                                   .existentialName(r.getQuantityUnit()),
                             model.records()
                                  .existentialName(r.getProduct()),
                             model.records()
                                  .existentialName(r.getAssignTo()),
                             model.records()
                                  .existentialName(r.getRequester()),
                             model.records()
                                  .existentialName(r.getDeliverTo()),
                             model.records()
                                  .existentialName(r.getDeliverFrom()));
    }

    /**
     * @param metaProtocol
     * @return
     */
    public Object toString(MetaProtocolRecord r) {
        return String.format("MetaProtocol[%s->%s:%s:%s:%s:%s:%s:%s]",
                             model.records()
                                  .existentialName(r.getService()),
                             model.records()
                                  .existentialName(r.getServiceType()),
                             model.records()
                                  .existentialName(r.getQuantityUnit()),
                             model.records()
                                  .existentialName(r.getProduct()),
                             model.records()
                                  .existentialName(r.getAssignTo()),
                             model.records()
                                  .existentialName(r.getRequester()),
                             model.records()
                                  .existentialName(r.getDeliverTo()),
                             model.records()
                                  .existentialName(r.getDeliverFrom()));
    }

    @Override
    public String toString(ProtocolRecord r) {
        return String.format("Protocol[%s:%s:%s:%s:%s:%s:%s]", model.records()
                                                                    .existentialName(r.getService()),
                             model.records()
                                  .existentialName(r.getProduct()),
                             model.records()
                                  .existentialName(r.getAssignTo()),
                             model.records()
                                  .existentialName(r.getRequester()),
                             model.records()
                                  .existentialName(r.getDeliverTo()),
                             model.records()
                                  .existentialName(r.getDeliverFrom()),
                             model.records()
                                  .existentialName(r.getQuantityUnit()));
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

    private void addMask(SelectQuery<Record> selectQuery, UUID classifier,
                         UUID classification, TableField<?, UUID> field,
                         ExistentialDomain domain) {
        if (classifier.equals(WellKnownRelationship.ANY.id())) {
            return;
        }
        Condition condition;
        if (classifier.equals(WellKnownRelationship.SAME.id())) {
            condition = field.eq(classification);
        } else {
            condition = field.in(inferenceSubQuery(classifier, classification));
        }
        condition = condition.or(field.equal(getAnyId(domain)))
                             .or(field.equal(getSameId(domain)))
                             .or(field.equal(getNotApplicable(domain)));
        selectQuery.addConditions(condition);
    }

    // Service gets special handling.  we don't want infinite jobs due to ANY
    private void addServiceMask(MetaProtocolRecord metaProtocol, JobRecord job,
                                SelectQuery<Record> selectQuery) {
        if (metaProtocol.getServiceType()
                        .equals(WellKnownRelationship.SAME.id())) {
            selectQuery.addConditions(PROTOCOL.SERVICE.equal(job.getService()));
        } else {
            selectQuery.addConditions(PROTOCOL.SERVICE.in(inferenceSubQuery(metaProtocol.getServiceType(),
                                                                            job.getService())));
        }
    }

    // Status gets special handling.  we don't want infinite jobs due to ANY
    private void addStatusMask(MetaProtocolRecord metaProtocol, JobRecord job,
                               SelectQuery<Record> selectQuery) {
        if (metaProtocol.getStatus()
                        .equals(WellKnownRelationship.SAME.id())) {
            selectQuery.addConditions(PROTOCOL.STATUS.equal(job.getStatus()));
        } else {
            selectQuery.addConditions(PROTOCOL.STATUS.in(inferenceSubQuery(metaProtocol.getStatus(),
                                                                           job.getService())));
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
        child.setStatus(resolve(false, model.records()
                                            .resolve(protocol.getStatus()),
                                model.records()
                                     .resolve(parent.getStatus()),
                                model.records()
                                     .resolve(protocol.getStatus())));
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

    private UUID getAnyId(ExistentialDomain domain) {
        switch (domain) {
            case Agency:
                return WellKnownAgency.ANY.id();
            case Attribute:
                return WellKnownAttribute.ANY.id();
            case Interval:
                return WellKnownInterval.ANY.id();
            case Location:
                return WellKnownLocation.ANY.id();
            case Product:
                return WellKnownProduct.ANY.id();
            case Relationship:
                return WellKnownRelationship.ANY.id();
            case StatusCode:
                return WellKnownStatusCode.ANY.id();
            case Unit:
                return WellKnownUnit.ANY.id();

            default:
                throw new IllegalArgumentException();
        }
    }

    private UUID getNotApplicable(ExistentialDomain domain) {
        switch (domain) {
            case Agency:
                return WellKnownAgency.NOT_APPLICABLE.id();
            case Attribute:
                return WellKnownAttribute.NOT_APPLICABLE.id();
            case Interval:
                return WellKnownInterval.NOT_APPLICABLE.id();
            case Location:
                return WellKnownLocation.NOT_APPLICABLE.id();
            case Product:
                return WellKnownProduct.NOT_APPLICABLE.id();
            case Relationship:
                return WellKnownRelationship.NOT_APPLICABLE.id();
            case StatusCode:
                return WellKnownStatusCode.NOT_APPLICABLE.id();
            case Unit:
                return WellKnownUnit.NOT_APPLICABLE.id();

            default:
                throw new IllegalArgumentException();
        }
    }

    private List<ProtocolRecord> getProtocolsMatching(JobRecord job) {
        return model.create()
                    .selectFrom(PROTOCOL)
                    .where(PROTOCOL.SERVICE.equal(job.getService()))
                    .and(PROTOCOL.STATUS.equal(job.getStatus()))
                    .and(PROTOCOL.REQUESTER.equal(job.getRequester()))
                    .and(PROTOCOL.PRODUCT.equal(job.getProduct()))
                    .and(PROTOCOL.DELIVER_TO.equal(job.getDeliverTo()))
                    .and(PROTOCOL.DELIVER_FROM.equal(job.getDeliverFrom()))
                    .and(PROTOCOL.ASSIGN_TO.equal(job.getAssignTo()))
                    .fetch();
    }

    private UUID getSameId(ExistentialDomain domain) {
        switch (domain) {
            case Agency:
                return WellKnownAgency.SAME.id();
            case Attribute:
                return WellKnownAttribute.SAME.id();
            case Interval:
                return WellKnownInterval.SAME.id();
            case Location:
                return WellKnownLocation.SAME.id();
            case Product:
                return WellKnownProduct.SAME.id();
            case Relationship:
                return WellKnownRelationship.SAME.id();
            case StatusCode:
                return WellKnownStatusCode.SAME.id();
            case Unit:
                return WellKnownUnit.SAME.id();

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Do the work of matching protocols to networks defined by jobs and
     * metaprocols.
     *
     * @param metaprotocol
     * @param job
     * @return
     */

    private SelectConditionStep<Record1<UUID>> inferenceSubQuery(UUID classifier,
                                                                 UUID classification) {
        return model.create()
                    .select(EXISTENTIAL_NETWORK.CHILD)
                    .from(EXISTENTIAL_NETWORK)
                    .where(EXISTENTIAL_NETWORK.PARENT.equal(classification))
                    .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(classifier));
    }

    private List<JobRecord> insert(JobRecord parent, ProtocolRecord protocol,
                                   InferenceMap txfm) {
        if (parent.getDepth() > MAXIMUM_JOB_DEPTH) {
            throw new IllegalStateException(String.format("Maximum job depth exceeded.  parent: %s, protocol: %s",
                                                          toString(parent),
                                                          toString(protocol)));
        }
        List<JobRecord> jobs = new ArrayList<>();
        if (protocol.getChildrenRelationship()
                    .equals(kernel.getNotApplicableRelationship()
                                  .getId())) {
            JobRecord job = model.records()
                                 .newJob();
            job.setParent(parent.getId());
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

    private boolean isTerminalState(UUID status, UUID service) {
        return ZERO.equals(model.create()
                                .selectCount()
                                .from(STATUS_CODE_SEQUENCING)
                                .where(STATUS_CODE_SEQUENCING.PARENT.equal(status))
                                .and(STATUS_CODE_SEQUENCING.SERVICE.equal(service))
                                .fetchOne()
                                .value1());
    }

    private boolean isTxfm(Relationship relationship) {
        return !WellKnownRelationship.ANY.id()
                                         .equals(relationship.getId())
               && !WellKnownRelationship.SAME.id()
                                             .equals(relationship.getId());
    }

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

    private String toString(SelfSequencingAuthorizationRecord seq) {
        return seq.toString();
    }

    private void insert(JobRecord child, JobRecord parent,
                        ProtocolRecord protocol, InferenceMap txfm,
                        Product product) {
        child.setDepth(parent.getDepth() + 1);
        child.setStatus(protocol.getChildStatus());
        child.setParent(parent.getId());
        child.setProtocol(protocol.getId());
        child.setProduct(product.getId());
        copyIntoChild(parent, protocol, txfm, child);
        child.insert();
        if (log.isTraceEnabled()) {
            log.trace(String.format("Inserted job %s\nfrom protocol %s\ntxfm %s",
                                    toString(child), toString(protocol), txfm));
        }
    }
}
