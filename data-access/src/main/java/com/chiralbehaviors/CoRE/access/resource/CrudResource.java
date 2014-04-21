/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.access.resource;

import static com.chiralbehaviors.CoRE.access.resource.Constants.QUALIFIER_PLAN;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.apache.openjpa.kernel.BrokerImpl;
import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.persistence.ArgumentException;
import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.util.ApplicationIds;
import org.w3c.dom.Document;

import com.chiralbehaviors.CoRE.access.formatting.ExceptionFormatter;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author hhildebrand
 * 
 */
@Path("/v{version : \\d+}/services/data/")
public class CrudResource {
	public static final String CONTEXT_ROOT = "/";
	public static final String QUALIFIER_FIRSTRESULT = "first";
	public static final String QUALIFIER_MAXRESULT = "max";
	public static final String QUALIFIER_NAMED = "named";
	public static final String QUALIFIER_SINGLE = "single";
	private static final String ARG_QUERY = "q";
	private static final String ARG_TYPE = "type";
	private static Localizer loc = Localizer.forPackage(CrudResource.class);
	private static final List<String> mandatoryFindArgs = Arrays
			.asList(ARG_TYPE);
	private static final List<String> mandatoryQueryArgs = Arrays
			.asList(ARG_QUERY);
	private static final List<String> validFindQualifiers = Arrays
			.asList(QUALIFIER_PLAN);

	private static final List<String> validQueryQualifiers = Arrays.asList(
			QUALIFIER_PLAN, QUALIFIER_NAMED, QUALIFIER_SINGLE,
			QUALIFIER_FIRSTRESULT, QUALIFIER_MAXRESULT);
	private final OpenJPAEntityManagerFactory emf;

	public CrudResource(EntityManagerFactory emf) {
		this.emf = (OpenJPAEntityManagerFactory) emf;
	}

	@DELETE
	@Path("/delete/{qualifiers : .+}")
	@Timed
	public Response delete(@PathParam("version") int version,
			@PathParam("qualifiers") String qualifiers,
			@Context UriInfo uriInfo, @Context HttpHeaders headers)
			throws JsonGenerationException, JsonMappingException, IOException,
			JAXBException {
		OpenJPAEntityManager em = getPersistenceContext();
		@SuppressWarnings("unchecked")
		Parse parse = new Parse(qualifiers, uriInfo, mandatoryFindArgs,
				Collections.EMPTY_LIST, 1, Integer.MAX_VALUE);
		String type = parse.getMandatoryArgument(ARG_TYPE);
		ClassMetaData meta = resolve(type);
		Map<String, String> parameters = parse.getArguments();
		Object[] pks = new Object[parameters.size()];
		int i = 0;
		for (String key : parameters.keySet()) {
			pks[i++] = key;
		}
		Object oid = ApplicationIds.fromPKValues(pks, meta);
		pushFetchPlan(em, parse);
		try {
			Object pc = em.find(meta.getDescribedType(), oid);
			if (pc != null) {
				em.remove(pc);
				return Response.status(Status.NO_CONTENT).build();
			} else {
				return Response
						.status(Status.NOT_FOUND)
						.entity(loc.get("product-not-found", type,
								Arrays.toString(pks)).getMessage()).build();
			}
		} finally {
			popFetchPlan(true, parse);
		}
	}

	@GET
	@Path("/find/{qualifiers : .+}")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	@Timed
	public Object find(@PathParam("version") int version,
			@PathParam("qualifiers") String qualifiers,
			@Context UriInfo uriInfo, @Context HttpHeaders headers)
			throws JsonGenerationException, JsonMappingException, IOException,
			JAXBException {
		OpenJPAEntityManager em = getPersistenceContext();
		Parse parse = new Parse(qualifiers, uriInfo, mandatoryFindArgs,
				validFindQualifiers, 1, Integer.MAX_VALUE);
		String type = parse.getMandatoryArgument(ARG_TYPE);
		ClassMetaData meta = resolve(type);
		Map<String, String> parameters = parse.getArguments();
		Object[] pks = new Object[parameters.size()];
		int i = 0;
		for (String key : parameters.keySet()) {
			pks[i++] = key;
		}
		Object oid = ApplicationIds.fromPKValues(pks, meta);
		pushFetchPlan(em, parse);
		try {
			Object pc = em.find(meta.getDescribedType(), oid);
			if (pc != null) {
				return pc;
			} else {
				return Response
						.status(Status.NOT_FOUND)
						.entity(loc.get("product-not-found", type,
								Arrays.toString(pks)).getMessage()).build();
			}
		} finally {
			popFetchPlan(true, parse);
		}
	}

	@GET
	@Path("/query/{qualifiers : .+}")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	@Timed
	public Object query(@PathParam("version") int version,
			@PathParam("qualifiers") String qualifiers,
			@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		OpenJPAEntityManager em = getPersistenceContext();
		Parse parse = new Parse(qualifiers, uriInfo, mandatoryQueryArgs,
				validQueryQualifiers, 1, Integer.MAX_VALUE);
		String spec = parse.getMandatoryArgument(ARG_QUERY);
		try {
			Query query = parse.isBooleanQualifier(QUALIFIER_NAMED) ? em
					.createNamedQuery(spec) : em.createQuery(spec);
			if (parse.hasQualifier(QUALIFIER_FIRSTRESULT)) {
				query.setFirstResult(Integer.parseInt(parse
						.getQualifier(QUALIFIER_FIRSTRESULT)));
			}
			if (parse.hasQualifier(QUALIFIER_MAXRESULT)) {
				query.setMaxResults(Integer.parseInt(parse
						.getQualifier(QUALIFIER_MAXRESULT)));
			}

			pushFetchPlan(query, parse);

			Map<String, String> args = parse.getArguments();
			for (Map.Entry<String, String> entry : args.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
			if (parse.isBooleanQualifier(QUALIFIER_SINGLE)) {
				return query.getSingleResult();
			} else {
				return query.getResultList();
			}

		} catch (ArgumentException e) {
			ExceptionFormatter formatter = new ExceptionFormatter();
			Document xml = formatter.createXML(
					"Request URI: " + uriInfo.getRequestUri(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.type(MediaType.APPLICATION_XML).entity(xml).build();
		} catch (Exception e) {
			ExceptionFormatter formatter = new ExceptionFormatter();
			Document xml = formatter.createXML(
					"Request URI: " + uriInfo.getRequestUri(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.type(MediaType.APPLICATION_XML).entity(xml).build();
		} finally {
			popFetchPlan(false, parse);
		}
	}

	protected OpenJPAEntityManager getPersistenceContext() {
		return emf.createEntityManager();
	}

	protected void popFetchPlan(boolean finder, Parse parse) {
		if (!parse.hasQualifier(QUALIFIER_PLAN)) {
			return;
		}
		OpenJPAEntityManager em = getPersistenceContext();
		BrokerImpl broker = (BrokerImpl) JPAFacadeHelper.toBroker(em);
		if (finder) {
			broker.setCacheFinderQuery(false);
		} else {
			broker.setCachePreparedQuery(false);
		}
	}

	protected void pushFetchPlan(Object target, Parse parse) {
		if (!parse.hasQualifier(QUALIFIER_PLAN)) {
			return;
		}
		OpenJPAEntityManager em = getPersistenceContext();
		FetchPlan plan = em.pushFetchPlan();
		BrokerImpl broker = (BrokerImpl) JPAFacadeHelper.toBroker(em);
		if (target instanceof OpenJPAEntityManager) {
			broker.setCacheFinderQuery(false);
		} else if (target instanceof OpenJPAQuery) {
			broker.setCachePreparedQuery(false);
		}

		String[] plans = parse.getQualifier(QUALIFIER_PLAN).split(",");
		for (String p : plans) {
			p = p.trim();
			if (p.charAt(0) == '-') {
				plan.removeFetchGroup(p.substring(1));
			} else {
				plan.addFetchGroup(p);
			}
		}
	}

	protected ClassMetaData resolve(String alias) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration()
				.getMetaDataRepositoryInstance()
				.getMetaData(alias, loader, true);
	}

}
