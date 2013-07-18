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
package com.hellblazer.CoRE.location;

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
 * The persistent class for the context_attribute database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "context_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "context_attribute_id_seq", sequenceName = "context_attribute_id_seq")
public class ContextAttribute extends AttributeValue<LocationContext> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "context_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to LocationContext
    @ManyToOne
    @JoinColumn(name = "context")
    private LocationContext   locationContext;

    private Boolean           required;

    public ContextAttribute() {
    }

    /**
     * @param attribute
     */
    public ContextAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ContextAttribute(Attribute attribute, BigDecimal value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ContextAttribute(Attribute attribute, boolean value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ContextAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ContextAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ContextAttribute(Attribute attribute, String value,
                            Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ContextAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ContextAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ContextAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public LocationContext getLocationContext() {
        return locationContext;
    }

    public Boolean getRequired() {
        return required;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ContextAttribute, LocationContext> getRuleformAttribute() {
        return ContextAttribute_.locationContext;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<LocationContext> getRuleformClass() {
        return LocationContext.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocationContext(LocationContext locationContext) {
        this.locationContext = locationContext;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (locationContext != null) locationContext = (LocationContext) locationContext.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}