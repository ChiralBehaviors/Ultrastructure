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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.dropwizard.auth.basic.BasicCredentials;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.chiralbehaviors.CoRE.utils.Util;
import com.google.common.base.Optional;

/**
 * @author hhildebrand
 * 
 */
public class AgencyLoginTest extends AbstractModelTest {
    @Test
    public void testGoodUser() throws Exception {
        em.getTransaction().begin();
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        Aspect<Agency> loginAspect = new Aspect<Agency>(
                                                        model.getKernel().getIsA(),
                                                        model.getKernel().getCoreUser());
        Agency bob = model.getAgencyModel().create("Bob", "Test Dummy",
                                                   loginAspect).asRuleform();
        em.persist(bob);
        em.flush();
        em.clear();
        Facet<Agency, AgencyAttribute> loginFacet = model.getAgencyModel().getFacet(bob,
                                                                                    loginAspect);

        AgencyAttribute usernameValue = loginFacet.getValue(model.getKernel().getLoginAttribute());
        assertNotNull(usernameValue);
        AgencyAttribute passwordHashValue = loginFacet.getValue(model.getKernel().getPasswordHashAttribute());
        assertNotNull(passwordHashValue);
        usernameValue.setTextValue(username);
        passwordHashValue.setTextValue(Util.md5Hash(password));
        em.flush();

        AgencyAuthenticator authenticator = new AgencyAuthenticator(model);
        Optional<AuthenticatedPrincipal> authenticated = authenticator.authenticate(new BasicCredentials(
                                                                                                         username,
                                                                                                         password));
        assertNotNull(authenticated.get());
        assertEquals(bob, authenticated.get().getPrincipal());
    }
}
