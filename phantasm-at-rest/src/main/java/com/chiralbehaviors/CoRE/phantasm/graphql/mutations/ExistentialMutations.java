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
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialMutations {

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency CreateAgency(ExistentialState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAgency();
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default Attribute CreateAttribute(AttributeState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newAttribute();
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default Interval CreateInterval(ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newInterval();
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default Location CreateLocation(ExistentialState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newLocation();
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default Product CreateProduct(ExistentialState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newProduct();
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship CreateRelationship(RelationshipState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newRelationship();
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default StatusCode CreateStatusCode(StatusCodeState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newStatusCode();
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default Unit CreateUnit(ExistentialState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = ctx(env).records()
                                           .newUnit();
        state.update(record);
        record.insert();
        return new Unit(record);
    }

    @GraphQLField
    default Boolean RemoveAgency(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveAttribute(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveInterval(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveLocation(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveProduct(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveRelationship(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveStatusCode(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    default Boolean RemoveUnit(String id, DataFetchingEnvironment env) {
        Existential.resolve(env, UUID.fromString(id))
                   .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    default Agency UpdateAgency(ExistentialUpdateState state,
                                DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    default Attribute UpdateAttribute(AttributeUpdateState state,
                                      DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @GraphQLField
    @GraphQLType(IntervalTypeFunction.class)
    default Interval UpdateInterval(ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    default Location UpdateLocation(ExistentialUpdateState state,
                                    DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    default Product UpdateProduct(ExistentialUpdateState state,
                                  DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    default Relationship UpdateRelationship(RelationshipUpdateState state,
                                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    default StatusCode UpdateStatusCode(StatusCodeUpdateState state,
                                        DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    default Unit UpdateUnit(ExistentialUpdateState state,
                            DataFetchingEnvironment env) {
        ExistentialRecord record = Existential.resolve(env,
                                                       UUID.fromString(state.id));
        state.update(record);
        record.insert();
        return new Unit(record);
    }
}
