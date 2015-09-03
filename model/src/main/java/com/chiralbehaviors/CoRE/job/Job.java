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
package com.chiralbehaviors.CoRE.job;

import static com.chiralbehaviors.CoRE.job.Job.EXISTING_JOB_WITH_PARENT_AND_PROTOCOL;
import static com.chiralbehaviors.CoRE.job.Job.FIND_ALL;
import static com.chiralbehaviors.CoRE.job.Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS;
import static com.chiralbehaviors.CoRE.job.Job.GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES;
import static com.chiralbehaviors.CoRE.job.Job.GET_ACTIVE_OR_TERMINATED_SUB_JOBS;
import static com.chiralbehaviors.CoRE.job.Job.GET_ASSIGNED_TO;
import static com.chiralbehaviors.CoRE.job.Job.GET_CHILD_JOBS;
import static com.chiralbehaviors.CoRE.job.Job.GET_CHILD_JOBS_FOR_SERVICE;
import static com.chiralbehaviors.CoRE.job.Job.GET_NEXT_STATUS_CODES;
import static com.chiralbehaviors.CoRE.job.Job.GET_STATUS_CODE_SEQUENCES;
import static com.chiralbehaviors.CoRE.job.Job.GET_SUB_JOBS_ASSIGNED_TO;
import static com.chiralbehaviors.CoRE.job.Job.GET_TERMINAL_STATES;
import static com.chiralbehaviors.CoRE.job.Job.GET_UNSET_SIBLINGS;
import static com.chiralbehaviors.CoRE.job.Job.HAS_SCS;
import static com.chiralbehaviors.CoRE.job.Job.INITIAL_STATE;
import static com.chiralbehaviors.CoRE.job.Job.TOP_LEVEL_JOBS;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An instantiation of a service; something actually done
 *
 */
@NamedQueries({ @NamedQuery(name = FIND_ALL, query = "select j from Job j"),
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
                @NamedQuery(name = GET_CHILD_JOBS, query = "SELECT j "
                                                           + "FROM Job AS j "
                                                           + "WHERE j.parent = :parent"),
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
                @NamedQuery(name = GET_ASSIGNED_TO, query = "SELECT j "
                                                            + "FROM Job AS j "
                                                            + "  WHERE j.assignTo = :agency"),
                @NamedQuery(name = GET_SUB_JOBS_ASSIGNED_TO, query = "SELECT j "
                                                                     + "FROM Job AS j "
                                                                     + "WHERE j.parent = :parent "
                                                                     + "  AND j.assignTo = :agency"),
                @NamedQuery(name = INITIAL_STATE, query = "SELECT distinct(sc) "
                                                          + "FROM StatusCodeSequencing AS seq, StatusCode AS sc "
                                                          + "WHERE seq.parentCode = sc "
                                                          + "AND NOT EXISTS( "
                                                          + "  SELECT seq2.childCode FROM StatusCodeSequencing seq2 "
                                                          + "  WHERE seq2.service = seq.service "
                                                          + "    AND seq2.childCode = seq.parentCode "
                                                          + ") "
                                                          + " AND seq.service = :service"),
                @NamedQuery(name = GET_TERMINAL_STATES, query = "SELECT DISTINCT(sc) "
                                                                + "FROM StatusCodeSequencing AS seq, StatusCode AS sc "
                                                                + "WHERE seq.childCode = sc "
                                                                + "  AND NOT EXISTS ( "
                                                                + "    SELECT seq2.parentCode FROM StatusCodeSequencing seq2"
                                                                + "    WHERE seq2.service = seq.service "
                                                                + "      AND seq2.parentCode = seq.childCode "
                                                                + "  ) "
                                                                + "  AND seq.service = :service "
                                                                + "ORDER BY sc.name ASC") })
@Entity
@Table(name = "job", schema = "ruleform")
@Cacheable(false)
public class Job extends AbstractProtocol {
    public static final String CHANGE_STATUS                          = "job.changeStatus";
    public static final String CHRONOLOGY                             = "job.chronology";
    public static final String CLASSIFIED                             = "event.classified";
    public static final String EXISTING_JOB_WITH_PARENT_AND_PROTOCOL  = "job.existingJobWithParentAndProtocol";
    public static final String FIND_ALL                               = "job.findAll";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUS   = "job.getActiveJobsForAgencyInStatus";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY_IN_STATUSES = "job.getActiveJobsForAgencyInStatuses";
    public static final String GET_ACTIVE_OR_TERMINATED_SUB_JOBS      = "job.getActiveOrTerminatedSubJobs";
    public static final String GET_ASSIGNED_TO                        = "job.getAssignedTo";
    public static final String GET_ATTRIBUTE_VALUE                    = "job.getAttributeValue";
    public static final String GET_ATTRIBUTES_FOR_JOB                 = "job.getAttributesForJob";
    public static final String GET_CHILD_JOBS                         = "job.getChildJobs";
    public static final String GET_CHILD_JOBS_FOR_SERVICE             = "job.getChildJobsForService";
    public static final String GET_NEXT_STATUS_CODES                  = "job.getNextStatusCodes";
    public static final String GET_STATUS_CODE_IDS                    = "job.getStatusCodeIds";
    public static final String GET_STATUS_CODE_SEQUENCES              = "job.getStatusCodeSequences";
    public static final String GET_SUB_JOBS_ASSIGNED_TO               = "job.getSubJobsAssignedTo";
    public static final String GET_TERMINAL_STATES                    = "job.getTerminalStates";
    public static final String GET_UNSET_SIBLINGS                     = "job.getUnsetSiblings";
    public static final String HAS_SCS                                = "job.hasScs";
    public static final String INITIAL_STATE                          = "job.initialState";
    public static final String TOP_LEVEL_JOBS                         = "job.topLevelJobs";

    private static final long serialVersionUID = 1L;

    /**
     * The children of this job
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    private Set<Job> childJobs;

    /**
     * The chronology of this job
     */
    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private Set<JobChronology> chronology;

    @Column(name = "depth")
    private int depth = 0;

    /**
     * The parent of this job
     */
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Job parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "protocol")
    private Protocol protocol;

    /**
     * This job's status
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status")
    private StatusCode status;

    public Job() {
    }

    /**
     * @param updatedBy
     */
    public Job(Agency updatedBy) {
        super(updatedBy);
    }

    public Set<Job> getChildJobs() {
        return childJobs;
    }

    public Set<JobChronology> getChronology() {
        return chronology;
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

    public StatusCode getStatus() {
        return status;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    public void setChildJobs(Set<Job> jobs) {
        childJobs = jobs;
    }

    public void setChronology(Set<JobChronology> jobChronologies) {
        chronology = jobChronologies;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setParent(Job job) {
        if (equals(job)) {
            throw new IllegalArgumentException("Cannot set the parent to self");
        }
        parent = job;
        depth = job == null ? 0 : job.getDepth() + 1;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
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
    public void setStatus(StatusCode newStatus) {
        status = newStatus;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Job [status=%s, %s]", getStatus().getName(),
                             getToString());
    }

    @Override
    public void update(Triggers triggers) {
        triggers.update(this);
    }
}
