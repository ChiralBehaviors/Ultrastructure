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
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.FacetTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet.FacetState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet.FacetUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface FacetMutations {

    @GraphQLField
    @GraphQLType(FacetTypeFunction.class)
    default Facet createFacet(FacetState state, DataFetchingEnvironment env) {
        FacetRecord record = ctx(env).records()
                                     .newFacet();
        state.update(record);
        record.insert();
        return new Facet(record);
    }

    @GraphQLField
    default Boolean removeFacet(String id, DataFetchingEnvironment env) {
        Facet.fetch(env, UUID.fromString(id))
             .getRecord()
             .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(FacetTypeFunction.class)
    default Facet updateFacet(FacetUpdateState state,
                              DataFetchingEnvironment env) {
        FacetRecord record = ctx(env).create()
                                     .selectFrom(Tables.FACET)
                                     .where(Tables.FACET.ID.equal(UUID.fromString(state.id)))
                                     .fetchOne();
        state.update(record);
        record.insert();
        return new Facet(record);
    }
}
