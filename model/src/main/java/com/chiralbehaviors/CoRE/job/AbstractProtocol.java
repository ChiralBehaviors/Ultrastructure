/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.RuleformIdGenerator;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIdentityInfo(generator = RuleformIdGenerator.class, property = "@id")
@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY)
public abstract class AbstractProtocol extends Ruleform {

    private static final long serialVersionUID = 1L;

    /**
     * The agency assigned to this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "assign_to")
    private Agency            assignTo;

    /**
     * The location where the product will be delivered from
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from")
    private Location          deliverFrom;
    /**
     * The location to deliver the product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to")
    private Location          deliverTo;

    /**
     * The product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Product           product;

    @Column(name = "quantity")
    private BigDecimal        quantity         = BigDecimal.ZERO;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "quantity_unit")
    private Unit              quantityUnit;

    /**
     * The consumer of this job's product
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "requester")
    private Agency            requester;

    /**
     * The service this job is performing
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product           service;

    public AbstractProtocol() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AbstractProtocol(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param notes
     */
    public AbstractProtocol(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public AbstractProtocol(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public AbstractProtocol(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public AbstractProtocol(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public void copyFrom(AbstractProtocol protocol) {
        setAssignTo(protocol.getAssignTo());
        setDeliverTo(protocol.getDeliverTo());
        setDeliverFrom(protocol.getDeliverFrom());
        setProduct(protocol.getProduct());
        setRequester(protocol.getRequester());
        setService(protocol.getService());
        setQuantityUnit(protocol.getQuantityUnit());
        setQuantity(protocol.getQuantity());
    }

    public Agency getAssignTo() {
        return assignTo;
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

    /**
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Unit getQuantityUnit() {
        return quantityUnit;
    }

    /**
     * @return the requester
     */
    public Agency getRequester() {
        return requester;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    @JsonIgnore
    public String getToString() {
        return String.format("requester=%s, assignTo=%s, service=%s, product=%s, deliverTo=%s, deliverFrom=%s quantity=%s, quantityUnit=%s",
                             getRequester().getName(), getAssignTo().getName(),
                             getService().getName(), getProduct().getName(),
                             getDeliverTo().getName(),
                             getDeliverFrom().getName(), quantity,
                             getQuantityUnit().getName());
    }

    public void setAssignTo(Agency agency2) {
        assignTo = agency2;
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

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException();
        }
        this.quantity = quantity;
    }

    public void setQuantityUnit(Unit quantityUnit) {
        if (quantityUnit == null) {
            throw new IllegalArgumentException();
        }
        this.quantityUnit = quantityUnit;
    }

    /**
     * @param requester
     *            the requester to set
     */
    public void setRequester(Agency requester) {
        this.requester = requester;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Product service) {
        this.service = service;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AbstractProtocol [requester=%s, assignTo=%s, service=%s, product=%s, deliverTo=%s, deliverFrom=%s, quantity=%s, quantityUnit=%s]",
                             getRequester().getName(), getAssignTo().getName(),
                             getService().getName(), getProduct().getName(),
                             getDeliverTo().getName(),
                             getDeliverFrom().getName(), quantity,
                             getQuantityUnit().getName());
    }
}