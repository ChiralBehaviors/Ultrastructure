/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class AttributeSearchTest extends DatabaseTest {

    @Before
    public void initData() {
        beginTransaction();

        Agency core = new Agency("core");
        core.setUpdatedBy(core);
        em.persist(core);

        Attribute a1 = new Attribute("Attribute", core);
        a1.setValueType(ValueType.INTEGER);
        em.persist(a1);

        Attribute a2 = new Attribute("Wooziness", core);
        a2.setValueType(ValueType.INTEGER);
        em.persist(a2);

        commitTransaction();
    }

    @Test
    public void searchByName() throws Exception {
        searchByName("Attribute", true);
        searchByName("Puppy Flammability", false);

    }

    public void searchByName(String name, boolean shouldFind) throws Exception {
        Model model = new ModelImpl(em, null);

        Attribute a = model.find(name, Attribute.class);
        if (shouldFind) {
            assertNotNull(a);
            assertEquals(name, a.getName());
        } else {
            // we weren't expecting to find anything
            assertNull(a);
        }
    }
}
