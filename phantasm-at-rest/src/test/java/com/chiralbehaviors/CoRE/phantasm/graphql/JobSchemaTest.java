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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.OrderProcessing;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
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

        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model,
                                                           Collections.emptySet());
        Map<String, Object> variables = new HashMap<>();

        variables.put("service", UuidUtil.encode(scenario.getDeliver()
                                                         .getId()));
        variables.put("assignTo",
                      UuidUtil.encode(scenario.getOrderFullfillment()
                                              .getId()));
        variables.put("product", UuidUtil.encode(scenario.getAbc486()
                                                         .getId()));
        variables.put("deliverTo", UuidUtil.encode(scenario.getRc31()
                                                           .getId()));
        variables.put("deliverFrom", UuidUtil.encode(scenario.getFactory1()
                                                             .getId()));
        variables.put("requester", UuidUtil.encode(scenario.getCafleurBon()
                                                           .getId()));

        ObjectNode result = execute(schema,
                                    "mutation m ($service: ID, $assignTo: ID, $product: ID, $deliverTo: ID, "
                                            + "          $deliverFrom: ID, $requester: ID) { "
                                            + "  createJob(state: { service: $service, assignTo: $assignTo, product: $product, "
                                            + "                     deliverTo: $deliverTo, deliverFrom: $deliverFrom, requester: $requester}) { "
                                            + "      id, status {id, name} parent {id} product {name} service {name} requester {name} assignTo {name} "
                                            + "      deliverFrom {name} deliverTo{name} quantity quantityUnit {name} "
                                            + "      chronology {"
                                            + "          id, job {id} status {id, name} product {name} service {name} requester {name} assignTo {name} "
                                            + "          deliverFrom {name} deliverTo{name} quantity quantityUnit {name} updateDate sequenceNumber"
                                            + "      } " + "   } " + "}",
                                    variables);

        result = (ObjectNode) result.get("createJob");
        assertNotNull(result);
        String order = result.get("id")
                             .asText();
        assertNotNull(order);

        assertEquals(UuidUtil.encode(model.getKernel()
                                          .getUnset()
                                          .getId()),
                     result.get("status")
                           .get("id")
                           .asText());

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", UuidUtil.encode(scenario.getAvailable()
                                                        .getId()));
        variables.put("notes", "transition during test");

        result = execute(schema,
                         "mutation m ($id: ID!, $status: ID, $notes: String) { "
                                 + "  updateJob(state: { id: $id, status: $status, notes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}",
                         variables);

        assertFalse(result.get("updateJob")
                          .isNull());
        result = (ObjectNode) result.get("updateJob");
        assertNotNull(result);
        assertEquals(UuidUtil.encode(scenario.getAvailable()
                                             .getId()),
                     result.get("status")
                           .get("id")
                           .asText());

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", UuidUtil.encode(scenario.getActive()
                                                        .getId()));
        variables.put("notes", "transition during test");

        result = execute(schema,
                         "mutation m ($id: ID!, $status: ID, $notes: String) { "
                                 + "  updateJob(state: { id: $id, status: $status, notes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}",
                         variables);

        result = (ObjectNode) result.get("updateJob");
        assertNotNull(result);
        assertEquals(UuidUtil.encode(scenario.getActive()
                                             .getId()),
                     result.get("status")
                           .get("id")
                           .asText());

        variables = new HashMap<>();
        variables.put("id", order);

        result = execute(schema,
                         "query m ($id: ID!) { job(id: $id) { parent {id} allChildren {id } activeChildren {id } children {id } chronology {id} } }",
                         variables);

        result = (ObjectNode) result.get("job");
        assertNotNull(result);

        assertEquals(6, result.withArray("allChildren")
                              .size());

        variables.put("id", result.get("chronology")
                                  .get(0)
                                  .get("id")
                                  .asText());
        result = execute(schema,
                         "query m ($id: ID!) { jobChronology(id: $id) { id, status {id} job {id} product {name} service {name} requester {name} assignTo {name} "
                                 + "      deliverFrom {name} deliverTo{name} quantity quantityUnit {name} } }",
                         variables);
        assertNotNull(result);

        variables.put("ids", Collections.singletonList(variables.get("id")));
        result = execute(schema,
                         "query m ($ids: [ID]!) { jobChronologies(ids: $ids) { id } }",
                         variables);
        assertNotNull(result);
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) {
        WorkspaceContext context = new WorkspaceContext(model, model.getKernel()
                                                                    .getKernelWorkspace());
        ExecutionResult execute = context.execute(schema, query, variables);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        ObjectNode result = new ObjectMapper().valueToTree(execute.getData());
        assertNotNull(result);
        return result;
    }
}
