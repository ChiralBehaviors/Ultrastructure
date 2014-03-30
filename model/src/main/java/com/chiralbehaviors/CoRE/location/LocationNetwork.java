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
package com.chiralbehaviors.CoRE.location;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.location.LocationNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.location.LocationNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
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
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * The persistent class for the location_network database table.
 * 
 */
@NamedQueries({
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from LocationNetwork n"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM LocationNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship") })
@Entity
@Table(name = "location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_network_id_seq", sequenceName = "location_network_id_seq")
public class LocationNetwork extends NetworkRuleform<Location> {
    public static final String DEDUCE_NEW_NETWORK_RULES      = "locationNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "locationNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "locationNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                  = "locationNetwork"
                                                               + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS        = "locationNetwork.getUsedRelationships";
    public static final String INFERENCE_STEP                = "locationNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "locationNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "locationNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID              = 1L;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Location child;

    @Id
    @GeneratedValue(generator = "location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long     id;

    @ManyToOne
    @JoinColumn(name = "inferred_from")
    private Location inferredFrom;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Location parent;

    public LocationNetwork() {
    }

    /**
     * @param updatedBy
     */
    public LocationNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Location parent, Relationship relationship,
                           Location child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public LocationNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    @Override
    public Location getChild() {
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
    public Location getInferredFrom() {
        return inferredFrom;
    }

    @Override
    public Location getParent() {
        return parent;
    }

    @Override
    public void setChild(Location child) {
        this.child = child;
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
    public void setInferredFrom(Location inferredFrom) {
        this.inferredFrom = inferredFrom;
    }

    @Override
    public void setParent(Location parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (child != null) {
            child = (Location) child.manageEntity(em, knownObjects);
        }
        if (parent != null) {
            parent = (Location) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
