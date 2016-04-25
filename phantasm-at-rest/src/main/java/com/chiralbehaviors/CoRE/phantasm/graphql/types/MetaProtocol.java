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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocol {
    private final MetaProtocolRecord record;

    public MetaProtocol(MetaProtocolRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getAssignTo(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getAssignTo()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getDeliverFrom(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getDeliverTo(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getDeliverTo()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getProduct(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getProduct()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getQuantityUnit(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getRequester(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getRequester()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getService(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getService()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getStatus(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getStatus()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return record.getVersion();
    }
}
