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
import java.util.LinkedList;
import java.util.List;

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
	private List<NetworkRuleform<?>> edges;
	private List<Ruleform> nodes;
	private EntityManager em;
	private Class<?> nodeclz;
	private Class<?> edgeclz;

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
		for (Class<? extends NetworkRuleform> form : reflections.getSubTypesOf(NetworkRuleform.class)) {
			if (!Modifier.isAbstract(form.getModifiers()) && form.getName().equalsIgnoreCase(node.getClass().getName() + "network")) {
				edgeclz = form;
			}
		}
		assert nodeclz != null;
		assert edgeclz != null;

		findNeighbors();
	}

	@SuppressWarnings("unchecked")
	private void findNeighbors() {
		//TODO if relationship array is null, get the whole graph
		Query q = em.createNamedQuery(nodeclz.getSimpleName().toLowerCase() + Networked.GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX);
		q.setParameter(nodeclz.getSimpleName().toLowerCase(), origin);
		q.setParameter("relationships", relationships);
		edges = (List<NetworkRuleform<?>>) q.getResultList();

		nodes = new LinkedList<Ruleform>();
		nodes.add(origin);
		

	}
	
	public Ruleform getOrigin() {
		return this.origin;
	}

	/**
	 * Gets the "edges" of the graph. The source and target properties of the
	 * edge object are indexes that refer to values in the node array. They are
	 * NOT ids.
	 * 
	 * @return the compound network ruleforms that represent graph edges
	 */
	public List<NetworkRuleform<?>> getEdges() {
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
	
	/**
	 * returns the union of nodes and edges between this graph and g2. 
	 * IMPORTANT: this is a destructive function
	 * @param g2
	 * @return
	 */
	public NetworkGraph union(NetworkGraph g2) {
		return this;
	}
	
	/**
	 * returns the intersection of nodes and edges between this graph and g2. 
	 * IMPORTANT: this is a destructive function
	 * @param g2
	 * @return
	 */
	public NetworkGraph intersection(NetworkGraph g2) {
		return this;
	}
	
	/**
	 * returns the graph created by using the nodes from g2 to seed this graph 
	 * IMPORTANT: this is a destructive function
	 * @param g2
	 * @return
	 */
	public NetworkGraph graphOf(NetworkGraph g2){
		return this;
	}
	


	

}
