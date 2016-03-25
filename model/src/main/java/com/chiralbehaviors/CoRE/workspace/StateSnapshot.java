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

import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import com.chiralbehaviors.CoRE.jooq.Ruleform;

/**
 * Every category must have its null
 * 
 * @author hhildebrand
 *
 */
public class StateSnapshot extends WorkspaceSnapshot {

    public StateSnapshot() {
    }

    public StateSnapshot(DSLContext create, Collection<UUID> exlude) {
        records = new ArrayList<>();
        loadFromDb(create, exlude);
    }

    private void loadFromDb(DSLContext create, Collection<UUID> exlude) {
        Ruleform.RULEFORM.getTables()
                         .forEach(t -> {
                             if (!t.equals(WORKSPACE_AUTHORIZATION)) {
                                 records.addAll(create.selectDistinct(t.fields())
                                                      .from(t)
                                                      .where(t.field("workspace")
                                                              .isNull())
                                                      .and(t.field("id")
                                                            .notIn(exlude))
                                                      .fetchInto(t.getRecordType())
                                                      .stream()
                                                      .map(r -> (TableRecord<?>) r)
                                                      .collect(Collectors.toList()));
                             }
                         });
    }

    @Override
    protected void loadDefiningProduct(DSLContext create) {
        // the void
    }
}
