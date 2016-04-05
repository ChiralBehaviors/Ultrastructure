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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.domain.StatusCode;

/**
 * Use Tarjan's algorithm to detect all strongly-connected components (SCCs) in
 * the graph.
 *
 * @author hhildebrand
 *
 */
public class SCC {
    private final Map<StatusCode, List<StatusCode>> graph;
    private final Map<StatusCode, Integer>          low    = new HashMap<StatusCode, Integer>();
    private final List<StatusCode[]>                result = new ArrayList<StatusCode[]>();
    private List<StatusCode>                        stack  = new ArrayList<StatusCode>();

    public SCC(Map<StatusCode, List<StatusCode>> graph) {
        this.graph = graph;
    }

    public List<StatusCode[]> getStronglyConnectedComponents() {
        for (StatusCode StatusCode : graph.keySet()) {
            visit(StatusCode);
        }
        return result;
    }

    public void visit(StatusCode statusCode) {
        if (low.containsKey(statusCode)) {
            return;
        }
        int num = low.size();
        low.put(statusCode, num);
        int stackPos = stack.size();
        stack.add(statusCode);
        for (StatusCode successor : graph.get(statusCode)) {
            visit(successor);
            low.put(statusCode,
                    Math.min(low.get(statusCode), low.get(successor)));
        }

        if (num == low.get(statusCode)
                      .intValue()) {
            List<StatusCode> component = stack.subList(stackPos, stack.size());
            stack = stack.subList(0, stackPos);

            if (component.size() > 1) {
                StatusCode[] array = component.toArray(new StatusCode[component.size()]);
                result.add(array);
            }

            for (StatusCode item : component) {
                low.put(item, graph.size());
            }
        }

    }
}