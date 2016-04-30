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

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.IntervalTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLNonNull;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialMutations {

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency createAgency(@GraphQLNonNull ExistentialState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAgency();
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default Attribute createAttribute(@GraphQLNonNull AttributeState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAttribute();
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default Interval createInterval(@GraphQLNonNull ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newInterval();
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default Location createLocation(@GraphQLNonNull ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newLocation();
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default Product createProduct(@GraphQLNonNull ExistentialState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newProduct();
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship createRelationship(@GraphQLNonNull RelationshipState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newRelationship();
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default StatusCode createStatusCode(@GraphQLNonNull StatusCodeState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newStatusCode();
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default Unit createUnit(@GraphQLNonNull ExistentialState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newUnit();
        state.update(record);
        record.insert();
        return new Unit(record);
    }

    @GraphQLField
    default Boolean removeAgency(@GraphQLNonNull String id,
                                 DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeAttribute(@GraphQLNonNull String id,
                                    DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeInterval(@GraphQLNonNull String id,
                                   DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeLocation(@GraphQLNonNull String id,
                                   DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeProduct(@GraphQLNonNull String id,
                                  DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeRelationship(@GraphQLNonNull String id,
                                       DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeStatusCode(@GraphQLNonNull String id,
                                     DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean removeUnit(@GraphQLNonNull String id,
                               DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency updateAgency(ExistentialUpdateState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default Attribute updateAttribute(AttributeUpdateState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default Interval updateInterval(ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default Location updateLocation(ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default Product updateProduct(ExistentialUpdateState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship updateRelationship(RelationshipUpdateState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default StatusCode updateStatusCode(StatusCodeUpdateState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default Unit updateUnit(ExistentialUpdateState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Unit(record);
    }
}
