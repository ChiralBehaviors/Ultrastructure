/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author halhildebrand
 *
 */
public class SpaTest {
    @Test
    public void testContext() throws Exception {
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(getClass().getResourceAsStream("/test-app.json"));
        Spa spa = new Spa(node);
        Page page = spa.getRoot();
        Context ctx = new Context("foo", page);
        assertEquals("foo", ctx.getFrame());
        assertEquals(page, ctx.getPage());
        assertEquals("singlePageApplications", ctx.getRoot()
                                                  .getField());
        Relation relation = new Relation("singlePageApplications");
        assertEquals(page.getNavigation(relation), ctx.getNavigation(relation));
    }

    @Test
    public void testSpa() throws Exception {
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(getClass().getResourceAsStream("/test-app.json"));
        Spa spa = new Spa(node);
        Page page = spa.getRoot();
        assertNotNull(page);
        assertEquals("Applications", page.getTitle());
        assertEquals("All applications", page.getName());
        assertEquals("Page with all applications", page.getDescription());
        assertNull(page.getFrame());
        assertNotNull(page.getQuery());

        Relation relation = new Relation("singlePageApplications");
        Action create = page.getCreate(relation);
        assertNotNull(create);
        Map<String, String> extract = create.getExtract();
        assertNotNull(extract);
        assertEquals(1, extract.size());
        assertEquals("bar", extract.get("foo"));
        assertNotNull(create.getQuery());
        assertEquals("cc2f49ae-1c72-11e7-b9f2-31cf61e9d1f6",
                     create.getFrameBy());

        assertNotNull(page.getUpdate(relation));
        assertNotNull(page.getDelete(relation));

        Launch launch = page.getLaunch(relation);
        assertNotNull(launch);
        assertNull(launch.getFrame());
        assertEquals("id", launch.getFrameBy());
        assertEquals("cc2f49ae-1c72-11e7-b9f2-31cf61e9d1f6",
                     launch.getImmediate());
        assertEquals("id", launch.getLaunchBy());

        Route route = page.getNavigation(relation);
        assertNotNull(route);
        assertEquals("workspace", route.getFrameBy());
        extract = route.getExtract();
        assertNotNull(extract);
        assertEquals("bar", extract.get("foo"));
        assertEquals("foo", route.getPath());
    }
}
