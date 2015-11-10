/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.AdditionalAnswers;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 *
 * @author hparry, hhildebrand
 *
 */
public class AbstractModelTest {
    public static final String            TARGET_CLASSES_THING_1_2_JSON = "target/test-classes/thing.1.2.json";
    public static final String            TARGET_CLASSES_THING_1_JSON   = "target/test-classes/thing.1.json";
    public static final String            TARGET_CLASSES_THING_2_JSON   = "target/test-classes/thing.2.json";
    public static final String            TARGET_THINGS_JSON            = "target/things.json";
    public static final String            THING_URI                     = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm";

    private static boolean                initialized                   = false;
    protected static EntityManager        em;
    protected static EntityManagerFactory emf;
    protected static Kernel               kernel;
    protected static Model                model;

    @AfterClass
    public static void afterClass() {
        if (em != null) {
            if (em.getTransaction()
                  .isActive()) {
                try {
                    em.getTransaction()
                      .rollback();
                    em.close();
                } catch (Throwable e) {
                    LoggerFactory.getLogger(AbstractModelTest.class)
                                 .warn(String.format("Had a bit of trouble cleaning up after %s",
                                                     e.getMessage()),
                                       e);
                }
            }
        }
    }

    @BeforeClass
    public static void initializeDatabase() throws IOException, SQLException,
                                            InstantiationException {
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
        if (initialized == false) {
            initialized = true;
            em = getEntityManager();
            KernelUtil.clearAndLoadKernel(em);
            em.close();
            try (Model myModel = new ModelImpl(emf)) {
                myModel.getEntityManager()
                       .getTransaction()
                       .begin();
                KernelUtil.initializeInstance(myModel,
                                              "Abstract Model Test CoRE Instance",
                                              "CoRE instance for an Abstract Model Test");
                myModel.getEntityManager()
                       .getTransaction()
                       .commit();
            }
        }
        model = new ModelImpl(emf);
        kernel = model.getKernel();
        em = model.getEntityManager();
    }

    public static EntityManagerFactory mockedEmf() {
        EntityManagerFactory mockedEmf = mock(EntityManagerFactory.class);
        EntityManager mockedEm = mock(EntityManager.class,
                                      AdditionalAnswers.delegatesTo(em));
        EntityTransaction mockedTxn = mock(EntityTransaction.class);
        doReturn(mockedTxn).when(mockedEm)
                           .getTransaction();
        doNothing().when(mockedEm)
                   .close();
        when(mockedEmf.createEntityManager()).thenReturn(mockedEm);
        return mockedEmf;
    }

    private static EntityManager getEntityManager() throws IOException,
                                                    SQLException {
        if (emf == null) {
            InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
            assertNotNull("jpa properties missing", is);
            Properties properties = new Properties();
            properties.load(is);

            // configuration by convention
            properties.put("hibernate.dialect",
                           "com.chiralbehaviors.CoRE.attribute.json.JsonPostgreSqlDialect");

            System.out.println(String.format("Database URL: %s",
                                             properties.getProperty("javax.persistence.jdbc.url")));
            emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                         properties);
        }
        EntityManager em = emf.createEntityManager();
        return em;
    }

    public AbstractModelTest() {
        super();
    }

    @After
    public void after() {
        if (em.getTransaction()
              .isActive()) {
            try {
                em.getTransaction()
                  .rollback();
            } catch (PersistenceException e) {
                LoggerFactory.getLogger(AbstractModelTest.class)
                             .warn(String.format("Bit of a problem cleaning up"),
                                   e);
            }
        }
        ModelImpl.clearPhantasmCache();
    }

    @Before
    public void beginTxnBefore() {
        if (em.getTransaction()
              .isActive()) {
            em.getTransaction()
              .rollback();
        }
        em.getTransaction()
          .begin();
    }
}
