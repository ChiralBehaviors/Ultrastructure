/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceResourceTest extends AbstractModelTest {

    @Test
    public void testResource() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        assertEquals(1, resource.getWorkspaces(model.getCurrentPrincipal(),
                                               model.create())
                                .size());
    }

    @Test
    public void testLoadWorkspace() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        String iri = resource.loadWorkspace(null, null,
                                            getClass().getResourceAsStream(THING_1_JSON),
                                            model.create());
        assertEquals(THING_URI, iri);
    }

    @Test
    public void testSerializeWorkspace() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        String serialized = resource.serializeWorkspace(null,
                                                        WellKnownObject.KERNEL_IRI,
                                                        model.create());
        assertNotNull(serialized);
    }

    @Test
    public void testManifest() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        String iri = resource.manifest(null, null,
                                       getClass().getResourceAsStream("/thing.wsp"),
                                       model.create());
        assertEquals(THING_URI, iri);
    }

    @Test
    public void testSnapshot() throws Exception {
        CoreUser frank = model.construct(CoreUser.class,
                                         ExistentialDomain.Agency, "frank",
                                         "TV's frank");

        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        String snapshot = resource.snapshot(null, model.create());
        assertNotNull(snapshot);
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .rollback();
        assertNull(model.records()
                        .resolve(frank.getRuleform()
                                      .getId()));
        resource.loadSnapshot(null, null,
                              new ByteArrayInputStream(snapshot.getBytes()),
                              model.create());
        assertNotNull(model.records()
                           .resolve(frank.getRuleform()
                                         .getId()));
    }
}
