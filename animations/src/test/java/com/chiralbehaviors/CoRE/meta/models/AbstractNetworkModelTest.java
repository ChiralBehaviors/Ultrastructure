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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AbstractNetworkModelTest extends AbstractModelTest {

    @Test
    public void testCascadedDeleteInference() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel()
                           .getCore();

        em.getTransaction()
          .begin();

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

        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
        aEqualsA = em.find(NetworkInference.class, aEqualsA.getId());
        em.remove(aEqualsA);
        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
    }

    @Test
    public void testCascadedDeletePremise() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));

        em.getTransaction()
          .begin();
        Agency core = model.getKernel()
                           .getCore();

        Relationship equals = new Relationship("= again", "an alias for equals",
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

        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
        edgeA = em.find(AgencyNetwork.class, edgeA.getId());
        em.remove(edgeA);
        em.flush();
        a = em.find(Agency.class, a.getId());
        List<Agency> children = model.getAgencyModel()
                                     .getChildren(a, equals);
        assertEquals(children.toString(), 1, children.size());
        assertEquals(0, model.getAgencyModel()
                             .getChildren(a, equals2)
                             .size());
        b = em.find(Agency.class, b.getId());
        assertEquals(1, model.getAgencyModel()
                             .getChildren(b, equals2)
                             .size());
    }

    @Test
    public void testCascadedDeletePremise2() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel()
                           .getCore();

        em.getTransaction()
          .begin();
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

        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
        edgeB = em.find(AgencyNetwork.class, edgeB.getId());
        em.remove(edgeB);
        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
    }

    @Test
    public void testDeduction() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel()
                           .getCore();

        em.getTransaction()
          .begin();

        Relationship a = model.getRelationshipModel()
                              .create("a", "a", "a'", "a'");
        Relationship b = model.getRelationshipModel()
                              .create("b", "b", "b'", "b'");
        Relationship c = model.getRelationshipModel()
                              .create("c", "c", "c'", "c'");
        Relationship d = model.getRelationshipModel()
                              .create("d", "d", "d'", "d'");
        Relationship e = model.getRelationshipModel()
                              .create("e", "e", "e'", "e'");
        Relationship f = model.getRelationshipModel()
                              .create("f", "f", "f'", "f'");
        Relationship g = model.getRelationshipModel()
                              .create("g", "g", "g'", "g'");

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

        em.flush();
        A = em.find(Agency.class, A.getId());
        List<Agency> children = model.getAgencyModel()
                                     .getChildren(A, a);
        assertEquals(String.format("%s", children), 7, children.size());
    }

    @Test
    public void testGetImmediateRelationships() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));

        em.getTransaction()
          .begin();
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);

        Relationship equals = new Relationship("equals on another level",
                                               "an alias for equals", core);
        equals.setInverse(equals);
        em.persist(equals);
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

        em.flush();
        em.refresh(a);
        assertEquals(1, model.getAgencyModel()
                             .getImmediateRelationships(a)
                             .size());
    }

    @Test
    public void testGetTransitiveRelationships() throws SQLException {
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals = new Relationship("so equals so",
                                               "an alias for equals", core);
        equals.setInverse(equals);
        em.persist(equals);

        em.getTransaction()
          .begin();

        Relationship equals2 = new Relationship("so equals",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        em.flush();

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
        em.flush();
        em.refresh(a);
        em.refresh(b);
        em.refresh(c);
        assertEquals(2, model.getAgencyModel()
                             .getTransitiveRelationships(a)
                             .size());
    }

    @Test
    public void testInGroup() {
        em.getTransaction()
          .begin();
        Relationship classifier = new Relationship("test group classifier",
                                                   kernel.getCore());
        em.persist(classifier);
        Relationship inverse = new Relationship("inverse test group classifier",
                                                kernel.getCore());
        em.persist(inverse);
        classifier.setInverse(inverse);
        inverse.setInverse(classifier);
        Agency classification = new Agency("test in group agency classification",
                                           kernel.getCore());
        em.persist(classification);
        Aspect<Agency> myAspect = new Aspect<Agency>(classifier,
                                                     classification);
        @SuppressWarnings("unchecked")
        Agency testAgency = model.getAgencyModel()
                                 .create("test agency in group", "test",
                                         myAspect, kernel.getCore());
        em.persist(testAgency);
        em.flush();
        List<Agency> inGroup = model.getAgencyModel()
                                    .getInGroup(classification, inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testAgency, inGroup.get(0));
    }

    @Test
    public void testIterativeInference() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel()
                           .getCore();

        em.getTransaction()
          .begin();
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

        em.flush();
        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getAgencyModel()
                             .getChildren(a, equals)
                             .size());
    }

    // @Test
    public void testNotInGroup() {
        em.getTransaction()
          .begin();
        Relationship classifier = new Relationship("test not in group classifier",
                                                   kernel.getCore());
        em.persist(classifier);
        Relationship inverse = new Relationship("inverse test not in group classifier",
                                                kernel.getCore());
        em.persist(inverse);
        classifier.setInverse(inverse);
        inverse.setInverse(classifier);
        Agency classification = new Agency("test not in group agency classification",
                                           kernel.getCore());
        em.persist(classification);
        em.flush();
        List<Agency> notInGroup = model.getAgencyModel()
                                       .getNotInGroup(classification, inverse);
        assertNotNull(notInGroup);
        assertEquals(1, notInGroup.size());
    }
}
