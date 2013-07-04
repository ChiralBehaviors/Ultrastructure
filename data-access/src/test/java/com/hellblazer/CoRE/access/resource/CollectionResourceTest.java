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

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
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
        em.persist(core);
        em.getTransaction().commit();
        Assert.assertTrue(core != null);
        Resource user = new Resource("User", null, core);
        Product channel = new Product("MyChannel", null, core);
        Relationship owns = new Relationship("owns", null, core);
        Relationship ownedBy = new Relationship("ownedBy", null, core);
        /*
        owns.setInverse(ownedBy);
        ownedBy.setInverse(owns);
        */

        Ruleform[] rules = new Ruleform[4];
        rules[0] = user;
        rules[1] = channel;
        rules[2] = owns;
        rules[3] = ownedBy;

        em.getTransaction().begin();

        for (Ruleform r : rules) {
            em.merge(r);
        }
        em.getTransaction().commit();

    }

}
