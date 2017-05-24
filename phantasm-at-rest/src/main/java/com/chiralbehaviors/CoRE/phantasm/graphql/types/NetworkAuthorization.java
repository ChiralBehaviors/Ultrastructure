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

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class NetworkAuthorization {

    public static NetworkAuthorization fetch(DataFetchingEnvironment env,
                                             UUID id) {
        return new NetworkAuthorization(WorkspaceSchema.ctx(env)
                                                       .create()
                                                       .selectFrom(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                       .where(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(id))
                                                       .fetchOne());
    }

    private final ExistentialNetworkAuthorizationRecord record;

    public NetworkAuthorization(ExistentialNetworkAuthorizationRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        if (record.getAuthority() == null) {
            return null;
        }
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    public Cardinality getCardinality() {
        return record.getCardinality();
    }

    @GraphQLField
    public Facet getChild(DataFetchingEnvironment env) {
        return Facet.fetch(env, record.getChild());
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
        return Facet.fetch(env, record.getParent());
    }

    public ExistentialNetworkAuthorizationRecord getRecord() {
        return record;
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
}
