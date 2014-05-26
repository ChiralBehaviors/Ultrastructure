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
package com.chiralbehaviors.CoRE.network;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.network.RelationshipNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.network.RelationshipNetwork.GET_NETWORKS;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.agency.Agency;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM RelationshipNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship"),
               @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM RelationshipNetwork n "
                                                        + "WHERE n.parent IN :parents "
                                                        + "AND n.relationship IN :relationships "
                                                        + "AND n.child IN :children") })
@Entity
@Table(name = "relationship_network", schema = "ruleform")
public class RelationshipNetwork extends NetworkRuleform<Relationship> {
    public static final String  DEDUCE_NEW_NETWORK_RULES      = "relationshipNetwork"
                                                                + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String  GENERATE_NETWORK_INVERSES     = "relationshipNetwork"
                                                                + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String  GET_CHILDREN                  = "relationshipNetwork"
                                                                + GET_CHILDREN_SUFFIX;
    public static final String  GET_NETWORKS                  = "relationshipNetwork"
                                                                + GET_NETWORKS_SUFFIX;
    public static final String  GET_USED_RELATIONSHIPS        = "relationshipNetwork"
                                                                + USED_RELATIONSHIPS_SUFFIX;
    public static final String  INFERENCE_STEP                = "relationshipNetwork"
                                                                + INFERENCE_STEP_SUFFIX;
    public static final String  INFERENCE_STEP_FROM_LAST_PASS = "relationshipNetwork"
                                                                + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String  INSERT_NEW_NETWORK_RULES      = "relationshipNetwork"
                                                                + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long   serialVersionUID              = 1L;

    @ManyToOne
    @JoinColumn(name = "child")
    private Relationship        child;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Relationship        parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, name = "premise1")
    private RelationshipNetwork premise1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, name = "premise2")
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

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public Relationship getChild() {
        return child;
    }

    /*
     * (non-Javadoc) 
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    public Relationship getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    public RelationshipNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    public RelationshipNetwork getPremise2() {
        return premise2;
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

    /**
     * @param premise1
     *            the premise1 to set
     */
    @Override
    public void setPremise1(NetworkRuleform<Relationship> premise1) {
        this.premise1 = (RelationshipNetwork) premise1;
    }

    /**
     * @param premise2
     *            the premise2 to set
     */
    @Override
    public void setPremise2(NetworkRuleform<Relationship> premise2) {
        this.premise2 = (RelationshipNetwork) premise2;
    }
}
