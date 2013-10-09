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
	private List<IEdge> edges;
	
	public Graph(List<INode<?>> nodes, List<IEdge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	
	public List<INode<?>> getNodes() {
		return nodes;
	}
	
	public List<IEdge> getEdges() {
		return edges;
	}
	
	@Override
	public Graph union(IGraph g) {
		nodes.addAll(g.getNodes());
		edges.addAll(g.getEdges());
		return this;
	}
	
	@Override
	public Graph intersection(IGraph g) {
		for (INode<?> n : nodes) {
			if (!g.getNodes().contains(n)) {
				nodes.remove(n);
			}
		}
		
		for (IEdge e : edges) {
			if (!g.getEdges().contains(e)) {
				edges.remove(e);
			}
		}
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
	public IGraph addEdge(IEdge e) {
		this.edges.add(e);
		return this;
	}

}
