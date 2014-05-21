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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.access.resource.CollectionResource;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject;
import com.chiralbehaviors.CoRE.meta.BootstrapLoader;
import com.chiralbehaviors.CoRE.meta.models.ModelTest;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author hparry
 * 
 */
public class CollectionResourceTest extends DatabaseTest {

    CollectionResource resource;
    Agency             core;

    @Test
    public void testCollectionResource() {
        resource = new CollectionResource(emf);
        em.getTransaction().begin();

        core = new Agency("CoRE");
        core.setUpdatedBy(core);
        core = em.merge(core);
        assertTrue(core != null);
        Agency user = new Agency("User", null, core);
        user = em.merge(user);
        Product channel = new Product("MyChannel", null, core);
        channel = em.merge(channel);
        Relationship owns = new Relationship("owns", null, core);
        owns = em.merge(owns);
        Relationship ownedBy = new Relationship("ownedBy", null, core);
        ownedBy = em.merge(ownedBy);
        owns.setInverse(ownedBy);
        ownedBy.setInverse(owns);

        Ruleform[] rules = new Ruleform[4];
        rules[0] = user;
        rules[1] = channel;
        rules[2] = owns;
        rules[3] = ownedBy;

        em.getTransaction().commit();

    }

    @Test
    public void testInsertAndUpdate() throws JsonProcessingException {
        resource = new CollectionResource(emf);
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        core = (Agency) resource.post(core);
        UUID id = core.getId();
        core.setName("new name");
        Relationship owns = new Relationship("owns", null, core);
        Relationship ownedBy = new Relationship("ownedBy", null, core);
        owns.setInverse(ownedBy);
        ownedBy.setInverse(owns);

        Relationship graph = (Relationship) resource.post(owns);
        assertNotNull(graph.getId());
        assertNotNull(graph.getInverse().getId());
        assertEquals("new name", graph.getUpdatedBy().getName());
        assertTrue(id.equals(graph.getUpdatedBy().getId()));
    }

    /**
     * Tests inserting a cycle
     * 
     * @throws JsonProcessingException
     */
    @Test
    public void testInsertRelationshipAndInverse()
                                                  throws JsonProcessingException {
        resource = new CollectionResource(emf);
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        Relationship owns = new Relationship("owns", null, core);
        Relationship ownedBy = new Relationship("ownedBy", null, core);
        owns.setInverse(ownedBy);
        ownedBy.setInverse(owns);

        Relationship graph = (Relationship) resource.post(owns);
        assertNotNull(graph.getId());
        assertNotNull(graph.getInverse().getId());
    }

    @Test
    public void testInsertSimpleGraph() throws IOException {
        resource = new CollectionResource(emf);
        Agency core = new Agency("insertSimpleGraph", "test resource");
        core.setUpdatedBy(core);

        Product prod = new Product("myProd", null, core);
        Product res = (Product) resource.post(prod);
        assertNotNull(res.getId());
        assertEquals("myProd", res.getName());
    }

    @Test
    public void testInsertSingleRuleform() throws JsonProcessingException {
        resource = new CollectionResource(emf);
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        core = (Agency) resource.post(core);
        assertNotNull(core.getId());

    }

    @Test
    public void testPost() throws IOException, SQLException {
        InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
        assertNotNull("jpa properties missing", is);
        Properties properties = new Properties();
        properties.load(is);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);
        em = emf.createEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        loader.bootstrap();
        em.getTransaction().commit();
        resource = new CollectionResource(emf);
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        core = (Agency) resource.post(core);
        Product p = new Product("wheee", "wheee");

        Product parent = new Product("Parent", "parent", core);
        parent = (Product) resource.post(parent);
        Relationship workspaceOf = new Relationship("workspaceOf",
                                                    "workspaceOf", core);
        workspaceOf.setInverse(workspaceOf);
        workspaceOf = (Relationship) resource.post(workspaceOf);
        // Agency core = new Agency("hparry", "test resource");
        // core.setUpdatedBy(core);
        p.setUpdatedBy(core);
        p = resource.createNewProduct(parent.getId(), workspaceOf.getId(), p);
        assertNotNull(p.getId());
    }

    // @Test
    // public void testGet() throws JsonProcessingException {
    // resource = new CollectionAgency(emf);
    // Agency core = new Agency("hparry", "test resource");
    // core.setUpdatedBy(core);
    //
    // Product p = new Product("Product", null, core);
    // Product q = new Product("Other Product", null, core);
    // p = (Product) resource.post(p);
    // q = (Product) resource.post(q);
    //
    // Relationship owns = new Relationship("owns", null, core);
    // Relationship ownedBy = new Relationship("ownedBy", null, core);
    // owns.setInverse(ownedBy);
    // ownedBy.setInverse(owns);
    // owns = (Relationship) resource.post(owns);
    //
    //
    // ProductNetwork pn = new ProductNetwork(p, owns, q, core);
    // ProductNetwork pnI = new ProductNetwork(q, ownedBy, p, core);
    //
    // resource.post(pn);
    // resource.post(pnI);
    // LinkedList<Relationship> rels = new LinkedList<Relationship>();
    // rels.add(owns);
    // List<Ruleform> nodes = new LinkedList<Ruleform>();
    // nodes.add(p);
    // GraphQuery ng = resource.getNetwork(nodes, rels);
    //
    // assertEquals(ng.getNodes().get(0).getId(), p.getId());
    // assertEquals(2, ng.getNodes().size());
    // assertNotNull(ng.getRelationships());
    // }

}
