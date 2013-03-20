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

import static com.hellblazer.CoRE.location.LocationNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;

import javax.persistence.CascadeType;
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

import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the location_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_network_id_seq", sequenceName = "location_network_id_seq")
@NamedQueries({ @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from LocationNetwork n") })
public class LocationNetwork extends NetworkRuleform<Location> {
    private static final long  serialVersionUID       = 1L;
    public static final String GET_USED_RELATIONSHIPS = "locationNetwork.getUsedRelationships";

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Location child;

    @Id
    @GeneratedValue(generator = "location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long     id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Location parent;

    public LocationNetwork() {
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Location parent, Relationship relationship,
                           Location child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public LocationNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public LocationNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Location getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Location getParent() {
        return parent;
    }

    @Override
    public void setChild(Location child) {
        this.child = child;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Location parent) {
        this.parent = parent;
    }
}