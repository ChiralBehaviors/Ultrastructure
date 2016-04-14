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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.annotations.GraphQLAnnotations;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class ExistentialSchema {

    public static final String CREATE_INSTANCES_MUTATION = "CreateExistentials";
    public static final String CREATE_MUTATION           = "CreateExistential";
    public static final String CREATE_TYPE               = "ExistentialCreate";
    public static final String DELETE_MUTATION           = "DeleteExistential";
    public static final String DESCRIPTION               = "description";
    public static final String DOMAIN                    = "domain";
    public static final String EXISTENTIAL               = "Existential";
    public static final String ID                        = "id";
    public static final String INSTANCES_OF_QUERY        = "InstancesOfExistential";
    public static final String NAME                      = "name";
    public static final String NOTES                     = "notes";
    public static final String SET_DESCRIPTION           = "setDescription";
    public static final String SET_NAME                  = "setName";
    public static final String SET_NOTES                 = "setNotes";
    public static final String STATE                     = "state";
    public static final String UPDATE_INSTANCES_MUTATION = "UpdateExistentialInstances";
    public static final String UPDATE_MUTATION           = "UpdateExistential";
    public static final String UPDATE_TYPE               = "UpdateExistential";

    public static GraphQLSchema build() {
        Builder topLevelQuery = newObject().name("Query")
                                           .description("Top level query");
        Builder topLevelMutation = newObject().name("Mutation")
                                              .description("Top level mutation");
        new ExistentialSchema().build(topLevelQuery, topLevelMutation);
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build();
        return schema;
    }

    public void build(Builder query, Builder mutation) {
        Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate = new HashMap<>();

        GraphQLObjectType type = buildType();
        GraphQLInputObjectType updateType = buildUpdateType(updateTemplate);
        GraphQLInputObjectType createType = buildCreateType();

        query.field(instance(type));
        query.field(instances(type));

        mutation.field(createInstance(createType, updateTemplate));
        mutation.field(createInstances(createType, updateTemplate));
        mutation.field(update(updateType, updateTemplate));
        mutation.field(updateInstances(updateType, updateTemplate));
        mutation.field(remove());
    }

    private GraphQLInputObjectType buildCreateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(CREATE_TYPE)
                                                                                .description("Existential creation");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
                                           .description("The name of the existential")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(DOMAIN)
                                           .description("The name of the existential")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DESCRIPTION)
                                           .description("The description of the existential")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The notes of the existential")
                                           .build());
        return builder.build();
    }

    private GraphQLObjectType buildType() {
        try {
            return GraphQLAnnotations.object(Existential.class);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private GraphQLInputObjectType buildUpdateType(Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(UPDATE_TYPE)
                                                                                .description("Job update");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(ID)
                                           .description("The id of the existential")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NAME)
                                           .description("The name of the existential")
                                           .build());
        updateTemplate.put(SET_NAME, (e, value) -> e.setName((String) value));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DESCRIPTION)
                                           .description("The description of the existential")
                                           .build());
        updateTemplate.put(SET_DESCRIPTION,
                           (e, value) -> e.setDescription((String) value));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The notes of the existential")
                                           .build());
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        return builder.build();
    }

    private GraphQLFieldDefinition createInstance(GraphQLInputObjectType createType,
                                                  Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE_MUTATION)
                                   .description("Create an instance of Job")
                                   .type(new GraphQLTypeReference(EXISTENTIAL))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the existential")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newJob(env, createState,
                                                     updateTemplate);
                                   })
                                   .build();
    }

    private GraphQLFieldDefinition createInstances(GraphQLInputObjectType createType,
                                                   Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE_INSTANCES_MUTATION)
                                   .description("Create instances of Job")
                                   .type(new GraphQLList(new GraphQLTypeReference(EXISTENTIAL)))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial states of the jobs")
                                                          .type(new GraphQLNonNull(new GraphQLList(createType)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       List<Map<String, Object>> createState = (List<Map<String, Object>>) env.getArgument(STATE);
                                       return createState.stream()
                                                         .map(state -> newJob(env,
                                                                              state,
                                                                              updateTemplate))
                                                         .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    private ExistentialRecord fetch(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.EXISTENTIAL)
                       .where(Tables.EXISTENTIAL.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private GraphQLFieldDefinition instance(GraphQLObjectType type) {
        return newFieldDefinition().name(EXISTENTIAL)
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the existential")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return fetch(env);
                                   })
                                   .build();
    }

    private GraphQLFieldDefinition instances(GraphQLObjectType type) {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("existential ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return fetch(env);
                                   })
                                   .build();
    }

    private Object newJob(DataFetchingEnvironment env,
                          Map<String, Object> createState,
                          Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        ExistentialRecord ruleform = (ExistentialRecord) ctx(env).records()
                                                                 .newExistential(ExistentialDomain.valueOf((String) createState.get(DOMAIN)));
        if (ruleform == null) {
            return null;
        }
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(ruleform,
                                                            createState.get(k)));
        ruleform.update();
        return ruleform;
    }

    private GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE_MUTATION)
                                   .type(new GraphQLTypeReference(EXISTENTIAL))
                                   .description("Remove the %s facet from the instance")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                          Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE_MUTATION)
                                   .type(new GraphQLTypeReference(EXISTENTIAL))
                                   .description("Update the instance of a existential")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(type))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateJob(env, updateState,
                                                        updateTemplate);
                                   })
                                   .build();
    }

    private GraphQLFieldDefinition updateInstances(GraphQLInputObjectType type,
                                                   Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE_INSTANCES_MUTATION)
                                   .type(new GraphQLTypeReference(EXISTENTIAL))
                                   .description("Update the existential instances")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update states to apply")
                                                          .type(new GraphQLNonNull(new GraphQLList(type)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateJob(env, updateState,
                                                        updateTemplate);
                                   })
                                   .build();
    }

    private Object updateJob(DataFetchingEnvironment env,
                             Map<String, Object> updateState,
                             Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        ExistentialRecord existential = ctx(env).create()
                                                .selectFrom(Tables.EXISTENTIAL)
                                                .where(Tables.EXISTENTIAL.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                                .fetchOne();
        updateState.remove(ID);
        if (existential == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(existential,
                                                            updateState.get(k)));
        existential.update();
        return existential;
    }

}
