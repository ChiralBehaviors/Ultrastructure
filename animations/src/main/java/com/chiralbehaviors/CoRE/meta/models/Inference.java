/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE;
import static com.chiralbehaviors.CoRE.jooq.Tables.NETWORK_INFERENCE;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.jooq.tables.Existential;
import com.chiralbehaviors.CoRE.jooq.tables.Edge;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Network inference logic.
 * 
 * I implemented this as a functional programming experiment. I think it worked
 * out quite well.
 *
 * @author hhildebrand
 *
 */
public interface Inference {

    interface CurentPassRules {
        Field<UUID> CHILD        = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "child"),
                                             UUID.class);

        Field<UUID> ID           = DSL.field(DSL.name(CURRENT_PASS_RULES, "id"),
                                             UUID.class);
        Field<UUID> INFERENCE    = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "inference"),
                                             UUID.class);
        Field<UUID> PARENT       = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "parent"),
                                             UUID.class);
        Field<UUID> PREMISE1     = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "premise1"),
                                             UUID.class);
        Field<UUID> PREMISE2     = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "premise2"),
                                             UUID.class);
        Field<UUID> RELATIONSHIP = DSL.field(DSL.name(CURRENT_PASS_RULES,
                                                      "relationship"),
                                             UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(ID, PARENT, RELATIONSHIP, CHILD, PREMISE1,
                                 PREMISE2, INFERENCE);
        }
    }

    interface LastPassRules {
        Field<UUID> CHILD        = DSL.field(DSL.name(LAST_PASS_RULES, "child"),
                                             UUID.class);
        Field<UUID> ID           = DSL.field(DSL.name(LAST_PASS_RULES, "id"),
                                             UUID.class);
        Field<UUID> INFERENCE    = DSL.field(DSL.name(LAST_PASS_RULES,
                                                      "inference"),
                                             UUID.class);
        Field<UUID> PARENT       = DSL.field(DSL.name(LAST_PASS_RULES,
                                                      "parent"),
                                             UUID.class);
        Field<UUID> PREMISE1     = DSL.field(DSL.name(LAST_PASS_RULES,
                                                      "premise1"),
                                             UUID.class);
        Field<UUID> PREMISE2     = DSL.field(DSL.name(LAST_PASS_RULES,
                                                      "premise2"),
                                             UUID.class);
        Field<UUID> RELATIONSHIP = DSL.field(DSL.name(LAST_PASS_RULES,
                                                      "relationship"),
                                             UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(ID, PARENT, RELATIONSHIP, CHILD, PREMISE1,
                                 PREMISE2, INFERENCE);
        }
    }

    interface WorkingMemory {
        Field<UUID> CHILD        = DSL.field(DSL.name(WORKING_MEMORY, "child"),
                                             UUID.class);
        Field<UUID> INFERENCE    = DSL.field(DSL.name(WORKING_MEMORY,
                                                      "inference"),
                                             UUID.class);
        Field<UUID> PARENT       = DSL.field(DSL.name(WORKING_MEMORY, "parent"),
                                             UUID.class);
        Field<UUID> PREMISE1     = DSL.field(DSL.name(WORKING_MEMORY,
                                                      "premise1"),
                                             UUID.class);
        Field<UUID> PREMISE2     = DSL.field(DSL.name(WORKING_MEMORY,
                                                      "premise2"),
                                             UUID.class);
        Field<UUID> RELATIONSHIP = DSL.field(DSL.name(WORKING_MEMORY,
                                                      "relationship"),
                                             UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(PARENT, RELATIONSHIP, CHILD, PREMISE1,
                                 PREMISE2, INFERENCE);
        }
    }

    final String       CURRENT_PASS_RULES       = "current_pass_rules";
    final Table<?>     CURRENT_PASS_RULES_TABLE = DSL.table(DSL.name(CURRENT_PASS_RULES));
    static Field<UUID> GENERATE_UUID            = DSL.field("uuid_generate_v1mc()",
                                                            UUID.class);
    final String       LAST_PASS_RULES          = "last_pass_rules";
    final Table<?>     LAST_PASS_RULES_TABLE    = DSL.table(DSL.name(LAST_PASS_RULES));
    static Logger      log                      = LoggerFactory.getLogger(Inference.class);
    static int         MAX_DEDUCTIONS           = 1000;
    final String       WORKING_MEMORY           = "working_memory";
    final Table<?>     WORKING_MEMORY_TABLE     = DSL.table(DSL.name(WORKING_MEMORY));

    default void alterDeductionTablesForNextPass() {
        create().truncate("last_pass_rules")
                .execute();
        create().execute("ALTER TABLE current_pass_rules RENAME TO temp_last_pass_rules");
        create().execute("ALTER TABLE last_pass_rules RENAME TO current_pass_rules");
        create().execute("ALTER TABLE temp_last_pass_rules RENAME TO last_pass_rules");
        create().truncate(WORKING_MEMORY_TABLE)
                .execute();
    }

    default DSLContext create() {
        return model().create();
    }

    default void createCurrentPassRules() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS current_pass_rules ("
                         + "id uuid NOT NULL," + "parent uuid NOT NULL,"
                         + "relationship uuid NOT NULL,"
                         + "child uuid NOT NULL," + "premise1 uuid NOT NULL,"
                         + "premise2 uuid NOT NULL,"
                         + "inference uuid NOT NULL )");
        create().truncate(CURRENT_PASS_RULES_TABLE)
                .execute();
    }

    default void createDeductionTemporaryTables() {
        createWorkingMemory();
        createCurrentPassRules();
        createLastPassRules();
    }

    default void createLastPassRules() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS last_pass_rules ("
                         + "id uuid NOT NULL, parent uuid NOT NULL,"
                         + "relationship uuid NOT NULL, child uuid NOT NULL,"
                         + "premise1 uuid NOT NULL, premise2 uuid NOT NULL,"
                         + "inference uuid NOT NULL )");
        create().truncate(LAST_PASS_RULES_TABLE)
                .execute();
    }

    default void createWorkingMemory() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS working_memory("
                         + "parent uuid NOT NULL, relationship uuid NOT NULL,"
                         + "child uuid NOT NULL, premise1 uuid NOT NULL,"
                         + "premise2 uuid NOT NULL, inference uuid NOT NULL )");
        create().truncate(WORKING_MEMORY_TABLE)
                .execute();
    }

    // Deduce the new rules
    default void deduce() {
        int deductions = create().insertInto(CURRENT_PASS_RULES_TABLE)
                                 .columns(CurentPassRules.ID,
                                          CurentPassRules.PARENT,
                                          CurentPassRules.RELATIONSHIP,
                                          CurentPassRules.CHILD,
                                          CurentPassRules.PREMISE1,
                                          CurentPassRules.PREMISE2,
                                          CurentPassRules.INFERENCE)
                                 .select(create().select(GENERATE_UUID,
                                                         WorkingMemory.PARENT,
                                                         WorkingMemory.RELATIONSHIP,
                                                         WorkingMemory.CHILD,
                                                         WorkingMemory.PREMISE1,
                                                         WorkingMemory.PREMISE2,
                                                         WorkingMemory.INFERENCE)
                                                 .from(WORKING_MEMORY_TABLE))
                                 .execute();
        if (log.isTraceEnabled()) {
            log.trace(String.format("deduced %s rules", deductions));

        }
    }

    default void generateInverses() {
        long then = System.currentTimeMillis();
        Edge exist = EDGE.as("exist");
        Edge net = EDGE.as("net");
        Existential rel = EXISTENTIAL.as("rel");

        int inverses = create().insertInto(EDGE,
                                           EDGE.ID,
                                           EDGE.PARENT,
                                           EDGE.RELATIONSHIP,
                                           EDGE.CHILD,
                                           EDGE.INFERENCE,
                                           EDGE.PREMISE1,
                                           EDGE.PREMISE2,
                                           EDGE.UPDATED_BY,
                                           EDGE.VERSION)
                               .select(create().select(GENERATE_UUID,
                                                       net.field(EDGE.CHILD),
                                                       rel.field(EXISTENTIAL.INVERSE),
                                                       net.field(EDGE.PARENT),
                                                       net.field(EDGE.INFERENCE),
                                                       net.field(EDGE.PREMISE1),
                                                       net.field(EDGE.PREMISE2),
                                                       DSL.val(model().getCurrentPrincipal()
                                                                      .getPrincipal()
                                                                      .getId()),
                                                       DSL.val(0))
                                               .from(net)
                                               .join(rel)
                                               .on(net.field(EDGE.RELATIONSHIP)
                                                      .equal(rel.field(EXISTENTIAL.ID)))
                                               .leftOuterJoin(exist)
                                               .on(net.field(EDGE.CHILD)
                                                      .equal(exist.field(EDGE.PARENT)))
                                               .and(rel.field(EXISTENTIAL.INVERSE)
                                                       .equal(exist.field(EDGE.RELATIONSHIP)))
                                               .and(net.field(EDGE.PARENT)
                                                       .equal(exist.field(EDGE.CHILD)))
                                               .where(exist.field(EDGE.ID)
                                                           .isNull()))

                               .execute();
        if (log.isTraceEnabled()) {
            log.trace(String.format("created %s inverse rules in %s ms",
                                    inverses,
                                    System.currentTimeMillis() - then));
        }
    }

    default int infer() {
        Edge exist = EDGE.as("exist");
        Edge p1 = EDGE.as("p1");
        Edge p2 = EDGE.as("p2");

        return create().insertInto(WORKING_MEMORY_TABLE, WorkingMemory.PARENT,
                                   WorkingMemory.RELATIONSHIP,
                                   WorkingMemory.CHILD, WorkingMemory.INFERENCE,
                                   WorkingMemory.PREMISE1,
                                   WorkingMemory.PREMISE2)
                       .select(create().select(p1.field(EDGE.PARENT),
                                               NETWORK_INFERENCE.INFERENCE,
                                               p2.field(EDGE.CHILD),
                                               NETWORK_INFERENCE.ID,
                                               p1.field(EDGE.ID),
                                               p2.field(EDGE.ID))
                                       .from(p1)
                                       .join(p2)
                                       .on(p2.field(EDGE.PARENT)
                                             .equal(p1.field(EDGE.CHILD)))
                                       .and(p2.field(EDGE.CHILD)
                                              .notEqual(p1.field(EDGE.PARENT)))
                                       .and(p2.field(p2.INFERENCE)
                                              .isNull())
                                       .join(NETWORK_INFERENCE)
                                       .on(p1.field(EDGE.RELATIONSHIP)
                                             .equal(NETWORK_INFERENCE.PREMISE1))
                                       .and(p2.field(EDGE.RELATIONSHIP)
                                              .equal(NETWORK_INFERENCE.PREMISE2))
                                       .leftOuterJoin(exist)
                                       .on(exist.field(EDGE.PARENT)
                                                .equal(p1.field(EDGE.PARENT)))
                                       .and(exist.field(EDGE.RELATIONSHIP)
                                                 .equal(NETWORK_INFERENCE.INFERENCE))
                                       .and(exist.field(EDGE.CHILD)
                                                 .equal(p2.field(EDGE.CHILD)))
                                       .where(exist.field(EDGE.ID)
                                                   .isNull()))
                       .execute();
    }

    // Infer all possible rules
    default int infer(boolean firstPass) {
        int newRules;
        if (firstPass) {
            newRules = infer();
            firstPass = false;
        } else {
            newRules = inferFromLastPass();
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("inferred %s new rules", newRules));
        }
        return newRules;
    }

    default int inferFromLastPass() {
        Edge exist = EDGE.as("exist");
        Edge p2 = EDGE.as("p2");

        return create().insertInto(WORKING_MEMORY_TABLE, WorkingMemory.PARENT,
                                   WorkingMemory.RELATIONSHIP,
                                   WorkingMemory.CHILD, WorkingMemory.INFERENCE,
                                   WorkingMemory.PREMISE1,
                                   WorkingMemory.PREMISE2)
                       .select(create().select(LastPassRules.PARENT,
                                               NETWORK_INFERENCE.INFERENCE,
                                               p2.field(EDGE.CHILD),
                                               NETWORK_INFERENCE.ID,
                                               LastPassRules.ID,
                                               p2.field(EDGE.ID))
                                       .from(LAST_PASS_RULES_TABLE)
                                       .join(p2)
                                       .on(p2.field(EDGE.PARENT)
                                             .equal(LastPassRules.CHILD))
                                       .and(p2.field(EDGE.CHILD)
                                              .notEqual(LastPassRules.PARENT))
                                       .and(p2.field(EDGE.INFERENCE)
                                              .isNull())
                                       .join(NETWORK_INFERENCE)
                                       .on(LastPassRules.RELATIONSHIP.equal(NETWORK_INFERENCE.PREMISE1))
                                       .and(p2.field(LastPassRules.RELATIONSHIP)
                                              .equal(NETWORK_INFERENCE.PREMISE2))
                                       .leftOuterJoin(exist)
                                       .on(exist.field(LastPassRules.PARENT)
                                                .equal(LastPassRules.PARENT))
                                       .and(exist.field(LastPassRules.RELATIONSHIP)
                                                 .equal(NETWORK_INFERENCE.INFERENCE))
                                       .and(exist.CHILD.equal(p2.field(EDGE.CHILD)))
                                       .where(exist.ID.isNull()))
                       .execute();
    }

    default int insert() {
        return create().insertInto(EDGE, EDGE.ID,
                                   EDGE.PARENT,
                                   EDGE.RELATIONSHIP,
                                   EDGE.CHILD,
                                   EDGE.INFERENCE,
                                   EDGE.PREMISE1,
                                   EDGE.PREMISE2,
                                   EDGE.UPDATED_BY,
                                   EDGE.VERSION)
                       .select(create().select(CurentPassRules.ID,
                                               CurentPassRules.PARENT,
                                               CurentPassRules.RELATIONSHIP,
                                               CurentPassRules.CHILD,
                                               CurentPassRules.INFERENCE,
                                               CurentPassRules.PREMISE1,
                                               CurentPassRules.PREMISE2,
                                               DSL.val(model().getCurrentPrincipal()
                                                              .getPrincipal()
                                                              .getId()),
                                               DSL.val(0))
                                       .from(CURRENT_PASS_RULES_TABLE)
                                       .leftOuterJoin(EDGE)
                                       .on(CurentPassRules.PARENT.equal(EDGE.PARENT))
                                       .and(CurentPassRules.RELATIONSHIP.equal(EDGE.RELATIONSHIP))
                                       .and(CurentPassRules.CHILD.equal(EDGE.CHILD))
                                       .where(EDGE.ID.isNull()))
                       .execute();

    }

    Model model();

    default void propagate() {
        createDeductionTemporaryTables();
        boolean firstPass = true;
        do {
            if (infer(firstPass) == 0) {
                break;
            }
            firstPass = false;
            deduce();
            int inserted = insert();
            log.trace("Inserted: {} deductions", inserted);
            if (inserted == 0) {
                break;
            }
            alterDeductionTablesForNextPass();
        } while (true);
        generateInverses();
    }
}