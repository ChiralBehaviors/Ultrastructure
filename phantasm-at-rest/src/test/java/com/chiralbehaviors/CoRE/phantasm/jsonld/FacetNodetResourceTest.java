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

import java.io.IOException;

import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class FacetNodetResourceTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";

    private WorkspaceScope scope;

    @Before
    public void getWorkspace() throws IOException {
        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(FacetNodetResourceTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction().commit();
        em.getTransaction().begin();
        scope = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
    }

    @Test
    public void testNode() throws Exception {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUriBuilder()).thenReturn(new JerseyUriBuilder()).thenReturn(new JerseyUriBuilder()).thenReturn(new JerseyUriBuilder());
        FacetNodeResource resource = new FacetNodeResource(emf, uriInfo);
        try {
            Thing1 thing1 = (Thing1) model.construct(Thing1.class, "test",
                                                     "testy");
            thing1.setAliases(new String[] { "smith", "jones" });
            em.getTransaction().commit();
            em.getTransaction().begin();
            FacetNode<Product, ProductNetwork> facet = resource.getProduct(thing1.getRuleform().getId().toString(),
                                                                           scope.lookup("kernel",
                                                                                        "IsA").getId().toString(),
                                                                           scope.lookup("Thing1").getId().toString());
            assertNotNull(facet);
            ObjectMapper objMapper = new ObjectMapper();
            System.out.println(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(facet));
        } finally {
            resource.close();
        }
    }
}
