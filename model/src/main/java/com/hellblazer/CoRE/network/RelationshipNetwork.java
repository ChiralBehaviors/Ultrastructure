/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
package com.hellblazer.CoRE.network;

import static com.hellblazer.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.hellblazer.CoRE.network.RelationshipNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.network.RelationshipNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.hellblazer.CoRE.network.RelationshipNetwork.GENERATE_NETWORK_INVERSES;
import static com.hellblazer.CoRE.network.RelationshipNetwork.GET_CHILDREN;
import static com.hellblazer.CoRE.network.RelationshipNetwork.INFERENCE_STEP;
import static com.hellblazer.CoRE.network.RelationshipNetwork.INFERENCE_STEP_FROM_LAST_PASS;
import static com.hellblazer.CoRE.network.RelationshipNetwork.INSERT_NEW_NETWORK_RULES;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.agency.Agency;

/**
 * @author hhildebrand
 * 
 */
@NamedNativeQueries({
                     @NamedNativeQuery(name = INFERENCE_STEP, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                      + "     SELECT "
                                                                      + "         premise1.parent, "
                                                                      + "         deduction.inference, "
                                                                      + "         premise2.child, "
                                                                      + "         premise1.id, "
                                                                      + "         premise2.id "
                                                                      + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "              FROM ruleform.relationship_network AS n) as premise1 "
                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "            FROM ruleform.relationship_network AS n "
                                                                      + "            WHERE n.inferred = 0) as premise2  "
                                                                      + "         ON premise2.parent = premise1.child "
                                                                      + "         AND premise2.child <> premise1.parent "
                                                                      + "     JOIN ruleform.network_inference AS deduction "
                                                                      + "         ON premise1.relationship = deduction.premise1 "
                                                                      + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = INFERENCE_STEP_FROM_LAST_PASS, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                                     + "     SELECT "
                                                                                     + "         premise1.parent, "
                                                                                     + "         deduction.inference, "
                                                                                     + "         premise2.child, "
                                                                                     + "         premise1.id, "
                                                                                     + "         premise2.id "
                                                                                     + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child"
                                                                                     + "              FROM last_pass_rules AS n) as premise1 "
                                                                                     + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                                     + "            FROM ruleform.relationship_network AS n "
                                                                                     + "            WHERE n.inferred = 0) as premise2  "
                                                                                     + "         ON premise2.parent = premise1.child "
                                                                                     + "         AND premise2.child <> premise1.parent "
                                                                                     + "     JOIN ruleform.network_inference AS deduction "
                                                                                     + "         ON premise1.relationship = deduction.premise1 "
                                                                                     + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
                                                                                     + "SELECT exist.id, wm.* "
                                                                                     + "FROM working_memory AS wm "
                                                                                     + "JOIN ruleform.relationship_network AS exist "
                                                                                     + "    ON wm.parent = exist.parent "
                                                                                     + "    AND wm.relationship = exist.relationship "
                                                                                     + "    AND wm.child = exist.child"),
                     @NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
                                                                                + "    SELECT nextval('ruleform.relationship_network_id_seq'), wm.* "
                                                                                + "    FROM (SELECT parent, relationship, child"
                                                                                + "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
                                                                                + "    LEFT OUTER JOIN ruleform.relationship_network AS exist "
                                                                                + "         ON wm.parent = exist.parent "
                                                                                + "         AND wm.relationship = exist.relationship "
                                                                                + "         AND wm.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "WITH upsert AS "
                                                                                + "       (UPDATE ruleform.relationship_network n  "
                                                                                + "        SET id = n.id, parent = n.parent, child= n.child "
                                                                                + "        FROM current_pass_rules cpr "
                                                                                + "        WHERE n.parent = cpr.parent "
                                                                                + "          AND n.relationship = cpr.relationship "
                                                                                + "          AND n.child = cpr.child "
                                                                                + "        RETURNING n.*) "
                                                                                + "INSERT INTO ruleform.relationship_network(id, parent, relationship, child, inferred, updated_by) "
                                                                                + "        SELECT cpr.id, cpr.parent, cpr.relationship, cpr.child, 1, ?1 "
                                                                                + "    FROM current_pass_rules cpr "
                                                                                + "    LEFT OUTER JOIN upsert AS exist "
                                                                                + "        ON cpr.parent = exist.parent "
                                                                                + "        AND cpr.relationship = exist.relationship "
                                                                                + "        AND cpr.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = GENERATE_NETWORK_INVERSES, query = "INSERT INTO ruleform.relationship_network(parent, relationship, child, updated_by, inferred) "
                                                                                 + "SELECT net.child as parent, "
                                                                                 + "    rel.inverse as relationship, "
                                                                                 + "    net.parent as child, "
                                                                                 + "    ?1 as updated_by,"
                                                                                 + "    net.inferred "
                                                                                 + "FROM ruleform.relationship_network AS net "
                                                                                 + "JOIN ruleform.relationship AS rel ON net.relationship = rel.id "
                                                                                 + "LEFT OUTER JOIN ruleform.relationship_network AS exist "
                                                                                 + "    ON net.child = exist.parent "
                                                                                 + "    AND rel.inverse = exist.relationship "
                                                                                 + "    AND net.parent = exist.child "
                                                                                 + " WHERE exist.parent IS NULL "
                                                                                 + "  AND exist.relationship IS NULL "
                                                                                 + "  AND exist.child IS NULL") })
@NamedQueries({ @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM RelationshipNetwork n "
                                                         + "WHERE n.parent = :parent "
                                                         + "AND n.relationship = :relationship") })
@Entity
@Table(name = "relationship_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_network_id_seq", sequenceName = "relationship_network_id_seq")
public class RelationshipNetwork extends NetworkRuleform<Relationship> {
    public static final String DEDUCE_NEW_NETWORK_RULES      = "relationshipNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "relationshipNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "relationshipNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                  = "relationshipNetwork"
                                                               + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS        = "relationshipNetwork"
                                                               + USED_RELATIONSHIPS_SUFFIX;
    public static final String INFERENCE_STEP                = "relationshipNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "relationshipNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "relationshipNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID              = 1L;                                    //bi-directional many-to-one association to Agency

    @ManyToOne
    @JoinColumn(name = "child")
    private Relationship       child;

    @Id
    @GeneratedValue(generator = "relationship_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Relationship       parent;

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
     * @param id
     */
    public RelationshipNetwork(Long id) {
        super(id);
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public Relationship getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    public Relationship getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setChild(com.hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public void setChild(Relationship child) {
        this.child = child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setParent(com.hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public void setParent(Relationship parent) {
        this.parent = parent;
    }
}
