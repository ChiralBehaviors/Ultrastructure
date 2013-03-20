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

package com.hellblazer.CoRE.kernel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.junit.AfterClass;

/**
 * Test disabled in schema module to allow updating of animation proceedures
 * without destroying database state
 * 
 * @author hhildebrand
 * 
 */
public class TestBootstrap {

    @AfterClass
    public static void clean() throws Exception {
        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        props.load(TestBootstrap.class.getResourceAsStream("/load-sql.properties"));
        Connection connection = DriverManager.getConnection(props.getProperty("url"),
                                                            props.getProperty("username"),
                                                            props.getProperty("password"));
        connection.setAutoCommit(false);
        Bootstrap bootstrap = new Bootstrap(connection);
        bootstrap.clear();
        connection.commit();
    }

    // @Test
    public void testBootstrap() throws Exception {
        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/load-sql.properties"));
        Connection connection = DriverManager.getConnection(props.getProperty("url"),
                                                            props.getProperty("username"),
                                                            props.getProperty("password"));
        connection.setAutoCommit(false);
        Bootstrap bootstrap = new Bootstrap(connection);
        bootstrap.bootstrap();
        connection.commit();
    }
}
