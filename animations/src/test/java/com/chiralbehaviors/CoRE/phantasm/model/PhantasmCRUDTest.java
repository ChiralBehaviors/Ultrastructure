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

package com.chiralbehaviors.CoRE.phantasm.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.JsonImporter;
import com.chiralbehaviors.CoRE.phantasm.RbacTest;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.Aspect;

/**
 * @author hhildebrand
 *
 */
public class PhantasmCRUDTest extends AbstractModelTest {

    @Before
    public void loadThingOntology() throws Exception {
        JsonImporter.manifest(RbacTest.class.getResourceAsStream("/thing.wsp"),
                                   model);
    }

    @Test
    public void testIt() throws Exception {
        PhantasmCRUD crud = new PhantasmCRUD(model);
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(model.getKernel()
                                                           .getIsA(),
                                                      model.getKernel()
                                                           .getCoreUser());
        assertNotNull(facet);
        Aspect aspect = new Aspect(model.create(), facet);
        Agency test = model.records()
                           .newAgency("foo", "bar");
        assertNotNull(crud.apply(aspect, test, e -> e));
        crud.cast(test, aspect);
        ExistentialRuleform instance = crud.createInstance(aspect, "bar", "foo",
                                                           e -> {
                                                           });
        assertNotNull(instance);
        assertEquals(instance.getId(), crud.lookup(instance.getId())
                                           .getId());
        assertEquals(1, crud.lookupList(Collections.singletonList(instance.getId()))
                            .size());
        crud.remove(aspect, instance, false);
        try {
            crud.cast(instance, aspect);
            fail("remove did not succeed");
        } catch (ClassCastException e) {
            // expected
        }
        crud.setName(instance, "testy");
        crud.setDescription(instance, "a test");
        assertEquals(2, crud.getInstances(aspect)
                            .size());
    }
}
