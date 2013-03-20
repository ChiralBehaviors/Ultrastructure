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
 * The persistent class for the entity_network_attribute database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "entity_network_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_network_attribute_id_seq", sequenceName = "entity_network_attribute_id_seq")
public class EntityNetworkAttribute extends AttributeValue<EntityNetwork> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to EntityNetwork
    @ManyToOne
    @JoinColumn(name = "network_rule")
    private EntityNetwork     entityNetwork;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity_value")
    private Entity            entityValue;

    @Id
    @GeneratedValue(generator = "entity_network_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    public EntityNetworkAttribute() {
    }

    /**
     * @param attribute
     */
    public EntityNetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityNetworkAttribute(Attribute attribute, BigDecimal value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityNetworkAttribute(Attribute attribute, boolean value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityNetworkAttribute(Attribute attribute, int value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public EntityNetworkAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public EntityNetworkAttribute(Attribute attribute, String value,
                                  Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public EntityNetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public EntityNetworkAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityNetworkAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    public EntityNetwork getEntityNetwork() {
        return entityNetwork;
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
    public SingularAttribute<EntityNetworkAttribute, EntityNetwork> getRuleformAttribute() {
        return EntityNetworkAttribute_.entityNetwork;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<EntityNetwork> getRuleformClass() {
        return EntityNetwork.class;
    }

    public void setEntityNetwork(EntityNetwork entityNetwork) {
        this.entityNetwork = entityNetwork;
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