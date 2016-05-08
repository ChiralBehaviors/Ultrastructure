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

import java.util.UUID;

import org.junit.Test;

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

    public void testLoadWorkspace() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        UUID definingProduct = resource.loadWorkspace(null, null,
                                                      getClass().getResourceAsStream("/thing.wsp"),
                                                      model.create());
        assertNotNull(definingProduct);
    }

    public void testLoadSnapshot() throws Exception {
        WorkspaceResource resource = new WorkspaceResource(getClass().getClassLoader());
        UUID definingProduct = resource.loadWorkspace(null, null,
                                                      getClass().getResourceAsStream("/thing.wsp"),
                                                      model.create());
        assertNotNull(definingProduct);
    }
}
