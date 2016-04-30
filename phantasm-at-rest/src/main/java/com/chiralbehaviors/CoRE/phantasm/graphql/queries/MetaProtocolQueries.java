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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaProtocolTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface MetaProtocolQueries {

    @GraphQLField
    @GraphQLType(MetaProtocolTypeFunction.class)
    default MetaProtocol metaProtocol(@NotNull @GraphQLName("id") String id,
                                      DataFetchingEnvironment env) {
        return MetaProtocol.fetch(env, UUID.fromString(id));
    }

    @GraphQLField
    @GraphQLType(MetaProtocolTypeFunction.class)
    default List<MetaProtocol> metaProtocols(@GraphQLName("ids") List<String> ids,
                                             DataFetchingEnvironment env) {
        if (ids == null) {
            Product workspace = ((WorkspaceContext) env.getContext()).getWorkspace();
            return ctx(env).create()
                           .selectDistinct(Tables.META_PROTOCOL.fields())
                           .from(Tables.META_PROTOCOL)
                           .join(Tables.WORKSPACE_AUTHORIZATION)
                           .on(Tables.WORKSPACE_AUTHORIZATION.ID.eq(Tables.META_PROTOCOL.WORKSPACE))
                           .and(Tables.WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                           .fetch()
                           .into(MetaProtocolRecord.class)
                           .stream()
                           .map(r -> new MetaProtocol(r))
                           .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> MetaProtocol.fetch(env, id))
                  .collect(Collectors.toList());
    }
}
