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

import static com.chiralbehaviors.CoRE.RecordsFactory.RECORDS;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
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
        core = RECORDS.newAgency(create);
        core.setName("Ye CoRE");
        core.setUpdatedBy(core.getId());
        create.insertInto(EXISTENTIAL)
              .set(core);
    }

    @Test
    public void testSerializeWorkspaceSnapshot() throws Exception {
        Agency pseudoScientist = RECORDS.newAgency(create,
                                                   "Behold the Pseudo Scientist!",
                                                   core);
        pseudoScientist.insert();
        Product definingProduct = RECORDS.newProduct(create, "zee product",
                                                     pseudoScientist);
        definingProduct.insert();
        WorkspaceAuthorizationRecord auth;
        auth = RECORDS.newWorkspaceAuthorization(create, definingProduct,
                                                 definingProduct,
                                                 pseudoScientist);
        auth.insert();
        auth = RECORDS.newWorkspaceAuthorization(create, definingProduct,
                                                 pseudoScientist,
                                                 pseudoScientist);
        auth.insert();

        WorkspaceSnapshot retrieved = new WorkspaceSnapshot(definingProduct,
                                                            create);
        assertEquals(4, retrieved.getRecords()
                                 .size());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        mapper.writeValue(os, retrieved);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        WorkspaceSnapshot deserialized = mapper.readValue(is,
                                                          WorkspaceSnapshot.class);
        assertEquals(4, deserialized.getRecords()
                                    .size());
        assertTrue(deserialized.getRecords()
                               .contains(pseudoScientist));
        assertTrue(deserialized.getRecords()
                               .contains(definingProduct));

    }
}
