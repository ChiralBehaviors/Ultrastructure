package com.hellblazer.CoRE.meta.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.meta.graph.impl.GraphQuery;
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
	
	public SerializableGraph(GraphQuery ng) {
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
