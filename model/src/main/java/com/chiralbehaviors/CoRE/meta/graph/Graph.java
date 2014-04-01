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

package com.chiralbehaviors.CoRE.meta.graph;

import java.util.List;

/**
 * @author hparry
 * 
 */
public interface Graph<NodeType, EdgeType> {

    Graph<NodeType, EdgeType> addEdge(Edge<EdgeType> e);

    Graph<NodeType, EdgeType> addNode(Node<NodeType> n);

    List<Edge<EdgeType>> getEdges();

    List<Node<NodeType>> getNodes();

    Graph<NodeType, EdgeType> intersection(Graph<NodeType, EdgeType> g);

    Graph<NodeType, EdgeType> union(Graph<NodeType, EdgeType> g);

}
