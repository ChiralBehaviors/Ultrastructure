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
package com.chiralbehaviors.CoRE.event.status;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork.GET_CHILDREN;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({ @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM StatusCodeNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship") })
@Entity
@Table(name = "status_code_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_network_id_seq", sequenceName = "status_code_network_id_seq")
public class StatusCodeNetwork extends NetworkRuleform<StatusCode> {

    public static final String DEDUCE_NEW_NETWORK_RULES      = "statusCodeNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "statusCodeNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                  = "statusCodeNetwork"
                                                               + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS        = "statusCodeNetwork"
                                                               + USED_RELATIONSHIPS_SUFFIX;
    public static final String INFERENCE_STEP                = "statusCodeNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "statusCodeNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "statusCodeNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;

    private static final long  serialVersionUID              = 1L;                                    // bi-directional
                                                                                                       // many-to-one
                                                                                                       // association to Agency

    @ManyToOne
    @JoinColumn(name = "child")
    private StatusCode         child;

    @Id
    @GeneratedValue(generator = "status_code_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private StatusCode         parent;

    @ManyToOne
    @JoinColumn(insertable = false, name = "premise1")
    private StatusCodeNetwork  premise1;

    @ManyToOne
    @JoinColumn(insertable = false, name = "premise2")
    private StatusCodeNetwork  premise2;

    /**
     * 
     */
    public StatusCodeNetwork() {
        super();
    }

    /**
     * @param updatedBy
     */
    public StatusCodeNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public StatusCodeNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public StatusCodeNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public StatusCodeNetwork(StatusCode parent, Relationship relationship,
                             StatusCode child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public StatusCode getChild() {
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    public StatusCode getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    public StatusCodeNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    public StatusCodeNetwork getPremise2() {
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
    public void setChild(StatusCode child) {
        this.child = child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#setParent(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setParent(StatusCode parent) {
        this.parent = parent;
    }

    /**
     * @param premise1
     *            the premise1 to set
     */
    @Override
    public void setPremise1(NetworkRuleform<StatusCode> premise1) {
        this.premise1 = (StatusCodeNetwork) premise1;
    }

    /**
     * @param premise2
     *            the premise2 to set
     */
    @Override
    public void setPremise2(NetworkRuleform<StatusCode> premise2) {
        this.premise2 = (StatusCodeNetwork) premise2;
    }
}
