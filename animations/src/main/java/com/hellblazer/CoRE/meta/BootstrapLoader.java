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

import com.hellblazer.CoRE.agency.AgencyAttributeAuthorization;
import com.hellblazer.CoRE.agency.access.AgencyAttribute;
import com.hellblazer.CoRE.kernel.Bootstrap;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.utils.Util;

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
        constructAgencyNetworks();
        constructLocationNetworks();
        constructAttributeNetworks();
        constructAspects();
        constructAgencyAttributes();
    }

    public void constructAgencyAttributes() {
        AgencyAttribute userName = new AgencyAttribute(
                                                       kernel.getLoginAttribute(),
                                                       kernel.getCore());
        userName.setAgency(kernel.getSuperUser());
        userName.setTextValue("superUser@example.com");
        em.persist(userName);

        AgencyAttribute passwordHash = new AgencyAttribute(
                                                           kernel.getPasswordHashAttribute(),
                                                           kernel.getCore());

        passwordHash.setAgency(kernel.getSuperUser());
        try {
            passwordHash.setTextValue(Util.md5Hash("password"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        em.persist(passwordHash);
    }

    public void constructAgencyNetworks() {
        kernel.getAgency().link(kernel.getIsA(), kernel.getAgency(),
                                kernel.getCore(), kernel.getCore(), em);
        kernel.getCore().link(kernel.getIsA(), kernel.getAgency(),
                              kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyAgency().link(kernel.getIsA(), kernel.getAgency(),
                                   kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalAgency().link(kernel.getIsA(), kernel.getAgency(),
                                        kernel.getCore(), kernel.getCore(), em);
        kernel.getCoreAnimationSoftware().link(kernel.getIsA(),
                                               kernel.getAgency(),
                                               kernel.getCore(),
                                               kernel.getCore(), em);
        kernel.getPropagationSoftware().link(kernel.getIsA(),
                                             kernel.getAgency(),
                                             kernel.getCore(),
                                             kernel.getCore(), em);
        kernel.getSpecialSystemAgency().link(kernel.getIsA(),
                                             kernel.getAgency(),
                                             kernel.getCore(),
                                             kernel.getCore(), em);
        kernel.getCoreUser().link(kernel.getIsA(), kernel.getAgency(),
                                  kernel.getCore(), kernel.getCore(), em);
        kernel.getSuperUser().link(kernel.getIsA(), kernel.getCoreUser(),
                                   kernel.getCore(), kernel.getCore(), em);
        kernel.getInverseSoftware().link(kernel.getIsA(), kernel.getAgency(),
                                         kernel.getCore(), kernel.getCore(), em);
    }

    public void constructAspects() {
        AgencyAttributeAuthorization loginAuth = new AgencyAttributeAuthorization(
                                                                                  kernel.getIsA(),
                                                                                  kernel.getCoreUser(),
                                                                                  kernel.getLoginAttribute(),
                                                                                  kernel.getCore());
        em.persist(loginAuth);
        AgencyAttributeAuthorization passwordHashAuth = new AgencyAttributeAuthorization(
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

    public void constructProductNetworks() {
        kernel.getProduct().link(kernel.getIsA(), kernel.getProduct(),
                                 kernel.getCore(), kernel.getCore(), em);
        kernel.getAnyProduct().link(kernel.getIsA(), kernel.getProduct(),
                                    kernel.getCore(), kernel.getCore(), em);
        kernel.getOriginalProduct().link(kernel.getIsA(), kernel.getProduct(),
                                         kernel.getCore(), kernel.getCore(), em);
    }
}
