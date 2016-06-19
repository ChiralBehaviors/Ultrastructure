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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class NetworkAttributeAuthorization {

    public static class NetworkAttributeAuthorizationState {
        @GraphQLField
        public String  authority;
        @GraphQLField
        public String  authorizedAttribute;
        @GraphQLField
        public String  binaryValue;
        @GraphQLField
        public Boolean booleanValue;
        @GraphQLField
        public String  networkAuthorization;
        @GraphQLField
        public Integer integerValue;
        @GraphQLField
        public String  jsonValue;
        @GraphQLField
        public String  notes;
        @GraphQLField
        public Double  numericValue;
        @GraphQLField
        public String  textValue;
        @GraphQLField
        public Long    timestampValue;

        public void update(ExistentialNetworkAttributeAuthorizationRecord record) {
            if (authority != null) {
                record.setAuthority(UUID.fromString(authority));
            }
            if (authority != null) {
                record.setAuthority(UUID.fromString(authority));
            }
            if (authorizedAttribute != null) {
                record.setAuthorizedAttribute(UUID.fromString(authorizedAttribute));
            }
            if (binaryValue != null) {
                record.setBinaryValue(Base64.getDecoder()
                                            .decode(binaryValue));
            }
            if (booleanValue != null) {
                record.setBooleanValue(booleanValue);
            }
            if (networkAuthorization != null) {
                record.setNetworkAuthorization(UUID.fromString(networkAuthorization));
            }
            if (integerValue != null) {
                record.setIntegerValue(integerValue);
            }
            if (jsonValue != null) {
                try {
                    record.setJsonValue(new ObjectMapper().readTree(jsonValue));
                } catch (IOException e) {
                    throw new IllegalArgumentException(String.format("Invalid JSON value: %s",
                                                                     jsonValue),
                                                       e);
                }
            }
            if (numericValue != null) {
                record.setNumericValue(BigDecimal.valueOf(numericValue));
            }
            if (textValue != null) {
                record.setTextValue(textValue);
            }
            if (timestampValue != null) {
                record.setTimestampValue(new Timestamp(timestampValue));
            }
            if (notes != null) {
                record.setNotes(notes);
            }
        }
    }

    public static class NetworkAttributeAuthorizationUpdateState
            extends NetworkAttributeAuthorizationState {
        @GraphQLField
        public String id;
    }

    public static ExistentialNetworkAttributeAuthorizationRecord fetch(DataFetchingEnvironment env,
                                                                       UUID id) throws DataAccessException,
                                                                                TooManyRowsException {
        return WorkspaceSchema.ctx(env)
                              .create()
                              .selectFrom(Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                              .where(Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                              .fetchOne();
    }

    private final ExistentialNetworkAttributeAuthorizationRecord record;

    public NetworkAttributeAuthorization(ExistentialNetworkAttributeAuthorizationRecord record) {
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
    public Attribute getAuthorizedAttribute(DataFetchingEnvironment env) {
        return new Attribute(resolve(env, record.getAuthorizedAttribute()));
    }

    @GraphQLField
    public String getBinaryValue() {
        if (record.getBinaryValue() == null) {
            return null;
        }
        return Base64.getEncoder()
                     .encodeToString(record.getBinaryValue());
    }

    @GraphQLField
    public Boolean getBooleanValue() {
        return record.getBooleanValue();
    }

    @GraphQLField
    public NetworkAuthorization getNetworkAuthorization(DataFetchingEnvironment env) {
        return NetworkAuthorization.fetch(env,
                                          record.getNetworkAuthorization());
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public Integer getIntegerValue() {
        return record.getIntegerValue();
    }

    @GraphQLField
    public String getJsonValue() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(record.getJsonValue());
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    public Double getNumericValue() {
        if (record.getNumericValue() == null) {
            return null;
        }
        return record.getNumericValue()
                     .doubleValue();
    }

    @GraphQLField
    public String getTextValue() {
        return record.getTextValue();
    }

    @GraphQLField
    public Long getTimestampValue() {
        if (record.getTimestampValue() == null) {
            return null;
        }
        return record.getTimestampValue()
                     .getTime();
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
