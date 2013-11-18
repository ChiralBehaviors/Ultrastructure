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
