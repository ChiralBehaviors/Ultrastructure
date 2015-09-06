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

import org.junit.Test;

import com.chiralbehaviors.CoRE.kernel.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AgencyLoginTest extends AbstractModelTest {
    @Test
    public void testGoodUser() throws Exception {
        em.getTransaction()
          .begin();
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
        assertNotNull(authenticated.get());
        assertEquals(bob, authenticated.get()
                                       .getPrincipal());
    }
}
