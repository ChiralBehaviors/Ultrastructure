/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.meta.models;

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;

import com.hellblazer.CoRE.Kernel;
import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.BootstrapLoader;
import com.hellblazer.CoRE.meta.Model;
import static org.junit.Assert.*;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 * 
 * @author hhildebrand
 * 
 */
public class AbstractModelTest {

    protected static Model         model;
    protected static Kernel        kernel;
    protected static EntityManager em;

    @BeforeClass
    public static void initializeModel() throws Exception {
        InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
        assertNotNull("jpa properties missing", is);
        Properties properties = new Properties();
        properties.load(is);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);
        em = emf.createEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        loader.bootstrap();
        em.getTransaction().commit();

        model = new ModelImpl(em);
        kernel = model.getKernel();
    }

    public AbstractModelTest() {
        super();
    }

}