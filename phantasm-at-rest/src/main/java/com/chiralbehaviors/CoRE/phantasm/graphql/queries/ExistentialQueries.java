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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
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
@GraphQLInterface
public interface ExistentialQueries {

    static List<ExistentialRecord> resolved(DataFetchingEnvironment env,
                                            ExistentialDomain domain) {
        return WorkspaceSchema.ctx(env)
                              .create()
                              .selectFrom(Tables.EXISTENTIAL)
                              .where(Tables.EXISTENTIAL.WORKSPACE.eq(((WorkspaceContext) env.getContext()).getWorkspace()
                                                                                                          .getId()))
                              .and(Tables.EXISTENTIAL.DOMAIN.equal(domain))
                              .fetch()
                              .into(ExistentialRecord.class);
    }

    @GraphQLField
    default List<Agency> agencies(@GraphQLName("ids") List<String> ids,
                                  DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Agency).stream()
                                                          .map(r -> new Agency(r))
                                                          .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Agency(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Agency agency(@NotNull @GraphQLName("id") String id,
                          DataFetchingEnvironment env) {
        return new Agency(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default Attribute attribute(@NotNull @GraphQLName("id") String id,
                                DataFetchingEnvironment env) {
        return new Attribute(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Attribute> attributes(@GraphQLName("ids") List<String> ids,
                                       DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Attribute).stream()
                                                             .map(r -> new Attribute(r))
                                                             .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Attribute(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Interval interval(@NotNull @GraphQLName("id") String id,
                              DataFetchingEnvironment env) {
        return new Interval(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Interval> intervals(@GraphQLName("ids") List<String> ids,
                                     DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Interval).stream()
                                                            .map(r -> new Interval(r))
                                                            .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Interval(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Location location(@NotNull @GraphQLName("id") String id,
                              DataFetchingEnvironment env) {
        return new Location(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Location> locations(@GraphQLName("ids") List<String> ids,
                                     DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Location).stream()
                                                            .map(r -> new Location(r))
                                                            .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Location(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Product product(@NotNull @GraphQLName("id") String id,
                            DataFetchingEnvironment env) {
        return new Product(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Product> products(@GraphQLName("ids") List<String> ids,
                                   DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Product).stream()
                                                           .map(r -> new Product(r))
                                                           .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Product(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Relationship relationship(@NotNull @GraphQLName("id") String id,
                                      DataFetchingEnvironment env) {
        return new Relationship(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Relationship> relationships(@GraphQLName("ids") List<String> ids,
                                             DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Relationship).stream()
                                                                .map(r -> new Relationship(r))
                                                                .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Relationship(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default StatusCode statusCode(@NotNull @GraphQLName("id") String id,
                                  DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<StatusCode> statusCodes(@GraphQLName("ids") List<String> ids,
                                         DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.StatusCode).stream()
                                                              .map(r -> new StatusCode(r))
                                                              .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new StatusCode(resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    default Unit unit(@NotNull @GraphQLName("id") String id,
                      DataFetchingEnvironment env) {
        return new Unit(resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    default List<Unit> units(@GraphQLName("ids") List<String> ids,
                             DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Unit).stream()
                                                        .map(r -> new Unit(r))
                                                        .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Unit(resolve(env, id)))
                  .collect(Collectors.toList());
    }
}
