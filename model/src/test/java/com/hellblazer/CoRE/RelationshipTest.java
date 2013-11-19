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
package com.hellblazer.CoRE;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */

public class RelationshipTest extends DatabaseTest {

    @Before
    public void initData() {
        beginTransaction();
        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Relationship massList = new Relationship(
                                                 "mass-list",
                                                 "A is a member of the mass list B",
                                                 core);
        em.persist(massList);

        Relationship massListOf = new Relationship(
                                                   "mass-list-of",
                                                   "A is a mass list that has B as a member",
                                                   core, massList);
        em.persist(massListOf);

        commitTransaction();
        em.clear();
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
        beginTransaction();

        TypedQuery<Agency> query = em.createNamedQuery("agency.findByName",
                                                         Agency.class);
        query.setParameter("name", "CoRE");
        Agency core = query.getSingleResult();

        Relationship relationship = new Relationship();
        relationship.setName("Foo");
        relationship.setPreferred(true);
        relationship.setUpdatedBy(core);
        em.persist(relationship);

        Relationship inverse = new Relationship();
        inverse.setName("Bar");
        inverse.setPreferred(false);
        inverse.setUpdatedBy(core);
        em.persist(inverse);

        relationship.setInverse(inverse);

        assertNotNull(relationship.getInverse());
        assertEquals(inverse, relationship.getInverse());

        assertNotNull(inverse.getInverse());
        assertEquals(relationship, inverse.getInverse());

        System.out.println("R: " + relationship);
        System.out.println("I: " + relationship.getInverse());

        Relationship r1 = em.merge(relationship);

        //Relationship i1 = dao.makePersistent(inverse);

        // assertFalse(relationship == r1);
        //assertTrue(inverse == i1);

        System.out.println("R1: " + r1);
        System.out.println("I1: " + r1.getInverse());

        System.out.println("R1 Class: " + r1.getClass());
        System.out.println("I1 Class: " + r1.getInverse().getClass());

        assertNotNull(r1.getInverse());

        //assertEquals(r1.getInverse(), i1);

        //assertNotNull(i1.getInverse());
        //assertEquals(i1.getInverse(), r1);

        //dao.flush();  
        commitTransaction();
    }
}
