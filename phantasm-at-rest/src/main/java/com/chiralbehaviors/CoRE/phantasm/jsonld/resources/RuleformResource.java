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

package com.chiralbehaviors.CoRE.phantasm.jsonld.resources;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/ruleform")
@Produces({ "application/json", "text/json" })
public class RuleformResource extends TransactionalResource {

    public final static Map<String, Class<? extends Ruleform>> entityMap = new TreeMap<>();

    private final static ArrayList<String> sortedRuleformTypes;

    static {
        Reflections reflections = new Reflections(Ruleform.class.getPackage().getName());
        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                entityMap.put(form.getSimpleName(), form);
            }
        }
        sortedRuleformTypes = new ArrayList<>(entityMap.keySet());
        Collections.sort(sortedRuleformTypes);
    }

    @Context
    private UriInfo uriInfo;

    public RuleformResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Path("context")
    @GET
    public Map<String, Object> getContext() {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(getClass());
        ub.path("context");
        ub.path("context.jsonld");
        Map<String, Object> context = new TreeMap<>();
        context.put(Constants.ID, ub.build().toASCIIString());
        return context;
    }

    @Path("context/{ruleform-type}")
    @GET
    public RuleformContext getContext(@PathParam("ruleform-type") String ruleformType) {
        RuleformContext ruleformContext = new RuleformContext(entityMap.get(ruleformType),
                                                              uriInfo);
        return ruleformContext;
    }

    @Path("type/{ruleform-type}")
    @GET
    public List<Object> getType(@PathParam("ruleform-type") String ruleformType) {
        Map<String, Object> definition = new TreeMap<>();
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleformType);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformClass),
                                              Status.NOT_FOUND);
        }
        definition.put(Constants.ID,
                       RuleformContext.getTypeIri(ruleformClass, uriInfo));
        definition.put(Constants.TYPE,
                       String.format("http://ultrastructure.me#%s",
                                     ruleformType));
        List<Object> type = new ArrayList<>();
        type.add(new RuleformContext(ruleformClass, uriInfo));
        type.add(definition);
        return type;
    }

    @Path("type/{ruleform-type}/{term}")
    @GET
    public Map<String, Object> getTerm(@PathParam("ruleform-type") String ruleformType,
                                       @PathParam("term") String term) {
        Map<String, Object> definition = new TreeMap<>();
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleformType);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformType),
                                              Status.NOT_FOUND);
        }
        definition.put(Constants.ID, RuleformContext.getTermIri(ruleformClass,
                                                                term, uriInfo));
        definition.put(Constants.TYPE, "http://ultrastructure.me#term");
        return definition;
    }

    @Path("{ruleform-type}/{instance}")
    @GET
    public RuleformNode getInstance(@PathParam("ruleform-type") String ruleformType,
                                    @PathParam("instance") String ruleformId) {
        UUID uuid = toUuid(ruleformId);
        Ruleform ruleform = (Ruleform) readOnlyModel.getEntityManager().find(entityMap.get(ruleformType),
                                                                             uuid);
        if (ruleform == null) {
            throw new WebApplicationException(String.format("%s:%s does not exist",
                                                            ruleformType,
                                                            ruleformId));
        }
        return new RuleformNode(ruleform, uriInfo);
    }

    @Path("{ruleform-type}")
    @GET
    public List<Map<String, String>> getInstances(@PathParam("ruleform-type") String ruleformType) {
        Class<? extends Ruleform> ruleformClass = entityMap.get(ruleformType);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformType));
        }
        List<Map<String, String>> instances = new ArrayList<>();
        readOnlyModel.findAll(ruleformClass).forEach(rf -> {
            Map<String, String> map = new TreeMap<>();
            map.put(Constants.CONTEXT,
                    RuleformContext.getContextIri(rf.getClass(), uriInfo));
            map.put(Constants.ID, RuleformNode.getIri(rf, uriInfo));
            instances.add(map);
        });
        return instances;
    }

    @GET
    public List<String> getRuleformTypes() {
        return sortedRuleformTypes;
    }
}