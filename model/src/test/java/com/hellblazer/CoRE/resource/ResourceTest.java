/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.resource;

import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */

public class ResourceTest extends DatabaseTest {

    @Test
    public void testEquals() {
        beginTransaction();

        TypedQuery<Resource> query = em.createNamedQuery("resource.findByName",
                                                         Resource.class);

        query.setParameter("name", "CoRE");
        Resource test = query.getSingleResult();

        query.setParameter("name", "Foo");
        Resource foo = query.getSingleResult();

        System.out.println("Test Class: " + test.getClass());
        System.out.println("Foo Updated By Class (Proxy): "
                           + foo.getUpdatedBy().getClass());

        //        Assert.assertTrue(test.hashCode() == foo.getUpdatedBy().hashCode(), "Hashcodes aren't equal!");
        //        Assert.assertTrue(test.getName().equals(foo.getUpdatedBy().getName()), "Names aren't equal!");
        //        Assert.assertTrue(!test.getClass().equals(foo.getUpdatedBy().getClass()), "Classes are equal!  One should be a proxy");
        //        
        //        Assert.assertFalse(test == foo.getUpdatedBy(), "The objects shouldn't be identical!");
        //        
        //        Assert.assertTrue((Object)test.getClass() != (Object)foo.getUpdatedBy().getClass());
        //        Assert.assertTrue((Object)foo.getUpdatedBy().getClass() != (Object)test.getClass());
        //        System.out.println("Foo Updated By Class (Proxy): " + foo.getUpdatedBy().getClass());
        //        
        //        Assert.assertEquals(test, foo.getUpdatedBy(), "What the hell?");
        Assert.assertEquals(test, foo.getUpdatedBy());

        Assert.assertTrue(test.equals(foo.getUpdatedBy()));
        Assert.assertTrue(foo.getUpdatedBy().equals(test));

        commitTransaction();
    }

    @Test
    public void testResourceAuthorizationConstraints() {
        em.getTransaction().begin();
        TypedQuery<Resource> query = em.createNamedQuery("resource.findByName",
                                                         Resource.class);

        query.setParameter("name", "CoRE");
        Resource test = query.getSingleResult();

        Relationship r = new Relationship("r", test);
        em.persist(r);
        Relationship inverseR = new Relationship("inverse-r", null, test, r);
        r.setInverse(inverseR);
        em.persist(inverseR);

        Product p = new Product("p", test);
        em.persist(p);

        ResourceRelationshipProductAuthorization auth = new ResourceRelationshipProductAuthorization();
        auth.setProduct(p);
        auth.setRelationship(r);
        auth.setResource(test);
        auth.setUpdatedBy(test);

        em.persist(auth);
        em.getTransaction().commit();

    }

    @Before
    public void initData() {
        beginTransaction();
        Resource core = new Resource("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Resource foo = new Resource("Foo", "More Foo", core);
        em.persist(foo);
        commitTransaction();
        em.clear();
    }
}
