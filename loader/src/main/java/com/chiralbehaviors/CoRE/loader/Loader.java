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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.hellblazer.utils.Utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.RollbackFailedException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author hhildebrand
 *
 */
public class Loader {

    private static final String CREATE_DATABASE_XML                            = "create-database.xml";
    public static final String  INITIAL_DATABASE_CREATE_TEMPLATE               = "initial-database-create-%s";
    private static final String INITIALIZE_XML                                 = "initialize.xml";
    private static final Logger log                                            = LoggerFactory.getLogger(Loader.class);
    public static final String  MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML = "com/chiralbehaviors/CoRE/schema/core.xml";

    public static void main(String[] argv) throws Exception {
        Loader loader = new Loader(Configuration.fromYaml(Utils.resolveResource(Loader.class,
                                                                                argv[0])));
        loader.bootstrap();
    }

    private final Configuration configuration;

    public Loader(Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    public void bootstrap() throws Exception {
        initialize();
        loadModel();
        bootstrapCoRE();
    }

    public Loader createDatabase() throws Exception, SQLException {
        if (configuration.dropDatabase) {
            dropDatabase();
        }
        log.info(String.format("Creating core db %s", configuration.coreDb));
        load(CREATE_DATABASE_XML, configuration.getDbaConnection());
        return this;
    }

    public void dropDatabase() throws Exception {
        Connection connection = configuration.getDbaConnection();
        Liquibase liquibase = null;
        try {
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
        } finally {
            if (liquibase != null) {
                liquibase.forceReleaseLocks();
            }
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    private void bootstrapCoRE() throws SQLException, IOException {
        log.info(String.format("Bootstrapping core in db %s",
                               configuration.coreDb));
        String txfmd;
        try (InputStream is = getClass().getResourceAsStream("/jpa.properties")) {
            if (is == null) {
                throw new IllegalStateException("jpa properties missing");
            }
            Map<String, String> props = new HashMap<>();
            props.put("init.db.login", configuration.coreUsername);
            props.put("init.db.password", configuration.corePassword);
            props.put("init.db.server", configuration.coreServer);
            props.put("init.db.port", Integer.toString(configuration.corePort));
            props.put("init.db.database", configuration.coreDb);
            txfmd = Utils.getDocument(is, props);
        }
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(txfmd.getBytes()));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);

        EntityManager loadingEm = emf.createEntityManager();
        try {
            KernelUtil.loadKernel(loadingEm);
        } finally {
            if (loadingEm.getTransaction()
                         .isActive()) {
                loadingEm.getTransaction()
                         .rollback();
            }
            loadingEm.close();
        }
        try (Model model = new ModelImpl(emf)) {
            EntityTransaction transaction = model.getEntityManager()
                                                 .getTransaction();
            transaction.begin();
            KernelUtil.initializeInstance(model, configuration.coreDb,
                                          "CoRE instance");
            transaction.commit();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Unable to create CoRE instance",
                                            e);
        } finally {
            emf.close();
        }
        log.info("Bootstrapping complete");
    }

    private void initialize() throws Exception, SQLException {
        log.info(String.format("initializing core db %s",
                               configuration.coreDb));
        load(INITIALIZE_XML, configuration.getCoreConnection());
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
        try {
            Database database = DatabaseFactory.getInstance()
                                               .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(changeLog,
                                      new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                      database);
            initializeParameters(liquibase);
            liquibase.update(Integer.MAX_VALUE, configuration.contexts);

        } finally {
            if (liquibase != null) {
                liquibase.forceReleaseLocks();
            }
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    private void loadModel() throws Exception, SQLException {
        log.info(String.format("loading model sql in core db %s",
                               configuration.coreDb));
        load(MODEL_COM_CHIRALBEHAVIORS_CORE_SCHEMA_CORE_XML,
             configuration.getCoreConnection());
    }
}
