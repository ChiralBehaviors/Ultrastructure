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

import static com.chiralbehaviors.CoRE.event.MetaProtocol.FOR_JOB;

import java.util.Map;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The persistent class for the meta_protocol database table.
 * 
 */
@Entity
@Table(name = "meta_protocol", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "meta_protocol_id_seq", sequenceName = "meta_protocol_id_seq")
@NamedQueries({ @NamedQuery(name = FOR_JOB, query = "SELECT mp from MetaProtocol mp "
                                                    + "WHERE mp.service = :service "
                                                    + "ORDER BY mp.sequenceNumber") })
public class MetaProtocol extends Ruleform {

    public static final String FOR_JOB          = "metaprotocol.getForJob";

    private static final long  serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Relationship       deliverFrom;

    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Relationship       deliverTo;

    @Id
    @GeneratedValue(generator = "meta_protocol_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    /**
     * The relationship that transforms the product ordered
     */
    @ManyToOne
    @JoinColumn(name = "product_ordered")
    private Relationship       productOrdered;

    /**
     * the relationship that transforms the requesting agency
     */
    @ManyToOne
    @JoinColumn(name = "requesting_agency")
    private Relationship       requestingAgency;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber   = 1;

    /**
     * The service factor for this rule
     */
    @ManyToOne
    @JoinColumn(name = "service")
    private Product            service;

    /**
     * the relationship that transforms the service type
     */
    @ManyToOne
    @JoinColumn(name = "service_type")
    private Relationship       serviceType;

    /**
     * Indicates no further transformations should be applied
     */
    @Column(name = "stop_on_match")
    private Integer            stopOnMatch      = FALSE;

    public MetaProtocol() {
    }

    /**
     * @param updatedBy
     */
    public MetaProtocol(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public MetaProtocol(Long id) {
        super(id);
    }

    public MetaProtocol(Product service, int sequenceNumber,
                        Relationship productOrdered,
                        Relationship requestingAgency,
                        Relationship serviceType, Relationship deliverTo,
                        Relationship deliverFrom, Agency updatedBy) {
        super(updatedBy);
        setService(service);
        setSequenceNumber(sequenceNumber);
        setProductOrdered(productOrdered);
        setRequestingAgency(requestingAgency);
        setServiceType(serviceType);
        setDeliverTo(deliverTo);
        setDeliverFrom(deliverFrom);
    }

    public MetaProtocol(Product service, Relationship requestingAgency,
                        Relationship serviceType, Relationship deliverTo,
                        Relationship deliverFrom, Agency updatedBy) {
        super(updatedBy);
        setService(service);
        setRequestingAgency(requestingAgency);
        setServiceType(serviceType);
        setDeliverTo(deliverTo);
        setDeliverFrom(deliverFrom);
    }

    /**
     * @return the deliverFrom
     */
    public Relationship getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @return the deliverTo
     */
    public Relationship getDeliverTo() {
        return deliverTo;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return the productOrdered
     */
    public Relationship getProductOrdered() {
        return productOrdered;
    }

    /**
     * @return the requestingAgency
     */
    public Relationship getRequestingAgency() {
        return requestingAgency;
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
     * @return the serviceType
     */
    public Relationship getServiceType() {
        return serviceType;
    }

    /**
     * @return the stopOnMatch
     */
    public Boolean getStopOnMatch() {
        return toBoolean(stopOnMatch);
    }

    /**
     * @param deliverFrom
     *            the deliverFrom to set
     */
    public void setDeliverFrom(Relationship deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @param deliverTo
     *            the deliverTo to set
     */
    public void setDeliverTo(Relationship deliverTo) {
        this.deliverTo = deliverTo;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param productOrdered
     *            the productOrdered to set
     */
    public void setProductOrdered(Relationship productOrdered) {
        this.productOrdered = productOrdered;
    }

    /**
     * @param requestingAgency
     *            the requesting agency to set
     */
    public void setRequestingAgency(Relationship requestingAgency) {
        this.requestingAgency = requestingAgency;
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
     * @param serviceType
     *            the serviceType to set
     */
    public void setServiceType(Relationship serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param stopOnMatch
     *            the stopOnMatch to set
     */
    public void setStopOnMatch(Boolean stopOnMatch) {
        this.stopOnMatch = toInteger(stopOnMatch);
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
        if (deliverFrom != null) {
            deliverFrom = (Relationship) deliverFrom.manageEntity(em,
                                                                  knownObjects);
        }
        if (deliverTo != null) {
            deliverTo = (Relationship) deliverTo.manageEntity(em, knownObjects);
        }
        if (productOrdered != null) {
            productOrdered = (Relationship) productOrdered.manageEntity(em,
                                                                        knownObjects);
        }
        if (requestingAgency != null) {
            requestingAgency = (Relationship) requestingAgency.manageEntity(em,
                                                                            knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        if (serviceType != null) {
            serviceType = (Relationship) serviceType.manageEntity(em,
                                                                  knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}