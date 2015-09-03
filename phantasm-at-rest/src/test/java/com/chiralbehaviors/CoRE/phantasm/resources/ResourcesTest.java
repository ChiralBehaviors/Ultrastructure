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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.resource.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource.QueryRequest;
import com.chiralbehaviors.CoRE.phantasm.resources.test.TestApplication;
import com.chiralbehaviors.CoRE.product.Product;
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
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction()
          .commit();
        application.run("server", "target/test-classes/test.yml");
    }

    @AfterClass
    public static void shutdown() {
        application.stop();
    }

    protected WorkspaceScope scope;

    @Before
    public void loadWorkspace() {
        scope = model.getWorkspaceModel()
                     .getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
    }

    @Test
    public void testFacetContext() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/context",
                                        application.getPort(),
                                        scope.lookup("kernel", "IsA")
                                             .getId()
                                             .toString(),
                                        scope.lookup("Thing1")
                                             .getId()
                                             .toString()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println("Thing1 facet context");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFacetNode() throws Exception {
        em.getTransaction()
          .begin();
        URL url;
        Map<String, Object> jsonObject;
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing1.setThing2(thing2);
        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        JsonLdOptions options = new JsonLdOptions();
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s",
                                    application.getPort(),
                                    scope.lookup("kernel", "IsA")
                                         .getId()
                                         .toString(),
                                    scope.lookup("Thing1")
                                         .getId()
                                         .toString(),
                                    thing1.getRuleform()
                                          .getId()));
        jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println("Node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed;
        URL contextUrl = new URL((String) jsonObject.get(Constants.CONTEXT));
        Map<String, Object> context = (Map<String, Object>) JsonUtils.fromInputStream(contextUrl.openStream());
        processed = JsonLdProcessor.normalize(jsonObject, options);
        System.out.println("Normalized node value selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, context, options);
        System.out.println("Compacted node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, context, options);
        System.out.println("Flattened node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, options);
        System.out.println("Expanded node value of an instance of Thing1");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testSelect() throws Exception {
        em.getTransaction()
          .begin();
        URL url;
        Object jsonObject;
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing2.setThing1(thing1);
        thing1.setThing2(thing2);
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        artifact.setType("jar");
        thing2.addDerivedFrom(artifact);
        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        JsonLdOptions options = new JsonLdOptions(String.format("http://localhost:%s/json-ld/facet",
                                                                application.getPort()));
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s?select=thing2/derivedFroms;a=description;a=name",
                                    application.getPort(),
                                    scope.lookup("kernel", "IsA")
                                         .getId()
                                         .toString(),
                                    scope.lookup("Thing1")
                                         .getId()
                                         .toString(),
                                    thing1.getRuleform()
                                          .getId()));

        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println("Node value of selection");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed = JsonLdProcessor.normalize(jsonObject, options);
        System.out.println("Normalized node value selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Compacted node value of selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Flattened node value of selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, options);
        System.out.println("Expanded node value of selection");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testInstancesSelect() throws Exception {
        em.getTransaction()
          .begin();
        URL url;
        Object jsonObject;
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing2.setThing1(thing1);
        thing1.setThing2(thing2);
        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        JsonLdOptions options = new JsonLdOptions(String.format("http://localhost:%s/json-ld/facet",
                                                                application.getPort()));
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/instances?select=;a=URI",
                                    application.getPort(),
                                    scope.lookup("kernel", "IsA")
                                         .getId()
                                         .toString(),
                                    scope.lookup("Thing1")
                                         .getId()
                                         .toString()));

        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        System.out.println("Node value of instances selection");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed = JsonLdProcessor.normalize(jsonObject, options);
        System.out.println("Normalized node value instances selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Compacted node value of instances selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Flattened node value of instances selection");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, options);
        System.out.println("Expanded node value of instances selection");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testLookupWorkspace() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/workspace/%s",
                                        application.getPort(),
                                        scope.getWorkspace()
                                             .getDefiningProduct()
                                             .getId()
                                             .toString()));
        Map<?, ?> jsonObject = (Map<?, ?>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject.get("ruleforms"));
        assertNotNull(jsonObject.get("frontier"));
    }

    @Test
    public void testRuleformContext() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform/Attribute/context",
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
                                    application.getPort(), scope.lookup("URI")
                                                                .getId()
                                                                .toString()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        JsonLdOptions options = new JsonLdOptions(String.format("http://localhost:%s/",
                                                                application.getPort()));
        System.out.println("Node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
        Object processed = JsonLdProcessor.normalize(jsonObject, options);
        System.out.println("Normalized node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.compact(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Compacted node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.flatten(jsonObject, new HashMap<>(),
                                            options);
        System.out.println("Flattened node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
        processed = JsonLdProcessor.expand(jsonObject, options);
        System.out.println("Expanded node value of an attribute");
        System.out.println(JsonUtils.toPrettyString(processed));
    }

    @Test
    public void testRuleforms() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform",
                                        application.getPort()));
        Object jsonObject = JsonUtils.fromInputStream(url.openStream());
        System.out.println("Ruleform types:");
        System.out.println(JsonUtils.toPrettyString(jsonObject));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGraphQlCreateAndMutate() throws Exception {
        em.getTransaction()
          .begin();
        String[] newAliases = new String[] { "jones", "smith" };
        String newUri = "new iri";

        MavenArtifact artifact1 = model.construct(MavenArtifact.class, "core",
                                                  "core artifact");
        artifact1.setArtifactID("com.chiralbehaviors.CoRE");
        artifact1.setArtifactID("core");
        artifact1.setVersion("0.0.2-SNAPSHOT");
        artifact1.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        em.getTransaction()
          .commit();
        em.getTransaction()
          .begin();
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/graphql/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(TEST_SCENARIO_URI,
                                                     "UTF-8"));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);

        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", artifact1.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String, $description: String, $artifact: String) { CreateThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name } }",
                                                variables);

        Response response = invocationBuilder.post(Entity.entity(request,
                                                                 MediaType.APPLICATION_JSON_TYPE));
        Map<String, Object> result = response.readEntity(Map.class);

        assertNull(result.get("errors") == null ? "" : result.get("errors")
                                                             .toString(),
                   result.get("errors"));

        Map<String, Object> thing1Result = (Map<String, Object>) result.get("CreateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
        Thing1 thing1 = model.wrap(Thing1.class,
                                   em.find(Product.class,
                                           UUID.fromString((String) thing1Result.get("id"))));
        assertNotNull(thing1);
        assertEquals(artifact1, thing1.getDerivedFrom());

        variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("aliases", Arrays.asList(newAliases));
        variables.put("name", "hello");
        variables.put("uri", newUri);
        request = new QueryRequest("mutation m($id: String, $name: String, $artifact: String, $aliases: [String], $uri: String) { UpdateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { id name } }",
                                   variables);
        response = invocationBuilder.post(Entity.entity(request,
                                                        MediaType.APPLICATION_JSON_TYPE));
        result = response.readEntity(Map.class);

        assertNull(result.get("errors"));
        thing1Result = (Map<String, Object>) result.get("UpdateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
        thing1 = model.wrap(Thing1.class,
                            em.find(Product.class,
                                    UUID.fromString((String) thing1Result.get("id"))));
        assertNotNull(thing1);
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());
    }
}
