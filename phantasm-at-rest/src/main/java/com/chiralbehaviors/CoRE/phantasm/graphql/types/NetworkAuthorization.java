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

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;

import graphql.annotations.GraphQLField;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class NetworkAuthorization {

    private static final String SET_NOTES = "setNotes";

    private static final String SET_NAME  = "setName";

    class NeworkAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return NetworkAuthorizationType;
        }
    }

    public static final GraphQLObjectType NetworkAuthorizationType    = Existential.objectTypeOf(NetworkAuthorization.class);

    private static final String           CREATE                      = "CreateNetworkAuthorization";
    private static final String           DELETE                      = "DeleteNetworkAuthorization";
    private static final String           ID                          = "id";
    private static final String           NETWORK_AUTHORIZATION_STATE = "NetworkAuthorizationState";
    private static final String           SET_AUTHORITY               = "setAuthority";
    private static final String           SET_CARDINALITY             = "setCardinality";
    private static final String           SET_CHILD                   = "setChild";
    private static final String           SET_PARENT                  = "setParent";
    private static final String           SET_RELATIONSHIP            = "setRelationship";
    private static final String           STATE                       = "state";
    private static final String           UPDATE                      = "UpdateNetworkAuthorization";

    public static void build(Builder query, Builder mutation) {
        Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    private static Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NAME, (e, value) -> e.setName((String) value));
        updateTemplate.put(SET_AUTHORITY,
                           (e,
                            value) -> e.setAuthority(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD,
                           (e,
                            value) -> e.setChild(UUID.fromString((String) value)));
        updateTemplate.put(SET_PARENT,
                           (e,
                            value) -> e.setParent(UUID.fromString((String) value)));
        updateTemplate.put(SET_RELATIONSHIP,
                           (e,
                            value) -> e.setRelationship(UUID.fromString((String) value)));
        updateTemplate.put(SET_CARDINALITY,
                           (e,
                            value) -> e.setCardinality(Cardinality.valueOf((String) value)));
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        return updateTemplate;
    }

    public static NetworkAuthorization fetch(DataFetchingEnvironment env,
                                             UUID id) {
        return new NetworkAuthorization(ctx(env).create()
                                                .selectFrom(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                .where(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(id))
                                                .fetchOne());
    }

    public static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(NetworkAuthorizationType.getName())
                                   .type(NetworkAuthorizationType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the network authorization")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new NetworkAuthorization(fetch(env));
                                   })
                                   .build();
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(NETWORK_AUTHORIZATION_STATE)
                                                                                .description("Network authorization creation/update state");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
                                           .description("The name of the network authorization")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NOTES)
                                           .description("The notes of the network authorization")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_AUTHORITY)
                                           .description("The authority of the network authorization")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CARDINALITY)
                                           .description("The authorized cardinality of the children")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_PARENT)
                                           .description("The authorized parent facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_RELATIONSHIP)
                                           .description("The authorize relationship between the parent and child facets")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CHILD)
                                           .description("The authorized child facet")
                                           .build());
        return builder.build();
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of Facet")
                                   .type(new GraphQLTypeReference(NetworkAuthorizationType.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the network authorization")
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

    public static ExistentialNetworkAuthorizationRecord fetch(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION)
                       .where(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private static Object newAuth(DataFetchingEnvironment env,
                                  Map<String, Object> createState,
                                  Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate) {
        ExistentialNetworkAuthorizationRecord record = ctx(env).records()
                                                               .newExistentialNetworkAuthorization();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new NetworkAuthorization(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(NetworkAuthorizationType.getName()))
                                   .description("Delete the %s facet")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the facet instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(NetworkAuthorizationType.getName()))
                                   .description("Update the instance of a facet")
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

    private static NetworkAuthorization update(DataFetchingEnvironment env,
                                               Map<String, Object> updateState,
                                               Map<String, BiConsumer<ExistentialNetworkAuthorizationRecord, Object>> updateTemplate) {
        ExistentialNetworkAuthorizationRecord auth = ctx(env).create()
                                                             .selectFrom(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                             .where(Tables.EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                                             .fetchOne();
        updateState.remove(ID);
        if (auth == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(auth,
                                                            updateState.get(k)));
        auth.update();
        return new NetworkAuthorization(auth);
    }

    private final ExistentialNetworkAuthorizationRecord record;

    public NetworkAuthorization(ExistentialNetworkAuthorizationRecord record) {
        this.record = record;
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        return resolve(env, record.getAuthority());
    }

    @GraphQLField
    public Cardinality getCardinality() {
        return record.getCardinality();
    }

    @GraphQLField
    public Facet getChild(DataFetchingEnvironment env) {
        return Facet.fetch(env, record.getChild());
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
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
    public Facet getParent(DataFetchingEnvironment env) {
        return Facet.fetch(env, record.getParent());
    }

    @GraphQLField
    public Relationship getRelationship(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getRelationship()));
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
