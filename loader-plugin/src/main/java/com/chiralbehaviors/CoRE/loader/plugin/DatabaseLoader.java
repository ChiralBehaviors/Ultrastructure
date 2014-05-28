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

package com.chiralbehaviors.CoRE.loader.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.chiralbehaviors.CoRE.loader.Configuration;
import com.chiralbehaviors.CoRE.loader.Loader;

/**
 * @author hhildebrand
 * 
 */
/*
 * Copyright (c) 2009, 2011 Hal Hildebrand, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author hhildebrand
 * 
 * @goal load
 * 
 * @phase package
 */
public class DatabaseLoader extends AbstractMojo {
    public static class JdbcConnection {
        public String jdbcUrl;
        public String password;
        public String username;
    }

    /**
     * Drop the database before loading
     * 
     * @parameter
     */
    private boolean        dropDatabase   = false;

    /**
     * Initialize the sqlj schema
     * 
     * @parameter
     */
    private boolean        initializeSqlJ = false;

    /**
     * Super user connect information for creating core database
     * 
     * @parameter
     */
    private JdbcConnection dbaConnection;

    /**
     * Core user connect information for schemas in the core database
     * 
     * @parameter
     */
    private JdbcConnection coreConnection;

    private Configuration constructConfiguration() {
        Configuration config = new Configuration();
        config.jdbcUrl = dbaConnection.jdbcUrl;
        config.username = dbaConnection.username;
        config.password = dbaConnection.password;
        config.coreJdbcUrl = coreConnection.jdbcUrl;
        config.username = coreConnection.username;
        config.password = coreConnection.password;
        config.dropDatabase = dropDatabase;
        config.initializeSqlJ = initializeSqlJ;
        return config;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            new Loader(constructConfiguration()).bootstrap();
        } catch (Exception e) {
            MojoFailureException ex = new MojoFailureException(
                                                               "Unable to load database");
            ex.initCause(e);
            throw ex;
        }
    }
}
