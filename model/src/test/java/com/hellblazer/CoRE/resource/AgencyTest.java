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
package com.hellblazer.CoRE.resource;

import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */

public class AgencyTest extends DatabaseTest {

    @Before
    public void initData() {
        beginTransaction();
        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Agency foo = new Agency("Foo", "More Foo", core);
        em.persist(foo);
        commitTransaction();
        em.clear();
    }

    @Test
    public void testEquals() {
        beginTransaction();

        TypedQuery<Agency> query = em.createNamedQuery("agency.findByName",
                                                       Agency.class);

        query.setParameter("name", "CoRE");
        Agency test = query.getSingleResult();

        query.setParameter("name", "Foo");
        Agency foo = query.getSingleResult();

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

}
