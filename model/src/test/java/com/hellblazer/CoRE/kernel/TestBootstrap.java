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
