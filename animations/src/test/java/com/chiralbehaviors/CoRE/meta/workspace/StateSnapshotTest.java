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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class StateSnapshotTest extends AbstractModelTest {

    private static final String TARGET_TEST_CLASSES = "target/test-classes";

    @Test
    public void testSnap() throws Exception {
        // Need to commit for snap state testing
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .commit();
        UUID tvsFrank;
        WorkspaceSnapshot snap;
        try (Model myModel = new ModelImpl(newConnection())) {
            CoreUser frank = myModel.construct(CoreUser.class,
                                               ExistentialDomain.Agency,
                                               "frank", "TV's frank");
            tvsFrank = frank.getRuleform()
                            .getId();
            snap = myModel.snapshot();
            assertEquals(3, snap.getInserts()
                                 .size());
            try (OutputStream os = new FileOutputStream(new File(TARGET_TEST_CLASSES,
                                                                 THINGS_JSON))) {
                snap.serializeTo(os);
            }
        }

        try (Model myModel = new ModelImpl(newConnection())) {
            Agency frankenstein = myModel.records()
                                         .resolve(tvsFrank);
            assertNull("Shouldn't be alive", frankenstein);

            StateSnapshot snapshot;
            try (InputStream os = new FileInputStream(new File(TARGET_TEST_CLASSES,
                                                               THINGS_JSON))) {
                snapshot = new ObjectMapper().registerModule(new CoREModule())
                                             .readValue(os,
                                                        StateSnapshot.class);
            }
            snapshot.load(myModel.create());
            frankenstein = myModel.records()
                                  .resolve(tvsFrank);
            assertNotNull("Should be found", frankenstein);
        }

        Agency frankenstein = model.records()
                                   .resolve(tvsFrank);
        assertNull("Shouldn't be alive", frankenstein);
    }
}