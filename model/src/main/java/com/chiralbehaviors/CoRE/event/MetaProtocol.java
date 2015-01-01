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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the meta_protocol database table.
 *
 */
@Entity
@Table(name = "meta_protocol", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = FOR_JOB, query = "SELECT mp from MetaProtocol mp "
                                                    + "WHERE mp.service = :service "
                                                    + "ORDER BY mp.sequenceNumber") })
public class MetaProtocol extends Ruleform {

    public static final String FOR_JOB          = "metaprotocol.getForJob";

    private static final long  serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "assign_to")
    private Relationship       assignTo;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "assign_to_attribute")
    private Relationship       assignToAttribute;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from")
    private Relationship       deliverFrom;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from_attribute")
    private Relationship       deliverFromAttribute;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to")
    private Relationship       deliverTo;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to_attribute")
    private Relationship       deliverToAttribute;

    /**
     * The relationship that transforms the product
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Relationship       product;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product_attribute")
    private Relationship       productAttribute;

    /**
     * the relationship that transforms the quantity unit type
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "quantity_unit")
    private Relationship       quantityUnit;

    /**
     * the relationship that transforms the requesting agency
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "requester")
    private Relationship       requester;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "requester_attribute")
    private Relationship       requesterAttribute;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber   = 1;

    /**
     * The service factor for this rule
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product            service;

    /**
     * the relationship that transforms the service type
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service_attribute")
    private Relationship       serviceAttribute;

    /**
     * the relationship that transforms the service
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service_type")
    private Relationship       serviceType;

    /**
     * Indicates no further transformations should be applied
     */
    @Column(name = "stop_on_match")
    private Integer            stopOnMatch      = FALSE;

    public MetaProtocol() {
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, MetaProtocol> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.metaProtocol;
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public MetaProtocol(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public MetaProtocol(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public MetaProtocol(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    /**
     * @return the assignTo
     */
    public Relationship getAssignTo() {
        return assignTo;
    }

    /**
     * @return the assignToAttribute
     */
    public Relationship getAssignToAttribute() {
        return assignToAttribute;
    }

    /**
     * @return the deliverFrom
     */
    public Relationship getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @return the deliverFromAttribute
     */
    public Relationship getDeliverFromAttribute() {
        return deliverFromAttribute;
    }

    /**
     * @return the deliverTo
     */
    public Relationship getDeliverTo() {
        return deliverTo;
    }

    /**
     * @return the deliverToAttribute
     */
    public Relationship getDeliverToAttribute() {
        return deliverToAttribute;
    }

    /**
     * @return the productOrdered
     */
    public Relationship getProduct() {
        return product;
    }

    /**
     * @return the productOrderedAttribute
     */
    public Relationship getProductAttribute() {
        return productAttribute;
    }

    public Relationship getQuantityUnit() {
        return quantityUnit;
    }

    /**
     * @return the requestingAgency
     */
    public Relationship getRequester() {
        return requester;
    }

    /**
     * @return the requestingAgencyAttribute
     */
    public Relationship getRequesterAttribute() {
        return requesterAttribute;
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
     * @return the serviceAttribute
     */
    public Relationship getServiceAttribute() {
        return serviceAttribute;
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
     * @param assignTo
     *            the assignTo to set
     */
    public void setAssignTo(Relationship assignTo) {
        this.assignTo = assignTo;
    }

    /**
     * @param assignToAttribute
     *            the assignToAttribute to set
     */
    public void setAssignToAttribute(Relationship assignToAttribute) {
        this.assignToAttribute = assignToAttribute;
    }

    /**
     * @param deliverFrom
     *            the deliverFrom to set
     */
    public void setDeliverFrom(Relationship deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @param deliverFromAttribute
     *            the deliverFromAttribute to set
     */
    public void setDeliverFromAttribute(Relationship deliverFromAttribute) {
        this.deliverFromAttribute = deliverFromAttribute;
    }

    /**
     * @param deliverTo
     *            the deliverTo to set
     */
    public void setDeliverTo(Relationship deliverTo) {
        this.deliverTo = deliverTo;
    }

    /**
     * @param deliverToAttribute
     *            the deliverToAttribute to set
     */
    public void setDeliverToAttribute(Relationship deliverToAttribute) {
        this.deliverToAttribute = deliverToAttribute;
    }

    /**
     * @param productOrdered
     *            the productOrdered to set
     */
    public void setProduct(Relationship productOrdered) {
        product = productOrdered;
    }

    /**
     * @param productOrderedAttribute
     *            the productOrderedAttribute to set
     */
    public void setProductAttribute(Relationship productOrderedAttribute) {
        productAttribute = productOrderedAttribute;
    }

    public void setQuantityUnit(Relationship quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    /**
     * @param requestingAgency
     *            the requesting agency to set
     */
    public void setRequester(Relationship requestingAgency) {
        requester = requestingAgency;
    }

    /**
     * @param requestingAgencyAttribute
     *            the requestingAgencyAttribute to set
     */
    public void setRequesterAttribute(Relationship requestingAgencyAttribute) {
        requesterAttribute = requestingAgencyAttribute;
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
     * @param serviceAttribute
     *            the serviceAttribute to set
     */
    public void setServiceAttribute(Relationship serviceAttribute) {
        this.serviceAttribute = serviceAttribute;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MetaProtocol [requestingAgency=" + requester.getName()
               + ", service=" + service.getName() + ", serviceType="
               + serviceType.getName() + ", productOrdered="
               + product.getName() + ", deliverFrom=" + deliverFrom.getName()
               + ", deliverTo=" + deliverTo.getName() + ", stopOnMatch="
               + stopOnMatch + ", sequenceNumber=" + sequenceNumber + "]";
    }
}
