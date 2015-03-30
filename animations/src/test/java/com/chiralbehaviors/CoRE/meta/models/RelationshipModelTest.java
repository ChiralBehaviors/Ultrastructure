/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;

/**
 * @author hhildebrand
 *
 */
public class RelationshipModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
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
        Relationship a = new Relationship("A", core);
        a.setInverse(a);
        em.persist(a);
        Relationship b = new Relationship("B", core);
        b.setInverse(b);
        em.persist(b);
        Relationship c = new Relationship("C", core);
        c.setInverse(c);
        em.persist(c);
        RelationshipNetwork edgeA = new RelationshipNetwork(a, equals, b, core);
        em.persist(edgeA);
        RelationshipNetwork edgeB = new RelationshipNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.flush();
        em.clear();

        List<RelationshipNetwork> edges = em.createQuery("SELECT edge FROM RelationshipNetwork edge WHERE edge.inference.id <> 'AAAAAAAAAAAAAAAAAAAAAA'",
                                                         RelationshipNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }
}
