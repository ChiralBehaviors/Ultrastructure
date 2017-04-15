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
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Network;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Network.NetworkState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Network.NetworkUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface NetworkMutations {

    @GraphQLField
    default Network createNetwork(@NotNull @GraphQLName("state") NetworkState state,
                                  DataFetchingEnvironment env) {
        ExistentialNetworkRecord record = WorkspaceSchema.ctx(env)
                                                         .records()
                                                         .newExistentialNetwork();
        state.update(record);
        record.insert();
        return new Network(record);
    }

    @GraphQLField
    default Boolean removeNetwork(@NotNull @GraphQLName("id") String id,
                                  DataFetchingEnvironment env) {
        Network.fetch(env, UUID.fromString(id))
               .getRecord()
               .delete();
        return true;
    }

    @GraphQLField
    default Network updateNetwork(@NotNull @GraphQLName("state") NetworkUpdateState state,
                                  DataFetchingEnvironment env) {
        ExistentialNetworkRecord record = WorkspaceSchema.ctx(env)
                                                         .create()
                                                         .selectFrom(Tables.EXISTENTIAL_NETWORK)
                                                         .where(Tables.EXISTENTIAL_NETWORK.ID.equal(UUID.fromString(state.id)))
                                                         .fetchOne();
        state.update(record);
        record.update();
        return new Network(record);
    }
}
