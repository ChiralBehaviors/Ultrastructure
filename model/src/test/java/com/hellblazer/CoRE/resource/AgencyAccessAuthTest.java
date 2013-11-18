/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.resource;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.AgencyProductAccessAuthorization;
import com.hellblazer.CoRE.kernel.Bootstrap;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hparry
 * 
 */
public class AgencyAccessAuthTest extends DatabaseTest {

    private static Kernel kernel;

    @Before
    public void initKernel() throws SQLException {
        beginTransaction();
        Bootstrap bt = new Bootstrap(connection);
        bt.clear();
        bt.bootstrap();
        commitTransaction();
        kernel = new KernelImpl(em);
    }

    @Test
    public void testClassHierarchy() {
        beginTransaction();
        AgencyProductAccessAuthorization auth = new AgencyProductAccessAuthorization(
                                                                                     kernel.getAnyAgency(),
                                                                                     kernel.getAnyRelationship(),
                                                                                     kernel.getAnyProduct(),
                                                                                     kernel.getCoreUser());
        em.persist(auth);
        commitTransaction();

    }

}
