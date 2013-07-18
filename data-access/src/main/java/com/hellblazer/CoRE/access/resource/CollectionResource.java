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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.meta.graph.ProductGraph;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * A REST resource for processing atomic transactions full of multiple objects
 * of many types.
 * 
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/collection")
public class CollectionResource {

	EntityManager em;

	/**
	 * @param emf
	 */
	public CollectionResource(EntityManagerFactory emf) {
		em = emf.createEntityManager();
	}

	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Ruleform post(Ruleform graph) throws JsonProcessingException {
		em.getTransaction().begin();
		try {

			Map<Ruleform, Ruleform> knownObjects = new HashMap<Ruleform, Ruleform>();
			graph.manageEntity(em, knownObjects);

			em.getTransaction().commit();
			em.refresh(graph);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enableDefaultTyping();
			return graph;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		}

	}

	@GET
	@Path("/product")
	public Response getProductNetwork(@QueryParam("ruleId") long ruleId,
			@QueryParam("relId") long relId) throws JsonProcessingException {

		Product p = em.find(Product.class, ruleId);
		Relationship r = em.find(Relationship.class, relId);

		ProductGraph pg = new ProductGraph(p, r, em);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping();
		return Response.ok(
				mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(
						pg.getNeighborNodes()), "text/json").build();

	}

}
