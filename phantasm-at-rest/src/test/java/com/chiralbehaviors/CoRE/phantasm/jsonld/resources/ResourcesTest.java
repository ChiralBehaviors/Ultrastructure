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

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.test.TestApplication;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author hhildebrand
 *
 */
public class ResourcesTest extends AbstractModelTest {

    private static final String            TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";
    protected final static TestApplication application       = new TestApplication();

    @BeforeClass
    public static void initialize() throws Exception {
        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction().commit();
        application.run("server", "target/test-classes/test.yml");
    }

    @AfterClass
    public static void shutdown() {
        application.stop();
    }

    protected WorkspaceScope scope;

    @Before
    public void loadWorkspace() {
        em.getTransaction().begin();
        scope = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
    }

    @Test
    public void testFacetContext() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/facet/context/Product/%s/%s",
                                        application.getPort(),
                                        scope.lookup("kernel",
                                                     "IsA").getId().toString(),
                                        scope.lookup("Thing1").getId().toString()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println("Thing1 facet context");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
    }

    @Test
    public void testFacetNode() throws Exception {
        URL url;
        Object jsonObject;
        Thing1 thing1 = (Thing1) model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = (Thing2) model.construct(Thing2.class, "tester",
                                                 "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing1.setThing2(thing2);
        em.getTransaction().commit();
        em.getTransaction().begin();
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s",
                                    application.getPort(),
                                    scope.lookup("kernel",
                                                 "IsA").getId().toString(),
                                    scope.lookup("Thing1").getId().toString(),
                                    thing1.getRuleform().getId()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println("Node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed = JsonLdProcessor.normalize(jsonObject);
        System.out.println("Normalized node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, new HashMap<>(),
                                            new JsonLdOptions());
        System.out.println("Compacted node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                            new JsonLdOptions());
        System.out.println("Flattened node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, new JsonLdOptions());
        System.out.println("Expanded node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testRuleformContext() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform/context/Attribute",
                                        application.getPort()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println("Attribute @context");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
    }

    @Test
    public void testRuleformNode() throws Exception {
        URL url;
        Object jsonObject;
        url = new URL(String.format("http://localhost:%s/json-ld/ruleform/Attribute/%s",
                                    application.getPort(),
                                    scope.lookup("URI").getId().toString()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println("Node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed = JsonLdProcessor.normalize(jsonObject);
        System.out.println("Normalized node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, new HashMap<>(),
                                            new JsonLdOptions());
        System.out.println("Compacted node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                            new JsonLdOptions());
        System.out.println("Flattened node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, new JsonLdOptions());
        System.out.println("Expanded node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testFrame() throws Exception {
        Thing1 thing1 = (Thing1) model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = (Thing2) model.construct(Thing2.class, "tester",
                                                 "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing1.setThing2(thing2);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Aspect<Product> aspect = new Aspect<>(scope.lookup("kernel", "IsA"),
                                              (Product) scope.lookup("Thing1"));
        Map<String, String> properties = new HashMap<>();
        properties.put("context",
                       new URL(String.format("http://localhost:%s/json-ld/facet/context/Product/%s/%s#is-a:Thing1",
                                             application.getPort(),
                                             aspect.getClassifier().getId().toString(),
                                             aspect.getClassification().getId().toString())).toExternalForm());
        properties.put("thing1.type",
                       new URL(String.format("http://localhost:%s/json-ld/facet/type/Product/%s/%s#is-a:Thing1",
                                             application.getPort(),
                                             aspect.getClassifier().getId().toString(),
                                             scope.lookup("Thing1").getId().toString())).toExternalForm());
        properties.put("thing2.type",
                       new URL(String.format("http://localhost:%s/json-ld/facet/type/Product/%s/%s#is-a:Thing2",
                                             application.getPort(),
                                             aspect.getClassifier().getId().toString(),
                                             scope.lookup("Thing2").getId().toString())).toExternalForm());
        Object frame = JsonUtils.fromInputStream(new ByteArrayInputStream(com.hellblazer.utils.Utils.getDocument(getClass().getResourceAsStream("/thing-frame.jsonld"),
                                                                                                                 properties).getBytes()));
        assertNotNull(frame);
        System.out.println("frame: ");
        System.out.println(JsonUtils.toPrettyString(frame));
        URL url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s",
                                        application.getPort(),
                                        aspect.getClassifier().getId().toString(),
                                        aspect.getClassification().getId().toString(),
                                        thing1.getRuleform().getId()));
        Object node = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(node);
        System.out.println("original: ");
        System.out.println(JsonUtils.toPrettyString(node));
        JsonLdOptions opts = new JsonLdOptions();
        opts.setEmbed(true);
        Map<String, Object> framed = JsonLdProcessor.frame(node, frame, opts);
        System.out.println("framed: ");
        System.out.println(JsonUtils.toPrettyString(framed));
    }

    @Test
    public void testRuleforms() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform",
                                        application.getPort()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println("Ruleform types:");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
    }

    @Test
    public void testLookupWorkspace() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/workspace/%s",
                                        application.getPort(),
                                        scope.getWorkspace().getDefiningProduct().getId().toString()));
        Map<?, ?> jsonObject = (Map<?, ?>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject.get("auths"));
        assertNotNull(jsonObject.get("frontier"));
    }
}
