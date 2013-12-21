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

package com.hellblazer.CoRE.authentication;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.hellblazer.CoRE.Util;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.access.AgencyAttribute;
import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.BootstrapLoader;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.security.AuthenticatedPrincipal;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AgencyLoginTest {
    private Model         model;
    private EntityManager em;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream("/jpa.properties");
        Properties properties = new Properties();
        properties.load(is);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);
        em = emf.createEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        loader.bootstrap();
        em.getTransaction().commit();

        model = new ModelImpl(em);
    }

    @Test
    public void testGoodUser() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        em.getTransaction().begin();
        Aspect<Agency> loginAspect = new Aspect<Agency>(
                                                        model.getKernel().getIsA(),
                                                        model.getKernel().getCoreUser());
        @SuppressWarnings("unchecked")
        Agency bob = model.getAgencyModel().create("Bob", "Test Dummy",
                                                   loginAspect);
        em.persist(bob);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Facet<Agency, AgencyAttribute> loginFacet = model.getAgencyModel().getFacet(bob,
                                                                                    loginAspect);

        AgencyAttribute usernameValue = loginFacet.getValue(model.getKernel().getLoginAttribute());
        assertNotNull(usernameValue);
        AgencyAttribute passwordHashValue = loginFacet.getValue(model.getKernel().getPasswordHashAttribute());
        assertNotNull(passwordHashValue);
        usernameValue.setTextValue(username);
        passwordHashValue.setTextValue(Util.md5Hash(password));
        em.getTransaction().commit();

        AgencyAuthenticator authenticator = new AgencyAuthenticator(model);
        Optional<AuthenticatedPrincipal> authenticated = authenticator.authenticate(new BasicCredentials(
                                                                                                         username,
                                                                                                         password));
        assertNotNull(authenticated.get());
        assertEquals(bob, authenticated.get().getPrincipal());
    }
}
