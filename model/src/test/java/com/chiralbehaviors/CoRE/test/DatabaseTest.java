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

package com.chiralbehaviors.CoRE.test;

import java.sql.Connection;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author hhildebrand
 * 
 */
abstract public class DatabaseTest {
    protected static Connection           connection;
    protected static EntityManager        em;
    protected static EntityManagerFactory emf;

    @AfterClass
    public static void afterClass() {
        if (em != null && em.getTransaction()
                            .isActive()) {
            em.getTransaction()
              .rollback();
            em.clear();
            em.close();
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
        Properties properties = new Properties();
        properties.load(DatabaseTest.class.getResourceAsStream("/jpa.properties"));
        emf = Persistence.createEntityManagerFactory("CoRE", properties);
        em = emf.createEntityManager();
        em.getTransaction()
          .begin();
    }

    /**
     * Initiates a database transaction.
     */
    protected static void beginTransaction() {
        if (!em.getTransaction()
               .isActive()) {
            em.getTransaction()
              .begin();
        }
    }

    /**
     * Commits the current transaction, if it is still active.
     */
    protected static final void commitTransaction() {
        if (em.getTransaction()
              .isActive()) {
            em.getTransaction()
              .commit();
        }
    }

    @After
    public void after() {
        if (em.getTransaction()
              .isActive()) {
            em.getTransaction()
              .rollback();
        }
        em.clear();
    }

    @Before
    public void before() {
        beginTransaction();
        em.clear();
    }
}
