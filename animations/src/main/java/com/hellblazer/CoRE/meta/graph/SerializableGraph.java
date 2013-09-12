/**
 * Copyright (c) 2012, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.hellblazer.CoRE.meta.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * Representation of a NetworkGraph intended to be serialized by Jackson. Jackson will
 * only fully serialize the first appearance of an object in a response, and thereafter
 * it will confine itself to the object type and ID. This makes the generated JSON structures
 * inconsistent and very hard for JS clients to consume.
 * 
 * To make life easier, this class contains no duplicate instances. It's a unique list of
 * nodes in the graph, a unique list of Relationship types (a.k.a. edge types) and a
 * GraphEdge data structure that is nothing more than array index references for the parent
 * and child nodes as well as the Relationship the edge represents.
 * 
 * @author hparry
 *
 */
public class SerializableGraph {
	
	private Ruleform origin;
	private List<Relationship> relationships;
	private List<GraphEdge> edges;
	private List<Ruleform> nodes;
	
	public SerializableGraph(NetworkGraph ng) {
		this.relationships = ng.getRelationships();
		this.nodes = ng.getNodes();
		this.origin = ng.getOrigin();
		Map<Ruleform, Integer> indices = new HashMap<Ruleform, Integer>();
		for (int i = 0; i < nodes.size(); i++ ) {
			indices.put(nodes.get(i), i);
		}
		
		for (NetworkRuleform<?> n : ng.getEdges()) {
			int source, target;
			long relationship;
			if (indices.get(n.getParent()) == null) {
				source = nodes.size();
				nodes.add((Ruleform) n.getParent());
				indices.put((Ruleform) n.getParent(), source);
			} else {
				source = indices.get(n.getParent());
			}

			if (indices.get(n.getChild()) == null) {
				target = nodes.size();
				nodes.add((Ruleform) n.getChild());
				indices.put((Ruleform) n.getChild(), target);
			} else {
				target = indices.get(n.getChild());
			}

			relationship = n.getRelationship().getId();

			edges.add(new GraphEdge(source, relationship, target));
		}
		
	}
	
	public Ruleform getOrigin() {
		return this.origin;
	}
	
	public List<Ruleform> getNodes() {
		return this.nodes;
	}
	
	public List<Relationship> getRelationships() {
		return this.relationships;
	}
	
	public List<GraphEdge> getEdges() {
		return this.edges;
	}
	
	public class GraphEdge {
		private long source;
		private long target;
		private long relationship;

		GraphEdge(long source, long relationship, long target) {
			this.source = source;
			this.target = target;
			this.relationship = relationship;
		}

		/**
		 * @return the source id
		 */
		public long getSource() {
			return source;
		}

		/**
		 * @return the target id
		 */
		public long getTarget() {
			return target;
		}

		/**
		 * @return the relationship id
		 */
		public long getRelationship() {
			return relationship;
		}
	}

}
