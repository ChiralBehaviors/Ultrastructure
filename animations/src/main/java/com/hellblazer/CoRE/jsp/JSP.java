/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.openjpa.jdbc.sql.SQLExceptions;
import org.postgresql.pljava.Session;
import org.postgresql.pljava.SessionManager;
import org.postgresql.pljava.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.kernel.WellKnownObject;

/**
 * 
 * @author hhildebrand
 * 
 */
public class JSP {
    private static final EntityManager EM;
    private static final Logger        log = LoggerFactory.getLogger(JSP.class);

    static {
        try {
            Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
            SQLExceptions.class.getCanonicalName();
            InputStream is = JSP.class.getResourceAsStream("jpa.properties");
            if (is == null) {
                log.error("Unable to read jpa.properties, resource is null");
                throw new IllegalStateException(
                                                "Unable to read jpa.properties, resource is null");
            }
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                log.error("Unable to read jpa properties", e);
                throw new IllegalStateException(
                                                "Unable to read jpa.properties",
                                                e);
            }
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                              properties);
            EM = emf.createEntityManager();
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            pw.println();
            for (Entry<String, Object> entry : EM.getProperties().entrySet()) {
                pw.println(String.format("%s = %s", entry.getKey(),
                                         entry.getValue()));
            }
            pw.close();
            log.info(String.format("JPA Properties: %s", writer.toString()));
            final Session current = SessionManager.current();
            EM.getTransaction().begin();
            current.addTransactionListener(new TransactionListener() {

                @Override
                public void onPrepare(Session session) throws SQLException {
                    EM.flush();
                }

                @Override
                public void onCommit(Session session) throws SQLException {
                }

                @Override
                public void onAbort(Session session) throws SQLException {
                }
            });
            log.info("Entity manager created");
        } catch (RuntimeException e) {
            log.error("Unable to initialize Animations", e);
            throw e;
        } catch (SQLException e) {
            log.error("Unable to retreive current Session from SessionManager",
                      e);
            throw new IllegalStateException(
                                            "Unable to retreive current Session from SessionManager",
                                            e);
        }
    }

    public static EntityManager getEm() {
        return EM;
    }

    public static <T> T execute(Callable<T> call) throws Exception {
        Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
        return call.call();
    }
}