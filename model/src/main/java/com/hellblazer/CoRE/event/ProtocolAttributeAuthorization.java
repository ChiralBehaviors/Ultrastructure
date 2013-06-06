/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeAuthorization;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "protocol_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_authorization_id_seq", sequenceName = "protocol_attribute_authorization_id_seq")
public class ProtocolAttributeAuthorization extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Resource          assignTo;

    @ManyToOne
    @JoinColumn(name = "assign_to_classification")
    private Relationship      assignToClassification;

    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Location          deliverFrom;

    @ManyToOne
    @JoinColumn(name = "deliver_from_classification")
    private Relationship      deliverFromClassification;

    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Location          deliverTo;

    @ManyToOne
    @JoinColumn(name = "deliver_to_classification")
    private Relationship      deliverToClassification;

    @Id
    @GeneratedValue(generator = "protocol_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    @ManyToOne
    @JoinColumn(name = "product_classification")
    private Relationship      procuctClassification;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product           product;

    @ManyToOne
    @JoinColumn(name = "service")
    private Product           service;

    @ManyToOne
    @JoinColumn(name = "service_classification")
    private Relationship      serviceClassification;

    /**
     * 
     */
    public ProtocolAttributeAuthorization() {
        super();
    }

    /**
     * @param authorized
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Attribute authorized,
                                          Resource updatedBy) {
        super(authorized, updatedBy);
    }

    /**
     * @param id
     */
    public ProtocolAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    public Resource getAssignTo() {
        return assignTo;
    }

    public Relationship getAssignToClassification() {
        return assignToClassification;
    }

    public Location getDeliverFrom() {
        return deliverFrom;
    }

    public Relationship getDeliverFromClassification() {
        return deliverFromClassification;
    }

    public Location getDeliverTo() {
        return deliverTo;
    }

    public Relationship getDeliverToClassification() {
        return deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Relationship getProcuctClassification() {
        return procuctClassification;
    }

    public Product getProduct() {
        return product;
    }

    public Product getService() {
        return service;
    }

    public Relationship getServiceClassification() {
        return serviceClassification;
    }

    public void setAssignTo(Resource assignTo) {
        this.assignTo = assignTo;
    }

    public void setAssignToClassification(Relationship assignToClassification) {
        this.assignToClassification = assignToClassification;
    }

    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    public void setDeliverFromClassification(Relationship deliverFromClassification) {
        this.deliverFromClassification = deliverFromClassification;
    }

    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    public void setDeliverToClassification(Relationship deliverToClassification) {
        this.deliverToClassification = deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProcuctClassification(Relationship procuctClassification) {
        this.procuctClassification = procuctClassification;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public void setServiceClassification(Relationship serviceClassification) {
        this.serviceClassification = serviceClassification;
    }

}
