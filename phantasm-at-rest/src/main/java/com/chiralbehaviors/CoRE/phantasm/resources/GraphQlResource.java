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

import static com.chiralbehaviors.CoRE.kernel.product.WorkspaceOf.workspaceOf;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
import com.chiralbehaviors.CoRE.kernel.product.Plugin;
import com.chiralbehaviors.CoRE.kernel.product.Workspace;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.FacetType;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import io.dropwizard.auth.Auth;

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

    private static final Logger log = LoggerFactory.getLogger(GraphQlResource.class);

    private final ConcurrentMap<UUID, GraphQLSchema> cache             = new ConcurrentHashMap<>();
    private final ConcurrentMap<Plugin, ClassLoader> executionContexts = new ConcurrentHashMap<>();

    public GraphQlResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Timed
    @GET
    @Path("workspace")
    public List<Map<String, Object>> getWorkspaces(@Auth AuthorizedPrincipal principal) {
        return readOnly(principal, readOnlyModel -> {
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
        });
    }

    @Timed
    @Path("workspace/{workspace}")
    @POST
    public Map<String, Object> query(@Auth AuthorizedPrincipal principal,
                                     @PathParam("workspace") String workspace,
                                     QueryRequest request) {
        if (request == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        return perform(principal, model -> {
            Map<String, Object> result = new HashMap<>();
            UUID uuid = WorkspaceAccessor.uuidOf(workspace);

            GraphQLSchema schema = cache.computeIfAbsent(uuid, id -> {
                WorkspaceScope scoped;
                try {
                    scoped = model.getWorkspaceModel()
                                  .getScoped(id);
                } catch (IllegalArgumentException e) {
                    result.put("errors",
                               String.format("Workspace %s does not exist {%s}",
                                             workspace, id));
                    return null;
                }

                return build(scoped.getWorkspace(), model);
            });

            if (schema == null) {
                return result;
            }

            @SuppressWarnings("rawtypes")
            PhantasmCRUD crud = new PhantasmCRUD(model);
            ExecutionResult execute = new GraphQL(schema).execute(request.getQuery(),
                                                                  crud,
                                                                  request.getVariables());

            if (execute.getErrors()
                       .isEmpty()) {
                return execute.getData();
            }

            result.put("errors", execute.getErrors());

            log.error("Query: {} Errors: {}", request.getQuery(),
                      execute.getErrors());

            return result;
        });

    }

    private ClassLoader buildExecutionContext(Plugin plugin) {
        return Thread.currentThread()
                     .getContextClassLoader();
    }

    private Deque<NetworkAuthorization<?>> initialState(WorkspaceAccessor workspace,
                                                        Model model) {
        Product definingProduct = workspace.getDefiningProduct();
        Deque<NetworkAuthorization<?>> unresolved = new ArrayDeque<>();
        unresolved.addAll(model.getAgencyModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getAttributeModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getIntervalModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getLocationModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getProductModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getRelationshipModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getStatusCodeModel()
                               .getFacets(definingProduct));
        unresolved.addAll(model.getUnitModel()
                               .getFacets(definingProduct));
        return unresolved;
    }

    protected GraphQLSchema build(WorkspaceAccessor accessor, Model model) {
        Deque<NetworkAuthorization<?>> unresolved = initialState(accessor,
                                                                 model);
        Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved = new HashMap<>();
        Product definingProduct = accessor.getDefiningProduct();
        Workspace workspace = workspaceOf(model, definingProduct);
        Builder topLevelQuery = newObject().name("Query")
                                           .description(String.format("Top level query for %s",
                                                                      definingProduct.getName()));
        Builder topLevelMutation = newObject().name("Mutation")
                                              .description(String.format("Top level mutation for %s",
                                                                         definingProduct.getName()));
        List<Plugin> plugins = workspace.getPlugins();
        plugins.stream()
               .forEach(plugin -> executionContexts.computeIfAbsent(plugin,
                                                                    p -> buildExecutionContext(p)));
        while (!unresolved.isEmpty()) {
            NetworkAuthorization<?> facet = unresolved.pop();
            if (resolved.containsKey(facet)) {
                continue;
            }
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FacetType<?, ?> type = new FacetType(facet);
            resolved.put(facet, type);
            List<Plugin> facetPlugins = plugins.stream()
                                               .filter(plugin -> facet.getName()
                                                                      .equals(plugin.getFacetName()))
                                               .collect(Collectors.toList());
            type.build(topLevelQuery, topLevelMutation, facet, facetPlugins,
                       model, executionContexts)
                .stream()
                .filter(auth -> !resolved.containsKey(auth))
                .forEach(auth -> unresolved.add(auth));
        }
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build();
        return schema;
    }
}
