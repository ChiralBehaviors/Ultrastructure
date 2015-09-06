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

package com.chiralbehaviors.CoRE.security;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */
public class AgencyGroupingTest extends DatabaseTest {

    @Test
    public void testAgencyGroupings() {
        Agency agency = new Agency("prime", "prime");
        agency.setUpdatedBy(agency);

        em.persist(agency);
        em.flush();

        AgencyAgencyGrouping aag = new AgencyAgencyGrouping();
        aag.setUpdatedBy(agency);
        aag.setEntity(agency);
        aag.setGroupingAgency(agency);

        em.persist(aag);

        em.flush();

    }
}
