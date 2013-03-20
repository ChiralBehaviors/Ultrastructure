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

import static com.hellblazer.CoRE.entity.EntityLocationNetwork.LOCATION_RULES;

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
import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_location_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "entity_location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_location_network_id_seq", sequenceName = "entity_location_network_id_seq")
@NamedQueries({ @NamedQuery(name = LOCATION_RULES, query = "select n from EntityLocationNetwork n where n.entity = :entity and n.coordinate.kind = :coordinateKind") })
public class EntityLocationNetwork extends Ruleform {
    private static final long  serialVersionUID = 1L;
    public static final String LOCATION_RULES   = "entityLocationNetwork.locationRules";

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "contextual_entity")
    private Entity             contextualEntity;                                         ;

    //bi-directional many-to-one association to Coordinate
    @ManyToOne
    @JoinColumn(name = "coordinate")
    private Coordinate         coordinate;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity")
    private Entity             entity;

    @Id
    @GeneratedValue(generator = "entity_location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource           resource;

    public EntityLocationNetwork() {
    }

    /**
     * @param id
     */
    public EntityLocationNetwork(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityLocationNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    public Entity getContextualEntity() {
        return contextualEntity;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setContextualEntity(Entity entity1) {
        contextualEntity = entity1;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setEntity(Entity entity2) {
        entity = entity2;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}