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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
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
import com.codahale.metrics.annotation.Timed;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet")
@Produces({ "application/json", "text/json" })
public class FacetResource extends TransactionalResource {

    public static URI facetResourceIri(UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        return ub.build();
    }

    public static URI getTypeContextIri() {
        UriBuilder ub = UriBuilder.fromResource(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getContext"));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to retrieve getContext method",
                                            e);
        }
        return ub.build();
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

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @POST
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Response apply(@PathParam("ruleform-type") String ruleformType,
                                                                                                                               @PathParam("classifier") UUID relationship,
                                                                                                                               @PathParam("classification") UUID ruleform,
                                                                                                                               @PathParam("instance") UUID existential,
                                                                                                                               @QueryParam("select") List<String> selection) {
        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
            RuleForm instance = networkedModel.find(existential);
            networkedModel.initialize(instance, aspect);
            return Response.ok()
                           .build();
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @DELETE
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Response delete(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                @PathParam("classifier") UUID relationship,
                                                                                                                                @PathParam("classification") UUID ruleform,
                                                                                                                                @PathParam("instance") UUID existential,
                                                                                                                                @QueryParam("select") List<String> selection) {
        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
            RuleForm instance = networkedModel.find(existential);
            networkedModel.unlink(instance, aspect.getClassifier(),
                                  aspect.getClassification());
            return Response.ok()
                           .build();
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/instances")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getAllInstances(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                    @PathParam("classifier") UUID classifier,
                                                                                                                                                    @PathParam("classification") UUID classification,
                                                                                                                                                    @QueryParam("select") List<String> selection) {
        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, classifier,
                                                classification, readOnlyModel);

            Map<String, Object> returned = new TreeMap<>();

            returned.put(Constants.CONTEXT,
                         Facet.getContextIri(aspect, uriInfo));
            returned.put(Constants.ID,
                         Facet.getAllInstancesIri(aspect, uriInfo));

            List<Map<String, Object>> facets = new ArrayList<>();
            returned.put(Constants.GRAPH, facets);

            Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                         uriInfo);
            NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
            for (RuleForm ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                                aspect.getClassifier()
                                                                      .getInverse())) {
                facets.add(traverse(ruleform, selection, facet, networkedModel,
                                    readOnlyModel));
            }
            return returned;
        });
    }

    @Timed
    @Path("context")
    @GET
    public Map<String, Object> getContext() {
        Map<String, Object> node = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        node.put(Constants.CONTEXT, context);
        context.put(Constants.ID, getTypeContextIri().toASCIIString());
        return node;
    }

    @Path("{ruleform-type}/{classifier}/{classification}/context")
    @GET
    public Map<String, Object> getContext(@PathParam("ruleform-type") String ruleformType,
                                          @PathParam("classifier") UUID relationship,
                                          @PathParam("classification") UUID ruleform) {
        return readOnly(readOnlyModel -> createContext(getAspect(ruleformType,
                                                                 relationship,
                                                                 ruleform,
                                                                 readOnlyModel),
                                                       readOnlyModel));
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacet(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                             @PathParam("classifier") UUID relationship,
                                                                                                                                             @PathParam("classification") UUID ruleform) {

        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                         uriInfo);
            Map<String, Object> type = facet.toContext(uriInfo);
            type.put(Constants.ID, "");
            type.put(Constants.TYPE, "http://ultrastructure.me#Facet");
            return type;
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/@facet:{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacetQualifiedInstance(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                              @PathParam("classifier") UUID relationship,
                                                                                                                                                              @PathParam("classification") UUID ruleform,
                                                                                                                                                              @PathParam("instance") UUID existential,
                                                                                                                                                              @QueryParam("select") List<String> selection) {
        return getInstance(ruleformType, relationship, ruleform, existential,
                           selection);
    }

    @Timed
    @GET
    public Map<String, Object> getFacetRuleforms() {

        List<String> ruleforms = new ArrayList<>();
        ruleforms.add("Agency");
        ruleforms.add("Attribute");
        ruleforms.add("Interval");
        ruleforms.add("Location");
        ruleforms.add("Product");
        ruleforms.add("Relationship");
        ruleforms.add("Status Code");
        ruleforms.add("Unit");

        Map<String, Object> context = new TreeMap<>();
        Map<String, Object> keyTerm = new TreeMap<>();
        keyTerm.put(Constants.ID, facetResourceIri(uriInfo));
        keyTerm.put(Constants.TYPE, Constants.ID);
        keyTerm.put(Constants.CONTAINER, Constants.LIST);
        context.put("ruleforms", keyTerm);
        Map<String, Object> returned = new TreeMap<>();
        returned.put(Constants.CONTEXT, context);
        returned.put(Constants.ID, facetResourceIri(uriInfo));
        returned.put("ruleforms", ruleforms);
        return returned;
    }

    @Timed
    @Path("{ruleform-type}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacets(@PathParam("ruleform-type") String ruleformType) {
        return readOnly(readOnlyModel -> {
            Class<?> clazz = RuleformResource.entityMap.get(ruleformType);
            switch (ruleformType) {
                case "Agency":
                    return getFacets(readOnlyModel.getAgencyModel(), clazz,
                                     readOnlyModel);
                case "Attribute":
                    return getFacets(readOnlyModel.getAttributeModel(), clazz,
                                     readOnlyModel);
                case "Interval":
                    return getFacets(readOnlyModel.getIntervalModel(), clazz,
                                     readOnlyModel);
                case "Location":
                    return getFacets(readOnlyModel.getLocationModel(), clazz,
                                     readOnlyModel);
                case "Product":
                    return getFacets(readOnlyModel.getProductModel(), clazz,
                                     readOnlyModel);
                case "Relationship":
                    return getFacets(readOnlyModel.getRelationshipModel(),
                                     clazz, readOnlyModel);
                case "StatusCode":
                    return getFacets(readOnlyModel.getStatusCodeModel(), clazz,
                                     readOnlyModel);
                case "Unit":
                    return getFacets(readOnlyModel.getAgencyModel(), clazz,
                                     readOnlyModel);
            }
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformType),
                                              Status.NOT_FOUND);
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getInstance(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                @PathParam("classifier") UUID relationship,
                                                                                                                                                @PathParam("classification") UUID ruleform,
                                                                                                                                                @PathParam("instance") UUID existential,
                                                                                                                                                @QueryParam("select") List<String> selection) {
        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
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
            Map<String, Object> object = traverse(instance, selection, node,
                                                  networkedModel,
                                                  readOnlyModel);
            object.put(Constants.CONTEXT, Facet.getContextIri(node, uriInfo));
            object.put(Constants.TYPE, Constants.FACET);
            object.put(Constants.TYPENAME, node.getTypeName());
            return object;
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/@ruleform:{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getRuleformQualifiedInstance(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                                 @PathParam("classifier") UUID relationship,
                                                                                                                                                                 @PathParam("classification") UUID ruleform,
                                                                                                                                                                 @PathParam("instance") UUID existential,
                                                                                                                                                                 @QueryParam("select") List<String> selection) {
        return getInstance(ruleformType, relationship, ruleform, existential,
                           selection);
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/term/{term}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getTerm(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") UUID relationship,
                                                                                                                                            @PathParam("classification") UUID ruleform,
                                                                                                                                            @PathParam("term") String term) {
        return readOnly(readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            Facet<RuleForm, Network> facet = new Facet<>(aspect, readOnlyModel,
                                                         uriInfo);
            Map<String, Object> clazz = new TreeMap<>();
            clazz.put(Constants.ID, Facet.getTermIri(aspect, term, uriInfo));
            Attribute attribute = facet.getAttribute(term);
            if (attribute != null) {
                clazz.put(Constants.TYPE, RuleformContext.getIri(attribute));
            } else if (facet.getTerm(term) != null) {
                Aspect<?> targetAspect = facet.getTerm(term);
                clazz.put(Constants.TYPE,
                          Facet.getFullFacetIri(targetAspect, uriInfo));

            } else if (facet.getRuleformTerm(term) != null) {
                clazz.put(Constants.TYPE, facet.getRuleformTerm(term).type);
            } else {
                throw new WebApplicationException(String.format("term %s does not exist on %s",
                                                                term, aspect),
                                                  Status.NOT_FOUND);
            }
            return clazz;
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void additionalAttributes(ExistentialRuleform instance, Facet facet,
                                      NetworkedModel networkedModel,
                                      Map<String, Object> object,
                                      MultivaluedMap<String, String> parameters) {
        if (!parameters.containsKey("a")) {
            return;
        }
        for (String property : parameters.get("a")) {
            switch (property) {
                case "name":
                    object.put(property, instance.getName());
                    break;
                case "description":
                    object.put(property, instance.getDescription());
                    break;
                case "notes":
                    object.put(property, instance.getNotes());
                    break;
                case "updatedBy":
                    object.put(property,
                               new RuleformContext(Agency.class,
                                                   uriInfo).getShort(instance.getUpdatedBy(),
                                                                     uriInfo));
                    break;
                default: {
                    Attribute attribute = facet.getAttribute(property);
                    if (attribute != null) {
                        object.put(property, getAttribute(instance, attribute,
                                                          networkedModel));
                    } else {
                        throw new WebApplicationException(String.format("%s not found on %s",
                                                                        property,
                                                                        facet));
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, Object> createContext(Aspect<?> aspect,
                                              Model readOnlyModel) {
        return new Facet(aspect, readOnlyModel, uriInfo).toContext(uriInfo);
    }

    @SuppressWarnings("unchecked")
    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getAgencyLocationProperty(RuleForm instance,
                                                                                                 MultivaluedMap<String, String> parameters,
                                                                                                 XDomainNetworkAuthorization<Agency, Location> auth,
                                                                                                 List<PathSegment> traversal,
                                                                                                 NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                 Model readOnlyModel) {
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
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getLocationModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getLocationModel(), object,
                                     parameters);
            }
        } else {
            List<Agency> children = networkedModel.getAuthorizedAgencies(instance,
                                                                         auth.getConnection());
            @SuppressWarnings("rawtypes")
            Facet<Agency, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                    uriInfo);
            for (Agency child : children) {
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getAgencyModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getAgencyModel(), object,
                                     parameters);
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getAgencyProductProperty(RuleForm instance,
                                                                                                MultivaluedMap<String, String> parameters,
                                                                                                XDomainNetworkAuthorization<Agency, Product> auth,
                                                                                                List<PathSegment> traversal,
                                                                                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                Model readOnlyModel) {
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
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getProductModel(), object,
                                     parameters);
            }
        } else {
            List<Agency> children = networkedModel.getAuthorizedAgencies(instance,
                                                                         auth.getConnection());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Agency, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                    uriInfo);
            for (Agency child : children) {
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getAgencyModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getAgencyModel(), object,
                                     parameters);
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
            return networkedModel.getAttributeValue(instance, attribute)
                                 .getValue();
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getChild(RuleForm instance,
                                                                                MultivaluedMap<String, String> parameters,
                                                                                NetworkAuthorization<RuleForm> auth,
                                                                                List<PathSegment> traversal,
                                                                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                Model readOnlyModel) {
        Aspect<RuleForm> childAspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                                    auth.getAuthorizedParent());
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Facet<RuleForm, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                  uriInfo);
        if (auth.getCardinality() == Cardinality.N) {
            List<Object> result = new ArrayList<>();
            // TODO handle inferred as well as immediate
            for (RuleForm child : networkedModel.getImmediateChildren(instance,
                                                                      auth.getChildRelationship())) {
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           networkedModel, uriInfo,
                                           readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet, networkedModel, object,
                                     parameters);
            }
            return result;
        } else {
            RuleForm immediateChild = networkedModel.getImmediateChild(instance,
                                                                       auth.getChildRelationship());
            if (immediateChild == null) {
                return null;
            }
            Map<String, Object> object = childFacet.getShort(immediateChild,
                                                             uriInfo);
            if (!traversal.isEmpty()) {
                object = (Map<String, Object>) childFacet.getShort(immediateChild,
                                                                   uriInfo);
                object.put(traversal.get(0)
                                    .getPath(),
                           getProperty(childFacet, immediateChild, traversal,
                                       networkedModel, uriInfo, readOnlyModel));
                additionalAttributes(immediateChild, childFacet, networkedModel,
                                     object, parameters);
            }
            additionalAttributes(immediateChild, childFacet, networkedModel,
                                 object, parameters);
            return object;

        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Map<String, Object> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                              Class<?> clazz,
                                                                                              Model readOnlyModel) {

        Map<String, Object> context = new TreeMap<>();
        context.put(Constants.VOCAB, uriInfo.getBaseUriBuilder()
                                            .path(FacetResource.class)
                                            .build());

        Map<String, Object> returned = new TreeMap<>();

        returned.put(Constants.CONTEXT, context);
        returned.put(Constants.ID, Facet.getFacetsIri(clazz, uriInfo));

        List<Map<String, Object>> facets = new ArrayList<>();
        returned.put(Constants.GRAPH, facets);

        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Facet<RuleForm, ?> facet = new Facet(aspect, readOnlyModel,
                                                 uriInfo);
            Map<String, Object> obj = new TreeMap<>();
            obj.put(Constants.ID, Facet.getFacetIri(aspect));
            obj.put(Constants.TYPENAME, facet.getTypeName());
            facets.add(obj);
        }

        return returned;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getProductLocationProperty(RuleForm instance,
                                                                                                  MultivaluedMap<String, String> parameters,
                                                                                                  XDomainNetworkAuthorization<Product, Location> auth,
                                                                                                  List<PathSegment> traversal,
                                                                                                  NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                  Model readOnlyModel) {
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
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getLocationModel(),
                                           uriInfo, readOnlyModel));
                }
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getLocationModel(), object,
                                     parameters);
                result.add(object);
            }
        } else {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Product, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                     uriInfo);
            List<Product> children = networkedModel.getAuthorizedProducts(instance,
                                                                          auth.getConnection());
            for (Product child : children) {
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getProductModel(), object,
                                     parameters);
            }
        }
        if (auth.getCardinality() == Cardinality.N) {
            return result;
        } else {
            return result.isEmpty() ? null : result.get(0);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Object getProductRelationshipProperty(RuleForm instance,
                                                                                                      MultivaluedMap<String, String> parameters,
                                                                                                      XDomainNetworkAuthorization<Product, Relationship> auth,
                                                                                                      List<PathSegment> traversal,
                                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                      Model readOnlyModel) {
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
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getRelationshipModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getRelationshipModel(),
                                     object, parameters);
            }
        } else {
            List<Product> children = networkedModel.getAuthorizedProducts(instance,
                                                                          auth.getConnection());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Facet<Product, ?> childFacet = new Facet(childAspect, readOnlyModel,
                                                     uriInfo);
            for (Product child : children) {
                Map<String, Object> object = childFacet.getShort(child,
                                                                 uriInfo);
                if (!traversal.isEmpty()) {
                    object = (Map<String, Object>) childFacet.getShort(child,
                                                                       uriInfo);
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(childFacet, child, traversal,
                                           readOnlyModel.getProductModel(),
                                           uriInfo, readOnlyModel));
                }
                result.add(object);
                additionalAttributes(child, childFacet,
                                     readOnlyModel.getProductModel(), object,
                                     parameters);
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
                                                                                   NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                   UriInfo uriInfo,
                                                                                   Model readOnlyModel) {
        String property = traversal.get(0)
                                   .getPath();
        MultivaluedMap<String, String> parameters = traversal.isEmpty() ? new MultivaluedHashMap<>()
                                                                        : traversal.get(0)
                                                                                   .getMatrixParameters();
        List<PathSegment> next = traversal.subList(1, traversal.size());
        switch (property) {
            case "name":
                return instance.getName();
            case "description":
                return instance.getDescription();
            case "notes":
                return instance.getNotes();
            case "updatedBy":
                return new RuleformContext(Agency.class,
                                           uriInfo).getShort(instance.getUpdatedBy(),
                                                             uriInfo);
        }
        Attribute attribute = facet.getAttribute(property);
        if (attribute != null) {
            return getAttribute(instance, attribute, networkedModel);
        }
        NetworkAuthorization<RuleForm> auth = facet.getNetworkAuth(property);
        if (auth != null) {
            return getChild(instance, parameters, auth, next, networkedModel,
                            readOnlyModel);
        }
        XDomainNetworkAuthorization<Agency, Location> agencyLocationAuth = facet.getAgencyLocationAuth(property);
        if (agencyLocationAuth != null) {
            return getAgencyLocationProperty(instance, parameters,
                                             agencyLocationAuth, next,
                                             networkedModel, readOnlyModel);
        }
        XDomainNetworkAuthorization<Agency, Product> agencyProductAuth = facet.getAgencyProductAuth(property);
        if (agencyProductAuth != null) {
            return getAgencyProductProperty(instance, parameters,
                                            agencyProductAuth, next,
                                            networkedModel, readOnlyModel);
        }
        XDomainNetworkAuthorization<Product, Location> productLocationAuth = facet.getProductLocationAuth(property);
        if (productLocationAuth != null) {
            return getProductLocationProperty(instance, parameters,
                                              productLocationAuth, next,
                                              networkedModel, readOnlyModel);
        }
        XDomainNetworkAuthorization<Product, Relationship> productRelationshipAuth = facet.getProductRelationshipAuth(property);
        if (productRelationshipAuth != null) {
            return getProductRelationshipProperty(instance, parameters,
                                                  productRelationshipAuth, next,
                                                  networkedModel,
                                                  readOnlyModel);
        }
        throw new WebApplicationException(String.format("%s is not a property of %s:%s",
                                                        property,
                                                        facet.getClassifier(),
                                                        facet.getClassification()),
                                          Status.NOT_FOUND);
    }

    private List<PathSegment> selectFrom(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        List<PathSegment> selection = new ArrayList<>();
        for (StringTokenizer tokes = new StringTokenizer(query,
                                                         "/"); tokes.hasMoreTokens();) {
            String segment = tokes.nextToken()
                                  .trim();
            if (segment.isEmpty()) {
                continue;
            }
            MultivaluedHashMap<String, String> matrixParams = new MultivaluedHashMap<String, String>(0);
            String path;
            if (segment.contains(";")) {
                StringTokenizer split = new StringTokenizer(segment, ";");
                path = segment.startsWith(";") ? "" : split.nextToken();
                while (split.hasMoreTokens()) {
                    String[] param = split.nextToken()
                                          .split("=");
                    matrixParams.computeIfAbsent(param[0],
                                                 key -> new ArrayList<>(1))
                                .add(param[1]);
                }
            } else {
                path = segment;
            }
            selection.add(new PathSegment() {

                @Override
                public MultivaluedMap<String, String> getMatrixParameters() {
                    return matrixParams;
                }

                @Override
                public String getPath() {
                    return path;
                }

                @Override
                public String toString() {
                    return String.format("%s [%s]", path, matrixParams);
                }
            });
        }

        return selection;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> traverse(RuleForm instance,
                                                                                                                                              List<String> selection,
                                                                                                                                              Facet<RuleForm, Network> node,
                                                                                                                                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                                                              Model readOnlyModel) {
        if (selection == null || selection.isEmpty()) {
            return node.toCompactInstance(instance, readOnlyModel, uriInfo);
        } else {
            Map<String, Object> object = node.getShort(instance, uriInfo);
            for (String query : selection) {
                List<PathSegment> traversal = selectFrom(query);
                MultivaluedMap<String, String> parameters = traversal.get(0)
                                                                     .getMatrixParameters();
                if (!traversal.get(0)
                              .getPath()
                              .isEmpty()) {
                    object.put(traversal.get(0)
                                        .getPath(),
                               getProperty(node, instance, traversal,
                                           networkedModel, uriInfo,
                                           readOnlyModel));
                } else {
                    additionalAttributes(instance, node, networkedModel, object,
                                         parameters);
                }
            }
            return object;
        }
    }
}
