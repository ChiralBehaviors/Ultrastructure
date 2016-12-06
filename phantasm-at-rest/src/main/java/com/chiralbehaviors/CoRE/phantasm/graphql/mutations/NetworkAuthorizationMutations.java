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

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext.getWorkspace;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization.NetworkAuthorizationUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization.NetwworkAuthorizationState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface NetworkAuthorizationMutations {

    @GraphQLField
    default NetworkAuthorization createNetworkAuthorization(@NotNull @GraphQLName("state") NetwworkAuthorizationState state,
                                                            DataFetchingEnvironment env) {
        if (!WorkspaceSchema.ctx(env)
                            .checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialNetworkAuthorizationRecord record = WorkspaceSchema.ctx(env)
                                                                      .records()
                                                                      .newExistentialNetworkAuthorization();
        state.update(record);
        record.insert();
        return new NetworkAuthorization(record);
    }

    @GraphQLField
    default Boolean removeNetworkAuthorization(@NotNull @GraphQLName("id") String id,
                                               DataFetchingEnvironment env) {
        ExistentialNetworkAuthorizationRecord removed = NetworkAuthorization.fetch(env,
                                                                                   UUID.fromString(id))
                                                                            .getRecord();
        if (!WorkspaceSchema.ctx(env)
                            .checkDelete(removed)) {
            return null;
        }
        removed.delete();
        return true;
    }

    @GraphQLField
    default NetworkAuthorization updateNetworkAuthorization(@NotNull @GraphQLName("state") NetworkAuthorizationUpdateState state,
                                                            DataFetchingEnvironment env) {
        ExistentialNetworkAuthorizationRecord record = WorkspaceSchema.ctx(env)
                                                                      .create()
                                                                      .selectFrom(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                      .where(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(UUID.fromString(state.id)))
                                                                      .fetchOne();
        state.update(record);
        record.update();
        return new NetworkAuthorization(record);
    }
}
