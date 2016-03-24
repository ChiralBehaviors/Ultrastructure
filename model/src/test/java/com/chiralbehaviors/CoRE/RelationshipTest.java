/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.test.DatabaseTest;;

/**
 * @author hhildebrand
 *
 */

public class RelationshipTest extends DatabaseTest {

    @Before
    public void initData() {
        Relationship massList = RECORDS.newRelationship("mass-list",
                                                        "A is a member of the mass list B");

        Relationship massListOf = RECORDS.newRelationship("mass-list-of",
                                                          "A is a mass list that has B as a member",
                                                          massList);
        massList.insert();
        massListOf.insert();
    }

    @Test
    public void setInverseTest() {
        Relationship r = RECORDS.newRelationship("Foo");

        Relationship i = RECORDS.newRelationship("Bar", r);
        r.insert();
        i.insert();

        assertNotNull(r.getInverse());
        assertEquals(i.getId(), r.getInverse());

        assertNotNull(i.getInverse());
        assertEquals(r.getId(), i.getInverse());
    }
}
