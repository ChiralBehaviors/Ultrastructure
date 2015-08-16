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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchemaBuilder;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.ResourcesTest;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;

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
        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setThing2(thing2);
        WorkspaceSchemaBuilder wspSchema = new WorkspaceSchemaBuilder(TEST_SCENARIO_URI,
                                                                      model);
        GraphQLSchema schema = wspSchema.build();
        WorkspaceContext ctx = new WorkspaceContext(() -> model);
        Map<String, Object> result = new GraphQL(schema).execute(String.format("{ Thing1(id: \"%s\") {id name}}",
                                                                               thing1.getRuleform()
                                                                                     .getId()),
                                                                 ctx)
                                                        .getData();

        assertNotNull(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) result.get("Thing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(thing1.getRuleform()
                           .getId()
                           .toString(),
                     thing1Result.get("id"));

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
