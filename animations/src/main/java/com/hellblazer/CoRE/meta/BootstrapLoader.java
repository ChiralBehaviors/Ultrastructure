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

package com.hellblazer.CoRE.meta;

import static com.hellblazer.CoRE.kernel.WellKnownObject.CORE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.hellblazer.CoRE.Util;
import com.hellblazer.CoRE.capability.Capability;
import com.hellblazer.CoRE.kernel.Bootstrap;
import com.hellblazer.CoRE.meta.models.KernelImpl;
import com.hellblazer.CoRE.resource.ResourceAttribute;
import com.hellblazer.CoRE.resource.ResourceAttributeAuthorization;

/**
 * The bootstrap loader of the kernel rules for the CoRE
 * 
 * @author hhildebrand
 * 
 */
public class BootstrapLoader extends Bootstrap {
    private final static String DEFAULT_PROPERTIES_FILE = "jpa.properties";

    public static void main(String[] argv) throws IOException, SQLException {
        if (argv.length > 1) {
            System.err.println("Only one argument, the jpa properties file location, allowed");
            System.exit(1);
        }
        String propertiesFileName = DEFAULT_PROPERTIES_FILE;
        if (argv.length == 1) {
            propertiesFileName = argv[0];
        }
        Properties properties = new Properties();
        File file = new File(propertiesFileName);
        if (!file.exists()) {
            System.err.println(String.format("JPA Properties file %s does not exist",
                                             file.getAbsolutePath()));
            System.exit(1);
        }
        InputStream is = new FileInputStream(file);
        properties.load(is);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(CORE,
                                                                          properties);
        EntityManager em = emf.createEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        loader.bootstrap();
        em.getTransaction().commit();
        System.out.println("BootstrapLoader completed successfully.");
    }

    private final EntityManager em;
    private KernelImpl          kernel;

    public BootstrapLoader(EntityManager em) {
        super(em.unwrap(Connection.class));
        this.em = em;
    }

    @Override
    public void bootstrap() throws SQLException {
        super.bootstrap();
        kernel = new KernelImpl(em);
        constructProductNetworks();
        constructResourceNetworks();
        constructLocationNetworks();
        constructAttributeNetworks();
        constructCapabilities();
        constructAspects();
        constructResourceAttributes();
    }

    public void constructAspects() {
        ResourceAttributeAuthorization loginAuth = new ResourceAttributeAuthorization(
                                                                                      kernel.getIsA(),
                                                                                      kernel.getCoreUser(),
                                                                                      kernel.getLoginAttribute(),
                                                                                      kernel.getCore());
        em.persist(loginAuth);
        ResourceAttributeAuthorization passwordHashAuth = new ResourceAttributeAuthorization(
                                                                                             kernel.getIsA(),
                                                                                             kernel.getCoreUser(),
                                                                                             kernel.getPasswordHashAttribute(),
                                                                                             kernel.getCore());
        em.persist(passwordHashAuth);

    }

    public void constructAttributeNetworks() {
        kernel.getAttribute().link(kernel.getIsA(), kernel.getAttribute(),
                                   kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyAttribute().link(kernel.getIsA(), kernel.getAttribute(),
                                      kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalAttribute().link(kernel.getIsA(),
                                           kernel.getAttribute(),
                                           kernel.getCore(), kernel.getCore(),
                                           em);
        kernel.getLoginAttribute().link(kernel.getIsA(), kernel.getAttribute(),
                                        kernel.getCore(), kernel.getCore(), em);
        kernel.getPasswordHashAttribute().link(kernel.getIsA(),
                                               kernel.getAttribute(),
                                               kernel.getCore(),
                                               kernel.getCore(), em);
    }

    public void constructCapabilities() {
        Capability coreAttributeCapability = new Capability(
                                                            kernel.getCore(),
                                                            kernel.getAnything(),
                                                            kernel.getAttribute(),
                                                            "The capability that gives CoRE the right to do anything to Attributes",
                                                            kernel.getCore());
        em.persist(coreAttributeCapability);

        Capability coreEntityCapability = new Capability(
                                                         kernel.getCore(),
                                                         kernel.getAnything(),
                                                         kernel.getProduct(),
                                                         "The capability that gives CoRE the right to do anything to Entities",
                                                         kernel.getCore());
        em.persist(coreEntityCapability);

        Capability coreLocationCapability = new Capability(
                                                           kernel.getCore(),
                                                           kernel.getAnything(),
                                                           kernel.getLocation(),
                                                           "The capability that gives CoRE the right to anything to Locations",
                                                           kernel.getCore());
        em.persist(coreLocationCapability);

        Capability coreResourceCapability = new Capability(
                                                           kernel.getCore(),
                                                           kernel.getAnything(),
                                                           kernel.getResource(),
                                                           "The capability that gives CoRE the right to do anything to Resources",
                                                           kernel.getCore());
        em.persist(coreResourceCapability);
    }

    public void constructProductNetworks() {
        kernel.getProduct().link(kernel.getIsA(), kernel.getProduct(),
                                kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyProduct().link(kernel.getIsA(), kernel.getProduct(),
                                   kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalProduct().link(kernel.getIsA(), kernel.getProduct(),
                                        kernel.getCore(), kernel.getCore(), em);
    }

    public void constructLocationNetworks() {
        kernel.getLocation().link(kernel.getIsA(), kernel.getLocation(),
                                  kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyLocation().link(kernel.getIsA(), kernel.getLocation(),
                                     kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalLocation().link(kernel.getIsA(),
                                          kernel.getLocation(),
                                          kernel.getCore(), kernel.getCore(),
                                          em);
    }

    public void constructResourceAttributes() {
        ResourceAttribute userName = new ResourceAttribute(
                                                           kernel.getLoginAttribute(),
                                                           kernel.getCore());
        userName.setResource(kernel.getSuperUser());
        userName.setTextValue("superUser@example.com");
        em.persist(userName);

        ResourceAttribute passwordHash = new ResourceAttribute(
                                                               kernel.getPasswordHashAttribute(),
                                                               kernel.getCore());

        passwordHash.setResource(kernel.getSuperUser());
        try {
            passwordHash.setTextValue(Util.md5Hash("password"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        em.persist(passwordHash);
    }

    public void constructResourceNetworks() {
        kernel.getResource().link(kernel.getIsA(), kernel.getResource(),
                                  kernel.getCore(), kernel.getCore(), em);
        kernel.getCore().link(kernel.getIsA(), kernel.getResource(),
                              kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyResource().link(kernel.getIsA(), kernel.getResource(),
                                     kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalResource().link(kernel.getIsA(),
                                          kernel.getResource(),
                                          kernel.getCore(), kernel.getCore(),
                                          em);
        kernel.getCoreAnimationSoftware().link(kernel.getIsA(),
                                               kernel.getResource(),
                                               kernel.getCore(),
                                               kernel.getCore(), em);
        kernel.getPropagationSoftware().link(kernel.getIsA(),
                                             kernel.getResource(),
                                             kernel.getCore(),
                                             kernel.getCore(), em);
        kernel.getSpecialSystemResource().link(kernel.getIsA(),
                                               kernel.getResource(),
                                               kernel.getCore(),
                                               kernel.getCore(), em);
        kernel.getCoreUser().link(kernel.getIsA(), kernel.getResource(),
                                  kernel.getCore(), kernel.getCore(), em);
        kernel.getSuperUser().link(kernel.getIsA(), kernel.getCoreUser(),
                                   kernel.getCore(), kernel.getCore(), em);
        kernel.getInverseSoftware().link(kernel.getIsA(), kernel.getResource(),
                                         kernel.getCore(), kernel.getCore(), em);
    }
}
