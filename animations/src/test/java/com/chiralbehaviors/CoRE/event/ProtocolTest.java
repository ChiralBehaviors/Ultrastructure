/**
 * Copyright (c) 2014 Chiral Behaviors, LLC, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.event;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.product.Product;

import static org.junit.Assert.*;

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

        Job startWW3 = model.getJobModel().newInitializedJob(fireFuzzyGreenWarhead,
                                                             kernel.getCore());
        startWW3.setAssignTo(halIncandenza);
        em.persist(startWW3);
        em.getTransaction().commit();

        assertEquals(1, model.getJobModel().getAllChildren(startWW3).size());

    }
}
