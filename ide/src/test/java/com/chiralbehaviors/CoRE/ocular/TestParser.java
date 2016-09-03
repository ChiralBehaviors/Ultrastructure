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

package com.chiralbehaviors.CoRE.ocular;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.chiralbehaviors.graphql.layout.AutoLayout;
import com.chiralbehaviors.graphql.layout.Relation;
import com.chiralbehaviors.graphql.layout.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class TestParser {

    protected PhantasmApplication application = new PhantasmApplication();

    @After
    public void after() {
        application.stop();
    }

    @Test
    public void testSimple() throws Exception {
        String input = Utils.getDocument(TestParser.class.getResourceAsStream("/testQuery.gql"));
        String source = "allFilms";
        Relation schema = (Relation) AutoLayout.buildSchema(input, source);
        JsonNode data = new ObjectMapper().readTree(TestParser.class.getResourceAsStream("/testQuery.data"));
        schema.measure(data.get("data")
                           .get(source));
        assertEquals("allFilms", schema.getLabel());
        List<SchemaNode> children = schema.getChildren();
        assertEquals(1, children.size());

        Relation current = (Relation) children.get(0);
        assertEquals("films", current.getLabel());
        children = current.getChildren();
        assertEquals(5, children.size());
        assertEquals("title", children.get(0)
                                      .getLabel());
        assertEquals("episodeID", children.get(1)
                                          .getLabel());
        assertEquals("director", children.get(2)
                                         .getLabel());
        assertEquals("producers", children.get(3)
                                          .getLabel());

        current = (Relation) children.get(4);
        assertEquals("vehicleConnection", current.getLabel());
        children = current.getChildren();
        assertEquals(1, children.size());

        current = (Relation) children.get(0);
        assertEquals("vehicles", current.getLabel());
        children = current.getChildren();
        assertEquals(4, children.size());
        assertEquals("name", children.get(0)
                                     .getLabel());
        assertEquals("model", children.get(1)
                                      .getLabel());
        assertEquals("manufacturers", children.get(2)
                                              .getLabel());

        current = (Relation) children.get(3);
        assertEquals("pilotConnection", current.getLabel());
        children = current.getChildren();
        assertEquals(1, children.size());

        current = (Relation) children.get(0);
        assertEquals("pilots", current.getLabel());
        children = current.getChildren();
        assertEquals(4, children.size());

        assertEquals("name", children.get(0)
                                     .getLabel());
        assertEquals("birthYear", children.get(1)
                                          .getLabel());
        assertEquals("eyeColor", children.get(2)
                                         .getLabel());
        assertEquals("gender", children.get(3)
                                       .getLabel());
    }
}
