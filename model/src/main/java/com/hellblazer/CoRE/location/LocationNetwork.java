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
package com.hellblazer.CoRE.location;

import static com.hellblazer.CoRE.location.LocationNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.location.LocationNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.hellblazer.CoRE.location.LocationNetwork.GENERATE_NETWORK_INVERSES;
import static com.hellblazer.CoRE.location.LocationNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.location.LocationNetwork.INFERENCE_STEP;
import static com.hellblazer.CoRE.location.LocationNetwork.INFERENCE_STEP_FROM_LAST_PASS;
import static com.hellblazer.CoRE.location.LocationNetwork.INSERT_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.network.Networked.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INFERENCE_STEP_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INSERT_NEW_NETWORK_RULES_SUFFIX;

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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the location_network database table.
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
                                                                      + "              FROM ruleform.location_network AS n) as premise1 "
                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "            FROM ruleform.location_network AS n "
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
                                                                                     + "            FROM ruleform.location_network AS n "
                                                                                     + "            WHERE n.inferred = FALSE) as premise2  "
                                                                                     + "         ON premise2.parent = premise1.child "
                                                                                     + "         AND premise2.child <> premise1.parent "
                                                                                     + "     JOIN ruleform.network_inference AS deduction "
                                                                                     + "         ON premise1.relationship = deduction.premise1 "
                                                                                     + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
                                                                                     + "SELECT exist.id, wm.* "
                                                                                     + "FROM working_memory AS wm "
                                                                                     + "JOIN ruleform.location_network AS exist "
                                                                                     + "    ON wm.parent = exist.parent "
                                                                                     + "    AND wm.relationship = exist.relationship "
                                                                                     + "    AND wm.child = exist.child"),
                     @NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
                                                                                + "    SELECT nextval('ruleform.location_network_id_seq'), wm.* "
                                                                                + "    FROM (SELECT parent, relationship, child"
                                                                                + "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
                                                                                + "    LEFT OUTER JOIN ruleform.location_network AS exist "
                                                                                + "         ON wm.parent = exist.parent "
                                                                                + "         AND wm.relationship = exist.relationship "
                                                                                + "         AND wm.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "WITH upsert AS "
                                                                                + "       (UPDATE ruleform.location_network n  "
                                                                                + "        SET id = n.id, parent = n.parent, child= n.child "
                                                                                + "        FROM current_pass_rules cpr "
                                                                                + "        WHERE n.parent = cpr.parent "
                                                                                + "          AND n.relationship = cpr.relationship "
                                                                                + "          AND n.child = cpr.child "
                                                                                + "        RETURNING n.*) "
                                                                                + "INSERT INTO ruleform.location_network(id, parent, relationship, child, inferred, updated_by) "
                                                                                + "        SELECT cpr.id, cpr.parent, cpr.relationship, cpr.child, TRUE, ?1 "
                                                                                + "    FROM current_pass_rules cpr "
                                                                                + "    LEFT OUTER JOIN upsert AS exist "
                                                                                + "        ON cpr.parent = exist.parent "
                                                                                + "        AND cpr.relationship = exist.relationship "
                                                                                + "        AND cpr.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = GENERATE_NETWORK_INVERSES, query = "INSERT INTO ruleform.location_network(parent, relationship, child, updated_by, inferred) "
                                                                                 + "SELECT net.child as parent, "
                                                                                 + "    rel.inverse as relationship, "
                                                                                 + "    net.parent as child, "
                                                                                 + "    ?1 as updated_by,"
                                                                                 + "    net.inferred "
                                                                                 + "FROM ruleform.location_network AS net "
                                                                                 + "JOIN ruleform.relationship AS rel ON net.relationship = rel.id "
                                                                                 + "LEFT OUTER JOIN ruleform.location_network AS exist "
                                                                                 + "    ON net.child = exist.parent "
                                                                                 + "    AND rel.inverse = exist.relationship "
                                                                                 + "    AND net.parent = exist.child "
                                                                                 + " WHERE exist.parent IS NULL "
                                                                                 + "  AND exist.relationship IS NULL "
                                                                                 + "  AND exist.child IS NULL") })
@Entity
@Table(name = "location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_network_id_seq", sequenceName = "location_network_id_seq")
@NamedQueries({ @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from LocationNetwork n") })
public class LocationNetwork extends NetworkRuleform<Location> {
    private static final long  serialVersionUID              = 1L;
    public static final String GET_USED_RELATIONSHIPS        = "locationNetwork.getUsedRelationships";
    public static final String INFERENCE_STEP                = "locationNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "locationNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "locationNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String DEDUCE_NEW_NETWORK_RULES      = "locationNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "locationNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "locationNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Location child;

    @Id
    @GeneratedValue(generator = "location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long     id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Location parent;

    public LocationNetwork() {
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public LocationNetwork(Location parent, Relationship relationship,
                           Location child, Resource updatedBy) {
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
    public LocationNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public LocationNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Location getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
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

    @Override
    public void setParent(Location parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
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
