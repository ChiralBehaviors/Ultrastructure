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

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class ParentSequencing {

    public static class ParentSequencingState {
        @GraphQLField
        public String  notes;
        @GraphQLField
        public String  parent;
        @GraphQLField
        public String  parentStatus;
        @GraphQLField
        public Integer sequenceNumber;
        @GraphQLField
        public String  service;
        @GraphQLField
        public String  statusCode;

        public void update(ParentSequencingAuthorizationRecord record) {
            if (parent != null) {
                record.setParent(UUID.fromString(parent));
            }
            if (parentStatus != null) {
                record.setParentStatusToSet(UUID.fromString(parentStatus));
            }
            if (notes != null) {
                record.setNotes(notes);
            }
            if (service != null) {
                record.setService(UUID.fromString(service));
            }
            if (statusCode != null) {
                record.setStatusCode(UUID.fromString(statusCode));
            }
            if (sequenceNumber != null) {
                record.setSequenceNumber(sequenceNumber);
            }
        }
    }

    public static class ParentSequencingUpdateState
            extends ParentSequencingState {
        @GraphQLField
        public String id;
    }

    public static ParentSequencing fetch(DataFetchingEnvironment env, UUID id) {
        return new ParentSequencing(ctx(env).create()
                                            .selectFrom(Tables.PARENT_SEQUENCING_AUTHORIZATION)
                                            .where(Tables.PARENT_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                            .fetchOne());
    }

    private final ParentSequencingAuthorizationRecord record;

    public ParentSequencing(ParentSequencingAuthorizationRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField

    public Product getParent(DataFetchingEnvironment env) {
        return new Product(ctx(env).records()
                                   .resolve(record.getParent()));
    }

    @GraphQLField

    public StatusCode getParentStatusToSet(DataFetchingEnvironment env) {
        return new StatusCode(ctx(env).records()
                                      .resolve(record.getParentStatusToSet()));
    }

    public ParentSequencingAuthorizationRecord getRecord() {
        return record;
    }

    @GraphQLField
    public Integer getSequenceNumber() {
        return record.getSequenceNumber();
    }

    @GraphQLField

    public Product getService(DataFetchingEnvironment env) {
        return new Product(ctx(env).records()
                                   .resolve(record.getService()));
    }

    @GraphQLField

    public StatusCode getStatusCode(DataFetchingEnvironment env) {
        return new StatusCode(ctx(env).records()
                                      .resolve(record.getStatusCode()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(ctx(env).records()
                                  .resolve(record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }
}
