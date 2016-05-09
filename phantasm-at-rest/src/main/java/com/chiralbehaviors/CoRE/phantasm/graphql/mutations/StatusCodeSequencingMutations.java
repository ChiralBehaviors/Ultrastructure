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

package com.chiralbehaviors.CoRE.phantasm.graphql.mutations;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.StatusCodeSequencing.StatusCodeSequencingState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.StatusCodeSequencing.StatusCodeSequencingUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface StatusCodeSequencingMutations {

    @GraphQLField
    default StatusCodeSequencing createStatusCodeSequencing(@NotNull @GraphQLName("state") StatusCodeSequencingState state,
                                                            DataFetchingEnvironment env) {
        StatusCodeSequencingRecord record = WorkspaceSchema.ctx(env)
                                                           .records()
                                                           .newStatusCodeSequencing();
        state.update(record);
        record.insert();
        return new StatusCodeSequencing(record);
    }

    @GraphQLField
    default Boolean removeStatusCodeSequencing(@NotNull String id,
                                               DataFetchingEnvironment env) {
        StatusCodeSequencing.fetch(env, UUID.fromString(id))
                            .getRecord()
                            .delete();
        return true;
    }

    @GraphQLField
    default StatusCodeSequencing updateStatusCodeSequencing(@NotNull @GraphQLName("state") StatusCodeSequencingUpdateState state,
                                                            DataFetchingEnvironment env) {
        StatusCodeSequencingRecord record = WorkspaceSchema.ctx(env)
                                                           .create()
                                                           .selectFrom(Tables.STATUS_CODE_SEQUENCING)
                                                           .where(Tables.STATUS_CODE_SEQUENCING.ID.equal(UUID.fromString(state.id)))
                                                           .fetchOne();
        state.update(record);
        record.update();
        return new StatusCodeSequencing(record);
    }
}
