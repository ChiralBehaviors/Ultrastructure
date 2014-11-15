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

package com.chiralbehaviors.CoRE.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.postgresql.pljava.Session;
import org.postgresql.pljava.SessionManager;
import org.postgresql.pljava.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject;

/**
 *
 * @author hhildebrand
 *
 */
public final class JSP {

    public static final EntityManagerFactory EMF;

    private static int                       depth                    = 0;
    private static int                       jobProcessingCount       = 0;
    private static final Logger              log                      = LoggerFactory.getLogger(JSP.class);
    private static final int                 MAX_JOB_PROCESSING       = 100;
    /**
     *
     */
    private static final int                 MAX_REENTRANT_CALL_DEPTH = 10;
    private static final Properties          PROPERTIES               = new Properties();
    private static SQLException              rootCause;

    static {
        ClassLoader classLoader = JSP.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        // assume SLF4J is bound to logback in the current environment
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        InputStream is = JSP.class.getResourceAsStream("jpa.properties");
        if (is == null) {
            log.error("Unable to read jpa.properties, resource is null");
            throw new IllegalStateException(
                                            "Unable to read jpa.properties, resource is null");
        }
        try {
            PROPERTIES.load(is);
        } catch (IOException e) {
            log.error("Unable to read jpa properties", e);
            throw new IllegalStateException("Unable to read jpa.properties", e);
        }
        EMF = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                     PROPERTIES);

        Session session;
        try {
            session = SessionManager.current();
        } catch (SQLException e) {
            log.error("Unable to obtain current session", e);
            throw new IllegalStateException("Unable to obtain current session",
                                            e);
        }
        session.addTransactionListener(new TransactionListener() {

            @Override
            public void onAbort(Session arg0) throws SQLException {
                EMF.getCache().evictAll();
                jobProcessingCount = 0;
            }

            @Override
            public void onCommit(Session arg0) throws SQLException {
                jobProcessingCount = 0;
            }

            @Override
            public void onPrepare(Session arg0) throws SQLException {
            }
        });
        EntityManager em = EMF.createEntityManager();
        log.info("loading the kernel");
        try {
            KernelImpl.cacheKernel(em);
        } finally {
            em.close();
        }
    }

    private JSP() {
    }

    public static <T> T call(StoredProcedure<T> call) throws SQLException {
        if (rootCause != null) {
            return null;
        }
        depth++;
        if (depth > MAX_REENTRANT_CALL_DEPTH) {
            rootCause = new SQLException(
                                         String.format("Max reentrant call depth reached, call: %s",
                                                       call));
            depth--;
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("nesting depth: %s", depth));
        }
        try {
            Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
            EntityManager em = EMF.createEntityManager();
            em.getTransaction().begin();
            T value;
            try {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("calling %s", call));
                }
                try {
                    value = call.call(em);
                } finally {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("exiting %s", call));
                    }
                }
            } catch (SQLException e) {
                if (rootCause == null) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Setting root cause to: %s", e));
                    }
                    rootCause = e;
                }
                if (log.isTraceEnabled()) {
                    log.warn(String.format("Error during %s", call), e);
                }
                return null;
            } catch (Throwable e) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Error during %s", call), e);
                }
                StringWriter string = new StringWriter();
                PrintWriter writer = new PrintWriter(string);
                e.printStackTrace(writer);
                writer.flush();
                SQLException sqlException = new SQLException(
                                                             String.format("** Java Stored procedure failed %s\n%s",
                                                                           call,
                                                                           string.toString()),
                                                             e);
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Setting root cause to: %s",
                                            sqlException));
                    rootCause = sqlException;
                }
                return null;
            }
            try {
                em.getTransaction().commit();
            } catch (RollbackException e) {
                Throwable cause = e.getCause();
                for (int i = 0; i < 15; i++) {
                    if (cause == null) {
                        break;
                    }
                    if (cause instanceof SQLException) {
                        throw (SQLException) cause;
                    }
                    cause = cause.getCause();
                }
                throw new SQLException("Txn rollback", e);
            }
            return value;
        } finally {
            depth--;
            if (depth == 0) {
                if (log.isTraceEnabled()) {
                    log.trace("clearing root cause");
                }
                SQLException cause = rootCause;
                rootCause = null;
                if (cause != null) {
                    throw cause;
                }
            }
        }
    }

    public static void incJobProcessingCount() throws SQLException {
        jobProcessingCount++;
        if (jobProcessingCount >= MAX_JOB_PROCESSING) {
            throw new SQLException(
                                   String.format("The number of jobs processed [%s} exceeds the maximum allowed [%s]",
                                                 jobProcessingCount,
                                                 MAX_JOB_PROCESSING));
        }
    }
}