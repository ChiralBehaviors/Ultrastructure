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
import com.hellblazer.CoRE.product.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.product.ProductNetwork;
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
		ProductNetwork net = new ProductNetwork(workspace, workspaceOf, p1, core);
		em.persist(net);
		ProductAgencyAccessAuthorization coreAuth = new ProductAgencyAccessAuthorization(workspace, workspaceOf, core, core);
		ProductLocationAccessAuthorization locAuth = new ProductLocationAccessAuthorization(workspace, workspaceOf, kernel.getAnyLocation(), core);
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
