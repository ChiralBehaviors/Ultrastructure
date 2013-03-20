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

import static com.hellblazer.CoRE.resource.ResourceNetwork.GET_CHILD;
import static com.hellblazer.CoRE.resource.ResourceNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.resource.ResourceNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

import java.util.List;

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

/**
 * The persistent class for the resource_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "resource_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_network_id_seq", sequenceName = "resource_network_id_seq")
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from ResourceNetwork AS n "
                                                                            + "where n.parent = :resource "
                                                                            + "and n.distance = 1 "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ResourceNetwork n"),
               @NamedQuery(name = GET_CHILD, query = "SELECT rn.child "
                                                     + "FROM ResourceNetwork rn "
                                                     + "WHERE rn.parent = :parent "
                                                     + "AND rn.relationship = :relationship") })
public class ResourceNetwork extends NetworkRuleform<Resource> {
    private static final long  serialVersionUID                 = 1L;
    public static final String GET_CHILD                        = "resourceNetwork.getChild";
    public static final String GET_USED_RELATIONSHIPS           = "resourceNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "resource.immediateChildrenNetworkRules";

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "child")
    private Resource           child;

    @Id
    @GeneratedValue(generator = "resource_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "parent")
    private Resource           parent;

    public ResourceNetwork() {
    }

    /**
     * @param id
     */
    public ResourceNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ResourceNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ResourceNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ResourceNetwork(Resource parent, Relationship relationship,
                           Resource child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Resource getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Resource getParent() {
        return parent;
    }

    public List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @Override
    public void setChild(Resource resource3) {
        child = resource3;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Resource resource2) {
        parent = resource2;
    }
}