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

package com.chiralbehaviors.CoRE.loader.plugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.chiralbehaviors.CoRE.loader.Configuration;
import com.chiralbehaviors.CoRE.loader.Loader;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.RollbackFailedException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author hhildebrand
 * 
 * @goal clear
 * 
 * @phase compile
 */
public class ClearDatabase extends AbstractMojo {

    /**
     * The comma seperated list of contexts to use for loading ("local" or
     * "aws")
     * 
     * @parameter
     */
    public String contexts = "local";

    /**
     * the name the core database
     * 
     * @parameter
     */
    public String coreDb   = "core";

    /**
     * the password of the core user
     * 
     * @parameter
     */
    public String corePassword;

    /**
     * the port of the core database
     * 
     * @parameter
     */
    public int    corePort;

    /**
     * the server host of the core database
     * 
     * @parameter
     */
    public String coreServer;

    /**
     * the core user name
     * 
     * @parameter
     */
    public String coreUsername;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (corePassword == null) {
            initializeFromEnvironment();
        }
        String url = String.format(Configuration.JDBC_URL, coreServer, corePort,
                                   coreDb);
        getLog().info(String.format("core connection: %s", url));
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, coreUsername,
                                                     corePassword);
        } catch (SQLException e1) {
            throw new MojoFailureException(String.format("Cannot connect to database: %s",
                                                         url),
                                           e1);
        }
        Liquibase liquibase = null;
        try {
            Database database = DatabaseFactory.getInstance()
                                               .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(Loader.MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML,
                                      new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                      database);
            initializeParameters(liquibase);
            liquibase.rollback("initial-schema-create", contexts);

        } catch (RollbackFailedException e) {
            if (e.getMessage()
                 .contains("Could not find tag 'initial-schema-create")) {
                getLog().info(String.format("%s is new database, not dropping",
                                            coreDb));
                return;
            }
            throw new MojoFailureException("Could not roll back", e);
        } catch (DatabaseException e) {
            throw new MojoFailureException("Could not roll back", e);
        } catch (LiquibaseException e) {
            throw new MojoFailureException("Could not roll back", e);
        } finally {
            if (liquibase != null) {
                try {
                    liquibase.forceReleaseLocks();
                } catch (LiquibaseException e) {
                    throw new MojoExecutionException(String.format("Could not release liquibase lock on: %s",
                                                                   coreDb));
                }
            }
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    private void initializeParameters(Liquibase liquibase) {
        liquibase.setChangeLogParameter("create.db.database", coreDb);
        liquibase.setChangeLogParameter("create.db.role", coreUsername);
        liquibase.setChangeLogParameter("create.db.password", corePassword);
    }

    private void initializeFromEnvironment() {
        URI dbUri;
        try {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s is not a valid URI",
                                                          System.getenv("DATABASE_URL")),
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

}
