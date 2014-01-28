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
package com.hellblazer.CoRE.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.kernel.Bootstrap;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hparry
 * 
 */
public class WorkspaceTest extends DatabaseTest {

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
    public void testLoadWorkspace() {
        beginTransaction();
        Product workspace = kernel.getWorkspace();
        Relationship workspaceOf = kernel.getWorkspaceOf();
        Agency core = kernel.getCore();
        Product p1 = new Product("MyProduct", core);
        em.persist(p1);
        ProductNetwork net = new ProductNetwork(workspace, workspaceOf, p1,
                                                core);
        em.persist(net);
        ProductAgencyAccessAuthorization coreAuth = new ProductAgencyAccessAuthorization(
                                                                                         workspace,
                                                                                         workspaceOf,
                                                                                         core,
                                                                                         core);
        ProductLocationAccessAuthorization locAuth = new ProductLocationAccessAuthorization(
                                                                                            workspace,
                                                                                            workspaceOf,
                                                                                            kernel.getAnyLocation(),
                                                                                            core);
        em.persist(coreAuth);
        em.persist(locAuth);
        commitTransaction();

        Workspace w = Workspace.loadWorkspace(workspace, workspaceOf, em);
        assertEquals(2, w.getProducts().size());
        assertTrue(w.getProducts().contains(p1));
        assertEquals(2, w.getAccessAuths().size());
        assertTrue(w.getAccessAuths().contains(coreAuth));
    }

}
