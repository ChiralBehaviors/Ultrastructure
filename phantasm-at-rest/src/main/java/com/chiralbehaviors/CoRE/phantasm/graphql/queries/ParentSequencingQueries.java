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

package com.chiralbehaviors.CoRE.phantasm.graphql.queries;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ParentSequencing;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface ParentSequencingQueries {

    @GraphQLField
    default ParentSequencing parentSequencing(@NotNull @GraphQLName("id") String id,
                                              DataFetchingEnvironment env) {
        return ParentSequencing.fetch(env, UUID.fromString(id));
    }

    @GraphQLField
    default List<ParentSequencing> parentSequencings(@GraphQLName("ids") List<String> ids,
                                                     DataFetchingEnvironment env) {
        if (ids == null) {
            Product workspace = ((WorkspaceContext) env.getContext()).getWorkspace();
            return WorkspaceSchema.ctx(env)
                                  .create()
                                  .selectDistinct(Tables.PARENT_SEQUENCING_AUTHORIZATION.fields())
                                  .from(Tables.PARENT_SEQUENCING_AUTHORIZATION)
                                  .join(Tables.WORKSPACE_AUTHORIZATION)
                                  .on(Tables.WORKSPACE_AUTHORIZATION.ID.eq(Tables.PARENT_SEQUENCING_AUTHORIZATION.WORKSPACE))
                                  .and(Tables.WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                                  .fetch()
                                  .into(ParentSequencingAuthorizationRecord.class)
                                  .stream()
                                  .map(r -> new ParentSequencing(r))
                                  .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> ParentSequencing.fetch(env, id))
                  .collect(Collectors.toList());
    }
}
