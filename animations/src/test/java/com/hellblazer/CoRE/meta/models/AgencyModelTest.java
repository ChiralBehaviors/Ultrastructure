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

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
public class AgencyModelTest extends AbstractModelTest {

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

        List<AgencyNetwork> edges = em.createQuery("SELECT edge FROM AgencyNetwork edge WHERE edge.inferred = TRUE",
                                                   AgencyNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }

}
