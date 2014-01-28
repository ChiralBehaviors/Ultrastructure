/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
package com.hellblazer.CoRE.resource;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.access.AgencyProductAccessAuthorization;
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
