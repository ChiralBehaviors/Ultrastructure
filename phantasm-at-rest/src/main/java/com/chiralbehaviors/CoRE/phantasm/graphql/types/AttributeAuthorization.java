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
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.MetaSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.MetaSchema.FacetTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeTypeFunction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.Scalars;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class AttributeAuthorization {

    private static final String ATTRIBUTE_AUTHORIZATION_STATE = "AttributeAuthorizationState";
    private static final String CREATE                        = "CreateAttributeAuthorization";
    private static final String DELETE                        = "DeleteAttributeAuthorization";
    private static final String ID                            = "id";
    private static final String SET_AUTHORITY                 = "setAuthority";
    private static final String SET_AUTHORIZED_ATTRIBUTE      = "setAuthorizedAttribute";
    private static final String SET_BINARY_VALUE              = "setBinaryValue";
    private static final String SET_BOOLEAN_VALUE             = "setBooleanValue";
    private static final String SET_INTEGER_VALUE             = "setIntegerValue";
    private static final String SET_JSON_VALUE                = "setJsonValue";
    private static final String SET_NOTES                     = "setNotes";
    private static final String SET_NUMERIC_VALUE             = "setNumericValue";
    private static final String SET_TEXT_VALUE                = "setTextValue";
    private static final String SET_TIMESTAMP_VALUE           = "setTimestampValue";
    private static final String STATE                         = "state";
    private static final String UPDATE                        = "UpdateAttributeAuthorization";

    public static void build(Builder query, Builder mutation,
                             ThreadLocal<Product> currentWorkspace) {
        Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(ATTRIBUTE_AUTHORIZATION_STATE)
                                                                                .description("Network authorization creation/update state");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NOTES)
                                           .description("The notes of the attribute authorization")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_AUTHORITY)
                                           .description("The authority of the attribute authorization")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_AUTHORIZED_ATTRIBUTE)
                                           .description("The authorized attribute")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_BINARY_VALUE)
                                           .description("Set the binary value of the authorization")
                                           .build());
        builder.field(newInputObjectField().type(Scalars.GraphQLInt)
                                           .name(SET_INTEGER_VALUE)
                                           .description("Set the integer value of the authorization")
                                           .build());
        builder.field(newInputObjectField().type(Scalars.GraphQLString)
                                           .name(SET_JSON_VALUE)
                                           .description("Set the json value of the authorization")
                                           .build());
        builder.field(newInputObjectField().type(Scalars.GraphQLLong)
                                           .name(SET_NUMERIC_VALUE)
                                           .description("Set the numer value of the authorization")
                                           .build());
        builder.field(newInputObjectField().type(Scalars.GraphQLString)
                                           .name(SET_TEXT_VALUE)
                                           .description("Set the text value of the authorization")
                                           .build());
        builder.field(newInputObjectField().type(Scalars.GraphQLLong)
                                           .name(SET_TIMESTAMP_VALUE)
                                           .description("Set the timestamp value of the authorization")
                                           .build());
        return builder.build();
    }

    private static Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_AUTHORITY,
                           (e,
                            value) -> e.setAuthority(UUID.fromString((String) value)));
        updateTemplate.put(SET_AUTHORIZED_ATTRIBUTE,
                           (e,
                            value) -> e.setAuthorizedAttribute(UUID.fromString((String) value)));
        updateTemplate.put(SET_BINARY_VALUE,
                           (e, value) -> e.setBinaryValue(Base64.getDecoder()
                                                                .decode((String) value)));
        updateTemplate.put(SET_BOOLEAN_VALUE,
                           (e,
                            value) -> e.setBooleanValue(Boolean.parseBoolean((String) value)));
        updateTemplate.put(SET_INTEGER_VALUE,
                           (e, value) -> e.setIntegerValue((Integer) value));
        updateTemplate.put(SET_JSON_VALUE,
                           (e, value) -> e.setJsonValue(decodeJson(value)));
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        updateTemplate.put(SET_NUMERIC_VALUE,
                           (e,
                            value) -> e.setNumericValue(BigDecimal.valueOf((Long) value)));
        updateTemplate.put(SET_TIMESTAMP_VALUE,
                           (e,
                            value) -> e.setTimestampValue(new Timestamp((Long) value)));
        updateTemplate.put(SET_TEXT_VALUE,
                           (e, value) -> e.setTextValue((String) value));
        return updateTemplate;
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of attribute authorization")
                                   .type(new GraphQLTypeReference(MetaSchema.AttributeAuthorizationType.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the attribute authorization")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newAuth(env, createState,
                                                      updateTemplate);
                                   })
                                   .build();
    }

    private static JsonNode decodeJson(Object value) {
        try {
            return new ObjectMapper().readValue((String) value, JsonNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Not a json value: %s",
                                                             value),
                                               e);
        }
    }

    private static ExistentialAttributeAuthorizationRecord fetch(DataFetchingEnvironment env) {
        UUID id = UUID.fromString((String) env.getArgument(ID));
        return fetch(env, id);
    }

    public static ExistentialAttributeAuthorizationRecord fetch(DataFetchingEnvironment env,
                                                                UUID id) throws DataAccessException,
                                                                         TooManyRowsException {
        return ctx(env).create()
                       .selectFrom(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                       .where(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(MetaSchema.AttributeAuthorizationType.getName())
                                   .type(MetaSchema.AttributeAuthorizationType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the attribute authorization")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new AttributeAuthorization(fetch(env));
                                   })
                                   .build();
    }

    private static Object newAuth(DataFetchingEnvironment env,
                                  Map<String, Object> createState,
                                  Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate) {
        ExistentialAttributeAuthorizationRecord record = ctx(env).records()
                                                                 .newExistentialAttributeAuthorization();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new AttributeAuthorization(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(MetaSchema.AttributeAuthorizationType.getName()))
                                   .description("Delete the %s facet")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the facet instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static AttributeAuthorization update(DataFetchingEnvironment env,
                                                 Map<String, Object> updateState,
                                                 Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate) {
        ExistentialAttributeAuthorizationRecord auth = ctx(env).create()
                                                               .selectFrom(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                               .where(Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                                               .fetchOne();
        updateState.remove(ID);
        if (auth == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(auth,
                                                            updateState.get(k)));
        auth.update();
        return new AttributeAuthorization(auth);
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<ExistentialAttributeAuthorizationRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(MetaSchema.AttributeAuthorizationType.getName()))
                                   .description("Update the instance of a authorization")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(type))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return update(env, updateState,
                                                     updateTemplate);
                                   })
                                   .build();
    }

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
    public Long getNumericValue() {
        return record.getNumericValue()
                     .longValue();
    }

    @GraphQLField
    public String getTextValue() {
        return record.getTextValue();
    }

    @GraphQLField
    public Long getTimestampValue() {
        return record.getTimestampValue()
                     .getTime();
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
