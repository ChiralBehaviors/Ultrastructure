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

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.test.DatabaseTest;;

/**
 * @author hhildebrand
 *
 */

public class PropTest extends DatabaseTest {

    @Test
    public void setPropertiesTest() { 
        Relationship r = RECORDS.newRelationship("Foo");

        Relationship i = RECORDS.newRelationship("Bar", r);
        r.insert();
        i.insert();
        Agency a = RECORDS.newAgency(); 
        a.insert();
        FacetRecord f = RECORDS.newFacet(r, a);
        f.insert();
        FacetPropertyRecord p = RECORDS.newFacetProperty();
        p.setFacet(f.getId());
        p.setExistential(a.getId());
        p.insert();
    }
}
