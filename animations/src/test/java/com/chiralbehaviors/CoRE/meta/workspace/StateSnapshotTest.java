/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class StateSnapshotTest extends AbstractModelTest {

    @Test
    public void testSnap() throws Exception {
        UUID id;
        try (Model myModel = new ModelImpl(emf)) {
            myModel.create()
                   .getTransaction()
                   .begin();
            CoreUser frank = myModel.construct(CoreUser.class, "frank",
                                               "he's frank");
            id = frank.getRuleform()
                      .getId();
            StateSnapshot snap = myModel.snapshot();
            try (OutputStream os = new FileOutputStream(TARGET_THINGS_JSON)) {
                new ObjectMapper().registerModule(new CoREModule())
                                  .writeValue(os, snap);
            }
        }
        Agency frankenstein = em.find(Agency.class, id);
        assertNull("Shouldn't be alive", frankenstein);

        try (Model myModel = new ModelImpl(emf)) {
            myModel.create()
                   .getTransaction()
                   .begin();
            StateSnapshot snapshot;
            try (InputStream os = new FileInputStream(TARGET_THINGS_JSON)) {
                snapshot = new ObjectMapper().registerModule(new CoREModule())
                                             .readValue(os,
                                                        StateSnapshot.class);
            }
            snapshot.retarget(myModel.create());
            myModel.create()
                   .flush();
            frankenstein = myModel.create()
                                  .find(Agency.class, id);
            assertNotNull("Should be found", frankenstein);
        }
        frankenstein = em.find(Agency.class, id);
        assertNull("Shouldn't be alive", frankenstein);
    }
}