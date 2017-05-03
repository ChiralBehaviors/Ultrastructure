package com.chiralbehaviors.CoRE.phantasm.graphql.jooq;

import static graphql.schema.GraphQLObjectType.newObject;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.FacetTypeTest;
import com.chiralbehaviors.CoRE.phantasm.graphql.UuidUtil;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

/**
 * @author halhildebrand
 *
 */
public class JooqSchemaTest extends AbstractModelTest {
    private static GraphQLSchema schema = generateTestSchema();

    private static GraphQLSchema generateTestSchema() {
        JooqSchema constructor = new JooqSchema();
        GraphQLObjectType.Builder topLevelQuery = newObject().name("Query");
        GraphQLObjectType.Builder topLevelMutation = newObject().name("Mutation");
        constructor.contributeTo(topLevelQuery, topLevelMutation);
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build(constructor.getTypes());
        return schema;
    }

    private Product definingProduct;
    private Kernel  k;

    @Before
    public void load() throws Exception {
        k = model.getKernel();
        definingProduct = k.getKernelWorkspace();
    }

    @Test
    public void testChildSequencingMutations() throws IllegalArgumentException,
                                               Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("service", k.getAnyProduct()
                                  .getId()
                                  .toString());
        variables.put("statusCode", k.getAnyStatusCode()
                                     .getId()
                                     .toString());
        variables.put("nextChild", k.getAnyProduct()
                                    .getId()
                                    .toString());
        variables.put("nextChildStatus", k.getAnyStatusCode()
                                          .getId()
                                          .toString());

        ObjectNode result = execute(schema,
                                    "mutation m($service: ID $statusCode: ID $nextChild: ID $nextChildStatus: ID) "
                                            + "{ createChildSequencingAuthorization(state: {service: $service statusCode: $statusCode "
                                            + "nextChild: $nextChild nextChildStatus: $nextChildStatus }) {id} }",
                                    variables);
        variables.put("id", result.get("createChildSequencingAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateChildSequencingAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: ID!) { deleteChildSequencingAuthorization(id: $id) }",
                variables);
    }

    @Test
    public void testIntrospection() throws Exception {
        ObjectNode result = execute(schema, getIntrospectionQuery(),
                                    Collections.emptyMap());
        assertNotNull(result);
    }

    @Test
    public void testMetaProtocolMutations() throws IllegalArgumentException,
                                            Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("r", UuidUtil.encode(k.getAnyRelationship()
                                            .getId()));
        ObjectNode result = execute(schema,
                                    "mutation m($r: ID) { createMetaProtocol(state: {assignTo: $r deliverFrom: $r "
                                            + "requester: $r service: $r serviceType: $r  status: $r "
                                            + "product: $r deliverTo: $r  quantityUnit: $r "
                                            + "}) {id} }",
                                    variables);
        variables.put("id", result.get("createMetaProtocol")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateMetaProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema, "mutation m($id: ID!) { deleteMetaProtocol(id: $id) }",
                variables);
    }

    @Test
    public void testParentSequencingMutations() throws IllegalArgumentException,
                                                Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("service", k.getAnyProduct()
                                  .getId()
                                  .toString());
        variables.put("statusCode", k.getAnyStatusCode()
                                     .getId()
                                     .toString());
        variables.put("parent", k.getAnyProduct()
                                 .getId()
                                 .toString());
        variables.put("parentStatus", k.getAnyStatusCode()
                                       .getId()
                                       .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($service: ID $statusCode: ID $parent: ID $parentStatus: ID) { "
                                            + "createParentSequencingAuthorization(state: {service: $service "
                                            + "statusCode: $statusCode parent: $parent parentStatusToSet: $parentStatus "
                                            + "}) {id} }",
                                    variables);
        variables.put("id", result.get("createParentSequencingAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateParentSequencingAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: ID!) { deleteParentSequencingAuthorization(id: $id) }",
                variables);
    }

    @Test
    public void testProtocolMutations() throws IllegalArgumentException,
                                        Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("r", k.getAnyRelationship()
                            .getId()
                            .toString());
        variables.put("a", k.getAnyAgency()
                            .getId()
                            .toString());
        variables.put("l", k.getAnyLocation()
                            .getId()
                            .toString());
        variables.put("p", k.getAnyProduct()
                            .getId()
                            .toString());
        variables.put("s", k.getAnyStatusCode()
                            .getId()
                            .toString());
        variables.put("u", k.getAnyUnit()
                            .getId()
                            .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($r: ID) "
                                            + "{ createProtocol(state: {assignTo: $r deliverFrom: $r "
                                            + "deliverTo: $r " + "product: $r "
                                            + "requester: $r " + "service: $r "
                                            + "status: $r "
                                            + "quantityUnit: $r "
                                            + "childAssignTo: $r "
                                            + "childDeliverFrom: $r "
                                            + "childDeliverTo: $r "
                                            + "childProduct: $r "
                                            + "childService: $r "
                                            + "childStatus: $r "
                                            + "childQuantityUnit: $r "
                                            + "childrenRelationship: $r}) {id} }",
                                    variables);
        variables.put("id", result.get("createProtocol")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema, "mutation m($id: ID!) { deleteProtocol(id: $id) }",
                variables);
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
        WorkspaceContext context = new WorkspaceContext(model, definingProduct);
        ExecutionResult execute = context.execute(schema, query, variables);
        assertTrue(format(execute.getErrors()), execute.getErrors()
                                                       .isEmpty());
        ObjectNode result = new ObjectMapper().valueToTree(execute.getData());
        assertNotNull(result);
        return result;

    }

    private String format(List<GraphQLError> list) {
        StringBuilder builder = new StringBuilder();
        list.forEach(e -> builder.append(e)
                                 .append('\n'));
        return builder.toString();
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

    private List<String> ids(ArrayNode in) {
        List<String> ids = new ArrayList<>();
        in.forEach(o -> ids.add(o.get("id")
                                 .asText()));
        return ids;
    }

    @Test
    public void testFacetMutations() throws IllegalArgumentException,
                                     Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("auth", k.getCore()
                               .getId()
                               .toString());
        variables.put("classifier", k.getIsA()
                                     .getId()
                                     .toString());
        variables.put("classification", k.getCore()
                                         .getId()
                                         .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($auth: ID $classifier: ID $classification: ID) { "
                                            + "createFacet(state: {authority: $auth classifier: $classifier name: \"foo\" "
                                            + "classification: $classification }) {id} }",
                                    variables);
        variables.put("id", result.get("createFacet")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateFacet(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema, "mutation m($id: ID!) { deleteFacet(id: $id) }",
                variables);
    }

    @Test
    public void testQueries() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                                                model);
        definingProduct = importer.getWorkspace()
                                  .getDefiningProduct();
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data = execute(schema,
                                  "{ facets { id name  classifier {id} classification {id} authority { id } }}",
                                  variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ childSequencingAuthorizations { id service {id} nextChild { id } nextChildStatus {id} notes sequenceNumber statusCode {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("childSequencingAuthorizations")));
        data = execute(schema,
                       "query q($ids: [ID]!) { childSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("childSequencingAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { childSequencingAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ metaProtocols { id  product {id} assignTo {id} deliverFrom{id} deliverTo{id} quantityUnit {id} requester{id} service{id} status{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("metaProtocols")));
        data = execute(schema,
                       "query q($ids: [ID]!) { metaProtocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("metaProtocols")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { metaProtocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ parentSequencingAuthorizations { id notes parent{id} parentStatusToSet{id} sequenceNumber statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("parentSequencingAuthorizations")));
        data = execute(schema,
                       "query q($ids: [ID]!) { parentSequencingAuthorizations(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("parentSequencingAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { parentSequencingAuthorization(id: $id) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ protocols { id name notes assignTo {id} deliverFrom{id} deliverTo{id} "
                               + "product {id} quantity quantityUnit {id} requester{id} service{id} status{id} updatedBy{id} version "
                               + "childAssignTo {id} childDeliverFrom{id} childDeliverTo{id} childProduct {id} "
                               + "childQuantity childQuantityUnit {id} childrenRelationship{id} childService{id} childStatus{id}  } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("protocols")));
        data = execute(schema,
                       "query q($ids: [ID]!) { protocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("protocols")).get(0));
        data = execute(schema, "query q($id: ID!) { protocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ selfSequencingAuthorizations { id notes sequenceNumber service{id} setIfActiveSiblings statusCode{id} statusToSet{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("selfSequencingAuthorizations")));
        data = execute(schema,
                       "query q($ids: [ID]!) { selfSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ siblingSequencingAuthorizations { id nextSibling{id} nextSiblingStatus{id} notes sequenceNumber service{id} statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("siblingSequencingAuthorizations")));
        data = execute(schema,
                       "query q($ids: [ID]!) { siblingSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("siblingSequencingAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { siblingSequencingAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ statusCodeSequencings { id child{id} notes parent{id} service{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodeSequencings")));
        data = execute(schema,
                       "query q($ids: [ID]!) { statusCodeSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("statusCodeSequencings")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { statusCodeSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);
    }
}
