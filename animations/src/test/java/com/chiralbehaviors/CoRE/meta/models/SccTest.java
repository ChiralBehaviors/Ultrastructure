/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.StatusCode;

/**
 * @author hhildebrand
 *
 */
public class SccTest {

    @Test
    public void testScc() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(new UUID(0, 1)),
                                                new StatusCode(new UUID(0, 2)),
                                                new StatusCode(new UUID(0, 3)),
                                                new StatusCode(new UUID(0, 4)),
                                                new StatusCode(new UUID(0, 5)),
                                                new StatusCode(new UUID(0, 6)),
                                                new StatusCode(new UUID(0, 7)),
                                                new StatusCode(new UUID(0, 8)),
                                                new StatusCode(new UUID(0,
                                                                        9)) };
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
        Comparator<StatusCode> scComparator = new Comparator<StatusCode>() {

            @Override
            public int compare(StatusCode o1, StatusCode o2) {
                return o1.getId()
                         .compareTo(o2.getId());
            }
        };

        StatusCode[] expected;

        sccs = sccs.stream()
                   .peek(g -> Arrays.sort(g, scComparator))
                   .sorted((a, b) -> a[0].getId()
                                         .compareTo(b[0].getId()))
                   .collect(Collectors.toList());

        expected = new StatusCode[] { codes[0], codes[1], codes[2] };
        Arrays.sort(expected, scComparator);
        StatusCode[] got = sccs.get(0);
        assertArrayEquals(expected, got);

        expected = new StatusCode[] { codes[4], codes[5], codes[3] };
        Arrays.sort(expected, scComparator);
        got = sccs.get(1);
        assertArrayEquals(expected, got);

        expected = new StatusCode[] { codes[6], codes[7], codes[8] };
        Arrays.sort(expected, scComparator);
        got = sccs.get(2);
        assertArrayEquals(expected, got);
    }
}
