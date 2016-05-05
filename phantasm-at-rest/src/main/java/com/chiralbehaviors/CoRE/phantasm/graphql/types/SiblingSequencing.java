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
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class SiblingSequencing {

    public static class SiblingSequencingState {
        @GraphQLField
        public String  nextSibling;
        @GraphQLField
        public String  nextSiblingStatus;
        @GraphQLField
        public String  notes;
        @GraphQLField
        public Integer sequenceNumber;
        @GraphQLField
        public String  service;
        @GraphQLField
        public String  statusCode;

        public void update(SiblingSequencingAuthorizationRecord record) {
            if (nextSibling != null) {
                record.setNextSibling(UUID.fromString(nextSibling));
            }
            if (nextSiblingStatus != null) {
                record.setNextSiblingStatus(UUID.fromString(nextSiblingStatus));
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

    public static class SiblingSequencingUpdateState
            extends SiblingSequencingState {
        @GraphQLField
        public String id;
    }

    public static SiblingSequencing fetch(DataFetchingEnvironment env,
                                          UUID id) {
        return new SiblingSequencing(ctx(env).create()
                                             .selectFrom(Tables.SIBLING_SEQUENCING_AUTHORIZATION)
                                             .where(Tables.SIBLING_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                             .fetchOne());
    }

    private final SiblingSequencingAuthorizationRecord record;

    public SiblingSequencing(SiblingSequencingAuthorizationRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public Product getNextSibling(DataFetchingEnvironment env) {
        return new Product(ctx(env).records()
                                   .resolve(record.getNextSibling()));
    }

    @GraphQLField
    public StatusCode getNextSiblingStatus(DataFetchingEnvironment env) {
        return new StatusCode(ctx(env).records()
                                      .resolve(record.getNextSiblingStatus()));
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    public SiblingSequencingAuthorizationRecord getRecord() {
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
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(ctx(env).records()
                                  .resolve(record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }
}
