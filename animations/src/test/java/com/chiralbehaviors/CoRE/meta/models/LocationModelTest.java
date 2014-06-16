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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.access.LocationAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 * 
 */
public class LocationModelTest extends AbstractModelTest {

    @Test
    public void testIsAgencyAccessible() {
        em.getTransaction().begin();
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();
        Relationship isA = model.getKernel().getIsA();

        Location a = new Location("A", "A", core);
        em.persist(a);
        Location b = new Location("B", "B", core);
        em.persist(b);
        LocationNetwork edgeA = new LocationNetwork(a, isA, b, core);
        em.persist(edgeA);
        Agency ag = new Agency("AG", "AG", core);
        em.persist(ag);
        Agency ag2 = new Agency("AG2", "AG2", core);
        em.persist(ag2);

        AgencyNetwork aNet = new AgencyNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        LocationAgencyAccessAuthorization auth = new LocationAgencyAccessAuthorization(
                                                                                       a,
                                                                                       equals,
                                                                                       ag,
                                                                                       core);
        em.persist(auth);
        em.getTransaction().commit();

        LocationModelImpl model = new LocationModelImpl(em);
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

        Location a = new Location("A", "A", core);
        em.persist(a);
        Location b = new Location("B", "B", core);
        em.persist(b);
        LocationNetwork edgeA = new LocationNetwork(a, isA, b, core);
        em.persist(edgeA);
        Product ag = new Product("AG", "AG", core);
        em.persist(ag);
        Product ag2 = new Product("AG2", "AG2", core);
        em.persist(ag2);

        ProductNetwork aNet = new ProductNetwork(ag, isA, ag2, core);
        em.persist(aNet);

        LocationProductAccessAuthorization auth = new LocationProductAccessAuthorization(
                                                                                         a,
                                                                                         equals,
                                                                                         ag,
                                                                                         core);
        em.persist(auth);
        em.getTransaction().commit();

        LocationModelImpl model = new LocationModelImpl(em);
        assertTrue(model.isAccessible(a, null, equals, ag, null));
        assertTrue(model.isAccessible(b, isA, equals, ag, null));
        assertTrue(model.isAccessible(a, null, equals, ag2, isA));
        assertTrue(model.isAccessible(b, isA, equals, ag2, isA));

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

        List<LocationNetwork> edges = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inference.id <> 'AAAAAAAAAAAAAAAAAAAAAA'",
                                                     LocationNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }

}
