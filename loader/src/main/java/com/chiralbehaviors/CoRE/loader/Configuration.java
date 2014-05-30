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

package com.chiralbehaviors.CoRE.loader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author hhildebrand
 * 
 */
public class Configuration {
    public static String JDBC_URL = "jdbc:postgresql://%s:%s/%s";

    public static Configuration fromYaml(InputStream yaml)
                                                          throws JsonParseException,
                                                          JsonMappingException,
                                                          IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(yaml, Configuration.class);
    }

    /**
     * the name the core database
     * 
     * @parameter
     */
    public String  coreDb       = "core";

    /**
     * the password of the core user
     * 
     * @parameter
     */
    public String  corePassword;

    /**
     * the port of the core database
     * 
     * @parameter
     */
    public String  corePort;

    /**
     * the server host of the core database
     * 
     * @parameter
     */
    public String  coreServer;

    /**
     * the core user name
     * 
     * @parameter
     */
    public String  coreUsername;

    /**
     * the dba database
     * 
     * @parameter
     */
    public String  dbaDb        = "postgres";
    /**
     * the dba password
     * 
     * @parameter
     */
    public String  dbaPassword;
    /**
     * the port of the dba database
     * 
     * @parameter
     */
    public String  dbaPort;
    /**
     * the host name of the dba database
     * 
     * @parameter
     */
    public String  dbaServer;
    /**
     * the dba username
     * 
     * @parameter
     */
    public String  dbaUsername;
    /**
     * drop the database
     * 
     * @parameter
     */
    public boolean dropDatabase = false;

    public Connection getCoreConnection() throws SQLException {
        return DriverManager.getConnection(String.format(JDBC_URL, coreServer,
                                                         corePort, coreDb),
                                           coreUsername, corePassword);
    }

    public Connection getDbaConnection() throws SQLException {
        return DriverManager.getConnection(String.format(JDBC_URL, dbaServer,
                                                         dbaPort, dbaDb),
                                           dbaUsername, dbaPassword);
    }
}
