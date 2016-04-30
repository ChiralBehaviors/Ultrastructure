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

    public static class MetaProtocolState {
        @GraphQLField
        public String assignTo;
        @GraphQLField
        public String deliverFrom;
        @GraphQLField
        public String deliverTo;
        @GraphQLField
        public String notes;
        @GraphQLField
        public String product;
        @GraphQLField
        public Float  quantity;
        @GraphQLField
        public String requester;
        @GraphQLField
        public String service;
        @GraphQLField
        public String serviceType;
        @GraphQLField
        public String status;
        @GraphQLField
        public String unit;

        public void update(MetaProtocolRecord r) {
            if (assignTo != null) {
                r.setAssignTo(UUID.fromString(assignTo));
            }
            if (deliverFrom != null) {
                r.setDeliverFrom(UUID.fromString(deliverFrom));
            }
            if (deliverTo != null) {
                r.setDeliverTo(UUID.fromString(deliverTo));
            }
            if (notes != null) {
                r.setNotes(notes);
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (requester != null) {
                r.setRequester(UUID.fromString(requester));
            }
            if (service != null) {
                r.setService(UUID.fromString(service));
            }
            if (status != null) {
                r.setStatus(UUID.fromString(status));
            }
            if (unit != null) {
                r.setQuantityUnit(UUID.fromString(unit));
            }
            if (serviceType != null) {
                r.setServiceType(UUID.fromString(serviceType));
            }
        }
    }

    public static class MetaProtocolUpdateState extends MetaProtocolState {
        @GraphQLField
        public String id;
    }

    public static MetaProtocol fetch(DataFetchingEnvironment env, UUID id) {
        return new MetaProtocol(ctx(env).create()
                                        .selectFrom(Tables.META_PROTOCOL)
                                        .where(Tables.META_PROTOCOL.ID.equal(id))
                                        .fetchOne());
    }

    private final MetaProtocolRecord record;

    public MetaProtocol(MetaProtocolRecord record) {
        assert record != null;
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

    public MetaProtocolRecord getRecord() {
        return record;
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
