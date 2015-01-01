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
package com.chiralbehaviors.CoRE.event;

import static com.chiralbehaviors.CoRE.event.Job.ACTIVE_JOBS;
import static com.chiralbehaviors.CoRE.event.Job.EXISTING_JOB_WITH_PARENT_AND_PROTOCOL;
import static com.chiralbehaviors.CoRE.event.Job.FIND_ALL;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_EXPLICIT_JOBS;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_JOBS_FOR_AGENCY;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_OR_TERMINATED_SUB_JOBS;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_SUB_JOBS;
import static com.chiralbehaviors.CoRE.event.Job.GET_ACTIVE_SUB_JOBS_FOR_SERVICE;
import static com.chiralbehaviors.CoRE.event.Job.GET_CHILD_JOBS_FOR_SERVICE;
import static com.chiralbehaviors.CoRE.event.Job.GET_INITIAL_SUB_JOBS;
import static com.chiralbehaviors.CoRE.event.Job.GET_NEXT_STATUS_CODES;
import static com.chiralbehaviors.CoRE.event.Job.GET_STATUS_CODE_SEQUENCES;
import static com.chiralbehaviors.CoRE.event.Job.GET_SUB_JOBS_ASSIGNED_TO;
import static com.chiralbehaviors.CoRE.event.Job.GET_TERMINAL_STATES;
import static com.chiralbehaviors.CoRE.event.Job.GET_UNSET_SIBLINGS;
import static com.chiralbehaviors.CoRE.event.Job.HAS_SCS;
import static com.chiralbehaviors.CoRE.event.Job.INITIAL_STATE;
import static com.chiralbehaviors.CoRE.event.Job.TOP_LEVEL_JOBS;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An instantiation of a service; something actually done
 *
 */
@NamedQueries({
               @NamedQuery(name = FIND_ALL, query = "select j from Job j"),
               @NamedQuery(name = TOP_LEVEL_JOBS, query = "SELECT j  FROM Job AS j  WHERE j.parent IS NULL"),
               @NamedQuery(name = EXISTING_JOB_WITH_PARENT_AND_PROTOCOL, query = "SELECT COUNT(j) from Job as j "
                                                                                 + "WHERE j.parent = :parent "
                                                                                 + "AND j.protocol = :protocol"),
               @NamedQuery(name = GET_ACTIVE_OR_TERMINATED_SUB_JOBS, query = "SELECT j "
                                                                             + "FROM Job AS j "
                                                                             + "WHERE j.parent = :parent "
                                                                             + "  AND j.status <> :unset"),
               @NamedQuery(name = GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS, query = "SELECT j "
                                                                                + "FROM Job AS j "
                                                                                + "WHERE j.assignTo = :agency "
                                                                                + "  AND j.status = :status"),
               @NamedQuery(name = GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES, query = "SELECT j "
                                                                                  + "FROM Job AS j "
                                                                                  + "WHERE j.assignTo = :agency "
                                                                                  + "  AND j.status IN :statuses"),
               @NamedQuery(name = GET_CHILD_JOBS_FOR_SERVICE, query = "SELECT j "
                                                                      + "FROM Job AS j "
                                                                      + "WHERE j.parent = :parent "
                                                                      + "  AND j.service = :service"),
               @NamedQuery(name = HAS_SCS, query = "SELECT scs from StatusCodeSequencing scs where scs.service = :service "),
               @NamedQuery(name = GET_NEXT_STATUS_CODES, query = "SELECT code "
                                                                 + "FROM StatusCodeSequencing AS sequencing, StatusCode AS code "
                                                                 + "WHERE sequencing.childCode = code "
                                                                 + "AND sequencing.service = :service "
                                                                 + "  AND sequencing.parentCode = :parent "),
               @NamedQuery(name = GET_STATUS_CODE_SEQUENCES, query = "SELECT sequencing "
                                                                     + " FROM StatusCodeSequencing AS sequencing"
                                                                     + " WHERE sequencing.childCode = :code "
                                                                     + " AND sequencing.service = :service "
                                                                     + "   AND sequencing.parentCode = :parent "),
               @NamedQuery(name = GET_UNSET_SIBLINGS, query = "SELECT j FROM Job AS j "
                                                              + "WHERE j.service = :service "
                                                              + "  AND j.status = :unset "
                                                              + "  AND j.parent = :parent"),
               @NamedQuery(name = GET_SUB_JOBS_ASSIGNED_TO, query = "SELECT j "
                                                                    + "FROM Job AS j "
                                                                    + "WHERE j.parent = :parent "
                                                                    + "  AND j.assignTo = :agency") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = GET_TERMINAL_STATES, query = "SELECT DISTINCT(sc.*) "
                                                                           + "FROM ruleform.status_code_sequencing AS seq "
                                                                           + "    JOIN ruleform.status_code AS sc ON seq.child_code = sc.id "
                                                                           + "WHERE "
                                                                           + "  NOT EXISTS ( "
                                                                           + "    SELECT parent_code FROM ruleform.status_code_sequencing "
                                                                           + "    WHERE service = seq.service "
                                                                           + "      AND parent_code = seq.child_code "
                                                                           + "  ) "
                                                                           + "  AND service = ? "
                                                                           + "ORDER BY sc.name ASC"),
                     @NamedNativeQuery(name = GET_ACTIVE_SUB_JOBS, query = "SELECT job.* FROM ruleform.job as job "
                                                                           + "WHERE parent = ? "
                                                                           + "  AND ruleform.is_job_active( job.id )"),
                     @NamedNativeQuery(name = GET_ACTIVE_SUB_JOBS_FOR_SERVICE, query = "SELECT job.* FROM ruleform.job as job "
                                                                                       + "WHERE parent = ? "
                                                                                       + "  AND ruleform.is_job_active( job.id ) "
                                                                                       + "  AND job.service = ?"),
                     @NamedNativeQuery(name = ACTIVE_JOBS, query = "SELECT j.* "
                                                                   + "FROM ruleform.job_chronology AS jc "
                                                                   + "JOIN ruleform.job AS j ON jc.job = j.id "
                                                                   + " WHERE j.assign_to = $1 "
                                                                   + "  AND NOT ruleform.is_terminal_state(j.event, jc.status) "
                                                                   + "  AND jc.time_stamp = "
                                                                   + "   (SELECT max(time_stamp) FROM ruleform.job_chronology WHERE job = jc.job)"),
                     @NamedNativeQuery(name = GET_ACTIVE_EXPLICIT_JOBS, query = "SELECT j.* "
                                                                                + "FROM ruleform.job AS j "
                                                                                + "WHERE j.parent IS NULL "
                                                                                + "  AND ruleform.is_job_active( j.id )"),
                     @NamedNativeQuery(name = GET_ACTIVE_JOBS_FOR_AGENCY, query = "SELECT DISTINCT j.* "
                                                                                  + "FROM ruleform.job AS j "
                                                                                  + "WHERE j.assign_to = ? "
                                                                                  + "  AND NOT ruleform.is_terminal_state(j.service, j.status) "),
                     // Probably a candidate for 8.4 WITH query...
                     @NamedNativeQuery(name = GET_INITIAL_SUB_JOBS, query = "SELECT j.id  FROM ruleform.job AS j "
                                                                            + "JOIN ruleform.product_sibling_sequencing_authorization AS seq "
                                                                            + "    ON j.service = seq.parent "
                                                                            + "JOIN "
                                                                            + "( SELECT service FROM ruleform.job WHERE parent = ? "
                                                                            + "  EXCEPT "
                                                                            + "  SELECT next_sibling_status "
                                                                            + "    FROM ruleform.product_sibling_sequencing_authorization "
                                                                            + "    WHERE parent IN "
                                                                            + "    ( SELECT service FROM ruleform.job WHERE parent = ? ) "
                                                                            + ") AS valid ON j.service = valid.service "
                                                                            + "JOIN ruleform.status_code AS sc ON j.status = sc.id AND sc.id = ? "
                                                                            + "WHERE j.parent = ?"),
                     @NamedNativeQuery(name = INITIAL_STATE, query = "SELECT distinct(sc.*) "
                                                                     + "FROM ruleform.status_code_sequencing AS seq "
                                                                     + "JOIN ruleform.status_code AS sc ON seq.parent_code = sc.id "
                                                                     + "WHERE "
                                                                     + "NOT EXISTS( "
                                                                     + "  SELECT child_code FROM ruleform.status_code_sequencing "
                                                                     + "  WHERE service = seq.service "
                                                                     + "    AND child_code = seq.parent_code "
                                                                     + ") "
                                                                     + " AND seq.service = ?") })
@Entity
@Table(name = "job", schema = "ruleform")
public class Job extends AbstractProtocol {
    public static final String ACTIVE_JOBS                            = "job.getActiveJobs";
    public static final String CHANGE_STATUS                          = "job.changeStatus";
    public static final String CHRONOLOGY                             = "job.chronology";
    public static final String CLASSIFIED                             = "event.classified";
    public static final String FIND_ALL                               = "job.findAll";
    public static final String GET_ACTIVE_EXPLICIT_JOBS               = "job.getActiveExplicitJobs";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY             = "job.getActiveJobsForAgency";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS   = "job.getActiveJobsForAgencyInStatus";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES = "job.getActiveJobsForAgencyInStatuses";
    public static final String GET_ACTIVE_OR_TERMINATED_SUB_JOBS      = "job.getActiveOrTerminatedSubJobs";
    public static final String GET_ACTIVE_SUB_JOBS                    = "job.getActiveSubJobs";
    public static final String GET_ACTIVE_SUB_JOBS_FOR_SERVICE        = "job.getActiveSubJobsForService";
    public static final String GET_ATTRIBUTE_VALUE                    = "job.getAttributeValue";
    public static final String GET_ATTRIBUTES_FOR_JOB                 = "job.getAttributesForJob";
    public static final String GET_CHILD_JOBS_FOR_SERVICE             = "job.getChildJobsForService";
    public static final String GET_INITIAL_SUB_JOBS                   = "job.getInitialSubJobs";
    public static final String EXISTING_JOB_WITH_PARENT_AND_PROTOCOL  = "job.existingJobWithParentAndProtocol";
    public static final String GET_NEXT_STATUS_CODES                  = "job.getNextStatusCodes";
    public static final String GET_STATUS_CODE_IDS                    = "job.getStatusCodeIds";
    public static final String GET_STATUS_CODE_SEQUENCES              = "job.getStatusCodeSequences";
    public static final String GET_SUB_JOBS_ASSIGNED_TO               = "job.getSubJobsAssignedTo";
    public static final String GET_TERMINAL_STATES                    = "job.getTerminalStates";
    public static final String GET_UNSET_SIBLINGS                     = "job.getUnsetSiblings";
    public static final String HAS_SCS                                = "job.hasScs";
    public static final String INITIAL_STATE                          = "job.initialState";
    public static final String TOP_LEVEL_JOBS                         = "job.topLevelJobs";

    private static final long  serialVersionUID                       = 1L;

    /**
     * The children of this job
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    private Set<Job>           childJobs;

    /**
     * The chronology of this job
     */
    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private Set<JobChronology> chronology;

    @Column(name = "current_log_sequence")
    private int                currentLogSequence                     = 0;

    @Column(name = "depth")
    private int                depth                                  = 0;

    /**
     * The parent of this job
     */
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent", updatable = false)
    private Job                parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "protocol", updatable = false)
    private Protocol           protocol;

    @Column(name = "sequence_number")
    private int                sequenceNumber                         = 1;

    /**
     * This job's status
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status")
    private StatusCode         status;

    public Job() {
    }

    /**
     * @param updatedBy
     */
    public Job(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * Should ONLY be called from JobModel. You call this yourself, you ain't be
     * logging. We have to make it Public because it ain't in the same package.
     * <p>
     * <b>word</b>
     * </p>
     *
     * @param newStatus
     */
    public void _setStatus(StatusCode newStatus) {
        status = newStatus;
    }

    public Set<Job> getChildJobs() {
        return childJobs;
    }

    public Set<JobChronology> getChronology() {
        return chronology;
    }

    public int getCurrentLogSequence() {
        return currentLogSequence;
    }

    public int getDepth() {
        return depth;
    }

    public Job getParent() {
        return parent;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public StatusCode getStatus() {
        return status;
    }

    /**
     * @return
     */
    public int nextLogSequence() {
        currentLogSequence++;
        return currentLogSequence;
    }

    public void setChildJobs(Set<Job> jobs) {
        childJobs = jobs;
    }

    public void setChronology(Set<JobChronology> jobChronologies) {
        chronology = jobChronologies;
    }

    public void setCurrentLogSequence(int currentLogSequence) {
        this.currentLogSequence = currentLogSequence;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setParent(Job job) {
        if (equals(job)) {
            throw new IllegalArgumentException("Cannot set the parent to self");
        }
        parent = job;
        depth = job.getDepth() + 1;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Job [status=%s, %s, sequenceNumber=%s, currentLogSequence=%s]",
                             getStatus().getName(), getToString(),
                             sequenceNumber, currentLogSequence);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, Job> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.job;
    }
}
