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
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocol {
    class MetaProtocolTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return MetaProtocolType;
        }
    }

    public static final GraphQLObjectType MetaProtocolType   = Existential.objectTypeOf(MetaProtocol.class);

    private static final String           CREATE             = "CreateMetaProtocol";
    private static final String           DELETE             = "DeleteMetaProtocol";
    private static final String           ID                 = "id";
    private static final String           IDS                = "ids";
    private static final String           INSTANCES_OF_QUERY = "InstancesOfMetaProtocol";
    private static final String           PROTOCOL_STATE     = "MetaProtocolState";
    private static final String           SET_ASSIGN_TO      = "setAssignTo";
    private static final String           SET_DELIVER_FROM   = "setDeliverFrom";
    private static final String           SET_DELIVER_TO     = "setDeliverTo";
    private static final String           SET_NOTES          = "setNotes";
    private static final String           SET_PRODUCT        = "setProduct";
    private static final String           SET_QUANTITY       = "setQuantity";
    private static final String           SET_QUANTITY_UNIT  = "setQuantityUnit";
    private static final String           SET_REQUESTER      = "setRequester";
    private static final String           SET_SERVICE        = "setService";
    private static final String           SET_SERVICE_TYPE   = "setServiceType";
    private static final String           SET_STATUS         = "setStatus";
    private static final String           STATE              = "state";
    private static final String           UPDATE             = "UpdateMetaProtocol";

    public static void build(Builder query, Builder mutation,
                             ThreadLocal<Product> currentWorkspace) {
        Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        query.field(instances());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(PROTOCOL_STATE)
                                                                                .description("MetaProtocol creation/update state");
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The notes of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_PRODUCT)
                                           .description("The product of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLFloat)
                                           .name(SET_QUANTITY)
                                           .description("The quqntity of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_QUANTITY_UNIT)
                                           .description("The quantity unit of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_REQUESTER)
                                           .description("The requester of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_SERVICE)
                                           .description("The service of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_SERVICE_TYPE)
                                           .description("The service type of the  meta protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_STATUS)
                                           .description("The status of the  meta protocol")
                                           .build());
        return builder.build();
    }

    private static Map<String, BiConsumer<MetaProtocolRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        updateTemplate.put(SET_ASSIGN_TO,
                           (e,
                            value) -> e.setAssignTo(UUID.fromString((String) value)));
        updateTemplate.put(SET_QUANTITY_UNIT,
                           (e,
                            value) -> e.setQuantityUnit(UUID.fromString((String) value)));
        updateTemplate.put(SET_DELIVER_FROM,
                           (e,
                            value) -> e.setDeliverFrom(UUID.fromString((String) value)));
        updateTemplate.put(SET_DELIVER_TO,
                           (e,
                            value) -> e.setDeliverTo(UUID.fromString((String) value)));
        updateTemplate.put(SET_PRODUCT,
                           (e,
                            value) -> e.setProduct(UUID.fromString((String) value)));
        updateTemplate.put(SET_REQUESTER,
                           (e,
                            value) -> e.setRequester(UUID.fromString((String) value)));
        updateTemplate.put(SET_SERVICE,
                           (e,
                            value) -> e.setService(UUID.fromString((String) value)));
        updateTemplate.put(SET_SERVICE_TYPE,
                           (e,
                            value) -> e.setServiceType(UUID.fromString((String) value)));
        updateTemplate.put(SET_STATUS,
                           (e,
                            value) -> e.setStatus(UUID.fromString((String) value)));
        return updateTemplate;
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of  meta protocol")
                                   .type(new GraphQLTypeReference(MetaProtocolType.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the  meta protocol")
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

    private static MetaProtocolRecord fetch(DataFetchingEnvironment env) {
        return fetch(env, (String) env.getArgument(ID));
    }

    private static MetaProtocolRecord fetch(DataFetchingEnvironment env,
                                            String id) {
        return ctx(env).create()
                       .selectFrom(Tables.META_PROTOCOL)
                       .where(Tables.META_PROTOCOL.ID.equal(UUID.fromString(id)))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(MetaProtocolType.getName())
                                   .type(MetaProtocolType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the meta protocol")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new MetaProtocol(fetch(env));
                                   })
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private static GraphQLFieldDefinition instances() {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(new GraphQLList(MetaProtocolType))
                                   .argument(newArgument().name(IDS)
                                                          .description("meta protocol ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return ((List<String>) env.getArgument(IDS)).stream()
                                                                                   .map(id -> MetaProtocol.fetch(env,
                                                                                                                 id))
                                                                                   .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static Object newAuth(DataFetchingEnvironment env,
                                  Map<String, Object> createState,
                                  Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate) {
        MetaProtocolRecord record = ctx(env).records()
                                            .newMetaProtocol();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new MetaProtocol(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(MetaProtocolType.getName()))
                                   .description("Delete the %s meta protocol")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the meta protocol instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static MetaProtocol update(DataFetchingEnvironment env,
                                       Map<String, Object> updateState,
                                       Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate) {
        MetaProtocolRecord auth = ctx(env).create()
                                          .selectFrom(Tables.META_PROTOCOL)
                                          .where(Tables.META_PROTOCOL.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                          .fetchOne();
        updateState.remove(ID);
        if (auth == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(auth,
                                                            updateState.get(k)));
        auth.update();
        return new MetaProtocol(auth);
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<MetaProtocolRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(MetaProtocolType.getName()))
                                   .description("Update the instance of a meta protocol")
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

    private final MetaProtocolRecord record;

    public MetaProtocol(MetaProtocolRecord record) {
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
