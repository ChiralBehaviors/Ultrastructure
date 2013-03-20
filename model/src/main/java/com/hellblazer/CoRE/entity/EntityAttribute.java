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

import static com.hellblazer.CoRE.entity.EntityAttribute.FIND_ATTRIBUTE_VALUE_FROM_RESOURCE;

import java.math.BigDecimal;

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

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_attribute database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = FIND_ATTRIBUTE_VALUE_FROM_RESOURCE, query = "SELECT ea FROM EntityAttribute ea"
                                                                               + "   AND ea.entity = :entity "
                                                                               + "   AND ea.attribute = :attribute") })
@javax.persistence.Entity
@Table(name = "entity_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_attribute_id_seq", sequenceName = "entity_attribute_id_seq")
public class EntityAttribute extends AttributeValue<Entity> {
    public final static String FIND_ATTRIBUTE_VALUE_FROM_RESOURCE = "entityAttribute.findAttributeValueFromResource";
    private static final long  serialVersionUID                   = 1L;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity")
    private Entity             entity;

    @Id
    @GeneratedValue(generator = "entity_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    public EntityAttribute() {
    }

    /**
     * @param attribute
     */
    public EntityAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityAttribute(Attribute attribute, BigDecimal value,
                           Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityAttribute(Attribute attribute, boolean value,
                           Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public EntityAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityAttribute(Attribute attribute, String value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public EntityAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public EntityAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<EntityAttribute, Entity> getRuleformAttribute() {
        return EntityAttribute_.entity;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Entity> getRuleformClass() {
        return Entity.class;
    }

    public void setEntity(Entity entity2) {
        entity = entity2;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}