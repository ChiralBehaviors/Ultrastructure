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
package com.hellblazer.CoRE.resource;

import static com.hellblazer.CoRE.network.Networked.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INFERENCE_STEP_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INSERT_DEDUCTION_PATH_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.RECORD_DEDUCTION_PATH_SUFFIX;
import static com.hellblazer.CoRE.resource.ResourceNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.resource.ResourceNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.hellblazer.CoRE.resource.ResourceNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.resource.ResourceNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.resource.ResourceNetwork.INFERENCE_STEP;
import static com.hellblazer.CoRE.resource.ResourceNetwork.INSERT_DEDUCTION_PATH;
import static com.hellblazer.CoRE.resource.ResourceNetwork.INSERT_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.resource.ResourceNetwork.RECORD_DEDUCTION_PATH;

import java.util.List;

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

import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The network relationships of agencies
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "resource_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_network_id_seq", sequenceName = "resource_network_id_seq")
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from ResourceNetwork AS n "
                                                                            + "where n.parent = :resource "
                                                                            + "and n.inferred = FALSE "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ResourceNetwork n") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = INFERENCE_STEP, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                      + "     SELECT "
                                                                      + "         premise1.parent, "
                                                                      + "         deduction.inferrence, "
                                                                      + "         premise2.child, "
                                                                      + "         premise1.id, "
                                                                      + "         premise2.id "
                                                                      + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child, n.distance "
                                                                      + "              FROM ruleform.network_inferrence ) as premise1 "
                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "            FROM ruleform.resource_network AS n "
                                                                      + "            WHERE n.inferred = FALSE) as premise2  "
                                                                      + "         ON premise2.parent = premise1.child "
                                                                      + "         AND premise2.child <> premise1.parent "
                                                                      + "     JOIN network_inferrence AS deduction "
                                                                      + "         ON premise1.relationship = deduction.premise1 "
                                                                      + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
                                                                                     + "SELECT exist.id, wm.* "
                                                                                     + "FROM working_memory AS wm "
                                                                                     + "JOIN ruleform.resource_network AS exist "
                                                                                     + "    ON wm.parent = exist.parent "
                                                                                     + "    AND wm.relationship = exist.relationship "
                                                                                     + "    AND wm.child = exist.child"),
                     @NamedNativeQuery(name = RECORD_DEDUCTION_PATH, query = "INSERT INTO resource_network_deduction(deduction, premise1, premise2) "
                                                                             + "SELECT c.id, c.premise1, c.premise2) "
                                                                             + "    FROM current_pass_existing_rules AS c "
                                                                             + "    LEFT OUTER JOIN resource_network_deduction AS b "
                                                                             + "        ON c.id = b.deduction "
                                                                             + "        AND c.premise1 = b.premise1 "
                                                                             + "        AND c.premise2 = b.premise2 "
                                                                             + "     WHERE b.deduction IS NULL "
                                                                             + "     AND b.premise1 IS NULL "
                                                                             + "     AND b.premise2 IS NULL "),
                     @NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
                                                                                + "    SELECT nextval(resource_network_id_seq), wm.* "
                                                                                + "    FROM (SELECT parent, relationship, child"
                                                                                + "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
                                                                                + "    LEFT OUTER JOIN resource_network AS exist "
                                                                                + "         ON wm.parent = exist.parent "
                                                                                + "         AND wm.relationship = exist.relationship "
                                                                                + "         AND wm.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "INSERT INTO resource_network(id, parent, relationship, child, TRUE) "
                                                                                + "    SELECT id, parent, relationship, child "
                                                                                + "    FROM current_pass_rules"),
                     @NamedNativeQuery(name = INSERT_DEDUCTION_PATH, query = "INSERT INTO resource_network_deduction(deduction, premise1, premise2)"
                                                                             + "SELECT cpr.id, wm.premise1, wm.premise2) "
                                                                             + "FROM current_pass_rules AS cpr "
                                                                             + "JOIN working_memory AS wm "
                                                                             + "   ON cpr.resource = wm.resource "
                                                                             + "   AND cpr.parent = wm.parent "
                                                                             + "   AND cpr.relationship = wm.relationship "
                                                                             + "   AND cpr.child = wm.child") })
public class ResourceNetwork extends NetworkRuleform<Resource> {
    private static final long  serialVersionUID                 = 1L;
    public static final String GET_USED_RELATIONSHIPS           = "resourceNetwork.getUsedRelationships";
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "resource.immediateChildrenNetworkRules";
    public static final String INFERENCE_STEP                   = "resourceNetwork"
                                                                  + INFERENCE_STEP_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES    = "resourceNetwork"
                                                                  + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String RECORD_DEDUCTION_PATH            = "resourceNetwork"
                                                                  + RECORD_DEDUCTION_PATH_SUFFIX;
    public static final String DEDUCE_NEW_NETWORK_RULES         = "resourceNetwork"
                                                                  + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES         = "resourceNetwork"
                                                                  + INSERT_NEW_NETWORK_RULES_SUFFIX;
    public static final String INSERT_DEDUCTION_PATH            = "resourceNetwork"
                                                                  + INSERT_DEDUCTION_PATH_SUFFIX;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "child")
    private Resource           child;

    @Id
    @GeneratedValue(generator = "resource_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "parent")
    private Resource           parent;

    public ResourceNetwork() {
    }

    /**
     * @param id
     */
    public ResourceNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ResourceNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ResourceNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ResourceNetwork(Resource parent, Relationship relationship,
                           Resource child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Resource getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Resource getParent() {
        return parent;
    }

    public List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @Override
    public void setChild(Resource resource3) {
        child = resource3;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Resource resource2) {
        parent = resource2;
    }
}