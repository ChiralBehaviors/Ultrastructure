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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformNode;
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
    public WorkspaceSnapshot getWorkspace(@PathParam("uuid") String workspaceId) {
        Product workspace = readOnlyModel.getEntityManager().find(Product.class,
                                                                  toUuid(workspaceId));
        if (workspace == null) {
            throw new WebApplicationException(String.format("Workspace not found: %s",
                                                            workspaceId),
                                              Status.NOT_FOUND);
        }
        return new WorkspaceSnapshot(workspace,
                                     readOnlyModel.getEntityManager());
    }

    @GET
    public List<UUID> getWorkspaces() {
        return Collections.emptyList();
    }

    @Path("{uuid}/lookup")
    @GET
    public RuleformNode lookup(@PathParam("uuid") String workspaceId,
                               @QueryParam("namespace") String namespace,
                               @QueryParam("member") String member) {
        WorkspaceScope scope = readOnlyModel.getWorkspaceModel().getScoped(toUuid(workspaceId));
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
        return new RuleformNode(resolved, uriInfo);
    }
}
