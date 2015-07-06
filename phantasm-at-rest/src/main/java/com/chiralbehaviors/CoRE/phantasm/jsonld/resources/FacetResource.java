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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContext;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/")
@Produces({ "application/json", "text/json" })
public class FacetResource extends TransactionalResource {

    @Context
    private UriInfo uriInfo;

    public FacetResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getAllInstances(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                          @PathParam("classifier") String relationship,
                                                                                                                                                          @PathParam("classification") String ruleform) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        return getFacetInstances(aspect);
    }

    @Path("context")
    public Map<String, Object> getContext() {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        try {
            ub.path(getClass().getMethod("getContext"));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to retrieve getContext method",
                                            e);
        }
        Map<String, Object> context = new HashMap<>();
        context.put(Constants.ID, ub.build().toASCIIString());
        return context;
    }

    @Path("context/{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> getContext(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                           @PathParam("classifier") String relationship,
                                                                                                                                                           @PathParam("classification") String ruleform) {
        return createContext(getAspect(ruleformType, relationship, ruleform));
    }

    @Path("{ruleform-type}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacets(@PathParam("ruleform-type") String ruleformType) {
        switch (ruleformType) {
            case "Agency":
                return getFacets(readOnlyModel.getAgencyModel());
            case "Attribute":
                return getFacets(readOnlyModel.getAttributeModel());
            case "Interval":
                return getFacets(readOnlyModel.getIntervalModel());
            case "Location":
                return getFacets(readOnlyModel.getLocationModel());
            case "Product":
                return getFacets(readOnlyModel.getProductModel());
            case "Relationship":
                return getFacets(readOnlyModel.getRelationshipModel());
            case "StatusCode":
                return getFacets(readOnlyModel.getStatusCodeModel());
            case "Unit":
                return getFacets(readOnlyModel.getAgencyModel());
        }
        throw new WebApplicationException(String.format("%s does not exist",
                                                        ruleformType),
                                          Status.NOT_FOUND);
    }

    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getInstance(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                   @PathParam("classifier") String relationship,
                                                                                                                                   @PathParam("classification") String ruleform,
                                                                                                                                   @PathParam("instance") String facetInstance,
                                                                                                                                   @QueryParam("frame") String frame) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        ObjectNode node = createFacetNode(facetInstance, aspect);
        try {
            return frame != null ? frame(URLDecoder.decode(frame, "UTF-8"),
                                         node)
                                 : node;
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(String.format("frame was not encoded correctly: %s",
                                                            frame),
                                              Status.BAD_REQUEST);
        }
    }

    @Path("type/{ruleform-type}/{classifier}/{classification}/{term}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getTerm(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") String relationship,
                                                                                                                                            @PathParam("classification") String ruleform,
                                                                                                                                            @PathParam("term") String term) {
        Map<String, Object> clazz = new HashMap<>();
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        clazz.put(Constants.ID, FacetContext.getTermIri(aspect, term, uriInfo));
        clazz.put(Constants.TYPE, "http://ultrastructure.me#term");
        return clazz;
    }

    @Path("type/{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getType(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") String relationship,
                                                                                                                                            @PathParam("classification") String ruleform) {
        Map<String, Object> clazz = new HashMap<>();
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        clazz.put(Constants.ID, FacetContext.getTypeIri(aspect, uriInfo));
        clazz.put(Constants.TYPE, "http://ultrastructure.me#Facet");
        return clazz;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> createContext(Aspect<?> aspect) {
        return new FacetContext(aspect, readOnlyModel, uriInfo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> ObjectNode createFacetNode(String facetInstance,
                                                                                                                                            Aspect<RuleForm> aspect) {
        UUID existential = toUuid(facetInstance);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        RuleForm instance = networkedModel.find(existential);
        if (instance == null) {
            throw new WebApplicationException(String.format("node %s is not found [%s:%s] (%s:%s)",
                                                            Status.NOT_FOUND));
        }
        return new FacetNode(instance, aspect, readOnlyModel, uriInfo).toNode();
    }

    private Map<String, Object> frame(String frameDescription,
                                      ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> from;
        try {
            from = objectMapper.treeToValue(node, Map.class);
        } catch (JsonProcessingException e) {
            throw new WebApplicationException("Cannot convert facet instanc eto map",
                                              Status.INTERNAL_SERVER_ERROR);
        }
        JsonLdOptions options = new JsonLdOptions();
        options.setEmbed(true);
        Map<?, ?> frame;
        try {
            frame = objectMapper.readValue(frameDescription.getBytes(),
                                           Map.class);
        } catch (IOException e) {
            throw new WebApplicationException(String.format("Invalid frame: %s",
                                                            frameDescription),
                                              Status.BAD_REQUEST);
        }
        try {
            return JsonLdProcessor.frame(from, frame, options);
        } catch (JsonLdError e) {
            throw new WebApplicationException(String.format("Invalid frame %s",
                                                            frame),
                                              Status.BAD_REQUEST);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacetInstances(Aspect<RuleForm> aspect) {
        List<Map<String, String>> facets = new ArrayList<>();
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        for (RuleForm ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                            aspect.getClassifier().getInverse())) {
            Map<String, String> ctx = new HashMap<>();
            ctx.put(Constants.CONTEXT,
                    FacetContext.getContextIri(aspect, uriInfo));
            ctx.put(Constants.ID,
                    FacetContext.getNodeIri(aspect, ruleform, uriInfo));
            ctx.put(Constants.TYPE, FacetContext.getTypeIri(aspect, uriInfo));
            facets.add(ctx);
        }
        return facets;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<Map<String, String>> facets = new ArrayList<>();
        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            Map<String, String> ctx = new HashMap<>();
            FacetContext facetContext = new FacetContext(aspect, readOnlyModel,
                                                         uriInfo, true);
            ctx.put("Type Name",
                    String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
            ctx.put(Constants.ID, facetContext.getIri());
            ctx.put("All Facet Instances", facetContext.getAllInstancesIri());
            facets.add(ctx);
        }
        return facets;
    }
}
