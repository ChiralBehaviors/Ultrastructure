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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.kernel.Bootstrap;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 * 
 */
public class Loader {

    private static final String ANIMATIONS_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_ANIMATIONS_XML = "animations/com/chiralbehaviors/CoRE/schema/animations.xml";
    private static final String ANIMATIONS_JAR;
    private static final String ANIMATIONS_JAR_NAME;
    private static final String CREATE_DATABASE_XML                                        = "create-database.xml";
    private static final String DROP_DATABASE_SQL                                          = "/drop-database.sql";
    private static final String DROP_LIQUIBASE_SQL                                         = "/drop-liquibase.sql";
    private static final String DROP_ROLES_SQL                                             = "/drop-roles.sql";
    private static final Logger log                                                        = LoggerFactory.getLogger(Loader.class);
    private static final String MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML            = "model/com/chiralbehaviors/CoRE/schema/core.xml";

    static {
        String version;
        try {
            version = Utils.getDocument(Loader.class.getResourceAsStream("/version"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        ANIMATIONS_JAR = String.format("/animations/animations-%s-phat.jar",
                                       version);
        ANIMATIONS_JAR_NAME = String.format("animations_%s",
                                            version.replace('.', '_').replace("-",
                                                                              "_"));
    }

    public static void main(String[] argv) throws Exception {
        Loader loader = new Loader(
                                   Configuration.fromYaml(Utils.resolveResource(Loader.class,
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
        loadAnimationsJar();
        loadAnimations();
        setClassPath();
        bootstrapCoRE();
    }

    private void drop(PreparedStatement drop, String name,
                      PreparedStatement validate) throws SQLException {
        validate.setString(1, name);
        ResultSet result = validate.executeQuery();
        if (result.next()) {
            log.info(String.format("dropping jar %s", name));
            drop.setString(1, name);
            drop.setBoolean(2, false);
            drop.execute();
            result = validate.executeQuery();
            if (result.next()) {
                throw new IllegalStateException(
                                                String.format("Did not actually drop %s",
                                                              name));
            }
        }
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
        log.info(String.format("Dropping roles in db %s", configuration.coreDb));
        execute(connection,
                Utils.getDocument(getClass().getResourceAsStream(DROP_ROLES_SQL)));
    }

    private void executeWithError(Connection connection, String sqlFile)
                                                                        throws SQLException {
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

    private void execute(Connection connection, String sqlFile)
                                                               throws Exception {
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

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[16 * 1024];
            for (int read = is.read(buffer); read != -1; read = is.read(buffer)) {
                baos.write(buffer, 0, read);
            }
        } finally {
            is.close();
        }
        return baos.toByteArray();
    }

    private void load(PreparedStatement load) throws IOException, SQLException {
        byte[] bytes = getBytes(getClass().getResourceAsStream(ANIMATIONS_JAR));
        load.setBytes(1, bytes);
        load.setString(2, ANIMATIONS_JAR_NAME);
        load.setBoolean(3, true);
        load.execute();
    }

    private void load(String changeLog, Connection connection) throws Exception {
        Liquibase liquibase = null;
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(
                                                                                                                   connection));
            liquibase = new Liquibase(
                                      changeLog,
                                      new ClassLoaderResourceAccessor(
                                                                      getClass().getClassLoader()),
                                      database);
            liquibase.update(Integer.MAX_VALUE, "");

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

    private void loadAnimationsJar() throws Exception {
        log.info(String.format("loading animations code in core db %s",
                               configuration.coreDb));
        Connection connection = configuration.getCoreConnection();
        connection.setAutoCommit(true);
        PreparedStatement drop = connection.prepareStatement("SELECT sqlj.remove_jar(?, ?)");
        PreparedStatement load = connection.prepareStatement("SELECT sqlj.install_jar(?, ?, ?)");
        PreparedStatement validate = connection.prepareStatement("SELECT jarid from sqlj.jar_repository where jarname=?");
        drop(drop, ANIMATIONS_JAR_NAME, validate);
        log.info(String.format("loading artifact %s", ANIMATIONS_JAR));
        load(load);
    }

    private void setClassPath() throws SQLException {
        log.info(String.format("setting the pl/java classpath in core db %s",
                               configuration.coreDb));
        Connection connection = configuration.getCoreConnection();
        connection.setAutoCommit(true);
        PreparedStatement statement = connection.prepareStatement(String.format("SELECT sqlj.set_classpath('ruleform', '%s')",
                                                                                ANIMATIONS_JAR_NAME));
        statement.execute();
    }

    protected void bootstrapCoRE() throws SQLException {
        log.info(String.format("Bootstrapping core in db %s",
                               configuration.coreDb));
        Connection connection = configuration.getCoreConnection();
        connection.setAutoCommit(false);
        new Bootstrap(connection).bootstrap();
        connection.commit();
    }

    protected void createDatabase() throws Exception, SQLException {
        log.info(String.format("Creating core db %s", configuration.coreDb));
        load(CREATE_DATABASE_XML, configuration.getDbaConnection());
    }

    protected void loadAnimations() throws Exception, SQLException {
        log.info(String.format("loading animations sql in core db %s",
                               configuration.coreDb));
        load(ANIMATIONS_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_ANIMATIONS_XML,
             configuration.getCoreConnection());
    }

    protected void loadModel() throws Exception, SQLException {
        log.info(String.format("loading model sql in core db %s",
                               configuration.coreDb));
        load(MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML,
             configuration.getCoreConnection());
    }
}
