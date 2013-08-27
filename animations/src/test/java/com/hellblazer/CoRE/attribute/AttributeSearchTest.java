/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class AttributeSearchTest extends DatabaseTest {

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
    
    @Before
    public void initData() {
        beginTransaction();

        Resource core = new Resource("core");
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
}
