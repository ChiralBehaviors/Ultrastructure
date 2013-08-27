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
package com.hellblazer.CoRE.meta.graph;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.reflections.Reflections;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;

/**
 * Class for representing the graphs created by networked ruleforms. The nodes
 * are existential ruleforms and the edges are created by relationships. This
 * means that the edges are a) directed and b) typed.
 * 
 * @author hparry
 * 
 */
public final class NetworkGraph {

	private Ruleform origin;
	private List<Relationship> relationships;
	private List<GraphEdge> edges;
	private List<Ruleform> nodes;
	private EntityManager em;
	private Class<?> nodeclz;

	public NetworkGraph(Ruleform node, List<Relationship> relationships,
			EntityManager em) {
		this.origin = node;
		this.relationships = relationships;
		this.em = em;
		
		Reflections reflections = new Reflections(Ruleform.class.getPackage()
				.getName());
		for (Class<? extends Ruleform> form : reflections
				.getSubTypesOf(Ruleform.class)) {
			if (!Modifier.isAbstract(form.getModifiers()) && form.getName().equalsIgnoreCase(node.getClass().getName())) {
				nodeclz = form;
			}
		}
		assert nodeclz != null;
		findNeighbors();
	}

	private void findNeighbors() {
		//TODO if relationship array is null, get the whole graph
		Query q = em.createNamedQuery(nodeclz.getSimpleName().toLowerCase() + Networked.GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX);
		q.setParameter(nodeclz.getSimpleName().toLowerCase(), origin);
		q.setParameter("relationships", relationships);
		@SuppressWarnings("unchecked")
		List<NetworkRuleform<?>> results = (List<NetworkRuleform<?>>) q.getResultList();

		Map<Ruleform, Integer> indices = new HashMap<Ruleform, Integer>();
		nodes = new LinkedList<Ruleform>();
		edges = new LinkedList<GraphEdge>();
		nodes.add(origin);
		indices.put(origin, 0);
		for (NetworkRuleform<?> n : results) {
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

	/**
	 * Gets the "edges" of the graph. The source and target properties of the
	 * edge object are indexes that refer to values in the node array. They are
	 * NOT ids.
	 * 
	 * @return the compound network ruleforms that represent graph edges
	 */
	public List<GraphEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the set of nodes in the graph, starting with the origin. These
	 * are existential ruleforms.
	 * 
	 * @return
	 */
	public List<Ruleform> getNodes() {
		return nodes;
	}

	/**
	 * @return the list of relationships that appear in the graph. This
	 *         information is used for typifying edges.
	 */
	public List<Relationship> getRelationships() {
		return relationships;
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
