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

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
public class AbstractNetworkModelTest extends AbstractModelTest {

    @Test
    public void testInGroup() {
        em.getTransaction().begin();
        Relationship classification = new Relationship(
                                                       "test group classification",
                                                       kernel.getCore());
        em.persist(classification);
        Relationship inverse = new Relationship(
                                                "inverse test group classification",
                                                kernel.getCore());
        em.persist(inverse);
        classification.setInverse(inverse);
        inverse.setInverse(classification);
        Agency classifier = new Agency("test in group agency classifier",
                                           kernel.getCore());
        em.persist(classifier);
        Aspect<Agency> myAspect = new Aspect<Agency>(classification,
                                                         classifier);
        @SuppressWarnings("unchecked")
        Agency testAgency = model.getAgencyModel().create("test agency in group",
                                                                "test",
                                                                myAspect);
        em.persist(testAgency);
        em.getTransaction().commit();
        List<Agency> inGroup = model.getAgencyModel().getInGroup(classifier,
                                                                     inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testAgency, inGroup.get(0));
    }

    // @Test TODO not currently working ;)
    public void testNotInGroup() {
        em.getTransaction().begin();
        Relationship classification = new Relationship(
                                                       "test not in group classification",
                                                       kernel.getCore());
        em.persist(classification);
        Relationship inverse = new Relationship(
                                                "inverse test not in group classification",
                                                kernel.getCore());
        em.persist(inverse);
        classification.setInverse(inverse);
        inverse.setInverse(classification);
        Agency classifier = new Agency(
                                           "test not in group agency classifier",
                                           kernel.getCore());
        em.persist(classifier);
        em.getTransaction().commit();
        List<Agency> notInGroup = model.getAgencyModel().getNotInGroup(classifier,
                                                                           inverse);
        assertNotNull(notInGroup);
        assertEquals(1, notInGroup.size());
    }
}
