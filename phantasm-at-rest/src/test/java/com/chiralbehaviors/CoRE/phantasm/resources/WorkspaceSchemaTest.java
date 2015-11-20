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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import org.junit.Test;

import com.chiralbehaviors.CoRE.kernel.phantasm.product.Argument;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Constructor;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.InstanceMethod;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Plugin;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Workspace;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.resource.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing3;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource.QueryRequest;
import com.chiralbehaviors.CoRE.phantasm.resources.plugin.Thing1_Plugin;
import com.chiralbehaviors.CoRE.product.Product;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchemaTest extends ThingWorkspaceTest {

    private static final String INTROSPECTION_QUERY = "\n  query IntrospectionQuery "
                                                      + "{\n    __schema "
                                                      + "{\n      queryType { name }\n      mutationType { name }\n      "
                                                      + "types {\n        ...FullType\n      }\n      "
                                                      + "directives {\n        name\n        description\n        "
                                                      + "args {\n          ...InputValue\n        }\n        "
                                                      + "onOperation\n        onFragment\n        onField\n      }\n    }\n  }\n\n  "
                                                      + "fragment FullType on __Type {\n    kind\n    name\n    description\n    "
                                                      + "fields {\n      name\n      description\n      args {\n        ...InputValue\n      }\n      "
                                                      + "type {\n        ...TypeRef\n      }\n      isDeprecated\n      deprecationReason\n    }\n    "
                                                      + "inputFields {\n      ...InputValue\n    }\n    "
                                                      + "interfaces {\n      ...TypeRef\n    }\n    "
                                                      + "enumValues {\n      name\n      description\n      isDeprecated\n      deprecationReason\n    }\n    "
                                                      + "possibleTypes {\n      ...TypeRef\n    }\n  }\n\n  "
                                                      + "fragment InputValue on __InputValue {\n    name\n    description\n    "
                                                      + "type { ...TypeRef }\n    defaultValue\n  }\n\n  "
                                                      + "fragment TypeRef on __Type {\n    kind\n    name\n    "
                                                      + "ofType {\n      kind\n      name\n      "
                                                      + "ofType {\n        kind\n        name\n        "
                                                      + "ofType {\n          kind\n          name\n        }\n      }\n    }\n  }\n";

    @Test
    public void testCasting() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, "Thingy",
                                        "a favorite thing");
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);

        EntityManagerFactory mockedEmf = mockedEmf();

        GraphQlResource resource = new GraphQlResource(mockedEmf,
                                                       getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        variables.put("thing3", thing3.getRuleform()
                                      .getId()
                                      .toString());
        QueryRequest request = new QueryRequest("mutation m($id: String!, $thing3: String!) { UpdateThing1(state: { id: $id, setThing2: $thing3}) { name } }",
                                                variables);
        ExecutionResult result = resource.query(null, TEST_SCENARIO_URI,
                                                request);
        assertNotNull(result);

        assertEquals(result.getErrors()
                           .toString(),
                     1, result.getErrors()
                              .size());
        assertTrue(result.getErrors()
                         .get(0)
                         .getMessage()
                         .contains("ClassCastException"));
    }

    @Test
    public void testCreate() throws Exception {
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, "Thingy",
                                        "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class, "model",
                                                 "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        thing2.addThing3(thing3);

        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        EntityManagerFactory mockedEmf = mockedEmf();

        GraphQlResource resource = new GraphQlResource(mockedEmf,
                                                       getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String!, $description: String, $artifact: String) { CreateThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name } }",
                                                variables);
        ExecutionResult result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .toString());
            return;
        }
        assertNotNull(result);

        assertEquals(result.getErrors()
                           .toString(),
                     0, result.getErrors()
                              .size());
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("CreateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));

        // assertEquals(artifact2, thing1.getDerivedFrom());
    }

    @Test
    public void testGraphQlResource() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, "Thingy",
                                        "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class, "model",
                                                 "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setDerivedFrom(artifact);
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);

        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        EntityManagerFactory mockedEmf = mockedEmf();

        GraphQlResource resource = new GraphQlResource(mockedEmf,
                                                       getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        QueryRequest request = new QueryRequest("query it($id: String!) { Thing1(id: $id) {id name thing2 {id name thing3s {id name  derivedFroms {id name}}} derivedFrom {id name}}}",
                                                variables);
        ExecutionResult result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .getEntity()
                  .toString());
            return;
        }
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("Thing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(thing1.getRuleform()
                           .getId()
                           .toString(),
                     thing1Result.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> thing2Result = (Map<String, Object>) thing1Result.get("thing2");
        assertNotNull(thing2Result);
        assertEquals(thing2.getName(), thing2Result.get("name"));
        assertEquals(thing2.getRuleform()
                           .getId()
                           .toString(),
                     thing2Result.get("id"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> thing3s = (List<Map<String, Object>>) thing2Result.get("thing3s");
        assertNotNull(thing3s);
        assertEquals(1, thing3s.size());
        Map<String, Object> thing3Result = thing3s.get(0);
        assertEquals(thing3.getName(), thing3Result.get("name"));
        assertEquals(thing3.getRuleform()
                           .getId()
                           .toString(),
                     thing3Result.get("id"));

    }

    @Test
    public void testIntrospection() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        EntityManagerFactory mockedEmf = mockedEmf();
        GraphQLSchema schema = new GraphQlResource(mockedEmf,
                                                   getClass().getClassLoader()).build(thing1.getScope()
                                                                                            .getWorkspace(),
                                                                                      model);
        String query = INTROSPECTION_QUERY;
        @SuppressWarnings("rawtypes")
        ExecutionResult execute = new GraphQL(schema).execute(query,
                                                              new PhantasmCRUD(model));
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) execute.getData();

        assertNotNull(result);
    }

    @Test
    public void testMutation() throws Exception {
        String[] newAliases = new String[] { "jones", "smith" };
        String newUri = "new iri";
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, "Thingy",
                                        "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class, "model",
                                                 "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setDerivedFrom(artifact);
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);

        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        EntityManagerFactory mockedEmf = mockedEmf();

        GraphQlResource resource = new GraphQlResource(mockedEmf,
                                                       getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("aliases", Arrays.asList(newAliases));
        variables.put("name", "hello");
        variables.put("uri", newUri);
        QueryRequest request = new QueryRequest("mutation m($id: String!, $name: String!, $artifact: String!, $aliases: [String], $uri: String) { UpdateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { name } }",
                                                variables);
        ExecutionResult result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .toString());
            return;
        }
        assertNotNull(result);

        assertEquals(0, result.getErrors()
                              .size());
        assertEquals("hello", thing1.getName());
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("UpdateThing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPlugin() throws InstantiationException {

        EntityManagerFactory mockedEmf = mockedEmf();

        Workspace workspace = model.wrap(Workspace.class, scope.getWorkspace()
                                                               .getDefiningProduct());
        workspace.addPlugin(constructPlugin());

        GraphQlResource resource = new GraphQlResource(mockedEmf,
                                                       getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "hello");
        String hello = "goodbye";
        variables.put("description", hello);
        QueryRequest request = new QueryRequest("mutation m ($name: String!, $description: String) { "
                                                + "CreateThing1("
                                                + "  state: { "
                                                + "     setName: $name, "
                                                + "     setDescription: $description"
                                                + "   }) { id name description } }",
                                                variables);
        String bob = "Give me food or give me slack or kill me";
        Thing1_Plugin.passThrough.set(bob);
        ExecutionResult result = resource.query(null, TEST_SCENARIO_URI,
                                                request);

        assertEquals(result.getErrors()
                           .toString(),
                     0, result.getErrors()
                              .size());

        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("CreateThing1");
        assertNotNull(thing1Result);
        assertEquals(bob, thing1Result.get("description"));
        String thing1ID = (String) thing1Result.get("id");
        assertNotNull(thing1ID);
        Thing1 thing1 = model.wrap(Thing1.class, model.getEntityManager()
                                                      .find(Product.class,
                                                            UUID.fromString(thing1ID)));
        assertEquals(bob, thing1.getDescription());

        String apple = "Connie";
        Thing2 thing2 = model.construct(Thing2.class, apple, "Her Dobbsness");
        thing1.setThing2(thing2);
        variables = new HashMap<>();
        variables.put("id", thing1ID);
        variables.put("test", "me");
        request = new QueryRequest("query it($id: String!, $test: String) { Thing1(id: $id) {id name instanceMethod instanceMethodWithArgument(arg1: $test) } }",
                                   variables);
        result = resource.query(null, TEST_SCENARIO_URI, request);

        assertEquals(result.getErrors()
                           .toString(),
                     0, result.getErrors()
                              .size());

        thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("Thing1");
        assertNotNull(thing1Result);
        assertEquals(apple, thing1Result.get("instanceMethod"));
        assertEquals("me", Thing1_Plugin.passThrough.get());
        assertEquals(apple, thing1Result.get("instanceMethodWithArgument"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkspaceSchema() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, "Thingy",
                                        "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class, "model",
                                                 "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");
        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");
        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setDerivedFrom(artifact);
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);
        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        EntityManagerFactory mockedEmf = mockedEmf();
        GraphQLSchema schema = new GraphQlResource(mockedEmf,
                                                   getClass().getClassLoader()).build(thing1.getScope()
                                                                                            .getWorkspace(),
                                                                                      model);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        ExecutionResult execute = new GraphQL(schema).execute("query it($id: String!) { Thing1(id: $id) {id name thing2 {id name thing3s {id name derivedFroms {id name}}} derivedFrom {id name}}}",

                                                              new PhantasmCRUD(model),
                                                              variables);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        Map<String, Object> result = (Map<String, Object>) execute.getData();

        assertNotNull(result);

        Map<String, Object> thing1Result = (Map<String, Object>) result.get("Thing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(thing1.getRuleform()
                           .getId()
                           .toString(),
                     thing1Result.get("id"));

        Map<String, Object> thing2Result = (Map<String, Object>) thing1Result.get("thing2");
        assertNotNull(thing2Result);
        assertEquals(thing2.getName(), thing2Result.get("name"));
        assertEquals(thing2.getRuleform()
                           .getId()
                           .toString(),
                     thing2Result.get("id"));
        List<Map<String, Object>> thing3s = (List<Map<String, Object>>) thing2Result.get("thing3s");
        assertNotNull(thing3s);
        assertEquals(1, thing3s.size());
        Map<String, Object> thing3Result = thing3s.get(0);
        assertEquals(thing3.getName(), thing3Result.get("name"));
        assertEquals(thing3.getRuleform()
                           .getId()
                           .toString(),
                     thing3Result.get("id"));
        List<Map<String, Object>> thing3DerivedFroms = (List<Map<String, Object>>) thing3Result.get("derivedFroms");
        assertNotNull(thing3DerivedFroms);
        assertEquals(2, thing3DerivedFroms.size());

        result = (Map<String, Object>) new GraphQL(schema).execute(String.format("{ InstancesOfThing1 {id name URI}}",
                                                                                 thing1.getRuleform()
                                                                                       .getId()),
                                                                   new PhantasmCRUD(model))
                                                          .getData();
        List<Map<String, Object>> instances = (List<Map<String, Object>>) result.get("InstancesOfThing1");
        assertEquals(1, instances.size());
        Map<String, Object> instance = instances.get(0);
        assertEquals(thing1.getName(), instance.get("name"));
        assertEquals(thing1.getRuleform()
                           .getId()
                           .toString(),
                     instance.get("id"));
        assertEquals(uri, instance.get("URI"));
    }

    private Plugin constructPlugin() throws InstantiationException {
        Plugin testPlugin = model.construct(Plugin.class, "Test Plugin",
                                            "My super green test plugin");
        testPlugin.setFacetName("Thing1");
        testPlugin.setPackageName("com.chiralbehaviors.CoRE.phantasm.resources.plugin");
        testPlugin.setConstructor(model.construct(Constructor.class,
                                                  "constructor",
                                                  "For all your construction needs"));
        testPlugin.addInstanceMethod(model.construct(InstanceMethod.class,
                                                     "instanceMethod",
                                                     "For instance"));
        InstanceMethod methodWithArg = model.construct(InstanceMethod.class,
                                                       "instanceMethodWithArgument",
                                                       "For all your argument needs");
        Argument argument = model.construct(Argument.class, "arg1",
                                            "Who needs an argument?");
        methodWithArg.addArgument(argument);
        argument.setInputType("String");
        testPlugin.addInstanceMethod(methodWithArg);
        return testPlugin;
    }
}
