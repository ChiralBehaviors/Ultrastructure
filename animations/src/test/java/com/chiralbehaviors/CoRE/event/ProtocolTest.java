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

import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;

/**
 * @author hparry
 *
 */
public class ProtocolTest extends AbstractModelTest {

    @Test
    public void testMatchOnAssignTo() throws Exception {
        Product fireFuzzyGreenWarhead = model.records()
                                             .newProduct("FireFuzzyGreenWarheadService");
        fireFuzzyGreenWarhead.insert();

        Agency halIncandenza = model.records()
                                    .newAgency("HalIncandenza");
        halIncandenza.insert();

        Agency michaelPemulous = model.records()
                                      .newAgency("MichaelPemulous");
        michaelPemulous.insert();

        ProtocolRecord infiniteTest = model.getJobModel()
                                           .newInitializedProtocol(fireFuzzyGreenWarhead);
        infiniteTest.setAssignTo(halIncandenza.getId());
        infiniteTest.setChildAssignTo(michaelPemulous.getId());
        infiniteTest.setChildService(fireFuzzyGreenWarhead.getId());
        infiniteTest.update();

        JobRecord startWW3 = model.getJobModel()
                                  .newInitializedJob(fireFuzzyGreenWarhead);
        startWW3.setAssignTo(halIncandenza.getId());
        startWW3.update();
        model.flush();
        assertEquals(1, model.getJobModel()
                             .getAllChildren(startWW3)
                             .size());

    }

    @Test
    public void testMetaprotocolMatch() throws Exception {
        Product service = model.records()
                               .newProduct("*service*");
        service.insert();

        Product service2 = model.records()
                                .newProduct("*service 2*");
        service2.insert();

        Agency assignTo = model.records()
                               .newAgency("*assignTo*");
        assignTo.insert();

        Agency delegate = model.records()
                               .newAgency("c");
        delegate.insert();
        Agency classification = model.records()
                                     .newAgency("*classification*");
        classification.insert();
        model.getPhantasmModel()
             .link(assignTo, model.getKernel()
                                  .getInstanceOf(),
                   classification);

        Location deliverTo = model.records()
                                  .newLocation("home");
        deliverTo.insert();
        Location locationClassification = model.records()
                                               .newLocation("no place");
        locationClassification.insert();
        model.getPhantasmModel()
             .link(deliverTo, model.getKernel()
                                   .getInstanceOf(),
                   locationClassification);

        ProtocolRecord proto = model.getJobModel()
                                    .newInitializedProtocol(service);
        proto.setAssignTo(classification.getId());
        proto.setChildAssignTo(delegate.getId());
        proto.setDeliverTo(locationClassification.getId());
        proto.setChildDeliverTo(deliverTo.getId());
        proto.setChildService(service2.getId());
        proto.update();

        MetaProtocolRecord meta = model.getJobModel()
                                       .newInitializedMetaProtocol(service);
        meta.setAssignTo(model.getKernel()
                              .getInstanceOf()
                              .getId());
        meta.setDeliverTo(model.getKernel()
                               .getInstanceOf()
                               .getId());
        meta.update();

        JobRecord job = model.getJobModel()
                             .newInitializedJob(service);
        job.setAssignTo(assignTo.getId());
        job.setDeliverTo(deliverTo.getId());
        job.update();
        Map<ProtocolRecord, InferenceMap> match = model.getJobModel()
                                                       .getProtocols(job);
        assertEquals(1, match.size());
        model.flush();
        assertEquals(1, model.getJobModel()
                             .getAllChildren(job)
                             .size());

    }
}
