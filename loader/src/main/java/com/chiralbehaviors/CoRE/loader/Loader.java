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
package com.chiralbehaviors.CoRE.loader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.util.postgres.PostgresDSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.utils.DbaConfiguration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.exception.RollbackFailedException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author hhildebrand
 *
 */
public class Loader {

    public static final String     INITIAL_DATABASE_CREATE_TEMPLATE               = "initial-database-create-%s";
    public static final String     MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML = "com/chiralbehaviors/CoRE/schema/core.xml";
    private static final String    CREATE_DATABASE_XML                            = "create-database.xml";
    private static final String    INITIALIZE_XML                                 = "initialize.xml";
    private static final Logger    log                                            = LoggerFactory.getLogger(Loader.class);

    private final DbaConfiguration configuration;

    public Loader(DbaConfiguration configuration) throws Exception {
        this.configuration = configuration;
    }

    public void bootstrap() throws Exception {
        initialize();
        loadModel();
        bootstrapCoRE();
    }

    public void clear() throws SQLException, LiquibaseException {
        Liquibase liquibase = null;
        try (Connection connection = configuration.getCoreConnection()) {
            connection.setSchema("public");
            Database database = DatabaseFactory.getInstance()
                                               .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(Loader.MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML,
                                      new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                      database);
            initializeParameters(liquibase);
            liquibase.rollback("initial-schema-create", configuration.contexts);

        } catch (RollbackFailedException e) {
            if (e.getMessage()
                 .contains("Could not find tag 'initial-schema-create")) {
                log.info(String.format("%s is new database, not dropping",
                                       configuration.coreDb));
                return;
            }
            throw e;
        }
    }

    public Loader createDatabase() throws Exception, SQLException {
        if (configuration.dropDatabase) {
            dropDatabase();
        }
        log.info(String.format("Creating core db %s", configuration.coreDb));
        try (Connection connection = configuration.getDbaConnection()) {
            load(CREATE_DATABASE_XML, connection);
        }
        return this;
    }

    public void dropDatabase() throws Exception {
        Liquibase liquibase = null;
        try (Connection connection = configuration.getDbaConnection()) {
            Database database = DatabaseFactory.getInstance()
                                               .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(CREATE_DATABASE_XML,
                                      new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                      database);
            initializeParameters(liquibase);
            liquibase.rollback(String.format(INITIAL_DATABASE_CREATE_TEMPLATE,
                                             configuration.coreDb),
                               configuration.contexts);

        } catch (RollbackFailedException e) {
            if (e.getMessage()
                 .contains("Could not find tag 'initial-database-create")) {
                log.info(String.format("%s is new database, not dropping",
                                       configuration.coreDb));
                return;
            }
            throw e;
        }
    }

    public void execute() throws Exception {
        System.out.println(String.format("executing loader.dbaUsername: %s",
                                         configuration.dbaUsername));
        if (configuration.dbaUsername != null) {
            log.info("Creating multi tentant DB");
            createDatabase();
        } else {
            log.info("Creating single tentant DB");
        }
        bootstrap();
    }

    private void bootstrapCoRE() throws SQLException, IOException {
        log.info(String.format("Bootstrapping core in db %s",
                               configuration.coreDb));
        try (Connection conn = configuration.getCoreConnection()) {
            conn.setAutoCommit(false);
            DSLContext create = PostgresDSL.using(conn);
            create.transaction(config -> KernelUtil.clearAndLoadKernel(create));
            try (Model model = new ModelImpl(conn)) {
                create.transaction(config -> KernelUtil.initializeInstance(model,
                                                                           configuration.coreDb,
                                                                           "CoRE instance"));
            }
        }
        log.info("Bootstrapping complete");
    }

    private void initialize() throws Exception, SQLException {
        log.info(String.format("initializing core db %s: user: %s",
                               configuration.coreDb,
                               configuration.coreUsername));
        try (Connection connection = configuration.getCoreConnection()) {
            load(INITIALIZE_XML, connection);
        }
    }

    private void initializeParameters(Liquibase liquibase) {
        liquibase.setChangeLogParameter("create.db.database",
                                        configuration.coreDb);
        liquibase.setChangeLogParameter("create.db.role",
                                        configuration.coreUsername);
        liquibase.setChangeLogParameter("create.db.password",
                                        configuration.corePassword);
    }

    private void load(String changeLog,
                      Connection connection) throws Exception {
        Liquibase liquibase = null;
        Database database = DatabaseFactory.getInstance()
                                           .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        liquibase = new Liquibase(changeLog,
                                  new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                  database);
        initializeParameters(liquibase);
        liquibase.update(Integer.MAX_VALUE, configuration.contexts);
    }

    private void loadModel() throws Exception, SQLException {
        log.info(String.format("loading model sql in core db %s",
                               configuration.coreDb));
        try (Connection connection = configuration.getCoreConnection()) {
            load(MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML, connection);
        }
    }
}
