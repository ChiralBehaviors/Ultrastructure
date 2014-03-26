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

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.access.AgencyLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.agency.access.AgencyProductAccessAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.models.AgencyModelImpl;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

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
