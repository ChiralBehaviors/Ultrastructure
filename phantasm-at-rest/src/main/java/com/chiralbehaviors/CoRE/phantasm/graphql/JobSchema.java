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

import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.meta.Model;

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
public class JobSchema {
    private static final String ASSIGN_TO                 = "assignTo";
    private static final String CREATE_INSTANCES_MUTATION = "CreateInstancesOfJob";
    private static final String CREATE_MUTATION           = "CreateJob";
    private static final String CREATE_TYPE               = "JobCreate";
    private static final String DELETE_MUTATION           = "DeleteJob";
    private static final String DELIVER_FROM              = "deliverFrom";
    private static final String DELIVER_TO                = "deliverTo";
    private static final String ID                        = "id";
    private static final String INSTANCES_OF_QUERY        = "InstancesOfJob";
    private static final String JOB                       = "Job";
    private static final String NOTES                     = "notes";
    private static final String PARENT                    = "parent";
    private static final String PRODUCT                   = "product";
    private static final String QUANTITY                  = "quantity";
    private static final String QUANTITY_UNIT             = "quantityUnit";
    private static final String REQUESTER                 = "requester";
    private static final String SERVICE                   = "service";
    private static final String SET_ASSIGN_TO             = "setAssignTo";
    private static final String SET_DELIVER_FROM          = "setDeliverFrom";
    private static final String SET_DELIVER_TO            = "setDeliverTo";
    private static final String SET_NOTES                 = "setNotes";
    private static final String SET_PRODUCT               = "setProduct";
    private static final String SET_QUANTITY              = "setQuantity";
    private static final String SET_QUANTITY_UNIT         = "setQuantityUnit";
    private static final String SET_REQUESTER             = "setRequester";
    private static final String SET_SERVICE               = "setService";
    private static final String SET_STATUS                = "setStatus";
    private static final String STATE                     = "state";
    private static final String STATUS                    = "status";
    private static final String UPDATE_INSTANCES_MUTATION = "UpdateInstancesOfJob";
    private static final String UPDATE_MUTATION           = "UpdateJob";
    private static final String UPDATE_TYPE               = "JobUpdate";
    private static final String UPDATED_BY                = "updatedBy";

    public static GraphQLSchema build() {
        Builder topLevelQuery = newObject().name("Query")
                                           .description("Top level query");
        Builder topLevelMutation = newObject().name("Mutation")
                                              .description("Top level mutation");
        new JobSchema().build(topLevelQuery, topLevelMutation);
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build();
        return schema;
    }

    public void build(Builder query, Builder mutation) {
        Map<String, BiConsumer<JobRecord, Object>> updateTemplate = new HashMap<>();

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
        graphql.schema.GraphQLInputObjectType.Builder createTypeBuilder;
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                JOB))
                                            .description("Job Creation");
        return createTypeBuilder.build();
    }

    private GraphQLObjectType buildType() {
        Builder builder = newObject().name(JOB)
                                     .description("A Job");
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(ID)
                                          .description("The id of the job instance")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(ASSIGN_TO)
                                          .description("The agency assigned to this job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(DELIVER_FROM)
                                          .description("The location the job's product is delivered from")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(DELIVER_TO)
                                          .description("The location the job's product is delivered to")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLFloat)
                                          .name(QUANTITY)
                                          .description("The job quantity")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(QUANTITY_UNIT)
                                          .description("The unit of the job quantity")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(REQUESTER)
                                          .description("The agency requesting the job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(SERVICE)
                                          .description("The service the job performs")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(STATUS)
                                          .description("The status of the job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(NOTES)
                                          .description("The notes of the job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(UPDATED_BY)
                                          .description("The agency that updated the job")
                                          .build());
        builder.field(newFieldDefinition().type(new GraphQLTypeReference(JOB))
                                          .name(PARENT)
                                          .description("The parent of the job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name(PRODUCT)
                                          .description("The product of the job")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLInt)
                                          .name("depth")
                                          .description("The depth of the job relative to its parent")
                                          .build());
        builder.field(newFieldDefinition().type(new GraphQLList(new GraphQLTypeReference(JOB)))
                                          .name("children")
                                          .dataFetcher(env -> ctx(env).getJobModel()
                                                                      .getChildren(job(env)))
                                          .description("The children of the job")
                                          .build());
        builder.field(newFieldDefinition().type(new GraphQLList(new GraphQLTypeReference(JOB)))
                                          .name("activeChildren")
                                          .dataFetcher(env -> ctx(env).getJobModel()
                                                                      .getActiveSubJobsOf(job(env)))
                                          .description("The children of the job")
                                          .build());
        return builder.build();
    }

    private GraphQLInputObjectType buildUpdateType(Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                                                    JOB))
                                                                                .description("Job update");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(ID)
                                           .description("The id of the updated job instance")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_ASSIGN_TO)
                                           .description("The agency assigned to this job")
                                           .build());
        updateTemplate.put(SET_ASSIGN_TO,
                           (job,
                            value) -> job.setAssignTo(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DELIVER_FROM)
                                           .description("The location the job's product is delivered from")
                                           .build());
        updateTemplate.put(SET_DELIVER_FROM,
                           (job,
                            value) -> job.setDeliverFrom(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DELIVER_TO)
                                           .description("The location the job's product is delivered to")
                                           .build());
        updateTemplate.put(SET_DELIVER_TO,
                           (job,
                            value) -> job.setDeliverTo(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLFloat)
                                           .name(SET_QUANTITY)
                                           .description("The job quantity")
                                           .build());
        updateTemplate.put(SET_QUANTITY,
                           (job,
                            value) -> job.setQuantity(BigDecimal.valueOf(((Double) value))));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_QUANTITY_UNIT)
                                           .description("The unit of the job quantity")
                                           .build());
        updateTemplate.put(SET_QUANTITY_UNIT,
                           (job,
                            value) -> job.setQuantityUnit(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_REQUESTER)
                                           .description("The agency requesting the job")
                                           .build());
        updateTemplate.put(SET_REQUESTER,
                           (job,
                            value) -> job.setRequester(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_SERVICE)
                                           .description("The service performed")
                                           .build());
        updateTemplate.put(SET_SERVICE,
                           (job,
                            value) -> job.setService(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_STATUS)
                                           .description("The status of the job")
                                           .build());
        updateTemplate.put(SET_STATUS,
                           (job,
                            value) -> job.setStatus(UUID.fromString((String) value)));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The job's notes")
                                           .build());
        updateTemplate.put(SET_NOTES,
                           (job, value) -> job.setNotes((String) value));
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_PRODUCT)
                                           .description("The job's product")
                                           .build());
        updateTemplate.put(SET_PRODUCT,
                           (job,
                            value) -> job.setProduct(UUID.fromString((String) value)));
        return builder.build();
    }

    private GraphQLFieldDefinition createInstance(GraphQLInputObjectType createType,
                                                  Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE_MUTATION)
                                   .description("Create an instance of Job")
                                   .type(new GraphQLTypeReference(JOB))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the job")
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
                                                   Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE_INSTANCES_MUTATION)
                                   .description("Create instances of Job")
                                   .type(new GraphQLList(new GraphQLTypeReference(JOB)))
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
        return (Model) env.getContext();
    }

    private JobRecord fetchJob(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.JOB)
                       .where(Tables.JOB.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private GraphQLFieldDefinition instance(GraphQLObjectType type) {
        return newFieldDefinition().name(JOB)
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the job")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return fetchJob(env);
                                   })
                                   .build();
    }

    private GraphQLFieldDefinition instances(GraphQLObjectType type) {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("job ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return fetchJob(env);
                                   })
                                   .build();
    }

    private JobRecord job(DataFetchingEnvironment env) {
        return (JobRecord) env.getSource();
    }

    private Object newJob(DataFetchingEnvironment env,
                          Map<String, Object> createState,
                          Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        JobRecord job = ctx(env).records()
                                .newJob();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(job,
                                                            createState.get(k)));
        job.insert();
        return job;
    }

    private GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE_MUTATION)
                                   .type(new GraphQLTypeReference(JOB))
                                   .description("Remove the %s facet from the instance")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetchJob(env).delete())
                                   .build();
    }

    private GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                          Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE_MUTATION)
                                   .type(new GraphQLTypeReference(JOB))
                                   .description("Update the instance of a job")
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
                                                   Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE_INSTANCES_MUTATION)
                                   .type(new GraphQLTypeReference(JOB))
                                   .description("Update the job instances")
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
                             Map<String, BiConsumer<JobRecord, Object>> updateTemplate) {
        JobRecord job = (JobRecord) ctx(env);
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(job,
                                                            updateState.get(k)));
        job.update();
        return job;
    }
}
