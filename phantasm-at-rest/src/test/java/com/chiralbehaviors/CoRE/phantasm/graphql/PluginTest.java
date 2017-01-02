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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing2;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class PluginTest extends AbstractModelTest {

    private static final String COM_CHIRALBEHAVIORS_CO_RE_PHANTASM_PLUGIN_TEST = "com.chiralbehaviors.CoRE.phantasm.plugin.test";

    @Before
    public void initializeScope() throws IOException {
        WorkspaceImporter.manifest(FacetTypeTest.class.getResourceAsStream("/thing.wsp"),
                                   model);
    }

    @Test
    public void testPlugin() throws Exception {
        URLClassLoader executionScope = FacetFields.configureExecutionScope(Collections.singletonList("lib/test-plugin.jar"));
        ClassLoader prev = Thread.currentThread()
                                 .getContextClassLoader();
        Thread.currentThread()
              .setContextClassLoader(executionScope);
        try {
            executePlugin(executionScope);
        } finally {
            Thread.currentThread()
                  .setContextClassLoader(prev);
        }
    }

    @SuppressWarnings("unchecked")
    private void executePlugin(URLClassLoader executionScope) throws ClassNotFoundException,
                                                              IllegalArgumentException,
                                                              IllegalAccessException,
                                                              NoSuchFieldException,
                                                              SecurityException,
                                                              NoSuchMethodException,
                                                              InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addClassLoader(executionScope)
                                                                            .addUrls(executionScope.getURLs()));
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        Class<?> thing1Plugin = executionScope.loadClass(String.format("%s.Thing1_Plugin",
                                                                       COM_CHIRALBEHAVIORS_CO_RE_PHANTASM_PLUGIN_TEST));
        assertNotNull(thing1Plugin);
        assertNotNull(thing1Plugin.getAnnotation(Plugin.class));
        Set<Class<?>> plugins = reflections.getTypesAnnotatedWith(Plugin.class);
        assertFalse(plugins.isEmpty());
        AtomicReference<String> passThrough = (AtomicReference<String>) thing1Plugin.getField("passThrough")
                                                                                    .get(null);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "hello");
        String hello = "goodbye";
        variables.put("description", hello);
        QueryRequest request = new QueryRequest("mutation m ($name: String!, $description: String) { "
                                                + "createThing1("
                                                + "  state: { "
                                                + "     setName: $name, "
                                                + "     setDescription: $description"
                                                + "   }) { id name description } }",
                                                variables);
        String bob = "Give me food or give me slack or kill me";
        passThrough.set(bob);
        GraphQLSchema schema = new WorkspaceSchema().build(scope.getWorkspace(),
                                                           model, reflections);

        ExecutionResult execute = new WorkspaceContext(model,
                                                       scope.getWorkspace()
                                                            .getDefiningProduct()).execute(schema,
                                                                                           request.getQuery(),
                                                                                           request.getVariables());

        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) execute.getData()).get("createThing1");
        assertNotNull(thing1Result);
        assertEquals(bob, thing1Result.get("description"));
        String thing1ID = (String) thing1Result.get("id");
        assertNotNull(thing1ID);
        Thing1 thing1 = model.wrap(Thing1.class, model.records()
                                                      .resolve(UUID.fromString(thing1ID)));
        assertEquals(bob, thing1.getDescription());

        String apple = "Connie";
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        apple, "Her Dobbsness");
        thing2.setName(apple);
        thing2.setDescription("Her Dobbsness");
        thing1.setThing2(thing2);
        variables = new HashMap<>();
        variables.put("id", thing1ID);
        variables.put("test", "me");
        request = new QueryRequest("query it($id: String!, $test: String) { thing1(id: $id) {id name instanceMethod instanceMethodWithArgument(arg1: $test) } }",
                                   variables);

        execute = new GraphQL(schema).execute(request.getQuery(),
                                              new PhantasmCRUD(model),
                                              request.getVariables());

        assertTrue(execute.getErrors()
                          .toString(),
                   execute.getErrors()
                          .isEmpty());

        thing1Result = (Map<String, Object>) ((Map<String, Object>) execute.getData()).get("thing1");
        assertNotNull(thing1Result);
        assertEquals(apple, thing1Result.get("instanceMethod"));
        assertEquals("me", passThrough.get());
        assertEquals(apple, thing1Result.get("instanceMethodWithArgument"));
    }
}
