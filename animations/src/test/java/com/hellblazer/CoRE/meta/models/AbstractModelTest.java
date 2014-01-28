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

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;

import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.BootstrapLoader;
import com.hellblazer.CoRE.meta.Model;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 * 
 * @author hhildebrand
 * 
 */
public class AbstractModelTest {

    protected Model         model;
    protected Kernel        kernel;
    protected EntityManager em;

    public AbstractModelTest() {
        super();
    }

    @Before
    public void initialize() throws Exception {
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

}