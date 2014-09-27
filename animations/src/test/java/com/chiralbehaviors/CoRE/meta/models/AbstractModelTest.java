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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject;
import com.chiralbehaviors.CoRE.meta.BootstrapLoader;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 *
 * @author hhildebrand
 *
 */
public class AbstractModelTest {
    @AfterClass
    public static void afterClass() {
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            em.clear();
            em.close();
        }
    }

    @BeforeClass
    public static void initializeDatabase() throws IOException, SQLException {
        if (em != null) {
            em.close();
        }
        em = getEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        loader.bootstrap();
        em.getTransaction().commit();
        model = new ModelImpl(em);
        kernel = model.getKernel();
    }

    private static EntityManager getEntityManager() throws IOException {
        InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
        assertNotNull("jpa properties missing", is);
        Properties properties = new Properties();
        properties.load(is);
        emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                     properties);
        EntityManager em = emf.createEntityManager();
        return em;
    }

    private static final String           SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    protected static Model                model;

    protected static Kernel               kernel;
    protected static EntityManager        em;

    protected static EntityManagerFactory emf;

    public AbstractModelTest() {
        super();
    }

    @After
    public void after() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            em.clear();
        }
    }

    protected void alterTriggers(boolean enable) throws SQLException {
        Connection connection = em.unwrap(Connection.class);
        for (String table : new String[] { "ruleform.agency",
                "ruleform.product", "ruleform.location" }) {
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        ResultSet r = connection.createStatement().executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }

}