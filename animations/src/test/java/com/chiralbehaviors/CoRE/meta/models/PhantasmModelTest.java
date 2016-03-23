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
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelTest extends AbstractModelTest {

    @Test
    public void testCascadedDeleteInference() throws Exception {
        Agency core = model.getKernel()
                           .getCore();

        Relationship equals = model.records()
                                   .newRelationship("= a",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records()
                                    .newRelationship("equals, also",
                                                     "an alias for equals",
                                                     core);
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        Agency d = model.records()
                        .newAgency("D", "D", core);
        d.insert();
        Agency e = model.records()
                        .newAgency("E", "E", core);
        e.insert();
        Agency f = model.records()
                        .newAgency("F", "F", core);
        f.insert();
        Agency g = model.records()
                        .newAgency("G", "G", core);
        g.insert();
        Agency h = model.records()
                        .newAgency("H", "H", core);
        h.insert();
        Agency i = model.records()
                        .newAgency("I", "I", core);
        i.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();
        ExistentialNetworkRecord edgeC = model.records()
                                              .newExistentialNetwork(c, equals2,
                                                                     d, core);
        edgeC.insert();
        ExistentialNetworkRecord edgeD = model.records()
                                              .newExistentialNetwork(d, equals2,
                                                                     e, core);
        edgeD.insert();
        ExistentialNetworkRecord edgeE = model.records()
                                              .newExistentialNetwork(e, equals2,
                                                                     f, core);
        edgeE.insert();
        ExistentialNetworkRecord edgeF = model.records()
                                              .newExistentialNetwork(f, equals2,
                                                                     g, core);
        edgeF.insert();
        ExistentialNetworkRecord edgeG = model.records()
                                              .newExistentialNetwork(g, equals2,
                                                                     h, core);
        edgeG.insert();
        ExistentialNetworkRecord edgeH = model.records()
                                              .newExistentialNetwork(h, equals2,
                                                                     i, core);
        edgeH.insert();

        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
        aEqualsA = em.find(Inference.class, aEqualsA.getId());
        aEqualsA.delete();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
    }

    @Test
    public void testCascadedDeletePremise() throws Exception {
        Agency core = model.getKernel()
                           .getCore();

        Relationship equals = model.records()
                                   .newRelationship("= again",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records()
                                    .newRelationship("also, too, equals",
                                                     "an alias for equals",
                                                     core);
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        Agency d = model.records()
                        .newAgency("D", "D", core);
        d.insert();
        Agency e = model.records()
                        .newAgency("E", "E", core);
        e.insert();
        Agency f = model.records()
                        .newAgency("F", "F", core);
        f.insert();
        Agency g = model.records()
                        .newAgency("G", "G", core);
        g.insert();
        Agency h = model.records()
                        .newAgency("H", "H", core);
        h.insert();
        Agency i = model.records()
                        .newAgency("I", "I", core);
        i.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();
        ExistentialNetworkRecord edgeC = model.records()
                                              .newExistentialNetwork(c, equals2,
                                                                     d, core);
        edgeC.insert();
        ExistentialNetworkRecord edgeD = model.records()
                                              .newExistentialNetwork(d, equals2,
                                                                     e, core);
        edgeD.insert();
        ExistentialNetworkRecord edgeE = model.records()
                                              .newExistentialNetwork(e, equals2,
                                                                     f, core);
        edgeE.insert();
        ExistentialNetworkRecord edgeF = model.records()
                                              .newExistentialNetwork(f, equals2,
                                                                     g, core);
        edgeF.insert();
        ExistentialNetworkRecord edgeG = model.records()
                                              .newExistentialNetwork(g, equals2,
                                                                     h, core);
        edgeG.insert();
        ExistentialNetworkRecord edgeH = model.records()
                                              .newExistentialNetwork(h, equals2,
                                                                     i, core);
        edgeH.insert();

        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
        edgeA = em.find(ExistentialNetworkRecord.class, edgeA.getId());
        edgeA.delete();
        a = em.find(Agency.class, a.getId());
        List<Agency> children = model.getPhantasmModel()
                                     .getChildren(a, equals);
        assertEquals(children.toString(), 1, children.size());
        assertEquals(0, model.getPhantasmModel()
                             .getChildren(a, equals2)
                             .size());
        b = em.find(Agency.class, b.getId());
        assertEquals(1, model.getPhantasmModel()
                             .getChildren(b, equals2)
                             .size());
    }

    @Test
    public void testCascadedDeletePremise2() throws Exception {
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals = model.records()
                                   .newRelationship("= d",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records()
                                    .newRelationship("also equals",
                                                     "an alias for equals",
                                                     core);
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        Agency d = model.records()
                        .newAgency("D", "D", core);
        d.insert();
        Agency e = model.records()
                        .newAgency("E", "E", core);
        e.insert();
        Agency f = model.records()
                        .newAgency("F", "F", core);
        f.insert();
        Agency g = model.records()
                        .newAgency("G", "G", core);
        g.insert();
        Agency h = model.records()
                        .newAgency("H", "H", core);
        h.insert();
        Agency i = model.records()
                        .newAgency("I", "I", core);
        i.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();
        ExistentialNetworkRecord edgeC = model.records()
                                              .newExistentialNetwork(c, equals2,
                                                                     d, core);
        edgeC.insert();
        ExistentialNetworkRecord edgeD = model.records()
                                              .newExistentialNetwork(d, equals2,
                                                                     e, core);
        edgeD.insert();
        ExistentialNetworkRecord edgeE = model.records()
                                              .newExistentialNetwork(e, equals2,
                                                                     f, core);
        edgeE.insert();
        ExistentialNetworkRecord edgeF = model.records()
                                              .newExistentialNetwork(f, equals2,
                                                                     g, core);
        edgeF.insert();
        ExistentialNetworkRecord edgeG = model.records()
                                              .newExistentialNetwork(g, equals2,
                                                                     h, core);
        edgeG.insert();
        ExistentialNetworkRecord edgeH = model.records()
                                              .newExistentialNetwork(h, equals2,
                                                                     i, core);
        edgeH.insert();

        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
        edgeB = em.find(ExistentialNetworkRecord.class, edgeB.getId());
        edgeB.delete();
        a = em.find(Agency.class, a.getId());
        assertEquals(1, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
    }

    @Test
    public void testDeduction() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-db.xml")));
        Agency core = model.getKernel()
                           .getCore();

        Relationship a = model.getPhantasmModel()
                              .create("a", "a", "a'", "a'");
        Relationship b = model.getPhantasmModel()
                              .create("b", "b", "b'", "b'");
        Relationship c = model.getPhantasmModel()
                              .create("c", "c", "c'", "c'");
        Relationship d = model.getPhantasmModel()
                              .create("d", "d", "d'", "d'");
        Relationship e = model.getPhantasmModel()
                              .create("e", "e", "e'", "e'");
        Relationship f = model.getPhantasmModel()
                              .create("f", "f", "f'", "f'");
        Relationship g = model.getPhantasmModel()
                              .create("g", "g", "g'", "g'");

        NetworkInferenceRecord aIsB = model.records()
                                           .newNetworkInference(a, b, a, core);
        aIsB.insert();
        NetworkInferenceRecord aIsC = model.records()
                                           .newNetworkInference(a, c, a, core);
        aIsC.insert();
        NetworkInferenceRecord aIsD = model.records()
                                           .newNetworkInference(a, d, a, core);
        aIsD.insert();
        NetworkInferenceRecord aIsE = model.records()
                                           .newNetworkInference(a, e, a, core);
        aIsE.insert();
        NetworkInferenceRecord aIsF = model.records()
                                           .newNetworkInference(a, f, a, core);
        aIsF.insert();
        NetworkInferenceRecord aIsG = model.records()
                                           .newNetworkInference(a, g, a, core);
        aIsG.insert();
        Agency A = model.records()
                        .newAgency("A", "A", core);
        A.insert();
        Agency B = model.records()
                        .newAgency("B", "B", core);
        B.insert();
        Agency C = model.records()
                        .newAgency("C", "C", core);
        C.insert();
        Agency D = model.records()
                        .newAgency("D", "D", core);
        D.insert();
        Agency E = model.records()
                        .newAgency("E", "E", core);
        E.insert();
        Agency F = model.records()
                        .newAgency("F", "F", core);
        F.insert();
        Agency G = model.records()
                        .newAgency("G", "G", core);
        G.insert();
        Agency H = model.records()
                        .newAgency("H", "H", core);
        H.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(A, a, B,
                                                                     core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(B, b, C,
                                                                     core);
        edgeB.insert();
        ExistentialNetworkRecord edgeC = model.records()
                                              .newExistentialNetwork(C, c, D,
                                                                     core);
        edgeC.insert();
        ExistentialNetworkRecord edgeD = model.records()
                                              .newExistentialNetwork(D, d, E,
                                                                     core);
        edgeD.insert();
        ExistentialNetworkRecord edgeE = model.records()
                                              .newExistentialNetwork(E, e, F,
                                                                     core);
        edgeE.insert();
        ExistentialNetworkRecord edgeF = model.records()
                                              .newExistentialNetwork(F, f, G,
                                                                     core);
        edgeF.insert();
        ExistentialNetworkRecord edgeG = model.records()
                                              .newExistentialNetwork(G, g, H,
                                                                     core);
        edgeG.insert();

        A = em.find(Agency.class, A.getId());
        List<Agency> children = model.getPhantasmModel()
                                     .getChildren(A, a);
        assertEquals(String.format("%s", children), 7, children.size());
    }

    @Test
    public void testGetImmediateRelationships() throws Exception {
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals2 = model.records()
                                    .newRelationship("equals 2",
                                                     "an alias for equals",
                                                     core);

        Relationship equals = model.records()
                                   .newRelationship("equals on another level",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();

        assertEquals(1, model.getPhantasmModel()
                             .getImmediateRelationships(a)
                             .size());
    }

    @Test
    public void testGetTransitiveRelationships() throws SQLException {
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals = model.records()
                                   .newRelationship("so equals so",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();
        ;

        Relationship equals2 = model.records()
                                    .newRelationship("so equals",
                                                     "an alias for equals",
                                                     core);
        equals2.setInverse(equals2.getId());
        equals2.insert();
        ;
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();

        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        ;
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        ;
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        ;
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();
        assertEquals(2, model.getPhantasmModel()
                             .getTransitiveRelationships(a)
                             .size());
    }

    @Test
    public void testInGroup() {
        Relationship classifier = model.records()
                                       .newRelationship("test group classifier",
                                                        kernel.getCore());
        classifier.insert();
        ;
        Relationship inverse = model.records()
                                    .newRelationship("inverse test group classifier",
                                                     kernel.getCore());
        inverse.insert();
        classifier.setInverse(inverse.getId());
        inverse.setInverse(classifier.getId());
        Agency classification = model.records()
                                     .newAgency("test in group agency classification",
                                                kernel.getCore());
        classification.insert();
        Aspect<Agency> myAspect = new Aspect<Agency>(classifier,
                                                     classification);
        @SuppressWarnings("unchecked")
        Agency testAgency = model.getPhantasmModel()
                                 .create("test agency in group", "test",
                                         myAspect, kernel.getCore());
        testAgency.insert();
        List<Agency> inGroup = model.getPhantasmModel()
                                    .getInGroup(classification, inverse);
        assertNotNull(inGroup);
        assertEquals(1, inGroup.size());
        assertEquals(testAgency, inGroup.get(0));
    }

    @Test
    public void testIterativeInference() throws Exception {
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals = model.records()
                                   .newRelationship("= c",
                                                    "an alias for equals",
                                                    core);
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records()
                                    .newRelationship("equals too",
                                                     "an alias for equals",
                                                     core);
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals,
                                                                    core);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A", core);
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B", core);
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C", core);
        c.insert();
        Agency d = model.records()
                        .newAgency("D", "D", core);
        d.insert();
        Agency e = model.records()
                        .newAgency("E", "E", core);
        e.insert();
        Agency f = model.records()
                        .newAgency("F", "F", core);
        f.insert();
        Agency g = model.records()
                        .newAgency("G", "G", core);
        g.insert();
        Agency h = model.records()
                        .newAgency("H", "H", core);
        h.insert();
        Agency i = model.records()
                        .newAgency("I", "I", core);
        i.insert();
        ExistentialNetworkRecord edgeA = model.records()
                                              .newExistentialNetwork(a, equals,
                                                                     b, core);
        edgeA.insert();
        ExistentialNetworkRecord edgeB = model.records()
                                              .newExistentialNetwork(b, equals2,
                                                                     c, core);
        edgeB.insert();
        ExistentialNetworkRecord edgeC = model.records()
                                              .newExistentialNetwork(c, equals2,
                                                                     d, core);
        edgeC.insert();
        ExistentialNetworkRecord edgeD = model.records()
                                              .newExistentialNetwork(d, equals2,
                                                                     e, core);
        edgeD.insert();
        ExistentialNetworkRecord edgeE = model.records()
                                              .newExistentialNetwork(e, equals2,
                                                                     f, core);
        edgeE.insert();
        ExistentialNetworkRecord edgeF = model.records()
                                              .newExistentialNetwork(f, equals2,
                                                                     g, core);
        edgeF.insert();
        ExistentialNetworkRecord edgeG = model.records()
                                              .newExistentialNetwork(g, equals2,
                                                                     h, core);
        edgeG.insert();
        ExistentialNetworkRecord edgeH = model.records()
                                              .newExistentialNetwork(h, equals2,
                                                                     i, core);
        edgeH.insert();

        a = em.find(Agency.class, a.getId());
        assertEquals(8, model.getPhantasmModel()
                             .getChildren(a, equals)
                             .size());
    }

    @Test
    public void testNotInGroup() {
        Relationship classifier = model.records()
                                       .newRelationship("test not in group classifier",
                                                        kernel.getCore());
        classifier.insert();
        Relationship inverse = model.records()
                                    .newRelationship("inverse test not in group classifier",
                                                     kernel.getCore());
        inverse.insert();
        classifier.setInverse(inverse.getId());
        inverse.setInverse(classifier.getId());
        Agency classification = model.records()
                                     .newAgency("test not in group agency classification",
                                                kernel.getCore());
        classification.insert();
        em.flush();
        List<Agency> notInGroup = model.getPhantasmModel()
                                       .getNotInGroup(classification, inverse);
        assertNotNull(notInGroup);
        assertTrue(!notInGroup.isEmpty());
    }
}
