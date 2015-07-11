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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.hellblazer.utils.Utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author hhildebrand
 * 
 */
public class Loader {

    private static final String CREATE_DATABASE_XML                             = "create-database.xml";
    private static final String DROP_DATABASE_SQL                               = "/drop-database.sql";
    private static final String DROP_LIQUIBASE_SQL                              = "/drop-liquibase.sql";
    private static final String DROP_ROLES_SQL                                  = "/drop-roles.sql";
    private static final Logger log                                             = LoggerFactory.getLogger(Loader.class);
    private static final String MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML = "model/com/chiralbehaviors/CoRE/schema/core.xml";

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
        if (configuration.dropDatabase) {
            dropDatabase();
        }
        createDatabase();
        loadModel();
        bootstrapCoRE();
    }

    private void dropDatabase() throws Exception {
        Connection connection = configuration.getDbaConnection();
        connection.setAutoCommit(true);
        log.info(String.format("Dropping db %s", configuration.coreDb));
        executeWithError(connection,
                         Utils.getDocument(getClass().getResourceAsStream(DROP_DATABASE_SQL)));
        log.info(String.format("Dropping liquibase metadata in db %s",
                               configuration.dbaDb));
        execute(connection,
                Utils.getDocument(getClass().getResourceAsStream(DROP_LIQUIBASE_SQL)));
        log.info(String.format("Dropping roles in db %s",
                               configuration.coreDb));
        execute(connection,
                Utils.getDocument(getClass().getResourceAsStream(DROP_ROLES_SQL)));
    }

    private void executeWithError(Connection connection,
                                  String sqlFile) throws SQLException {
        StringTokenizer tokes = new StringTokenizer(sqlFile, ";");
        while (tokes.hasMoreTokens()) {
            String line = tokes.nextToken();
            PreparedStatement exec = connection.prepareStatement(line);
            try {
                exec.execute();
            } finally {
                exec.close();
            }
        }
    }

    private void execute(Connection connection,
                         String sqlFile) throws Exception {
        StringTokenizer tokes = new StringTokenizer(sqlFile, ";");
        while (tokes.hasMoreTokens()) {
            String line = tokes.nextToken();
            PreparedStatement exec = connection.prepareStatement(line);
            try {
                exec.execute();
            } catch (SQLException e) {
            } finally {
                exec.close();
            }
        }
    }

    private void load(String changeLog,
                      Connection connection) throws Exception {
        Liquibase liquibase = null;
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(changeLog,
                                      new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                                      database);
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

    protected void bootstrapCoRE() throws SQLException, IOException {
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
        EntityManager em = emf.createEntityManager();
        KernelUtil.loadKernel(em);
        em.close();
        emf.close();
        log.info("Bootstrapping complete");
    }

    protected void createDatabase() throws Exception, SQLException {
        log.info(String.format("Creating core db %s", configuration.coreDb));
        load(CREATE_DATABASE_XML, configuration.getDbaConnection());
    }

    protected void loadModel() throws Exception, SQLException {
        log.info(String.format("loading model sql in core db %s",
                               configuration.coreDb));
        load(MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML,
             configuration.getCoreConnection());
    }
}
