/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.agency;

import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */

public class AgencyTest extends DatabaseTest {

    @Before
    public void initData() {
        Agency core = new Agency("PseudoCore");
        core.setUpdatedBy(core);
        em.persist(core);

        Agency foo = new Agency("Foo", "More Foo", core);
        em.persist(foo);
        em.flush();
        em.clear();
    }

    @Test
    public void testEquals() {
        TypedQuery<Agency> query = em.createNamedQuery("agency.findByName",
                                                       Agency.class);

        query.setParameter("name", "PseudoCore");
        Agency test = query.getSingleResult();

        query.setParameter("name", "Foo");
        Agency foo = query.getSingleResult();

        System.out.println("Test Class: " + test.getClass());
        System.out.println("Foo Updated By Class (Proxy): "
                           + foo.getUpdatedBy().getClass());
        Assert.assertEquals(test, foo.getUpdatedBy());

        Assert.assertTrue(test.equals(foo.getUpdatedBy()));
        Assert.assertTrue(foo.getUpdatedBy().equals(test));

    }

}
