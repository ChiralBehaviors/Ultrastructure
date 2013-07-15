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
package com.hellblazer.CoRE.attribute;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
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
import com.hellblazer.CoRE.resource.Resource;

/**
 * An attribute value on an attribute
 * 
 */
@javax.persistence.Entity
@Table(name = "attribute_meta_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attribute_meta_attribute_id_seq", sequenceName = "attribute_meta_attribute_id_seq")
public class AttributeMetaAttribute extends AttributeValue<Attribute> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute_value")
    private Attribute         attributeValue;

    @Id
    @GeneratedValue(generator = "attribute_meta_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "meta_attribute")
    private Attribute         metaAttribute;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    public AttributeMetaAttribute() {
    }

    public AttributeMetaAttribute(Attribute attribute) {
        super(attribute);
    }

    public AttributeMetaAttribute(Attribute attribute, BigDecimal value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, boolean value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, int value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttribute(Attribute attribute, Resource updatedBy) {
        super(updatedBy);
        this.attribute = attribute;
    }

    public AttributeMetaAttribute(Attribute attribute, String value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public AttributeMetaAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    public Attribute getAttributeValue() {
        return attributeValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Attribute getMetaAttribute() {
        return metaAttribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<AttributeMetaAttribute, Attribute> getRuleformAttribute() {
        return AttributeMetaAttribute_.attribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Attribute> getRuleformClass() {
        return Attribute.class;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public void setAttribute(Attribute attribute1) {
        attribute = attribute1;
    }

    public void setAttributeValue(Attribute attribute2) {
        attributeValue = attribute2;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setMetaAttribute(Attribute attribute3) {
        metaAttribute = attribute3;
    }

    @Override
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		attribute.manageEntity(em, knownObjects);
		attributeValue.manageEntity(em, knownObjects);
		metaAttribute.manageEntity(em, knownObjects);
		
	}
}