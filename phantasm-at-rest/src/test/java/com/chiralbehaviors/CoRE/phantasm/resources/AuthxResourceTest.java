/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource.CapabilityRequest;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;

/**
 * @author hhildebrand
 *
 */
public class AuthxResourceTest extends AbstractModelTest {
    protected final static PhantasmApplication application = new PhantasmApplication();

    @BeforeClass
    public static void initialize() throws Exception {
        EntityManagerFactory emf = mockedEmf();
        application.setEmf(emf);
        application.run("server", "target/test-classes/oauth.yml");
    }

    @AfterClass
    public static void shutdown() {
        application.stop();
    }

    @Test
    public void functionalAuthRoundTripTest() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class, "Bob",
                                                  "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);

        em.flush();
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/oauth2/token/login",
                                                          application.getPort()));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Form creds = new Form();
        creds.param("username", username);
        creds.param("password", password);
        UUID token = invocationBuilder.post(Entity.entity(creds,
                                                          MediaType.APPLICATION_FORM_URLENCODED),
                                            UUID.class);
        System.out.println(token);
        webTarget = client.target(String.format("http://localhost:%s/graphql/workspace",
                                                application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(Ruleform.KERNEL_IRI,
                                                     "UTF-8"));
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        QueryRequest request = new QueryRequest("query q { InstancesOfCoreUser { id name } }");

        @SuppressWarnings("rawtypes")
        Map response;
        try {
            response = invocationBuilder.post(Entity.entity(request,
                                                            MediaType.APPLICATION_JSON_TYPE),
                                              Map.class);
            fail("Should not have succeeded, expecting 401");
        } catch (NotAuthorizedException e) {
            // expected;
        }
        invocationBuilder.header(HttpHeaders.AUTHORIZATION,
                                 String.format("Bearer %s", token));
        response = invocationBuilder.post(Entity.json(request), Map.class);
        assertNotNull(response);

        webTarget = client.target(String.format("http://localhost:%s/oauth2/token/deauthorize",
                                                application.getPort()));
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response resp = invocationBuilder.post(null);
        assertEquals(401, resp.getStatus());

        invocationBuilder.header(HttpHeaders.AUTHORIZATION,
                                 String.format("Bearer %s", token));
        resp = invocationBuilder.post(null);
        assertEquals(204, resp.getStatus());

        resp = invocationBuilder.post(null);
        assertEquals(401, resp.getStatus());

        NetworkAuthorization<Agency> asserted = model.getAgencyModel()
                                                     .getFacetDeclaration(new Aspect<>(kernel.getIsA(),
                                                                                       kernel.getCoreUser()));

        CapabilityRequest capReq = new CapabilityRequest();
        capReq.username = username;
        capReq.password = password;
        capReq.capabilities = Arrays.asList(asserted.getId());

        webTarget = client.target(String.format("http://localhost:%s/oauth2/token/capability",
                                                application.getPort()));
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        token = invocationBuilder.post(Entity.json(capReq), UUID.class);

        webTarget = client.target(String.format("http://localhost:%s/oauth2/token/deauthorize",
                                                application.getPort()));
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        invocationBuilder.header(HttpHeaders.AUTHORIZATION,
                                 String.format("Bearer %s", token));
        resp = invocationBuilder.post(null);
        assertEquals(204, resp.getStatus());
    }
}
