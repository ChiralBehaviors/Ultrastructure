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
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetResource;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.utils.English;
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
    public static String getTermIri(Aspect<?> aspect, String term,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getTerm", String.class,
                                                  String.class, String.class,
                                                  String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getFacetType method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("term", term);
        return ub.build().toASCIIString();
    }

    public static String getContextIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getContext", String.class,
                                                  String.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getFacetContext method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.fragment(String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    public static String getTypeIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getType", String.class,
                                                  String.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getFacetType method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.fragment(String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    public static String getNodeIri(ExistentialRuleform<?, ?> child,
                                    Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getInstance", String.class,
                                                  String.class, String.class,
                                                  String.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getInstance method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("instance", child.getId().toString());
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
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getAllInstances",
                                                  String.class, String.class,
                                                  String.class));
            ub.resolveTemplate("ruleform-type",
                               getClassification().getClass().getSimpleName());
            ub.resolveTemplate("classifier",
                               getClassifier().getId().toString());
            ub.resolveTemplate("classification",
                               getClassification().getId().toString());
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build().toASCIIString();
    }

    public String getIri() {
        return getTypeIri(this, uriInfo);
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
            String term = auth.getAuthorizedAttribute().getName();
            gen.writeObjectFieldStart(term);
            gen.writeStringField(Constants.ID,
                                 getTermIri(this,
                                            auth.getAuthorizedAttribute().getName(),
                                            uriInfo));
            gen.writeStringField(Constants.TYPE,
                                 RuleformNode.getIri(auth.getAuthorizedAttribute(),
                                                     uriInfo));
            gen.writeEndObject();
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
        gen.writeStringField("name", getTermIri(this, "name", uriInfo));
        gen.writeStringField("description",
                             getTermIri(this, "description", uriInfo));
        gen.writeStringField("notes", getTermIri(this, "notes", uriInfo));
        gen.writeStringField("updatedBy",
                             getTermIri(this, "updatedBy", uriInfo));
    }

    private void writeTerm(String term, Aspect<?> childAspect,
                           JsonGenerator gen) throws IOException {
        if (term == null) {
            return;
        }
        gen.writeObjectFieldStart(term);
        gen.writeStringField(Constants.ID,
                             FacetContext.getTermIri(this, term, uriInfo));
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
