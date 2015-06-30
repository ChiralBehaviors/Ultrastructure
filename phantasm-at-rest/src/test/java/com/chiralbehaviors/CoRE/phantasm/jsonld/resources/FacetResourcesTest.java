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

package com.chiralbehaviors.CoRE.phantasm.jsonld.resources;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.test.TestApplication;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author hhildebrand
 *
 */
public class FacetResourcesTest extends AbstractModelTest {

    private static final String     TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";
    protected WorkspaceScope        scope;
    protected final TestApplication application       = new TestApplication();

    @Before
    public void initialize() throws Exception {
        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(FacetResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction().commit();

        em.getTransaction().begin();
        scope = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
        application.run("server", "target/test-classes/test.yml");
    }

    @After
    public void shutdown() {
        application.stop();
    }

    @Test
    public void testContext() throws Exception {
        Thing1 thing1 = (Thing1) model.construct(Thing1.class, "test", "testy");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        em.getTransaction().commit();
        em.getTransaction().begin();
        URL url = new URL(String.format("http://localhost:%s/json-ld/facet/context/product/%s/%s",
                                        application.getPort(),
                                        scope.lookup("kernel",
                                                     "IsA").getId().toString(),
                                        scope.lookup("Thing1").getId().toString()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object compact = JsonLdProcessor.normalize(jsonObject);
        System.out.println(JsonUtils.toPrettyString(compact));

        url = new URL(String.format("http://localhost:%s/json-ld/facet/node/product/%s/%s/%s",
                                    application.getPort(),
                                    thing1.getRuleform().getId(),
                                    scope.lookup("kernel",
                                                 "IsA").getId().toString(),
                                    scope.lookup("Thing1").getId().toString()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        compact = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                          new JsonLdOptions());
        System.out.println(JsonUtils.toPrettyString(compact));
    }
}
