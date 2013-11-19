/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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

	public List<Node<?>> getNodes() {
		return nodes;
	}

	public List<Edge<?>> getEdges() {
		return edges;
	}

	@Override
	public GraphImpl union(Graph g) {
		this.nodes.addAll(g.getNodes());
		this.edges.addAll(g.getEdges());
		return this;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hellblazer.CoRE.meta.graph.IGraph#addNode(com.hellblazer.CoRE.meta
	 * .graph.INode)
	 */
	@Override
	public Graph addNode(Node<?> n) {
		this.nodes.add(n);
		return this;
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
		this.edges.add(e);
		return this;
	}

}
