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

package com.chiralbehaviors.CoRE.authentication;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.access.AgencyAttribute;
import com.chiralbehaviors.CoRE.authentication.AgencyAuthenticator;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject;
import com.chiralbehaviors.CoRE.meta.BootstrapLoader;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.chiralbehaviors.CoRE.utils.Util;
import com.google.common.base.Optional;
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
