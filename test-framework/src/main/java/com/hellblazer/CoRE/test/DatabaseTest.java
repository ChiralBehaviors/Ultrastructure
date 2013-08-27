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

package com.hellblazer.CoRE.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author hhildebrand
 * 
 */
abstract public class DatabaseTest {
    protected static Connection           connection;
    protected static EntityManager        em;
    private static final String           SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";
    protected static EntityManagerFactory emf;

    @BeforeClass
    public static void setup() throws Exception {

        Properties properties = new Properties();
        properties.load(DatabaseTest.class.getResourceAsStream("/jpa.properties"));
        emf = Persistence.createEntityManagerFactory("CoRE", properties);
        em = emf.createEntityManager();
        connection = em.unwrap(Connection.class);
    }

    protected static void alterAllTriggers(boolean enable) throws SQLException {
        ResultSet r = connection.createStatement().executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }

    @Before
    public void clear() throws SQLException {
        alterAllTriggers(false);
        ResultSet r = connection.createStatement().executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("DELETE FROM %s", table);
            connection.createStatement().execute(query);
        }
        r.close();
        alterAllTriggers(true);
    }

    /**
     * Initiates a database transaction.
     */
    protected void beginTransaction() {
        em.getTransaction().begin();
    }

    /**
     * Commits the current transaction, if it is still active.
     */
    protected final void commitTransaction() {
        em.getTransaction().commit();
    }
}
