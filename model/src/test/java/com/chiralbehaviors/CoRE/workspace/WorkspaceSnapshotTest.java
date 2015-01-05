/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.workspace;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshotTest extends DatabaseTest {

    @Test
    public void testSerializeWorkspaceSnapshot() throws Exception {
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
        assertEquals(1, retrieved.getAuths().size());
        assertEquals(auth, retrieved.getAuths().get(0));
        assertEquals(pseudoScientist, retrieved.getAuths().get(0).getEntity());
    }
}
