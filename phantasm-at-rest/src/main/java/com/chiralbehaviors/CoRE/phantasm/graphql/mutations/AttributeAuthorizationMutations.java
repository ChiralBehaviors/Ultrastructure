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
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization.AttributeAuthorizationState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization.AttributeAuthorizationUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface AttributeAuthorizationMutations {

    @GraphQLField
    default AttributeAuthorization createAttributeAuthorization(@NotNull @GraphQLName("state") AttributeAuthorizationState state,
                                                                DataFetchingEnvironment env) {
        ExistentialAttributeAuthorizationRecord record = WorkspaceSchema.ctx(env)
                                                                        .records()
                                                                        .newExistentialAttributeAuthorization();
        state.update(record);
        record.insert();
        return new AttributeAuthorization(record);
    }

    @GraphQLField
    default Boolean removeAttributeAuthorization(@NotNull String id,
                                                 DataFetchingEnvironment env) {
        AttributeAuthorization.fetch(env, UUID.fromString(id))
                              .delete();
        return true;
    }

    @GraphQLField
    default AttributeAuthorization updateAttributeAuthorization(@NotNull @GraphQLName("state") AttributeAuthorizationUpdateState state,
                                                                DataFetchingEnvironment env) {
        ExistentialAttributeAuthorizationRecord record = WorkspaceSchema.ctx(env)
                                                                        .create()
                                                                        .selectFrom(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                                        .where(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.equal(UUID.fromString(state.id)))
                                                                        .fetchOne();
        state.update(record);
        record.insert();
        return new AttributeAuthorization(record);
    }
}
