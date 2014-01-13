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