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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.*;

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Network {

    public static class NetworkState {
        @GraphQLField
        public String authority;
        @GraphQLField
        public String child;
        @GraphQLField
        public String notes;
        @GraphQLField
        public String parent;
        @GraphQLField
        public String relationship;

        public void update(ExistentialNetworkRecord record) {
            if (authority != null) {
                record.setAuthority(UUID.fromString(authority));
            }
            if (notes != null) {
                record.setNotes(notes);
            }
            if (child != null) {
                record.setChild(UUID.fromString(child));
            }
            if (parent != null) {
                record.setParent(UUID.fromString(parent));
            }
        }
    }

    public static class NetworkUpdateState extends NetworkState {
        @GraphQLField
        public String id;
    }

    public static Network fetch(DataFetchingEnvironment env, UUID id) {
        return new Network(fetchRecord(env, id));
    }

    public static ExistentialNetworkRecord fetchRecord(DataFetchingEnvironment env,
                                                       UUID id) {
        return WorkspaceSchema.ctx(env)
                              .create()
                              .selectFrom(Tables.EXISTENTIAL_NETWORK)
                              .where(Tables.EXISTENTIAL_NETWORK.ID.equal(id))
                              .fetchOne();
    }

    private final ExistentialNetworkRecord record;

    public Network(ExistentialNetworkRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        ExistentialRecord a = resolve(env, record.getAuthority());
        if (a == null) {
            return null;
        }
        return new Agency(a);
    }

    @GraphQLField
    public Existential getChild(DataFetchingEnvironment env) {
        return wrap(resolve(env, record.getChild()));
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public NetworkInference getInference(DataFetchingEnvironment env) {
        return new NetworkInference(WorkspaceSchema.ctx(env)
                                                   .records()
                                                   .resolve(record.getInference()));
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    public Existential getParent(DataFetchingEnvironment env) {
        return wrap(resolve(env, record.getChild()));
    }

    @GraphQLField
    public Network getPremise1(DataFetchingEnvironment env) {
        UUID premise1 = record.getPremise1();
        if (premise1 == null) {
            return null;
        }
        return fetch(env, premise1);
    }

    @GraphQLField
    public Network getPremise2(DataFetchingEnvironment env) {
        UUID premise2 = record.getPremise2();
        if (premise2 == null) {
            return null;
        }
        return Network.fetch(env, premise2);
    }

    public ExistentialNetworkRecord getRecord() {
        return record;
    }

    @GraphQLField
    public Relationship getRelationship(DataFetchingEnvironment env) {
        return new Relationship(WorkspaceSchema.ctx(env)
                                               .records()
                                               .resolve(record.getRelationship()));
    }

    @GraphQLField
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(WorkspaceSchema.ctx(env)
                                         .records()
                                         .resolve(record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }
}
