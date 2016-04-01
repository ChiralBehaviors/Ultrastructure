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

import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author hhildebrand
 *
 */
public class ResourcesTest extends ThingWorkspaceTest {
    protected final static PhantasmApplication application = new PhantasmApplication();

    @BeforeClass
    public static void initialize() throws Exception {
        EntityManagerFactory emf = mockedEmf();
        application.setEmf(emf);
        application.run("server", "target/test-classes/test.yml");
    }

    @AfterClass
    public static void shutdown() {
        application.stop();
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
        JsonUtils.fromInputStream(url.openStream());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFacetNode() throws Exception {
        URL url;
        Map<String, Object> jsonObject;
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing1.setThing2(thing2);
        JsonLdOptions options = new JsonLdOptions();
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s",
                                    application.getPort(),
                                    ((ExistentialRecord) scope.lookup("kernel",
                                                                      "IsA")).getId()
                                                                             .toString(),
                                    ((ExistentialRecord) scope.lookup("Thing1")).getId()
                                                                                .toString(),
                                    thing1.getRuleform()
                                          .getId()));
        jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        URL contextUrl = new URL((String) jsonObject.get(Constants.CONTEXT));
        Map<String, Object> context = (Map<String, Object>) JsonUtils.fromInputStream(contextUrl.openStream());
        JsonLdProcessor.normalize(jsonObject, options);
        JsonLdProcessor.compact(jsonObject, context, options);
        JsonLdProcessor.flatten(jsonObject, context, options);
        JsonLdProcessor.expand(jsonObject, options);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGraphQlCreateAndMutate() throws Exception {
        String[] newAliases = new String[] { "jones", "smith" };
        String newUri = "new iri";

        MavenArtifact artifact1 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
                                                  "core", "core artifact");
        artifact1.setArtifactID("com.chiralbehaviors.CoRE");
        artifact1.setArtifactID("core");
        artifact1.setVersion("0.0.2-SNAPSHOT");
        artifact1.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(String.format("http://localhost:%s/graphql/workspace",
                                                          application.getPort()));
        webTarget = webTarget.path(URLEncoder.encode(THING_URI, "UTF-8"));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);

        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", artifact1.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String!, $description: String, $artifact: String) { CreateThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name } }",
                                                variables);

        Response response = invocationBuilder.post(Entity.entity(request,
                                                                 MediaType.APPLICATION_JSON_TYPE));
        Map<String, Object> result = response.readEntity(Map.class);

        assertEquals(result.get("errors")
                           .toString(),
                     0, ((List<?>) result.get("errors")).size());

        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.get("data")).get("CreateThing1");
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
        request = new QueryRequest("mutation m($id: String!, $name: String!, $artifact: String, $aliases: [String], $uri: String) { UpdateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { id name } }",
                                   variables);
        response = invocationBuilder.post(Entity.entity(request,
                                                        MediaType.APPLICATION_JSON_TYPE));
        result = response.readEntity(Map.class);

        assertEquals(result.get("errors")
                           .toString(),
                     0, ((List<?>) result.get("errors")).size());
        thing1Result = (Map<String, Object>) ((Map<String, Object>) result.get("data")).get("UpdateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
        thing1 = model.wrap(Thing1.class,
                            em.find(Product.class,
                                    UUID.fromString((String) thing1Result.get("id"))));
        assertNotNull(thing1);
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());

        variables = new HashMap<>();
        variables.put("thing1", thing1.getRuleform()
                                      .getId()
                                      .toString());
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        request = new QueryRequest("mutation m($name: String!, $artifact: String, $thing1: String!) { CreateThing2(state: {setName: $name, addDerivedFrom: $artifact, setThing1: $thing1}) { id name } }",
                                   variables);
        response = invocationBuilder.post(Entity.entity(request,
                                                        MediaType.APPLICATION_JSON_TYPE));
        result = response.readEntity(Map.class);

        assertEquals(result.get("errors")
                           .toString(),
                     0, ((List<?>) result.get("errors")).size());
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
        JsonUtils.fromInputStream(url.openStream());
    }

    @Test
    public void testRuleformNode() throws Exception {
        URL url;
        Object jsonObject;
        url = new URL(String.format("http://localhost:%s/json-ld/ruleform/Attribute/%s",
                                    application.getPort(),
                                    ((ExistentialRecord) scope.lookup("URI")).getId()
                                                                             .toString()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        JsonLdOptions options = new JsonLdOptions(String.format("http://localhost:%s/",
                                                                application.getPort()));
        JsonLdProcessor.normalize(jsonObject, options);
        JsonLdProcessor.compact(jsonObject, new HashMap<>(), options);
        JsonLdProcessor.flatten(jsonObject, new HashMap<>(), options);
        JsonLdProcessor.expand(jsonObject, options);
    }

    @Test
    public void testRuleforms() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform",
                                        application.getPort()));
        JsonUtils.fromInputStream(url.openStream());
    }
}
