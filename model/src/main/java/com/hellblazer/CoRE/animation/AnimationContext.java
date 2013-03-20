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

package com.hellblazer.CoRE.animation;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.EntityModel;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.meta.security.AuthenticatedPrincipal;

/**
 * The context in which to run CoRE animations.
 * 
 * @author hhildebrand
 * 
 */
public class AnimationContext {
    private static class InDatabaseEntityManager {
        private static final EntityManager EM;
        static {
            try {
                InputStream is = AnimationContext.class.getResourceAsStream("jpa.properties");
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
            } catch (RuntimeException e) {
                log.error("Unable to initialize Animations", e);
                throw e;
            }
        }

        private static EntityManager getEm() {
            return EM;
        }
    }

    private static Logger          log = LoggerFactory.getLogger(AnimationContext.class);

    private final Connection       connection;
    private final EntityManager    em;
    private final Model            model;
    private AuthenticatedPrincipal principal;

    /**
     * @param em
     */
    public AnimationContext(EntityManager em) {
        this(new ModelImpl(em), em);
    }

    /**
     * @param model
     * @param EM
     * @param principal
     */
    public AnimationContext(Model model, EntityManager em) {
        this.model = model;
        this.em = em;
        connection = em.unwrap(Connection.class);
    }

    AnimationContext() {
        this(InDatabaseEntityManager.getEm());
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @return the EM
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @return
     */
    public EntityModel getEntityModel() {
        return model.getEntityModel();
    }

    /**
     * @return
     */
    public JobModel getJobModel() {
        return model.getJobModel();
    }

    /**
     * @return
     */
    public Kernel getKernel() {
        return model.getKernel();
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * @return the principal
     */
    public AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(AuthenticatedPrincipal principal) {
        this.principal = principal;
    }

}
