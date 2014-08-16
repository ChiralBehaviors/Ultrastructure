/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.junit.Test;

import com.chiralbehaviors.CoRE.event.status.StatusCode;

/**
 * @author hhildebrand
 *
 */
public class SccTest {

    @Test
    public void testScc() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(new UUID(0, 1)),
                new StatusCode(new UUID(0, 2)), new StatusCode(new UUID(0, 3)),
                new StatusCode(new UUID(0, 4)), new StatusCode(new UUID(0, 5)),
                new StatusCode(new UUID(0, 6)), new StatusCode(new UUID(0, 7)),
                new StatusCode(new UUID(0, 8)), new StatusCode(new UUID(0, 9)) };
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
                return o1.getId().compareTo(o2.getId());
            }
        };

        StatusCode[] expected = new StatusCode[] { codes[6], codes[7], codes[8] };
        Arrays.sort(expected, scComparator);
        StatusCode[] got = sccs.get(0);
        Arrays.sort(got, scComparator);
        assertArrayEquals(expected, got);

        expected = new StatusCode[] { codes[0], codes[1], codes[2] };
        Arrays.sort(expected, scComparator);

        got = sccs.get(1);
        Arrays.sort(got, scComparator);
        assertArrayEquals(expected, got);

        expected = new StatusCode[] { codes[4], codes[5], codes[3] };
        Arrays.sort(expected, scComparator);

        got = sccs.get(2);
        Arrays.sort(got, scComparator);
        assertArrayEquals(expected, got);
    }
}
