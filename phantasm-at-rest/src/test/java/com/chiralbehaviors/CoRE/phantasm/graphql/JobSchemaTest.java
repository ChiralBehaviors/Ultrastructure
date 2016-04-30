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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.OrderProcessing;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class JobSchemaTest extends AbstractModelTest {

    private OrderProcessing   scenario;
    private WorkspaceImporter scope;

    @Before
    public void initializeScope() throws IOException {
        scope = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                           model);
        scenario = scope.getWorkspace()
                        .getAccessor(OrderProcessing.class);
    }

    @Test
    public void testEuOrder() throws Exception {

        GraphQLSchema schema = WorkspaceSchema.build(scope.getWorkspace(),
                                                     model,
                                                     getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();

        variables.put("service", scenario.getDeliver()
                                         .getId()
                                         .toString());
        variables.put("assignTo", scenario.getOrderFullfillment()
                                          .getId()
                                          .toString());
        variables.put("product", scenario.getAbc486()
                                         .getId()
                                         .toString());
        variables.put("deliverTo", scenario.getRc31()
                                           .getId()
                                           .toString());
        variables.put("deliverFrom", scenario.getFactory1()
                                             .getId()
                                             .toString());
        variables.put("requester", scenario.getCafleurBon()
                                           .getId()
                                           .toString());

        ObjectNode result = execute(schema,
                                    "mutation m ($service: String, $assignTo: String, $product: String, $deliverTo: String, "
                                            + "          $deliverFrom: String, $requester: String) { "
                                            + "  createJob(state: { service: $service, assignTo: $assignTo, product: $product, "
                                            + "                     deliverTo: $deliverTo, deliverFrom: $deliverFrom, requester: $requester}) { "
                                            + "      id, status {id, name} parent {id} product {name} service {name} requester {name} assignTo {name} "
                                            + "      deliverFrom {name} deliverTo{name} quantity quantityUnit {name} "
                                            + "      chronology {"
                                            + "          id, job {id} status {id, name} product {name} service {name} requester {name} assignTo {name} "
                                            + "          deliverFrom {name} deliverTo{name} quantity quantityUnit {name} updateDate sequenceNumber"
                                            + "      } " + "   } " + "}",
                                    variables);

        result = (ObjectNode) result.get("CreateJob");
        assertNotNull(result);
        String order = result.get("id")
                             .asText();
        assertNotNull(order);

        assertEquals(model.getKernel()
                          .getUnset()
                          .getId()
                          .toString(),
                     result.get("status")
                           .get("id")
                           .asText());

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", scenario.getAvailable()
                                        .getId()
                                        .toString());
        variables.put("notes", "transition during test");

        result = execute(schema,
                         "mutation m ($id: String!, $status: String, $notes: String) { "
                                 + "  updateJob(state: { id: $id, setStatus: $status, setNotes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}",
                         variables);

        result = (ObjectNode) result.get("UpdateJob");
        assertNotNull(result);
        assertEquals(scenario.getAvailable()
                             .getId()
                             .toString(),
                     result.get("status")
                           .get("id")
                           .asText());

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", scenario.getActive()
                                        .getId()
                                        .toString());
        variables.put("notes", "transition during test");

        result = execute(schema,
                         "mutation m ($id: String!, $status: String, $notes: String) { "
                                 + "  updateJob(state: { id: $id, setStatus: $status, setNotes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}",
                         variables);

        result = (ObjectNode) result.get("UpdateJob");
        assertNotNull(result);
        assertEquals(scenario.getActive()
                             .getId()
                             .toString(),
                     result.get("status")
                           .get("id")
                           .asText());

        variables = new HashMap<>();
        variables.put("id", order);

        result = execute(schema,
                         "query m ($id: String!) { job(id: $id) { parent {id} allChildren {id } activeChildren {id } children {id } chronology {id} } }",
                         variables);

        result = (ObjectNode) result.get("Job");
        assertNotNull(result);

        assertEquals(6, result.withArray("allChildren")
                              .size());
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) {
        ExecutionResult execute = new GraphQL(schema).execute(query,
                                                              new WorkspaceContext(model,
                                                                                   model.getKernel()
                                                                                        .getKernelWorkspace()),
                                                              variables);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        ObjectNode result = new ObjectMapper().valueToTree(execute.getData());
        assertNotNull(result);
        return result;
    }
}
