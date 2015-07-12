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

import java.util.List;
import java.util.Map;
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
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/workspace")
@Produces({ "application/json", "text/json" })
public class WorkspaceResource extends TransactionalResource {

    @Context
    private UriInfo uriInfo;

    public WorkspaceResource(EntityManagerFactory emf) {
        super(emf);
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
        return FacetResource.getFacetInstances(aspect, readOnlyModel, uriInfo);
    }

    @Path("{uuid}/lookup/{member}")
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
}
