/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
import com.chiralbehaviors.CoRE.attribute.Attribute;
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
     * The attribute for the agency assigned to this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "assign_to_attribute")
    private Attribute         assignToAttribute;

    /**
     * The location where the product will be delivered from
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from")
    private Location          deliverFrom;
    /**
     * The the attribute on the location where the product will be delivered
     * from
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_from_attribute")
    private Attribute         deliverFromAttribute;
    /**
     * The location to deliver the product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to")
    private Location          deliverTo;

    /**
     * The attribute on the location to deliver the product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "deliver_to_attribute")
    private Attribute         deliverToAttribute;

    /**
     * The product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Product           product;

    /**
     * The attribute on the product of this job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product_attribute")
    private Attribute         productAttribute;

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
     * The attribute of the consumer of this job's product
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "requester_attribute")
    private Attribute         requesterAttribute;

    /**
     * The service this job is performing
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product           service;

    /**
     * The attribute on the service this job is performing
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service_attribute")
    private Attribute         serviceAttribute;

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
        setAssignToAttribute(protocol.getAssignToAttribute());
        setDeliverTo(protocol.getDeliverTo());
        setDeliverToAttribute(protocol.getDeliverToAttribute());
        setDeliverFrom(protocol.getDeliverFrom());
        setDeliverFromAttribute(protocol.getDeliverFromAttribute());
        setProduct(protocol.getProduct());
        setProductAttribute(protocol.getProductAttribute());
        setRequester(protocol.getRequester());
        setRequesterAttribute(protocol.getRequesterAttribute());
        setService(protocol.getService());
        setServiceAttribute(protocol.getServiceAttribute());
        setQuantityUnit(protocol.getQuantityUnit());
        setQuantity(protocol.getQuantity());
    }

    public Agency getAssignTo() {
        return assignTo;
    }

    /**
     * @return the assignToAttribute
     */
    public Attribute getAssignToAttribute() {
        return assignToAttribute;
    }

    /**
     * @return the deliverFrom
     */
    public Location getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @return the deliverFromAttribute
     */
    public Attribute getDeliverFromAttribute() {
        return deliverFromAttribute;
    }

    /**
     * @return the deliverTo
     */
    public Location getDeliverTo() {
        return deliverTo;
    }

    /**
     * @return the deliverToAttribute
     */
    public Attribute getDeliverToAttribute() {
        return deliverToAttribute;
    }

    /**
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @return the productAttribute
     */
    public Attribute getProductAttribute() {
        return productAttribute;
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
     * @return the requesterAttribute
     */
    public Attribute getRequesterAttribute() {
        return requesterAttribute;
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
    public Attribute getServiceAttribute() {
        return serviceAttribute;
    }

    @JsonIgnore
    public String getToString() {
        return String.format("requester=%s, assignTo=%s, service=%s, product=%s, deliverTo=%s, deliverFrom=%s, requesterAttribute=%s, assignToAttribute=%s, serviceAttribute=%s, productAttribute=%s, deliverToAttribute=%s, deliverFromAttribute=%s, quantity=%s, quantityUnit=%s",
                             getRequester().getName(), getAssignTo().getName(),
                             getService().getName(), getProduct().getName(),
                             getDeliverTo().getName(),
                             getDeliverFrom().getName(),
                             getRequesterAttribute().getName(),
                             getAssignToAttribute().getName(),
                             getServiceAttribute().getName(),
                             getProductAttribute().getName(),
                             getDeliverToAttribute().getName(),
                             getDeliverFromAttribute().getName(), quantity,
                             getQuantityUnit().getName());
    }

    public void setAssignTo(Agency agency2) {
        assignTo = agency2;
    }

    /**
     * @param assignToAttribute
     *            the assignToAttribute to set
     */
    public void setAssignToAttribute(Attribute assignToAttribute) {
        this.assignToAttribute = assignToAttribute;
    }

    /**
     * @param deliverFrom
     *            the deliverFrom to set
     */
    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @param deliverFromAttribute
     *            the deliverFromAttribute to set
     */
    public void setDeliverFromAttribute(Attribute deliverFromAttribute) {
        this.deliverFromAttribute = deliverFromAttribute;
    }

    /**
     * @param deliverTo
     *            the deliverTo to set
     */
    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    /**
     * @param deliverToAttribute
     *            the deliverToAttribute to set
     */
    public void setDeliverToAttribute(Attribute deliverToAttribute) {
        this.deliverToAttribute = deliverToAttribute;
    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @param productAttribute
     *            the productAttribute to set
     */
    public void setProductAttribute(Attribute productAttribute) {
        this.productAttribute = productAttribute;
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
     * @param requesterAttribute
     *            the requesterAttribute to set
     */
    public void setRequesterAttribute(Attribute requesterAttribute) {
        this.requesterAttribute = requesterAttribute;
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
    public void setServiceAttribute(Attribute serviceAttribute) {
        this.serviceAttribute = serviceAttribute;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AbstractProtocol [requester=%s, assignTo=%s, service=%s, product=%s, deliverTo=%s, deliverFrom=%s, requesterAttribute=%s, assignToAttribute=%s, serviceAttribute=%s, productAttribute=%s, deliverToAttribute=%s, deliverFromAttribute=%s, quantity=%s, quantityUnit=%s]",
                             getRequester().getName(), getAssignTo().getName(),
                             getService().getName(), getProduct().getName(),
                             getDeliverTo().getName(),
                             getDeliverFrom().getName(),
                             getRequesterAttribute().getName(),
                             getAssignToAttribute().getName(),
                             getServiceAttribute().getName(),
                             getProductAttribute().getName(),
                             getDeliverToAttribute().getName(),
                             getDeliverFromAttribute().getName(), quantity,
                             getQuantityUnit().getName());
    }
}