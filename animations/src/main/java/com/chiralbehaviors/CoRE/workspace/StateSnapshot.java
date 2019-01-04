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

import static com.chiralbehaviors.CoRE.jooq.Tables.AUTHENTICATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.TOKEN;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_LABEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.jooq.Ruleform;

/**
 * Every category must have its null
 * 
 * @author hhildebrand
 *
 */
public class StateSnapshot extends WorkspaceSnapshot {

    @SuppressWarnings("unchecked")
    public static List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> selectNullClosure(DSLContext create,
                                                                                                                   Collection<UUID> exlude) {
        List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> records = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .stream()
                         .filter(t -> !t.equals(TOKEN))
                         .filter(t -> !t.equals(AUTHENTICATION))
                         .filter(t -> !t.equals(WORKSPACE_LABEL))
                         .forEach(t -> {
                             records.addAll(create.selectDistinct(t.fields())
                                                  .from(t)
                                                  .leftJoin(WORKSPACE_LABEL)
                                                  .on(((Field<UUID>) t.field("id")).equal(WORKSPACE_LABEL.REFERENCE))
                                                  .where(WORKSPACE_LABEL.WORKSPACE.isNull())
                                                  .and(t.field("id")
                                                        .notIn(exlude))
                                                  .fetchInto(t.getRecordType())
                                                  .stream()
                                                  .map(r -> (UpdatableRecord<?>) r)
                                                  .collect(Collectors.toList()));
                         });
        return records;
    }

    public StateSnapshot() {
    }

    public StateSnapshot(DSLContext create, Collection<UUID> exlude) {
        inserts = selectNullClosure(create, exlude);
    }

    @Override
    protected void loadDefiningProduct(DSLContext create) {
        // the void
    }
}
