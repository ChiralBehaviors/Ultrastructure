/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
import javax.persistence.PersistenceException;

import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 *
 * @author hhildebrand
 *
 */
public class AbstractModelTest {
    private static final String           SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    protected static EntityManager        em;

    protected static EntityManagerFactory emf;

    protected static Kernel               kernel;

    protected static Model                model;

    @AfterClass
    public static void afterClass() {
        if (em != null && em.getTransaction().isActive()) {
            try {
                em.getTransaction().rollback();
                em.clear();
                em.close();
            } catch (Throwable e) {
                LoggerFactory.getLogger(AbstractModelTest.class).warn(String.format("Had a bit of trouble cleaning up after %s",
                                                                                    e.getMessage()),
                                                                      e);
            }
        }
    }

    @BeforeClass
    public static void initializeDatabase() throws IOException, SQLException {
        if (em != null && em.isOpen()) {
            em.close();
        }
        em = getEntityManager();
        KernelUtil.clearAndLoadKernel(em);
        em.close();
        model = new ModelImpl(emf);
        kernel = model.getKernel();
        em = model.getEntityManager();
    }

    private static EntityManager getEntityManager() throws IOException {
        if (emf == null) {
            InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
            assertNotNull("jpa properties missing", is);
            Properties properties = new Properties();
            properties.load(is);
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
        if (em.getTransaction().isActive()) {
            try {
                em.getTransaction().rollback();
            } catch (PersistenceException e) {
                LoggerFactory.getLogger(AbstractModelTest.class).warn(String.format("Bit of a problem cleaning up"),
                                                                      e);
            }
        }
    }

    protected void alterTriggers(boolean enable) throws SQLException {
        Connection connection = em.unwrap(SessionImpl.class).connection();
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
