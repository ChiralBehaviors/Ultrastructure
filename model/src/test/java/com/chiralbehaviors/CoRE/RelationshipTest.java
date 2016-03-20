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

import static com.chiralbehaviors.CoRE.RecordsFactory.RECORDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.test.DatabaseTest;;

/**
 * @author hhildebrand
 *
 */

public class RelationshipTest extends DatabaseTest {

    @Before
    public void initData() {
        Agency core = RECORDS.newAgency(create, "CoREd");
        core.setUpdatedBy(core.getId());
        core.insert();

        Relationship massList = RECORDS.newRelationship(create, "mass-list",
                                                        "A is a member of the mass list B",
                                                        core);

        Relationship massListOf = RECORDS.newRelationship(create,
                                                          "mass-list-of",
                                                          "A is a mass list that has B as a member",
                                                          core, massList);
        massList.insert();
        massListOf.insert();
    }

    @Test
    public void setInverseTest() {
        Agency core = RECORDS.newAgency(create, "CoREd");
        core.setUpdatedBy(core.getId());
        core.insert();
        Relationship r = RECORDS.newRelationship(create, "Foo", core);

        Relationship i = RECORDS.newRelationship(create, "Bar", core, r);
        r.insert();
        i.insert();

        assertNotNull(r.getInverse());
        assertEquals(i.getId(), r.getInverse());

        assertNotNull(i.getInverse());
        assertEquals(r.getId(), i.getInverse());
    }
}
