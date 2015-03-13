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

import java.math.BigDecimal;
import java.util.List;

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
        em.clear();

        List<IntervalNetwork> edges = em.createQuery("SELECT edge FROM IntervalNetwork edge WHERE edge.inference.id <> 'AAAAAAAAAAAAAAAAAAAAAA'",
                                                     IntervalNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }
}
