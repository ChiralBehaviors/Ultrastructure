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

import java.io.IOException;

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
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetContextResource;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetNodeResource;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * @author hhildebrand
 *
 */
@JsonSerialize()
public class FacetContext<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Aspect<RuleForm>implements JsonSerializable {

    public static String getContextIri(Aspect<?> aspect, UriInfo uriInfo) {
        String eeType = aspect.getClassification().getClass().getSimpleName();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = aspect.getClassifier().getId().toString();
        String classification = aspect.getClassification().getId().toString();
        ub.path(FacetContextResource.class).path(eeType).path(classifier).path(classification);
        ub.fragment(String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    public static String getNodeIri(ExistentialRuleform<?, ?> child,
                                    Aspect<?> aspect, UriInfo uriInfo) {
        String eeType = aspect.getClassification().getClass().getSimpleName();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = aspect.getClassifier().getId().toString();
        String classification = aspect.getClassification().getId().toString();
        ub.path(FacetNodeResource.class).path(eeType).path(classifier).path(classification).path(child.getId().toString());
        ub.fragment(String.format("%s:%s:%s", child.getName(),
                                  aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    private final Model   model;
    private final UriInfo uriInfo;

    private final boolean writeId;

    public FacetContext(Aspect<RuleForm> aspect, Model model, UriInfo uriInfo) {
        this(aspect.getClassifier(), aspect.getClassification(), model, uriInfo,
             false);
    }

    public FacetContext(Aspect<RuleForm> aspect, Model model, UriInfo uriInfo,
                        boolean writeId) {
        this(aspect.getClassifier(), aspect.getClassification(), model, uriInfo,
             writeId);
    }

    public FacetContext(Relationship classifier, RuleForm classification,
                        Model model, UriInfo uriInfo, boolean writeId) {
        super(classifier, classification);
        this.uriInfo = uriInfo;
        this.model = model;
        this.writeId = writeId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FacetContext && super.equals(obj);
    }

    /**
     * @return
     */
    public String getAllInstancesIri() {
        String eeType = getClassification().getClass().getSimpleName();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = getClassifier().getId().toString();
        String classification = getClassification().getId().toString();
        ub.path(FacetNodeResource.class).path(eeType).path(classifier).path(classification);
        return ub.build().toASCIIString();
    }

    public String getIri() {
        return getContextIri(this, uriInfo);
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serialize(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        if (writeId) {
            gen.writeStringField(Constants.ID, getIri());
        }
        gen.writeObjectFieldStart(Constants.CONTEXT);
        writeContext(gen);
        gen.writeEndObject();
        gen.writeEndObject();
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serializeWithType(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider, com.fasterxml.jackson.databind.jsontype.TypeSerializer)
     */
    @Override
    public void serializeWithType(JsonGenerator gen,
                                  SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }

    public void writeContext(JsonGenerator gen) throws IOException {
        writeRuleformTerms(gen);
        writeAttributeTerms(gen);
        writeNetworkAuthTerms(gen);
        writeXdAuthTerms(gen);
    }

    /**
     * @param authorizedAttribute
     * @return
     */
    private String typeFrom(Attribute authorizedAttribute) {
        AttributeValue<Attribute> type = model.getAttributeModel().getAttributeValue(authorizedAttribute,
                                                                                     model.getKernel().getJsonldType());
        return type != null ? type.getTextValue() : null;
    }

    @SuppressWarnings("unchecked")
    private void writeAgencyAuthTerms(JsonGenerator gen) throws IOException {
        AgencyModel agencyModel = model.getAgencyModel();
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths((Aspect<Agency>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Location>(auth.getToRelationship(),
                                                 auth.getToParent()),
                      gen);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths((Aspect<Agency>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Product>(auth.getToRelationship(),
                                                auth.getToParent()),
                      gen);
        }
    }

    private void writeAttributeTerms(JsonGenerator gen) throws IOException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(getClassification());
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            String iri = RuleformNode.getIri(auth.getAuthorizedAttribute(),
                                             uriInfo);
            String type = typeFrom(auth.getAuthorizedAttribute());
            String term = auth.getAuthorizedAttribute().getName();
            if (type == null) {
                gen.writeStringField(term, iri);
            } else {
                gen.writeObjectFieldStart(term);
                gen.writeStringField(Constants.ID, iri);
                gen.writeStringField(Constants.TYPE, type);
                gen.writeEndObject();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeLocationAuthTerms(JsonGenerator gen) throws IOException {
        for (AgencyLocationAuthorization auth : model.getLocationModel().getLocationAgencyAuths((Aspect<Location>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Agency>(auth.getFromRelationship(),
                                               auth.getFromParent()),
                      gen);
        }
        for (ProductLocationAuthorization auth : model.getLocationModel().getLocationProductAuths((Aspect<Location>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Product>(auth.getFromRelationship(),
                                                auth.getFromParent()),
                      gen);
        }
    }

    private void writeNetworkAuthTerms(JsonGenerator gen) throws IOException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(getClassification());
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            Aspect<RuleForm> childAspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                auth.getAuthorizedParent());
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, childAspect, gen);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeProductAuthTerms(JsonGenerator gen) throws IOException {
        for (AgencyProductAuthorization auth : model.getProductModel().getProductAgencyAuths((Aspect<Product>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Agency>(auth.getFromRelationship(),
                                               auth.getFromParent()),
                      gen);
        }
        for (ProductLocationAuthorization auth : model.getProductModel().getProductLocationAuths((Aspect<Product>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Location>(auth.getToRelationship(),
                                                 auth.getToParent()),
                      gen);
        }
        for (ProductRelationshipAuthorization auth : model.getProductModel().getProductRelationshipAuths((Aspect<Product>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Relationship>(auth.getToRelationship(),
                                                     auth.getToParent()),
                      gen);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeRelationshipAuthTerms(JsonGenerator gen) throws IOException {
        for (ProductRelationshipAuthorization auth : model.getRelationshipModel().getRelationshipProductAuths((Aspect<Relationship>) this)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            writeTerm(term, new Aspect<Relationship>(auth.getToRelationship(),
                                                     auth.getToParent()),
                      gen);
        }
    }

    /**
     * @param gen
     * @throws IOException
     */
    private void writeRuleformTerms(JsonGenerator gen) throws IOException {
        gen.writeStringField("name",
                             String.format("%s#name",
                                           RuleformContext.getContextIri(getClassification().getClass(),
                                                                         uriInfo)));
        gen.writeStringField("description",
                             String.format("%s#description",
                                           RuleformContext.getContextIri(getClassification().getClass(),
                                                                         uriInfo)));
        gen.writeStringField("notes",
                             String.format("%s#notes",
                                           RuleformContext.getContextIri(getClassification().getClass(),
                                                                         uriInfo)));
        gen.writeStringField("updatedBy",
                             String.format("%s#updatedBy",
                                           RuleformContext.getContextIri(Agency.class,
                                                                         uriInfo)));
    }

    private void writeTerm(String term, Aspect<?> childAspect,
                           JsonGenerator gen) throws IOException {
        if (term == null) {
            return;
        }
        gen.writeObjectFieldStart(term);
        if (childAspect.getClassifier().isAny()
            && childAspect.getClassification().isAny()) {
            gen.writeStringField(Constants.ID,
                                 RuleformContext.getContextIri(childAspect.getClassification().getClass(),
                                                               uriInfo));
        } else {
            gen.writeStringField(Constants.ID,
                                 getContextIri(childAspect, uriInfo));
        }
        gen.writeStringField(Constants.TYPE, Constants.ID);
        gen.writeEndObject();
    }

    /**
     * @param gen
     * @throws IOException
     */
    private void writeXdAuthTerms(JsonGenerator gen) throws IOException {
        if (getClassification() instanceof Agency) {
            writeAgencyAuthTerms(gen);
        } else if (getClassification() instanceof Product) {
            writeProductAuthTerms(gen);
        } else if (getClassification() instanceof Location) {
            writeLocationAuthTerms(gen);
        } else if (getClassification() instanceof Relationship) {
            writeRelationshipAuthTerms(gen);
        }
    }
}
