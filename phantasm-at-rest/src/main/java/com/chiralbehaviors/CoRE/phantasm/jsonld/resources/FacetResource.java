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
import java.util.Arrays;
import java.util.HashMap;
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
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Facet;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet")
@Produces({ "application/json", "text/json" })
public class FacetResource extends TransactionalResource {

    public static String getTypeContextIri(UriInfo uriInfo) {
        UriBuilder ub = UriBuilder.fromResource(FacetResource.class);
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, Object>> getAllInstances(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                          @PathParam("classifier") UUID classifier,
                                                                                                                                                          @PathParam("classification") UUID classification) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, classifier,
                                            classification);
        List<Map<String, Object>> facets = new ArrayList<>();
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        for (RuleForm ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                            aspect.getClassifier().getInverse())) {
            Map<String, Object> ctx = new TreeMap<>();
            ctx.put(Constants.TYPENAME,
                    String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
            ctx.put("name", ruleform.getName());
            ctx.put(Constants.CONTEXT, Facet.getContextIri(aspect, uriInfo));
            ctx.put(Constants.ID, Facet.getNodeIri(aspect, ruleform, uriInfo));
            ctx.put(Constants.TYPE, Facet.getTypeIri(aspect, uriInfo));
            facets.add(ctx);
        }
        return facets;
    }

    @Path("context")
    @GET
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
                                          @PathParam("classifier") UUID relationship,
                                          @PathParam("classification") UUID ruleform) {
        return createContext(getAspect(ruleformType, relationship, ruleform));
    }

    @GET
    public Map<String, String> getFacetRuleforms() {
        Map<String, String> ruleforms = new HashMap<String, String>();
        ruleforms.put("Agency", Facet.getFacetsIri(uriInfo, Agency.class));
        ruleforms.put("Attribute",
                      Facet.getFacetsIri(uriInfo, Attribute.class));
        ruleforms.put("Interval", Facet.getFacetsIri(uriInfo, Interval.class));
        ruleforms.put("Location", Facet.getFacetsIri(uriInfo, Location.class));
        ruleforms.put("Product", Facet.getFacetsIri(uriInfo, Product.class));
        ruleforms.put("Relationship",
                      Facet.getFacetsIri(uriInfo, Relationship.class));
        ruleforms.put("Status Code",
                      Facet.getFacetsIri(uriInfo, StatusCode.class));
        ruleforms.put("Unit", Facet.getFacetsIri(uriInfo, Unit.class));
        return ruleforms;
    }

    @Path("{ruleform-type}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacets(@PathParam("ruleform-type") String ruleformType) {
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
                                                                                                                                                @PathParam("classifier") UUID relationship,
                                                                                                                                                @PathParam("classification") UUID ruleform,
                                                                                                                                                @PathParam("instance") UUID existential,
                                                                                                                                                @QueryParam("frame") String frame) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        RuleForm instance = networkedModel.find(existential);
        if (instance == null) {
            throw new WebApplicationException(String.format("node %s is not found %s (%s)",
                                                            instance, this,
                                                            aspect),
                                              Status.NOT_FOUND);
        }
        Facet<RuleForm, Network> node = new Facet<>(aspect, readOnlyModel,
                                                    uriInfo);
        if (frame != null) {
            try {
                return frame(URLDecoder.decode(frame, "UTF-8"), node, instance);
            } catch (UnsupportedEncodingException e) {
                throw new WebApplicationException(String.format("frame was not encoded correctly: %s",
                                                                frame),
                                                  Status.BAD_REQUEST);
            }
        } else {
            return node.toInstance(instance, readOnlyModel, uriInfo);
        }
    }

    @Path("{ruleform-type}/{classifier}/{classification}/{instance}/{traversal:.+}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object select(@PathParam("ruleform-type") String ruleformType,
                                                                                                                              @PathParam("classifier") UUID relationship,
                                                                                                                              @PathParam("classification") UUID ruleform,
                                                                                                                              @PathParam("instance") UUID existential,
                                                                                                                              @PathParam("traversal") List<PathSegment> traversal,
                                                                                                                              @QueryParam("frame") String frame) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        RuleForm instance = networkedModel.find(existential);
        if (instance == null) {
            throw new WebApplicationException(String.format("node %s is not found %s (%s)",
                                                            instance, this,
                                                            aspect),
                                              Status.NOT_FOUND);
        }
        Facet<RuleForm, Network> node = new Facet<>(aspect, readOnlyModel,
                                                    uriInfo);
        Object value = getProperty(node, instance, traversal, networkedModel,
                                   uriInfo);
        return value;
    }

    @Path("term/{ruleform-type}/{classifier}/{classification}/{term}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getTerm(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") UUID relationship,
                                                                                                                                            @PathParam("classification") UUID ruleform,
                                                                                                                                            @PathParam("term") String term) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                     uriInfo);
        Map<String, Object> clazz = new TreeMap<>();
        clazz.put(Constants.ID, Facet.getTermIri(aspect, term, uriInfo));
        Attribute attribute = facet.getAttribute(term);
        if (attribute != null) {
            clazz.put(Constants.TYPE,
                      RuleformContext.getIri(attribute, uriInfo));
        } else if (facet.getTerm(term) != null) {
            Aspect<?> targetAspect = facet.getTerm(term);
            clazz.put(Constants.TYPE, Facet.getTypeIri(targetAspect, uriInfo));

        } else if (facet.getRuleformTerm(term) != null) {
            clazz.put(Constants.TYPE, facet.getRuleformTerm(term).type);
        } else {
            throw new WebApplicationException(String.format("term %s does not exist on %s",
                                                            term, aspect),
                                              Status.NOT_FOUND);
        }
        return clazz;
    }

    @Path("type/{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getType(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") UUID relationship,
                                                                                                                                            @PathParam("classification") UUID ruleform) {

        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                     uriInfo);
        Map<String, Object> type = facet.toContext();
        type.put(Constants.ID, Facet.getTypeIri(aspect, uriInfo));
        type.put(Constants.TYPE, "http://ultrastructure.me#Facet");
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
        List<Map<String, Object>> graph = traverse(instance, node, frame);
        JsonLdOptions options = new JsonLdOptions();
        options.setEmbed(true);
        try {
            return JsonLdProcessor.frame(graph, frame, options);
        } catch (JsonLdError e) {
            throw new WebApplicationException(String.format("Invalid frame %s",
                                                            frame),
                                              Status.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getAgencyLocationProperty(RuleForm instance,
                                                                                                 XDomainNetworkAuthorization<Agency, Location> auth,
                                                                                                 List<PathSegment> traversal,
                                                                                                 NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                 UriInfo uriInfo) {
        Aspect<?> childAspect = auth.isForward() ? new Aspect<>(auth.getToRelationship(),
                                                                auth.getToParent())
                                                 : new Aspect<>(auth.getFromRelationship(),
                                                                auth.getFromParent());

        List<Object> result = new ArrayList<>();
        if (auth.isForward()) {
            @SuppressWarnings("rawtypes")
            Facet<Location, ?> childFacet = new Facet(childAspect,
                                                      readOnlyModel, uriInfo);
            for (Location child : networkedModel.getAuthorizedLocations(instance,
                                                                        auth.getConnection())) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getLocationModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        } else {
            List<Agency> children = networkedModel.getAuthorizedAgencies(instance,
                                                                         auth.getConnection());
            @SuppressWarnings("rawtypes")
            Facet<Agency, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                    uriInfo);
            for (Agency child : children) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getAgencyModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getAgencyProductProperty(RuleForm instance,
                                                                                                XDomainNetworkAuthorization<Agency, Product> auth,
                                                                                                List<PathSegment> traversal,
                                                                                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                UriInfo uriInfo) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Aspect<RuleForm> childAspect = auth.isForward() ? new Aspect(auth.getToRelationship(),
                                                                     auth.getToParent())
                                                        : new Aspect(auth.getFromRelationship(),
                                                                     auth.getFromParent());

        List<Object> result = new ArrayList<>();
        if (auth.isForward()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Product, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                     uriInfo);
            for (Product child : networkedModel.getAuthorizedProducts(instance,
                                                                      auth.getConnection())) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        } else {
            List<Agency> children = networkedModel.getAuthorizedAgencies(instance,
                                                                         auth.getConnection());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Agency, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                    uriInfo);
            for (Agency child : children) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getAgencyModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getAttribute(RuleForm instance,
                                                                                    Attribute attribute,
                                                                                    NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (attribute.getIndexed()) {
            @SuppressWarnings("unchecked")
            List<AttributeValue<RuleForm>> attributeValues = (List<AttributeValue<RuleForm>>) networkedModel.getAttributeValues(instance,
                                                                                                                                attribute);
            List<Object> values = new ArrayList<>(attributeValues.size());
            for (AttributeValue<RuleForm> value : attributeValues) {
                values.add(value.getValue());
            }
            return values;
        } else {
            return networkedModel.getAttributeValue(instance, attribute);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getChild(RuleForm instance,
                                                                                NetworkAuthorization<RuleForm> auth,
                                                                                List<PathSegment> traversal,
                                                                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                UriInfo uriInfo) {
        Aspect<RuleForm> childAspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                                    auth.getAuthorizedParent());
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Facet<RuleForm, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                  uriInfo);
        if (auth.getCardinality() == Cardinality.N) {
            List<Object> result = new ArrayList<>();
            // TODO handle infered as well as immediate
            for (RuleForm child : networkedModel.getImmediateChildren(instance,
                                                                      auth.getChildRelationship())) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           networkedModel, uriInfo));
                    result.add(object);
                }
            }
            return result;
        } else {
            RuleForm immediateChild = networkedModel.getImmediateChild(instance,
                                                                       auth.getChildRelationship());
            if (traversal.isEmpty()) {
                return immediateChild == null ? null
                                              : childFacet.toInstance(immediateChild,
                                                                      readOnlyModel,
                                                                      uriInfo);
            } else {
                Map<String, Object> object = new TreeMap<String, Object>();
                object.put(traversal.get(0).getPath(),
                           getProperty(childFacet, immediateChild, traversal,
                                       networkedModel, uriInfo));
                return object;
            }

        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Map<String, Object> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, Object> result = new TreeMap<>();
        Map<String, Object> terms = new TreeMap<>();
        terms.put("allInstances", Constants.ID);
        result.put(Constants.CONTEXT, terms);
        List<Map<String, Object>> facets = new ArrayList<>();
        result.put(Constants.GRAPH, facets);
        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            Map<String, Object> ctx = new TreeMap<>();
            ctx.put(Constants.TYPENAME,
                    String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
            ctx.put(Constants.ID, Facet.getTypeIri(aspect, uriInfo));
            ctx.put("allInstances", Facet.getAllInstancesIri(aspect, uriInfo));
            facets.add(ctx);
        }
        return result;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getProductLocationProperty(RuleForm instance,
                                                                                                  XDomainNetworkAuthorization<Product, Location> auth,
                                                                                                  List<PathSegment> traversal,
                                                                                                  NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                  UriInfo uriInfo) {
        Aspect<?> childAspect = auth.isForward() ? new Aspect<>(auth.getToRelationship(),
                                                                auth.getToParent())
                                                 : new Aspect<>(auth.getFromRelationship(),
                                                                auth.getFromParent());

        List<Object> result = new ArrayList<>();
        if (auth.isForward()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Location, ?> childFacet = new Facet(childAspect,
                                                      readOnlyModel, uriInfo);
            for (Location child : networkedModel.getAuthorizedLocations(instance,
                                                                        auth.getConnection())) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getLocationModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        } else {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Product, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                     uriInfo);
            List<Product> children = networkedModel.getAuthorizedProducts(instance,
                                                                          auth.getConnection());
            for (Product child : children) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getProductRelationshipProperty(RuleForm instance,
                                                                                                      XDomainNetworkAuthorization<Product, Relationship> auth,
                                                                                                      List<PathSegment> traversal,
                                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                      UriInfo uriInfo) {
        Aspect<?> childAspect = auth.isForward() ? new Aspect<>(auth.getToRelationship(),
                                                                auth.getToParent())
                                                 : new Aspect<>(auth.getFromRelationship(),
                                                                auth.getFromParent());

        List<Object> result = new ArrayList<>();
        if (auth.isForward()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Relationship, ?> childFacet = new Facet(childAspect,
                                                          readOnlyModel,
                                                          uriInfo);
            for (Relationship child : networkedModel.getAuthorizedRelationships(instance,
                                                                                auth.getConnection())) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getRelationshipModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        } else {
            List<Product> children = networkedModel.getAuthorizedProducts(instance,
                                                                          auth.getConnection());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Product, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                     uriInfo);
            for (Product child : children) {
                if (traversal.isEmpty()) {
                    result.add(childFacet.toInstance(child, readOnlyModel,
                                                     uriInfo));
                } else {
                    Map<String, Object> object = new TreeMap<String, Object>();
                    object.put(traversal.get(0).getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo));
                    result.add(object);
                }
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getProperty(Facet<RuleForm, ?> facet,
                                                                                   RuleForm instance,
                                                                                   List<PathSegment> traversal,
                                                                                   NetworkedModel<RuleForm, ?, ?, ?> locationModel,
                                                                                   UriInfo uriInfo) {
        String property = traversal.get(0).getPath();
        traversal = traversal.subList(1, traversal.size());
        Attribute attribute = facet.getAttribute(property);
        if (attribute != null) {
            return getAttribute(instance, attribute, locationModel);
        }
        NetworkAuthorization<RuleForm> auth = facet.getNetworkAuth(property);
        if (auth != null) {
            return getChild(instance, auth, traversal, locationModel, uriInfo);
        }
        XDomainNetworkAuthorization<Agency, Location> agencyLocationAuth = facet.getAgencyLocationAuth(property);
        if (agencyLocationAuth != null) {
            return getAgencyLocationProperty(instance, agencyLocationAuth,
                                             traversal, locationModel, uriInfo);
        }
        XDomainNetworkAuthorization<Agency, Product> agencyProductAuth = facet.getAgencyProductAuth(property);
        if (agencyProductAuth != null) {
            return getAgencyProductProperty(instance, agencyProductAuth,
                                            traversal, locationModel, uriInfo);
        }
        XDomainNetworkAuthorization<Product, Location> productLocationAuth = facet.getProductLocationAuth(property);
        if (productLocationAuth != null) {
            return getProductLocationProperty(instance, productLocationAuth,
                                              traversal, locationModel,
                                              uriInfo);
        }
        XDomainNetworkAuthorization<Product, Relationship> productRelationshipAuth = facet.getProductRelationshipAuth(property);
        if (productRelationshipAuth != null) {
            return getProductRelationshipProperty(instance,
                                                  productRelationshipAuth,
                                                  traversal, locationModel,
                                                  uriInfo);
        }
        throw new WebApplicationException(String.format("%s is not a property of %s:%s",
                                                        property,
                                                        facet.getClassifier(),
                                                        facet.getClassification()),
                                          Status.NOT_FOUND);
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, Object>> traverse(RuleForm instance,
                                                                                                                                                    Facet<RuleForm, Network> node,
                                                                                                                                                    Map<?, ?> frame) {
        return Arrays.asList(node.toInstance(instance, readOnlyModel, uriInfo));
    }
}
