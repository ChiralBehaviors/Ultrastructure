/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.location;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.location.LocationNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.location.LocationNetwork.GET_NETWORKS;
import static com.chiralbehaviors.CoRE.location.LocationNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the location_network database table.
 *
 */
@NamedQueries({ @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from LocationNetwork n"),
                @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM LocationNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship"),
                @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM LocationNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship "
                                                         + "AND n.child = :child") })
@Entity
@Table(name = "location_network", schema = "ruleform")
public class LocationNetwork extends NetworkRuleform<Location> {
    public static final String GET_CHILDREN           = "locationNetwork"
                                                        + GET_CHILDREN_SUFFIX;
    public static final String GET_NETWORKS           = "locationNetwork"
                                                        + GET_NETWORKS_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS = "locationNetwork.getUsedRelationships";
    private static final long  serialVersionUID       = 1L;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class)
                 .getResultList();
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Location child;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Location parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise1")
    private LocationNetwork premise1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise2")
    private LocationNetwork premise2;

    public LocationNetwork() {
    }

    /**
     * @param updatedBy
     */
    public LocationNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Location parent, Relationship relationship,
                           Location child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param id
     */
    public LocationNetwork(UUID id) {
        super(id);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    @Override
    @JsonIgnore
    public Class<?> getAttributeClass() {
        return LocationNetworkAttribute.class;
    }

    @Override
    @JsonGetter
    public Location getChild() {
        return child;
    }

    @Override
    @JsonGetter
    public Location getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    @JsonGetter
    public LocationNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    @JsonGetter
    public LocationNetwork getPremise2() {
        return premise2;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    @Override
    public void setChild(Location child) {
        this.child = child;
    }

    @Override
    public void setParent(Location parent) {
        this.parent = parent;
    }
}
