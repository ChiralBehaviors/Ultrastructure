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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;

import java.lang.reflect.AnnotatedType;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;

import graphql.annotations.GraphQLField;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

/**
 * @author hhildebrand
 *
 */
public class NetworkAuthorization {
    public static GraphQLObjectType NetworkAuthorizationType;

    class NeworkAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return NetworkAuthorizationType;
        }
    }

    private final ExistentialNetworkAuthorizationRecord record;

    public NetworkAuthorization(ExistentialNetworkAuthorizationRecord record) {
        this.record = record;
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        return resolve(env, record.getAuthority());
    }

    @GraphQLField
    public Cardinality getCardinality() {
        return record.getCardinality();
    }

    @GraphQLField
    public Facet getChild(DataFetchingEnvironment env) {
        return fetch(env, record.getChild());
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    public String getName() {
        return record.getName();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    public Facet getParent(DataFetchingEnvironment env) {
        return fetch(env, record.getParent());
    }

    @GraphQLField
    public Relationship getRelationship(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getRelationship()));
    }

    @GraphQLField
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }

    private Facet fetch(DataFetchingEnvironment env, UUID id) {
        return new Facet(ctx(env).create()
                                 .selectFrom(Tables.FACET)
                                 .where(Tables.FACET.ID.equal(id))
                                 .fetchOne());
    }

    private <T> T resolve(DataFetchingEnvironment env, UUID id) {
        return ctx(env).records()
                       .resolve(id);
    }
}
