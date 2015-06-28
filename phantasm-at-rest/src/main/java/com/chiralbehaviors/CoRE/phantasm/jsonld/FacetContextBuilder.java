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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class FacetContextBuilder {

    private final Model readOnlyModel;

    public FacetContextBuilder(Model readOnlyModel) {
        this.readOnlyModel = readOnlyModel;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildContainer(Aspect<RuleForm> aspect,
                                                                                                                                        UriInfo uriInfo) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set(Constants.CONTEXT, buildContext(aspect, uriInfo));
        return container;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildContext(Aspect<RuleForm> aspect,
                                                                                                                                      UriInfo uriInfo) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        ObjectNode context = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        addAttributeTerms(context, aspect, networkedModel);
        addNetworkAuthTerms(context, aspect, networkedModel,
                            aspect.getClassification().getClass().getSimpleName().toLowerCase(),
                            uriInfo);
        addXdomainAuthTerms(context, aspect, uriInfo);
        return context;
    }

    private void addAgencyAuthTerms(ObjectNode context, Aspect<Agency> aspect,
                                    UriInfo uriInfo) {
        AgencyModel agencyModel = readOnlyModel.getAgencyModel();
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Location>(auth.getToRelationship(),
                                                                   auth.getToParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Product>(auth.getToRelationship(),
                                                                  auth.getToParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
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
                termDefinition.put(Constants.ID, iri);
                termDefinition.put(Constants.TYPE, type);
                context.set(term, termDefinition);
            }
        }
    }

    private void addLocationAuthTerms(ObjectNode context,
                                      Aspect<Location> aspect,
                                      UriInfo uriInfo) {
        for (AgencyLocationAuthorization auth : readOnlyModel.getLocationModel().getLocationAgencyAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Agency>(auth.getFromRelationship(),
                                                                 auth.getFromParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
        for (ProductLocationAuthorization auth : readOnlyModel.getLocationModel().getLocationProductAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Product>(auth.getFromRelationship(),
                                                                  auth.getFromParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addNetworkAuthTerms(ObjectNode context,
                                                                                         Aspect<RuleForm> aspect,
                                                                                         NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                         String eeType,
                                                                                         UriInfo uriInfo) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(aspect)) {
            Aspect<RuleForm> childAspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                auth.getAuthorizedParent());
            if (auth.getName() != null) {
                ObjectNode termDefinition = getTermDefinition(childAspect,
                                                              eeType, uriInfo);
                context.set(auth.getName(), termDefinition);
            }
        }
    }

    private void addProductAuthTerms(ObjectNode context, Aspect<Product> aspect,
                                     UriInfo uriInfo) {
        for (AgencyProductAuthorization auth : readOnlyModel.getProductModel().getProductAgencyAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Agency>(auth.getFromRelationship(),
                                                                 auth.getFromParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
        for (ProductLocationAuthorization auth : readOnlyModel.getProductModel().getProductLocationAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Location>(auth.getToRelationship(),
                                                                   auth.getToParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
        for (ProductRelationshipAuthorization auth : readOnlyModel.getProductModel().getProductRelationshipAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Relationship>(auth.getToRelationship(),
                                                                       auth.getToParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
        for (ProductRelationshipAuthorization auth : readOnlyModel.getProductModel().getProductRelationshipAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Relationship>(auth.getToRelationship(),
                                                                       auth.getToParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
    }

    private void addRelationshipAuthTerms(ObjectNode context,
                                          Aspect<Relationship> aspect,
                                          UriInfo uriInfo) {
        for (ProductRelationshipAuthorization auth : readOnlyModel.getRelationshipModel().getRelationshipProductAuths(aspect)) {
            if (auth.getName() != null) {
                context.set(auth.getName(),
                            getTermDefinition(new Aspect<Product>(auth.getFromRelationship(),
                                                                  auth.getFromParent()),
                                              Constants.TYPE, uriInfo));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> void addXdomainAuthTerms(ObjectNode context,
                                                                                         Aspect<RuleForm> aspect,
                                                                                         UriInfo uriInfo) {
        if (aspect.getClassification() instanceof Agency) {
            addAgencyAuthTerms(context, (Aspect<Agency>) aspect, uriInfo);
        } else if (aspect.getClassification() instanceof Product) {
            addProductAuthTerms(context, (Aspect<Product>) aspect, uriInfo);
        } else if (aspect.getClassification() instanceof Location) {
            addLocationAuthTerms(context, (Aspect<Location>) aspect, uriInfo);
        } else if (aspect.getClassification() instanceof Relationship) {
            addRelationshipAuthTerms(context, (Aspect<Relationship>) aspect,
                                     uriInfo);
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> ObjectNode getTermDefinition(Aspect<RuleForm> aspect,
                                                                                             String eeType,
                                                                                             UriInfo uriInfo) {
        ObjectNode termDefinition = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        termDefinition.put(Constants.ID, getTypeIri(eeType, aspect, uriInfo));
        termDefinition.put(Constants.TYPE, Constants.ID);
        termDefinition.put(Constants.CLASSIFIER,
                           aspect.getClassifier().getName());
        termDefinition.put(Constants.CLASSIFICATION,
                           aspect.getClassification().getName());
        return termDefinition;
    }

    private String getTypeIri(String eeType, Aspect<?> aspect,
                              UriInfo uriInfo) {
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
