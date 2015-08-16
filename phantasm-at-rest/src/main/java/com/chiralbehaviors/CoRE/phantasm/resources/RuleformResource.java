/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.codahale.metrics.annotation.Timed;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/ruleform")
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
public class RuleformResource extends TransactionalResource {

    public final static Map<String, Class<? extends Ruleform>> entityMap = new TreeMap<>();

    private final static ArrayList<String> sortedRuleforms;

    static {
        Reflections reflections = new Reflections(Ruleform.class.getPackage().getName());
        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                entityMap.put(form.getSimpleName(), form);
            }
        }
        sortedRuleforms = new ArrayList<>(entityMap.keySet());
        Collections.sort(sortedRuleforms);
    }

    public static String getRuleformIri(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(RuleformResource.class).build().toASCIIString()
               + "/";
    }

    @Context
    private UriInfo uriInfo;

    public RuleformResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Timed
    @Path("{ruleform}")
    @POST
    public Map<String, Object> create(@PathParam("ruleform") String ruleform,
                                      Map<String, Object> instance) {
        return null;
    }

    @Timed
    @Path("context")
    @GET
    public Map<String, Object> getContext() {
        UriBuilder ub = UriBuilder.fromResource(getClass());
        ub.path("context");
        ub.path("context.jsonld");
        Map<String, Object> context = new TreeMap<>();
        context.put(Constants.ID, ub.build().toASCIIString());
        return context;
    }

    @Timed
    @Path("{ruleform}/context")
    @GET
    public Map<String, Object> getContext(@PathParam("ruleform") String ruleform) {
        return new RuleformContext(entityMap.get(ruleform),
                                   uriInfo).toContext(uriInfo);
    }

    @Timed
    @Path("{ruleform}/@facet:{instance}")
    @GET
    public Map<String, Object> getFacetQualifiedInstance(@PathParam("ruleform") String ruleform,
                                                         @PathParam("instance") UUID instance) {
        return getInstance(ruleform, instance);
    }

    @Timed
    @Path("{ruleform}/{instance}")
    @GET
    public Map<String, Object> getInstance(@PathParam("ruleform") String ruleform,
                                           @PathParam("instance") UUID instance) {
        Ruleform ruleformInstance = (Ruleform) readOnlyModel.getEntityManager().find(entityMap.get(ruleform),
                                                                                     instance);
        if (ruleformInstance == null) {
            throw new WebApplicationException(String.format("%s:%s does not exist",
                                                            ruleform,
                                                            instance));
        }
        return new RuleformContext(ruleformInstance.getClass(),
                                   uriInfo).toNode(ruleformInstance, uriInfo);
    }

    @Timed
    @Path("{ruleform}/instances")
    @GET
    public List<Map<String, String>> getInstances(@PathParam("ruleform") String ruleform) {
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleform);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleform));
        }
        List<Map<String, String>> instances = new ArrayList<>();
        readOnlyModel.findAll(ruleformClass).forEach(rf -> {
            Map<String, String> map = new TreeMap<>();
            map.put(Constants.CONTEXT,
                    RuleformContext.getContextIri(rf.getClass(),
                                                  uriInfo).toASCIIString());
            map.put(Constants.ID, RuleformContext.getIri(rf));
            instances.add(map);
        });
        return instances;
    }

    @Timed
    @Path("{ruleform}")
    @GET
    public List<Map<String, String>> getRuleform(@PathParam("ruleform") String ruleform) {
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleform);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleform));
        }
        List<Map<String, String>> instances = new ArrayList<>();
        readOnlyModel.findAll(ruleformClass).forEach(rf -> {
            Map<String, String> map = new TreeMap<>();
            map.put(Constants.CONTEXT,
                    RuleformContext.getContextIri(rf.getClass(),
                                                  uriInfo).toASCIIString());
            map.put(Constants.ID, RuleformContext.getIri(rf));
            instances.add(map);
        });
        return instances;
    }

    @Timed
    @Path("{ruleform}/@ruleform:{instance}")
    @GET
    public Map<String, Object> getRuleformQualifiedInstance(@PathParam("ruleform") String ruleform,
                                                            @PathParam("instance") UUID instance) {
        return getInstance(ruleform, instance);
    }

    @Timed
    @GET
    public List<String> getRuleforms() {
        return sortedRuleforms;
    }

    @Timed
    @Path("{ruleform}/term/{term}")
    @GET
    public Map<String, Object> getTerm(@PathParam("ruleform") String ruleform,
                                       @PathParam("term") String term) {
        Map<String, Object> definition = new TreeMap<>();
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleform);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleform),
                                              Status.NOT_FOUND);
        }
        definition.put(Constants.ID,
                       RuleformContext.getTermIri(ruleformClass, term));
        definition.put(Constants.TYPE, "http://ultrastructure.me#term");
        return definition;
    }
}
