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
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.AttributeAuthorizationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface AttributeAuthorizationQueries {

    @GraphQLField
    @GraphQLType(AttributeAuthorizationTypeFunction.class)
    default AttributeAuthorization attributeAuthorization(@NotNull @GraphQLName("id") String id,
                                                          DataFetchingEnvironment env) {
        return new AttributeAuthorization(AttributeAuthorization.fetch(env,
                                                                       UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(AttributeAuthorizationTypeFunction.class)
    default List<AttributeAuthorization> attributeAuthorizations(@GraphQLName("ids") List<String> ids,
                                                                 DataFetchingEnvironment env) {
        if (ids == null) {
            Product workspace = ((WorkspaceContext) env.getContext()).getWorkspace();
            return ctx(env).create()
                           .selectDistinct(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                           .from(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                           .join(Tables.WORKSPACE_AUTHORIZATION)
                           .on(Tables.WORKSPACE_AUTHORIZATION.ID.eq(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.WORKSPACE))
                           .and(Tables.WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                           .fetch()
                           .into(ExistentialAttributeAuthorizationRecord.class)
                           .stream()
                           .map(r -> new AttributeAuthorization(r))
                           .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> AttributeAuthorization.fetch(env, id))
                  .map(r -> new AttributeAuthorization(r))
                  .collect(Collectors.toList());
    }
}
