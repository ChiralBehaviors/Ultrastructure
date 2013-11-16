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
package com.hellblazer.CoRE.event;

import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the job_chronology database table.
 * 
 */
@Entity
@Table(name = "job_chronology", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "job_chronology_id_seq", sequenceName = "job_chronology_id_seq")
public class JobChronology extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "job_chronology_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Job
    @ManyToOne
    @JoinColumn(name = "job")
    private Job               job;

    @SequenceGenerator(schema = "ruleform", name = "job_chronology_seq", sequenceName = "job_chronology_seq")
    @GeneratedValue(generator = "job_chronology_seq", strategy = GenerationType.SEQUENCE)
    private Long              sequence;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "status")
    private StatusCode        statusCode;

    @Column(name = "time_stamp")
    private Timestamp         timeStamp;

    public JobChronology() {
    }

    public JobChronology(Job job, StatusCode status, Timestamp timeStamp,
                         String notes, Resource updatedBy) {
        super(notes, updatedBy);
        this.job = job;
        statusCode = status;
        this.timeStamp = timeStamp;
    }

    /**
     * @param id
     */
    public JobChronology(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public JobChronology(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public Long getSequence() {
        return sequence;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (job != null) {
            job = (Job) job.manageEntity(em, knownObjects);
        }
        if (statusCode != null) {
            statusCode = (StatusCode) statusCode.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}