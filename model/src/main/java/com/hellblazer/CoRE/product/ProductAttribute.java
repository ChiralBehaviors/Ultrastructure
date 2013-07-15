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

import static com.hellblazer.CoRE.product.ProductAttribute.FIND_ATTRIBUTE_VALUE_FROM_RESOURCE;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The attribute values of product attributes
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({ @NamedQuery(name = FIND_ATTRIBUTE_VALUE_FROM_RESOURCE, query = "SELECT ea FROM ProductAttribute ea"
                                                                               + "   WHERE ea.product = :product "
                                                                               + "   AND ea.attribute = :attribute") })
@javax.persistence.Entity
@Table(name = "product_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_attribute_id_seq", sequenceName = "product_attribute_id_seq")
public class ProductAttribute extends AttributeValue<Product> {
    public final static String FIND_ATTRIBUTE_VALUE_FROM_RESOURCE = "productAttribute.findAttributeValueFromResource";
    private static final long  serialVersionUID                   = 1L;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    @Id
    @GeneratedValue(generator = "product_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    public ProductAttribute() {
    }

    /**
     * @param attribute
     */
    public ProductAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductAttribute(Attribute attribute, BigDecimal value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductAttribute(Attribute attribute, boolean value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ProductAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductAttribute(Attribute attribute, String value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ProductAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ProductAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ProductAttribute, Product> getRuleformAttribute() {
        return ProductAttribute_.product;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Product> getRuleformClass() {
        return Product.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product2) {
        product = product2;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (product != null) product = (Product) product.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}