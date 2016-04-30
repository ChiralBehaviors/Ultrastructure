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

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialMutations {

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency createAgency(@NotNull @GraphQLName("state") ExistentialState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAgency();
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    default Attribute createAttribute(@NotNull @GraphQLName("state") AttributeState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAttribute();
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    default Interval createInterval(@NotNull @GraphQLName("state") ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newInterval();
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    default Location createLocation(@NotNull @GraphQLName("state") ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newLocation();
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    default Product createProduct(@NotNull @GraphQLName("state") ExistentialState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newProduct();
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship createRelationship(@NotNull @GraphQLName("state") RelationshipState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newRelationship();
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    default StatusCode createStatusCode(@NotNull @GraphQLName("state") StatusCodeState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newStatusCode();
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    default Unit createUnit(@NotNull @GraphQLName("state") ExistentialState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newUnit();
        state.update(record);
        record.insert();
        return new Unit(record);
    }

    @GraphQLField
    default Boolean removeAgency(@NotNull @GraphQLName("id") String id,
                                 DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeAttribute(@NotNull @GraphQLName("id") String id,
                                    DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeInterval(@NotNull @GraphQLName("id") String id,
                                   DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeLocation(@NotNull @GraphQLName("id") String id,
                                   DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeProduct(@NotNull @GraphQLName("id") String id,
                                  DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeRelationship(@NotNull @GraphQLName("id") String id,
                                       DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeStatusCode(@NotNull @GraphQLName("id") String id,
                                     DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeUnit(@NotNull @GraphQLName("id") String id,
                               DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency updateAgency(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    default Attribute updateAttribute(@NotNull @GraphQLName("state") AttributeUpdateState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    default Interval updateInterval(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    default Location updateLocation(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    default Product updateProduct(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship updateRelationship(@NotNull @GraphQLName("state") RelationshipUpdateState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    default StatusCode updateStatusCode(@NotNull @GraphQLName("state") StatusCodeUpdateState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    default Unit updateUnit(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Unit(record);
    }
}
