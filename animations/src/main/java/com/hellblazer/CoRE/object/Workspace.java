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
package com.hellblazer.CoRE.object;

import java.util.List;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.meta.graph.query.AccessAuthorizationGraphQuery;
import com.hellblazer.CoRE.meta.graph.query.NetworkGraphQuery;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * The object that gets de/serialized as a workspace in JSON
 * 
 * @author hparry
 * 
 */
public class Workspace {

	private Product workspace;
	private Relationship workspaceOf;
	private EntityManager em;
	private List<Product> products;
	private List<AccessAuthorization> auths;

	public Workspace(Product workspace, Relationship workspaceOf,
			EntityManager em) {
		this.workspace = workspace;
		this.workspaceOf = workspaceOf;
		this.em = em;
		products = loadWorkspaceProducts();
		auths = loadWorkspaceAccessAuthorizations();
	}
	
	public Workspace() {
		//empty constructor for JSON
	}


	private List<Product> loadWorkspaceProducts() {
		NetworkGraphQuery<Product> queryResource = new NetworkGraphQuery<Product>(
				workspace, workspaceOf, em);
		return queryResource.getNodes();

	}

	private List<AccessAuthorization> loadWorkspaceAccessAuthorizations() {
		AccessAuthorizationGraphQuery query = 
				new AccessAuthorizationGraphQuery(workspace, workspaceOf, em);
		return query.getResults();
	}


	/**
	 * @return the products
	 */
	public List<Product> getProducts() {
		return products;
	}


	/**
	 * @return the auths
	 */
	public List<AccessAuthorization> getAuths() {
		return auths;
	}


	/**
	 * @return
	 */
	public Product getParentProduct() {
		return workspace;
	}

	/**
	 * @return the workspace
	 */
	public Product getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(Product workspace) {
		this.workspace = workspace;
	}

	/**
	 * @return the workspaceOf
	 */
	public Relationship getWorkspaceOf() {
		return workspaceOf;
	}

	/**
	 * @param workspaceOf the workspaceOf to set
	 */
	public void setWorkspaceOf(Relationship workspaceOf) {
		this.workspaceOf = workspaceOf;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * @param auths the auths to set
	 */
	public void setAuths(List<AccessAuthorization> auths) {
		this.auths = auths;
	}
	
	

}
