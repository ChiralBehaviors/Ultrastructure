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

import java.math.BigDecimal;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Protocol {

    public static class ProtocolState {
        @GraphQLField
        public String assignTo;
        @GraphQLField
        public String childAssignTo;
        @GraphQLField
        public String childDeliverFrom;
        @GraphQLField
        public String childDeliverTo;
        @GraphQLField
        public String childProduct;
        @GraphQLField
        public Float  childQuantity;
        @GraphQLField
        public String childService;
        @GraphQLField
        public String childStatus;
        @GraphQLField
        public String childUnit;
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
        public String status;
        @GraphQLField
        public String unit;

        public void update(ProtocolRecord r) {
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
            if (quantity != null) {
                r.setQuantity(BigDecimal.valueOf(quantity));
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
            if (childAssignTo != null) {
                r.setChildAssignTo(UUID.fromString(childAssignTo));
            }
            if (childDeliverFrom != null) {
                r.setChildDeliverFrom(UUID.fromString(childDeliverFrom));
            }
            if (childDeliverTo != null) {
                r.setChildDeliverTo(UUID.fromString(childDeliverTo));
            }
            if (childProduct != null) {
                r.setChildProduct(UUID.fromString(childProduct));
            }
            if (childQuantity != null) {
                r.setChildQuantity(BigDecimal.valueOf(childQuantity));
            }
            if (childProduct != null) {
                r.setChildProduct(UUID.fromString(childProduct));
            }
            if (childService != null) {
                r.setChildService(UUID.fromString(childService));
            }
            if (childStatus != null) {
                r.setChildStatus(UUID.fromString(childStatus));
            }
            if (childUnit != null) {
                r.setChildQuantityUnit(UUID.fromString(childUnit));
            }
        }
    }

    public static class ProtocolUpdateState extends ProtocolState {
        @GraphQLField
        public String id;
    }

    public static ProtocolRecord fetch(DataFetchingEnvironment env, UUID id) {
        return ctx(env).create()
                       .selectFrom(Tables.PROTOCOL)
                       .where(Tables.PROTOCOL.ID.equal(id))
                       .fetchOne();
    }

    private final ProtocolRecord record;

    public Protocol(ProtocolRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getChildAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getChildAssignTo()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getChildDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getChildDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverTo()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getChildProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildProduct()));
    }

    @GraphQLField
    public Long getChildQuantity() {
        return record.getChildQuantity()
                     .longValue();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getChildQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getChildQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getChildrenRelationship(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getChildrenRelationship()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getChildService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildService()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getChildStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChildStatus()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverTo()));
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
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
    @GraphQLType(ProductTypeFunction.class)
    public Product getProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getProduct()));
    }

    @GraphQLField
    public Long getQuantity() {
        return record.getQuantity()
                     .longValue();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getRequester(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getRequester()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getService()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getStatus()));
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
