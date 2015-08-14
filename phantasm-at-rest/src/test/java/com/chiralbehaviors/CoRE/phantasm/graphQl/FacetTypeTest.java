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

package com.chiralbehaviors.CoRE.phantasm.graphQl;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.graphql.FacetType;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.ResourcesTest;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 *
 */
public class FacetTypeTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1";

    @BeforeClass
    public static void initialize() throws Exception {
        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(ResourcesTest.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.getTransaction().commit();
    }

    protected WorkspaceScope scope;

    @Before
    public void loadWorkspace() {
        em.getTransaction().begin();
        scope = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI));
    }

    @Test
    public void testBuild() {
        FacetType<Product, ProductNetwork> thing1 = new FacetType<>(scope.lookup("kernel",
                                                                                 "IsA"),
                                                                    (Product) scope.lookup("Thing1"),
                                                                    model);
        thing1.buid();
        System.out.println(thing1.getReadType());
        Map<String, Aspect<?>> referencedTypes = thing1.getReferencedTypes();
        Aspect<Location> mavenArtifact = new Aspect<>(scope.lookup("kernel",
                                                                   "IsA"),
                                                      (Location) scope.lookup("MavenArtifact"));
        assertEquals(mavenArtifact, referencedTypes.get("MavenArtifact"));
        Aspect<Product> thing2 = new Aspect<>(scope.lookup("kernel", "IsA"),
                                              (Product) scope.lookup("Thing2"));
        assertEquals(thing2, referencedTypes.get("Thing2"));
        Aspect<Product> anyProduct = new Aspect<>(scope.lookup("kernel",
                                                               "AnyRelationship"),
                                                  (Product) scope.lookup("kernel",
                                                                         "AnyProduct"));
        assertEquals(anyProduct, referencedTypes.get("AnyProduct"));
    }
}
