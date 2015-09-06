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

import static com.chiralbehaviors.CoRE.job.MetaProtocol.FOR_JOB;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

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

    public static final String FOR_JOB = "metaprotocol.getForJob";

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "assign_to")
    private Relationship assignTo;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from")
    private Relationship deliverFrom;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to")
    private Relationship deliverTo;

    /**
     * The relationship that transforms the product
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Relationship product;

    /**
     * the relationship that transforms the quantity unit type
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "quantity_unit")
    private Relationship quantityUnit;

    /**
     * the relationship that transforms the requesting agency
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "requester")
    private Relationship requester;

    @Column(name = "sequence_number")
    private int sequenceNumber = 0;

    /**
     * The service factor for this rule
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product service;

    /**
     * the relationship that transforms the service
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service_type")
    private Relationship serviceType;

    /**
     * Indicates no further transformations should be applied
     */
    @Column(name = "stop_on_match")
    private Boolean stopOnMatch = false;

    public MetaProtocol() {
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

    /**
     * @return the productOrdered
     */
    public Relationship getProduct() {
        return product;
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
        return stopOnMatch;
    }

    /**
     * @param assignTo
     *            the assignTo to set
     */
    public void setAssignTo(Relationship assignTo) {
        this.assignTo = assignTo;
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

    /**
     * @param productOrdered
     *            the productOrdered to set
     */
    public void setProduct(Relationship productOrdered) {
        product = productOrdered;
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
        this.stopOnMatch = stopOnMatch;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MetaProtocol [requestingAgency=" + requester.getName()
               + ", service=" + service.getName() + ", serviceType="
               + serviceType.getName() + ", productOrdered=" + product.getName()
               + ", deliverFrom=" + deliverFrom.getName() + ", deliverTo="
               + deliverTo.getName() + ", stopOnMatch=" + stopOnMatch
               + ", sequenceNumber=" + sequenceNumber + "]";
    }
}
