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
package com.hellblazer.CoRE.attribute.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.test.DatabaseTestContext;

/**
 * @author hhildebrand
 * 
 */
public class AttributeDAOTest extends DatabaseTestContext {

    @Test
    public void searchByName() throws Exception {
        searchByName("Attribute", true);
        searchByName("Puppy Flammability", false);

    }

    public void searchByName(String name, boolean shouldFind) throws Exception {
        beginTransaction();

        Model model = new ModelImpl(em, null);

        Attribute a = model.find(name, Attribute.class);
        if (shouldFind) {
            assertNotNull(a);
            assertEquals(name, a.getName());
        } else {
            // we weren't expecting to find anything
            assertNull(a);
        }

        commitTransaction();
    }

    @Override
    protected void prepareSettings() {
        dataSetLocation = "AttributeDAOTestData.xml";
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
    }

    @Override
    protected void setSequences() throws Exception {
        setSequenceWithLastCalled("resource_id_seq", 1);
        setSequenceWithLastCalled("attribute_id_seq", 1);
    }

}
