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

import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class ChildSequencing {
    private final ChildSequencingAuthorizationRecord record;

    public ChildSequencing(ChildSequencingAuthorizationRecord record) {
        this.record = record;
    }

    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return new Product(ctx(env).records()
                                   .resolve(record.getService()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getNextChild(DataFetchingEnvironment env) {
        return new Product(ctx(env).records()
                                   .resolve(record.getNextChild()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getStatusCode(DataFetchingEnvironment env) {
        return new StatusCode(ctx(env).records()
                                      .resolve(record.getStatusCode()));
    }

    @GraphQLField
    public Integer getSequenceNumber() {
        return record.getSequenceNumber();
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getNextChildStatus(DataFetchingEnvironment env) {
        return new StatusCode(ctx(env).records()
                                      .resolve(record.getNextChildStatus()));
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
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
