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

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;

/**
 * The persistent class for the job_chronology database table.
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_ALL, query = "select j from JobChronology j"),
               @NamedQuery(name = FIND_FOR_JOB, query = "select j from JobChronology j "
                                                        + "where j.job = :job "),
               @NamedQuery(name = FIND_FOR_PRODUCT, query = "select j from JobChronology j "
                                                            + "where j.product = :product ") })
@Entity
@Table(name = "job_chronology", schema = "ruleform")
public class JobChronology extends AbstractProtocol {
    public static final String FIND_ALL         = "jobChronology"
                                                  + FIND_ALL_SUFFIX;
    public static final String FIND_FOR_JOB     = "jobChronology.findForJob";
    public static final String FIND_FOR_PRODUCT = "jobChronology.findForProduct";
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Job
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job")
    private Job                job;

    @SequenceGenerator(schema = "ruleform", name = "job_chronology_seq", sequenceName = "job_chronology_seq")
    @GeneratedValue(generator = "job_chronology_seq", strategy = GenerationType.SEQUENCE)
    private Long               sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private StatusCode         status;

    /**
     * @return the status
     */
    public StatusCode getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(StatusCode status) {
        this.status = status;
    }

    @Column(name = "time_stamp")
    private Timestamp timeStamp;

    public JobChronology() {
    }

    /**
     * @param updatedBy
     */
    public JobChronology(Agency updatedBy) {
        super(updatedBy);
    }

    public JobChronology(Job job, String notes, Agency updatedBy) {
        super(notes, updatedBy);
        this.job = job;
        this.status = job.getStatus();
        this.timeStamp = job.getUpdateDate();
        this.copyFrom(job);
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

    public Long getSequence() {
        return sequence;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setJob(Job job) {
        this.job = job;
        this.status = job.getStatus();
        this.timeStamp = job.getUpdateDate();
        copyFrom(job);
    }

    /**
     * @param sequence
     *            the sequence to set
     */
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (job != null) {
            job = (Job) job.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }
}