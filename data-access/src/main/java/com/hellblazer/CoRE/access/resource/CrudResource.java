/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

import static com.hellblazer.CoRE.access.resource.Constants.QUALIFIER_PLAN;

import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.util.ApplicationIds;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.CoRE.access.formatting.ExceptionFormatter;
import com.hellblazer.CoRE.json.AttributeValueSerializer;
import com.hellblazer.CoRE.resource.ResourceAttribute;
import com.yammer.metrics.annotation.Timed;

/**
 * @author hhildebrand
 * 
 */
@Path("/v{version : \\d+}/services/data/")
public class CrudResource {
    public static final String                CONTEXT_ROOT          = "/";
    public static final String                QUALIFIER_FIRSTRESULT = "first";
    public static final String                QUALIFIER_MAXRESULT   = "max";
    public static final String                QUALIFIER_NAMED       = "named";
    public static final String                QUALIFIER_SINGLE      = "single";
    private static final String               ARG_QUERY             = "q";
    private static final String               ARG_TYPE              = "type";
    private static Localizer                  loc                   = Localizer.forPackage(CrudResource.class);
    private static final List<String>         mandatoryFindArgs     = Arrays.asList(ARG_TYPE);
    private static final List<String>         mandatoryQueryArgs    = Arrays.asList(ARG_QUERY);
    private static final List<String>         validFindQualifiers   = Arrays.asList(QUALIFIER_PLAN);

    private static final List<String>         validQueryQualifiers  = Arrays.asList(QUALIFIER_PLAN,
                                                                                    QUALIFIER_NAMED,
                                                                                    QUALIFIER_SINGLE,
                                                                                    QUALIFIER_FIRSTRESULT,
                                                                                    QUALIFIER_MAXRESULT);
    private final OpenJPAEntityManagerFactory emf;

    public CrudResource(EntityManagerFactory emf) {
        this.emf = (OpenJPAEntityManagerFactory) emf;
    }

    @DELETE
    @Path("/delete/{qualifiers : .+}")
    @Timed
    public Response delete(@PathParam("version") int version,
                           @PathParam("qualifiers") String qualifiers,
                           @Context UriInfo uriInfo,
                           @Context HttpHeaders headers)
                                                        throws JsonGenerationException,
                                                        JsonMappingException,
                                                        IOException,
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
                return Response.status(Status.NOT_FOUND).entity(loc.get("product-not-found",
                                                                        type,
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
    public Response find(@PathParam("version") int version,
                         @PathParam("qualifiers") String qualifiers,
                         @Context UriInfo uriInfo, @Context HttpHeaders headers)
                                                                                throws JsonGenerationException,
                                                                                JsonMappingException,
                                                                                IOException,
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
                ObjectMapper mapper;
                if (meta.getDescribedType().equals(ResourceAttribute.class)) {
                    mapper = new ObjectMapper();
                    SimpleModule testModule = new SimpleModule(
                                                               "MyModule",
                                                               new Version(
                                                                           1,
                                                                           0,
                                                                           0,
                                                                           null,
                                                                           null,
                                                                           null));
                    testModule.addSerializer(new AttributeValueSerializer<ResourceAttribute>(
                                                                                             ResourceAttribute.class,
                                                                                             true)); // assuming serializer declares correct class to bind to
                    mapper.registerModule(testModule);
                } else {
                    mapper = new ObjectMapper();
                    mapper.enableDefaultTyping();
                }
                return Response.ok(mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(pc),
                                   "text/json").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity(loc.get("product-not-found",
                                                                        type,
                                                                        Arrays.toString(pks)).getMessage()).build();
            }
        } finally {
            popFetchPlan(true, parse);
        }
    }

    public OpenJPAEntityManager getPersistenceContext() {
        return emf.createEntityManager();
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("/query/{qualifiers : .+}")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    @Timed
    public Response query(@PathParam("version") int version,
                          @PathParam("qualifiers") String qualifiers,
                          @Context UriInfo uriInfo, @Context HttpHeaders headers) {
        OpenJPAEntityManager em = getPersistenceContext();
        Parse parse = new Parse(qualifiers, uriInfo, mandatoryQueryArgs,
                                validQueryQualifiers, 1, Integer.MAX_VALUE);
        String spec = parse.getMandatoryArgument(ARG_QUERY);
        try {
            Query query = parse.isBooleanQualifier(QUALIFIER_NAMED) ? em.createNamedQuery(spec)
                                                                   : em.createQuery(spec);
            if (parse.hasQualifier(QUALIFIER_FIRSTRESULT)) {
                query.setFirstResult(Integer.parseInt(parse.getQualifier(QUALIFIER_FIRSTRESULT)));
            }
            if (parse.hasQualifier(QUALIFIER_MAXRESULT)) {
                query.setMaxResults(Integer.parseInt(parse.getQualifier(QUALIFIER_MAXRESULT)));
            }

            pushFetchPlan(query, parse);

            Map<String, String> args = parse.getArguments();
            for (Map.Entry<String, String> entry : args.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            Object result;
            //TODO HPARRY can we build a typed object list out of the results here
            //and serialize to json?
            if (parse.isBooleanQualifier(QUALIFIER_SINGLE)) {
                result = query.getSingleResult();
            } else {
                result = new ArrayList<Object>(query.getResultList());
                ObjectMapper mapper = new ObjectMapper();
                return Response.ok(mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(result),
                                   "text/json").build();
            }

        } catch (ArgumentException e) {
            ExceptionFormatter formatter = new ExceptionFormatter();
            Document xml = formatter.createXML("Request URI: "
                                                       + uriInfo.getRequestUri(),
                                               e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(xml).build();
        } catch (Exception e) {
            ExceptionFormatter formatter = new ExceptionFormatter();
            Document xml = formatter.createXML("Request URI: "
                                                       + uriInfo.getRequestUri(),
                                               e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_XML).entity(xml).build();
        } finally {
            popFetchPlan(false, parse);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public ClassMetaData resolve(String alias) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return emf.getConfiguration().getMetaDataRepositoryInstance().getMetaData(alias,
                                                                                  loader,
                                                                                  true);
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

}
