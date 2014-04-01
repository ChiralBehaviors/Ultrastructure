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
package com.chiralbehaviors.CoRE.event.status;

import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.ENSURE_VALID_SERVICE_STATUS;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_CHILD_STATUS_CODES_SERVICE;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_PARENT_STATUS_CODES_SERVICE;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing.IS_VALID_NEXT_STATUS;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the status_code_sequencing database table.
 * 
 */
@NamedQueries({
               @NamedQuery(name = ENSURE_VALID_SERVICE_STATUS, query = "SELECT COUNT(scs.id) "
                                                                       + "FROM StatusCodeSequencing AS scs "
                                                                       + "WHERE scs.service = :service "
                                                                       + "  AND (scs.parentCode = :code "
                                                                       + "       OR scs.childCode = :code)"),
               @NamedQuery(name = IS_VALID_NEXT_STATUS, query = "SELECT COUNT(scs.id) "
                                                                + "FROM StatusCodeSequencing AS scs "
                                                                + "WHERE scs.service = :service "
                                                                + "  AND scs.parentCode = :parentCode "
                                                                + "  AND scs.childCode = :childCode"),
               @NamedQuery(name = GET_PARENT_STATUS_CODES_SERVICE, query = "SELECT DISTINCT(scs.parentCode) "
                                                                           + " FROM StatusCodeSequencing scs "
                                                                           + " WHERE scs.service = :service"),
               @NamedQuery(name = GET_CHILD_STATUS_CODES_SERVICE, query = "SELECT DISTINCT(scs.childCode) "
                                                                          + " FROM StatusCodeSequencing scs "
                                                                          + " WHERE scs.service = :service"),
               @NamedQuery(name = GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                                    + " WHERE scs.service = :service"
                                                                                    + "   AND scs.childCode = :statusCode"),
               @NamedQuery(name = GET_PARENT_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                             + " WHERE scs.parentCode = :statusCode"),
               @NamedQuery(name = GET_CHILD_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                            + " WHERE scs.childCode = :statusCode"),
               @NamedQuery(name = GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                                     + " WHERE scs.service = :service"
                                                                                     + "   AND scs.parentCode = :statusCode"),
               @NamedQuery(name = GET_ALL_STATUS_CODE_SEQUENCING, query = "SELECT scs "
                                                                          + " FROM StatusCodeSequencing scs "
                                                                          + " WHERE scs.service = :service") })
@Entity
@Table(name = "status_code_sequencing", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_sequencing_id_seq", sequenceName = "status_code_sequencing_id_seq")
public class StatusCodeSequencing extends Ruleform {
    public static final String  ENSURE_VALID_SERVICE_STATUS               = "statusCodeSequencing.ensureValidServiceAndStatus";
    public static final String  GET_ALL_STATUS_CODE_SEQUENCING            = "statusCodeSequencing.getAllStatusCodeSequencing";
    public static final String  GET_CHILD_STATUS_CODE_SEQUENCING          = "statusCodeSequencing.getChildStatusCodeSequencing";
    public static final String  GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE  = "statusCodeSequencing.getChildStatusCodeSequencingService";
    public static final String  GET_CHILD_STATUS_CODES_SERVICE            = "statusCodeSequencing.getChildStatusCodes";
    public static final String  GET_PARENT_STATUS_CODE_SEQUENCING         = "statusCodeSequencing.getParentStatusCodeSequencing";
    public static final String  GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE = "statusCodeSequencing.getParentStatusCodeSequencingService";
    public static final String  GET_PARENT_STATUS_CODES_SERVICE           = "statusCodeSequencing.getParentStatusCodes";
    public static final String  IS_VALID_NEXT_STATUS                      = "statusCodeSequencing.isValidNextStatus";

    private static final long   serialVersionUID                          = 1L;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "child_code")
    private StatusCode          childCode;

    @Id
    @GeneratedValue(generator = "status_code_sequencing_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                id;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "parent_code")
    private StatusCode          parentCode;

    @Column(name = "sequence_number")
    private Integer             sequenceNumber                            = 1;

    // bi-directional many-to-one association to Event
    @ManyToOne(optional = false)
    @JoinColumn(name = "service")
    private Product             service;

    // bi-directional many-to-one association to StatusCode
    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private Set<ProductNetwork> statusCodeByChild;

    // bi-directional many-to-one association to StatusCode
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<ProductNetwork> statusCodeByParent;

    public StatusCodeSequencing() {
    }

    /**
     * @param updatedBy
     */
    public StatusCodeSequencing(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public StatusCodeSequencing(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public StatusCodeSequencing(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public StatusCodeSequencing(Product service, StatusCode parent,
                                StatusCode child, Agency updatedBy) {
        super(updatedBy);
        this.service = service;
        parentCode = parent;
        childCode = child;
    }

    public StatusCodeSequencing(Product service, StatusCode parent,
                                StatusCode child, int sequenceNumber,
                                Agency updatedBy) {
        this(service, parent, child, updatedBy);
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param notes
     */
    public StatusCodeSequencing(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public StatusCodeSequencing(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    public StatusCode getChildCode() {
        return childCode;
    }

    @Override
    public Long getId() {
        return id;
    }

    public StatusCode getParentCode() {
        return parentCode;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    /**
     * @return the statusCodeByChild
     */
    public Set<ProductNetwork> getStatusCodeByChild() {
        return statusCodeByChild;
    }

    /**
     * @return the statusCodeByParent
     */
    public Set<ProductNetwork> getStatusCodeByParent() {
        return statusCodeByParent;
    }

    public void setChildCode(StatusCode statusCode1) {
        childCode = statusCode1;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setParentCode(StatusCode statusCode2) {
        parentCode = statusCode2;
    }

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

    /**
     * @param statusCodeByChild
     *            the statusCodeByChild to set
     */
    public void setStatusCodeByChild(Set<ProductNetwork> statusCodeByChild) {
        this.statusCodeByChild = statusCodeByChild;
    }

    /**
     * @param statusCodeByParent
     *            the statusCodeByParent to set
     */
    public void setStatusCodeByParent(Set<ProductNetwork> statusCodeByParent) {
        this.statusCodeByParent = statusCodeByParent;
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
        if (childCode != null) {
            childCode = (StatusCode) childCode.manageEntity(em, knownObjects);
        }
        if (parentCode != null) {
            parentCode = (StatusCode) parentCode.manageEntity(em, knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
