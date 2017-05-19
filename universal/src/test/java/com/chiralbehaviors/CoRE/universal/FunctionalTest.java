/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.universal;

import static com.chiralbehaviors.CoRE.universal.Universal.GET_APPLICATIONS_QUERY_RESOURCE;
import static com.chiralbehaviors.CoRE.universal.Universal.GET_APPLICATION_QUERY_RESOURCE;
import static com.chiralbehaviors.CoRE.universal.Universal.SINGLE_PAGE_APPLICATION;
import static com.chiralbehaviors.CoRE.universal.Universal.SINGLE_PAGE_APPLICATIONS;
import static com.chiralbehaviors.CoRE.universal.Universal.SINGLE_PAGE_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.service.NAVI;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

/**
 * @author halhildebrand
 *
 */
public class FunctionalTest {
    static Model model;
    private static final String APPLICATION_QUERY;
    
    static {
        try {
            APPLICATION_QUERY = Utils.getDocument(Universal.class.getResourceAsStream(GET_APPLICATION_QUERY_RESOURCE));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

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
        // Commit is required
        model.create()
             .configuration()
             .connectionProvider()
             .acquire()
             .commit();
    }

    protected NAVI<?> application = new NAVI<>();

    @After
    public void after() {
        application.stop();
    }

    @Test
    public void testLiveStructureParsing() throws Exception {
        application.run("server", "target/test-classes/basic.yml");

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/api/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(SINGLE_PAGE_UUID.toString(),
                                                     "UTF-8"));
        Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        QueryRequest request = new QueryRequest(Utils.getDocument(Universal.class.getResourceAsStream(GET_APPLICATIONS_QUERY_RESOURCE)));

        ObjectNode response = invocationBuilder.post(Entity.entity(request,
                                                                   MediaType.APPLICATION_JSON_TYPE),
                                                     ObjectNode.class);
        assertNotNull(response);
        ArrayNode applications = (ArrayNode) response.get("data")
                                                     .get(SINGLE_PAGE_APPLICATIONS);
        assertEquals(1, applications.size());
        String appLauncher = applications.get(0)
                                         .get("id")
                                         .asText();
        assertNotNull(appLauncher);

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", appLauncher);
        request = new QueryRequest(APPLICATION_QUERY, variables);
        response = invocationBuilder.post(Entity.entity(request,
                                                        MediaType.APPLICATION_JSON_TYPE),
                                          ObjectNode.class);
        assertNotNull(response);
        ObjectNode app = (ObjectNode) response.get("data")
                                              .get(SINGLE_PAGE_APPLICATION);
        assertNotNull(app);
        Spa spa = new Spa(app);
        assertNotNull(spa.getRoot());
    }
}
