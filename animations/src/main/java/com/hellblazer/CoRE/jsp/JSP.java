/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
        Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        T value;
        try {
            value = call.call(em);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter pWriter = new PrintWriter(writer);
            e.printStackTrace(pWriter);
            pWriter.flush();
            throw new SQLException(String.format("** Java Stored procedure failed\n%s",
                                                 writer.toString()), e);
        }
        em.getTransaction().commit();
        return value;
    }
}