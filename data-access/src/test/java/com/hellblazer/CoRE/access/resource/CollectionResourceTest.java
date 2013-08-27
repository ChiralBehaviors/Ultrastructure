/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.access.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.meta.graph.NetworkGraph;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hparry
 * 
 */
public class CollectionResourceTest extends DatabaseTest {

    CollectionResource resource;
    Resource           core;

    @Test
    public void testCollectionResource() {
        resource = new CollectionResource(emf);
        em.getTransaction().begin();

        Resource core = new Resource("CoRE");
        core.setUpdatedBy(core);
        core = em.merge(core);
        assertTrue(core != null);
        Resource user = new Resource("User", null, core);
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
    public void testInsertSingleRuleform() throws JsonProcessingException {
    	resource = new CollectionResource(emf);
    	Resource core = new Resource("hparry", "test resource");
    	core.setUpdatedBy(core);
    	
    	core = (Resource) resource.post(core);
    	assertNotNull(core.getId());
    	
    }
    
    @Test
    public void testInsertSimpleGraph() throws IOException {
    	resource = new CollectionResource(emf);
    	Resource core = new Resource("insertSimpleGraph", "test resource");
    	core.setUpdatedBy(core);
    	
    	Product prod = new Product("myProd", null, core);
    	Product res = (Product)resource.post(prod);
    	assertNotNull(res.getId());
    	assertEquals("myProd", res.getName());
    }
    
    /**
     * Tests inserting a cycle
     * @throws JsonProcessingException
     */
    @Test
    public void testInsertRelationshipAndInverse() throws JsonProcessingException {
    	resource = new CollectionResource(emf);
    	Resource core = new Resource("hparry", "test resource");
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
    public void testInsertAndUpdate() throws JsonProcessingException {
    	resource = new CollectionResource(emf);
    	Resource core = new Resource("hparry", "test resource");
    	core.setUpdatedBy(core);
    	
    	core = (Resource) resource.post(core);
    	long id = core.getId();
    	core.setName("new name");
    	Relationship owns = new Relationship("owns", null, core);
    	Relationship ownedBy = new Relationship("ownedBy", null, core);
    	owns.setInverse(ownedBy);
    	ownedBy.setInverse(owns);
    	
    	Relationship graph = (Relationship) resource.post(owns);
    	assertNotNull(graph.getId());
    	assertNotNull(graph.getInverse().getId());
    	assertEquals("new name", graph.getUpdatedBy().getName());
    	assertTrue(id == graph.getUpdatedBy().getId());
    }
    
    @Test
    public void testGet() throws JsonProcessingException {
    	resource = new CollectionResource(emf);
    	Resource core = new Resource("hparry", "test resource");
    	core.setUpdatedBy(core);
    	
    	Product p = new Product("Product", null, core);
    	Product q = new Product("Other Product", null, core);
    	p = (Product) resource.post(p);
    	q = (Product) resource.post(q);
    	
    	Relationship owns = new Relationship("owns", null, core);
    	Relationship ownedBy = new Relationship("ownedBy", null, core);
    	owns.setInverse(ownedBy);
    	ownedBy.setInverse(owns);
    	owns = (Relationship) resource.post(owns);
   
    	
    	ProductNetwork pn = new ProductNetwork(p, owns, q, core);
    	ProductNetwork pnI = new ProductNetwork(q, ownedBy, p, core);
    	
    	resource.post(pn);
    	resource.post(pnI);
    	LinkedList<Relationship> rels = new LinkedList<Relationship>();
    	rels.add(owns);
    	
    	NetworkGraph ng = resource.getNetwork(p, rels);
    	
    	assertEquals(ng.getNodes().get(0).getId(), p.getId());
    	assertEquals(2, ng.getNodes().size());
    	assertNotNull(ng.getRelationships());
    }
    
    //TODO HPARRY change this to network graph test
    //add tests to make sure node and relationship lists are unique

}
