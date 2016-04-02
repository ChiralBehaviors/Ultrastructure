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

package com.chiralbehaviors.CoRE.phantasm.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing3;

import graphql.ExecutionResult;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchemaTest extends ThingWorkspaceTest {

    @Test
    public void testGraphQlResource() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        Thing3 thing3 = model.construct(Thing3.class, ExistentialDomain.Product,
                                        "Thingy", "a favorite thing");
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "model", "model artifact");
        artifact.setArtifactID("com.chiralbehaviors.CoRE");
        artifact.setArtifactID("model");
        artifact.setVersion("0.0.2-SNAPSHOT");
        artifact.setType("jar");

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  ExistentialDomain.Location,
                                                  "animations",
                                                  "animations artifact");
        artifact2.setArtifactID("com.chiralbehaviors.CoRE");
        artifact2.setArtifactID("animations");
        artifact2.setVersion("0.0.2-SNAPSHOT");
        artifact2.setType("jar");

        thing1.setAliases(new String[] { "smith", "jones" });
        String uri = "http://example.com";
        thing1.setURI(uri);
        thing1.setDerivedFrom(artifact);
        thing1.setThing2(thing2);
        thing2.addThing3(thing3);

        thing3.addDerivedFrom(artifact);
        thing3.addDerivedFrom(artifact2);

        GraphQlResource resource = new GraphQlResource(getClass().getClassLoader());
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", thing1.getRuleform()
                                  .getId()
                                  .toString());
        QueryRequest request = new QueryRequest("query it($id: String!) { Thing1(id: $id) {id name thing2 {id name thing3s {id name  derivedFroms {id name}}} derivedFrom {id name}}}",
                                                variables);
        ExecutionResult result;
        try {
            result = resource.query(null, THING_URI, request);
        } catch (WebApplicationException e) {
            fail(e.getResponse()
                  .getEntity()
                  .toString());
            return;
        }
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> thing1Result = (Map<String, Object>) ((Map<String, Object>) result.getData()).get("Thing1");
        assertNotNull(thing1Result);
        assertEquals(thing1.getName(), thing1Result.get("name"));
        assertEquals(thing1.getRuleform()
                           .getId()
                           .toString(),
                     thing1Result.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> thing2Result = (Map<String, Object>) thing1Result.get("thing2");
        assertNotNull(thing2Result);
        assertEquals(thing2.getName(), thing2Result.get("name"));
        assertEquals(thing2.getRuleform()
                           .getId()
                           .toString(),
                     thing2Result.get("id"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> thing3s = (List<Map<String, Object>>) thing2Result.get("thing3s");
        assertNotNull(thing3s);
        assertEquals(1, thing3s.size());
        Map<String, Object> thing3Result = thing3s.get(0);
        assertEquals(thing3.getName(), thing3Result.get("name"));
        assertEquals(thing3.getRuleform()
                           .getId()
                           .toString(),
                     thing3Result.get("id"));

    }
}
