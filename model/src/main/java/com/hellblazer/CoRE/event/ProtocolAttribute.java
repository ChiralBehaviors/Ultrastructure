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

import java.math.BigDecimal;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the protocol_attribute database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "protocol_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_id_seq", sequenceName = "protocol_attribute_id_seq")
public class ProtocolAttribute extends AttributeValue<Protocol> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "protocol_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Protocol
    @ManyToOne
    @JoinColumn(name = "protocol")
    private Protocol          protocol;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location_value")
    private Location          location_value;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           product_value;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource_value")
    private Product           resource_value;

    public ProtocolAttribute() {
    }

    /**
     * @param attribute
     */
    public ProtocolAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProtocolAttribute(Attribute attribute, BigDecimal value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProtocolAttribute(Attribute attribute, boolean value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProtocolAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ProtocolAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProtocolAttribute(Attribute attribute, String value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ProtocolAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ProtocolAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProtocolAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public void copyInto(JobAttribute clone) {
        super.copyInto(clone);
        clone.setLocationValue(getLocationValue());
        clone.setResourceValue(getResourceValue());
        clone.setProductValue(getProductValue());
    }

    /**
     * @return a new job attribute cloned from the receiver
     */
    public JobAttribute createJobAttribute() {
        JobAttribute clone = new JobAttribute();
        clone.setAttribute(getAttribute());
        copyInto(clone);
        return clone;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Location getLocationValue() {
        return location_value;
    }

    public Product getProductValue() {
        return product_value;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Product getResourceValue() {
        return resource_value;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Protocol>, Protocol> getRuleformAttribute() {
        return ProtocolAttribute_.protocol;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Protocol> getRuleformClass() {
        return Protocol.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocationValue(Location location_value) {
        this.location_value = location_value;
    }

    public void setNumericValue(double value) {
        setNumericValue(BigDecimal.valueOf(value));
    }

    public void setNumericValue(float value) {
        setNumericValue(BigDecimal.valueOf(value));
    }

    public void setNumericValue(int value) {
        setNumericValue(BigDecimal.valueOf(value));
    }

    public void setNumericValue(long value) {
        setNumericValue(BigDecimal.valueOf(value));
    }

    public void setProductValue(Product product_value) {
        this.product_value = product_value;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setResourceValue(Product resource_value) {
        this.resource_value = resource_value;
    }
}