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

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.network.NetworkInferrence;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ResourceModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        Resource core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        em.persist(equals2);
        NetworkInferrence aIsA = new NetworkInferrence(equals, equals2, equals,
                                                       core);
        em.persist(aIsA);
        Resource a = new Resource("A", "A", core);
        em.persist(a);
        Resource b = new Resource("B", "B", core);
        em.persist(b);
        Resource c = new Resource("C", "C", core);
        em.persist(c);
        ResourceNetwork edgeA = new ResourceNetwork(a, equals, b, core);
        em.persist(edgeA);
        ResourceNetwork edgeB = new ResourceNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();

        em.getTransaction().begin();

        model.getResourceModel().propagate();

        em.getTransaction().commit();

        @SuppressWarnings("unused")
        List<ResourceNetwork> edges = em.createQuery("SELECT edge FROM ResourceNetwork edge",
                                                     ResourceNetwork.class).getResultList();
        // assertEquals(3, edges.size());
    }

}
