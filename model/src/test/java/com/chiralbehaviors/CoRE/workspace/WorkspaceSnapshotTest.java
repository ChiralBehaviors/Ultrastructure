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

package com.chiralbehaviors.CoRE.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshotTest extends DatabaseTest {

    private Agency core;

    @Before
    public void setupCore() {
        core = new Agency("Ye CoRE");
        core.setUpdatedBy(core);
        em.persist(core);
    }

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
                                                           em);
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
        assertTrue(deserialized.getRuleforms()
                               .contains(pseudoScientist));
        assertTrue(deserialized.getRuleforms()
                               .contains(definingProduct));

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
        Product aLeak = new Product("Snowden", core);
        em.persist(aLeak);
        new WorkspaceAuthorization(aLeak, definingProduct, pseudoScientist, em);
        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(definingProduct, em);
        em.getTransaction()
          .rollback();
        em.getTransaction()
          .begin();
        assertEquals("Did not find the leak!", 1, snapshot.getFrontier()
                                                          .size());
        Ruleform mole = snapshot.getFrontier()
                                .get(0);
        assertEquals("Imposter!", core, mole);
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
                assertEquals("compromised!", core.getName(),
                             ruleform.getUpdatedBy()
                                     .getName());
            }
        }
    }
}
