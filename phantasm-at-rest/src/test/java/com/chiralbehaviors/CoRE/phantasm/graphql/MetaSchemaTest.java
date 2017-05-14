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
    public void testExistentialMutations() throws IllegalArgumentException,
                                           Exception {
        Map<String, Object> variables = new HashMap<>();

        ObjectNode result = execute("mutation m { createAgency(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                                    variables);
        variables.put("id", result.get("createAgency")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateAgency(state: {id: $id notes:\"foo\" authority: $id}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeAgency(id: $id) }",
                variables);

        result = execute("mutation m { createAttribute(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createAttribute")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateAttribute(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeAttribute(id: $id) }",
                variables);

        result = execute("mutation m { createInterval(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createInterval")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateInterval(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeInterval(id: $id) }",
                variables);

        result = execute("mutation m { createLocation(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createLocation")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateLocation(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeLocation(id: $id) }",
                variables);

        result = execute("mutation m { createProduct(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createProduct")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateProduct(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeProduct(id: $id) }",
                variables);

        result = execute("mutation m { createRelationship(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createRelationship")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateRelationship(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeRelationship(id: $id) }",
                variables);

        result = execute("mutation m { createStatusCode(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createStatusCode")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateStatusCode(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeStatusCode(id: $id) }",
                variables);

        result = execute("mutation m { createUnit(state: {name:\"foo\" notes:\"bar\"}) {id} }",
                         variables);
        variables.put("id", result.get("createUnit")
                                  .get("id")
                                  .asText());
        execute("mutation m($id: String!) { updateUnit(state: {id: $id notes:\"foo\"}) {id} }",
                variables);

        execute("mutation m($id: String!) { removeUnit(id: $id) }", variables);
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
        data = execute("query q($ids: [String]!) { agencies(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("agencies")).get(0));
        data = execute("query q($id: String!) { agency(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ attributes { id name description keyed indexed valueType } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("attributes")));
        data = execute("query q($ids: [String]!) { attributes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("attributes")).get(0));
        data = execute("query q($id: String!) { attribute(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ intervals { id name description }  }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("intervals")));
        data = execute("query q($ids: [String]!) { intervals(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ locations { id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("locations")));
        data = execute("query q($ids: [String]!) { locations(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("locations")).get(0));
        data = execute("query q($id: String!) { location(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ products { id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("products")));
        data = execute("query q($ids: [String]!) { products(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("products")).get(0));
        data = execute("query q($id: String!) { product(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ relationships { id name description inverse { id } } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("relationships")));
        data = execute("query q($ids: [String]!) { relationships(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("relationships")).get(0));
        data = execute("query q($id: String!) { relationship(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ statusCodes { id name description failParent propagateChildren } }",
                       variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodes")));
        data = execute("query q($ids: [String]!) { statusCodes(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("statusCodes")).get(0));
        data = execute("query q($id: String!) { statusCode(id: $id) { id name description } }",
                       variables);
        assertNotNull(data);

        data = execute("{ units{ id name description } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("units")));
        data = execute("query q($ids: [String]!) { units(ids: $ids) { id name description } }",
                       variables);
        assertNotNull(data);
    }

    private ObjectNode execute(String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
        ExistentialContext context = new ExistentialContext(model,
                                                            definingProduct);
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

    private List<String> ids(ArrayNode in) {
        List<String> ids = new ArrayList<>();
        in.forEach(o -> ids.add(o.get("id")
                                 .asText()));
        return ids;
    }
}
