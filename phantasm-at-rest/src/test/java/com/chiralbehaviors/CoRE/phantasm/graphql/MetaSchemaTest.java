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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class MetaSchemaTest extends AbstractModelTest {
    private Product       definingProduct;
    private Kernel        k;
    private GraphQLSchema schema;

    @Before
    public void load() throws Exception {
        k = model.getKernel();
        schema = new WorkspaceSchema().buildMeta();
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

        ObjectNode result = execute("mutation m($service: ID $statusCode: ID $nextChild: ID $nextChildStatus: ID) "
                                    + "{ createChildSequencingAuthorization(state: {service: $service statusCode: $statusCode "
                                    + "nextChild: $nextChild nextChildStatus: $nextChildStatus }) {id} }",
                                    variables);
        variables.put("id", result.get("createChildSequencingAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute("mutation m($id: ID! $auth: ID) { updateChildSequencingAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute("mutation m($id: ID!) { deleteChildSequencingAuthorization(id: $id) }",
                variables);
    }

    @Test
    public void testExistentialMutations() throws IllegalArgumentException,
                                           Exception {
        Map<String, Object> variables = new HashMap<>();

        ObjectNode result = execute("mutation m { createAgency(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                                    variables);
        variables.put("id", result.get("createAgency")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateAgency(state: {id: $id notes:\"foo\" authority: $id}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeAgency(id: $id) }", variables);

        result = execute("mutation m { createAttribute(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createAttribute")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateAttribute(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeAttribute(id: $id) }", variables);

        result = execute("mutation m { createInterval(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createInterval")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateInterval(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeInterval(id: $id) }", variables);

        result = execute("mutation m { createLocation(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createLocation")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateLocation(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeLocation(id: $id) }", variables);

        result = execute("mutation m { createProduct(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createProduct")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateProduct(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeProduct(id: $id) }", variables);

        result = execute("mutation m { createRelationship(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createRelationship")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateRelationship(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeRelationship(id: $id) }",
                variables);

        result = execute("mutation m { createStatusCode(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createStatusCode")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateStatusCode(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeStatusCode(id: $id) }",
                variables);

        result = execute("mutation m { createUnit(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createUnit")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID!) { updateUnit(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: ID!) { removeUnit(id: $id) }", variables);
    }

    @Test
    public void testExistentialQueries() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                                                model);
        definingProduct = importer.getWorkspace()
                                  .getDefiningProduct();
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data;

        data = execute("{ agencies { id name description updatedBy {id} authority {id}} }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("agencies")));
        data = execute("query q($ids: [ID]!) { agencies(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("agencies")).get(0));
        data = execute("query q($id: ID!) { agency(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ attributes { id name description keyed indexed valueType } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("attributes")));
        data = execute("query q($ids: [ID]!) { attributes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("attributes")).get(0));
        data = execute("query q($id: ID!) { attribute(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ intervals { id name description }  }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("intervals")));
        data = execute("query q($ids: [ID]!) { intervals(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ locations { id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("locations")));
        data = execute("query q($ids: [ID]!) { locations(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("locations")).get(0));
        data = execute("query q($id: ID!) { location(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ products { id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("products")));
        data = execute("query q($ids: [ID]!) { products(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("products")).get(0));
        data = execute("query q($id: ID!) { product(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ relationships { id name description inverse { id } } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("relationships")));
        data = execute("query q($ids: [ID]!) { relationships(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("relationships")).get(0));
        data = execute("query q($id: ID!) { relationship(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ statusCodes { id name description failParent propagateChildren } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodes")));
        data = execute("query q($ids: [ID]!) { statusCodes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("statusCodes")).get(0));
        data = execute("query q($id: ID!) { statusCode(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ units{ id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("units")));
        data = execute("query q($ids: [ID]!) { units(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
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
        ObjectNode result = execute("mutation m($auth: ID $classifier: ID $classification: ID) { "
                                    + "createFacet(state: {authority: $auth classifier: $classifier name: \"foo\" "
                                    + "classification: $classification }) {id} }",
                                    variables);
        variables.put("id", result.get("createFacet")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: ID! $auth: ID) { updateFacet(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute("mutation m($id: ID!) { deleteFacet(id: $id) }", variables);
    }

    @Test
    public void testIntrospection() throws Exception {
        ObjectNode result = execute(getIntrospectionQuery(),
                                    Collections.emptyMap());
        assertNotNull(result);
    }

    @Test
    public void testMetaProtocolMutations() throws IllegalArgumentException,
                                            Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("r", UuidUtil.encode(k.getAnyRelationship()
                                            .getId()));
        ObjectNode result = execute("mutation m($r: ID) { createMetaProtocol(state: {assignTo: $r deliverFrom: $r "
                                    + "requester: $r service: $r serviceType: $r  status: $r "
                                    + "product: $r deliverTo: $r  quantityUnit: $r "
                                    + "}) {id} }", variables);
        variables.put("id", result.get("createMetaProtocol")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute("mutation m($id: ID! $auth: ID) { updateMetaProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute("mutation m($id: ID!) { deleteMetaProtocol(id: $id) }",
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
        ObjectNode result = execute("mutation m($service: ID $statusCode: ID $parent: ID $parentStatus: ID) { "
                                    + "createParentSequencingAuthorization(state: {service: $service "
                                    + "statusCode: $statusCode parent: $parent parentStatusToSet: $parentStatus "
                                    + "}) {id} }", variables);
        variables.put("id", result.get("createParentSequencingAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute("mutation m($id: ID! $auth: ID) { updateParentSequencingAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute("mutation m($id: ID!) { deleteParentSequencingAuthorization(id: $id) }",
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
        ObjectNode result = execute("mutation m($r: ID) "
                                    + "{ createProtocol(state: {assignTo: $r deliverFrom: $r "
                                    + "deliverTo: $r " + "product: $r "
                                    + "requester: $r " + "service: $r "
                                    + "status: $r " + "quantityUnit: $r "
                                    + "childAssignTo: $r "
                                    + "childDeliverFrom: $r "
                                    + "childDeliverTo: $r "
                                    + "childProduct: $r " + "childService: $r "
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
        execute("mutation m($id: ID! $auth: ID) { updateProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute("mutation m($id: ID!) { deleteProtocol(id: $id) }", variables);
    }

    @Test
    public void testQueries() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                                                model);
        definingProduct = importer.getWorkspace()
                                  .getDefiningProduct();
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data = execute("{ facets { id name  classifier {id} classification {id} authority { id } }}",
                                  variables);
        assertNotNull(data);

        data = execute("{ childSequencingAuthorizations { id service {id} nextChild { id } nextChildStatus {id} notes sequenceNumber statusCode {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("childSequencingAuthorizations")));
        data = execute("query q($ids: [ID]!) { childSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("childSequencingAuthorizations")).get(0));
        data = execute("query q($id: ID!) { childSequencingAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute("{ metaProtocols { id  product {id} assignTo {id} deliverFrom{id} deliverTo{id} quantityUnit {id} requester{id} service{id} status{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("metaProtocols")));
        data = execute("query q($ids: [ID]!) { metaProtocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("metaProtocols")).get(0));
        data = execute("query q($id: ID!) { metaProtocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute("{ parentSequencingAuthorizations { id notes parent{id} parentStatusToSet{id} sequenceNumber statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("parentSequencingAuthorizations")));
        data = execute("query q($ids: [ID]!) { parentSequencingAuthorizations(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("parentSequencingAuthorizations")).get(0));
        data = execute("query q($id: ID!) { parentSequencingAuthorization(id: $id) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);

        data = execute("{ protocols { id name notes assignTo {id} deliverFrom{id} deliverTo{id} "
                       + "product {id} quantity quantityUnit {id} requester{id} service{id} status{id} updatedBy{id} version "
                       + "childAssignTo {id} childDeliverFrom{id} childDeliverTo{id} childProduct {id} "
                       + "childQuantity childQuantityUnit {id} childrenRelationship{id} childService{id} childStatus{id}  } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("protocols")));
        data = execute("query q($ids: [ID]!) { protocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("protocols")).get(0));
        data = execute("query q($id: ID!) { protocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute("{ selfSequencingAuthorizations { id notes sequenceNumber service{id} setIfActiveSiblings statusCode{id} statusToSet{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("selfSequencingAuthorizations")));
        data = execute("query q($ids: [ID]!) { selfSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);

        data = execute("{ siblingSequencingAuthorizations { id nextSibling{id} nextSiblingStatus{id} notes sequenceNumber service{id} statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("siblingSequencingAuthorizations")));
        data = execute("query q($ids: [ID]!) { siblingSequencingAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("siblingSequencingAuthorizations")).get(0));
        data = execute("query q($id: ID!) { siblingSequencingAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute("{ statusCodeSequencings { id child{id} notes parent{id} service{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodeSequencings")));
        data = execute("query q($ids: [ID]!) { statusCodeSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("statusCodeSequencings")).get(0));
        data = execute("query q($id: ID!) { statusCodeSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);
    }

    private ObjectNode execute(String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
        MetaContext context = new MetaContext(model, definingProduct);
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
}
