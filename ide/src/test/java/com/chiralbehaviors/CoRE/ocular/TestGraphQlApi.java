/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.ocular;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.Test;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class TestGraphQlApi {

    protected PhantasmApplication application = new PhantasmApplication();

    @After
    public void after() {
        application.stop();
    }

    @Test
    public void testSimple() throws Exception {
        application.run("server", "target/test-classes/null.yml");
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/api/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(WellKnownObject.KERNEL_IRI,
                                                     "UTF-8"));
        GraphQlApi api = new GraphQlApi(webTarget, null);
        ObjectNode data = api.query("query q { coREUsers { id name } }", null);
        assertNotNull(data);
    }

    @Test
    public void testErrors() throws Exception {
        application.run("server", "target/test-classes/null.yml");
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/api/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(WellKnownObject.KERNEL_IRI,
                                                     "UTF-8"));
        GraphQlApi api = new GraphQlApi(webTarget, null);
        try {
            api.query("query q { coreUsers { id name } }", null);
        } catch (QueryException e) {
            assertNotNull(e.getErrors());
            assertEquals(1, e.getErrors()
                             .size());
        }
    }
}
