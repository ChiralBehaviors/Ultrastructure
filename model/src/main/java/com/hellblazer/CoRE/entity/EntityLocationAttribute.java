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
package com.hellblazer.CoRE.entity;

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
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_location_attribute database table.
 * 
 */
@javax.persistence.Entity
@SequenceGenerator(schema = "ruleform", name = "entity_location_attribute_id_seq", sequenceName = "entity_location_attribute_id_seq")
@Table(name = "entity_location_attribute", schema = "ruleform")
public class EntityLocationAttribute extends AttributeValue<EntityLocation> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to EntityLocation
    @ManyToOne
    @JoinColumn(name = "entity_location")
    private EntityLocation    entityLocation;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity_value")
    private Entity            entityValue;

    @Id
    @GeneratedValue(generator = "entity_location_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    public EntityLocationAttribute() {
    }

    /**
     * @param attribute
     */
    public EntityLocationAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityLocationAttribute(Attribute attribute, BigDecimal value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityLocationAttribute(Attribute attribute, boolean value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityLocationAttribute(Attribute attribute, int value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public EntityLocationAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityLocationAttribute(Attribute attribute, String value,
                                   Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public EntityLocationAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public EntityLocationAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityLocationAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    public EntityLocation getEntityLocation() {
        return entityLocation;
    }

    public Entity getEntityValue() {
        return entityValue;
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
    public SingularAttribute<EntityLocationAttribute, EntityLocation> getRuleformAttribute() {
        return EntityLocationAttribute_.entityLocation;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<EntityLocation> getRuleformClass() {
        return EntityLocation.class;
    }

    public void setEntityLocation(EntityLocation entityLocation) {
        this.entityLocation = entityLocation;
    }

    public void setEntityValue(Entity entity) {
        entityValue = entity;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}