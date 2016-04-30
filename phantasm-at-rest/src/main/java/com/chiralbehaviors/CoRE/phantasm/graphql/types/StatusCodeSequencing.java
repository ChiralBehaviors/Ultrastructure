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
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.StatusCodeTypeFunction;
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
public class StatusCodeSequencing {

    public static class StatusCodeSequencingState {
        @GraphQLField
        public String  child;
        @GraphQLField
        public String  notes;
        @GraphQLField
        public String  parent;
        @GraphQLField
        public Integer sequenceNumber;
        @GraphQLField
        public String  service;
        @GraphQLField
        public String  statusCode;

        public void update(StatusCodeSequencingRecord record) {
            if (parent != null) {
                record.setParent(UUID.fromString(parent));
            }
            if (child != null) {
                record.setChild(UUID.fromString(child));
            }
            if (notes != null) {
                record.setNotes(notes);
            }
            if (service != null) {
                record.setService(UUID.fromString(service));
            }
        }
    }

    public static class StatusCodeSequencingUpdateState
            extends StatusCodeSequencingState {
        @GraphQLField
        public String id;
    }

    public static StatusCodeSequencing fetch(DataFetchingEnvironment env,
                                             UUID id) {
        return new StatusCodeSequencing(ctx(env).create()
                                                .selectFrom(Tables.STATUS_CODE_SEQUENCING)
                                                .where(Tables.STATUS_CODE_SEQUENCING.ID.equal(id))
                                                .fetchOne());
    }

    private final StatusCodeSequencingRecord record;

    public StatusCodeSequencing(StatusCodeSequencingRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getChild(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChild()));
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
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getParent(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChild()));
    }

    public StatusCodeSequencingRecord getRecord() {
        return record;
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChild()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getChild()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }
}
