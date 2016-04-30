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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ProtocolTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Protocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Protocol.ProtocolState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Protocol.ProtocolUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ProtocolMutations {

    @GraphQLField
    @GraphQLType(ProtocolTypeFunction.class)
    default Protocol createProtocol(@NotNull @GraphQLName("state") ProtocolState state,
                                    DataFetchingEnvironment env) {
        ProtocolRecord record = ctx(env).records()
                                        .newProtocol();
        state.update(record);
        record.insert();
        return new Protocol(record);
    }

    @GraphQLField
    default Boolean removeProtocol(@NotNull @GraphQLName("id") String id,
                                   DataFetchingEnvironment env) {
        Protocol.fetch(env, UUID.fromString(id))
                .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(ProtocolTypeFunction.class)
    default Protocol updateProtocol(@NotNull @GraphQLName("state") ProtocolUpdateState state,
                                    DataFetchingEnvironment env) {
        ProtocolRecord record = ctx(env).create()
                                        .selectFrom(Tables.PROTOCOL)
                                        .where(Tables.PROTOCOL.ID.equal(UUID.fromString(state.id)))
                                        .fetchOne();
        state.update(record);
        record.insert();
        return new Protocol(record);
    }
}
