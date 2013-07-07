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

import org.junit.Assert;
import org.junit.Test;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceRelationshipProductAuthorization;
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
        Assert.assertTrue(core != null);
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
    
    public void testMerge() {
    	
    	em.getTransaction().begin();
    	//todo this will break if tests execute out of order
    	Resource user = new Resource("User", null, core);
    	em.persist(user);
    	
    	Relationship owns = new Relationship("owns", null, core);
    	em.persist(owns);
    	
    	Relationship ownedBy = new Relationship("ownedBy", null, core);
    	em.persist(ownedBy);
    	owns.setInverse(ownedBy);
    	ownedBy.setInverse(owns);
    	em.getTransaction().commit();
    	
    	Product channel1 = new Product("Channel One", null, user);
    	Product channel2 = new Product("Channel Two", null, user);
    	Product clip1 = new Product("Clip 1", null, user);
    	Product clip2 = new Product("Clip 2", null, user);
    	Product clip3 = new Product("Clip 3", null, user);
    	
    	ExistentialRuleform[] rules = {channel1, channel2, clip1, clip2, clip3};
    	
    	em.getTransaction().begin();
    	for (Ruleform r : rules) {
    		em.persist(r);
    	}
    	
    	//create network edges
    	
    	ResourceRelationshipProductAuthorization userCh1 = new ResourceRelationshipProductAuthorization(user, owns, channel1, user);
    	em.persist(userCh1);
    	ResourceRelationshipProductAuthorization userCh2 = new ResourceRelationshipProductAuthorization(user, owns, channel2, user);
    	em.persist(userCh2);
    	
    	
    }

}
