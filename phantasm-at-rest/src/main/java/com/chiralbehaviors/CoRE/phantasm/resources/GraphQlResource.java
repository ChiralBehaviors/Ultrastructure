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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.graphql.FacetType;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionResult;
import graphql.GraphQL;
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

    private static final Logger                      log       = LoggerFactory.getLogger(GraphQlResource.class);
    static final String                              QUERY     = "query";
    static final String                              VARIABLES = "variables";

    private final ConcurrentMap<UUID, GraphQLSchema> cache     = new ConcurrentHashMap<>();
    private final ClassLoader                        executionScope;

    public GraphQlResource(ClassLoader executionScope) {
        this.executionScope = executionScope;
    }

    @Timed
    @GET
    @Path("workspace")
    public List<Map<String, Object>> getWorkspaces(@Auth AuthorizedPrincipal principal,
                                                   @Context DSLContext create) {
        return read(principal, readOnlyModel -> {
            Kernel kernel = readOnlyModel.getKernel();
            List<Map<String, Object>> workspaces = new ArrayList<>();
            for (ExistentialRuleform definingProduct : readOnlyModel.getPhantasmModel()
                                                                    .getChildrenUuid(kernel.getWorkspace()
                                                                                           .getId(),
                                                                                     kernel.getIsA()
                                                                                           .getInverse(),
                                                                                     ExistentialDomain.Product)) {
                Map<String, Object> wsp = new TreeMap<>();
                wsp.put("id", definingProduct.getId()
                                             .toString());
                wsp.put("name", definingProduct.getName());
                wsp.put("description", definingProduct.getDescription());
                workspaces.add(wsp);
            }
            return workspaces;
        }, create);
    }

    @Timed
    @Path("workspace/{workspace}")
    @POST
    public ExecutionResult query(@Auth AuthorizedPrincipal principal,
                                 @PathParam("workspace") String workspace,
                                 @SuppressWarnings("rawtypes") Map request,
                                 @Context DSLContext create) {
        if (request == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        if (request.get(QUERY) == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        return mutate(principal, model -> {
            UUID uuid = WorkspaceAccessor.uuidOf(workspace);

            GraphQLSchema schema = cache.computeIfAbsent(uuid, id -> {
                WorkspaceScope scoped;
                try {
                    scoped = model.getWorkspaceModel()
                                  .getScoped(id);
                } catch (IllegalArgumentException e) {
                    throw new WebApplicationException(String.format("Workspace not found [%s] %s",
                                                                    id,
                                                                    workspace),
                                                      Status.NOT_FOUND);
                }

                return FacetType.build(scoped.getWorkspace(), model,
                                       executionScope);
            });

            if (schema == null) {
                throw new WebApplicationException(String.format("Workspace not found [%s] %s",
                                                                uuid,
                                                                workspace),
                                                  Status.NOT_FOUND);
            }

            PhantasmCRUD crud = new PhantasmCRUD(model);
            Product definingProduct = model.records()
                                           .resolve(uuid);
            if (!model.getPhantasmModel()
                      .checkCapability(definingProduct, crud.getREAD())
                || !model.getPhantasmModel()
                         .checkCapability(definingProduct, model.getKernel()
                                                                .getEXECUTE_QUERY())) {
                Agency p = model.getCurrentPrincipal()
                                .getPrincipal();
                log.info(String.format("Failed executing query on workspace [%s:%s] by: %s:%s",
                                       definingProduct.getName(), uuid,
                                       p.getName(), p.getId()));
                return null;
            }
            Map<String, Object> variables = getVariables(request);
            ExecutionResult result = new GraphQL(schema).execute((String) request.get(QUERY),
                                                                 crud,
                                                                 variables);
            if (result.getErrors()
                      .isEmpty()) {
                return result;
            }
            log.info("Query: {} Errors: {}", request.get(QUERY),
                     result.getErrors());
            return result;
        }, create);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, Object> getVariables(Map request) {
        Map<String, Object> variables = Collections.emptyMap();
        Object provided = request.get(VARIABLES);
        if (provided != null) {
            if (provided instanceof Map) {
                variables = (Map<String, Object>) provided;
            } else if (provided instanceof String) {
                try {
                    String variableString = ((String) provided).trim();
                    if (!variableString.isEmpty()) {
                        variables = new ObjectMapper().readValue(variableString,
                                                                 Map.class);
                    }
                } catch (Exception e) {
                    throw new WebApplicationException(String.format("Cannot deserialize variables: %s",
                                                                    e.getMessage()),
                                                      Status.BAD_REQUEST);
                }
            } else {
                throw new WebApplicationException("Invalid variables parameter",
                                                  Status.BAD_REQUEST);
            }
        }
        return variables;
    }
}
