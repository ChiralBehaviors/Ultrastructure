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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.chiralbehaviors.CoRE.RecordsFactory;

/**
 * @author hhildebrand
 * 
 */
abstract public class DatabaseTest {
    private static boolean          initialized = false;
    protected static Connection     connection;
    protected static DSLContext     create;
    protected static RecordsFactory RECORDS;

    @AfterClass
    public static void afterClass() throws DataAccessException, SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .rollback();
        ;
        create.close();
    }

    @BeforeClass
    public static void setup() throws Exception {
        Properties properties = new Properties();
        properties.load(DatabaseTest.class.getResourceAsStream("/db.properties"));
        if (!initialized) {
            initialized = true;
            Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                          (String) properties.get("user"),
                                                          (String) properties.get("password"));
            conn.setAutoCommit(false);

            create = PostgresDSL.using(conn);
            RECORDS = new RecordsFactory() {

                @Override
                public DSLContext create() {
                    return create;
                }
            };
        }
    }

    protected static final void commitTransaction() throws DataAccessException,
                                                    SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .commit();
    }

    @After
    public void after() throws DataAccessException, SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .rollback();
    }
}
