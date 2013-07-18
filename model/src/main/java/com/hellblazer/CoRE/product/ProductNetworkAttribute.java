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
 * The attribute value for product attributes
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "product_network_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_attribute_id_seq", sequenceName = "product_network_attribute_id_seq")
public class ProductNetworkAttribute extends AttributeValue<ProductNetwork> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to ProductNetwork
    @ManyToOne
    @JoinColumn(name = "network_rule")
    private ProductNetwork    productNetwork;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    @Id
    @GeneratedValue(generator = "product_network_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    public ProductNetworkAttribute() {
    }

    /**
     * @param attribute
     */
    public ProductNetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, BigDecimal value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, boolean value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, int value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, String value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ProductNetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ProductNetworkAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductNetworkAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public ProductNetwork getProductNetwork() {
        return productNetwork;
    }

    public Product getProductValue() {
        return productValue;
    }

    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ProductNetworkAttribute, ProductNetwork> getRuleformAttribute() {
        return ProductNetworkAttribute_.productNetwork;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<ProductNetwork> getRuleformClass() {
        return ProductNetwork.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProductNetwork(ProductNetwork productNetwork) {
        this.productNetwork = productNetwork;
    }

    public void setProductValue(Product product) {
        productValue = product;
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
		if (productNetwork != null) productNetwork = (ProductNetwork) productNetwork.manageEntity(em, knownObjects);
		if (productValue != null) productValue = (Product) productValue.manageEntity(em, knownObjects);
		if (resource != null) resource = (Resource) resource.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}