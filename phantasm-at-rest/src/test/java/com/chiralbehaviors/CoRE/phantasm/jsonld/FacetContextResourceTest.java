/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContextResource;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * @author hhildebrand
 *
 */
public class FacetContextResourceTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";

    @BeforeClass
    public static void loadWorkspace() throws Exception {
        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(FacetContextResourceTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction().commit();
    }

    private WorkspaceScope scope;

    @Before
    public void getWorkspace() {
        scope = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
    }

    @Test
    public void testContext() throws Exception {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUriBuilder()).thenReturn(new JerseyUriBuilder()).thenReturn(new JerseyUriBuilder()).thenReturn(new JerseyUriBuilder());
        FacetContextResource resource = new FacetContextResource(emf, uriInfo);
        JsonNode container = resource.getProduct(model.getKernel().getIsA().getId().toString(),
                                                 scope.lookup("Thing2").getId().toString());
        assertNotNull(container);
        ObjectMapper objMapper = new ObjectMapper();
        System.out.println(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(container));
        ObjectNode context = (ObjectNode) container.get(Constants.CONTEXT);
        assertNotNull(context);
        ObjectNode thing1 = (ObjectNode) context.get("Thing1");
        assertNotNull(thing1);
        ObjectNode derivedFrom = (ObjectNode) context.get("derivedFrom");
        assertNotNull(derivedFrom);
        ObjectNode thing3 = (ObjectNode) context.get("Thing3");
        assertNotNull(thing3);
        TextNode id = (TextNode) thing1.get(Constants.ID);
        assertNotNull(id);
    }
}
