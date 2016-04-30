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

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.IntervalTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialQueries {

    static List<ExistentialRecord> resolve(DataFetchingEnvironment env,
                                           ExistentialDomain domain) {
        return ctx(env).create()
                       .selectDistinct(Tables.EXISTENTIAL.fields())
                       .from(Tables.EXISTENTIAL)
                       .join(Tables.WORKSPACE_AUTHORIZATION)
                       .on(Tables.WORKSPACE_AUTHORIZATION.ID.equal(Tables.EXISTENTIAL.WORKSPACE))
                       .and(Tables.WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.eq(((WorkspaceContext) env.getContext()).getWorkspace()
                                                                                                                    .getId()))
                       .fetch()
                       .into(ExistentialRecord.class);
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default List<Agency> agencies(List<String> ids,
                                  DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Agency).stream()
                                                         .map(r -> new Agency(r))
                                                         .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Agency(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency agency(String id, DataFetchingEnvironment env) {
        return new Agency(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default Attribute attribute(String id, DataFetchingEnvironment env) {
        return new Attribute(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default List<Attribute> attributes(List<String> ids,
                                       DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Attribute).stream()
                                                            .map(r -> new Attribute(r))
                                                            .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Attribute(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default Interval interval(String id, DataFetchingEnvironment env) {
        return new Interval(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default List<Interval> intervals(List<String> ids,
                                     DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Interval).stream()
                                                           .map(r -> new Interval(r))
                                                           .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Interval(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default Location location(String id, DataFetchingEnvironment env) {
        return new Location(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default List<Location> locations(List<String> ids,
                                     DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Location).stream()
                                                           .map(r -> new Location(r))
                                                           .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Location(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default Product product(String id, DataFetchingEnvironment env) {
        return new Product(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default List<Product> products(List<String> ids,
                                   DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Product).stream()
                                                          .map(r -> new Product(r))
                                                          .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Product(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship relationship(String id, DataFetchingEnvironment env) {
        return new Relationship(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default List<Relationship> relationships(List<String> ids,
                                             DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Relationship).stream()
                                                               .map(r -> new Relationship(r))
                                                               .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Relationship(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default StatusCode statusCode(String id, DataFetchingEnvironment env) {
        return new StatusCode(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default List<StatusCode> statusCodes(List<String> ids,
                                         DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.StatusCode).stream()
                                                             .map(r -> new StatusCode(r))
                                                             .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new StatusCode(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default Unit unit(String id, DataFetchingEnvironment env) {
        return new Unit(Existential.resolve(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default List<Unit> units(List<String> ids, DataFetchingEnvironment env) {
        if (ids == null) {
            return resolve(env, ExistentialDomain.Unit).stream()
                                                       .map(r -> new Unit(r))
                                                       .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> new Unit(Existential.resolve(env, id)))
                  .collect(Collectors.toList());
    }
}