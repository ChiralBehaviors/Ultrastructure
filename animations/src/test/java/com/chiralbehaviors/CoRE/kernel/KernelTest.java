/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */
public class KernelTest extends DatabaseTest {

    @Test
    public void testDetachedCaching() throws IOException, SQLException {
        em.getTransaction().rollback();
        Kernel kernel = KernelUtil.clearAndLoadKernel(em);
        beginTransaction();
        Agency core = kernel.getCore();
        assertNotNull(core);
        assertEquals("CoRE", core.getName());
        Agency test = new Agency("My test agency", core);
        em.persist(test);
        commitTransaction();
        EntityManager newEm = emf.createEntityManager();
        newEm.getTransaction().begin();
        Agency newTest = new Agency("my other test agency", core);
        newEm.persist(newTest);
        newEm.getTransaction().commit();
        beginTransaction();
        Agency newerTest = new Agency("the final frontier", core);
        em.persist(newerTest);
        commitTransaction();
    }
}
