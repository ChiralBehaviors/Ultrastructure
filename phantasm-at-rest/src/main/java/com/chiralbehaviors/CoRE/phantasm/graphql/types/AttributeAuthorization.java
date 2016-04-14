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

import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet.FacetTypeFunction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

/**
 * @author hhildebrand
 *
 */
public class AttributeAuthorization {
    class AttributeAuthorizationTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return AttributeAuthorizationType;
        }
    }

    public static final GraphQLObjectType                 AttributeAuthorizationType = Existential.objectTypeOf(AttributeAuthorization.class);

    private final ExistentialAttributeAuthorizationRecord record;

    public AttributeAuthorization(ExistentialAttributeAuthorizationRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAuthority(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    @GraphQLType(AttributeTypeFunction.class)
    public Attribute getAuthorizedAttribute(DataFetchingEnvironment env) {
        return new Attribute(resolve(env, record.getAuthorizedAttribute()));
    }

    @GraphQLField
    public String getBinaryValue() {
        return Base64.getEncoder()
                     .encodeToString(record.getBinaryValue());
    }

    @GraphQLField
    public Boolean getBooleanValue() {
        return record.getBooleanValue();
    }

    @GraphQLField
    @GraphQLType(FacetTypeFunction.class)
    public Facet getFacet(DataFetchingEnvironment env) {
        return Facet.fetch(env, record.getFacet());
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
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
    public BigDecimal getNumericValue() {
        return record.getNumericValue();
    }

    @GraphQLField
    public String getTextValue() {
        return record.getTextValue();
    }

    @GraphQLField
    public Timestamp getTimestampValue() {
        return record.getTimestampValue();
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion() {
        return record.getVersion();
    }

}
