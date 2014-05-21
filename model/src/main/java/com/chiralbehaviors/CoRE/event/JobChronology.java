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

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;

/**
 * The persistent class for the job_chronology database table.
 * 
 */
@Entity
@Table(name = "job_chronology", schema = "ruleform")
public class JobChronology extends Ruleform {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Job
    @ManyToOne
    @JoinColumn(name = "job")
    private Job               job;

    @SequenceGenerator(schema = "ruleform", name = "job_chronology_seq", sequenceName = "job_chronology_seq")
    @GeneratedValue(generator = "job_chronology_seq", strategy = GenerationType.SEQUENCE)
    private Long              sequence;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "old_status")
    private StatusCode        oldStatusCode;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "new_status")
    private StatusCode        newStatusCode;

    @Column(name = "time_stamp")
    private Timestamp         timeStamp;

    public JobChronology() {
    }

    /**
     * @param updatedBy
     */
    public JobChronology(Agency updatedBy) {
        super(updatedBy);
    }

    public JobChronology(Job job, StatusCode oldStatus, StatusCode newStatus,
                         Timestamp timeStamp, String notes, Agency updatedBy) {
        super(notes, updatedBy);
        this.job = job;
        oldStatusCode = oldStatus;
        newStatusCode = newStatus;
        this.timeStamp = timeStamp;
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

    /**
     * @return the newStatusCode
     */
    public StatusCode getNewStatusCode() {
        return newStatusCode;
    }

    /**
     * @return the oldStatusCode
     */
    public StatusCode getOldStatusCode() {
        return oldStatusCode;
    }

    public Long getSequence() {
        return sequence;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * @param newStatusCode
     *            the newStatusCode to set
     */
    public void setNewStatusCode(StatusCode newStatusCode) {
        this.newStatusCode = newStatusCode;
    }

    /**
     * @param oldStatusCode
     *            the oldStatusCode to set
     */
    public void setOldStatusCode(StatusCode oldStatusCode) {
        this.oldStatusCode = oldStatusCode;
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
        if (oldStatusCode != null) {
            oldStatusCode = (StatusCode) oldStatusCode.manageEntity(em,
                                                                    knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}