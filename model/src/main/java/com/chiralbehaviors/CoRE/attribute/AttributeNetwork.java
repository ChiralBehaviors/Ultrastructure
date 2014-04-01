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
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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

@Entity
@Table(name = "attribute_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attribute_network_id_seq", sequenceName = "attribute_network_id_seq")
public class AttributeNetwork extends NetworkRuleform<Attribute> {
    public static final String DEDUCE_NEW_NETWORK_RULES         = "attributeNetwork"
                                                                  + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES    = "attributeNetwork"
                                                                  + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES        = "attributeNetwork"
                                                                  + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                     = "attributeNetwork"
                                                                  + GET_CHILDREN_SUFFIX;
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

    @Id
    @GeneratedValue(generator = "attribute_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "parent")
    private Attribute          parent;

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
     * @param id
     */
    public AttributeNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AttributeNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
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
