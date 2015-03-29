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

import static com.chiralbehaviors.CoRE.event.JobChronology.FIND_ALL;
import static com.chiralbehaviors.CoRE.event.JobChronology.FIND_FOR_JOB;
import static com.chiralbehaviors.CoRE.event.JobChronology.FIND_FOR_PRODUCT;
import static com.chiralbehaviors.CoRE.event.JobChronology.GET_LOG_FOR_SEQUENCE;
import static com.chiralbehaviors.CoRE.event.JobChronology.HIGHEST_SEQUENCE_FOR_JOB;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the job_chronology database table.
 *
 */
@NamedQueries({
               @NamedQuery(name = FIND_ALL, query = "SELECT j FROM JobChronology j"),
               @NamedQuery(name = FIND_FOR_JOB, query = "SELECT j FROM JobChronology j "
                                                        + "WHERE j.job = :job ORDER BY j.sequenceNumber "),
               @NamedQuery(name = FIND_FOR_PRODUCT, query = "SELECT j FROM JobChronology j "
                                                            + "WHERE j.product = :product "),
               @NamedQuery(name = HIGHEST_SEQUENCE_FOR_JOB, query = "SELECT MAX(j.sequenceNumber) FROM JobChronology j "
                                                                    + "WHERE j.job = :job"),
               @NamedQuery(name = GET_LOG_FOR_SEQUENCE, query = "SELECT j from JobChronology j "
                                                                + "WHERE j.job = :job "
                                                                + "    AND j.sequenceNumber = :sequence") })
@Entity
@Table(name = "job_chronology", schema = "ruleform")
public class JobChronology extends AbstractProtocol {
    public static final String FIND_ALL                 = "jobChronology"
                                                          + FIND_ALL_SUFFIX;
    public static final String FIND_FOR_JOB             = "jobChronology.findForJob";
    public static final String FIND_FOR_PRODUCT         = "jobChronology.findForProduct";
    public static final String GET_LOG_FOR_SEQUENCE     = "jobChronology.getLogForSequenc";
    public static final String HIGHEST_SEQUENCE_FOR_JOB = "jobChronology.highestSequenceForJob";
    private static final long  serialVersionUID         = 1L;

    // bi-directional many-to-one association to Job
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "job")
    private Job                job;

    @Column(name = "sequence_number")
    private int                sequenceNumber           = 0;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status")
    private StatusCode         status;

    public JobChronology() {
    }

    public JobChronology(Job job, String notes) {
        super(notes, job.getUpdatedBy());
        initializeFrom(job);
    }

    public JobChronology(Job job, String notes, int sequenceNumber) {
        super(notes, job.getUpdatedBy());
        initializeFrom(job);
        setSequenceNumber(sequenceNumber);
    }

    /**
     * @param id
     */
    public JobChronology(UUID id) {
        super(id);
    }

    public Job getJob() {
        return job;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the status
     */
    public StatusCode getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, JobChronology> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.jobChronology;
    }

    /**
     * @param job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * @param sequenceNumber
     *            the sequence to set
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(StatusCode status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("JobChronology [status=%s, %s, sequenceNumber=%s, notes=%s]",
                             getStatus().getName(), getToString(),
                             sequenceNumber, getNotes());
    }

    @SuppressWarnings("unused")
    private void validate() {
        if (getJob() == null) {
            throw new IllegalStateException();
        }
        if (getStatus() == null) {
            throw new IllegalStateException();
        }
        if (getAssignTo() == null) {
            throw new IllegalStateException();
        }
        if (getAssignToAttribute() == null) {
            throw new IllegalStateException();
        }
        if (getDeliverFrom() == null) {
            throw new IllegalStateException();
        }
        if (getDeliverFromAttribute() == null) {
            throw new IllegalStateException();
        }
        if (getDeliverTo() == null) {
            throw new IllegalStateException();
        }
        if (getDeliverToAttribute() == null) {
            throw new IllegalStateException();
        }
        if (getProduct() == null) {
            throw new IllegalStateException();
        }
        if (getProductAttribute() == null) {
            throw new IllegalStateException();
        }
        if (getQuantity() == null) {
            throw new IllegalStateException();
        }
        if (getQuantityUnit() == null) {
            throw new IllegalStateException();
        }
        if (getRequester() == null) {
            throw new IllegalStateException();
        }
        if (getRequesterAttribute() == null) {
            throw new IllegalStateException();
        }
        if (getService() == null) {
            throw new IllegalStateException();
        }
        if (getServiceAttribute() == null) {
            throw new IllegalStateException();
        }
    }

    protected void initializeFrom(Job job) {
        setJob(job);
        setStatus(job.getStatus());
        copyFrom(job);
    }
}