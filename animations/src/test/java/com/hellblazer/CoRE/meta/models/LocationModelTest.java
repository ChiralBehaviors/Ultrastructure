/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class LocationModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        Resource core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        Location a = new Location("A", "A", core);
        em.persist(a);
        Location b = new Location("B", "B", core);
        em.persist(b);
        Location c = new Location("C", "C", core);
        em.persist(c);
        LocationNetwork edgeA = new LocationNetwork(a, equals, b, core);
        em.persist(edgeA);
        LocationNetwork edgeB = new LocationNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();
        em.clear();

        List<LocationNetwork> edges = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inferred = TRUE",
                                                     LocationNetwork.class).getResultList();
        assertEquals(1, edges.size());
        LocationNetwork inferredEdge = edges.get(0);
        assertEquals(model.getKernel().getPropagationSoftware(),
                     inferredEdge.getUpdatedBy());
        assertEquals(a, inferredEdge.getParent());
        assertEquals(c, inferredEdge.getChild());
        assertEquals(equals, inferredEdge.getRelationship());
    }

}
