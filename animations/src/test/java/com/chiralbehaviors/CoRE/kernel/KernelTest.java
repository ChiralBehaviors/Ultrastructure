/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.kernel;

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
        Kernel kernel = KernelImpl.clearAndLoadKernel(em);
        beginTransaction();
        Agency core = kernel.getCore();
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
