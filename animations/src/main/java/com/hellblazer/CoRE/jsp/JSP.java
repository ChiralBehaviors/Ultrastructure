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

package com.hellblazer.CoRE.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.apache.openjpa.util.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.kernel.WellKnownObject;

/**
 * 
 * @author hhildebrand
 * 
 */
public abstract class JSP {
    private static final Properties           PROPERTIES = new Properties();
    private static final EntityManagerFactory EMF;
    private static final Logger               log        = LoggerFactory.getLogger(JSP.class);
    private static int                        depth      = 0;
    private static SQLException               rootCause;

    static {
        ClassLoader classLoader = JSP.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        StoreException.class.toString();
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
    }

    public static <T> T call(StoredProcedure<T> call) throws SQLException {
        depth++;
        if (log.isTraceEnabled()) {
            log.trace(String.format("nesting depth: %s", depth));
        }
        try {
            Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
            throwRootCause();
            EntityManager em = EMF.createEntityManager();
            em.getTransaction().begin();
            T value;
            try {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("calling %s", call));
                }
                try {
                    value = call.call(em);
                } finally {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("exiting %s", call));
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
                throw e;
            } catch (Throwable e) {
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Error during %s", call), e);
                }
                SQLException sqlException = new SQLException(
                                                             String.format("** Java Stored procedure failed %s",
                                                                           call),
                                                             e);
                if (rootCause == null) {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Setting root cause to: %s",
                                                sqlException));
                    }
                    rootCause = sqlException;
                }
                throw sqlException;
            }
            throwRootCause();
            try {
                em.getTransaction().commit();
            } catch (RollbackException e) {
                Throwable cause = e.getCause();
                while (true) {
                    if (cause == null) {
                        throw e;
                    }
                    if (cause instanceof SQLException) {
                        throw (SQLException) cause;
                    }
                    cause = cause.getCause();
                }
            }
            return value;
        } finally {
            depth--;
            if (depth == 0) {
                if (log.isTraceEnabled()) {
                    log.trace("clearing root cause");
                }
                rootCause = null;
            }
        }
    }

    private static void throwRootCause() throws SQLException {
        if (rootCause != null) {
            throw rootCause;
        }
    }
}