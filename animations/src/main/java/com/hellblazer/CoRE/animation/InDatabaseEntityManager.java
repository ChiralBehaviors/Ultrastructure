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

package com.hellblazer.CoRE.animation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.kernel.WellKnownObject;

/**
 * 
 * @author hhildebrand
 * 
 */
public class InDatabaseEntityManager {
    private static final EntityManager EM;
    private static final Logger        log = LoggerFactory.getLogger(InDatabaseEntityManager.class);

    static {
        try {
            establishContext();
            InputStream is = InDatabaseEntityManager.class.getResourceAsStream("jpa.properties");
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
            log.info(String.format("Entities: %s",
                                   EM.getMetamodel().getEntities()));
            log.info("Product manager created");
        } catch (RuntimeException e) {
            log.error("Unable to initialize Animations", e);
            throw e;
        }
    }

    /**
     * Establish the class loading context
     */
    public static void establishContext() {
        Thread.currentThread().setContextClassLoader(InDatabaseEntityManager.class.getClassLoader());
    }

    public static EntityManager getEm() {
        return EM;
    }
}