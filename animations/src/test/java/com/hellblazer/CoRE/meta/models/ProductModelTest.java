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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;

/**
 * @author hhildebrand
 * 
 */
public class ProductModelTest extends AbstractModelTest {

    @Test
    public void testIsAgencyAccessible() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Product a = new Product("A", "A", core);
        em.persist(a);
        Product b = new Product("B", "B", core);
        em.persist(b);
        ProductNetwork edgeA = new ProductNetwork(a, isA, b, core);
        em.persist(edgeA);
        Agency ag = new Agency("AG", "AG", core);
        em.persist(ag);
        Agency ag2 = new Agency("AG2", "AG2", core);
        em.persist(ag2);

        AgencyNetwork aNet = new AgencyNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        ProductAgencyAccessAuthorization auth = new ProductAgencyAccessAuthorization(
                                                                                     a,
                                                                                     equals,
                                                                                     ag,
                                                                                     core);
        em.persist(auth);
        em.getTransaction().commit();

        ProductModelImpl model = new ProductModelImpl(em);
        assertTrue(model.isAccessible(a, null, equals, ag, null));
        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

    }

    @Test
    public void testIsLocationAccessible() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Product a = new Product("A", "A", core);
        em.persist(a);
        Product b = new Product("B", "B", core);
        em.persist(b);
        ProductNetwork edgeA = new ProductNetwork(a, isA, b, core);
        em.persist(edgeA);
        Location ag = new Location("AG", "AG", core);
        em.persist(ag);
        Location ag2 = new Location("AG2", "AG2", core);
        em.persist(ag2);

        LocationNetwork aNet = new LocationNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        ProductLocationAccessAuthorization auth = new ProductLocationAccessAuthorization(
                                                                                         a,
                                                                                         equals,
                                                                                         ag,
                                                                                         core);
        em.persist(auth);
        em.getTransaction().commit();

        ProductModelImpl model = new ProductModelImpl(em);
        assertTrue(model.isAccessible(a, null, equals, ag, null));
        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

    }

    @Test
    public void testLeaves() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Product a = new Product("A", "A", core);
        em.persist(a);
        Product b = new Product("B", "B", core);
        em.persist(b);
        Product c = new Product("c", "c", core);
        em.persist(c);
        ProductNetwork edgeA = new ProductNetwork(a, isA, b, core);
        em.persist(edgeA);
        ProductNetwork edgeB = new ProductNetwork(b, isA, c, core);
        em.persist(edgeB);

        Agency ag = new Agency("AG", "AG", core);
        em.persist(ag);
        Agency ag2 = new Agency("AG2", "AG2", core);
        em.persist(ag2);

        AgencyNetwork aNet = new AgencyNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        ProductAgencyAccessAuthorization auth = new ProductAgencyAccessAuthorization(
                                                                                     a,
                                                                                     isA,
                                                                                     equals,
                                                                                     ag,
                                                                                     isA,
                                                                                     core);
        em.persist(auth);
        em.getTransaction().commit();

        ProductModelImpl model = new ProductModelImpl(em);
        // assertTrue(model.isAccessible(a, null, equals, ag, null));
        //bug here. this should be false since the access auth doesn't have
        //authorizing relationships
        //        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        //        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        //        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

        List<?> stuff = model.getChildren(a, equals);
        System.out.println(stuff);

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
        em.clear();

        List<ProductNetwork> edges = em.createQuery("SELECT edge FROM ProductNetwork edge WHERE edge.inferred = 1",
                                                    ProductNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }
}
