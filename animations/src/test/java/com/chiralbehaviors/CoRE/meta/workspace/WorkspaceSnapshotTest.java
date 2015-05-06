/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
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
        em.getTransaction().begin();
        Agency pseudoScientist = new Agency("Behold the Pseudo Scientist!");
        pseudoScientist.setUpdatedBy(pseudoScientist);
        Product definingProduct = new Product("zee product", pseudoScientist);
        WorkspaceAuthorization auth = new WorkspaceAuthorization(
                                                                 pseudoScientist,
                                                                 definingProduct,
                                                                 pseudoScientist);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(Arrays.asList(auth));
        snapshot.retarget(em);
        em.getTransaction().commit();
        WorkspaceSnapshot retrieved = new WorkspaceSnapshot(definingProduct, em);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        mapper.writeValue(os, retrieved);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        WorkspaceSnapshot deserialized = mapper.readValue(is,
                                                          WorkspaceSnapshot.class);
        assertEquals(1, deserialized.getAuths().size());
        assertEquals(auth, deserialized.getAuths().get(0));

    }

    @Test
    public void testWorkspaceSnapshot() {
        em.getTransaction().begin();
        Agency pseudoScientist = new Agency("Behold the Pseudo Scientist!");
        pseudoScientist.setUpdatedBy(pseudoScientist);
        Product definingProduct = new Product("zee product", pseudoScientist);
        WorkspaceAuthorization auth = new WorkspaceAuthorization(
                                                                 pseudoScientist,
                                                                 definingProduct,
                                                                 pseudoScientist);
        Product aLeak = new Product("Snowden", kernel.getCore());
        WorkspaceAuthorization auth2 = new WorkspaceAuthorization(
                                                                  aLeak,
                                                                  definingProduct,
                                                                  pseudoScientist);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(Arrays.asList(auth,
                                                                         auth2));
        assertEquals("Did not find the leak!", 1, snapshot.getFrontier().size());
        assertEquals("Imposter!", kernel.getCore(),
                     snapshot.getFrontier().get(0));
        snapshot.retarget(em);
        em.getTransaction().commit();
        WorkspaceSnapshot retrieved = new WorkspaceSnapshot(definingProduct, em);
        assertEquals(2, retrieved.getAuths().size());
        for (WorkspaceAuthorization a : retrieved.getAuths()) {
            if (a.getAgency() != null) {
                assertEquals(pseudoScientist, a.getRuleform());
            } else {
                assertEquals(aLeak, a.getRuleform());
            }
        }
    }
}
