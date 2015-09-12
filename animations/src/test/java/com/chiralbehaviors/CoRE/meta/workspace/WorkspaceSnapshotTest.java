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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshotTest extends AbstractModelTest {

    @Test
    public void testSerializeWorkspaceSnapshot() throws Exception {
        Agency pseudoScientist = new Agency("Behold the Pseudo Scientist!");
        pseudoScientist.setUpdatedBy(pseudoScientist);
        Product definingProduct = new Product("zee product", pseudoScientist);
        WorkspaceAuthorization auth = new WorkspaceAuthorization(definingProduct,
                                                                 definingProduct,
                                                                 pseudoScientist,
                                                                 em);
        auth = new WorkspaceAuthorization(pseudoScientist, definingProduct,
                                          pseudoScientist, em);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(definingProduct,
                                                           Arrays.asList(auth),
                                                           model.getEntityManager());
        snapshot.retarget(em);
        em.flush();
        WorkspaceSnapshot retrieved = new WorkspaceSnapshot(definingProduct,
                                                            em);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        mapper.writeValue(os, retrieved);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        WorkspaceSnapshot deserialized = mapper.readValue(is,
                                                          WorkspaceSnapshot.class);
        assertEquals(2, deserialized.getRuleforms()
                                    .size());
        assertEquals(pseudoScientist, deserialized.getRuleforms()
                                                  .get(0));

    }

    @Test
    public void testWorkspaceSnapshot() {
        Agency pseudoScientist = new Agency("Behold the Pseudo Scientist!");
        pseudoScientist.setUpdatedBy(pseudoScientist);
        em.persist(pseudoScientist);
        Product definingProduct = new Product("zee product", pseudoScientist);
        em.persist(definingProduct);
        new WorkspaceAuthorization(pseudoScientist, definingProduct,
                                   pseudoScientist, em);
        new WorkspaceAuthorization(definingProduct, definingProduct,
                                   pseudoScientist, em);
        Product aLeak = new Product("Snowden", kernel.getCore());
        em.persist(aLeak);
        new WorkspaceAuthorization(aLeak, definingProduct, pseudoScientist, em);
        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(definingProduct,
                                                           model.getEntityManager());
        em.getTransaction()
          .rollback();
        em.getTransaction()
          .begin();
        assertEquals("Did not find the leak!", 1, snapshot.getFrontier()
                                                          .size());
        Ruleform mole = snapshot.getFrontier()
                                .get(0);
        assertEquals("Imposter!", kernel.getCore(), mole);
        em.getTransaction()
          .rollback();
        em.getTransaction()
          .begin();
        List<WorkspaceAuthorization> retrieved = WorkspaceSnapshot.getAuthorizations(definingProduct,
                                                                                     em);
        assertEquals(3, retrieved.size());
        for (WorkspaceAuthorization a : retrieved) {
            Ruleform ruleform = a.getRuleform(em);
            if (ruleform instanceof Agency) {
                assertEquals(pseudoScientist, ruleform);
            } else if (!ruleform.equals(definingProduct)) {
                assertEquals(aLeak, ruleform);
                assertEquals("compromised!", kernel.getCore()
                                                   .getName(),
                             ruleform.getUpdatedBy()
                                     .getName());
            }
        }
    }

    @Test
    public void testDeltaGeneration() throws Exception {
        File version1File = new File("target/version.1.json");
        File version2File = new File("target/version.2.json");
        Model myModel = new ModelImpl(emf);
        EntityManager myEm = myModel.getEntityManager();
        myEm.getTransaction()
            .begin();
        WorkspaceImporter importer;
        Product definingProduct;
        WorkspaceSnapshot snapshot;
        try {

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
        } finally {
            myEm.getTransaction()
                .rollback();
            myEm.close();
        }

        myModel = new ModelImpl(emf);
        myEm = myModel.getEntityManager();
        myEm.getTransaction()
            .begin();
        try {
            WorkspaceSnapshot.load(myEm, Arrays.asList(version1File.toURI()
                                                                   .toURL()));

            myEm.flush();

            // load version 2

            importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.2.wsp"),
                                                  myModel);
            myEm.flush();
            definingProduct = importer.getWorkspace()
                                      .getDefiningProduct();
            snapshot = new WorkspaceSnapshot(definingProduct, myEm);
            try (FileOutputStream os = new FileOutputStream(version2File)) {
                snapshot.serializeTo(os);
            }
        } finally {
            myEm.getTransaction()
                .rollback();
            myEm.close();
        }

        myModel = new ModelImpl(emf);
        myEm = myModel.getEntityManager();
        myEm.getTransaction()
            .begin();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new CoREModule());
            WorkspaceSnapshot version1;
            WorkspaceSnapshot version2;
            try (InputStream is = new FileInputStream(version1File);) {
                version1 = mapper.readValue(is, WorkspaceSnapshot.class);
            }
            try (InputStream is = new FileInputStream(version2File);) {
                version2 = mapper.readValue(is, WorkspaceSnapshot.class);
            }
            WorkspaceSnapshot delta = version2.deltaFrom(version1);
            try (FileOutputStream os = new FileOutputStream(new File("target/version.2-1.json"))) {
                delta.serializeTo(os);
            }
            assertEquals(7, delta.getRuleforms()
                                 .size());
            assertEquals(7, delta.getFrontier()
                                 .size());
        } finally {
            myEm.close();
        }
    }
}
