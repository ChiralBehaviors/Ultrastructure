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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.TokenRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.Role;
import com.chiralbehaviors.CoRE.kernel.phantasm.coreUserProperties.CoreUserProperties;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource.CapabilityRequest;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AuthenticatorsTest extends AbstractModelTest {
    @Test
    public void testBasic() throws Exception {
        AgencyBasicAuthenticator authenticator = new AgencyBasicAuthenticator();
        authenticator.setModel(model);
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        CoreUserProperties props = new CoreUserProperties();
        props.setLogin(username);
        bob.set_Properties(props);
        model.getAuthnModel()
             .create(bob, password.toCharArray());

        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(new BasicCredentials(username,
                                                                                                      password));
        assertFalse(authenticated.isPresent());

        model.flush();
        bob.addRole(model.wrap(Role.class, model.getKernel()
                                                .getLoginRole()));
        authenticated = authenticator.authenticate(new BasicCredentials(username,
                                                                        password));
        assertTrue(authenticated.isPresent());
        assertEquals(bob.getRuleform()
                        .getId(),
                     authenticated.get()
                                  .getPrincipal()
                                  .getId());
    }

    @Test
    public void testNull() throws Exception {
        NullAuthenticator authenticator = new NullAuthenticator();
        authenticator.setModel(model);
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(null);
        assertTrue(authenticated.isPresent());
        assertEquals(model.getKernel()
                          .getUnauthenticatedAgency()
                          .getId(),
                     authenticated.get()
                                  .getPrincipal()
                                  .getId());
    }

    @Test
    public void testBearerToken() throws Exception {
        String username = "bob@slack.com";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        CoreUserProperties props = new CoreUserProperties();
        props.setLogin(username);
        bob.set_Properties(props);

        Credential credential = new Credential();
        credential.ip = "No place like 127.00.1";
        List<UUID> roles = Arrays.asList(model.getKernel()
                                              .getLoginRole())
                                 .stream()
                                 .map(a -> a.getId())
                                 .collect(Collectors.toList());
        credential.roles = roles;

        OffsetDateTime current = OffsetDateTime.now();
        credential.isValid(current, current);

        TokenRecord accessToken = model.getAuthnModel()
                                       .mintToken(bob, credential.ip, 60,
                                                  UUID.randomUUID(), null);

        model.flush();

        AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator();
        authenticator.setModel(model);
        RequestCredentials requestCredentials = new RequestCredentials(credential.ip,
                                                                       accessToken.getId()
                                                                                  .toString());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(requestCredentials);
        assertTrue(authenticated.isPresent());
        AuthorizedPrincipal authBob = authenticated.get();
        assertEquals(bob, authBob.getPrincipal());
        assertEquals(1, authBob.getAsserted()
                               .size());
        assertEquals(model.getKernel()
                          .getLoginRole(),
                     authBob.getAsserted()
                            .get(0));

        requestCredentials = new RequestCredentials("No place like HOME",
                                                    accessToken.getId()
                                                               .toString());
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());

        requestCredentials = new RequestCredentials(credential.ip,
                                                    accessToken.getId()
                                                               .toString());
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());

        requestCredentials = new RequestCredentials(credential.ip,
                                                    "No place like HOME");
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());
    }

    @Test
    public void testAuthRoundTrip() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        CoreUserProperties props = new CoreUserProperties();
        props.setLogin(username);
        bob.set_Properties(props);

        model.getAuthnModel()
             .create(bob, password.toCharArray());
        bob.addRole(model.wrap(Role.class, model.getKernel()
                                                .getLoginRole()));

        HttpServletRequest request = mock(HttpServletRequest.class);
        String ip = "There's no place like 127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ip);

        UUID authToken = AuthxResource.loginUuidForToken(username, password,
                                                         request, model);
        assertNotNull(authToken);

        AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator();
        authenticator.setModel(model);
        RequestCredentials credential = new RequestCredentials(ip,
                                                               authToken.toString());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(credential);
        assertTrue(authenticated.isPresent());
        AuthorizedPrincipal authBob = authenticated.get();
        assertEquals(bob, authBob.getPrincipal());
        FacetRecord asserted = model.getPhantasmModel()
                                    .getFacetDeclaration(model.getKernel()
                                                              .getIsA(),
                                                         model.getKernel()
                                                              .getCoreUser());

        CapabilityRequest capRequest = new CapabilityRequest();
        capRequest.username = username;
        capRequest.password = password;
        capRequest.capabilities = Arrays.asList(asserted.getId());
        authToken = AuthxResource.requestCapability(capRequest, request, model);

        credential = new RequestCredentials(ip, authToken.toString());
        authenticated = authenticator.authenticate(credential);
        assertTrue(authenticated.isPresent());
        authBob = authenticated.get();

        assertEquals(bob, authBob.getPrincipal());

        assertEquals(authBob.getAsserted()
                            .stream()
                            .map(c -> c.getName())
                            .collect(Collectors.toList())
                            .toString(),
                     1, authBob.getAsserted()
                               .size());
        assertEquals(model.getKernel()
                          .getLoginRole()
                          .getId(),
                     authBob.getAsserted()
                            .get(0)
                            .getId());
    }
}
