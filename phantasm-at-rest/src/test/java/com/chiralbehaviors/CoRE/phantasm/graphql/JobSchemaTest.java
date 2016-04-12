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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.OrderProcessing;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.resources.QueryRequest;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class JobSchemaTest extends AbstractModelTest {

    private WorkspaceImporter scope;
    private OrderProcessing   scenario;

    @Before
    public void initializeScope() throws IOException {
        scope = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                           model);
        scenario = scope.getWorkspace()
                        .getAccessor(OrderProcessing.class);
    }

    @Test
    public void testIntrospection() throws Exception {
        GraphQLSchema schema = JobSchema.build();
        String query = getIntrospectionQuery();
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

    @SuppressWarnings("unchecked")
    @Test
    public void testEuOrder() throws Exception {

        GraphQLSchema schema = JobSchema.build();
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

        QueryRequest query = new QueryRequest("mutation m ($service: String!, $assignTo: String, $product: String, $deliverTo: String, "
                                              + "          $deliverFrom: String, $requester: String) { "
                                              + "  CreateJob(state: { setService: $service, setAssignTo: $assignTo, setProduct: $product, "
                                              + "                     setDeliverTo: $deliverTo, setDeliverFrom: $deliverFrom, setRequester: $requester}) { "
                                              + "      id, status {id, name} parent {id} product {name} service {name} requester {name} assignTo {name} "
                                              + "      deliverFrom {name} deliverTo{name} quantity quantityUnit {name} "
                                              + "      chronology {"
                                              + "      id, status {id, name} product {name} service {name} requester {name} assignTo {name} "
                                              + "      deliverFrom {name} deliverTo{name} quantity quantityUnit {name} updateDate sequenceNumber"
                                              + "      } " + "   } " + "}",
                                              variables);
        Map<String, Object> result = execute(schema, query);

        result = (Map<String, Object>) result.get("CreateJob");
        assertNotNull(result);
        String order = (String) result.get("id");
        assertNotNull(order);

        assertEquals(model.getKernel()
                          .getUnset()
                          .getId()
                          .toString(),
                     ((Map<String, Object>) result.get("status")).get("id"));

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", scenario.getAvailable()
                                        .getId()
                                        .toString());
        variables.put("notes", "transition during test");

        query = new QueryRequest("mutation m ($id: String!, $status: String, $notes: String) { "
                                 + "  UpdateJob(state: { id: $id, setStatus: $status, setNotes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}", variables);
        result = execute(schema, query);

        result = (Map<String, Object>) result.get("UpdateJob");
        assertNotNull(result);
        assertEquals(scenario.getAvailable()
                             .getId()
                             .toString(),
                     ((Map<String, Object>) result.get("status")).get("id"));

        model.flush();

        variables = new HashMap<>();
        variables.put("id", order);
        variables.put("status", scenario.getActive()
                                        .getId()
                                        .toString());
        variables.put("notes", "transition during test");

        query = new QueryRequest("mutation m ($id: String!, $status: String, $notes: String) { "
                                 + "  UpdateJob(state: { id: $id, setStatus: $status, setNotes: $notes}) { "
                                 + "      id, status {id, name} " + "   } "
                                 + "}", variables);
        result = execute(schema, query);

        result = (Map<String, Object>) result.get("UpdateJob");
        assertNotNull(result);
        assertEquals(scenario.getActive()
                             .getId()
                             .toString(),
                     ((Map<String, Object>) result.get("status")).get("id"));

        variables = new HashMap<>();
        variables.put("id", order);

        query = new QueryRequest("query m ($id: String!) { Job(id: $id) { parent {id} allChildren {id } activeSubJobs {id } children {id } chronology {id} } }",
                                 variables);
        result = execute(schema, query);

        result = (Map<String, Object>) result.get("Job");
        assertNotNull(result);

        assertEquals(6, ((List<?>) result.get("allChildren")).size());
    }

    public Map<String, Object> execute(GraphQLSchema schema,
                                       QueryRequest query) {
        ExecutionResult execute = new GraphQL(schema).execute(query.getQuery(),
                                                              new PhantasmCRUD(model),
                                                              query.getVariables());
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) execute.getData();
        assertNotNull(result);
        return result;
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
