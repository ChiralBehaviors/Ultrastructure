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

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialQueries {

    @GraphQLField
    List<Agency> agencies(@GraphQLName("ids") List<UUID> ids,
                          DataFetchingEnvironment env);

    @GraphQLField
    Agency agency(@NotNull @GraphQLName("id") UUID id,
                  DataFetchingEnvironment env);

    @GraphQLField
    Attribute attribute(@NotNull @GraphQLName("id") UUID id,
                        DataFetchingEnvironment env);

    @GraphQLField
    List<Attribute> attributes(@GraphQLName("ids") List<UUID> ids,
                               DataFetchingEnvironment env);

    @GraphQLField
    Existential existential(@NotNull @GraphQLName("id") UUID id,
                            DataFetchingEnvironment env);

    @GraphQLField
    Interval interval(@NotNull @GraphQLName("id") UUID id,
                      DataFetchingEnvironment env);

    @GraphQLField
    List<Interval> intervals(@GraphQLName("ids") List<UUID> ids,
                             DataFetchingEnvironment env);

    @GraphQLField
    Location location(@NotNull @GraphQLName("id") UUID id,
                      DataFetchingEnvironment env);

    @GraphQLField
    List<Location> locations(@GraphQLName("ids") List<UUID> ids,
                             DataFetchingEnvironment env);

    @GraphQLField
    Product product(@NotNull @GraphQLName("id") UUID id,
                    DataFetchingEnvironment env);

    @GraphQLField
    List<Product> products(@GraphQLName("ids") List<UUID> ids,
                           DataFetchingEnvironment env);

    @GraphQLField
    Relationship relationship(@NotNull @GraphQLName("id") UUID id,
                              DataFetchingEnvironment env);

    @GraphQLField
    List<Relationship> relationships(@GraphQLName("ids") List<UUID> ids,
                                     DataFetchingEnvironment env);

    @GraphQLField
    StatusCode statusCode(@NotNull @GraphQLName("id") UUID id,
                          DataFetchingEnvironment env);

    @GraphQLField
    List<StatusCode> statusCodes(@GraphQLName("ids") List<UUID> ids,
                                 DataFetchingEnvironment env);

    @GraphQLField
    Unit unit(@NotNull @GraphQLName("id") UUID id, DataFetchingEnvironment env);

    @GraphQLField
    List<Unit> units(@GraphQLName("ids") List<UUID> ids,
                     DataFetchingEnvironment env);
}
