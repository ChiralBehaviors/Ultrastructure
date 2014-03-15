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
package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.event.Job.ACTIVE_JOBS;
import static com.hellblazer.CoRE.event.Job.FIND_ALL;
import static com.hellblazer.CoRE.event.Job.GET_ACTIVE_EXPLICIT_JOBS;
import static com.hellblazer.CoRE.event.Job.GET_ACTIVE_JOBS_FOR_AGENCY;
import static com.hellblazer.CoRE.event.Job.GET_ACTIVE_OR_TERMINATED_SUB_JOBS;
import static com.hellblazer.CoRE.event.Job.GET_ACTIVE_SUB_JOBS;
import static com.hellblazer.CoRE.event.Job.GET_ACTIVE_SUB_JOBS_FOR_SERVICE;
import static com.hellblazer.CoRE.event.Job.GET_ATTRIBUTES_FOR_JOB;
import static com.hellblazer.CoRE.event.Job.GET_ATTRIBUTE_VALUE;
import static com.hellblazer.CoRE.event.Job.GET_INITIAL_SUB_JOBS;
import static com.hellblazer.CoRE.event.Job.GET_NEXT_STATUS_CODES;
import static com.hellblazer.CoRE.event.Job.GET_STATUS_CODE_SEQUENCES;
import static com.hellblazer.CoRE.event.Job.GET_SUB_JOBS_ASSIGNED_TO;
import static com.hellblazer.CoRE.event.Job.GET_TERMINAL_STATES;
import static com.hellblazer.CoRE.event.Job.GET_UNSET_SIBLINGS;
import static com.hellblazer.CoRE.event.Job.HAS_SCS;
import static com.hellblazer.CoRE.event.Job.INITIAL_STATE;
import static com.hellblazer.CoRE.event.Job.STATUS_CODE;
import static com.hellblazer.CoRE.event.Job.TOP_LEVEL_JOBS;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.product.Product;

/**
 * An instantiation of a service; something actually done
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_ALL, query = "select j from Job j"),
               @NamedQuery(name = TOP_LEVEL_JOBS, query = "SELECT j  FROM Job AS j  WHERE j.parent IS NULL"),
               @NamedQuery(name = GET_ATTRIBUTE_VALUE, query = "SELECT ja "
                                                               + "FROM JobAttribute AS ja "
                                                               + "WHERE ja.attribute = :attribute "
                                                               + "AND ja.job = :job"),
               @NamedQuery(name = GET_ACTIVE_OR_TERMINATED_SUB_JOBS, query = "SELECT j "
                                                                             + "FROM Job AS j "
                                                                             + "WHERE j.parent = :parent "
                                                                             + "  AND j.status <> :unset"),
               @NamedQuery(name = GET_ATTRIBUTES_FOR_JOB, query = "SELECT ja "
                                                               + "FROM JobAttribute AS ja "
                                                               + "WHERE ja.job = :job"),                                                          
               @NamedQuery(name = HAS_SCS, query = "SELECT scs from StatusCodeSequencing scs where scs.service = :service "),
               @NamedQuery(name = GET_NEXT_STATUS_CODES, query = "SELECT code "
                                                                 + "FROM StatusCodeSequencing AS sequencing, StatusCode AS code "
                                                                 + "WHERE sequencing.childCode = code "
                                                                 + "AND sequencing.service = :service "
                                                                 + "  AND sequencing.parentCode = :parent "
                                                                 + "ORDER BY sequencing.sequenceNumber"),
               @NamedQuery(name = GET_STATUS_CODE_SEQUENCES, query = "SELECT sequencing "
                                                                     + " FROM StatusCodeSequencing AS sequencing"
                                                                     + " WHERE sequencing.childCode = :code "
                                                                     + " AND sequencing.service = :service "
                                                                     + "   AND sequencing.parentCode = :parent "
                                                                     + " ORDER BY sequencing.sequenceNumber"),
               @NamedQuery(name = GET_UNSET_SIBLINGS, query = "SELECT j FROM Job AS j "
                                                              + "WHERE j.service = :service "
                                                              + "  AND j.status = :unset "
                                                              + "  AND j.parent = :parent"),
               @NamedQuery(name = GET_SUB_JOBS_ASSIGNED_TO, query = "SELECT j "
                                                                    + "FROM Job AS j "
                                                                    + "WHERE j.parent = :job "
                                                                    + "  AND j.assignTo = :agency") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = GET_TERMINAL_STATES, query = "SELECT DISTINCT(sc.*) "
                                                                           + "FROM ruleform.status_code_sequencing AS seq "
                                                                           + "    JOIN ruleform.status_code AS sc ON seq.child_code = sc.id "
                                                                           + "WHERE "
                                                                           + "  NOT EXISTS ( "
                                                                           + "    SELECT parent_code FROM ruleform.status_code_sequencing "
                                                                           + "    WHERE event = seq.event "
                                                                           + "      AND parent_code = seq.child_code "
                                                                           + "  ) "
                                                                           + "  AND event = :event "
                                                                           + "ORDER BY sc.name ASC"),
                     @NamedNativeQuery(name = GET_ACTIVE_SUB_JOBS, query = "SELECT job.* FROM ruleform.job as job "
                                                                           + "WHERE parent = ? "
                                                                           + "  AND ruleform.is_job_active( job.id )"),
                     @NamedNativeQuery(name = GET_ACTIVE_SUB_JOBS_FOR_SERVICE, query = "SELECT j.* FROM ruleform.job AS j "
                                                                                       + "WHERE j.service = ? "
                                                                                       + "  AND ruleform.is_job_active( j.id ) "
                                                                                       + "  AND j.parent = ?"),
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
                     @NamedNativeQuery(name = GET_ACTIVE_JOBS_FOR_AGENCY, query = "SELECT j.* "
                                                                                  + "FROM ruleform.job_chronology AS jc "
                                                                                  + "JOIN ruleform.job AS j ON jc.job = j.id "
                                                                                  + "WHERE j.assign_to = ? "
                                                                                  + "  AND NOT ruleform.is_terminal_state(j.service, jc.status) "
                                                                                  + "  AND jc.time_stamp = "
                                                                                  + "    (SELECT max(time_stamp) FROM ruleform.job_chronology WHERE job = jc.job)"),
                     //Probably a candidate for 8.4 WITH query...
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
                     @NamedNativeQuery(name = STATUS_CODE, query = "SELECT DISTINCT(parent_code)"
                                                                   + " FROM ruleform.status_code_sequencing "
                                                                   + " WHERE service = ? "
                                                                   + " UNION "
                                                                   + " SELECT DISTINCT(child_code)"
                                                                   + " FROM ruleform.status_code_sequencing "
                                                                   + " WHERE service = ? ", resultClass = Long.class),
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
@SequenceGenerator(schema = "ruleform", name = "job_id_seq", sequenceName = "job_id_seq")
public class Job extends Ruleform implements Attributable<JobAttribute> {
    public static final String ACTIVE_JOBS                       = "job.getActiveJobs";
    public static final String CHANGE_STATUS                     = "job.changeStatus";
    public static final String CHRONOLOGY                        = "job.chronology";
    public static final String CLASSIFIED                        = "event.classified";
    public static final String FIND_ALL                          = "job.findAll";
    public static final String GET_ACTIVE_EXPLICIT_JOBS          = "job.getActiveExplicitJobs";
    public static final String GET_ACTIVE_JOBS_FOR_AGENCY        = "job.getActiveJobsForAgency";
    public static final String GET_ACTIVE_OR_TERMINATED_SUB_JOBS = "job.getActiveOrTerminatedSubJobs";
    public static final String GET_ACTIVE_SUB_JOBS               = "job.getActiveSubJobs";
    public static final String GET_ACTIVE_SUB_JOBS_FOR_SERVICE   = "job.getActiveSubJobsForService";
    public static final String GET_ATTRIBUTES_FOR_JOB			 = "job.getAttributesForJob";
    public static final String GET_ATTRIBUTE_VALUE               = "job.getAttributeValue";
    public static final String GET_INITIAL_SUB_JOBS              = "job.getInitialSubJobs";
    public static final String GET_NEXT_STATUS_CODES             = "job.getNextStatusCodes";
    public static final String GET_STATUS_CODE_IDS               = "job.getStatusCodeIds";
    public static final String GET_SUB_JOBS_ASSIGNED_TO          = "job.getSubJobsAssignedTo";
    public static final String GET_TERMINAL_STATES               = "job.getTerminalStates";
    public static final String GET_UNSET_SIBLINGS                = "job.getUnsetSiblings";
    public static final String GET_STATUS_CODE_SEQUENCES         = "job.getStatusCodeSequences";
    public static final String HAS_SCS                           = "job.hasScs";
    public static final String INITIAL_STATE                     = "job.initialState";
    public static final String STATUS_CODE                       = "job.statusCode";
    public static final String TOP_LEVEL_JOBS                    = "job.topLevelJobs";

    private static final long  serialVersionUID                  = 1L;

    /**
     * The agency assigned to this job
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assign_to")
    private Agency             assignTo;

    /**
     * The attributes of this job
     */
    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private Set<JobAttribute>  attributes;

    /**
     * The children of this job
     */
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<Job>           childJobs;

    /**
     * The chronology of this job
     */
    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private Set<JobChronology> chronology;

    /**
     * The location where the product will be delivered from
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deliver_from")
    private Location           deliverFrom;

    /**
     * The location to deliver the product of this job
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deliver_to")
    private Location           deliverTo;

    @Id
    @GeneratedValue(generator = "job_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    /**
     * The parent of this job
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Job                parent;

    /**
     * The end product of this job
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product")
    private Product            product;

    /**
     * The consumer of this job's product
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requester")
    private Agency             requester;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber                    = 1;

    /**
     * The service this job is performing
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service")
    private Product            service;

    /**
     * This job's status
     */
    @ManyToOne
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

    public Job(Agency assignTo, Agency requester, Product service,
               Product product, Location deliverTo, Location deliverFrom,
               Agency updatedBy) {
        this(null, assignTo, service, product, deliverTo, deliverFrom,
             requester, updatedBy);
    }

    public Job(Job parent, Agency assignTo, Product service, Product product,
               Location deliverTo, Location deliverFrom, Agency requester,
               Agency updatedBy) {
        this(updatedBy);
        setParent(parent);
        setAssignTo(assignTo);
        setService(service);
        setProduct(product);
        setDeliverTo(deliverTo);
        setDeliverFrom(deliverFrom);
        setRequester(requester);
    }

    /**
     * @param id
     */
    public Job(Long id) {
        super(id);
    }

    public Agency getAssignTo() {
        return assignTo;
    }

    @Override
    public Set<JobAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<JobAttribute> getAttributeType() {
        return JobAttribute.class;
    }

    public Set<Job> getChildJobs() {
        return childJobs;
    }

    public Set<JobChronology> getChronology() {
        return chronology;
    }

    /**
     * @return the deliverFrom
     */
    public Location getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @return the deliverTo
     */
    public Location getDeliverTo() {
        return deliverTo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Job getParent() {
        return parent;
    }

    /**
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @return the requester
     */
    public Agency getRequester() {
        return requester;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    public StatusCode getStatus() {
        return status;
    }

    public void setAssignTo(Agency agency2) {
        assignTo = agency2;
    }

    @Override
    public void setAttributes(Set<JobAttribute> jobAttributes) {
        attributes = jobAttributes;
    }

    public void setChildJobs(Set<Job> jobs) {
        childJobs = jobs;
    }

    public void setChronology(Set<JobChronology> jobChronologies) {
        chronology = jobChronologies;
    }

    /**
     * @param deliverFrom
     *            the deliverFrom to set
     */
    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @param deliverTo
     *            the deliverTo to set
     */
    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setParent(Job job) {
        parent = job;
    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @param requester
     *            the requester to set
     */
    public void setRequester(Agency requester) {
        this.requester = requester;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Product service) {
        this.service = service;
    }

    public void setStatus(StatusCode statusCode) {
        status = statusCode;
    }

    @Override
    public String toString() {
        return "Job [id=" + id + ", sequenceNumber=" + sequenceNumber
               + ", status=" + status + ", parent="
               + (parent == null ? null : parent.getId()) + ", assignTo="
               + assignTo + ", service=" + service + ", product=" + product
               + ", deliverFrom=" + deliverFrom + ", deliverTo=" + deliverTo
               + "]";
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (assignTo != null) {
            assignTo = (Agency) assignTo.manageEntity(em, knownObjects);
        }
        if (deliverFrom != null) {
            deliverFrom = (Location) deliverFrom.manageEntity(em, knownObjects);
        }
        if (deliverTo != null) {
            deliverTo = (Location) deliverTo.manageEntity(em, knownObjects);
        }
        if (parent != null) {
            parent = (Job) parent.manageEntity(em, knownObjects);
        }
        if (product != null) {
            product = (Product) product.manageEntity(em, knownObjects);
        }
        if (requester != null) {
            requester = (Agency) requester.manageEntity(em, knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        if (status != null) {
            status = (StatusCode) status.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
