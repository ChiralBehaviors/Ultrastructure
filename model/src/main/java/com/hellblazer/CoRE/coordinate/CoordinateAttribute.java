/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.coordinate;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The attribute value of a location.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "coordinate_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_attribute_id_seq", sequenceName = "coordinate_attribute_id_seq")
public class CoordinateAttribute extends AttributeValue<Coordinate> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Coordinate
    @ManyToOne
    @JoinColumn(name = "coordinate")
    private Coordinate        coordinate;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    @Id
    @GeneratedValue(generator = "coordinate_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    public CoordinateAttribute() {
    }

    /**
     * @param attribute
     */
    public CoordinateAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, BigDecimal value,
                               Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, boolean value,
                               Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, int value,
                               Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public CoordinateAttribute(Attribute attribute, String value,
                               Resource updatedBy) {
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
    public CoordinateAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public CoordinateAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Product getProductValue() {
        return productValue;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<CoordinateAttribute, Coordinate> getRuleformAttribute() {
        return CoordinateAttribute_.coordinate;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Coordinate> getRuleformClass() {
        return Coordinate.class;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProductValue(Product product) {
        productValue = product;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (coordinate != null) coordinate = (Coordinate) coordinate.manageEntity(em, knownObjects);
		if (productValue != null) productValue = (Product) productValue.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}