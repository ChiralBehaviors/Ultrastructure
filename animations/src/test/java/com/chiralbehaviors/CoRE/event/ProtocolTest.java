/**
 * Copyright 2014, Chiral Behaviors, LLC.
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
package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;

/**
 * @author hparry
 *
 */
public class ProtocolTest extends AbstractModelTest {

    @Test
    public void testMatchOnAssignTo() {
        Product fireFuzzyGreenWarhead = model.records()
                                             .newProduct("FireFuzzyGreenWarheadService",
                                                         null,
                                                         kernel.getCore());
        fireFuzzyGreenWarhead.insert();

        Agency halIncandenza = model.records()
                                    .newAgency("HalIncandenza", null,
                                               kernel.getCore());
        halIncandenza.insert();

        Agency michaelPemulous = model.records()
                                      .newAgency("MichaelPemulous", null,
                                                 kernel.getCore());
        michaelPemulous.insert();

        ProtocolRecord infiniteTest = model.getJobModel()
                                           .newInitializedProtocol(fireFuzzyGreenWarhead,
                                                                   kernel.getCore());
        infiniteTest.setAssignTo(halIncandenza.getId());
        infiniteTest.setChildAssignTo(michaelPemulous.getId());
        infiniteTest.setChildService(fireFuzzyGreenWarhead.getId());
        infiniteTest.insert();

        ProtocolRecord infiniteTest2 = model.getJobModel()
                                            .newInitializedProtocol(fireFuzzyGreenWarhead,
                                                                    kernel.getCore());
        infiniteTest2.setAssignTo(halIncandenza.getId());
        infiniteTest2.setChildAssignTo(michaelPemulous.getId());
        infiniteTest2.setChildService(fireFuzzyGreenWarhead.getId());
        infiniteTest2.insert();

        JobRecord startWW3 = model.getJobModel()
                                  .newInitializedJob(fireFuzzyGreenWarhead,
                                                     kernel.getCore());
        startWW3.setAssignTo(halIncandenza.getId());
        startWW3.insert();

        assertEquals(2, model.getJobModel()
                             .getAllChildren(startWW3)
                             .size());

    }
}
