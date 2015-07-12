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

package com.chiralbehaviors.CoRE.phantasm.jsonld.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Facet;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/workspace")
@Produces({ "application/json", "text/json" })
public class WorkspaceResource extends TransactionalResource {

    public static String keysIri(UUID definingProduct, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("getKeys", UUID.class));
            ub.resolveTemplate("uuid", definingProduct);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build().toASCIIString();
    }

    public static String lookupIri(UUID definingProduct, String key,
                                   UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("lookup", UUID.class,
                                                      String.class));
            ub.resolveTemplate("uuid", definingProduct);
            ub.resolveTemplate("member", key);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build().toASCIIString();
    }

    public static String workspaceIri(UUID definingProduct, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(WorkspaceResource.class);
        try {
            ub.path(WorkspaceResource.class.getMethod("getWorkspace",
                                                      UUID.class));
            ub.resolveTemplate("uuid", definingProduct);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build().toASCIIString();
    }

    @Context
    private UriInfo uriInfo;

    public WorkspaceResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Path("{uuid}/key")
    @GET
    public Map<String, String> getKeys(@PathParam("uuid") UUID workspaceId) {
        WorkspaceScope scope = readOnlyModel.getWorkspaceModel().getScoped(workspaceId);
        if (scope == null) {
            throw new WebApplicationException(String.format("Workspace not found: %s",
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        Map<String, String> keys = new TreeMap<>();
        for (String key : scope.getWorkspace().getKeys()) {
            keys.put(key, lookupIri(workspaceId, key, uriInfo));
        }
        return keys;
    }

    @Path("{uuid}")
    @GET
    public WorkspaceSnapshot getWorkspace(@PathParam("uuid") UUID workspaceId) {
        EntityManager em = readOnlyModel.getEntityManager();
        Product workspace = em.find(Product.class, workspaceId);
        if (workspace == null) {
            throw new WebApplicationException(String.format("Workspace not found: %s",
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        return new WorkspaceSnapshot(workspace, em);
    }

    @GET
    public List<Map<String, Object>> getWorkspaces() {
        Kernel kernel = readOnlyModel.getKernel();
        Aspect<Product> aspect = new Aspect<>(kernel.getIsA(),
                                              kernel.getWorkspace());
        List<Map<String, Object>> facets = new ArrayList<>();
        NetworkedModel<Product, ?, ?, ?> networkedModel = readOnlyModel.getProductModel();
        for (Product definingProduct : networkedModel.getChildren(aspect.getClassification(),
                                                                  aspect.getClassifier().getInverse())) {
            Map<String, Object> ctx = new TreeMap<>();
            ctx.put(Constants.ID,
                    Facet.getNodeIri(aspect, definingProduct, uriInfo));
            ctx.put(Constants.TYPE, Facet.getTypeIri(aspect, uriInfo));
            Map<String, Object> wsp = new TreeMap<>();
            wsp.put(Constants.ID,
                    workspaceIri(definingProduct.getId(), uriInfo));
            wsp.put("keys", keysIri(definingProduct.getId(), uriInfo));
            wsp.put("definingProduct", ctx);
            facets.add(wsp);
        }
        return facets;
    }

    @Path("{uuid}/key/{member}")
    @GET
    public Map<String, Object> lookup(@PathParam("uuid") UUID workspaceId,
                                      @PathParam("member") String member) {
        WorkspaceScope scope = readOnlyModel.getWorkspaceModel().getScoped(workspaceId);
        if (scope == null) {
            throw new WebApplicationException(String.format("Workspace not found: %s",
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        Ruleform resolved = scope.lookup(member);
        if (resolved == null) {
            throw new WebApplicationException(String.format("%s not found in workspace: %s",
                                                            member,
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        return new RuleformContext(resolved.getClass(),
                                   uriInfo).toNode(resolved, uriInfo);
    }

    @Path("{uuid}/key/{namespace}/{member}")
    @GET
    public Map<String, Object> lookup(@PathParam("uuid") UUID workspaceId,
                                      @PathParam("namespace") String namespace,
                                      @PathParam("member") String member) {
        WorkspaceScope scope = readOnlyModel.getWorkspaceModel().getScoped(workspaceId);
        if (scope == null) {
            throw new WebApplicationException(String.format("Workspace not found: %s",
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        Ruleform resolved = scope.lookup(namespace, member);
        if (resolved == null) {
            throw new WebApplicationException(String.format("%s:%s not found in workspace: %s",
                                                            namespace, member,
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        return new RuleformContext(resolved.getClass(),
                                   uriInfo).toNode(resolved, uriInfo);
    }
}
