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
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.SelfSequencingTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SelfSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SelfSequencing.SelfSequencingState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SelfSequencing.SelfSequencingUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface SelfSequencingMutations {

    @GraphQLField
    @GraphQLType(SelfSequencingTypeFunction.class)
    default SelfSequencing createAttributeAuthorization(@NotNull @GraphQLName("state") SelfSequencingState state,
                                                        DataFetchingEnvironment env) {
        SelfSequencingAuthorizationRecord record = ctx(env).records()
                                                           .newSelfSequencingAuthorization();
        state.update(record);
        record.insert();
        return new SelfSequencing(record);
    }

    @GraphQLField
    default Boolean removeSelfSequencing(@NotNull @GraphQLName("id") String id,
                                         DataFetchingEnvironment env) {
        SelfSequencing.fetch(env, UUID.fromString(id))
                      .getRecord()
                      .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(SelfSequencingTypeFunction.class)
    default SelfSequencing updateSelfSequencing(@NotNull @GraphQLName("state") SelfSequencingUpdateState state,
                                                DataFetchingEnvironment env) {
        SelfSequencingAuthorizationRecord record = ctx(env).create()
                                                           .selectFrom(Tables.SELF_SEQUENCING_AUTHORIZATION)
                                                           .where(Tables.SELF_SEQUENCING_AUTHORIZATION.ID.equal(UUID.fromString(state.id)))
                                                           .fetchOne();
        state.update(record);
        record.insert();
        return new SelfSequencing(record);
    }
}
