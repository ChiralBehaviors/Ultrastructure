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

import static com.chiralbehaviors.CoRE.jooq.Tables.ALL_NETWORK_INFERENCES;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.NETWORK_INFERENCE;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.lateral;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.DSL.value;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.jooq.tables.Edge;
import com.chiralbehaviors.CoRE.jooq.tables.Existential;
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
        Field<UUID> CHILD        = field(name(CURRENT_PASS_RULES, "child"),
                                         UUID.class);
        Field<UUID> ID           = field(name(CURRENT_PASS_RULES, "id"),
                                         UUID.class);
        Field<UUID> PARENT       = field(name(CURRENT_PASS_RULES, "parent"),
                                         UUID.class);
        Field<UUID> RELATIONSHIP = field(name(CURRENT_PASS_RULES,
                                              "relationship"),
                                         UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(ID, PARENT, RELATIONSHIP, CHILD);
        }
    }

    interface LastPassRules {
        Field<UUID> CHILD        = field(name(LAST_PASS_RULES, "child"),
                                         UUID.class);
        Field<UUID> ID           = field(name(LAST_PASS_RULES, "id"),
                                         UUID.class);
        Field<UUID> PARENT       = field(name(LAST_PASS_RULES, "parent"),
                                         UUID.class);
        Field<UUID> RELATIONSHIP = field(name(LAST_PASS_RULES, "relationship"),
                                         UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(ID, PARENT, RELATIONSHIP, CHILD);
        }
    }

    interface WorkingMemory {
        Field<UUID> CHILD        = field(name(WORKING_MEMORY, "child"),
                                         UUID.class);
        Field<UUID> PARENT       = field(name(WORKING_MEMORY, "parent"),
                                         UUID.class);
        Field<UUID> RELATIONSHIP = field(name(WORKING_MEMORY, "relationship"),
                                         UUID.class);

        static List<Field<?>> fields() {
            return Arrays.asList(PARENT, RELATIONSHIP, CHILD);
        }
    }

    final Field<UUID>   CHILD                    = field(name("child"),
                                                         UUID.class);
    final String        CURRENT_PASS_RULES       = "current_pass_rules";
    final Table<?>      CURRENT_PASS_RULES_TABLE = table(name(CURRENT_PASS_RULES));
    static Field<UUID>  GENERATE_UUID            = field("uuid_generate_v1mc()",
                                                         UUID.class);
    final String        INFERENCES               = "inferences";
    final Table<Record> INFERENCES_TABLE         = table(name(INFERENCES));
    final String        LAST_PASS_RULES          = "last_pass_rules";
    final Table<?>      LAST_PASS_RULES_TABLE    = table(name(LAST_PASS_RULES));
    static Logger       log                      = LoggerFactory.getLogger(Inference.class);
    static int          MAX_DEDUCTIONS           = 1000;
    final Field<UUID>   PARENT                   = field(name("parent"),
                                                         UUID.class);
    final Field<UUID>   RELATIONSHIP             = field(name("relationship"),
                                                         UUID.class);
    final String        TARGET                   = "target";
    final Table<Record> TARGET_TABLE             = table(name(TARGET));
    final String        WORKING_MEMORY           = "working_memory";
    final Table<?>      WORKING_MEMORY_TABLE     = table(name(WORKING_MEMORY));

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
                         + "child uuid NOT NULL )");
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
                         + "relationship uuid NOT NULL, child uuid NOT NULL )");
        create().truncate(LAST_PASS_RULES_TABLE)
                .execute();
    }

    default void createWorkingMemory() {
        create().execute("CREATE TEMPORARY TABLE IF NOT EXISTS working_memory("
                         + "parent uuid NOT NULL, relationship uuid NOT NULL,"
                         + "child uuid NOT NULL)");
        create().truncate(WORKING_MEMORY_TABLE)
                .execute();
    }

    // Deduce the new rules
    default void deduce() {
        int deductions = create().insertInto(CURRENT_PASS_RULES_TABLE)
                                 .columns(CurentPassRules.ID,
                                          CurentPassRules.PARENT,
                                          CurentPassRules.RELATIONSHIP,
                                          CurentPassRules.CHILD)
                                 .select(create().select(GENERATE_UUID,
                                                         WorkingMemory.PARENT,
                                                         WorkingMemory.RELATIONSHIP,
                                                         WorkingMemory.CHILD)
                                                 .from(WORKING_MEMORY_TABLE))
                                 .execute();
        if (log.isTraceEnabled()) {
            log.trace(String.format("deduced %s rules", deductions));

        }
    }

    default boolean dynamicInference(UUID parent, UUID relationship,
                                     UUID child) {
        RecordsFactory records = model().records();
        records.existentialName(child);

        SelectWhereStep<Record> inferenceQuery;
        inferenceQuery = create().withRecursive(TARGET_TABLE.getName(),
                                                PARENT.getName(),
                                                RELATIONSHIP.getName(),
                                                CHILD.getName())
                                 .as(target(parent, relationship, child))
                                 .with(INFERENCES_TABLE.getName(),
                                       PARENT.getName(), RELATIONSHIP.getName(),
                                       CHILD.getName())
                                 .as(terminalInference().union(recursiveInferences()))
                                 .selectFrom(INFERENCES_TABLE);

//        System.out.println();
//        inferenceQuery.fetch()
//                      .stream()
//                      .map(r -> {
//                          return records.existentialName((UUID) r.get(0))
//                                 + " -> "
//                                 + records.existentialName((UUID) r.get(1))
//                                 + " -> "
//                                 + records.existentialName((UUID) r.get(2));
//                      })
//                      .forEach(row -> System.out.println(row));

        return create().fetchExists(inferenceQuery.where(PARENT.eq(parent)
                                                               .and(RELATIONSHIP.eq(relationship))
                                                               .and(CHILD.eq(child))));
    }

    default void generateInverses() {
        long then = System.currentTimeMillis();
        Edge exist = EDGE.as("exist");
        Edge net = EDGE.as("net");
        Existential rel = EXISTENTIAL.as("rel");

        int inverses = create().insertInto(EDGE, EDGE.ID, EDGE.PARENT,
                                           EDGE.RELATIONSHIP, EDGE.CHILD,
                                           EDGE.UPDATED_BY, EDGE.VERSION)
                               .select(create().select(GENERATE_UUID,
                                                       net.field(EDGE.CHILD),
                                                       rel.field(EXISTENTIAL.INVERSE),
                                                       net.field(EDGE.PARENT),
                                                       val(model().getCurrentPrincipal()
                                                                  .getPrincipal()
                                                                  .getId()),
                                                       val(0))
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
                                   WorkingMemory.CHILD)
                       .select(create().select(p1.field(EDGE.PARENT),
                                               NETWORK_INFERENCE.INFERENCE,
                                               p2.field(EDGE.CHILD))
                                       .from(p1)
                                       .join(p2)
                                       .on(p2.field(EDGE.PARENT)
                                             .equal(p1.field(EDGE.CHILD)))
                                       .and(p2.field(EDGE.CHILD)
                                              .notEqual(p1.field(EDGE.PARENT)))
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
                                   WorkingMemory.CHILD)
                       .select(create().select(LastPassRules.PARENT,
                                               NETWORK_INFERENCE.INFERENCE,
                                               p2.field(EDGE.CHILD))
                                       .from(LAST_PASS_RULES_TABLE)
                                       .join(p2)
                                       .on(p2.field(EDGE.PARENT)
                                             .equal(LastPassRules.CHILD))
                                       .and(p2.field(EDGE.CHILD)
                                              .notEqual(LastPassRules.PARENT))
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
        return create().insertInto(EDGE, EDGE.ID, EDGE.PARENT,
                                   EDGE.RELATIONSHIP, EDGE.CHILD,
                                   EDGE.UPDATED_BY, EDGE.VERSION)
                       .select(create().select(CurentPassRules.ID,
                                               CurentPassRules.PARENT,
                                               CurentPassRules.RELATIONSHIP,
                                               CurentPassRules.CHILD,
                                               val(model().getCurrentPrincipal()
                                                          .getPrincipal()
                                                          .getId()),
                                               val(0))
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

    default SelectJoinStep<Record3<UUID, UUID, UUID>> recursiveInferences() {
        Table<Record> target = TARGET_TABLE.as("target");
        Field<UUID> targetParent = field(name(target.getName(), "parent"),
                                         UUID.class);
        Table<Record> backtrack = INFERENCES_TABLE.as("inferred");
        Field<UUID> backtrackParent = field(name(backtrack.getName(), "parent"),
                                            UUID.class);
        Field<UUID> backtrackRelationship = field(name(backtrack.getName(),
                                                       "relationship"),
                                                  UUID.class);
        Field<UUID> backtrackChild = field(name(backtrack.getName(), "child"),
                                           UUID.class);
        Field<UUID> p = field(name("p"), UUID.class);
        Field<UUID> r = field(name("r"), UUID.class);
        Field<UUID> c = field(name("c"), UUID.class);
        Edge graph = EDGE.as("graph");

        SelectConditionStep<Record3<UUID, UUID, UUID>> infer;

        infer = create().select(graph.field(EDGE.PARENT)
                                     .as(p),
                                ALL_NETWORK_INFERENCES.INFERENCE.as(r),
                                backtrackChild.as(c))
                        .from(target)
                        .join(graph)
                        .on(graph.field(EDGE.PARENT)
                                 .notEqual(backtrackChild)
                                 .and(graph.field(EDGE.RELATIONSHIP)
                                           .equal(ALL_NETWORK_INFERENCES.PREMISE1))
                                 .and(graph.field(EDGE.CHILD)
                                           .notEqual(targetParent))
                                 .and(graph.field(EDGE.CHILD)
                                           .eq(backtrackParent)))
                        .where(backtrackChild.notEqual(targetParent)
                                             .and(backtrackRelationship.equal(ALL_NETWORK_INFERENCES.PREMISE2)));

        SelectConditionStep<Record3<UUID, UUID, UUID>> infer2;
        infer2 = create().select(graph.field(EDGE.PARENT)
                                      .as(p),
                                 ALL_NETWORK_INFERENCES.PREMISE1.as(r),
                                 backtrackParent.as(c))
                         .from(target)
                         .join(graph)
                         .on(graph.field(EDGE.PARENT)
                                  .notEqual(backtrackChild)
                                  .and(graph.field(EDGE.RELATIONSHIP)
                                            .equal(ALL_NETWORK_INFERENCES.PREMISE1))
                                  .and(graph.field(EDGE.CHILD)
                                            .eq(backtrackParent))
                                  .and(graph.field(EDGE.CHILD)
                                            .notEqual(targetParent)))
                         .where(backtrackChild.notEqual(targetParent)
                                              .and(backtrackRelationship.equal(ALL_NETWORK_INFERENCES.PREMISE2)));

        SelectConditionStep<Record3<UUID, UUID, UUID>> deduce;
        deduce = create().select(backtrackParent.as(p),
                                 ALL_NETWORK_INFERENCES.INFERENCE.as(r),
                                 graph.field(EDGE.CHILD)
                                      .as(c))
                         .from(target)
                         .join(graph)
                         .on(graph.field(EDGE.PARENT)
                                  .eq(backtrackChild)
                                  .and(graph.field(EDGE.RELATIONSHIP)
                                            .equal(ALL_NETWORK_INFERENCES.PREMISE2))
                                  .and(graph.field(EDGE.CHILD)
                                            .notEqual(targetParent))
                                  .and(graph.field(EDGE.CHILD)
                                            .notEqual(backtrackChild)))
                         .where(backtrackChild.notEqual(targetParent))
                         .and(backtrackRelationship.equal(ALL_NETWORK_INFERENCES.PREMISE1));

        return create().select(p, r, c)
                       .from(backtrack, ALL_NETWORK_INFERENCES,
                             lateral(infer.union(infer2)
                                          .union(deduce)));
    }

    default Select<Record3<UUID, UUID, UUID>> target(UUID parent,
                                                     UUID relationship,
                                                     UUID child) {
        return create().select(value(parent), value(relationship),
                               value(child));
    }

    default Select<Record3<UUID, UUID, UUID>> terminalInference() {
        Table<Record> target = TARGET_TABLE.as("target");
        Field<UUID> targetRelationship = field(name(target.getName(),
                                                    "relationship"),
                                               UUID.class);
        Field<UUID> targetChild = field(name(target.getName(), "child"),
                                        UUID.class);
        Edge p1 = EDGE.as("p1");
        return create().select(p1.field(EDGE.PARENT), targetRelationship,
                               targetChild)
                       .from(p1, target)
                       .where(p1.field(EDGE.RELATIONSHIP)
                                .eq(targetRelationship))
                       .and(p1.field(EDGE.CHILD)
                              .eq(targetChild));
    }
}