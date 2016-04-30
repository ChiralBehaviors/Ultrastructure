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

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.StatusCodeSequencingType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeSequencing {

    private static final String CREATE             = "CreateStatusCodeSequencing";
    private static final String DELETE             = "DeleteStatusCodeSequencing";
    private static final String ID                 = "id";
    private static final String IDS                = "ids";
    private static final String INSTANCES_OF_QUERY = "InstancesOfStatusCodeSequencing";
    private static final String PROTOCOL_STATE     = "StatusCodeSequencingState";
    private static final String SET_CHILD          = "setChild";
    private static final String SET_NOTES          = "setNotes";
    private static final String SET_PARENT         = "setParent";
    private static final String SET_SERVICE        = "setService";
    private static final String SET_STATUS         = "setStatus";
    private static final String STATE              = "state";
    private static final String UPDATE             = "UpdateStatusCodeSequencing";

    public static void build(Builder query, Builder mutation,
                             ThreadLocal<com.chiralbehaviors.CoRE.domain.Product> currentWorkspace) {
        Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        query.field(instances());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(PROTOCOL_STATE)
                                                                                .description("StatusCodeSequencing creation/update state");
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The notes of the  status code sequencing")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILD)
                                           .description("The child of the  status code sequencing")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_PARENT)
                                           .description("The parent of the  status code sequencing")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_SERVICE)
                                           .description("The service of the  status code sequencing")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_STATUS)
                                           .description("The status of the  status code sequencing")
                                           .build());
        return builder.build();
    }

    private static Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        updateTemplate.put(SET_PARENT,
                           (e,
                            value) -> e.setParent(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD,
                           (e,
                            value) -> e.setChild(UUID.fromString((String) value)));
        updateTemplate.put(SET_SERVICE,
                           (e,
                            value) -> e.setService(UUID.fromString((String) value)));
        return updateTemplate;
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of  status code sequencing")
                                   .type(new GraphQLTypeReference(StatusCodeSequencingType.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the  status code sequencing")
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

    private static StatusCodeSequencingRecord fetch(DataFetchingEnvironment env) {
        return fetch(env, (String) env.getArgument(ID));
    }

    private static StatusCodeSequencingRecord fetch(DataFetchingEnvironment env,
                                                    String id) {
        return ctx(env).create()
                       .selectFrom(Tables.STATUS_CODE_SEQUENCING)
                       .where(Tables.STATUS_CODE_SEQUENCING.ID.equal(UUID.fromString(id)))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(StatusCodeSequencingType.getName())
                                   .type(StatusCodeSequencingType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the status code sequencing")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new StatusCodeSequencing(fetch(env));
                                   })
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private static GraphQLFieldDefinition instances() {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(new GraphQLList(StatusCodeSequencingType))
                                   .argument(newArgument().name(IDS)
                                                          .description("status code sequencing ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return ((List<String>) env.getArgument(IDS)).stream()
                                                                                   .map(id -> StatusCodeSequencing.fetch(env,
                                                                                                                         id))
                                                                                   .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static Object newAuth(DataFetchingEnvironment env,
                                  Map<String, Object> createState,
                                  Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate) {
        StatusCodeSequencingRecord record = ctx(env).records()
                                                    .newStatusCodeSequencing();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new StatusCodeSequencing(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(StatusCodeSequencingType.getName()))
                                   .description("Delete the %s status code sequencing")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the status code sequencing instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static StatusCodeSequencing update(DataFetchingEnvironment env,
                                               Map<String, Object> updateState,
                                               Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate) {
        StatusCodeSequencingRecord auth = ctx(env).create()
                                                  .selectFrom(Tables.STATUS_CODE_SEQUENCING)
                                                  .where(Tables.STATUS_CODE_SEQUENCING.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                                  .fetchOne();
        updateState.remove(ID);
        if (auth == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(auth,
                                                            updateState.get(k)));
        auth.update();
        return new StatusCodeSequencing(auth);
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<StatusCodeSequencingRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(StatusCodeSequencingType.getName()))
                                   .description("Update the instance of a status code sequencing")
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

    private final StatusCodeSequencingRecord record;

    public StatusCodeSequencing(StatusCodeSequencingRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getChild(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChild()));
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
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
