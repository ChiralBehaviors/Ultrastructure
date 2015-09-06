/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class LocationModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        em.createQuery("DELETE FROM LocationNetwork")
          .executeUpdate();
        Agency core = model.getKernel()
                           .getCore();
        Relationship equals = model.getKernel()
                                   .getEquals();

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

        em.flush();

        TypedQuery<LocationNetwork> query = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inference IS NOT NULL",
                                                           LocationNetwork.class);
        List<LocationNetwork> edges = query.getResultList();
        assertEquals(2, edges.size());
    }

}
