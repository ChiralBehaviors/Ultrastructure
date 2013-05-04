/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.test;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hhildebrand
 * 
 */
public class DtdGenerator {
    final static Logger log = LoggerFactory.getLogger(DtdGenerator.class);

    public static final IDatabaseConnection getConnection() throws Exception {
        /*
         * Read in connections from command-line properties, allowing
         * some sensible defaults.
         */
        String host = System.getProperty("db.host", "localhost");
        String port = System.getProperty("db.port", "5432");
        String database = System.getProperty("db.database", "core");
        String user = System.getProperty("db.user", "core");
        String password = System.getProperty("db.password", "password");

        // Assemble URL and print it out for reference and sanity checking
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        System.out.println("Connecting with URL: " + url);

        // We're using Postgres... deal with it.
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection(url, user, password);

        IDatabaseConnection db = new DatabaseConnection(c, "ruleform");

        DatabaseConfig configuration = db.getConfig();

        configuration.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
                                 true);
        configuration.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS,
                                 true);
        configuration.setProperty(DatabaseConfig.PROPERTY_TABLE_TYPE,
                                  new String[] { "TABLE", "VIEW" });
        configuration.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                                  new DataTypeFactory());

        return db;
    }

    public static void main(String[] args) throws Exception {
        String outputFile = args.length == 1 ? args[0] : "core.dtd";
        IDatabaseConnection connection = getConnection();
        List<String> ruleformTableNames = getRuleformTableNames(connection.getConnection());
        FlatDtdWriter fw = new FlatDtdWriter(
                                             new OutputStreamWriter(
                                                                    new FileOutputStream(
                                                                                         outputFile)));
        fw.setContentModel(FlatDtdWriter.CHOICE);
        fw.write(connection.createDataSet(ruleformTableNames.toArray(new String[ruleformTableNames.size()])));
    }

    /**
     * Queries the information schema to retrieve all table names from the
     * "ruleform" schema.
     * 
     * @param c
     *            {@link Connection} to the testing database
     * @return list of schema-qualified ruleform table names (e.g.,
     *         "ruleform.bioproduct")
     * @throws java.lang.Exception
     */
    private static List<String> getRuleformTableNames(Connection c)
                                                                   throws Exception {
        List<String> l = new ArrayList<String>();
        ResultSet r = c.createStatement().executeQuery("SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name");
        while (r.next()) {
            String table = r.getString("name");
            System.out.println("Will add a DTD entry for table '" + table + "'");
            l.add(table);
        }
        r.close();
        return l;
    }
}