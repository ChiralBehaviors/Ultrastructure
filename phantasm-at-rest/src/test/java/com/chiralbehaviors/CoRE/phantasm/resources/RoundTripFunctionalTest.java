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
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.graphql.QueryRequest;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource.CapabilityRequest;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.BaseEncoding;

/**
 * @author hhildebrand
 *
 */
public class RoundTripFunctionalTest {
    private static final String PASSWORD = "give me food or give me slack or kill me";

    private static final String USER     = "bob@slack.com";

    static Model                model;

    @AfterClass
    public static void clearDb() throws Exception {
        try {
            RecordsFactory.clear(model.create());
        } finally {
            model.close();
        }
    }

    @BeforeClass
    public static void initializeDb() throws Exception {
        Connection connection = AbstractModelTest.newConnection();
        KernelUtil.clearAndLoadKernel(PostgresDSL.using(connection));
        model = new ModelImpl(connection);
        KernelUtil.initializeInstance(model,
                                      "Abstract Model Test CoRE Instance",
                                      "CoRE instance for an Abstract Model Test");
        String username = USER;
        String password = PASSWORD;
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .commit();
    }

    protected PhantasmApplication application = new PhantasmApplication();

    @After
    public void after() {
        application.stop();
    }

    @Test
    public void functionalBasicAuthRoundTripTest() throws Exception {
        application.run("server", "target/test-classes/basic.yml");
        String creds = BaseEncoding.base64()
                                   .encode(String.format("%s:%s", USER,
                                                         PASSWORD)
                                                 .getBytes());

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(WellKnownObject.KERNEL_IRI,
                                                     "UTF-8"));
        Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
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
                                 String.format("Basic %s", creds));
        response = invocationBuilder.post(Entity.json(request), Map.class);
        assertNotNull(response);
    }

    @Test
    public void functionalBearerAuthRoundTripTest() throws Exception {
        application.run("server", "target/test-classes/oauth.yml");

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/oauth2/token/login",
                                                          application.getPort()));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Form creds = new Form();
        creds.param("username", USER);
        creds.param("password", PASSWORD);
        UUID token = invocationBuilder.post(Entity.entity(creds,
                                                          MediaType.APPLICATION_FORM_URLENCODED),
                                            UUID.class);
        webTarget = client.target(String.format("http://localhost:%s/workspace",
                                                application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(WellKnownObject.KERNEL_IRI,
                                                     "UTF-8"));
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        QueryRequest request = new QueryRequest("query q { InstancesOfCoreUser { id name } }");

        ObjectNode response;
        try {
            response = invocationBuilder.post(Entity.entity(request,
                                                            MediaType.APPLICATION_JSON_TYPE),
                                              ObjectNode.class);
            fail("Should not have succeeded, expecting 401");
        } catch (NotAuthorizedException e) {
            // expected;
        }
        invocationBuilder.header(HttpHeaders.AUTHORIZATION,
                                 String.format("Bearer %s", token));
        response = invocationBuilder.post(Entity.json(request),
                                          ObjectNode.class);
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

        FacetRecord asserted = model.getPhantasmModel()
                                    .getFacetDeclaration(model.getKernel()
                                                              .getIsA(),
                                                         model.getKernel()
                                                              .getCoreUser());

        CapabilityRequest capReq = new CapabilityRequest();
        capReq.username = USER;
        capReq.password = PASSWORD;
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

    @Test
    public void functionalNullAuthRoundTripTest() throws Exception {
        application.run("server", "target/test-classes/null.yml");

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(WellKnownObject.KERNEL_IRI,
                                                     "UTF-8"));
        Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        QueryRequest request = new QueryRequest("query q { InstancesOfCoreUser { id name } }");

        @SuppressWarnings("rawtypes")
        Map response = invocationBuilder.post(Entity.json(request), Map.class);
        assertNotNull(response);
    }

    @After
    public void shutdown() {
        application.stop();
    }
}
