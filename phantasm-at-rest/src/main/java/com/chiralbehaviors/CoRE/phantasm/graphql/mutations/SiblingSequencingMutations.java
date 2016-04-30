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

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.SiblingSequencingTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing.SiblingSequencingState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing.SiblingSequencingUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface SiblingSequencingMutations {

    @GraphQLField
    @GraphQLType(SiblingSequencingTypeFunction.class)
    default SiblingSequencing createAttributeAuthorization(SiblingSequencingState state,
                                                           DataFetchingEnvironment env) {
        SiblingSequencingAuthorizationRecord record = ctx(env).records()
                                                              .newSiblingSequencingAuthorization();
        state.update(record);
        record.insert();
        return new SiblingSequencing(record);
    }

    @GraphQLField
    default Boolean removeSiblingSequencing(String id,
                                            DataFetchingEnvironment env) {
        SiblingSequencing.fetch(env, UUID.fromString(id))
                         .getRecord()
                         .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(SiblingSequencingTypeFunction.class)
    default SiblingSequencing updateSiblingSequencing(SiblingSequencingUpdateState state,
                                                      DataFetchingEnvironment env) {
        SiblingSequencingAuthorizationRecord record = ctx(env).create()
                                                              .selectFrom(Tables.SIBLING_SEQUENCING_AUTHORIZATION)
                                                              .where(Tables.SIBLING_SEQUENCING_AUTHORIZATION.ID.equal(UUID.fromString(state.id)))
                                                              .fetchOne();
        state.update(record);
        record.insert();
        return new SiblingSequencing(record);
    }
}
