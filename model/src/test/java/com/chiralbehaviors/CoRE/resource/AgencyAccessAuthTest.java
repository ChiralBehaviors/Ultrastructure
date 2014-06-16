/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.resource;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.access.AgencyProductAccessAuthorization;
import com.chiralbehaviors.CoRE.kernel.Bootstrap;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hparry
 * 
 */
public class AgencyAccessAuthTest extends DatabaseTest {

    private static Kernel kernel;

    @Before
    public void initKernel() throws SQLException {
        Bootstrap bt = new Bootstrap(connection);
        bt.clear();
        bt.bootstrap();
        kernel = new KernelImpl(em);
        em.flush();
        em.clear();
    }

    @Test
    public void testClassHierarchy() {
        AgencyProductAccessAuthorization auth = new AgencyProductAccessAuthorization(
                                                                                     kernel.getAnyAgency(),
                                                                                     kernel.getAnyRelationship(),
                                                                                     kernel.getAnyProduct(),
                                                                                     kernel.getCoreUser());
        em.persist(auth);
        em.flush();
    }

}
