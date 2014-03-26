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

package com.hellblazer.CoRE.meta.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.event.status.StatusCode;

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

    public void visit(StatusCode StatusCode) {
        if (low.containsKey(StatusCode)) {
            return;
        }
        int num = low.size();
        low.put(StatusCode, num);
        int stackPos = stack.size();
        stack.add(StatusCode);
        for (StatusCode successor : graph.get(StatusCode)) {
            visit(successor);
            low.put(StatusCode,
                    Math.min(low.get(StatusCode), low.get(successor)));
        }

        if (num == low.get(StatusCode).intValue()) {
            List<StatusCode> component = stack.subList(stackPos, stack.size());
            stack = stack.subList(0, stackPos);

            if (component.size() > 1) {// ignore trivial SCCs of a single
                                       // StatusCode
                StatusCode[] array = component.toArray(new StatusCode[component.size()]);
                result.add(array);
            }

            for (StatusCode item : component) {
                low.put(item, graph.size());
            }
        }

    }
}