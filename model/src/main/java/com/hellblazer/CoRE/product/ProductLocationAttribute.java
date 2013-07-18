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
package com.hellblazer.CoRE.product;

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
import com.hellblazer.CoRE.resource.Resource;

/**
 * The attribute value for product location attributes
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@SequenceGenerator(schema = "ruleform", name = "product_location_attribute_id_seq", sequenceName = "product_location_attribute_id_seq")
@Table(name = "product_location_attribute", schema = "ruleform")
public class ProductLocationAttribute extends AttributeValue<ProductLocation> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to ProductLocation
    @ManyToOne
    @JoinColumn(name = "product_location")
    private ProductLocation   productLocation;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    @Id
    @GeneratedValue(generator = "product_location_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    public ProductLocationAttribute() {
    }

    /**
     * @param attribute
     */
    public ProductLocationAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductLocationAttribute(Attribute attribute, BigDecimal value,
                                    Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductLocationAttribute(Attribute attribute, boolean value,
                                    Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductLocationAttribute(Attribute attribute, int value,
                                    Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ProductLocationAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductLocationAttribute(Attribute attribute, String value,
                                    Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ProductLocationAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ProductLocationAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductLocationAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    public ProductLocation getEntityLocation() {
        return productLocation;
    }

    public Product getEntityValue() {
        return productValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ProductLocationAttribute, ProductLocation> getRuleformAttribute() {
        return ProductLocationAttribute_.productLocation;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<ProductLocation> getRuleformClass() {
        return ProductLocation.class;
    }

    public void setEntityLocation(ProductLocation productLocation) {
        this.productLocation = productLocation;
    }

    public void setEntityValue(Product product) {
        productValue = product;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (productLocation != null) productLocation = (ProductLocation) productLocation.manageEntity(em, knownObjects);
		if (productValue != null) productValue = (Product) productValue.manageEntity(em, knownObjects);
		if (resource != null) resource = (Resource) resource.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}