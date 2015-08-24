/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.phantasm.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchemaBuilder;
import com.chiralbehaviors.CoRE.product.Product;
import com.codahale.metrics.annotation.Timed;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * A resource providing GraphQL queries on schemas generated from workspaces
 * 
 * @author hhildebrand
 *
 */
@Path("graphql")
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
public class GraphQlResource extends TransactionalResource {
    private static final Logger log = LoggerFactory.getLogger(GraphQlResource.class);

    public static class QueryRequest {
        private String              query;
        private Map<String, Object> variables = Collections.emptyMap();

        public QueryRequest() {
        }

        public QueryRequest(String query, Map<String, Object> variables) {
            this.query = query;
            this.variables = variables;
        }

        public String getQuery() {
            return query;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }
    }

    public GraphQlResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Timed
    @GET
    @Path("workspace")
    public List<Map<String, Object>> getWorkspaces() {
        Kernel kernel = readOnlyModel.getKernel();
        Aspect<Product> aspect = new Aspect<>(kernel.getIsA(),
                                              kernel.getWorkspace());
        List<Map<String, Object>> workspaces = new ArrayList<>();
        for (Product definingProduct : readOnlyModel.getProductModel()
                                                    .getChildren(aspect.getClassification(),
                                                                 aspect.getClassifier()
                                                                       .getInverse())) {
            Map<String, Object> wsp = new TreeMap<>();
            wsp.put("id", definingProduct.getId()
                                         .toString());
            wsp.put("name", definingProduct.getName());
            wsp.put("description", definingProduct.getDescription());
            workspaces.add(wsp);
        }
        return workspaces;
    }

    @Timed
    @Path("workspace/{workspace}")
    @POST
    public Map<String, Object> query(@PathParam("workspace") String workspace,
                                     QueryRequest request) {
        if (request == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        WorkspaceSchemaBuilder builder;
        try {
            builder = new WorkspaceSchemaBuilder(workspace, readOnlyModel);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), Status.NOT_FOUND);
        }
        GraphQLSchema schema = builder.build();
        WorkspaceContext ctx = new WorkspaceContext(new PhantasmCRUD(() -> readOnlyModel));
        ExecutionResult execute = new GraphQL(schema).execute(request.getQuery(),
                                                              ctx,
                                                              request.getVariables());
        if (execute.getErrors()
                   .isEmpty()) {
            return execute.getData();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("errors", execute.getErrors());
        log.error("Query: {} Errors: {}", request.getQuery(),
                  execute.getErrors());
        return result;
    }
}
