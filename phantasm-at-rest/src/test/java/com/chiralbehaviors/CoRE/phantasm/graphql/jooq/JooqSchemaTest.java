package com.chiralbehaviors.CoRE.phantasm.graphql.jooq;

import static graphql.schema.GraphQLObjectType.newObject;
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
                                            .build();
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
    public void testAttributeAuthQueries() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data = execute(schema,
                                  "{ ExistentialAttributeAuthorizationRecords { id authority updatedBy } }",
                                  variables);
        assertNotNull(data);
        variables.put("ids",
                      ids(data.withArray("ExistentialAttributeAuthorizationRecords")));
        data = execute(schema,
                       "query q($ids: [ID]) { ExistentialAttributeAuthorizationRecords(ids:$ids) { id facet jsonValue binaryValue booleanValue integerValue notes numericValue textValue timestampValue updatedBy } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("ExistentialAttributeAuthorizationRecords")).get(0));
        data = execute(schema,
                       "query q($id: ID!) { ExistentialAttributeAuthorizationRecord(id: $id) { id  } }",
                       variables);
        assertNotNull(data);
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
                                    "mutation m($auth: ID $attr: ID $facet: ID) { createExistentialAttributeAuthorizationRecord(state: {facet: $facet authority: $auth authorizedAttribute:$attr binaryValue: \"ab\" booleanValue: true integerValue: 1 jsonValue:\"null\" numericValue: 1.0 textValue: \"foo\" timestampValue: 1 }) {id} }",
                                    variables);
        variables.put("id",
                      result.get("createExistentialAttributeAuthorizationRecord")
                            .get("id")
                            .asText());
        variables.put("auth", model.getKernel()
                                   .getCore()
                                   .getId());
        execute(schema,
                "mutation m($id: ID! $auth: ID) { updateExistentialAttributeAuthorizationRecord(state: {id: $id notes:\"foo\" authority: $auth}) {id} }",
                variables);

        execute(schema,
                "mutation m($id: ID!) { deleteExistentialAttributeAuthorizationRecord(id: $id) }",
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

    private List<String> ids(ArrayNode in) {
        List<String> ids = new ArrayList<>();
        in.forEach(o -> ids.add(o.get("id")
                                 .asText()));
        return ids;
    }
}
