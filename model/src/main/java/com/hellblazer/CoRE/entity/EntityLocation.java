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

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_location database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "entity_location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_location_id_seq", sequenceName = "entity_location_id_seq")
public class EntityLocation extends Ruleform implements
        Attributable<EntityLocationAttribute> {
    private static final long            serialVersionUID = 1L;

    //bi-directional many-to-one association to EntityLocationAttribute
    @OneToMany(mappedBy = "entityLocation")
    @JsonIgnore
    private Set<EntityLocationAttribute> attributes;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity")
    private Entity                       entity;

    @Id
    @GeneratedValue(generator = "entity_location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                         id;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location                     location;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship                 relationship;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource                     resource;

    public EntityLocation() {
    }

    /**
     * @param id
     */
    public EntityLocation(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityLocation(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Set<EntityLocationAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<EntityLocationAttribute> getAttributeType() {
        return EntityLocationAttribute.class;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void setAttributes(Set<EntityLocationAttribute> entityLocationAttributes) {
        attributes = entityLocationAttributes;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}