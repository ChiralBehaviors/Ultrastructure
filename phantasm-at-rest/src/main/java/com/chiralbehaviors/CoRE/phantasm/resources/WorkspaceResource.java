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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
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
import org.jooq.UpdatableRecord;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.UuidUtil;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.MetaContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.PhantasmContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;

import graphql.ExecutionResult;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import io.dropwizard.auth.Auth;

/**
 * A resource providing GraphQL queries on schemas generated from workspaces
 *
 * @author hhildebrand
 *
 */
@Path("workspace")
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
public class WorkspaceResource extends TransactionalResource {
    private static final Logger log       = LoggerFactory.getLogger(WorkspaceResource.class);
    static final String         QUERY     = "query";
    static final String         VARIABLES = "variables";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> getVariables(Map request) {
        Map<String, Object> variables = null;
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
        return variables == null ? Collections.emptyMap() : variables;
    }

    private final ConcurrentMap<UUID, GraphQLSchema> cache = new ConcurrentHashMap<>();
    private final URLClassLoader                     executionScope;
    private final GraphQLSchema                      metaSchema;
    private final ObjectMapper                       objectMapper;
    private final Reflections                        reflections;
    {
        try {
            metaSchema = new WorkspaceSchema().buildMeta();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public WorkspaceResource() {
        this(new URLClassLoader(new URL[0],
                                WorkspaceResource.class.getClassLoader()));
    }

    public WorkspaceResource(URLClassLoader executionScope) {
        this.executionScope = executionScope;
        objectMapper = new ObjectMapper().registerModule(new CoREModule());
        ClassLoader prev = Thread.currentThread()
                                 .getContextClassLoader();
        Thread.currentThread()
              .setContextClassLoader(executionScope);
        try {
            reflections = new Reflections(new ConfigurationBuilder().addClassLoader(executionScope));
        } finally {
            Thread.currentThread()
                  .setContextClassLoader(prev);
        }
    }

    @Timed
    @GET
    public List<Map<String, Object>> getWorkspaces(@Auth AuthorizedPrincipal principal,
                                                   @Context DSLContext create) {
        return mutate(principal, readOnlyModel -> {
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
                wsp.put("IRI",
                        readOnlyModel.wrap(Workspace.class, definingProduct)
                                     .getIRI());
                wsp.put("version", definingProduct.getVersion());
                workspaces.add(wsp);
            }
            return workspaces;
        }, create);
    }

    @Timed
    @Path("loadSnapshot")
    @POST
    public void loadSnapshot(@Auth AuthorizedPrincipal principal,
                             @Context HttpServletRequest request,
                             InputStream requestBody,
                             @Context DSLContext create) {
        mutate(principal, model -> {
            Agency p = model.getCurrentPrincipal()
                            .getPrincipal();
            StateSnapshot snapshot;
            try {
                snapshot = objectMapper.readValue(requestBody,
                                                  StateSnapshot.class);
            } catch (IOException e) {
                log.info(String.format("Failed deserializing snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Faild deserializing snapshot: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }

            try {
                snapshot.load(create);
            } catch (Exception e) {
                log.info(String.format("Failed loading workspace snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Failed loading snapshot: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }
            return null;
        }, create);
    }

    @Timed
    @Path("loadWorkspace")
    @POST
    public String loadWorkspace(@Auth AuthorizedPrincipal principal,
                                @Context HttpServletRequest request,
                                InputStream requestBody,
                                @Context DSLContext create) {
        return mutate(principal, model -> {
            Agency p = model.getCurrentPrincipal()
                            .getPrincipal();
            WorkspaceSnapshot snapshot;
            try {
                snapshot = objectMapper.readValue(requestBody,
                                                  WorkspaceSnapshot.class);
            } catch (IOException e) {
                log.info(String.format("Failed deserializing workspace snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Failed deserializing workspace: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }

            try {
                snapshot.load(create);
            } catch (Exception e) {
                log.info(String.format("Failed loading workspace snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Cannot load workspace: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }
            Product definingProduct = snapshot.getDefiningProduct();

            Workspace workspace = model.wrap(Workspace.class, definingProduct);
            return workspace.getIRI();
        }, create);
    }

    @Timed
    @Path("manifest")
    @POST
    public String manifest(@Auth AuthorizedPrincipal principal,
                           @Context HttpServletRequest request,
                           InputStream requestBody,
                           @Context DSLContext create) {
        return mutate(principal, model -> {
            Agency p = model.getCurrentPrincipal()
                            .getPrincipal();

            WorkspaceImporter manifest;
            try {
                manifest = WorkspaceImporter.manifest(requestBody, model);
            } catch (IOException e) {
                log.info(String.format("Failed deserializing workspace snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Failed deserializing workspace: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }

            Product definingProduct = manifest.getWorkspace()
                                              .getDefiningProduct();
            Workspace workspace = model.wrap(Workspace.class, definingProduct);
            return workspace.getIRI();
        }, create);
    }

    @Path("{workspace}")
    @GET
    @Produces({ MediaType.TEXT_HTML })
    public String workspaceGraphIql(@Auth AuthorizedPrincipal principal,
                                    @PathParam("workspace") String workspace) {
        try {
            return Utils.getDocument(getClass().getResourceAsStream("/workspace-explorer/index.html"));
        } catch (IOException e) {
            log.error("unable to serve graphIql", e);
            throw new WebApplicationException(500);
        }
    }

    @Path("{workspace}/meta")
    @GET
    @Produces({ MediaType.TEXT_HTML })
    public String workspaceMetaGraphIql(@Auth AuthorizedPrincipal principal,
                                        @PathParam("workspace") String workspace) {
        try {
            return Utils.getDocument(getClass().getResourceAsStream("/workspace-explorer/index.html"));
        } catch (IOException e) {
            log.error("unable to serve graphIql", e);
            throw new WebApplicationException(500);
        }
    }

    @Timed
    @Path("{workspace}")
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
            UUID uuid = uuidFrom(workspace);

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
                if (scoped == null) { 
                    throw new WebApplicationException(String.format("Workspace not found [%s] %s",
                                                                    uuid,
                                                                    workspace),
                                                      Status.NOT_FOUND);
                }
                ClassLoader prev = Thread.currentThread()
                                         .getContextClassLoader();
                Thread.currentThread()
                      .setContextClassLoader(executionScope);
                try {
                    return new WorkspaceSchema().build(scoped.getWorkspace(),
                                                       model,
                                                       reflections.getTypesAnnotatedWith(Plugin.class));
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Unable to buidl schema for %s",
                                                                  scoped.getWorkspace()
                                                                        .getDefiningProduct()
                                                                        .getName()),
                                                    e);
                } finally {
                    Thread.currentThread()
                          .setContextClassLoader(prev);
                }
            });

            if (schema == null) {
                throw new WebApplicationException(String.format("Workspace not found [%s] %s",
                                                                uuid,
                                                                workspace),
                                                  Status.NOT_FOUND);
            }

            Product definingProduct = model.records()
                                           .resolve(uuid);
            WorkspaceContext crud = new WorkspaceContext(model,
                                                         definingProduct);
            if (!model.checkRead((UpdatableRecord<?>) definingProduct)
                || !model.checkPermission((ExistentialRuleform) definingProduct,
                                          model.getKernel()
                                               .getEXECUTE_QUERY())) {
                Agency p = model.getCurrentPrincipal()
                                .getPrincipal();
                log.info(String.format("Failed executing query on workspace [%s:%s] by: %s:%s",
                                       definingProduct.getName(), uuid,
                                       p.getName(), p.getId()));
                return null;
            }
            Map<String, Object> variables = getVariables(request);

            ClassLoader prev = Thread.currentThread()
                                     .getContextClassLoader();
            Thread.currentThread()
                  .setContextClassLoader(executionScope);
            try {
                return execute(schema, crud, (String) request.get(QUERY),
                               variables);
            } catch (GraphQLException e) {
                throw new WebApplicationException(String.format("Error executing: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            } finally {
                Thread.currentThread()
                      .setContextClassLoader(prev);
            }
        }, create);
    }

    private UUID uuidFrom(String encoded) {
        if (encoded.length() == 22) {
            return UuidUtil.decode(encoded);
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(encoded);
        } catch (IllegalArgumentException e) {
            uuid = WorkspaceAccessor.uuidOf(encoded);
        }
        return uuid;
    }

    @Timed
    @Path("{workspace}/meta")
    @POST
    public ExecutionResult queryMeta(@Auth AuthorizedPrincipal principal,
                                     @PathParam("workspace") String workspace,
                                     @SuppressWarnings("rawtypes") Map request,
                                     @Context DSLContext create) {
        if (request == null) {
            throw new WebApplicationException("Query request cannot be null",
                                              Status.BAD_REQUEST);
        }
        if (request.get(QUERY) == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        return mutate(principal, model -> {
            UUID uuid = uuidFrom(workspace);

            Product definingProduct = model.records()
                                           .resolve(uuid);
            if (definingProduct == null) {
                throw new WebApplicationException(String.format("Workspace not found [%s] %s",
                                                                uuid,
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            MetaContext crud = new MetaContext(model, definingProduct);
            if (!model.checkRead((UpdatableRecord<?>) definingProduct)
                || !model.checkRead((UpdatableRecord<?>) definingProduct)) {
                Agency p = model.getCurrentPrincipal()
                                .getPrincipal();
                log.info(String.format("Failed executing query on workspace [%s:%s] by: %s:%s",
                                       definingProduct.getName(), uuid,
                                       p.getName(), p.getId()));
                return null;
            }
            Map<String, Object> variables = getVariables(request);
            ExecutionResult result = crud.execute(metaSchema,
                                                  (String) request.get(QUERY),
                                                  variables);
            if (result.getErrors()
                      .isEmpty()) {
                return result;
            }
            return result;
        }, create);
    }

    @Timed
    @Path("{workspace}/serialize")
    @GET
    public String serializeWorkspace(@Auth AuthorizedPrincipal principal,
                                     @PathParam("workspace") String workspace,
                                     @Context DSLContext create) {
        return readOnly(principal, model -> {
            Agency p = model.getCurrentPrincipal()
                            .getPrincipal();
            UUID uuid = WorkspaceAccessor.uuidOf(workspace);
            Product definingProduct = model.records()
                                           .resolve(uuid);
            WorkspaceSnapshot snapshot = new WorkspaceSnapshot(definingProduct,
                                                               model.create());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                snapshot.serializeTo(baos);
            } catch (Exception e) {
                log.info(String.format("Failed serializing workspace snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Failed serializing workspace snapshot: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }
            return baos.toString();
        }, create);
    }

    @Timed
    @Path("snapshot")
    @GET
    public String snapshot(@Auth AuthorizedPrincipal principal,
                           @Context DSLContext create) {
        return readOnly(principal, model -> {
            Agency p = model.getCurrentPrincipal()
                            .getPrincipal();
            WorkspaceSnapshot snapshot = model.snapshot();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                snapshot.serializeTo(baos);
            } catch (Exception e) {
                log.info(String.format("Failed serializing snapshot by: %s:%s",
                                       p.getName(), p.getId()),
                         e);
                throw new WebApplicationException(String.format("Failed serializing snapshot: %s",
                                                                e.getMessage()),
                                                  Status.BAD_REQUEST);
            }
            return baos.toString();
        }, create);
    }

    private ExecutionResult execute(GraphQLSchema schema, PhantasmContext crud,
                                    String query,
                                    Map<String, Object> variables) {
        ExecutionResult result = crud.execute(schema, query, variables);
        if (result.getErrors()
                  .isEmpty()) {
            return result;
        }
        return result;
    }
}
