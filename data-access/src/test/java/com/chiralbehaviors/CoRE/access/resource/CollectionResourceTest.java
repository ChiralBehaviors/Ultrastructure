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
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
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

    @Before
    public void before() {
        resource = new CollectionResource(emf);
        core = new Agency("CoRE");
        core.setUpdatedBy(core);
        core = em.merge(core);
        beginTransaction();
    }

    @After
    public void after() {
        em.getTransaction().rollback();
    }

    @Test
    public void testCollectionResource() {
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

        em.flush();
    }

    @Test
    public void testInsertAndUpdate() throws JsonProcessingException {
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        core = (Agency) resource.post(core);
        UUID id = core.getUUID();
        core.setName("new name");
        Relationship owns = new Relationship("owns1", null, core);
        Relationship ownedBy = new Relationship("ownedBy1Å", null, core);
        owns.setInverse(ownedBy);
        ownedBy.setInverse(owns);

        Relationship graph = (Relationship) resource.post(owns);
        assertNotNull(graph.getId());
        assertNotNull(graph.getInverse().getId());
        assertEquals(core.getName(), graph.getUpdatedBy().getName());
        assertEquals(id, graph.getUpdatedBy().getUUID());
    }

    /**
     * Tests inserting a cycle
     * 
     * @throws JsonProcessingException
     */
    @Test
    public void testInsertRelationshipAndInverse()
                                                  throws JsonProcessingException {
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
        Agency core = new Agency("insertSimpleGraph", "test resource");
        core.setUpdatedBy(core);

        Product prod = new Product("myProd", null, core);
        Product res = (Product) resource.post(prod);
        assertNotNull(res.getId());
        assertEquals("myProd", res.getName());
    }

    @Test
    public void testInsertSingleRuleform() throws JsonProcessingException {
        Agency core = new Agency("hparry", "test resource");
        core.setUpdatedBy(core);

        core = (Agency) resource.post(core);
        assertNotNull(core.getId());

    }
}
