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

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GET_NETWORKS;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

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
    public static final String DEDUCE_NEW_NETWORK_RULES         = "attributeNetwork"
                                                                  + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES        = "attributeNetwork"
                                                                  + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                     = "attributeNetwork"
                                                                  + GET_CHILDREN_SUFFIX;
    public static final String GET_NETWORKS                     = "attributeNetwork"
                                                                  + GET_NETWORKS_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS           = "attributeNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "attribute.immediateChildrenNetworkRules";
    public static final String INFERENCE_STEP                   = "attributeNetwork"
                                                                  + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS    = "attributeNetwork"
                                                                  + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES         = "attributeNetwork"
                                                                  + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID                 = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "child")
    private Attribute          child;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "parent")
    private Attribute          parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, name = "premise1")
    private AttributeNetwork   premise1;

    @ManyToOne(fetch = FetchType.LAZY)
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
    public Attribute getChild() {
        return child;
    }

    @Override
    public Attribute getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    public AttributeNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    public AttributeNetwork getPremise2() {
        return premise2;
    }

    @Override
    public void setChild(Attribute child) {
        this.child = child;
    }

    @Override
    public void setParent(Attribute parent) {
        this.parent = parent;
    }

    /**
     * @param premise1
     *            the premise1 to set
     */
    @Override
    public void setPremise1(NetworkRuleform<Attribute> premise1) {
        this.premise1 = (AttributeNetwork) premise1;
    }

    /**
     * @param premise2
     *            the premise2 to set
     */
    @Override
    public void setPremise2(NetworkRuleform<Attribute> premise2) {
        this.premise2 = (AttributeNetwork) premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (child != null) {
            child = (Attribute) child.manageEntity(em, knownObjects);
        }

        if (parent != null) {
            parent = (Attribute) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
