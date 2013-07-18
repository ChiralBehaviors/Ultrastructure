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

import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hparry
 *
 */
public class ProductGraph extends AbstractNetworkGraph<Product> {

	private Product origin;
	private Relationship relationship;
	private Product[] neighbors;
	private EntityManager em;
	
	
	public ProductGraph(Product origin, Relationship relationship, EntityManager em) {
		this.origin = origin;
		this.relationship = relationship;
		this.em = em;
		neighbors = findNeighbors();	
	}
	
	/**
	 * @return
	 */
	private Product[] findNeighbors() {
		//TODO HPARRY create a query that just gets the child nodes, not the ProductNetworks
		Query q = em.createNamedQuery(Product.ALL_CHILDREN_NETWORK_RULES);
		q.setParameter("product", origin);
		q.setParameter("relationship", relationship);
		@SuppressWarnings("unchecked")
		List<ProductNetwork> results = (List<ProductNetwork>)q.getResultList();
		List<Product> neighbors = new LinkedList<Product>();
		
		for (ProductNetwork pn : results) {
			neighbors.add(pn.getChild());
		}
		return neighbors.toArray(new Product[0]);
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.AbstractNetworkGraph#getOrigin()
	 */
	@Override
	public Product getOrigin() {
		return origin;
	}
	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.AbstractNetworkGraph#getRelationship()
	 */
	@Override
	public Relationship getRelationship() {
		return relationship;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.AbstractNetworkGraph#getNeighborNodes()
	 */
	@Override
	public Product[] getNeighborNodes() {
		return neighbors;
	}
	
	
}
