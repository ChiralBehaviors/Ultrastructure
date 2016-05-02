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

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class MetaSchemaTest extends AbstractModelTest {
    private WorkspaceImporter importer;

    @Test
    public void testFacets() throws Exception {
        importer = WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream(ACM_95_WSP),
                                              model);

        GraphQLSchema schema = WorkspaceSchema.buildMeta();
        Map<String, Object> variables = new HashMap<>();
        ObjectNode data = execute(schema,
                                  "{ facets { id name attributes { id authorizedAttribute { id name } } children { id name parent { id name } relationship { id name } child { id name } } }}",
                                  variables);
        assertNotNull(data);

        data = execute(schema, "{ agencies { id name description } }",
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

        data = execute(schema, "{ attributeAuthorizations { id } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("attributeAuthorizations")));
        data = execute(schema,
                       "query q($ids: [String]!) { attributeAuthorizations(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("attributeAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { attributeAuthorization(id: $id) { id  } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ childSequencings { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("childSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { childSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("childSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { childSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ metaProtocols { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("metaProtocols")));
        data = execute(schema,
                       "query q($ids: [String]!) { metaProtocols(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("metaProtocols")).get(0));
        data = execute(schema,
                       "query q($id: String!) { metaProtocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ networkAuthorizations { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("networkAuthorizations")));
        data = execute(schema,
                       "query q($ids: [String]!) { networkAuthorizations(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("networkAuthorizations")).get(0));
        data = execute(schema,
                       "query q($id: String!) { networkAuthorization(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ parentSequencings { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("parentSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { parentSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("parentSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { parentSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ protocols { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("protocols")));
        data = execute(schema,
                       "query q($ids: [String]!) { protocols(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("protocols")).get(0));
        data = execute(schema,
                       "query q($id: String!) { protocol(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ selfSequencings { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("selfSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { selfSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ siblingSequencings { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("siblingSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { siblingSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id", ids(data.withArray("siblingSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { siblingSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

        data = execute(schema, "{ statusCodeSequencings { id  } }", variables);
        assertNotNull(data);
        variables.put("ids", ids(data.withArray("statusCodeSequencings")));
        data = execute(schema,
                       "query q($ids: [String]!) { statusCodeSequencings(ids:$ids) { id } }",
                       variables);
        assertNotNull(data);
        variables.put("id",
                      ids(data.withArray("statusCodeSequencings")).get(0));
        data = execute(schema,
                       "query q($id: String!) { statusCodeSequencing(id: $id) { id } }",
                       variables);
        assertNotNull(data);

    }

    private List<String> ids(ArrayNode in) {
        List<String> ids = new ArrayList<>();
        in.forEach(o -> ids.add(o.get("id")
                                 .asText()));
        return ids;
    }

    private ObjectNode execute(GraphQLSchema schema, String query,
                               Map<String, Object> variables) throws IllegalArgumentException,
                                                              Exception {
        ExecutionResult execute = new GraphQL(schema).execute(query,
                                                              new WorkspaceContext(model,
                                                                                   importer.getWorkspace()
                                                                                           .getDefiningProduct()),
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
