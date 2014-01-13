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

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.agency.access.AgencyLocationAccessAuthorization;
import com.hellblazer.CoRE.agency.access.AgencyProductAccessAuthorization;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 * 
 */
public class AgencyModelTest extends AbstractModelTest {

    @Test
    public void testIsLocationAccessible() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Agency a = new Agency("A", "A", core);
        em.persist(a);
        Agency b = new Agency("B", "B", core);
        em.persist(b);
        AgencyNetwork edgeA = new AgencyNetwork(a, isA, b, core);
        em.persist(edgeA);
        Location ag = new Location("AG", "AG", core);
        em.persist(ag);
        Location ag2 = new Location("AG2", "AG2", core);
        em.persist(ag2);

        LocationNetwork aNet = new LocationNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        AgencyLocationAccessAuthorization auth = new AgencyLocationAccessAuthorization(
                                                                                       a,
                                                                                       equals,
                                                                                       ag,
                                                                                       core);
        em.persist(auth);
        em.getTransaction().commit();

        AgencyModelImpl model = new AgencyModelImpl(em);
        assertTrue(model.isAccessible(a, null, equals, ag, null));
        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

    }

    @Test
    public void testIsProductAccessible() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Agency a = new Agency("A", "A", core);
        em.persist(a);
        Agency b = new Agency("B", "B", core);
        em.persist(b);
        AgencyNetwork edgeA = new AgencyNetwork(a, isA, b, core);
        em.persist(edgeA);
        Product ag = new Product("AG", "AG", core);
        em.persist(ag);
        Product ag2 = new Product("AG2", "AG2", core);
        em.persist(ag2);

        ProductNetwork aNet = new ProductNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        AgencyProductAccessAuthorization auth = new AgencyProductAccessAuthorization(
                                                                                     a,
                                                                                     equals,
                                                                                     ag,
                                                                                     core);
        em.persist(auth);
        em.getTransaction().commit();

        AgencyModelImpl model = new AgencyModelImpl(em);
        assertTrue(model.isAccessible(a, null, equals, ag, null));
        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

    }

    @Test
    public void testSimpleNetworkPropagation() throws SQLException {
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
        Agency a = new Agency("A", "A", core);
        em.persist(a);
        Agency b = new Agency("B", "B", core);
        em.persist(b);
        Agency c = new Agency("C", "C", core);
        em.persist(c);
        AgencyNetwork edgeA = new AgencyNetwork(a, equals, b, core);
        em.persist(edgeA);
        AgencyNetwork edgeB = new AgencyNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();
        em.clear();

        List<AgencyNetwork> edges = em.createQuery("SELECT edge FROM AgencyNetwork edge WHERE edge.inferred = 1",
                                                   AgencyNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }

}
