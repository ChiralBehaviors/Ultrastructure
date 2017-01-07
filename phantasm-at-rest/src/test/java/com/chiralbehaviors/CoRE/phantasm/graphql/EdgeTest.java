package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;

/**
 * 
 * @author halhildebrand
 *
 */
public class EdgeTest extends AbstractModelTest {

    @Before
    public void initializeScope() throws IOException {
        WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream("/thing.wsp"),
                                   model);
    }

    @Test
    public void testEdgeQueries() throws Exception {
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(WellKnownObject.KERNEL_IRI));

        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model);

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", WorkspaceAccessor.uuidOf(THING_URI)
                                             .toString());
        QueryRequest request = new QueryRequest("query ($id: String!) { workspace(id: $id) { imports { _edge { ... on _workspace_import { lookupOrder namespace } } } } }",
                                                variables);

        ExecutionResult execute = new WorkspaceContext(model,
                                                       scope.getWorkspace()
                                                            .getDefiningProduct()).execute(schema,
                                                                                           request.getQuery(),
                                                                                           request.getVariables());
        assertNotNull(execute);
        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        JsonNode result = new ObjectMapper().valueToTree(execute.getData());
        assertNotNull(result);
        assertEquals("kernel", result.get("workspace")
                                     .get("imports")
                                     .get(0)
                                     .get("_edge")
                                     .get("namespace")
                                     .asText());
        model.create().configuration().connectionProvider().acquire().commit();
    }
}
