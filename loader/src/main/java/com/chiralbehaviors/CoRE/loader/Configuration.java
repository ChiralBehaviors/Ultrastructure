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
    public static Configuration fromYaml(InputStream yaml)
                                                          throws JsonParseException,
                                                          JsonMappingException,
                                                          IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(yaml, Configuration.class);
    }

    public boolean createDatabase = false;
    public boolean dropDatabase   = false;
    public boolean initializeSqlJ = false;
    public String  jdbcUrl;
    public String  password;
    public String  username;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public Loader construct() throws Exception {
        return new Loader(getConnection(), createDatabase, initializeSqlJ);
    }
}
