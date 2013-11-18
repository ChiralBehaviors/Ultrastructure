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
package com.hellblazer.CoRE.access.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.object.Workspace;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hparry
 *
 */
@Path("/v{version : \\d+}/services/data/workspace")
public class WorkspaceResource {
	
	EntityManager em;
	
	/**
	 * @param emf
	 */
	public WorkspaceResource(EntityManagerFactory emf) {
		this.em = emf.createEntityManager();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Workspace get(@PathParam("id") long productId, @QueryParam("relId") long relId) {
		Product p = em.find(Product.class, productId);
		Relationship r = em.find(Relationship.class, relId);
		Workspace w = Workspace.loadWorkspace(p, r, em);
		
		return w;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Product insertWorkspace(Workspace w) {
		em.getTransaction().begin();
		try {
			Product origin = w.getProducts().get(0);
			List<Product> nets = w.getProducts();
			Map<Ruleform, Ruleform> knownObjects = new HashMap<Ruleform, Ruleform>();
			for (Product p : nets) {
				p.manageEntity(em, knownObjects);
			}
			List<AccessAuthorization> auths = w.getAuths();
			for (AccessAuthorization auth : auths) {
				auth.manageEntity(em, knownObjects);
			}

			em.getTransaction().commit();
			em.refresh(origin);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enableDefaultTyping();
			return origin;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		}

	}

}
