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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.IntervalType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.LocationType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeType;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitType;
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
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.annotations.DefaultTypeFunction;
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
public class ExistentialQueries {
    private static final String CREATE_INSTANCES_MUTATION = "Create%sInstances";
    private static final String CREATE_MUTATION           = "Create%s";
    private static final String CREATE_STATE              = "%sCreateState";
    private static final String DELETE_MUTATION           = "Delete%s";
    private static final String ID                        = "id";
    private static final String INSTANCES_OF_QUERY        = "InstancesOf%s";
    private static final String SET_DESCRIPTION           = "setDescription";
    private static final String SET_INDEXED               = "setIndexed";
    private static final String SET_INVERSE               = "setInverse";
    private static final String SET_KEYED                 = "setKeyed";
    private static final String SET_NAME                  = "setName";
    private static final String SET_NOTES                 = "setNotes";
    private static final String SET_PROPAGATE_CHILDREN    = "setPropagateChildren";
    private static final String SET_VALUE_TYPE            = "setValueType";
    private static final String STATE                     = "state";
    private static final String UPDATE_INSTANCES_MUTATION = "Update%sInstances";
    private static final String UPDATE_MUTATION           = "Update%s";
    private static final String UPDATE_STATE              = "Update%sState";

    static {
        DefaultTypeFunction.register(UUID.class, (u, t) -> GraphQLString);
        DefaultTypeFunction.register(ValueType.class, (u, t) -> GraphQLString);
    }

    public static void build(Builder query, Builder mutation) {
        Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate = buildUpdateTemplate();

        buildCommonExistentials(query, mutation, updateTemplate);
        buildFields(ExistentialDomain.Relationship, query, mutation,
                    updateTemplate, relationshipUpdateType(), RelationshipType,
                    relationshipCreateType());
        buildFields(ExistentialDomain.StatusCode, query, mutation,
                    updateTemplate, statusCodeUpdateType(), StatusCodeType,
                    statusCodeCreateType());
        buildFields(ExistentialDomain.Attribute, query, mutation,
                    updateTemplate, attributeUpdateType(), AttributeType,
                    attributeCreateType());
    }

    private static GraphQLInputObjectType attributeCreateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonCreateType(AttributeType);
        return builder.build();
    }

    private static GraphQLInputObjectType attributeUpdateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonUpdateType(AttributeType);
        return builder.build();
    }

    private static void buildCommonExistentials(Builder query, Builder mutation,
                                                Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        buildFields(ExistentialDomain.Agency, query, mutation, updateTemplate,
                    commonUpdateType(AgencyType).build(), AgencyType,
                    commonCreateType(AgencyType).build());
        buildFields(ExistentialDomain.Interval, query, mutation, updateTemplate,
                    commonUpdateType(IntervalType).build(), IntervalType,
                    commonCreateType(IntervalType).build());
        buildFields(ExistentialDomain.Location, query, mutation, updateTemplate,
                    commonUpdateType(LocationType).build(), LocationType,
                    commonCreateType(LocationType).build());
        buildFields(ExistentialDomain.Product, query, mutation, updateTemplate,
                    commonUpdateType(ProductType).build(), ProductType,
                    commonCreateType(ProductType).build());
        buildFields(ExistentialDomain.Unit, query, mutation, updateTemplate,
                    commonUpdateType(UnitType).build(), UnitType,
                    commonCreateType(UnitType).build());
    }

    private static void buildFields(ExistentialDomain domain, Builder query,
                                    Builder mutation,
                                    Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate,
                                    GraphQLInputObjectType updateType,
                                    GraphQLObjectType type,
                                    GraphQLInputObjectType createType) {

        query.field(instance(type));
        query.field(instances(type));
        mutation.field(createInstance(domain, type, createType,
                                      updateTemplate));
        mutation.field(createInstances(domain, type, createType,
                                       updateTemplate));
        mutation.field(update(type, updateType, updateTemplate));
        mutation.field(updateInstances(type, updateType, updateTemplate));
        mutation.field(remove(type));
    }

    private static Map<String, BiConsumer<ExistentialRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NAME, (e, value) -> e.setName((String) value));
        updateTemplate.put(SET_DESCRIPTION,
                           (e, value) -> e.setDescription((String) value));
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        updateTemplate.put(SET_INDEXED,
                           (e, value) -> e.setIndexed((Boolean) value));
        updateTemplate.put(SET_KEYED,
                           (e, value) -> e.setKeyed((Boolean) value));
        updateTemplate.put(SET_PROPAGATE_CHILDREN,
                           (e,
                            value) -> e.setPropagateChildren((Boolean) value));
        updateTemplate.put(SET_VALUE_TYPE,
                           (e,
                            value) -> e.setValueType(ValueType.valueOf((String) value)));
        updateTemplate.put(SET_INVERSE,
                           (e,
                            value) -> e.setInverse(UUID.fromString((String) value)));
        return updateTemplate;
    }

    private static graphql.schema.GraphQLInputObjectType.Builder commonCreateType(GraphQLObjectType type) {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(String.format(CREATE_STATE,
                                                                                                    type.getName()))
                                                                                .description(String.format("%s creation",
                                                                                                           type.getName()));
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
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
        return builder;
    }

    private static graphql.schema.GraphQLInputObjectType.Builder commonUpdateType(GraphQLObjectType type) {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(String.format(UPDATE_STATE,
                                                                                                    type.getName()))
                                                                                .description(String.format("%s update",
                                                                                                           type.getName()));
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(ID)
                                           .description("The id of the existential")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NAME)
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
        return builder;
    }

    private static GraphQLFieldDefinition createInstance(ExistentialDomain domain,
                                                         GraphQLObjectType type,
                                                         GraphQLInputObjectType createType,
                                                         Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                       type.getName()))
                                   .description("Create an instance of Job")
                                   .type(new GraphQLTypeReference(type.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the existential")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newExistential(domain, env,
                                                             createState,
                                                             updateTemplate);
                                   })
                                   .build();
    }

    private static GraphQLFieldDefinition createInstances(ExistentialDomain domain,
                                                          GraphQLObjectType type,
                                                          GraphQLInputObjectType createType,
                                                          Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(String.format(CREATE_INSTANCES_MUTATION,
                                                       type.getName()))
                                   .description("Create instances of Job")
                                   .type(new GraphQLList(new GraphQLTypeReference(type.getName())))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial states of the jobs")
                                                          .type(new GraphQLNonNull(new GraphQLList(createType)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       List<Map<String, Object>> createState = (List<Map<String, Object>>) env.getArgument(STATE);
                                       return createState.stream()
                                                         .map(state -> newExistential(domain,
                                                                                      env,
                                                                                      state,
                                                                                      updateTemplate))
                                                         .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    private static ExistentialRecord fetch(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.EXISTENTIAL)
                       .where(Tables.EXISTENTIAL.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance(GraphQLObjectType type) {
        return newFieldDefinition().name(type.getName())
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

    private static GraphQLFieldDefinition instances(GraphQLObjectType type) {
        return newFieldDefinition().name(String.format(INSTANCES_OF_QUERY,
                                                       type.getName()))
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

    private static Object newExistential(ExistentialDomain domain,
                                         DataFetchingEnvironment env,
                                         Map<String, Object> createState,
                                         Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        ExistentialRecord ruleform = (ExistentialRecord) ctx(env).records()
                                                                 .newExistential(domain);
        if (ruleform == null) {
            return null;
        }
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(ruleform,
                                                            createState.get(k)));
        ruleform.update();
        return ruleform;
    }

    private static GraphQLInputObjectType relationshipCreateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonCreateType(RelationshipType);
        return builder.build();
    }

    private static GraphQLInputObjectType relationshipUpdateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonUpdateType(RelationshipType);
        return builder.build();
    }

    private static GraphQLFieldDefinition remove(GraphQLObjectType type) {
        return newFieldDefinition().name(String.format(DELETE_MUTATION,
                                                       type.getName()))
                                   .type(new GraphQLTypeReference(String.format(type.getName())))
                                   .description("Remove the existential")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static GraphQLInputObjectType statusCodeCreateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonCreateType(StatusCodeType);
        return builder.build();
    }

    private static GraphQLInputObjectType statusCodeUpdateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = commonUpdateType(StatusCodeType);
        return builder.build();
    }

    private static GraphQLFieldDefinition update(GraphQLObjectType type,
                                                 GraphQLInputObjectType inputType,
                                                 Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(String.format(UPDATE_MUTATION,
                                                       type.getName()))
                                   .type(new GraphQLTypeReference(type.getName()))
                                   .description("Update the instance of an existential")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(inputType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateJob(env, updateState,
                                                        updateTemplate);
                                   })
                                   .build();
    }

    private static GraphQLFieldDefinition updateInstances(GraphQLObjectType type,
                                                          GraphQLInputObjectType inputType,
                                                          Map<String, BiConsumer<ExistentialRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(String.format(UPDATE_INSTANCES_MUTATION,
                                                       type.getName()))
                                   .type(new GraphQLTypeReference(type.getName()))
                                   .description("Update the existential instances")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update states to apply")
                                                          .type(new GraphQLNonNull(new GraphQLList(inputType)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateJob(env, updateState,
                                                        updateTemplate);
                                   })
                                   .build();
    }

    private static Object updateJob(DataFetchingEnvironment env,
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
