/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */
public class SmartMergeTest extends DatabaseTest {

    @Test
    public void testCircularity() {
        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);
        em.flush();

        Relationship massList = new Relationship("mass-list",
                                                 "A is a member of the mass list B",
                                                 core);

        Relationship massListOf = new Relationship("mass-list-of",
                                                   "A is a mass list that has B as a member",
                                                   core, massList);

        Relationship merged = Ruleform.smartMerge(em, massList,
                                              new HashMap<>(1024));
        assertEquals(massList, merged);
        assertEquals(massListOf, merged.getInverse());
        assertEquals(core, merged.getUpdatedBy());
    }
}
