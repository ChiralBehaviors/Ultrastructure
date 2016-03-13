/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshotTest extends AbstractModelTest {

    @Test
    public void testDeltaGeneration() throws Exception {
        File version1File = new File(TARGET_CLASSES_THING_1_JSON);
        File version2File = new File(TARGET_CLASSES_THING_2_JSON);
        File version2_1File = new File(TARGET_CLASSES_THING_1_2_JSON);
        try (Model myModel = new ModelImpl(emf)) {
            EntityManager myEm = myModel.getEntityManager();
            myEm.getTransaction()
                .begin();
            WorkspaceImporter importer;
            Product definingProduct;
            WorkspaceSnapshot snapshot;

            // load version 1
            importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.wsp"),
                                                  myModel);
            myEm.flush();
            definingProduct = importer.getWorkspace()
                                      .getDefiningProduct();
            snapshot = new WorkspaceSnapshot(definingProduct, myEm);
            try (FileOutputStream os = new FileOutputStream(version1File)) {
                snapshot.serializeTo(os);
            }
            assertTrue(snapshot.validate());
        }

        try (Model myModel = new ModelImpl(emf)) {
            EntityManager myEm = myModel.getEntityManager();
            myEm.getTransaction()
                .begin();
            WorkspaceSnapshot.load(myEm, Arrays.asList(version1File.toURI()
                                                                   .toURL()));

            myEm.flush();

            // load version 2

            WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.2.wsp"),
                                                                    myModel);
            myEm.flush();
            Product definingProduct = importer.getWorkspace()
                                              .getDefiningProduct();
            WorkspaceSnapshot snapshot = new WorkspaceSnapshot(definingProduct,
                                                               myEm);
            try (FileOutputStream os = new FileOutputStream(version2File)) {
                snapshot.serializeTo(os);
            }
            assertTrue(snapshot.validate());
        }

        try (Model myModel = new ModelImpl(emf)) {
            EntityManager myEm = myModel.getEntityManager();
            myEm.getTransaction()
                .begin();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new CoREModule());
            WorkspaceSnapshot version1;
            WorkspaceSnapshot version2;
            try (InputStream is = new FileInputStream(version1File);) {
                version1 = mapper.readValue(is, WorkspaceSnapshot.class);
            }

            assertTrue(version1.validate());

            try (InputStream is = new FileInputStream(version2File);) {
                version2 = mapper.readValue(is, WorkspaceSnapshot.class);
            }

            assertTrue(version2.validate());

            WorkspaceSnapshot delta = version2.deltaFrom(version1);
            try (FileOutputStream os = new FileOutputStream(version2_1File)) {
                delta.serializeTo(os);
            }
            assertEquals(7, delta.getRuleforms()
                                 .size());
            assertEquals(7, delta.getFrontier()
                                 .size());
            assertTrue(delta.validate());

            try {
                myModel.getWorkspaceModel()
                       .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
                fail("Should not exist");
            } catch (IllegalArgumentException e) {
                // expected
            }
            version1.retarget(myEm);
            delta.retarget(myEm);
            WorkspaceScope scope = myModel.getWorkspaceModel()
                                          .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
            Agency theDude = (Agency) scope.lookup("TheDude");
            assertNotNull(theDude);
        }

        try (Model myModel = new ModelImpl(emf)) {
            EntityManager myEm = myModel.getEntityManager();
            myEm.getTransaction()
                .begin();

            try {
                myModel.getWorkspaceModel()
                       .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
                fail("Should not exist");
            } catch (IllegalArgumentException e) {
                // expected
            }
            WorkspaceSnapshot.load(myEm, Arrays.asList(version1File.toURI()
                                                                   .toURL(),
                                                       version2_1File.toURI()
                                                                     .toURL()));
            WorkspaceScope scope = myModel.getWorkspaceModel()
                                          .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
            Agency theDude = (Agency) scope.lookup("TheDude");
            assertNotNull(theDude);
        }
    }

    // @Test
    public void testUnload() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.wsp"),
                                                                model);
        em.flush();
        Product definingProduct = importer.getWorkspace()
                                          .getDefiningProduct();

        Thing1 thing1 = model.construct(Thing1.class, "Freddy",
                                        "He always comes back");
        model.getEntityManager()
             .flush();
        model.getEntityManager()
             .clear();
        model.getWorkspaceModel()
             .unload(definingProduct);
        model.getEntityManager()
             .flush();
        model.getEntityManager()
             .clear();
        try {
            assertNull(model.wrap(Thing1.class, thing1.getRuleform()));
            fail("Thing ontology not unloaded");
        } catch (ClassCastException e) {
            // expected
        }
    }
}
