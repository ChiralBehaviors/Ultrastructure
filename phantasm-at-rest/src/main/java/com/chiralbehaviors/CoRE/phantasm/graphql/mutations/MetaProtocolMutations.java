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
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaProtocolTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol.MetaProtocolState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol.MetaProtocolUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface MetaProtocolMutations {

    @GraphQLField
    @GraphQLType(MetaProtocolTypeFunction.class)
    default MetaProtocol createMetaProtocol(@NotNull @GraphQLName("state") MetaProtocolState state,
                                            DataFetchingEnvironment env) {
        MetaProtocolRecord record = ctx(env).records()
                                            .newMetaProtocol();
        state.update(record);
        record.insert();
        return new MetaProtocol(record);
    }

    @GraphQLField
    default Boolean removeMetaProtocol(@NotNull @GraphQLName("id") String id,
                                       DataFetchingEnvironment env) {
        MetaProtocol.fetch(env, UUID.fromString(id))
                    .getRecord()
                    .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(MetaProtocolTypeFunction.class)
    default MetaProtocol updateMetaProtocol(@NotNull @GraphQLName("state") MetaProtocolUpdateState state,
                                            DataFetchingEnvironment env) {
        MetaProtocolRecord record = ctx(env).create()
                                            .selectFrom(Tables.META_PROTOCOL)
                                            .where(Tables.META_PROTOCOL.ID.equal(UUID.fromString(state.id)))
                                            .fetchOne();
        state.update(record);
        record.insert();
        return new MetaProtocol(record);
    }
}
