/**
 * Copyright 2014, Chiral Behaviors, LLC.
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
package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 *
 */
public class ProtocolTest extends AbstractModelTest {

    @Test
    public void testMatchOnAssignTo() {
        em.getTransaction().begin();
        Product fireFuzzyGreenWarhead = new Product(
                                                    "FireFuzzyGreenWarheadService",
                                                    null, kernel.getCore());
        em.persist(fireFuzzyGreenWarhead);
        em.flush();

        Agency halIncandenza = new Agency("HalIncandenza", null,
                                          kernel.getCore());
        em.persist(halIncandenza);

        Agency michaelPemulous = new Agency("MichaelPemulous", null,
                                            kernel.getCore());
        em.persist(michaelPemulous);

        Protocol infiniteTest = model.getJobModel().newInitializedProtocol(fireFuzzyGreenWarhead,
                                                                           kernel.getCore());
        infiniteTest.setAssignTo(halIncandenza);
        infiniteTest.setChildAssignTo(michaelPemulous);
        infiniteTest.setChildService(fireFuzzyGreenWarhead);
        em.persist(infiniteTest);

        Protocol infiniteTest2 = model.getJobModel().newInitializedProtocol(fireFuzzyGreenWarhead,
                                                                            kernel.getCore());
        infiniteTest2.setAssignTo(halIncandenza);
        infiniteTest2.setChildAssignTo(michaelPemulous);
        infiniteTest2.setChildService(fireFuzzyGreenWarhead);
        em.persist(infiniteTest2);

        Job startWW3 = model.getJobModel().newInitializedJob(fireFuzzyGreenWarhead,
                                                             kernel.getCore());
        startWW3.setAssignTo(halIncandenza);
        em.persist(startWW3);
        em.flush();

        assertEquals(2, model.getJobModel().getAllChildren(startWW3).size());

    }
}
