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

import static com.hellblazer.CoRE.attribute.Transformation.GET;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the transformation database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT t FROM Transformation t "
                                                + "WHERE t.service = :service "
                                                + "AND t.entity = :entity "
                                                + "AND t.resource = :resource "
                                                + "ORDER BY t.sequenceNumber") })
@javax.persistence.Entity
@Table(name = "transformation", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transformation_id_seq", sequenceName = "transformation_id_seq")
public class Transformation extends Ruleform implements Serializable {
    public final static String GET              = "transformation.get";
    private static final long  serialVersionUID = 1L;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Resource           assignTo;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity")
    private Entity             entity;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "entity_attribute_resource")
    private Resource           entityAttributeResource;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity_key")
    private Entity             entityKey;

    @Id
    @GeneratedValue(generator = "transformation_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship_key")
    private Relationship       relationshipKey;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource           resource;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource_key")
    private Resource           resourceKey;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    //bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "service")
    private Entity             service;

    public Transformation() {
    }

    public Resource getAssignTo() {
        return assignTo;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Entity getEntity() {
        return entity;
    }

    public Resource getEntityAttributeResource() {
        return entityAttributeResource;
    }

    public Entity getEntityKey() {
        return entityKey;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getRelationshipKey() {
        return relationshipKey;
    }

    public Resource getResource() {
        return resource;
    }

    public Resource getResourceKey() {
        return resourceKey;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the service
     */
    public Entity getService() {
        return service;
    }

    public void setAssignTo(Resource resource1) {
        assignTo = resource1;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setEntityAttributeResource(Resource resource3) {
        entityAttributeResource = resource3;
    }

    public void setEntityKey(Entity entityKey) {
        this.entityKey = entityKey;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationshipKey(Relationship relationship) {
        relationshipKey = relationship;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }

    public void setResourceKey(Resource resource4) {
        resourceKey = resource4;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Entity service) {
        this.service = service;
    }

}