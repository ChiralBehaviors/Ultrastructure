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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Facet;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/workspace")
@Produces({ "application/json", "text/json" })
public class WorkspaceResource extends TransactionalResource {

    private static final String DEFINING_PRODUCT = "definingProduct";
    private static final String KEYS             = "keys";

    public static String keysIri(UUID definingProduct, UriInfo uriInfo) {
        UriBuilder ub = UriBuilder.fromResource(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("getKeys",
                                                      AuthorizedPrincipal.class,
                                                      UUID.class));
            ub.resolveTemplate("workspace", definingProduct);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build()
                 .toASCIIString();
    }

    public static URI lookupIri(UUID definingProduct, String key,
                                UriInfo uriInfo) {
        UriBuilder ub = UriBuilder.fromResource(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("lookup",
                                                      AuthorizedPrincipal.class,
                                                      UUID.class,
                                                      String.class));
            ub.resolveTemplate("workspace", definingProduct);
            ub.resolveTemplate("member", key);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build();
    }

    public static Ruleform resolve(String qualifiedName, WorkspaceScope scope) {
        StringTokenizer key = new StringTokenizer(qualifiedName, "|");
        if (key.countTokens() == 1) {
            Ruleform resolved = scope.lookup(key.nextToken());
            if (resolved == null) {
                throw new WebApplicationException(String.format("The workspace key [%s] is not defined in %s",
                                                                qualifiedName,
                                                                scope));
            }
            return Ruleform.initializeAndUnproxy(resolved);
        } else if (key.countTokens() == 2) {
            Ruleform resolved = scope.lookup(key.nextToken(), key.nextToken());
            if (resolved == null) {
                throw new WebApplicationException(String.format("The workspace key [%s] is not defined in %s",
                                                                qualifiedName,
                                                                scope));
            }
            return Ruleform.initializeAndUnproxy(resolved);
        } else {
            throw new WebApplicationException(String.format("The workspace key [%s] is not defined in %s",
                                                            qualifiedName,
                                                            scope));
        }
    }

    public static UUID toUUID(String workspace) {
        UUID workspaceUUID;
        try {
            workspaceUUID = UUID.fromString(workspace);
        } catch (IllegalArgumentException e) {
            workspaceUUID = WorkspaceAccessor.uuidOf(workspace);
        }
        return workspaceUUID;
    }

    public static URI workspaceIri(UUID definingProduct, UriInfo uriInfo) {
        UriBuilder ub = UriBuilder.fromResource(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("getWorkspaces",
                                                      AuthorizedPrincipal.class));
            ub.resolveTemplate("uuid", definingProduct);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to retrieve getWorkspaces method",
                                            e);
        }
        return ub.build();
    }

    @Context
    private UriInfo uriInfo;

    public WorkspaceResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Timed
    @Path("{workspace}/key")
    @GET
    public Map<String, Object> getKeys(@Auth(required = false) AuthorizedPrincipal principal,
                                       @PathParam("workspace") UUID workspace) {
        return readOnly(principal, readOnlyModel -> {
            WorkspaceScope scope;
            try {
                scope = readOnlyModel.getWorkspaceModel()
                                     .getScoped(workspace);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(String.format("Workspace not found: %s",
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            Map<String, String> keys = new TreeMap<>();
            for (String key : scope.getWorkspace()
                                   .getKeys()) {
                keys.put(key,
                         lookupIri(workspace, key, uriInfo).toASCIIString());
            }
            Map<String, Object> context = new TreeMap<>();
            Map<String, Object> keyTerm = new TreeMap<>();
            keyTerm.put(Constants.ID, keysIri(workspace, uriInfo));
            keyTerm.put(Constants.TYPE, Constants.ID);
            keyTerm.put(Constants.CONTAINER, Constants.INDEX);
            context.put("keys", keyTerm);
            Map<String, Object> returned = new TreeMap<>();
            returned.put(Constants.CONTEXT, context);
            returned.put(KEYS, keys);
            return returned;
        });
    }

    @Timed
    @Path("{workspace}")
    @GET
    public WorkspaceSnapshot getWorkspace(@Auth(required = false) AuthorizedPrincipal principal,
                                          @PathParam("workspace") UUID wsp) {
        return readOnly(principal, readOnlyModel -> {
            EntityManager em = readOnlyModel.getEntityManager();
            Product workspace = em.find(Product.class, wsp);
            if (workspace == null) {
                throw new WebApplicationException(String.format("Workspace not found: %s",
                                                                wsp),
                                                  Status.NOT_FOUND);
            }
            return new WorkspaceSnapshot(workspace, em);
        });
    }

    @Timed
    @GET
    @Path("/")
    public Map<String, Object> getWorkspaces(@Auth(required = false) AuthorizedPrincipal principal) {
        return readOnly(principal, readOnlyModel -> {
            Kernel kernel = readOnlyModel.getKernel();
            Aspect<Product> aspect = new Aspect<>(Ruleform.initializeAndUnproxy(kernel.getIsA()),
                                                  Ruleform.initializeAndUnproxy(kernel.getWorkspace()));
            Map<String, Object> returned = new TreeMap<>();
            Map<String, Object> context = new TreeMap<>();
            context.put(KEYS, Constants.ID);
            context.put(DEFINING_PRODUCT, Constants.ID);
            returned.put(Constants.CONTEXT, context);
            List<Map<String, Object>> facets = new ArrayList<>();
            NetworkedModel<Product, ?, ?, ?> networkedModel = readOnlyModel.getProductModel();
            for (Product definingProduct : networkedModel.getChildren(aspect.getClassification(),
                                                                      aspect.getClassifier()
                                                                            .getInverse())) {
                Map<String, Object> ctx = new TreeMap<>();
                ctx.put(Constants.ID,
                        Facet.getInstanceIri(aspect,
                                             Ruleform.initializeAndUnproxy(definingProduct),
                                             uriInfo));
                ctx.put(Constants.TYPE, Facet.getFullFacetIri(aspect, uriInfo));
                Map<String, Object> wsp = new TreeMap<>();
                wsp.put(Constants.ID,
                        workspaceIri(definingProduct.getId(), uriInfo));
                wsp.put(KEYS, keysIri(definingProduct.getId(), uriInfo));
                wsp.put(DEFINING_PRODUCT, ctx);
                facets.add(wsp);
            }
            returned.put(Constants.GRAPH, facets);
            return returned;
        });
    }

    @Timed
    @Path("{workspace}/key/{member}")
    @GET
    public Map<String, Object> lookup(@Auth(required = false) AuthorizedPrincipal principal,
                                      @PathParam("workspace") UUID workspace,
                                      @PathParam("member") String member) {
        return readOnly(principal, readOnlyModel -> {
            WorkspaceScope scope;
            try {
                scope = readOnlyModel.getWorkspaceModel()
                                     .getScoped(workspace);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(String.format("Workspace not found: %s",
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            Ruleform resolved = scope.lookup(member);
            if (resolved == null) {
                throw new WebApplicationException(String.format("%s not found in workspace: %s",
                                                                member,
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            return new RuleformContext(resolved.getClass(),
                                       uriInfo).toNode(resolved, uriInfo);
        });
    }

    @Timed
    @Path("{workspace}/key/{namespace}/{member}")
    @GET
    public Map<String, Object> lookup(@Auth(required = false) AuthorizedPrincipal principal,
                                      @PathParam("workspace") UUID workspace,
                                      @PathParam("namespace") String namespace,
                                      @PathParam("member") String member) {
        return readOnly(principal, readOnlyModel -> {
            WorkspaceScope scope;
            try {
                scope = readOnlyModel.getWorkspaceModel()
                                     .getScoped(workspace);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(String.format("Workspace not found: %s",
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            Ruleform resolved = scope.lookup(namespace, member);
            if (resolved == null) {
                throw new WebApplicationException(String.format("%s:%s not found in workspace: %s",
                                                                namespace,
                                                                member,
                                                                workspace),
                                                  Status.NOT_FOUND);
            }
            return new RuleformContext(resolved.getClass(),
                                       uriInfo).toNode(resolved, uriInfo);
        });
    }

    @Timed
    @Path("translate/{uri}")
    @GET
    public UUID translate(@PathParam("uri") String uri) {
        return WorkspaceAccessor.uuidOf(uri);
    }
}
