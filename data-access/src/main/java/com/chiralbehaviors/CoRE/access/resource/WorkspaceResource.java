/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.access.resource;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.object.WorkspaceLoader;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.Workspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/workspace")
public class WorkspaceResource {

    EntityManager em;
    Model         model;

    /**
     * @param emf
     */
    public WorkspaceResource(EntityManagerFactory emf) {
        em = emf.createEntityManager();
        model = new ModelImpl(em);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Workspace get(@PathParam("id") UUID productId,
                         @QueryParam("relId") UUID relId) {
        Product p = new Product();
        p.setId(productId);

        Relationship r = new Relationship();
        r.setId(relId);

        WorkspaceLoader loader = new WorkspaceLoader(p, r, model);
        return loader.getWorkspace();
    }

    @POST
    public void importWorkspaceSnapshot(WorkspaceSnapshot workspace)
                                                                    throws IllegalArgumentException,
                                                                    IllegalAccessException {
        em.getTransaction().begin();
        workspace.merge(em);
        em.getTransaction().commit();
    }

}