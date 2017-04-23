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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceLabel {

    public static class WorkspaceLabelState {
        @GraphQLField
        public String key;
        @GraphQLField
        public String notes;

        public void update(WorkspaceLabelRecord record) {
            if (key != null) {
                record.setKey(key);
            }
            if (notes != null) {
                record.setNotes(notes);
            }
        }
    }

    public static class WorkspaceLabelUpdateState extends WorkspaceLabelState {
        @GraphQLField
        public String id;
    }

    public static WorkspaceLabel fetch(DataFetchingEnvironment env, UUID id) {
        return new WorkspaceLabel(WorkspaceSchema.ctx(env)
                                                 .create()
                                                 .selectFrom(Tables.WORKSPACE_LABEL)
                                                 .where(Tables.WORKSPACE_LABEL.ID.equal(id))
                                                 .fetchOne());
    }

    private final WorkspaceLabelRecord record;

    public WorkspaceLabel(WorkspaceLabelRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    public WorkspaceLabelRecord getRecord() {
        return record;
    }

    @GraphQLField
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(WorkspaceSchema.ctx(env)
                                         .records()
                                         .resolve(record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }
}
