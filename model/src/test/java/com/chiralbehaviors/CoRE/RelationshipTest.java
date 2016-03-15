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
package com.chiralbehaviors.CoRE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */

public class RelationshipTest extends DatabaseTest {

    @Before
    public void initData() {
        Agency core = new Agency("CoREd");
        core.setUpdatedBy(core);
        create.persist(core);

        Relationship massList = new Relationship("mass-list",
                                                 "A is a member of the mass list B",
                                                 core);
        create.persist(massList);

        Relationship massListOf = new Relationship("mass-list-of",
                                                   "A is a mass list that has B as a member",
                                                   core, massList);
        create.persist(massListOf);
        create.flush();
        create.clear();
    }

    @Test
    public void setInverseTest() {
        Relationship r = new Relationship();
        r.setName("Foo");

        Relationship i = new Relationship();
        i.setName("Bar");

        r.setInverse(i);

        assertNotNull(r.getInverse());
        assertEquals(r.getInverse(), i);

        assertNotNull(i.getInverse());
        assertEquals(i.getInverse(), r);
    }

    @Test
    public void testInverseMerge() {
        TypedQuery<Agency> query = create.createNamedQuery("agency.findByName",
                                                       Agency.class);
        query.setParameter("name", "CoREd");
        Agency core = query.getSingleResult();

        Relationship relationship = new Relationship();
        relationship.setName("Foo");
        relationship.setUpdatedBy(core);
        create.persist(relationship);

        Relationship inverse = new Relationship();
        inverse.setName("Bar");
        inverse.setUpdatedBy(core);
        create.persist(inverse);

        relationship.setInverse(inverse);

        assertNotNull(relationship.getInverse());
        assertEquals(inverse, relationship.getInverse());

        assertNotNull(inverse.getInverse());
        assertEquals(relationship, inverse.getInverse());

        System.out.println("R: " + relationship);
        System.out.println("I: " + relationship.getInverse());

        Relationship r1 = create.merge(relationship);

        System.out.println("R1: " + r1);
        System.out.println("I1: " + r1.getInverse());

        System.out.println("R1 Class: " + r1.getClass());
        System.out.println("I1 Class: " + r1.getInverse()
                                            .getClass());

        assertNotNull(r1.getInverse());
    }
}
