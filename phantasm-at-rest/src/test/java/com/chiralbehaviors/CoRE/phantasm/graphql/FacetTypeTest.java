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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.resource.test.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.resource.test.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.Thing2;
import com.chiralbehaviors.CoRE.phantasm.resource.test.Thing3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class FacetTypeTest extends AbstractModelTest {

    @Before
    public void initializeScope() throws IOException {
        WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream("/thing.wsp"),
                                   model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndMutate() throws Exception {
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

        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model,
                                                           Collections.emptySet());

        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", UuidUtil.encode(artifact1.getRuleform()
                                                           .getId()));
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String, $description: String, $artifact: ID) { createThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name description } }",
                                                variables);

        ExecutionResult execute = execute(scope, schema, request);

        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        Map<String, Object> result = (Map<String, Object>) execute.getData();

        Map<String, Object> thing1Result = (Map<String, Object>) result.get("createThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
        Thing1 thing1 = model.wrap(Thing1.class, model.records()
                                                      .resolve(UuidUtil.decode((String) thing1Result.get("id"))));
        assertNotNull(thing1);
        assertEquals(artifact1, thing1.getDerivedFrom());

        variables = new HashMap<>();
        variables.put("id", UuidUtil.encode(thing1.getRuleform()
                                                  .getId()));
        variables.put("artifact", UuidUtil.encode(artifact2.getRuleform()
                                                           .getId()));
        variables.put("aliases", Arrays.asList(newAliases));
        variables.put("name", "hello");
        variables.put("uri", newUri);
        request = new QueryRequest("mutation m($id: ID!, $name: String!, $artifact: ID, $aliases: [String], $uri: String) { updateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { id name } }",
                                   variables);
        execute = execute(scope, schema, request);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        result = (Map<String, Object>) execute.getData();

        thing1Result = (Map<String, Object>) result.get("updateThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
        thing1 = model.wrap(Thing1.class, model.records()
                                               .resolve(UuidUtil.decode((String) thing1Result.get("id"))));
        assertNotNull(thing1);
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());

        variables = new HashMap<>();
        variables.put("thing1", UuidUtil.encode(thing1.getRuleform()
                                                      .getId()));
        variables.put("artifact", UuidUtil.encode(artifact2.getRuleform()
                                                           .getId()));
        variables.put("name", "hello");
        request = new QueryRequest("mutation m($name: String!, $artifact: ID!, $thing1: ID!) { \n"
                                   + "createThing2(state: {setName: $name, \n"
                                   + "addDerivedFrom: $artifact, \n"
                                   + "setThing1: $thing1}) { id name } }",
                                   variables);

        execute = execute(scope, schema, request);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        result = (Map<String, Object>) execute.getData();

    }

    @Test
    public void testIntrospection() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        GraphQLSchema schema = new WorkspaceSchema().build(thing1.getScope()
                                                                 .getWorkspace(),
                                                           model,
                                                           Collections.emptySet());
        String query = getIntrospectionQuery();
        ExecutionResult execute = execute(thing1, schema, query,
                                          Collections.emptyMap());
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        JsonNode result = new ObjectMapper().convertValue(execute.getData(),
                                                          JsonNode.class);

        assertNotNull(result);
    }

    private ExecutionResult execute(Thing1 thing1, GraphQLSchema schema,
                                    String query,
                                    Map<String, Object> variables) {
        return new WorkspaceContext(model, thing1.getScope()
                                                 .getWorkspace()
                                                 .getDefiningProduct()).execute(schema,
                                                                                query,
                                                                                variables);
    }

    @Test
    public void testMutation() throws Exception {
        String[] newAliases = new String[] { "jones", "smith" };
        String newUri = "new iri";
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, ExistentialDomain.Product,
                                        "Thingy", "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "model", "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
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

        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model,
                                                           Collections.emptySet());

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", UuidUtil.encode(thing1.getRuleform()
                                                  .getId()));
        variables.put("artifact", UuidUtil.encode(artifact2.getRuleform()
                                                           .getId()));
        variables.put("aliases", Arrays.asList(newAliases));
        variables.put("name", "hello");
        variables.put("uri", newUri);
        QueryRequest request = new QueryRequest("mutation m($id: ID!, $name: String!, $artifact: ID!, $aliases: [String], $uri: String) { updateThing1(state: { id: $id, setName: $name, setDerivedFrom: $artifact, setAliases: $aliases, setURI: $uri}) { name } }",
                                                variables);
        ExecutionResult execute = execute(scope, schema, request);

        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) execute.getData();

        thing1.getRuleform()
              .refresh();

        assertEquals("hello", thing1.getName());
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("updateThing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(artifact2, thing1.getDerivedFrom());
        assertArrayEquals(newAliases, thing1.getAliases());
        assertEquals(newUri, thing1.getURI());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWorkspaceSchema() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, ExistentialDomain.Product,
                                        "Thingy", "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "model", "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");
        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
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

        GraphQLSchema schema = new WorkspaceSchema().build(thing1.getScope()
                                                                 .getWorkspace(),
                                                           model,
                                                           Collections.emptySet());
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", UuidUtil.encode(thing1.getRuleform()
                                                  .getId()));
        String query = "query it($id: ID!) { thing1(id: $id) {id name thing2 {id name thing3s {id name derivedFroms {id name}}} derivedFrom {id name}}}";
        ExecutionResult execute = execute(thing1, schema, query, variables);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        Map<String, Object> result = (Map<String, Object>) execute.getData();

        assertNotNull(result);

        Map<String, Object> thing1Result = (Map<String, Object>) result.get("thing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(UuidUtil.encode(thing1.getRuleform()
                                           .getId()),
                     thing1Result.get("id"));

        Map<String, Object> thing2Result = (Map<String, Object>) thing1Result.get("thing2");
        assertNotNull(thing2Result);
        assertEquals(thing2.getName(), thing2Result.get("name"));
        assertEquals(UuidUtil.encode(thing2.getRuleform()
                                           .getId()),
                     thing2Result.get("id"));
        List<Map<String, Object>> thing3s = (List<Map<String, Object>>) thing2Result.get("thing3s");
        assertNotNull(thing3s);
        assertEquals(1, thing3s.size());
        Map<String, Object> thing3Result = thing3s.get(0);
        assertEquals(thing3.getName(), thing3Result.get("name"));
        assertEquals(UuidUtil.encode(thing3.getRuleform()
                                           .getId()),
                     thing3Result.get("id"));
        List<Map<String, Object>> thing3DerivedFroms = (List<Map<String, Object>>) thing3Result.get("derivedFroms");
        assertNotNull(thing3DerivedFroms);
        assertEquals(2, thing3DerivedFroms.size());

        String q = "{ thing1s {id name URI}}";
        result = (Map<String, Object>) execute(thing1, schema, q,
                                               Collections.emptyMap()).getData();
        List<Map<String, Object>> instances = (List<Map<String, Object>>) result.get("thing1s");
        assertEquals(1, instances.size());
        Map<String, Object> instance = instances.get(0);
        assertEquals(thing1.getName(), instance.get("name"));
        assertEquals(UuidUtil.encode(thing1.getRuleform()
                                           .getId()),
                     instance.get("id"));
        assertEquals(uri, instance.get("URI"));
    }

    @Test
    public void testCreate() throws Exception {
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, ExistentialDomain.Product,
                                        "Thingy", "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "model", "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        thing2.addThing3(thing3);

        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model,
                                                           Collections.emptySet());

        Map<String, Object> variables = new HashMap<>();
        variables.put("artifact", artifact2.getRuleform()
                                           .getId()
                                           .toString());
        variables.put("name", "hello");
        variables.put("description", "goodbye");
        QueryRequest request = new QueryRequest("mutation m ($name: String!, $description: String!, $artifact: ID) { createThing1(state: { setName: $name, setDescription: $description, setDerivedFrom: $artifact}) { id name } }",
                                                variables);

        ExecutionResult execute = execute(scope, schema, request);

        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) execute.getData();
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("createThing1");
        assertNotNull(thing1Result);
        assertEquals("hello", thing1Result.get("name"));
    }

    @Test
    public void testCasting() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, ExistentialDomain.Product,
                                        "Thingy", "a favorite thing");
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);

        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model,
                                                           Collections.emptySet());

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", UuidUtil.encode(thing1.getRuleform()
                                  .getId()));
        variables.put("thing3", UuidUtil.encode(thing3.getRuleform()
                                      .getId()));

        QueryRequest request = new QueryRequest("mutation m($id: ID!, $thing3: ID!) { updateThing1(state: { id: $id, setThing2: $thing3}) { name } }",
                                                variables);

        ExecutionResult execute = execute(scope, schema, request);

        assertEquals(execute.getErrors()
                            .toString(),
                     1, execute.getErrors()
                               .size());
        assertTrue(execute.getErrors()
                          .get(0)
                          .getMessage()
                          .contains("ClassCastException"));
    }

    private ExecutionResult execute(WorkspaceScope scope, GraphQLSchema schema,
                                    QueryRequest request) {
        return new WorkspaceContext(model, scope.getWorkspace()
                                                .getDefiningProduct()).execute(schema,
                                                                               request.getQuery(),
                                                                               request.getVariables());
    }

    private String getIntrospectionQuery() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[16 * 4096];
        try (InputStream in = getClass().getResourceAsStream("/introspection-query")) {
            for (int read = in.read(buf); read != -1; read = in.read(buf)) {
                baos.write(buf, 0, read);
            }
        }
        return baos.toString();
    }
}
