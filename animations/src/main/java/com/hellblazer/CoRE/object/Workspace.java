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
import com.hellblazer.CoRE.authorization.WorkspaceAuthorization;
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
	private List<AccessAuthorization> accessAuths;
	private List<WorkspaceAuthorization> workspaceAuths;

	/**
	 * Use this to create a workspace object in memory and load relevant
	 * components from the db.
	 * @param workspace
	 * @param workspaceOf
	 * @param em
	 */
	public static Workspace loadWorkspace(Product workspace, Relationship workspaceOf,
			EntityManager em) {
		Workspace ws = new Workspace();
		
		ws.workspace = workspace;
		ws.workspaceOf = workspaceOf;
		ws.em = em;
		ws.products = ws.loadWorkspaceProducts();
		ws.accessAuths = ws.loadWorkspaceAccessAuthorizations();
		ws.loadWorkspaceAuthorizations();
		
		return ws;
	}
	
	/**
	 * 
	 */
	private void loadWorkspaceAuthorizations() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * An empty constructor for JSON serialization. 
	 */
	public Workspace() {
		//empty constructor for JSON
	}


	private List<Product> loadWorkspaceProducts() {
		NetworkGraphQuery<Product> queryAgency = new NetworkGraphQuery<Product>(
				workspace, workspaceOf, em);
		return queryAgency.getNodes();

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
		return accessAuths;
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
		this.accessAuths = auths;
	}
	
	

}
