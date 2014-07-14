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
package com.chiralbehaviors.CoRE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */

public class RelationshipTest extends DatabaseTest {

    @Before
    public void initData() {
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
        em.flush();
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

        System.out.println("R1: " + r1);
        System.out.println("I1: " + r1.getInverse());

        System.out.println("R1 Class: " + r1.getClass());
        System.out.println("I1 Class: " + r1.getInverse().getClass());

        assertNotNull(r1.getInverse());
    }
}
