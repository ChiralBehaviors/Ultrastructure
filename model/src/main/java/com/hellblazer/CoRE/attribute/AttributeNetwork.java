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

import static com.hellblazer.CoRE.attribute.AttributeNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

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
 * The persistent class for the attribute_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "attribute_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attribute_network_id_seq", sequenceName = "attribute_network_id_seq")
@NamedQueries({ @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "SELECT n FROM AttributeNetwork n "
                                                                             + "WHERE n.parent = :attribute and n.distance = 1 and n.relationship.preferred = FALSE "
                                                                             + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
public class AttributeNetwork extends NetworkRuleform<Attribute> {
    private static final long  serialVersionUID                 = 1L;
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "attribute.immediateChildrenNetworkRules";
    public static final String GET_USED_RELATIONSHIPS           = "attributeNetwork.getUsedRelationships";

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "child")
    private Attribute          child;

    @Id
    @GeneratedValue(generator = "attribute_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "parent")
    private Attribute          parent;

    public AttributeNetwork() {
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AttributeNetwork(Attribute parent, Relationship relationship,
                            Attribute child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public AttributeNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AttributeNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public AttributeNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Attribute getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Attribute getParent() {
        return parent;
    }

    @Override
    public void setChild(Attribute child) {
        this.child = child;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Attribute parent) {
        this.parent = parent;
    }
}