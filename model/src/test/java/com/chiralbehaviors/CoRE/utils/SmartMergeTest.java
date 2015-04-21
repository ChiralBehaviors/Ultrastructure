/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;
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
        em.getTransaction().commit();
        em.getTransaction().begin();

        Relationship massList = new Relationship(
                                                 "mass-list",
                                                 "A is a member of the mass list B",
                                                 core);

        Relationship massListOf = new Relationship(
                                                   "mass-list-of",
                                                   "A is a mass list that has B as a member",
                                                   core, massList);

        Relationship merged = Util.smartMerge(em, massList);
        em.getTransaction().commit();
        assertEquals(massList, merged);
        assertEquals(massListOf, merged.getInverse());
        assertEquals(core, merged.getUpdatedBy());
    }
}