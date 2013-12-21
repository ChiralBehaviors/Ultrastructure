/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hellblazer.CoRE.event.status.StatusCode;

/**
 * @author hhildebrand
 * 
 */
public class SccTest {

    @Test
    public void testScc() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(0L),
                new StatusCode(1L), new StatusCode(2L), new StatusCode(3L),
                new StatusCode(4L), new StatusCode(5L), new StatusCode(6L),
                new StatusCode(7L), new StatusCode(8L) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[0], codes[6]));
        graph.put(codes[3], asList(codes[4]));
        graph.put(codes[4], asList(codes[5], codes[6]));
        graph.put(codes[5], asList(codes[3]));
        graph.put(codes[6], asList(codes[7]));
        graph.put(codes[7], asList(codes[8]));
        graph.put(codes[8], asList(codes[6]));
        List<StatusCode[]> sccs = new SCC(graph).getStronglyConnectedComponents();
        assertNotNull(sccs);
        assertEquals(3, sccs.size());
        int i = 0;
        for (StatusCode n : asList(codes[6], codes[7], codes[8])) {
            assertEquals(n, sccs.get(0)[i++]);
        }
        i = 0;
        for (StatusCode n : asList(codes[0], codes[1], codes[2])) {
            assertEquals(n, sccs.get(1)[i++]);
        }
        i = 0;
        for (StatusCode n : asList(codes[3], codes[4], codes[5])) {
            assertEquals(n, sccs.get(2)[i++]);
        }
    }
}
