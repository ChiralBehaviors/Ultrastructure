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
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Plugin;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class JobType {
    private static final String                                                             ASSIGN_TO         = "assignTo";
    private static final String                                                             CREATE_TYPE       = "JobCreate";
    private static final String                                                             DELIVER_FROM      = "deliverFrom";
    private static final String                                                             DELIVER_TO        = "deliverTo";
    private static final String                                                             ID                = "id";
    private static final String                                                             NOTES             = "notes";
    private static final String                                                             PARENT            = "parent";
    private static final String                                                             PRODUCT           = "product";
    private static final String                                                             QUANTITY          = "quantity";
    private static final String                                                             QUANTITY_UNIT     = "quantityUnit";
    private static final String                                                             REQUESTOR         = "requestor";
    private static final String                                                             SERVICE           = "service";
    private static final String                                                             SET_ASSIGN_TO     = "setAssignTo";
    private static final String                                                             SET_DELIVER_FROM  = "setDeliverFrom";
    private static final String                                                             SET_DELIVER_TO    = "setDeliverTo";

    private static final String                                                             SET_NOTES         = "setNotes";
    private static final String                                                             SET_PRODUCT       = "setProduct";
    private static final String                                                             SET_QUANTITY      = "setQuantity";
    private static final String                                                             SET_QUANTITY_UNIT = "setQuantityUnit";
    private static final String                                                             SET_REQUESTOR     = "setRequestor";
    private static final String                                                             SET_SERVICE       = "setService";
    private static final String                                                             SET_STATUS        = "setStatus";
    private static final String                                                             STATUS            = "status";
    private static final String                                                             UPDATE_TYPE       = "JobUpdate";
    private static final String                                                             UPDATED_BY        = "updatedBy";

    @SuppressWarnings("unused")
    private graphql.schema.GraphQLInputObjectType.Builder                                   createTypeBuilder;
    private Builder                                                                         typeBuilder;
    @SuppressWarnings("unused")
    private Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> updateTemplate    = new HashMap<>();

    private graphql.schema.GraphQLInputObjectType.Builder                                   updateTypeBuilder;

    public JobType() {
        typeBuilder = newObject().name("Job")
                                 .description("A Job");
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                "Job"))
                                            .description("Job update");
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                "Job"))
                                            .description("Job Creation");
    }

    public void build(Builder query, Builder mutation, FacetRecord facet,
                      List<Plugin> plugins, Model model,
                      ClassLoader executionScope) {
        build();

        query.field(instance());
        query.field(instances());

        mutation.field(createInstance());
        mutation.field(createInstances());
        mutation.field(update());
        mutation.field(updateInstances());
        mutation.field(remove());
    }

    private void build() {
        buildType();
        buildUpdateType();
    }

    private void buildType() {
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(ID)
                                              .description("The id of the job instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(ASSIGN_TO)
                                              .description("The agency assigned to this job")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(DELIVER_FROM)
                                              .description("The location the job's product is delivered from")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(DELIVER_TO)
                                              .description("The location the job's product is delivered to")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLFloat)
                                              .name(QUANTITY)
                                              .description("The job quantity")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(QUANTITY_UNIT)
                                              .description("The unit of the job quantity")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(REQUESTOR)
                                              .description("The agency requesting the job")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(SERVICE)
                                              .description("The service performed")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(STATUS)
                                              .description("The status of the job")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(NOTES)
                                              .description("The job's notes")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(UPDATED_BY)
                                              .description("The agency that updated the job")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(new GraphQLTypeReference("Job"))
                                              .name(PARENT)
                                              .description("The job's parent")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(PRODUCT)
                                              .description("The job's product")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLInt)
                                              .name("depth")
                                              .description("The depth of the job relative to its parent")
                                              .build());
    }

    private void buildUpdateType() {
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                                     .name(ID)
                                                     .description("The id of the updated job instance")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_ASSIGN_TO)
                                                     .description("The agency assigned to this job")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_DELIVER_FROM)
                                                     .description("The location the job's product is delivered from")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_DELIVER_TO)
                                                     .description("The location the job's product is delivered to")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLFloat)
                                                     .name(SET_QUANTITY)
                                                     .description("The job quantity")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_QUANTITY_UNIT)
                                                     .description("The unit of the job quantity")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_REQUESTOR)
                                                     .description("The agency requesting the job")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_SERVICE)
                                                     .description("The service performed")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_STATUS)
                                                     .description("The status of the job")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_NOTES)
                                                     .description("The job's notes")
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_PRODUCT)
                                                     .description("The job's product")
                                                     .build());
    }

    private GraphQLFieldDefinition createInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition createInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition instance() {
        @SuppressWarnings("unused")
        GraphQLObjectType type = typeBuilder.build();
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition instances() {
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition remove() {
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition update() {
        // TODO Auto-generated method stub
        return null;
    }

    private GraphQLFieldDefinition updateInstances() {
        // TODO Auto-generated method stub
        return null;
    }
}
