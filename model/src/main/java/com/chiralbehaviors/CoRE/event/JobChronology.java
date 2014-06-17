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
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

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
public class JobChronology extends Ruleform {
    public static final String FIND_ALL         = "jobChronology"
                                                  + FIND_ALL_SUFFIX;
    public static final String FIND_FOR_JOB     = "jobChronology.findForJob";
    public static final String FIND_FOR_PRODUCT = "jobChronology.findForProduct";
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Job
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job")
    private Job                job;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product")
    private Product            product;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_to")
    private Agency             assignTo;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliver_from")
    private Location           deliverFrom;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliver_to")
    private Location           deliverTo;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Job                parent;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester")
    private Agency             requester;

    // bi-directional many-to-one association to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service")
    private Product            service;

    @SequenceGenerator(schema = "ruleform", name = "job_chronology_seq", sequenceName = "job_chronology_seq")
    @GeneratedValue(generator = "job_chronology_seq", strategy = GenerationType.SEQUENCE)
    private Long               sequence;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private StatusCode         status;

    @Column(name = "time_stamp")
    private Timestamp          timeStamp;

    @Column(name = "chronology_notes")
    private String             chronologyNotes;

    public JobChronology() {
    }

    /**
     * @param updatedBy
     */
    public JobChronology(Agency updatedBy) {
        super(updatedBy);
    }

    public JobChronology(Job job, StatusCode status, Timestamp timeStamp,
                         String notes, Agency updatedBy) {
        super(notes, updatedBy);
        this.job = job;
        product = job.getProduct();
        this.status = status;
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
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    public Long getSequence() {
        return sequence;
    }

    /**
     * @return the status
     */
    public StatusCode getStatus() {
        return status;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setJob(Job job) {
        this.job = job;
        product = job.getProduct();
    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @param sequence
     *            the sequence to set
     */
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(StatusCode status) {
        this.status = status;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the assignTo
     */
    public Agency getAssignTo() {
        return assignTo;
    }

    /**
     * @param assignTo the assignTo to set
     */
    public void setAssignTo(Agency assignTo) {
        this.assignTo = assignTo;
    }

    /**
     * @return the deliverFrom
     */
    public Location getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @param deliverFrom the deliverFrom to set
     */
    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @return the deliverTo
     */
    public Location getDeliverTo() {
        return deliverTo;
    }

    /**
     * @param deliverTo the deliverTo to set
     */
    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    /**
     * @return the parent
     */
    public Job getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Job parent) {
        this.parent = parent;
    }

    /**
     * @return the requester
     */
    public Agency getRequester() {
        return requester;
    }

    /**
     * @param requester the requester to set
     */
    public void setRequester(Agency requester) {
        this.requester = requester;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(Product service) {
        this.service = service;
    }

    /**
     * @return the chronologyNotes
     */
    public String getChronologyNotes() {
        return chronologyNotes;
    }

    /**
     * @param jobNotes the jobNotes to set
     */
    public void setChronologyNotes(String chronologyNotes) {
        this.chronologyNotes = chronologyNotes;
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
        if (status != null) {
            status = (StatusCode) status.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}