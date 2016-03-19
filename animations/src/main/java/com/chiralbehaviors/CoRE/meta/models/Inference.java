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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Network inference logic
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
    static Field<UUID> GENERATE_UUID            = DSL.field("uuid_generate_v1mc",
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

    DSLContext create();

    default void createCurrentPassRules() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS current_pass_rules ("
                         + "id uuid NOT NULL," + "parent uuid NOT NULL,"
                         + "relationship uuid NOT NULL,"
                         + "child uuid NOT NULL," + "premise1 uuid NOT NULL,"
                         + "premise2 uuid NOT NULL,"
                         + "inference uuid NOT NULL )");
    }

    default void createDeductionTemporaryTables(boolean initial) {
        if (initial) {
            createWorkingMemory();
            createCurrentPassRules();
            createLastPassRules();
        }
        create().truncate(WORKING_MEMORY_TABLE)
                .execute();
        create().truncate(CURRENT_PASS_RULES_TABLE)
                .execute();
        create().truncate(LAST_PASS_RULES_TABLE)
                .execute();
    }

    default void createLastPassRules() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS last_pass_rules ("
                         + "id uuid NOT NULL, parent uuid NOT NULL,"
                         + "relationship uuid NOT NULL, child uuid NOT NULL,"
                         + "premise1 uuid NOT NULL, premise2 uuid NOT NULL,"
                         + "inference uuid NOT NULL )");
    }

    default void createWorkingMemory() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS working_memory("
                         + "parent uuid NOT NULL, relationship uuid NOT NULL,"
                         + "child uuid NOT NULL, premise1 uuid NOT NULL,"
                         + "premise2 uuid NOT NULL, inference uuid NOT NULL )");
    }

    // Deduce the new rules
    default void deduce() {

        int deductions = create().insertInto(CURRENT_PASS_RULES_TABLE)
                                 .columns(CurentPassRules.fields())
                                 .select(create().select(GENERATE_UUID,
                                                         WorkingMemory.PARENT,
                                                         WorkingMemory.RELATIONSHIP,
                                                         WorkingMemory.CHILD,
                                                         WorkingMemory.PREMISE1,
                                                         WorkingMemory.PREMISE2,
                                                         WorkingMemory.INFERENCE))
                                 .execute();
        if (log.isTraceEnabled()) {
            log.trace(String.format("deduced %s rules", deductions));

        }
    }

    ExistentialDomain domain();

    default void generateInverses() {
        long then = System.currentTimeMillis();
        int inverses = 0;
        if (log.isTraceEnabled()) {
            log.trace(String.format("created %s inverse rules of %s in %s ms",
                                    inverses, domain(),
                                    System.currentTimeMillis() - then));
        }
    }

    default int infer() {
        return 0;
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
        return 0;
    }

    Model model();

    default int insert() {
        Field<UUID> exist = EXISTENTIAL_NETWORK.PARENT.as("exist");
        return create().insertInto(EXISTENTIAL_NETWORK, exist,
                                   EXISTENTIAL_NETWORK.RELATIONSHIP,
                                   EXISTENTIAL_NETWORK.CHILD,
                                   EXISTENTIAL_NETWORK.INFERENCE,
                                   EXISTENTIAL_NETWORK.PREMISE1,
                                   EXISTENTIAL_NETWORK.PREMISE2,
                                   EXISTENTIAL_NETWORK.UPDATED_BY,
                                   EXISTENTIAL_NETWORK.VERSION)
                       .select((create().select(CurentPassRules.PARENT,
                                                CurentPassRules.RELATIONSHIP,
                                                CurentPassRules.CHILD,
                                                CurentPassRules.INFERENCE,
                                                CurentPassRules.PREMISE1,
                                                CurentPassRules.PREMISE2,
                                                DSL.val(model().getCurrentPrincipal()
                                                               .getPrincipal()
                                                               .getId())
                                                   .as((EXISTENTIAL_NETWORK.UPDATED_BY)),
                                                DSL.val(0))).from(CURRENT_PASS_RULES_TABLE)
                                                            .leftOuterJoin(EXISTENTIAL_NETWORK)
                                                            .on(CurentPassRules.PARENT.equal(EXISTENTIAL_NETWORK.PARENT))
                                                            .where(EXISTENTIAL_NETWORK.ID.isNull())
                                                            .and(CurentPassRules.RELATIONSHIP.equal(CurentPassRules.RELATIONSHIP))
                                                            .and(CurentPassRules.CHILD.equal(CurentPassRules.CHILD)))
                       .execute();
    }

    default void propagate(boolean initial) {
        createDeductionTemporaryTables(initial);
        boolean firstPass = true;
        do {
            if (infer(firstPass) == 0) {
                break;
            }
            firstPass = false;
            deduce();
            if (insert() == 0) {
                break;
            }
            alterDeductionTablesForNextPass();
        } while (true);
        generateInverses();
    }

}