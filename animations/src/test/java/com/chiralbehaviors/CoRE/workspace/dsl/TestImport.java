/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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
package com.chiralbehaviors.CoRE.workspace.dsl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;

/**
 * @author hparry
 *
 */
public class TestImport extends AbstractModelTest {
    @Test
    public void testExampleWorkspace() throws Exception {
        em.getTransaction().begin();
        WorkspaceImporter importer = WorkspaceImporter.createWorkspace(getClass().getResourceAsStream("/thing.wsp"),
                                                                      model);
        em.flush();
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(
                                                                        importer.getWorkspace().getDefiningProduct(),
                                                                        model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope().lookup("kernel", "IsA"));
    }
}
