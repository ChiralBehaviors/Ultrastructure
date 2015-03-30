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

        List<RelationshipNetwork> edges = em.createQuery("SELECT edge FROM RelationshipNetwork edge WHERE edge.inference.id <> 'AAAAAAAAAAAAAAAAAAAAAA'",
                                                         RelationshipNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }
}
