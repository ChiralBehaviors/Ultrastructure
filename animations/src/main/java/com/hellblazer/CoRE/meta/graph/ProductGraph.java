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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hparry
 *
 */
public class ProductGraph extends AbstractNetworkGraph<Product> {

	private Product origin;
	private Relationship[] relationships;
	private List<GraphEdge> edges;
	private Product[] nodes;
	private EntityManager em;
	
	
	public ProductGraph(Product node, Relationship[] relationships, EntityManager em) {
		this.origin = node;
		this.relationships = relationships;
		this.em = em;
		findNeighbors();
	}
	
	/**
	 * @return
	 */
	private void findNeighbors() {
		//TODO HPARRY create a query that just gets the child nodes, not the ProductNetworks
		Query q = em.createNamedQuery(Product.ALL_CHILDREN_NETWORK_RULES);
		q.setParameter("product", origin);
		q.setParameter("relationship", relationships[0]);
		@SuppressWarnings("unchecked")
		List<ProductNetwork> results = (List<ProductNetwork>)q.getResultList();
		List<Product> nodes = new LinkedList<Product>();
		//origin node must ALWAYS be first in the list
		nodes.add(origin);
		List<GraphEdge> edges = new LinkedList<GraphEdge>();
		int i = 0;
		for (ProductNetwork pn : results) {
			if (pn.getChild().getId() != origin.getId()) {
				nodes.add(pn.getChild());
			}
			//TODO HPARRY this shouldn't always start at 0
			edges.add(new GraphEdge(0, pn.getRelationship().getId(), i));
			i++;
		}
		this.nodes = nodes.toArray(new Product[0]);
		this.edges = edges;
		
	}


	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.AbstractNetworkGraph#getEdges()
	 */
	@Override
	public List<GraphEdge> getEdges() {
		return edges;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.AbstractNetworkGraph#getNodes()
	 */
	@Override
	public ExistentialRuleform[] getNodes() {
		return nodes;
	}
	
	@Override
	public Relationship[] getRelationships() {
		return relationships;
	}
	
}
