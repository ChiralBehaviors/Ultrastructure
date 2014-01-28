/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
package com.hellblazer.CoRE.access.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.object.Workspace;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/workspace")
public class WorkspaceResource {

    EntityManager em;

    /**
     * @param emf
     */
    public WorkspaceResource(EntityManagerFactory emf) {
        em = emf.createEntityManager();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Workspace get(@PathParam("id") long productId,
                         @QueryParam("relId") long relId) {
        Product p = em.find(Product.class, productId);
        Relationship r = em.find(Relationship.class, relId);
        Workspace w = Workspace.loadWorkspace(p, r, em);

        return w;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Workspace insertWorkspace(Workspace w) {
        em.getTransaction().begin();
        try {
            Product origin = w.getProducts().get(0);
            List<Product> nets = w.getProducts();
            Map<Ruleform, Ruleform> knownObjects = new HashMap<Ruleform, Ruleform>();
            for (Product p : nets) {
                p.manageEntity(em, knownObjects);
            }
            List<AccessAuthorization<?, ?>> auths = w.getAccessAuths();
            for (AccessAuthorization<?, ?> auth : auths) {
                auth.manageEntity(em, knownObjects);
            }

            em.getTransaction().commit();
            em.refresh(origin);
            Workspace ws = Workspace.loadWorkspace(origin, w.getWorkspaceOf(),
                                                   em);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping();
            return ws;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

    }

    @POST
    @Path("/{id}/{relId}/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Workspace addRuleformToWorkspace(ExistentialRuleform<?, ?> ef,
                                            @PathParam("id") long id,
                                            @PathParam("relId") long relId) {
        Product p = new Product();
        p.setId(id);

        Relationship r = new Relationship();
        r.setId(relId);

        Workspace w = Workspace.loadWorkspace(p, r, em);
        em.getTransaction().begin();
        try {

            w.addToWorkspace(ef);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
        w = Workspace.loadWorkspace(w.getParentProduct(), w.getWorkspaceOf(),
                                    em);
        return w;
    }

    @GET
    @Path("/{id}/{relId}/Product")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ruleform> getAllRuleformsForType(@PathParam("id") long id,
                                                 @PathParam("relId") long relId) {

        return null;
    }

}