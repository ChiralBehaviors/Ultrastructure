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

import com.hellblazer.CoRE.meta.graph.IEdge;
import com.hellblazer.CoRE.meta.graph.IGraph;
import com.hellblazer.CoRE.meta.graph.INode;

/**
 * A graph of nodes and edges
 * @author hparry
 *
 */
public class Graph implements IGraph {
	

	private List<INode<?>> nodes;
	private List<IEdge<?>> edges;
	
	public Graph(List<INode<?>> nodes, List<IEdge<?>> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	
	
	/**
	 * @param erisNodes
	 * @param edges2
	 */
	public Graph(INode<?>[] nodes, List<IEdge<?>> edges) {
		this.nodes = new LinkedList<INode<?>>();
		for (INode<?> node : nodes) {
			this.nodes.add(node);
		}
		this.edges = edges;
	}

	public List<INode<?>> getNodes() {
		return nodes;
	}
	
	public List<IEdge<?>> getEdges() {
		return edges;
	}
	
	@Override
	public Graph union(IGraph g) {
		List<INode<?>> newNodes = new LinkedList<INode<?>>();
		if (nodes != null) {
			newNodes.addAll(nodes);
		}
		if (g.getNodes() != null) {
			newNodes.addAll(g.getNodes());
		}
		this.nodes = newNodes;

		List<IEdge<?>> newEdges = new LinkedList<IEdge<?>>();
		if (edges != null) {
			newEdges.addAll(edges);
		}
		if (g.getEdges() != null) {
			newEdges.addAll(g.getEdges());
		}
		edges = newEdges;
		return this;
	}
	
	@Override
	public Graph intersection(IGraph g) {
		List<INode<?>> newNodes = new LinkedList<INode<?>>();
		for (INode<?> n : nodes) {
			if (g.getNodes().contains(n)) {
				newNodes.add(n);
			}
		}
		this.nodes = newNodes;
		
		List<IEdge<?>> newEdges = new LinkedList<IEdge<?>>();
		
		for (IEdge<?> e : edges) {
			if (g.getEdges().contains(e)) {
				newEdges.add(e);
			}
		}
		
		this.edges = newEdges;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.IGraph#addNode(com.hellblazer.CoRE.meta.graph.INode)
	 */
	@Override
	public IGraph addNode(INode<?> n) {
		this.nodes.add(n);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.IGraph#addEdge(com.hellblazer.CoRE.meta.graph.IEdge)
	 */
	@Override
	public IGraph addEdge(IEdge<?> e) {
		this.edges.add(e);
		return this;
	}

}
