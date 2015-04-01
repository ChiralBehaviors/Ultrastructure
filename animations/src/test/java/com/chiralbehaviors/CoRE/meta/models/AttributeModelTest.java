/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AttributeModelTest extends AbstractModelTest {

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
        Attribute a = new Attribute("A", "A", ValueType.BOOLEAN, core);
        em.persist(a);
        Attribute b = new Attribute("B", "B", ValueType.BOOLEAN, core);
        em.persist(b);
        Attribute c = new Attribute("C", "C", ValueType.BOOLEAN, core);
        em.persist(c);
        AttributeNetwork edgeA = new AttributeNetwork(a, equals, b, core);
        em.persist(edgeA);
        AttributeNetwork edgeB = new AttributeNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.flush();

        TypedQuery<AttributeNetwork> query = em.createQuery("SELECT edge FROM AttributeNetwork edge WHERE edge.inference.id <> :id",
                                                            AttributeNetwork.class);
        query.setParameter("id", new UUID(0, 0));
        List<AttributeNetwork> edges = query.getResultList();
        assertEquals(2, edges.size());
    }

}
