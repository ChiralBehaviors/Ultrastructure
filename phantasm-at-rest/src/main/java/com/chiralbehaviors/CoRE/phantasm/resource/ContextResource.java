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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/context")
public class ContextResource extends TransactionalResource {

    private static final String CLASSIFICATION                      = "classification";
    private static final String CLASSIFIER                          = "classifier";
    private static final String CONTEXT                             = "@context";
    private static final String HTTP_ULTRASTRUCTURE_ME_SCHEMA_FACET = "http://ultrastructure.me/schema/facet";
    private static final String ID                                  = "@id";
    private static final String TYPE                                = "@type";

    @Context
    private UriInfo uriInfo;

    public ContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    public ContextResource(EntityManagerFactory emf, UriInfo uriInfo) {
        super(emf);
        this.uriInfo = uriInfo;
    }

    @Produces({ "application/json", "text/json" })
    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProductContext(@PathParam(CLASSIFIER) String relationship,
                                      @PathParam(CLASSIFICATION) String ruleform) {
        UUID classifier;
        try {
            classifier = UUID.fromString(relationship);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        UUID classification;
        try {
            classification = UUID.fromString(ruleform);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        Aspect<Product> aspect;
        NetworkedModel<Product, ?, ?, ?> networkedModel = readOnlyModel.getProductModel();
        try {
            aspect = networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }

        return buildContext(aspect, networkedModel);
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
                termDefinition.put(TYPE, iri);
                context.set(term, termDefinition);
            }
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addNetworkAuthTerms(Aspect<RuleForm> aspect,
                                                                                         NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                         ObjectNode context) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(aspect)) {
            Aspect<RuleForm> childAspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                auth.getAuthorizedParent());
            if (auth.getName() != null) {
                ObjectNode termDefinition = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
                termDefinition.put(ID, HTTP_ULTRASTRUCTURE_ME_SCHEMA_FACET);
                termDefinition.put(TYPE,
                                   getTypeIri(Product.class.getSimpleName().toLowerCase(),
                                              childAspect));
                termDefinition.put(CLASSIFIER,
                                   childAspect.getClassifier().getName());
                termDefinition.put(CLASSIFICATION,
                                   childAspect.getClassification().getName());
                context.set(auth.getName(), termDefinition);
            }
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> JsonNode buildContext(Aspect<RuleForm> aspect,
                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        ObjectNode context = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set(CONTEXT, context);
        addAttributeTerms(context, aspect, networkedModel);
        addNetworkAuthTerms(aspect, networkedModel, context);
        return container;
    }

    /**
     * @param eeType
     * @param aspect
     * @return
     */
    private String getTypeIri(String eeType, Aspect<?> aspect) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        URI userUri = ub.path(ContextResource.class).path(aspect.getClassifier().getId().toString()).path(aspect.getClassification().getId().toString()).build();
        return userUri.toASCIIString();
    }

    /**
     * @param authorizedAttribute
     * @return
     */
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
