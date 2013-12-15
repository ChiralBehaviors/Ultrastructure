/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.agency;

import static com.hellblazer.CoRE.agency.AgencyNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.agency.AgencyNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.hellblazer.CoRE.agency.AgencyNetwork.GENERATE_NETWORK_INVERSES;
import static com.hellblazer.CoRE.agency.AgencyNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.agency.AgencyNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.agency.AgencyNetwork.INFERENCE_STEP;
import static com.hellblazer.CoRE.agency.AgencyNetwork.INFERENCE_STEP_FROM_LAST_PASS;
import static com.hellblazer.CoRE.agency.AgencyNetwork.INSERT_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.hellblazer.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The network relationships of agencies
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "agency_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "agency_network_id_seq", sequenceName = "agency_network_id_seq")
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from AgencyNetwork AS n "
                                                                            + "where n.parent = :agency "
                                                                            + "and n.inferred = FALSE "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from AgencyNetwork n") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = INFERENCE_STEP, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                      + "     SELECT "
                                                                      + "         premise1.parent, "
                                                                      + "         deduction.inference, "
                                                                      + "         premise2.child, "
                                                                      + "         premise1.id, "
                                                                      + "         premise2.id "
                                                                      + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "              FROM ruleform.agency_network AS n) as premise1 "
                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "            FROM ruleform.agency_network AS n "
                                                                      + "            WHERE n.inferred = FALSE) as premise2  "
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
                                                                                     + "            FROM ruleform.agency_network AS n "
                                                                                     + "            WHERE n.inferred = FALSE) as premise2  "
                                                                                     + "         ON premise2.parent = premise1.child "
                                                                                     + "         AND premise2.child <> premise1.parent "
                                                                                     + "     JOIN ruleform.network_inference AS deduction "
                                                                                     + "         ON premise1.relationship = deduction.premise1 "
                                                                                     + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
                                                                                     + "SELECT exist.id, wm.* "
                                                                                     + "FROM working_memory AS wm "
                                                                                     + "JOIN ruleform.agency_network AS exist "
                                                                                     + "    ON wm.parent = exist.parent "
                                                                                     + "    AND wm.relationship = exist.relationship "
                                                                                     + "    AND wm.child = exist.child"),
                     @NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
                                                                                + "    SELECT nextval('ruleform.agency_network_id_seq'), wm.* "
                                                                                + "    FROM (SELECT parent, relationship, child"
                                                                                + "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
                                                                                + "    LEFT OUTER JOIN ruleform.agency_network AS exist "
                                                                                + "         ON wm.parent = exist.parent "
                                                                                + "         AND wm.relationship = exist.relationship "
                                                                                + "         AND wm.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "WITH upsert AS "
                                                                                + "       (UPDATE ruleform.agency_network n  "
                                                                                + "        SET id = n.id, parent = n.parent, child= n.child "
                                                                                + "        FROM current_pass_rules cpr "
                                                                                + "        WHERE n.parent = cpr.parent "
                                                                                + "          AND n.relationship = cpr.relationship "
                                                                                + "          AND n.child = cpr.child "
                                                                                + "        RETURNING n.*) "
                                                                                + "INSERT INTO ruleform.agency_network(id, parent, relationship, child, inferred, updated_by) "
                                                                                + "        SELECT cpr.id, cpr.parent, cpr.relationship, cpr.child, TRUE, ?1 "
                                                                                + "    FROM current_pass_rules cpr "
                                                                                + "    LEFT OUTER JOIN upsert AS exist "
                                                                                + "        ON cpr.parent = exist.parent "
                                                                                + "        AND cpr.relationship = exist.relationship "
                                                                                + "        AND cpr.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = GENERATE_NETWORK_INVERSES, query = "INSERT INTO ruleform.agency_network(parent, relationship, child, updated_by, inferred) "
                                                                                 + "SELECT net.child as parent, "
                                                                                 + "    rel.inverse as relationship, "
                                                                                 + "    net.parent as child, "
                                                                                 + "    ?1 as updated_by,"
                                                                                 + "    net.inferred "
                                                                                 + "FROM ruleform.agency_network AS net "
                                                                                 + "JOIN ruleform.relationship AS rel ON net.relationship = rel.id "
                                                                                 + "LEFT OUTER JOIN ruleform.agency_network AS exist "
                                                                                 + "    ON net.child = exist.parent "
                                                                                 + "    AND rel.inverse = exist.relationship "
                                                                                 + "    AND net.parent = exist.child "
                                                                                 + " WHERE exist.parent IS NULL "
                                                                                 + "  AND exist.relationship IS NULL "
                                                                                 + "  AND exist.child IS NULL") })
public class AgencyNetwork extends NetworkRuleform<Agency> {
    private static final long  serialVersionUID                 = 1L;
    public static final String GET_USED_RELATIONSHIPS           = "agencyNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "agency.immediateChildrenNetworkRules";
    public static final String INFERENCE_STEP                   = "agencyNetwork"
                                                                  + INFERENCE_STEP_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES    = "agencyNetwork"
                                                                  + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES        = "agencyNetwork"
                                                                  + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String DEDUCE_NEW_NETWORK_RULES         = "agencyNetwork"
                                                                  + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES         = "agencyNetwork"
                                                                  + INSERT_NEW_NETWORK_RULES_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS    = "agencyNetwork"
                                                                  + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "child")
    private Agency             child;

    @Id
    @GeneratedValue(generator = "agency_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Agency             parent;

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

    @Override
    public Agency getParent() {
        return parent;
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

    @Override
    public void setParent(Agency agency2) {
        parent = agency2;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
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
