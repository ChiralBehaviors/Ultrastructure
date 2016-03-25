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

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class StateSnapshotTest extends AbstractModelTest {

    @Test
    public void testSnap() throws Exception {
        UUID id;
        CoreUser frank = model.construct(CoreUser.class, "frank", "TV's frank");
        id = frank.getRuleform()
                  .getId();
        WorkspaceSnapshot snap = model.snapshot();
        try (OutputStream os = new FileOutputStream(TARGET_THINGS_JSON)) {
            new ObjectMapper().registerModule(new CoREModule())
                              .writeValue(os, snap);
        }
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .rollback();
        Agency frankenstein = model.records()
                                   .resolve(id);
        assertNull("Shouldn't be alive", frankenstein);

        StateSnapshot snapshot;
        try (InputStream os = new FileInputStream(TARGET_THINGS_JSON)) {
            snapshot = new ObjectMapper().registerModule(new CoREModule())
                                         .readValue(os, StateSnapshot.class);
        }
        snapshot.load(model.create());
        frankenstein = model.records()
                            .resolve(id);
        assertNotNull("Should be found", frankenstein);
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .rollback();
        frankenstein = model.records()
                            .resolve(id);
        assertNull("Shouldn't be alive", frankenstein);
    }
}