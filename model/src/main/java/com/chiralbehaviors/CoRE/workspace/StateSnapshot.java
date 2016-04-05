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

package com.chiralbehaviors.CoRE.workspace;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;

/**
 * Every category must have its null
 * 
 * @author hhildebrand
 *
 */
public class StateSnapshot extends WorkspaceSnapshot {

    public static List<UpdatableRecord<? extends UpdatableRecord<?>>> selectNullClosure(DSLContext create,
                                                                                        Collection<UUID> exlude) {
        List<UpdatableRecord<? extends UpdatableRecord<?>>> records = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .forEach(t -> {
                             if (t.equals(EXISTENTIAL_NETWORK)) {
                                 // Snapshots do not contain network inferences
                                 records.addAll(create.selectFrom(EXISTENTIAL_NETWORK)
                                                      .where(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                                      .and(EXISTENTIAL_NETWORK.WORKSPACE.isNull())
                                                      .and(EXISTENTIAL_NETWORK.ID.notIn(exlude))
                                                      .fetchInto(ExistentialNetworkRecord.class)
                                                      .stream()
                                                      .collect(Collectors.toList()));
                             } else if (!t.equals(WORKSPACE_AUTHORIZATION)) {
                                 records.addAll(create.selectDistinct(t.fields())
                                                      .from(t)
                                                      .where(t.field("workspace")
                                                              .isNull())
                                                      .and(t.field("id")
                                                            .notIn(exlude))
                                                      .fetchInto(t.getRecordType())
                                                      .stream()
                                                      .map(r -> (UpdatableRecord<?>) r)
                                                      .collect(Collectors.toList()));
                             }
                         });
        return records;
    }

    public StateSnapshot() {
    }

    public StateSnapshot(DSLContext create, Collection<UUID> exlude) {
        records = selectNullClosure(create, exlude);
    }

    @Override
    protected void loadDefiningProduct(DSLContext create) {
        // the void
    }
}
