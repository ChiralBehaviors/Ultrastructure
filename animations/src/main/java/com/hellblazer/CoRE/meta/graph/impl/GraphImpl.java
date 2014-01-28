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

package com.hellblazer.CoRE.meta.graph.impl;

import java.util.LinkedList;
import java.util.List;

import com.hellblazer.CoRE.meta.graph.Edge;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.meta.graph.Node;

/**
 * A graph of nodes and edges
 * 
 * @author hparry
 * 
 */
public class GraphImpl implements Graph {

    private List<Node<?>> nodes;
    private List<Edge<?>> edges;

    public GraphImpl(List<Node<?>> nodes, List<Edge<?>> edges) {
        this.nodes = nodes;
        this.edges = edges;
        if (this.nodes == null) {
            this.nodes = new LinkedList<Node<?>>();
        }
        if (this.edges == null) {
            this.edges = new LinkedList<Edge<?>>();
        }
    }

    /**
     * @param erisNodes
     * @param edges2
     */
    public GraphImpl(Node<?>[] nodes, List<Edge<?>> edges) {
        this.nodes = new LinkedList<Node<?>>();
        for (Node<?> node : nodes) {
            this.nodes.add(node);
        }
        this.edges = edges;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.graph.IGraph#addEdge(com.hellblazer.CoRE.meta
     * .graph.IEdge)
     */
    @Override
    public Graph addEdge(Edge<?> e) {
        edges.add(e);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.graph.IGraph#addNode(com.hellblazer.CoRE.meta
     * .graph.INode)
     */
    @Override
    public Graph addNode(Node<?> n) {
        nodes.add(n);
        return this;
    }

    @Override
    public List<Edge<?>> getEdges() {
        return edges;
    }

    @Override
    public List<Node<?>> getNodes() {
        return nodes;
    }

    @Override
    public GraphImpl intersection(Graph g) {
        List<Node<?>> nodesToRemove = new LinkedList<Node<?>>();
        for (Node<?> n : nodes) {
            if (!g.getNodes().contains(n)) {
                nodesToRemove.add(n);
            }
        }
        for (Node<?> n : nodesToRemove) {
            nodes.remove(n);
        }

        List<Edge<?>> edgesToRemove = new LinkedList<Edge<?>>();
        for (Edge<?> e : edges) {
            if (!g.getEdges().contains(e)) {
                edgesToRemove.add(e);
            }
        }

        for (Edge<?> e : edgesToRemove) {
            edges.remove(e);
        }
        return this;
    }

    @Override
    public GraphImpl union(Graph g) {
        nodes.addAll(g.getNodes());
        edges.addAll(g.getEdges());
        return this;
    }

}
