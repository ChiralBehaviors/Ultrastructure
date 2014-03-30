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
package com.chiralbehaviors.CoRE.coordinate;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.coordinate.CoordinateNetwork.GET_CHILDREN;

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
@NamedQueries({ @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM CoordinateNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship") })
@Entity
@Table(name = "coordinate_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_network_id_seq", sequenceName = "coordinate_network_id_seq")
public class CoordinateNetwork extends NetworkRuleform<Coordinate> {

    public static final String DEDUCE_NEW_NETWORK_RULES      = "coordinateNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "coordinateNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "coordinateNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                  = "coordinateNetwork"
                                                               + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS        = "coordinateNetwork"
                                                               + USED_RELATIONSHIPS_SUFFIX;
    public static final String INFERENCE_STEP                = "coordinateNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "coordinateNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "coordinateNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID              = 1L;                                    //bi-directional many-to-one association to Agency

    @ManyToOne
    @JoinColumn(name = "child")
    private Coordinate         child;

    @Id
    @GeneratedValue(generator = "coordinate_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Coordinate         parent;

    /**
     * 
     */
    public CoordinateNetwork() {
        super();
    }

    /**
     * @param updatedBy
     */
    public CoordinateNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public CoordinateNetwork(Coordinate parent, Relationship relationship,
                             Coordinate child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public CoordinateNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public CoordinateNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public Coordinate getChild() {
        return child;
    }

    /* (non-Javadoc)
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
    public Coordinate getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkRuleform#setChild(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setChild(Coordinate child) {
        this.child = child;
    }

    /* (non-Javadoc)
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
    public void setParent(Coordinate parent) {
        this.parent = parent;
    }
}
