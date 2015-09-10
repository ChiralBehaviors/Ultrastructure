/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
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
package com.chiralbehaviors.CoRE.workspace.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class TestImport extends AbstractModelTest {
    @Test
    public void testNetworkAttributes() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/network-attribute.wsp"),
                                                                model);
        em.flush();
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(importer.getWorkspace()
                                                                                .getDefiningProduct(),
                                                                        model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup("Thing1"));
    }

    @Test
    public void testExampleWorkspace() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.wsp"),
                                                                model);
        em.flush();
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(importer.getWorkspace()
                                                                                .getDefiningProduct(),
                                                                        model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup("kernel", "IsA"));
    }

    @Test
    public void testIncrementalVersionUpdate() throws Exception {
        // load version 1
        WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.wsp"),
                                                                model);
        em.flush();
        // load version 1

        importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.2.wsp"),
                                              model);
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(importer.getWorkspace()
                                                                                .getDefiningProduct(),
                                                                        model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup("TheDude"));
        Product definingProduct = workspace.getDefiningProduct();
        assertEquals(2, definingProduct.getVersion());
        assertEquals("Phantasm Demo V2", definingProduct.getName());
        assertEquals("Test of Workspace versioning",
                     definingProduct.getDescription());
    }
}
