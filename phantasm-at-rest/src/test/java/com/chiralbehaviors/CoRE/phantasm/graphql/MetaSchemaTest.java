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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class MetaSchemaTest extends AbstractModelTest {

    @Test
    public void testFacets() throws Exception {

        ThreadLocal<Product> currentWorkspace = new ThreadLocal<>();
        currentWorkspace.set(model.getKernel()
                                  .getKernelWorkspace());
        GraphQLSchema schema = WorkspaceSchema.buildMeta();
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data = execute(schema,
                                  "{ facets { id name attributes { id authorizedAttribute { id name } } children { id name parent { id name } relationship { id name } child { id name } } }}",
                                  variables);
        assertNotNull(data);
        data = execute(schema,
                       "{ agencies { id name description } attributes { id name description } }",
                       variables);
        assertNotNull(data);
        data = execute(schema,
                       "{ intervals { id name description } locations { id name description } }",
                       variables);
        assertNotNull(data);
        data = execute(schema,
                       "{ products { id name description } relationships { id name description } }",
                       variables);
        assertNotNull(data);
        data = execute(schema,
                       "{ statusCodes { id name description } units{ id name description } }",
                       variables);
        assertNotNull(data);
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
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
