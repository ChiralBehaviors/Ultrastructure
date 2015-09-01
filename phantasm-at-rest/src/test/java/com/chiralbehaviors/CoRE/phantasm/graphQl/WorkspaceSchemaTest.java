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

package com.chiralbehaviors.CoRE.phantasm.graphQl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.resource.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing3;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource.QueryRequest;
import com.chiralbehaviors.CoRE.phantasm.resources.ResourcesTest;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.validation.ValidationError;
import graphql.validation.ValidationErrorType;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchemaTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";

    @Test
    public void testBadQueries() throws Exception {
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);

        EntityManagerFactory mockedEmf = mockedEmf();
        GraphQlResource resource = new GraphQlResource(mockedEmf);
        Map<String, Object> variables = new HashMap<>();
        QueryRequest request = new QueryRequest("mutation m($id: String) { UpdateThing1(state: { id: $id, setName: \"foo\"}) { name } }",
                                                variables);
        Map<String, Object> result;
        result = resource.query(null, TEST_SCENARIO_URI, request);
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        List<ValidationError> errors = (List<ValidationError>) result.get("errors");
        assertNotNull(errors);
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals(ValidationErrorType.UnboundVariable,
                     error.getValidationErrorType());
        assertEquals("Validation error of type UnboundVariable: Variable not bound: '$id'",
                     error.getMessage());
    }

    @Test
    public void testCreate() throws Exception {
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
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

        GraphQlResource resource = new GraphQlResource(mockedEmf);
        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String, $description: String, $artifact: String) { CreateThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name } }",
                                                variables);
        Map<String, Object> result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .toString());
            return;
        }
        assertNotNull(result);

        System.out.println(result);

        assertNull(result.get("errors"));
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("CreateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));

        // assertEquals(artifact2, thing1.getDerivedFrom());
    }

    @Test
    public void testGraphQlResource() throws Exception {
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
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

        GraphQlResource resource = new GraphQlResource(mockedEmf);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        QueryRequest request = new QueryRequest("query it($id: String) { Thing1(id: $id) {id name thing2 {id name thing3s {id name  derivedFroms {id name}}} derivedFrom {id name}}}",
                                                variables);
        Map<String, Object> result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .getEntity()
                  .toString());
            return;
        }
        assertNotNull(result);

        System.out.println(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("Thing1");
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
    public void testMutation() throws Exception {
        em.getTransaction()
          .begin();
        String[] newAliases = new String[] { "jones", "smith" };
        String newUri = "new iri";
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
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

        GraphQlResource resource = new GraphQlResource(mockedEmf);
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
        QueryRequest request = new QueryRequest("mutation m($id: String, $name: String, $artifact: String, $aliases: [String], $uri: String) { UpdateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { name } }",
                                                variables);
        Map<String, Object> result;
        try {
            result = resource.query(null, TEST_SCENARIO_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .toString());
            return;
        }
        assertNotNull(result);

        System.out.println(result);

        assertNull(result.get("errors"));
        assertEquals("hello", thing1.getName());
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("UpdateThing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testWorkspaceSchema() throws Exception {
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
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
        GraphQLSchema schema = new GraphQlResource(mockedEmf).build(thing1.getScope()
                                                                          .getWorkspace(),
                                                                    model);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        ExecutionResult execute = new GraphQL(schema).execute("query it($id: String) { Thing1(id: $id) {id name thing2 {id name thing3s {id name derivedFroms {id name}}} derivedFrom {id name}}}",

        new PhantasmCRUD(model), variables);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        Map<String, Object> result = execute.getData();

        assertNotNull(result);

        System.out.println(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("Thing1");
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
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> thing3DerivedFroms = (List<Map<String, Object>>) thing3Result.get("derivedFroms");
        assertNotNull(thing3DerivedFroms);
        assertEquals(2, thing3DerivedFroms.size());

        result = new GraphQL(schema).execute(String.format("{ InstancesOfThing1 {id name URI}}",
                                                           thing1.getRuleform()
                                                                 .getId()),
                                             new PhantasmCRUD(model))
                                    .getData();
        @SuppressWarnings("unchecked")
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
}
