/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 *
 * This file is part of Ultrastructure.
 *
 * Ultrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * ULtrastructure is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Ultrastructure.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.hellblazer.utils.Tuple;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author hhildebrand
 *
 */
public class NetworkInferrenceTest extends AbstractModelTest {

    @Test
    public void testDeduction() throws Exception {

        Tuple<Relationship, Relationship> rels = model.records().newRelationship("onStreet", "a", "a'", "a'");
        rels.a.insert();
        rels.b.insert();
        Relationship onStreet = rels.a;

        rels = model.records().newRelationship("inCity", "b", "b'", "b'");
        rels.a.insert();
        rels.b.insert();
        Relationship inCity = rels.a;

        rels = model.records().newRelationship("inState", "c", "c'", "c'");
        rels.a.insert();
        rels.b.insert();
        Relationship inState = rels.a;

        rels = model.records().newRelationship("inCountry", "d", "d'", "d'");
        rels.a.insert();
        rels.b.insert();
        Relationship inCountry = rels.a;

        NetworkInferenceRecord aIsB = model.records().newNetworkInference(onStreet, inCity, inCity);
        aIsB.insert();
        NetworkInferenceRecord aIsC = model.records().newNetworkInference(inCity, inState, inState);
        aIsC.insert();
        NetworkInferenceRecord aIsD = model.records().newNetworkInference(inState, inCountry, inCountry);
        aIsD.insert();

        Agency house = model.records().newAgency("House", "A");
        house.insert();
        Agency firstStreet = model.records().newAgency("1st Street", "B");
        firstStreet.insert();
        Agency Portland = model.records().newAgency("Portland", "C");
        Portland.insert();
        Agency Oregon = model.records().newAgency("Oregon", "D");
        Oregon.insert();
        Agency US = model.records().newAgency("US", "E");
        US.insert();

        EdgeRecord edgeA = model.records().newExistentialNetwork(house, onStreet, firstStreet);
        edgeA.insert();
        EdgeRecord edgeB = model.records().newExistentialNetwork(firstStreet, inCity, Portland);
        edgeB.insert();
        EdgeRecord edgeC = model.records().newExistentialNetwork(Portland, inState, Oregon);
        edgeC.insert();
        EdgeRecord edgeD = model.records().newExistentialNetwork(Oregon, inCountry, US);
        edgeD.insert();

        model.flush();

        Inference inf = new Inference() {

            @Override
            public Model model() {
                return model;
            }
        };

        UUID parent = Oregon.getId();
        UUID relationship = inCountry.getId();
        UUID child = US.getId();

        assertTrue(inf.dynamicInference(parent, relationship, child));

        parent = Portland.getId();

        assertTrue(inf.dynamicInference(parent, relationship, child));

        parent = firstStreet.getId();

        assertTrue(inf.dynamicInference(parent, relationship, child));

        parent = house.getId();

        assertTrue(inf.dynamicInference(parent, relationship, child));

    }

    @Ignore
    @Test
    public void testIterativeInference() throws Exception {
        Relationship equals = model.records().newRelationship("= c", "an alias for equals");
        equals.setInverse(equals.getId());
        equals.insert();

        Relationship equals2 = model.records().newRelationship("equals too", "an alias for equals");
        equals2.setInverse(equals2.getId());
        equals2.insert();
        NetworkInferenceRecord aEqualsA = model.records().newNetworkInference(equals, equals2, equals);
        aEqualsA.insert();
        Agency a = model.records().newAgency("A", "A");
        a.insert();
        Agency b = model.records().newAgency("B", "B");
        b.insert();
        Agency c = model.records().newAgency("C", "C");
        c.insert();
        Agency d = model.records().newAgency("D", "D");
        d.insert();
        Agency e = model.records().newAgency("E", "E");
        e.insert();
        Agency f = model.records().newAgency("F", "F");
        f.insert();
        Agency g = model.records().newAgency("G", "G");
        g.insert();
        Agency h = model.records().newAgency("H", "H");
        h.insert();
        Agency i = model.records().newAgency("I", "I");
        i.insert();
        EdgeRecord edgeA = model.records().newExistentialNetwork(a, equals, b);
        edgeA.insert();
        EdgeRecord edgeB = model.records().newExistentialNetwork(b, equals2, c);
        edgeB.insert();
        EdgeRecord edgeC = model.records().newExistentialNetwork(c, equals2, d);
        edgeC.insert();
        EdgeRecord edgeD = model.records().newExistentialNetwork(d, equals2, e);
        edgeD.insert();
        EdgeRecord edgeE = model.records().newExistentialNetwork(e, equals2, f);
        edgeE.insert();
        EdgeRecord edgeF = model.records().newExistentialNetwork(f, equals2, g);
        edgeF.insert();
        EdgeRecord edgeG = model.records().newExistentialNetwork(g, equals2, h);
        edgeG.insert();
        EdgeRecord edgeH = model.records().newExistentialNetwork(h, equals2, i);
        edgeH.insert();

        model.flush();

        assertEquals(8, model.getPhantasmModel().getChildren(a, equals, ExistentialDomain.Agency).size());
    }
}
