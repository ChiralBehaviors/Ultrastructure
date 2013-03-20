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
package com.hellblazer.CoRE.resource;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The persistent class for the resource_location database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "resource_location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_location_id_seq", sequenceName = "resource_location_id_seq")
public class ResourceLocation extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "resource_location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location          location;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    public ResourceLocation() {
    }

    /**
     * @param id
     */
    public ResourceLocation(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ResourceLocation(Resource updatedBy) {
        super(updatedBy);
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