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
package com.chiralbehaviors.CoRE.relationship;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.relationship.RelationshipNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.relationship.RelationshipNetwork.GET_NETWORKS;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({ @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM RelationshipNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship"),
                @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM RelationshipNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship "
                                                         + "AND n.child = :child") })
@Entity
@Table(name = "relationship_network", schema = "ruleform")
public class RelationshipNetwork extends NetworkRuleform<Relationship> {
    public static final String GET_CHILDREN           = "relationshipNetwork"
                                                        + GET_CHILDREN_SUFFIX;
    public static final String GET_NETWORKS           = "relationshipNetwork"
                                                        + GET_NETWORKS_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS = "relationshipNetwork"
                                                        + USED_RELATIONSHIPS_SUFFIX;
    private static final long  serialVersionUID       = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Relationship child;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Relationship parent;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "premise1")
    private RelationshipNetwork premise1;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "premise2")
    private RelationshipNetwork premise2;

    /**
     *
     */
    public RelationshipNetwork() {
        super();
    }

    /**
     * @param updatedBy
     */
    public RelationshipNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public RelationshipNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public RelationshipNetwork(Relationship parent, Relationship relationship,
                               Relationship child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public RelationshipNetwork(UUID id) {
        super(id);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    @Override
    @JsonIgnore
    public Class<?> getAttributeClass() {
        return RelationshipNetworkAttribute.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    @JsonGetter
    public Relationship getChild() {
        return child;
    }

    /*
     * (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    @JsonGetter
    public Relationship getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    @JsonGetter
    public RelationshipNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    @JsonGetter
    public RelationshipNetwork getPremise2() {
        return premise2;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkRuleform#setChild(com.chiralbehaviors
     * .CoRE.ExistentialRuleform)
     */
    @Override
    public void setChild(Relationship child) {
        this.child = child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#setParent(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setParent(Relationship parent) {
        this.parent = parent;
    }
}
