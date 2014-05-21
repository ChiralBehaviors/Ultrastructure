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
package com.chiralbehaviors.CoRE.coordinate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The attribute value of a location.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "coordinate_attribute", schema = "ruleform")
public class CoordinateAttribute extends AttributeValue<Coordinate> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Coordinate
    @ManyToOne
    @JoinColumn(name = "coordinate")
    private Coordinate        coordinate;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    public CoordinateAttribute() {
    }

    /**
     * @param updatedBy
     */
    public CoordinateAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     */
    public CoordinateAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, BigDecimal value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, boolean value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, String value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public CoordinateAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public CoordinateAttribute(UUID id) {
        super(id);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Product getProductValue() {
        return productValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<CoordinateAttribute, Coordinate> getRuleformAttribute() {
        return CoordinateAttribute_.coordinate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Coordinate> getRuleformClass() {
        return Coordinate.class;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setProductValue(Product product) {
        productValue = product;
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
        if (coordinate != null) {
            coordinate = (Coordinate) coordinate.manageEntity(em, knownObjects);
        }
        if (productValue != null) {
            productValue = (Product) productValue.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}