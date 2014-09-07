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

import java.util.HashMap;
import java.util.LinkedList;
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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.meta.models.ProductModelImpl;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A REST resource for processing atomic transactions full of multiple objects
 * of many types.
 * 
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/collection")
public class CollectionResource {

    EntityManager em;

    /**
     * @param emf
     */
    public CollectionResource(EntityManagerFactory emf) {
        em = emf.createEntityManager();
    }

    @POST
    @Path("{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product createNewProduct(@PathParam("parentId") String parentId,
                                    @QueryParam("relId") String relId,
                                    Product child)
                                                  throws JsonProcessingException {
        em.getTransaction().begin();
        Product parent = em.find(Product.class, parentId);
        Relationship rel = em.find(Relationship.class, relId);
        em.persist(child);
        ProductNetwork net = new ProductNetwork(parent, rel, child,
                                                parent.getUpdatedBy());
        em.persist(net);
        em.getTransaction().commit();
        em.refresh(child);
        return child;
    }

    // @GET
    // @Path("/{id}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public SerializableGraph get(@PathParam("id") long id, @QueryParam("rel")
    // List<String> relIds) throws JsonProcessingException {
    //
    // Product p = new Product();
    // p.setId(id);
    // List<Ruleform> nodes = new LinkedList<Ruleform>();
    // nodes.add(p);
    // List<Relationship> rels = new LinkedList<Relationship>();
    // for (String rid : relIds) {
    // Relationship r = em.find(Relationship.class, rid);
    // rels.add(r);
    // }
    // GraphQuery ng = getNetwork(nodes, rels);
    // SerializableGraph sg = new SerializableGraph(ng);
    // return sg;
    // }
    //
    // public GraphQuery getNetwork(List<Ruleform> nodes, List<Relationship>
    // relationships) throws JsonProcessingException {
    // GraphQuery pg = new GraphQuery(nodes, relationships, em);
    // return pg;
    //
    // }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> get(@PathParam("id") String id,
                             @QueryParam("relId") List<String> relIds)
                                                                      throws JsonProcessingException {

        Product p = new Product();
        p.setId(id);
        List<Ruleform> nodes = new LinkedList<>();
        nodes.add(p);
        List<Relationship> rels = new LinkedList<>();
        for (String rid : relIds) {
            Relationship r = em.find(Relationship.class, rid);
            rels.add(r);
        }
        ProductModel pm = new ProductModelImpl(em);
        return pm.getChildren(p, rels.get(0));

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Ruleform post(Ruleform graph) throws JsonProcessingException {
        em.getTransaction().begin();
        try {

            Map<Ruleform, Ruleform> knownObjects = new HashMap<>();
            graph.manageEntity(em, knownObjects);

            em.getTransaction().commit();
            em.refresh(graph);
            return graph;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

    }
}
