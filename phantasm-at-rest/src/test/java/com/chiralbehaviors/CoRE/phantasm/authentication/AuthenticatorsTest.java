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

package com.chiralbehaviors.CoRE.phantasm.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.kernel.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.security.Credential;
import com.google.common.base.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AuthenticatorsTest extends AbstractModelTest {
    @Test
    public void testBasic() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class, "Bob",
                                                  "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);

        em.flush();
        em.clear();

        AgencyBasicAuthenticator authenticator = new AgencyBasicAuthenticator(mockedEmf());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(new BasicCredentials(username,
                                                                                                      password));
        assertTrue(authenticated.isPresent());
        assertEquals(bob, authenticated.get()
                                       .getPrincipal());
    }

    @Test
    public void testBearerToken() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class, "Bob",
                                                  "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);
        Credential credential = new Credential();
        credential.ip = "No place like 127.00.1";
        AgencyAttribute accessToken = new AgencyAttribute(kernel.getCore());
        accessToken.setAttribute(kernel.getAccessToken());
        accessToken.setAgency(bob.getRuleform());
        accessToken.setValue(credential);
        em.persist(accessToken);
        em.flush();
        em.clear();

        AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator(mockedEmf());
        RequestCredentials requestCredentials = new RequestCredentials(credential.ip,
                                                                       accessToken.getId()
                                                                                  .toString());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(requestCredentials);
        assertNotNull(authenticated.get());
        assertTrue(authenticated.isPresent());
        assertEquals(bob, authenticated.get()
                                       .getPrincipal());
    }
}
