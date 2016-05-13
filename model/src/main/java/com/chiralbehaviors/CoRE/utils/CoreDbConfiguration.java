/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author hhildebrand
 *
 */
public class CoreDbConfiguration {

    public static final String        DATABASE_URL = "DATABASE_URL";
    public static String              JDBC_URL     = "jdbc:postgresql://%s:%s/%s";
    public static CoreDbConfiguration TEST_ENV_CONFIGURATION;

    /**
     * The comma seperated list of contexts to use for loading ("local" or
     * "aws")
     * 
     * @parameter
     */
    public String                     contexts     = "local";

    /**
     * the name the core database
     * 
     * @parameter
     */
    public String                     coreDb       = "core";
    /**
     * the password of the core user
     * 
     * @parameter
     */
    public String                     corePassword;
    /**
     * the port of the core database
     * 
     * @parameter
     */
    public int                        corePort;
    /**
     * the server host of the core database
     * 
     * @parameter
     */
    public String                     coreServer;
    /**
     * the core user name
     * 
     * @parameter
     */
    public String                     coreUsername;

    /**
     * 
     */
    public CoreDbConfiguration() {
        super();
    }

    public Connection getCoreConnection() throws SQLException {
        if (corePassword == null) {
            initializeFromEnvironment();
        }
        String url = getCoreJdbcURL();
        System.out.println(String.format("core connection: %s, user: %s", url,
                                         coreUsername));
        Connection connection = DriverManager.getConnection(url, coreUsername,
                                                            corePassword);
        connection.setAutoCommit(false);
        return connection;
    }

    public String getCoreJdbcURL() {
        return String.format(JDBC_URL, coreServer, corePort,
                             coreDb.toLowerCase());
    }

    public String getDbUrlFromEnv() {
        return System.getenv(DATABASE_URL);
    }

    public void initializeFromEnvironment() {
        if (TEST_ENV_CONFIGURATION != null) {
            initializeFromTest();
            return;
        }
        URI dbUri;
        String envURI = getDbUrlFromEnv();
        if (envURI == null) {
            throw new IllegalStateException("DATABASE_URL is not defined in the system environment");
        }
        try {
            dbUri = new URI(envURI);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s is not a valid URI",
                                                          envURI),
                                            e);
        }

        String[] up = dbUri.getUserInfo()
                           .split(":");
        if (up.length != 2) {
            System.err.println("Invalid username:password in DATABASE_URL");
            throw new IllegalStateException();
        }
        coreDb = dbUri.getPath()
                      .substring(1);
        corePassword = up[1];
        coreUsername = up[0];
        corePort = dbUri.getPort();
        coreServer = dbUri.getHost();
    }

    @Override
    public String toString() {
        return String.format("CoreDbConfiguration [contexts=%s, coreDb=%s, corePassword=%s, corePort=%s, coreServer=%s, coreUsername=%s]",
                             contexts, coreDb,
                             corePassword == null ? ":: undefined :: "
                                                  : "**********",
                             corePort, coreServer, coreUsername);
    }

    private void initializeFromTest() {
        coreDb = TEST_ENV_CONFIGURATION.coreDb;
        corePassword = TEST_ENV_CONFIGURATION.corePassword;
        coreUsername = TEST_ENV_CONFIGURATION.coreUsername;
        corePort = TEST_ENV_CONFIGURATION.corePort;
        coreServer = TEST_ENV_CONFIGURATION.coreServer;
    }

}