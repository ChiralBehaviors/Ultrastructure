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

package com.chiralbehaviors.CoRE.meta.graph.impl;

import java.util.LinkedList;
import java.util.List;

import com.chiralbehaviors.CoRE.meta.graph.Edge;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.meta.graph.Node;

/**
 * A graph of nodes and edges
 *
 * @author hparry
 *
 */
public class GraphImpl<NodeType, EdgeType> implements Graph<NodeType, EdgeType> {

    private List<Edge<EdgeType>> edges;
    private List<Node<NodeType>> nodes;

    public GraphImpl(List<Node<NodeType>> nodes, List<Edge<EdgeType>> edges) {
        this.nodes = nodes;
        this.edges = edges;
        if (this.nodes == null) {
            this.nodes = new LinkedList<Node<NodeType>>();
        }
        if (this.edges == null) {
            this.edges = new LinkedList<Edge<EdgeType>>();
        }
    }

    /**
     * @param erisNodes
     * @param edges2
     */
    public GraphImpl(Node<NodeType>[] nodes, List<Edge<EdgeType>> edges) {
        this.nodes = new LinkedList<Node<NodeType>>();
        for (Node<NodeType> node : nodes) {
            this.nodes.add(node);
        }
        this.edges = edges;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.graph.IGraph#addEdge(com.chiralbehaviors
     * .CoRE.meta .graph.IEdge)
     */
    @Override
    public Graph<NodeType, EdgeType> addEdge(Edge<EdgeType> e) {
        edges.add(e);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.graph.IGraph#addNode(com.chiralbehaviors
     * .CoRE.meta .graph.INode)
     */
    @Override
    public Graph<NodeType, EdgeType> addNode(Node<NodeType> n) {
        nodes.add(n);
        return this;
    }

    @Override
    public List<Edge<EdgeType>> getEdges() {
        return edges;
    }

    @Override
    public List<Node<NodeType>> getNodes() {
        return nodes;
    }

    @Override
    public GraphImpl<NodeType, EdgeType> intersection(Graph<NodeType, EdgeType> g) {
        List<Node<NodeType>> nodesToRemove = new LinkedList<Node<NodeType>>();
        for (Node<NodeType> n : nodes) {
            if (!g.getNodes().contains(n)) {
                nodesToRemove.add(n);
            }
        }
        for (Node<NodeType> n : nodesToRemove) {
            nodes.remove(n);
        }

        List<Edge<EdgeType>> edgesToRemove = new LinkedList<Edge<EdgeType>>();
        for (Edge<EdgeType> e : edges) {
            if (!g.getEdges().contains(e)) {
                edgesToRemove.add(e);
            }
        }

        for (Edge<EdgeType> e : edgesToRemove) {
            edges.remove(e);
        }
        return this;
    }

    @Override
    public GraphImpl<NodeType, EdgeType> union(Graph<NodeType, EdgeType> g) {
        nodes.addAll(g.getNodes());
        edges.addAll(g.getEdges());
        return this;
    }

}
