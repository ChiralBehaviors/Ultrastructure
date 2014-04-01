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
package com.chiralbehaviors.CoRE.agency;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.agency.AgencyNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.agency.AgencyNetwork.GET_USED_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.agency.AgencyNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * The network relationships of agencies
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from AgencyNetwork AS n "
                                                                            + "where n.parent = :agency "
                                                                            + "and n.inferredFrom IS NULL "
                                                                            + "and n.relationship.preferred = 0 "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from AgencyNetwork n"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM AgencyNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship") })
@Entity
@Table(name = "agency_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "agency_network_id_seq", sequenceName = "agency_network_id_seq")
public class AgencyNetwork extends NetworkRuleform<Agency> {
    public static final String DEDUCE_NEW_NETWORK_RULES         = "agencyNetwork"
                                                                  + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES    = "agencyNetwork"
                                                                  + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES        = "agencyNetwork"
                                                                  + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                     = "agencyNetwork"
                                                                  + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS           = "agencyNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "agency.immediateChildrenNetworkRules";
    public static final String INFERENCE_STEP                   = "agencyNetwork"
                                                                  + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS    = "agencyNetwork"
                                                                  + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES         = "agencyNetwork"
                                                                  + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID                 = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "child")
    private Agency             child;

    @Id
    @GeneratedValue(generator = "agency_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;
 
    @ManyToOne
    @JoinColumn(insertable = false, name = "inferred_from")
    private AgencyNetwork      inferredFrom;

    //bi-directional many-to-one association to Agency 
    @ManyToOne
    @JoinColumn(name = "parent")
    private Agency             parent;

    @ManyToOne
    @JoinColumn(insertable = false, name = "premise1")
    private AgencyNetwork      premise1;

    @ManyToOne
    @JoinColumn(insertable = false, name = "premise2")
    private AgencyNetwork      premise2;

    public AgencyNetwork() {
    }

    /**
     * @param updatedBy
     */
    public AgencyNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AgencyNetwork(Agency parent, Relationship relationship,
                         Agency child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public AgencyNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public AgencyNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    @Override
    public Agency getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return the inferredFrom
     */
    @Override
    public AgencyNetwork getInferredFrom() {
        return inferredFrom;
    }

    @Override
    public Agency getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    public AgencyNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    public AgencyNetwork getPremise2() {
        return premise2;
    }

    public List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @Override
    public void setChild(Agency agency3) {
        child = agency3;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param inferredFrom
     *            the inferredFrom to set
     */
    @Override
    public void setInferredFrom(NetworkRuleform<Agency> inferredFrom) {
        this.inferredFrom = (AgencyNetwork) inferredFrom;
    }

    @Override
    public void setParent(Agency agency2) {
        parent = agency2;
    }
 
    /**
     * @param premise1
     *            the premise1 to set
     */
    @Override
    public void setPremise1(NetworkRuleform<Agency> premise1) {
        this.premise1 = (AgencyNetwork) premise1;
    }

    /**
     * @param premise2
     *            the premise2 to set
     */
    @Override
    public void setPremise2(NetworkRuleform<Agency> premise2) {
        this.premise2 = (AgencyNetwork) premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (child != null) {
            child = (Agency) child.manageEntity(em, knownObjects);
        }
        if (parent != null) {
            parent = (Agency) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
