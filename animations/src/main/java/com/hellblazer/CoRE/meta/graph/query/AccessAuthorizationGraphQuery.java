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

import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.location.LocationAccessAuthorization;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.ProductAccessAuthorization;
import com.hellblazer.CoRE.resource.ResourceAccessAuthorization;

/**
 * @author hparry
 * 
 */
public class AccessAuthorizationGraphQuery {

	private EntityManager em;
	private AccessAuthorization authorization;
	private List<AccessAuthorization> auths;
	private Networked<?, ?> parent;
	private Relationship relationship;

	public AccessAuthorizationGraphQuery(Networked<?, ?> parent,
			Relationship rel, EntityManager em) {
		this.parent = parent;
		this.relationship = rel;
		this.em = em;
		this.auths = new LinkedList<AccessAuthorization>();
		findAuthorizations();
	}

	/**
	 * @return the authorization
	 */
	public AccessAuthorization getAuthorization() {
		return authorization;
	}

	/**
	 * @param authorization
	 *            the authorization to set
	 */
	public void setAuthorization(AccessAuthorization authorization) {
		this.authorization = authorization;
	}

	private void findAuthorizations() {
		findProductAuthorizations();
	}


	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void findProductAuthorizations() {
		Query q = em
				.createNamedQuery(
						ProductAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP,
						ProductAccessAuthorization.class);
		q.setParameter("rf", parent);
		q.setParameter("r", relationship);
		auths.addAll(q.getResultList());

	}

	public List<AccessAuthorization> getResults() {
		return auths;
	}

}
