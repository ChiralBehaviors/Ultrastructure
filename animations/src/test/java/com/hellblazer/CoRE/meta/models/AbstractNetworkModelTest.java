/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.NetworkInference;
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
        Aspect<Agency> myAspect = new Aspect<Agency>(classification, classifier);
        @SuppressWarnings("unchecked")
        Agency testAgency = model.getAgencyModel().create("test agency in group",
                                                          "test", myAspect);
        em.persist(testAgency);
        em.getTransaction().commit();
        List<Agency> inGroup = model.getAgencyModel().getInGroup(classifier,
                                                                 inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testAgency, inGroup.get(0));
    }

    // @Test  
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
        Agency classifier = new Agency("test not in group agency classifier",
                                       kernel.getCore());
        em.persist(classifier);
        em.getTransaction().commit();
        List<Agency> notInGroup = model.getAgencyModel().getNotInGroup(classifier,
                                                                       inverse);
        assertNotNull(notInGroup);
        assertEquals(1, notInGroup.size());
    }

    @Test
    public void testGetTransitiveRelationships() {
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        Agency a = new Agency("A", "A", core);
        em.persist(a);
        Agency b = new Agency("B", "B", core);
        em.persist(b);
        Agency c = new Agency("C", "C", core);
        em.persist(c);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(2,
                     model.getAgencyModel().getTransitiveRelationships(a).size());
    }

    @Test
    public void testGetImmediateRelationships() {
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        Agency a = new Agency("A", "A", core);
        em.persist(a);
        Agency b = new Agency("B", "B", core);
        em.persist(b);
        Agency c = new Agency("C", "C", core);
        em.persist(c);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(1,
                     model.getAgencyModel().getImmediateRelationships(a).size());
    }
}
