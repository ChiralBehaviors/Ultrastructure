/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.authorizations;

import static junit.framework.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.hellblazer.CoRE.authorization.WorkspaceRelationshipAuthorization;
import com.hellblazer.CoRE.kernel.Bootstrap;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class WorkspaceRelationshipAuthorizationTest extends DatabaseTest {

    @Test
    public void testSimple() throws SQLException {
        Bootstrap bootstrap = new Bootstrap(connection);
        beginTransaction();
        bootstrap.clear();
        bootstrap.bootstrap();
        commitTransaction();

        Kernel kernel = new KernelImpl(em);

        beginTransaction();
        WorkspaceRelationshipAuthorization auth = new WorkspaceRelationshipAuthorization(
                                                                                         kernel.getCore());
        auth.setProduct(kernel.getAnyProduct());
        auth.setRelationship(kernel.getAnyRelationship());
        em.persist(auth);
        commitTransaction();
        em.clear();
        auth = em.find(WorkspaceRelationshipAuthorization.class, auth.getId());
        assertEquals(kernel.getAnyRelationship(), auth.getRelationship());
    }
}
