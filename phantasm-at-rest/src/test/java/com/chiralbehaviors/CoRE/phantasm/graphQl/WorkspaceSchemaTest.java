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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchemaBuilder;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.ResourcesTest;
import com.chiralbehaviors.CoRE.phantasm.resource.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing3;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchemaTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";

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
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");
        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setDerivedFrom(artifact);
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);
        WorkspaceSchemaBuilder schemaBuilder = new WorkspaceSchemaBuilder(TEST_SCENARIO_URI,
                                                                          model);
        GraphQLSchema schema = schemaBuilder.build();
        WorkspaceContext ctx = new WorkspaceContext(() -> model);
        ExecutionResult execute = new GraphQL(schema).execute(String.format("{ Thing1(id: \"%s\") {id name thing2 {id name thing3s {id name}} derivedFrom {id name}}}",
                                                                            thing1.getRuleform()
                                                                                  .getId()),
                                                              ctx);
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

        result = new GraphQL(schema).execute(String.format("{ InstancesOfThing1 {id name URI}}",
                                                           thing1.getRuleform()
                                                                 .getId()),
                                             ctx)
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
