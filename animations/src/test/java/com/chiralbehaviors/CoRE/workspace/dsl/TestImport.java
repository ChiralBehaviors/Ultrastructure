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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.JsonImporter;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;

/**
 * @author hhildebrand
 *
 */
public class TestImport extends AbstractModelTest {

    @Test
    public void testExampleWorkspace() throws Exception {
        Product definingProduct;
        try {
            JsonImporter importer = JsonImporter.manifest(getClass().getResource("/thing.json"),
                                                                    model);
            definingProduct = importer.getWorkspace()
                                      .getDefiningProduct();
        } catch (IllegalStateException e) {
            LoggerFactory.getLogger(TestImport.class)
                         .info("Not loading thing ontology version 1: {}",
                               e.getMessage());
            definingProduct = model.create()
                                   .selectFrom(EXISTENTIAL)
                                   .where(EXISTENTIAL.ID.equal(WorkspaceAccessor.uuidOf(THING_URI)))
                                   .fetchOne()
                                   .into(Product.class);
        }

        EditableWorkspace workspace = new DatabaseBackedWorkspace(definingProduct,
                                                                  model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup("kernel", ReferenceType.Existential, "IsA"));
    }

    @Test
    public void testIncrementalVersionUpdate() throws Exception {
        try {
            JsonImporter.manifest(getClass().getResourceAsStream("/thing.json"),
                                       model);
        } catch (IllegalStateException e) {
            LoggerFactory.getLogger(TestImport.class)
                         .info("Not loading thing ontology version 1: {}",
                               e.getMessage());
        }
        // load version 2

        JsonImporter importer = JsonImporter.manifest(getClass().getResourceAsStream("/thing.2.def.json"),
                                                                model);
        EditableWorkspace workspace = new DatabaseBackedWorkspace(importer.getWorkspace()
                                                                          .getDefiningProduct(),
                                                                  model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup(ReferenceType.Existential, "TheDude"));
        Product definingProduct = workspace.getDefiningProduct();
        assertEquals(2, definingProduct.getVersion()
                                       .intValue());
        assertEquals("Phantasm Demo V2", definingProduct.getName());
        assertEquals("Test of Workspace versioning",
                     definingProduct.getDescription());
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup("kernel", ReferenceType.Existential, "IsA"));
    }

    @Test
    public void testImport() throws Exception {
        Product definingProduct;
        JsonImporter importer = JsonImporter.manifest(getClass().getResourceAsStream("/import-test.json"),
                                                                model);
        definingProduct = importer.getWorkspace()
                                  .getDefiningProduct();

        EditableWorkspace workspace = new DatabaseBackedWorkspace(definingProduct,
                                                                  model);
        assertNotNull(workspace);
        assertNotNull(workspace.getScope()
                               .lookup(ReferenceType.Existential, "kernel"));
    }
}
