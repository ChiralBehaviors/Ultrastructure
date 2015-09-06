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

import static com.chiralbehaviors.CoRE.job.JobChronology.FIND_ALL;
import static com.chiralbehaviors.CoRE.job.JobChronology.FIND_FOR_JOB;
import static com.chiralbehaviors.CoRE.job.JobChronology.FIND_FOR_PRODUCT;
import static com.chiralbehaviors.CoRE.job.JobChronology.GET_LOG_FOR_SEQUENCE;
import static com.chiralbehaviors.CoRE.job.JobChronology.HIGHEST_SEQUENCE_FOR_JOB;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.job.status.StatusCode;

/**
 * The persistent class for the job_chronology database table.
 *
 */
@NamedQueries({ @NamedQuery(name = FIND_ALL, query = "SELECT j FROM JobChronology j"),
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
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "job")
    private Job job;

    @Column(name = "sequence_number")
    private int sequenceNumber = 0;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private StatusCode status;

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
        if (getDeliverFrom() == null) {
            throw new IllegalStateException();
        }
        if (getDeliverTo() == null) {
            throw new IllegalStateException();
        }
        if (getProduct() == null) {
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
        if (getService() == null) {
            throw new IllegalStateException();
        }
    }

    protected void initializeFrom(Job job) {
        setJob(job);
        setStatus(job.getStatus());
        copyFrom(job);
    }
}
