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
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class JobSchemTest extends AbstractModelTest {

    //@Test
    public void testCreateAndMutate() throws Exception {

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

    //@Test
    public void testMutation() throws Exception {
    }

    // @Test
    public void testJobSchema() throws Exception {
    }

    // @Test
    public void testCreate() throws Exception {
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
