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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQL;
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
    public void testAttributeAuthorizationMutations() throws IllegalArgumentException,
                                                      Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("auth", k.getCore()
                               .getId()
                               .toString());
        variables.put("attr", k.getIRI()
                               .getId()
                               .toString());
        variables.put("facet", model.getPhantasmModel()
                                    .getFacetDeclaration(k.getIsA(),
                                                         k.getCoreUser())
                                    .getId()
                                    .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($auth: String $attr: String $facet: String) { createAttributeAuthorization(state: {facet: $facet authority: $auth authorizedAttribute:$attr binaryValue: \"\" booleanValue: true integerValue: 1 jsonValue:\"null\" numericValue: 1.0 textValue: \"foo\" timestampValue: 1 }) {id} }",
                                    variables);
        variables.put("id", result.get("createAttributeAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String!) { updateAttributeAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeAttributeAuthorization(id: $id) }",
                variables);
    }

    @Test
    public void testNetworkAttributeAuthorizationMutations() throws IllegalArgumentException,
                                                             Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("auth", k.getCore()
                               .getId()
                               .toString());
        variables.put("attr", k.getIRI()
                               .getId()
                               .toString());
        FacetRecord isCoreUser = model.getPhantasmModel()
                                      .getFacetDeclaration(k.getIsA(),
                                                           k.getCoreUser());
        ExistentialNetworkAuthorizationRecord netAuth = model.getPhantasmModel()
                                                             .getNetworkAuthorizations(isCoreUser,
                                                                                       false)
                                                             .get(0);
        variables.put("netAuth", netAuth.getId()
                                        .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($auth: String $attr: String $netAuth: String) { createNetworkAttributeAuthorization(state: {networkAuthorization: $netAuth authority: $auth authorizedAttribute:$attr binaryValue: \"\" booleanValue: true integerValue: 1 jsonValue:\"null\" numericValue: 1.0 textValue: \"foo\" timestampValue: 1 }) {id} }",
                                    variables);
        variables.put("id", result.get("createNetworkAttributeAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateNetworkAttributeAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeNetworkAttributeAuthorization(id: $id) }",
                variables);
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
                                    "mutation m($service: String $statusCode: String $nextChild: String $nextChildStatus: String) "
                                            + "{ createChildSequencing(state: {service: $service statusCode: $statusCode "
                                            + "nextChild: $nextChild nextChildStatus: $nextChildStatus }) {id} }",
                                    variables);
        variables.put("id", result.get("createChildSequencing")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateChildSequencing(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeChildSequencing(id: $id) }",
                variables);
    }

    @Test
    public void testExistentialMutations() throws IllegalArgumentException,
                                           Exception {
        Map<String, Object> variables = new HashMap<>();

        ObjectNode result = execute(schema,
                                    "mutation m { createAgency(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                                    variables);
        variables.put("id", result.get("createAgency")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateAgency(state: {id: $id notes:\"foo\" authority: $id}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeAgency(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createAttribute(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createAttribute")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateAttribute(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeAttribute(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createInterval(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createInterval")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateInterval(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeInterval(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createLocation(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createLocation")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateLocation(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeLocation(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createProduct(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createProduct")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateProduct(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeProduct(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createRelationship(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createRelationship")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateRelationship(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeRelationship(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createStatusCode(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createStatusCode")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateStatusCode(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeStatusCode(id: $id) }",
                variables);

        result = execute(schema,
                         "mutation m { createUnit(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createUnit")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String!) { updateUnit(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeUnit(id: $id) }",
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
                                    "mutation m($r: String $a: String $l: String $p: String $s: String $u: String) "
                                            + "{ createProtocol(state: {assignTo: $a deliverFrom: $l deliverTo: $l product: $p "
                                            + "requester: $a service: $p status: $s unit: $u childAssignTo: $a childDeliverFrom: $l "
                                            + "childDeliverTo: $l childProduct: $p childService: $p childStatus: $s childUnit: $u "
                                            + "childrenRelationship: $r}) {id} }",
                                    variables);
        variables.put("id", result.get("createProtocol")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeProtocol(id: $id) }",
                variables);
    }

    @Test
    public void testMetaProtocolMutations() throws IllegalArgumentException,
                                            Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("r", k.getAnyRelationship()
                            .getId()
                            .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($r: String) { createMetaProtocol(state: {assignTo: $r deliverFrom: $r "
                                            + "deliverTo: $r quantity:1.0 requester: $r service: $r product: $r serviceType: $r "
                                            + "status: $r unit: $r}) {id} }",
                                    variables);
        variables.put("id", result.get("createMetaProtocol")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateMetaProtocol(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeMetaProtocol(id: $id) }",
                variables);
    }

    @Test
    public void testNetworkAuthorizationMutations() throws IllegalArgumentException,
                                                    Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("auth", k.getCore()
                               .getId()
                               .toString());
        variables.put("child", k.getIRI()
                                .getId()
                                .toString());
        variables.put("parent", k.getCore()
                                 .getId()
                                 .toString());
        variables.put("relationship", k.getAnyRelationship()
                                       .getId()
                                       .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($auth: String $child: String $parent: String $relationship: String $auth: String) { "
                                            + "createNetworkAuthorization(state: {authority: $auth cardinality: \"_1\" child: $child "
                                            + "name: \"foo\" parent: $parent relationship: $relationship }) {id} }",
                                    variables);
        variables.put("id", result.get("createNetworkAuthorization")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateNetworkAuthorization(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeNetworkAuthorization(id: $id) }",
                variables);
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
                                    "mutation m($auth: String $classifier: String $classification: String) { "
                                            + "createFacet(state: {authority: $auth classifier: $classifier name: \"foo\" "
                                            + "classification: $classification }) {id} }",
                                    variables);
        variables.put("id", result.get("createFacet")
                                  .get("id")
                                  .asText());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateFacet(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema, "mutation m($id: String!) { removeFacet(id: $id) }",
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
                                    "mutation m($service: String $statusCode: String $parent: String $parentStatus: String) { "
                                            + "createParentSequencing(state: {service: $service statusCode: $statusCode parent: $parent "
                                            + "parentStatus: $parentStatus }) {id} }",
                                    variables);
        variables.put("id", result.get("createParentSequencing")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateParentSequencing(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeParentSequencing(id: $id) }",
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
                                  "{ facets { id name attributes { id } classifier {id} classification {id} children { id } authority { id } }}",
                                  variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ agencies { id name description updatedBy {id} authority {id}} }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("agencies")));
        data = execute(schema,
                       "query q($ids: [String]!) { agencies(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("agencies")).get(0));
        data = execute(schema,
                       "query q($id: String!) { agency(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ attributes { id name description keyed indexed valueType } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("attributes")));
        data = execute(schema,
                       "query q($ids: [String]!) { attributes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("attributes")).get(0));
        data = execute(schema,
                       "query q($id: String!) { attribute(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ intervals { id name description }  }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("intervals")));
        data = execute(schema,
                       "query q($ids: [String]!) { intervals(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ locations { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("locations")));
        data = execute(schema,
                       "query q($ids: [String]!) { locations(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("locations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { location(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ products { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("products")));
        data = execute(schema,
                       "query q($ids: [String]!) { products(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("products")).get(0));
        data = execute(schema,
                       "query q($id: String!) { product(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ relationships { id name description inverse { id } } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("relationships")));
        data = execute(schema,
                       "query q($ids: [String]!) { relationships(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("relationships")).get(0));
        data = execute(schema,
                       "query q($id: String!) { relationship(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ statusCodes { id name description failParent propagateChildren } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodes")));
        data = execute(schema,
                       "query q($ids: [String]!) { statusCodes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("statusCodes")).get(0));
        data = execute(schema,
                       "query q($id: String!) { statusCode(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ units{ id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("units")));
        data = execute(schema,
                       "query q($ids: [String]!) { units(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ attributeAuthorizations { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("attributeAuthorizations")));
        data = execute(schema,
                       "query q($ids: [String]!) { attributeAuthorizations(ids:$ids) { id facet {id} jsonValue binaryValue booleanValue integerValue notes numericValue textValue timestampValue updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("attributeAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { attributeAuthorization(id: $id) { id  } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ networkAttributeAuthorizations { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("networkAttributeAuthorizations")));
        data = execute(schema,
                       "query q($ids: [String]!) { networkAttributeAuthorizations(ids:$ids) { id networkAuthorization {id} jsonValue binaryValue booleanValue integerValue notes numericValue textValue timestampValue updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("networkAttributeAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { networkAttributeAuthorization(id: $id) { id  } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ childSequencings { id service {id} nextChild { id } nextChildStatus {id} notes sequenceNumber statusCode {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("childSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { childSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("childSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { childSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ metaProtocols { id  product {id} assignTo {id} deliverFrom{id} deliverTo{id} quantityUnit {id} requester{id} service{id} status{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("metaProtocols")));
        data = execute(schema,
                       "query q($ids: [String]!) { metaProtocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("metaProtocols")).get(0));
        data = execute(schema,
                       "query q($id: String!) { metaProtocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ networkAuthorizations { id authority{id} cardinality child{id} name notes parent{id} relationship{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("networkAuthorizations")));
        data = execute(schema,
                       "query q($ids: [String]!) { networkAuthorizations(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("networkAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { networkAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ parentSequencings { id notes parent{id} parentStatusToSet{id} sequenceNumber statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("parentSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { parentSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("parentSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { parentSequencing(id: $id) { id authority {id} updatedBy {id} } }",
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
                       "query q($ids: [String]!) { protocols(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("protocols")).get(0));
        data = execute(schema,
                       "query q($id: String!) { protocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ selfSequencings { id notes sequenceNumber service{id} setIfActiveSiblings statusCode{id} statusToSet{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("selfSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { selfSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ siblingSequencings { id nextSibling{id} nextSiblingStatus{id} notes sequenceNumber service{id} statusCode{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("siblingSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { siblingSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("siblingSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { siblingSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema,
                       "{ statusCodeSequencings { id child{id} notes parent{id} service{id} updatedBy{id} version } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodeSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { statusCodeSequencings(ids:$ids) { id authority {id} updatedBy {id} } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("statusCodeSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { statusCodeSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

    }

    @Test
    public void testSelfSequencingMutations() throws IllegalArgumentException,
                                              Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("service", k.getAnyProduct()
                                  .getId()
                                  .toString());
        variables.put("statusCode", k.getAnyStatusCode()
                                     .getId()
                                     .toString());
        variables.put("statusToSet", k.getAnyStatusCode()
                                      .getId()
                                      .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($service: String $statusCode: String $statusToSet: String) { createSelfSequencing(state: {service: $service statusCode: $statusCode statusToSet: $statusToSet }) {id} }",
                                    variables);
        variables.put("id", result.get("createSelfSequencing")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateSelfSequencing(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeSelfSequencing(id: $id) }",
                variables);
    }

    @Test
    public void testSiblingSequencingMutations() throws IllegalArgumentException,
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
                                    "mutation m($service: String $statusCode: String $nextSibling: String $nextSiblingStatus: String) "
                                            + "{ createSiblingSequencing(state: {service: $service statusCode: $statusCode nextSibling: $nextSibling "
                                            + "nextSiblingStatus: $nextSiblingStatus }) {id} }",
                                    variables);
        variables.put("id", result.get("createSiblingSequencing")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateSiblingSequencing(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeSiblingSequencing(id: $id) }",
                variables);
    }

    @Test
    public void testStatusCodeSequencingMutations() throws IllegalArgumentException,
                                                    Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("service", k.getAnyProduct()
                                  .getId()
                                  .toString());
        variables.put("child", k.getAnyStatusCode()
                                .getId()
                                .toString());
        variables.put("parent", k.getAnyStatusCode()
                                 .getId()
                                 .toString());
        variables.put("statusCode", k.getAnyStatusCode()
                                     .getId()
                                     .toString());
        ObjectNode result = execute(schema,
                                    "mutation m($service: String $statusCode: String $child: String $parent: String) { "
                                            + "createStatusCodeSequencing(state: {service: $service statusCode: $statusCode parent: $parent "
                                            + "child: $child }) {id} }",
                                    variables);
        variables.put("id", result.get("createStatusCodeSequencing")
                                  .get("id")
                                  .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: String! $auth: String) { updateStatusCodeSequencing(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: String!) { removeStatusCodeSequencing(id: $id) }",
                variables);
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
        ExecutionResult execute = new GraphQL(schema).execute(query,
                                                              new WorkspaceContext(model,
                                                                                   definingProduct),
                                                              variables);
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

    private List<String> ids(ArrayNode in) {
        List<String> ids = new ArrayList<>();
        in.forEach(o -> ids.add(o.get("id")
                                 .asText()));
        return ids;
    }
}
