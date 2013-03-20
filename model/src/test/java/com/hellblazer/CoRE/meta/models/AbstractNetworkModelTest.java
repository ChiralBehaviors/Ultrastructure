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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.meta.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class AbstractNetworkModelTest extends AbstractModelTest {

    @Test
    public void testClearDeducedRules() {
        em.getTransaction().begin();
        model.getResourceModel().clearDeducedRules();
        em.getTransaction().commit();
    }

    @Test
    public void testGenerateInverses() {
        em.getTransaction().begin();
        model.getResourceModel().generateInverses();
        em.getTransaction().commit();
    }

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
        Resource classifier = new Resource("test in group resource classifier",
                                           kernel.getCore());
        em.persist(classifier);
        Aspect<Resource> myAspect = new Aspect<Resource>(classification,
                                                         classifier);
        @SuppressWarnings("unchecked")
        Resource testResource = model.getResourceModel().create("test resource in group",
                                                                "test",
                                                                myAspect);
        em.persist(testResource);
        em.getTransaction().commit();
        List<Resource> inGroup = model.getResourceModel().getInGroup(classifier,
                                                                     inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testResource, inGroup.get(0));
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
        Resource classifier = new Resource(
                                           "test not in group resource classifier",
                                           kernel.getCore());
        em.persist(classifier);
        em.getTransaction().commit();
        List<Resource> notInGroup = model.getResourceModel().getNotInGroup(classifier,
                                                                           inverse);
        assertNotNull(notInGroup);
        assertEquals(1, notInGroup.size());
    }
}
