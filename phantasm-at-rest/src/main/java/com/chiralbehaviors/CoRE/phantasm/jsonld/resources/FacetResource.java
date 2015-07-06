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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Facet;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static String getTypeContextIri(UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        try {
            ub.path(FacetResource.class.getMethod("getContext"));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to retrieve getContext method",
                                            e);
        }
        String ctxtUrl = ub.build().toASCIIString();
        return ctxtUrl;
    }

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
        String ctxtUrl = getTypeContextIri(uriInfo);
        Map<String, Object> node = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        node.put(Constants.CONTEXT, context);
        context.put(Constants.ID, ctxtUrl);
        return node;
    }

    @Path("context/{ruleform-type}/{classifier}/{classification}")
    @GET
    public Map<String, Object> getContext(@PathParam("ruleform-type") String ruleformType,
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getInstance(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                @PathParam("classifier") String relationship,
                                                                                                                                                @PathParam("classification") String ruleform,
                                                                                                                                                @PathParam("instance") String facetInstance,
                                                                                                                                                @QueryParam("frame") String frame) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        UUID existential = toUuid(facetInstance);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        RuleForm instance = networkedModel.find(existential);
        if (instance == null) {
            throw new WebApplicationException(String.format("node %s is not found [%s:%s] (%s:%s)",
                                                            Status.NOT_FOUND));
        }
        Facet<RuleForm, Network> node = new Facet<>(aspect, readOnlyModel,
                                                    uriInfo);
        try {
            return frame != null ? frame(URLDecoder.decode(frame, "UTF-8"),
                                         node, instance)
                                 : node.toInstance(instance, readOnlyModel,
                                                   uriInfo);
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(String.format("frame was not encoded correctly: %s",
                                                            frame),
                                              Status.BAD_REQUEST);
        }
    }

    @Path("term/{ruleform-type}/{classifier}/{classification}/{property}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getInstanceProperty(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                           @PathParam("classifier") String relationship,
                                                                                                                                           @PathParam("classification") String ruleform,
                                                                                                                                           @PathParam("property") String property,
                                                                                                                                           @QueryParam("instance") String facetInstance) {
        if (facetInstance == null) {
            throw new WebApplicationException("No instance query specified",
                                              Status.BAD_REQUEST);
        }
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        Facet<RuleForm, Network> node = new Facet<>(aspect, readOnlyModel,
                                                    uriInfo);
        Map<String, String> propertyReference = node.getPropertyReference(property);
        return propertyReference;
    }

    @Path("type/{ruleform-type}/{classifier}/{classification}/{term}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getTerm(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") String relationship,
                                                                                                                                            @PathParam("classification") String ruleform,
                                                                                                                                            @PathParam("term") String term) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                     uriInfo);
        Map<String, Object> clazz = new TreeMap<>();
        clazz.put(Constants.CONTEXT, getTypeContextIri(uriInfo));
        clazz.put(Constants.ID, Facet.getTermIri(aspect, term, uriInfo));
        Attribute attribute = facet.getAttribute(term);
        if (attribute != null) {
            clazz.put(Constants.TYPE, Facet.getIri(attribute, uriInfo));
        } else {
            Aspect<?> targetAspect = facet.getTerm(term);
            if (targetAspect != null) {
                clazz.put(Constants.TYPE,
                          Facet.getTypeIri(targetAspect, uriInfo));
            } else {
                throw new WebApplicationException(String.format("term %s does not exist on %s",
                                                                term, aspect),
                                                  Status.NOT_FOUND);
            }

        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    @Path("type/{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Object> getType(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                     @PathParam("classifier") String relationship,
                                                                                                                                     @PathParam("classification") String ruleform) {
        Map<String, Object> definition = new TreeMap<>();
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                     uriInfo);
        definition.put(Constants.ID, Facet.getTypeIri(aspect, uriInfo));
        definition.put(Constants.TYPE, "http://ultrastructure.me#Facet");
        List<Object> type = new ArrayList<>();
        type.add((Map<String, Object>) facet.toContext().get(Constants.CONTEXT));
        type.add(definition);
        return type;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, Object> createContext(Aspect<?> aspect) {
        return new Facet(aspect, readOnlyModel, uriInfo).toContext();
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> frame(String frameDescription,
                                                                                                                                           Facet<RuleForm, Network> node,
                                                                                                                                           RuleForm instance) {
        Map<?, ?> frame;
        try {
            frame = new ObjectMapper().readValue(frameDescription.getBytes(),
                                                 Map.class);
        } catch (IOException e) {
            throw new WebApplicationException(String.format("Invalid frame: %s",
                                                            frameDescription),
                                              Status.BAD_REQUEST);
        }
        JsonLdOptions options = new JsonLdOptions();
        options.setEmbed(true);
        try {
            return JsonLdProcessor.frame(node.toInstance(instance,
                                                         readOnlyModel,
                                                         uriInfo),
                                         frame, options);
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
            Map<String, String> ctx = new TreeMap<>();
            ctx.put(Constants.CONTEXT, Facet.getContextIri(aspect, uriInfo));
            ctx.put(Constants.ID, Facet.getNodeIri(aspect, ruleform, uriInfo));
            ctx.put(Constants.TYPE, Facet.getTypeIri(aspect, uriInfo));
            facets.add(ctx);
        }
        return facets;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<Map<String, String>> facets = new ArrayList<>();
        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            Map<String, String> ctx = new TreeMap<>();
            ctx.put("Type Name",
                    String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
            ctx.put(Constants.ID, Facet.getTypeIri(aspect, uriInfo));
            ctx.put("All Facet Instances",
                    Facet.getAllInstancesIri(aspect, uriInfo));
            facets.add(ctx);
        }
        return facets;
    }
}
