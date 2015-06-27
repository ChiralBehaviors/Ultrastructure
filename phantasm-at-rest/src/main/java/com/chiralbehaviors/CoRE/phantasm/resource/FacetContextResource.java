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

package com.chiralbehaviors.CoRE.phantasm.resource;

import java.net.URI;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/context")
public class FacetContextResource extends TransactionalResource {

    private static final String CLASSIFICATION = "classification";
    private static final String CLASSIFIER     = "classifier";
    private static final String CONTEXT        = "@context";
    private static final String ID             = "@id";
    private static final String TYPE           = "@type";

    @Context
    private UriInfo uriInfo;

    public FacetContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetContextResource(EntityManagerFactory emf, UriInfo uriInfo) {
        super(emf);
        this.uriInfo = uriInfo;
    }

    @Produces({ "application/json", "text/json" })
    @Path("agency/{classifier}/{classification}")
    @GET
    public JsonNode getAgencyContext(@PathParam(CLASSIFIER) String relationship,
                                     @PathParam(CLASSIFICATION) String ruleform) {
        AgencyModel agencyModel = readOnlyModel.getAgencyModel();
        Aspect<Agency> aspect = getAspect(relationship, ruleform, agencyModel);
        return buildContext(aspect, agencyModel,
                            Agency.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("attribute/{classifier}/{classification}")
    @GET
    public JsonNode getAttributeContext(@PathParam(CLASSIFIER) String relationship,
                                        @PathParam(CLASSIFICATION) String ruleform) {
        return buildContext(relationship, ruleform,
                            readOnlyModel.getAttributeModel(),
                            Attribute.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("interval/{classifier}/{classification}")
    @GET
    public JsonNode getIntervalContext(@PathParam(CLASSIFIER) String relationship,
                                       @PathParam(CLASSIFICATION) String ruleform) {
        return buildContext(relationship, ruleform,
                            readOnlyModel.getIntervalModel(),
                            Interval.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("location/{classifier}/{classification}")
    @GET
    public JsonNode getLocationContext(@PathParam(CLASSIFIER) String relationship,
                                       @PathParam(CLASSIFICATION) String ruleform) {
        LocationModel locationModel = readOnlyModel.getLocationModel();
        Aspect<Location> aspect = getAspect(relationship, ruleform,
                                            locationModel);
        return buildContext(aspect, locationModel,
                            Location.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProductContext(@PathParam(CLASSIFIER) String relationship,
                                      @PathParam(CLASSIFICATION) String ruleform) {
        ProductModel productModel = readOnlyModel.getProductModel();
        Aspect<Product> aspect = getAspect(relationship, ruleform,
                                           productModel);
        return buildContext(aspect, productModel,
                            Product.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("relationship/{classifier}/{classification}")
    @GET
    public JsonNode getRelationshipContext(@PathParam(CLASSIFIER) String relationship,
                                           @PathParam(CLASSIFICATION) String ruleform) {
        RelationshipModel relationshipModel = readOnlyModel.getRelationshipModel();
        Aspect<Relationship> aspect = getAspect(relationship, ruleform,
                                                relationshipModel);
        return buildContext(aspect, relationshipModel,
                            Relationship.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("statusCode/{classifier}/{classification}")
    @GET
    public JsonNode getStatusCodeContext(@PathParam(CLASSIFIER) String relationship,
                                         @PathParam(CLASSIFICATION) String ruleform) {
        return buildContext(relationship, ruleform,
                            readOnlyModel.getStatusCodeModel(),
                            StatusCode.class.getSimpleName().toLowerCase());
    }

    @Produces({ "application/json", "text/json" })
    @Path("unit/{classifier}/{classification}")
    @GET
    public JsonNode getUnitContext(@PathParam(CLASSIFIER) String relationship,
                                   @PathParam(CLASSIFICATION) String ruleform) {
        return buildContext(relationship, ruleform,
                            readOnlyModel.getUnitModel(),
                            Unit.class.getSimpleName().toLowerCase());
    }

    public UUID toUuid(String ruleform) {
        UUID classification;
        try {
            classification = UUID.fromString(ruleform);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return classification;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addAttributeTerms(ObjectNode context,
                                                                                       Aspect<RuleForm> aspect,
                                                                                       NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(aspect)) {
            String iri = iriFrom(auth.getAuthorizedAttribute());
            String type = typeFrom(auth.getAuthorizedAttribute());
            String term = auth.getAuthorizedAttribute().getName();
            if (type == null) {
                context.put(term, iri);
            } else {
                ObjectNode termDefinition = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
                termDefinition.put(ID, iri);
                termDefinition.put(TYPE, type);
                context.set(term, termDefinition);
            }
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addNetworkAuthTerms(ObjectNode context,
                                                                                         Aspect<RuleForm> aspect,
                                                                                         NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                         String eeType) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(aspect)) {
            Aspect<RuleForm> childAspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                auth.getAuthorizedParent());
            if (auth.getName() != null) {
                ObjectNode termDefinition = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
                termDefinition.put(ID, getTypeIri(eeType, childAspect));
                termDefinition.put(TYPE, ID);
                termDefinition.put(CLASSIFIER,
                                   childAspect.getClassifier().getName());
                termDefinition.put(CLASSIFICATION,
                                   childAspect.getClassification().getName());
                context.set(auth.getName(), termDefinition);
            }
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addXdomainAuthTerms(ObjectNode context,
                                                                                         Aspect<RuleForm> aspect,
                                                                                         NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                         String eeType) {

    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> JsonNode buildContext(Aspect<RuleForm> aspect,
                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                      String eeType) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        ObjectNode context = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set(CONTEXT, context);
        addAttributeTerms(context, aspect, networkedModel);
        addNetworkAuthTerms(context, aspect, networkedModel, eeType);
        addXdomainAuthTerms(context, aspect, networkedModel, eeType);
        return container;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> JsonNode buildContext(String relationship,
                                                                                      String ruleform,
                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                      String eeType) {
        return buildContext(getAspect(relationship, ruleform, networkedModel),
                            networkedModel, eeType);

    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Aspect<RuleForm> getAspect(String relationship,
                                                                                           String ruleform,
                                                                                           NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {

        UUID classifier = toUuid(relationship);
        UUID classification = toUuid(ruleform);
        try {
            return networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    private String getTypeIri(String eeType, Aspect<?> aspect) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = aspect.getClassifier().getId().toString();
        String classification = aspect.getClassification().getId().toString();

        URI userUri = ub.path(FacetContextResource.class).path(classifier).path(classification).build();
        return userUri.toASCIIString();
    }

    private String iriFrom(Attribute authorizedAttribute) {
        AttributeValue<Attribute> iri = readOnlyModel.getAttributeModel().getAttributeValue(authorizedAttribute,
                                                                                            readOnlyModel.getKernel().getIRI());
        if (iri != null) {
            return iri.getTextValue();
        }
        switch (authorizedAttribute.getValueType()) {
            case TEXT:
                return "http://schema.org/text";
            case BINARY:
                return "http://schema.org/binary";
            case BOOLEAN:
                return "http://schema.org/boolean";
            case INTEGER:
                return "http://schema.org/integer";
            case NUMERIC:
                return "http://schema.org/numeric";
            case TIMESTAMP:
                return "http://schema.org/timestamp";
        }
        return null;
    }

    /**
     * @param authorizedAttribute
     * @return
     */
    private String typeFrom(Attribute authorizedAttribute) {
        AttributeValue<Attribute> irl = readOnlyModel.getAttributeModel().getAttributeValue(authorizedAttribute,
                                                                                            readOnlyModel.getKernel().getIRI());
        return irl != null ? irl.getTextValue() : null;
    }
}
