/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.attribute;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GET_NETWORKS;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The network relationships of attributes.
 *
 * @author hhildebrand
 *
 */

@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "SELECT n FROM AttributeNetwork n "
                                                                            + "WHERE n.parent = :attribute and n.inference.id = 'AAAAAAAAAAAAAAAAAAAAAA' "
                                                                            + "AND n.relationship.preferred = 1 "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM AttributeNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship"),
               @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM AttributeNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship "
                                                        + "AND n.child = :child") })
@Entity
@Table(name = "attribute_network", schema = "ruleform")
public class AttributeNetwork extends NetworkRuleform<Attribute> {
    public static final String GET_CHILDREN                     = "attributeNetwork"
                                                                  + GET_CHILDREN_SUFFIX;
    public static final String GET_NETWORKS                     = "attributeNetwork"
                                                                  + GET_NETWORKS_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS           = "attributeNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "attribute.immediateChildrenNetworkRules";
    private static final long  serialVersionUID                 = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Attribute          child;

    //bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Attribute          parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(insertable = false, name = "premise1")
    private AttributeNetwork   premise1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(insertable = false, name = "premise2")
    private AttributeNetwork   premise2;

    public AttributeNetwork() {
    }

    /**
     * @param updatedBy
     */
    public AttributeNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AttributeNetwork(Attribute parent, Relationship relationship,
                            Attribute child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AttributeNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param id
     */
    public AttributeNetwork(UUID id) {
        super(id);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    @Override
    @JsonGetter
    public Attribute getChild() {
        return child;
    }

    @Override
    @JsonGetter
    public Attribute getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    @JsonGetter
    public AttributeNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    @JsonGetter
    public AttributeNetwork getPremise2() {
        return premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeNetwork> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeNetwork;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    @Override
    public void setChild(Attribute child) {
        this.child = child;
    }

    @Override
    public void setParent(Attribute parent) {
        this.parent = parent;
    }
}
