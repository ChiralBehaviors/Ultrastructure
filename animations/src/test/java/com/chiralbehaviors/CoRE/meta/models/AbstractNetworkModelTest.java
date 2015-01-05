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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AbstractNetworkModelTest extends AbstractModelTest {

    @Test
    public void testCascadedDeleteInference() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel().getCore();

        em.getTransaction().begin();

        Relationship equals = new Relationship("= a", "an alias for equals",
                                               core);
        equals.setInverse(equals);
        em.persist(equals);

        Relationship equals2 = new Relationship("equals, also",
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
        Agency d = new Agency("D", "D", core);
        em.persist(d);
        Agency e = new Agency("E", "E", core);
        em.persist(e);
        Agency f = new Agency("F", "F", core);
        em.persist(f);
        Agency g = new Agency("G", "G", core);
        em.persist(g);
        Agency h = new Agency("H", "H", core);
        em.persist(h);
        Agency i = new Agency("I", "I", core);
        em.persist(i);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);
        AgencyNetwork edgeC = new AgencyNetwork(c, equals2, d, core);
        em.persist(edgeC);
        AgencyNetwork edgeD = new AgencyNetwork(d, equals2, e, core);
        em.persist(edgeD);
        AgencyNetwork edgeE = new AgencyNetwork(e, equals2, f, core);
        em.persist(edgeE);
        AgencyNetwork edgeF = new AgencyNetwork(f, equals2, g, core);
        em.persist(edgeF);
        AgencyNetwork edgeG = new AgencyNetwork(g, equals2, h, core);
        em.persist(edgeG);
        AgencyNetwork edgeH = new AgencyNetwork(h, equals2, i, core);
        em.persist(edgeH);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel().getChildren(a, equals).size());
        em.getTransaction().begin();
        aEqualsA = em.find(NetworkInference.class, aEqualsA.getId());
        em.remove(aEqualsA);
        em.getTransaction().commit();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getAgencyModel().getChildren(a, equals).size());
    }

    @Test
    public void testCascadedDeletePremise() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel().getCore();

        em.getTransaction().begin();

        Relationship equals = new Relationship("= b", "an alias for equals",
                                               core);
        equals.setInverse(equals);
        em.persist(equals);

        Relationship equals2 = new Relationship("also, too, equals",
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
        Agency d = new Agency("D", "D", core);
        em.persist(d);
        Agency e = new Agency("E", "E", core);
        em.persist(e);
        Agency f = new Agency("F", "F", core);
        em.persist(f);
        Agency g = new Agency("G", "G", core);
        em.persist(g);
        Agency h = new Agency("H", "H", core);
        em.persist(h);
        Agency i = new Agency("I", "I", core);
        em.persist(i);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);
        AgencyNetwork edgeC = new AgencyNetwork(c, equals2, d, core);
        em.persist(edgeC);
        AgencyNetwork edgeD = new AgencyNetwork(d, equals2, e, core);
        em.persist(edgeD);
        AgencyNetwork edgeE = new AgencyNetwork(e, equals2, f, core);
        em.persist(edgeE);
        AgencyNetwork edgeF = new AgencyNetwork(f, equals2, g, core);
        em.persist(edgeF);
        AgencyNetwork edgeG = new AgencyNetwork(g, equals2, h, core);
        em.persist(edgeG);
        AgencyNetwork edgeH = new AgencyNetwork(h, equals2, i, core);
        em.persist(edgeH);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel().getChildren(a, equals).size());
        em.getTransaction().begin();
        edgeA = em.find(AgencyNetwork.class, edgeA.getId());
        em.remove(edgeA);
        em.getTransaction().commit();
        a = em.find(Agency.class, a.getId());
        assertEquals(0, model.getAgencyModel().getChildren(a, equals).size());
        b = em.find(Agency.class, b.getId());
        assertEquals(1, model.getAgencyModel().getChildren(b, equals2).size());
    }

    @Test
    public void testCascadedDeletePremise2() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel().getCore();

        em.getTransaction().begin();
        Relationship equals = new Relationship("= d", "an alias for equals",
                                               core);
        equals.setInverse(equals);
        em.persist(equals);

        Relationship equals2 = new Relationship("also equals",
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
        Agency d = new Agency("D", "D", core);
        em.persist(d);
        Agency e = new Agency("E", "E", core);
        em.persist(e);
        Agency f = new Agency("F", "F", core);
        em.persist(f);
        Agency g = new Agency("G", "G", core);
        em.persist(g);
        Agency h = new Agency("H", "H", core);
        em.persist(h);
        Agency i = new Agency("I", "I", core);
        em.persist(i);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);
        AgencyNetwork edgeC = new AgencyNetwork(c, equals2, d, core);
        em.persist(edgeC);
        AgencyNetwork edgeD = new AgencyNetwork(d, equals2, e, core);
        em.persist(edgeD);
        AgencyNetwork edgeE = new AgencyNetwork(e, equals2, f, core);
        em.persist(edgeE);
        AgencyNetwork edgeF = new AgencyNetwork(f, equals2, g, core);
        em.persist(edgeF);
        AgencyNetwork edgeG = new AgencyNetwork(g, equals2, h, core);
        em.persist(edgeG);
        AgencyNetwork edgeH = new AgencyNetwork(h, equals2, i, core);
        em.persist(edgeH);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel().getChildren(a, equals).size());
        em.getTransaction().begin();
        edgeB = em.find(AgencyNetwork.class, edgeB.getId());
        em.remove(edgeB);
        em.getTransaction().commit();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getAgencyModel().getChildren(a, equals).size());
    }

    @Test
    public void testDeduction() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel().getCore();

        em.getTransaction().begin();

        Relationship a = model.getRelationshipModel().create("a", "a", "a'",
                                                             "a'");
        Relationship b = model.getRelationshipModel().create("b", "b", "b'",
                                                             "b'");
        Relationship c = model.getRelationshipModel().create("c", "c", "c'",
                                                             "c'");
        Relationship d = model.getRelationshipModel().create("d", "d", "d'",
                                                             "d'");
        Relationship e = model.getRelationshipModel().create("e", "e", "e'",
                                                             "e'");
        Relationship f = model.getRelationshipModel().create("f", "f", "f'",
                                                             "f'");
        Relationship g = model.getRelationshipModel().create("g", "g", "g'",
                                                             "g'");

        NetworkInference aIsB = new NetworkInference(a, b, a, core);
        em.persist(aIsB);
        NetworkInference aIsC = new NetworkInference(a, c, a, core);
        em.persist(aIsC);
        NetworkInference aIsD = new NetworkInference(a, d, a, core);
        em.persist(aIsD);
        NetworkInference aIsE = new NetworkInference(a, e, a, core);
        em.persist(aIsE);
        NetworkInference aIsF = new NetworkInference(a, f, a, core);
        em.persist(aIsF);
        NetworkInference aIsG = new NetworkInference(a, g, a, core);
        em.persist(aIsG);
        Agency A = new Agency("A", "A", core);
        em.persist(A);
        Agency B = new Agency("B", "B", core);
        em.persist(B);
        Agency C = new Agency("C", "C", core);
        em.persist(C);
        Agency D = new Agency("D", "D", core);
        em.persist(D);
        Agency E = new Agency("E", "E", core);
        em.persist(E);
        Agency F = new Agency("F", "F", core);
        em.persist(F);
        Agency G = new Agency("G", "G", core);
        em.persist(G);
        Agency H = new Agency("H", "H", core);
        em.persist(H);
        AgencyNetwork edgeA = new AgencyNetwork(A, a, B, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(B, b, C, core);
        em.persist(edgeB);
        AgencyNetwork edgeC = new AgencyNetwork(C, c, D, core);
        em.persist(edgeC);
        AgencyNetwork edgeD = new AgencyNetwork(D, d, E, core);
        em.persist(edgeD);
        AgencyNetwork edgeE = new AgencyNetwork(E, e, F, core);
        em.persist(edgeE);
        AgencyNetwork edgeF = new AgencyNetwork(F, f, G, core);
        em.persist(edgeF);
        AgencyNetwork edgeG = new AgencyNetwork(G, g, H, core);
        em.persist(edgeG);

        em.getTransaction().commit();
        em.clear();
        A = em.find(Agency.class, A.getId());
        List<Agency> children = model.getAgencyModel().getChildren(A, a);
        assertEquals(String.format("%s", children), 7, children.size());
    }

    @Test
    public void testGetImmediateRelationships() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
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

    @Test
    public void testGetTransitiveRelationships() throws SQLException {
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("so equals",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        em.getTransaction().commit();

        em.getTransaction().begin();
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
        //        em.getTransaction().begin();
        //        model.getAgencyModel().propagate(edgeB);
        //        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(2,
                     model.getAgencyModel().getTransitiveRelationships(a).size());
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
        Agency classifier = new Agency("test in group agency classifier",
                                       kernel.getCore());
        em.persist(classifier);
        Aspect<Agency> myAspect = new Aspect<Agency>(classification, classifier);
        Agency testAgency = model.getAgencyModel().create("test agency in group",
                                                          "test", myAspect).asRuleform();
        em.persist(testAgency);
        em.getTransaction().commit();
        List<Agency> inGroup = model.getAgencyModel().getInGroup(classifier,
                                                                 inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testAgency, inGroup.get(0));
    }

    @Test
    public void testIterativeInference() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel().getCore();

        em.getTransaction().begin();
        Relationship equals = new Relationship("= c", "an alias for equals",
                                               core);
        equals.setInverse(equals);
        em.persist(equals);

        Relationship equals2 = new Relationship("equals too",
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
        Agency d = new Agency("D", "D", core);
        em.persist(d);
        Agency e = new Agency("E", "E", core);
        em.persist(e);
        Agency f = new Agency("F", "F", core);
        em.persist(f);
        Agency g = new Agency("G", "G", core);
        em.persist(g);
        Agency h = new Agency("H", "H", core);
        em.persist(h);
        Agency i = new Agency("I", "I", core);
        em.persist(i);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);
        AgencyNetwork edgeC = new AgencyNetwork(c, equals2, d, core);
        em.persist(edgeC);
        AgencyNetwork edgeD = new AgencyNetwork(d, equals2, e, core);
        em.persist(edgeD);
        AgencyNetwork edgeE = new AgencyNetwork(e, equals2, f, core);
        em.persist(edgeE);
        AgencyNetwork edgeF = new AgencyNetwork(f, equals2, g, core);
        em.persist(edgeF);
        AgencyNetwork edgeG = new AgencyNetwork(g, equals2, h, core);
        em.persist(edgeG);
        AgencyNetwork edgeH = new AgencyNetwork(h, equals2, i, core);
        em.persist(edgeH);

        em.getTransaction().commit();
        em.clear();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel().getChildren(a, equals).size());
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
}
