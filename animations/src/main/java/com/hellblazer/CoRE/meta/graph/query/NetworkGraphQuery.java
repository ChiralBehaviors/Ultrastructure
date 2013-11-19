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
package com.hellblazer.CoRE.meta.graph.query;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
public final class NetworkGraphQuery<RuleForm extends Networked<RuleForm, ?>> {

	private RuleForm origin;
	private List<Relationship> relationships;
	private List<NetworkRuleform<RuleForm>> edges;
	private List<RuleForm> nodes;
	private EntityManager em;

	
	public NetworkGraphQuery(RuleForm node, Relationship r, EntityManager em) {
		List<RuleForm> nodes = new LinkedList<RuleForm>();
		nodes.add(node);
		List<Relationship> relationships = new LinkedList<Relationship>();
		relationships.add(r);
		this.nodes = nodes;
		this.relationships = relationships;
		this.em = em;
		this.origin = node;
		findNeighbors();
	}

	public NetworkGraphQuery(List<RuleForm> nodes, List<Relationship> relationships,
			EntityManager em) {
		this.origin = nodes.get(0);
		this.relationships = relationships;
		this.nodes = nodes;
		this.em = em;
		findNeighbors();
		
	}

	@SuppressWarnings("unchecked")
	private void findNeighbors() {
		Query q = em.createNamedQuery(origin.getClass().getSimpleName().toLowerCase() + Networked.GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX);
		q.setParameter(origin.getClass().getSimpleName().toLowerCase(), origin);
		q.setParameter("relationships", relationships);
		edges = (List<NetworkRuleform<RuleForm>>) q.getResultList();

		nodes = new LinkedList<RuleForm>();
		nodes.add(origin);
		for (NetworkRuleform<?> n : edges) {
			if (!nodes.contains(n.getChild())) {
				nodes.add((RuleForm)n.getChild());
			}
		}
		

	}
	
	public RuleForm getOrigin() {
		return this.origin;
	}

	/**
	 * Gets the "edges" of the graph. The source and target properties of the
	 * edge object are indexes that refer to values in the node array. They are
	 * NOT ids.
	 * 
	 * @return the compound network ruleforms that represent graph edges
	 */
	public List<NetworkRuleform<RuleForm>> getEdges() {
		return edges;
	}

	/**
	 * Returns the set of nodes in the graph, starting with the origin. These
	 * are existential ruleforms.
	 * 
	 * @return
	 */
	public List<RuleForm> getNodes() {
		return nodes;
	}

	/**
	 * @return the list of relationships that appear in the graph. This
	 *         information is used for typifying edges.
	 */
	public List<Relationship> getRelationships() {
		return relationships;
	}
	

}
