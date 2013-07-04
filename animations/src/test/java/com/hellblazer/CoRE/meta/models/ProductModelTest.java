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

import com.hellblazer.CoRE.network.NetworkInferrence;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class ProductModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        Resource core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        em.persist(equals2);
        NetworkInferrence aEqualsA = new NetworkInferrence(equals, equals2,
                                                           equals, core);
        em.persist(aEqualsA);
        Product a = new Product("A", "A", core);
        em.persist(a);
        Product b = new Product("B", "B", core);
        em.persist(b);
        Product c = new Product("C", "C", core);
        em.persist(c);
        ProductNetwork edgeA = new ProductNetwork(a, equals, b, core);
        em.persist(edgeA);
        ProductNetwork edgeB = new ProductNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();

        em.getTransaction().begin();

        model.getProductModel().propagate();

        em.getTransaction().commit();
        em.clear();

        List<ProductNetwork> edges = em.createQuery("SELECT edge FROM ProductNetwork edge WHERE edge.inferred = TRUE",
                                                    ProductNetwork.class).getResultList();
        assertEquals(1, edges.size());
        ProductNetwork inferredEdge = edges.get(0);
        assertEquals(model.getKernel().getPropagationSoftware(),
                     inferredEdge.getUpdatedBy());
        assertEquals(a, inferredEdge.getParent());
        assertEquals(c, inferredEdge.getChild());
        assertEquals(equals, inferredEdge.getRelationship());
    }

}
