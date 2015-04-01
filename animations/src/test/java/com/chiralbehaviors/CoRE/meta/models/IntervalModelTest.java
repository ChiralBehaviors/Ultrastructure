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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public class IntervalModelTest extends AbstractModelTest {

    @Test
    public void testDefaulting() {
        em.getTransaction().begin();
        model.getIntervalModel().newDefaultInterval("int", "erval");
        em.getTransaction().commit();
    }

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
        Interval a = new Interval(BigDecimal.valueOf(0),
                                  BigDecimal.valueOf(100),
                                  kernel.getUnsetUnit(), kernel.getUnsetUnit(),
                                  "A", core);
        em.persist(a);
        Interval b = new Interval(BigDecimal.valueOf(0),
                                  BigDecimal.valueOf(100),
                                  kernel.getUnsetUnit(), kernel.getUnsetUnit(),
                                  "B", core);
        em.persist(b);
        Interval c = new Interval(BigDecimal.valueOf(0),
                                  BigDecimal.valueOf(100),
                                  kernel.getUnsetUnit(), kernel.getUnsetUnit(),
                                  "C", core);
        em.persist(c);
        IntervalNetwork edgeA = new IntervalNetwork(a, equals, b, core);
        em.persist(edgeA);
        IntervalNetwork edgeB = new IntervalNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.flush();

        TypedQuery<IntervalNetwork> query = em.createQuery("SELECT edge FROM IntervalNetwork edge WHERE edge.inference.id <> :id",
                                                           IntervalNetwork.class);
        query.setParameter("id", new UUID(0, 0));
        List<IntervalNetwork> edges = query.getResultList();
        assertEquals(2, edges.size());
    }
}
