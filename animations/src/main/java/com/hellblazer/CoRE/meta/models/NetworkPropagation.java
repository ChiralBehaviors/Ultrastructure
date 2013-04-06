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

package com.hellblazer.CoRE.meta.models;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class NetworkPropagation {
    private final String        networkTable;
    private final EntityManager em;
    private final Resource      inverseSoftware;
    private final Resource      propagationSoftware;

    /**
     * @param networkTable
     * @param em
     */
    public NetworkPropagation(String networkTable, Resource inverseSoftware,
                              Resource propagationSoftware, EntityManager em) {
        this.networkTable = networkTable;
        this.inverseSoftware = inverseSoftware;
        this.propagationSoftware = propagationSoftware;
        this.em = em;
    }

    public void createCurrentPassExistingRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE current_pass_existing_rules ("
                                     + "id BIGINT NOT NULL,"
                                     + "parent BIGINT NOT NULL,"
                                     + "relationship BIGINT NOT NULL,"
                                     + "child BIGINT NOT NULL,"
                                     + "distance INTEGER NOT NULL,"
                                     + "updated_by BIGINT NOT NULL,"
                                     + "premise1 BIGINT NOT NULL,"
                                     + "premise2 BIGINT NOT NULL)").executeUpdate();
    }

    public void createCurrentPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE current_pass_rules ("
                                     + "id BIGINT NOT NULL,"
                                     + "parent BIGINT NOT NULL,"
                                     + "relationship BIGINT NOT NULL,"
                                     + "child BIGINT NOT NULL,"
                                     + "distance INTEGER NOT NULL,"
                                     + "updated_by BIGINT NOT NULL)").executeUpdate();
    }

    public void createLastPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE last_pass_rules ("
                                     + "id BIGINT NOT NULL,"
                                     + "parent BIGINT NOT NULL,"
                                     + "relationship BIGINT NOT NULL,"
                                     + "child BIGINT NOT NULL,"
                                     + "distance INTEGER NOT NULL,"
                                     + "updated_by BIGINT NOT NULL)").executeUpdate();
    }

    public void createWorkingMemory() {
        em.createNativeQuery("CREATE TEMPORARY TABLE working_memory("
                                     + "parent BIGINT NOT NULL,"
                                     + "relationship BIGINT NOT NULL,"
                                     + "child BIGINT NOT NULL,"
                                     + "distance INTEGER NOT NULL,"
                                     + "updated_by BIGINT NOT NULL,"
                                     + "premise1 BIGINT NOT NULL,"
                                     + "premise2 BIGINT NOT NULL )").executeUpdate();
    }

    public void retrieveDeducedRulesUniqueID() {
        em.createNativeQuery("INSERT INTO current_pass_existing_rules "
                                     + "SELECT exist.id, wm.* "
                                     + "FROM working_memory AS wm "
                                     + "JOIN "
                                     + networkTable
                                     + " AS exist "
                                     + "ON "
                                     + "  wm.parent = exist.parent "
                                     + " AND wm.relationship = exist.relationship "
                                     + " AND wm.child = exist.child").executeUpdate();
    }

    public void step_deduceAllPossibleRules(boolean firstPass) {
        Query deduce = em.createNativeQuery("INSERT INTO working_memory("
                                            + "parent, relationship, child, distance, updated_by, premise1, premise2) "
                                            + "     SELECT "
                                            + "     premise1.parent, "
                                            + "     chain.result, "
                                            + "     premise2.child, "
                                            + "     (premise1.distance + premise2.distance) as distance, "
                                            + "     ?1, " // propagationSoftware Resource
                                            + "     premise1.id, "
                                            + "     premise2.id "
                                            + "     FROM  (SELECT n.id, "
                                            + "     n.parent, n.relationship, n.child, n.distance FROM "
                                            + (firstPass ? networkTable
                                                           + " AS n "
                                                        : " last_pass_rules AS n ")
                                            + "     ) as premise1 "
                                            + "     JOIN  (SELECT n.id, "
                                            + "     n.parent, n.relationship, n.child, n.distance "
                                            + "         FROM "
                                            + networkTable
                                            + " AS n "
                                            + "         WHERE "
                                            + "             n.distance = 1 "
                                            + "           AND n.updated_by != ?2" // inverseSoftware Resource
                                            + "     ) as premise2  "
                                            + "     ON premise2.parent = premise1.child "
                                            + "         AND premise2.child <> premise1.parent "
                                            + "     JOIN relationship_chain AS chain "
                                            + "         ON premise1.relationship = chain.input1 "
                                            + "         AND premise2.relationship = chain.input2 ");
        deduce.setParameter(1, propagationSoftware);
        deduce.setParameter(2, inverseSoftware);
        deduce.executeUpdate();
    }
}
