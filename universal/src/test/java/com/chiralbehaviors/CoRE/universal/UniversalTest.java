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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.service.NAVI;
import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.util.Pair;

/**
 * @author halhildebrand
 *
 */
public class UniversalTest {
    static Model model;

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
    private Client    client;

    @After
    public void after() {
        application.stop();
        if (client != null) {
            client.close();
        }
    }

    @Before
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void testLauncher() throws Exception {
        application.run("server", "target/test-classes/basic.yml");
        String endpoint = String.format("http://localhost:%s/api/workspace",
                                        application.getPort());
        AtomicReference<Universal> launched = new AtomicReference<>();
        AtomicReference<Pair<Context, JsonNode>> displayed = new AtomicReference<>();
        WebTarget target = client.target(new URI(endpoint));
        Universal universal = new Universal(Universal.SPA_WSP, target);
        universal.setLauncher(u -> launched.set(u));
        universal.setDisplay((c, n) -> displayed.set(new Pair<>(c, n)));
        assertNotNull(universal.getApplication()
                               .getRoot());
        universal.places();
        JsonNode data = universal.evaluate();
        assertNotNull(data);
        assertEquals(1, data.size());
        universal.navigate(data.get(0), new Relation("singlePageApplications"));
        assertNotNull(launched.get());
        assertNotEquals(universal, launched.get());
    }

    @Test
    public void testSmokeApp() throws Exception {
        application.run("server", "target/test-classes/basic.yml");
        String endpoint = String.format("http://localhost:%s/api/workspace",
                                        application.getPort());
        AtomicReference<Universal> launched = new AtomicReference<>();
        AtomicReference<Pair<Context, JsonNode>> displayed = new AtomicReference<>();
        WebTarget target = client.target(new URI(endpoint));
        Spa spa = Spa.manifest("/smoke.app");
        assertNotNull(spa);
        Universal universal = new Universal(Universal.SPA_WSP, spa, target);
        universal.setLauncher(u -> launched.set(u));
        universal.setDisplay((c, n) -> displayed.set(new Pair<>(c, n)));
        assertNotNull(universal.getApplication()
                               .getRoot());
        universal.places();
        JsonNode data = universal.evaluate();
        assertNotNull(data);
        assertEquals(2, data.size());
        universal.navigate(data.get(0), new Relation("workspaces"));
        assertNull(launched.get());
        assertNotNull(displayed.get());
        data = universal.evaluate();
        assertNotNull(data);
    }
}
