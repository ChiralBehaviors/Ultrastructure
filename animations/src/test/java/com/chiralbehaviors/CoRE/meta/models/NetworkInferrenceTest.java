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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class NetworkInferrenceTest extends AbstractModelTest {

    @Test
    public void testDeduction() throws Exception {

        Tuple<Relationship, Relationship> rels = model.records()
                                                      .newRelationship("a", "a",
                                                                       "a'",
                                                                       "a'");
        rels.a.insert();
        rels.b.insert();
        Relationship a = rels.a;

        rels = model.records()
                    .newRelationship("b", "b", "b'", "b'");
        rels.a.insert();
        rels.b.insert();
        Relationship b = rels.a;

        rels = model.records()
                    .newRelationship("c", "c", "c'", "c'");
        rels.a.insert();
        rels.b.insert();
        Relationship c = rels.a;

        rels = model.records()
                    .newRelationship("d", "d", "d'", "d'");
        rels.a.insert();
        rels.b.insert();
        Relationship d = rels.a;

        rels = model.records()
                    .newRelationship("e", "e", "e'", "e'");
        rels.a.insert();
        rels.b.insert();
        Relationship e = rels.a;

        rels = model.records()
                    .newRelationship("f", "f", "f'", "f'");
        rels.a.insert();
        rels.b.insert();
        Relationship f = rels.a;

        rels = model.records()
                    .newRelationship("g", "g", "g'", "g'");
        rels.a.insert();
        rels.b.insert();
        Relationship g = rels.a;

        NetworkInferenceRecord aIsB = model.records()
                                           .newNetworkInference(a, b, a);
        aIsB.insert();
        NetworkInferenceRecord aIsC = model.records()
                                           .newNetworkInference(a, c, a);
        aIsC.insert();
        NetworkInferenceRecord aIsD = model.records()
                                           .newNetworkInference(a, d, a);
        aIsD.insert();
        NetworkInferenceRecord aIsE = model.records()
                                           .newNetworkInference(a, e, a);
        aIsE.insert();
        NetworkInferenceRecord aIsF = model.records()
                                           .newNetworkInference(a, f, a);
        aIsF.insert();
        NetworkInferenceRecord aIsG = model.records()
                                           .newNetworkInference(a, g, a);
        aIsG.insert();
        Agency A = model.records()
                        .newAgency("A", "A");
        A.insert();
        Agency B = model.records()
                        .newAgency("B", "B");
        B.insert();
        Agency C = model.records()
                        .newAgency("C", "C");
        C.insert();
        Agency D = model.records()
                        .newAgency("D", "D");
        D.insert();
        Agency E = model.records()
                        .newAgency("E", "E");
        E.insert();
        Agency F = model.records()
                        .newAgency("F", "F");
        F.insert();
        Agency G = model.records()
                        .newAgency("G", "G");
        G.insert();
        Agency H = model.records()
                        .newAgency("H", "H");
        H.insert();
        EdgeRecord edgeA = model.records()
                                .newExistentialNetwork(A, a, B);
        edgeA.insert();
        EdgeRecord edgeB = model.records()
                                .newExistentialNetwork(B, b, C);
        edgeB.insert();
        EdgeRecord edgeC = model.records()
                                .newExistentialNetwork(C, c, D);
        edgeC.insert();
        EdgeRecord edgeD = model.records()
                                .newExistentialNetwork(D, d, E);
        edgeD.insert();
        EdgeRecord edgeE = model.records()
                                .newExistentialNetwork(E, e, F);
        edgeE.insert();
        EdgeRecord edgeF = model.records()
                                .newExistentialNetwork(F, f, G);
        edgeF.insert();
        EdgeRecord edgeG = model.records()
                                .newExistentialNetwork(G, g, H);
        edgeG.insert();

        model.flush();

        Inference inference = new Inference() {

            @Override
            public Model model() {
                return model;
            }
        };

        inference.dynamicInference(A.getId(), a.getId(), H.getId());

        List<ExistentialRuleform> children = model.getPhantasmModel()
                                                  .getChildren(A, a,
                                                               ExistentialDomain.Agency);
        assertEquals(String.format("%s", children.stream()
                                                 .map(r -> r.getName())
                                                 .collect(Collectors.toList())),
                     7, children.size());

        List<EdgeRecord> childrenLinks = model.getPhantasmModel()
                                              .getChildrenLinks(A, a);
        assertEquals(7, childrenLinks.size());
    }

    @Test
    public void testIterativeInference() throws Exception {
        Relationship equals = model.records()
                                   .newRelationship("= c",
                                                    "an alias for equals");
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records()
                                    .newRelationship("equals too",
                                                     "an alias for equals");
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records()
                                               .newNetworkInference(equals,
                                                                    equals2,
                                                                    equals);
        aEqualsA.insert();
        Agency a = model.records()
                        .newAgency("A", "A");
        a.insert();
        Agency b = model.records()
                        .newAgency("B", "B");
        b.insert();
        Agency c = model.records()
                        .newAgency("C", "C");
        c.insert();
        Agency d = model.records()
                        .newAgency("D", "D");
        d.insert();
        Agency e = model.records()
                        .newAgency("E", "E");
        e.insert();
        Agency f = model.records()
                        .newAgency("F", "F");
        f.insert();
        Agency g = model.records()
                        .newAgency("G", "G");
        g.insert();
        Agency h = model.records()
                        .newAgency("H", "H");
        h.insert();
        Agency i = model.records()
                        .newAgency("I", "I");
        i.insert();
        EdgeRecord edgeA = model.records()
                                .newExistentialNetwork(a, equals, b);
        edgeA.insert();
        EdgeRecord edgeB = model.records()
                                .newExistentialNetwork(b, equals2, c);
        edgeB.insert();
        EdgeRecord edgeC = model.records()
                                .newExistentialNetwork(c, equals2, d);
        edgeC.insert();
        EdgeRecord edgeD = model.records()
                                .newExistentialNetwork(d, equals2, e);
        edgeD.insert();
        EdgeRecord edgeE = model.records()
                                .newExistentialNetwork(e, equals2, f);
        edgeE.insert();
        EdgeRecord edgeF = model.records()
                                .newExistentialNetwork(f, equals2, g);
        edgeF.insert();
        EdgeRecord edgeG = model.records()
                                .newExistentialNetwork(g, equals2, h);
        edgeG.insert();
        EdgeRecord edgeH = model.records()
                                .newExistentialNetwork(h, equals2, i);
        edgeH.insert();

        model.flush();

        assertEquals(8, model.getPhantasmModel()
                             .getChildren(a, equals, ExistentialDomain.Agency)
                             .size());
    }
}
