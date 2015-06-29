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
import java.net.URI;
import java.util.List;

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
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * @author hhildebrand
 *
 */
public class FacetNode<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Aspect<RuleForm>implements JsonSerializable {
    private final RuleForm existential;
    private final Model    model;
    private final UriInfo  uriInfo;

    public FacetNode(RuleForm existential, Aspect<RuleForm> aspect, Model model,
                     UriInfo uriInfo) {
        this(existential, aspect.getClassifier(), aspect.getClassification(),
             model, uriInfo);
    }

    /**
     * @param classifier
     * @param classification
     */
    public FacetNode(RuleForm existential, Relationship classifier,
                     RuleForm classification, Model model, UriInfo uriInfo) {
        super(classifier, classification);
        this.existential = existential;
        this.model = model;
        this.uriInfo = uriInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FacetNode && super.equals(obj);
    }

    public FacetContext<RuleForm, Network> getContext() {
        return new FacetContext<>(getClassifier(), getClassification(), model,
                                  uriInfo);
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serialize(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        writeValue(gen);
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

    public void writeValue(JsonGenerator gen) throws IOException {
        gen.writeStringField(Constants.CONTEXT, getContextIri());
        gen.writeStringField(Constants.ID, getIri());
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        writeAttributes(networkedModel, gen);
        writeNetworkAuths(networkedModel, gen);
        writeXdAuths(networkedModel, gen);
    }

    /**
     * @return
     */
    private String getContextIri() {
        String eeType = getClassification().getClass().getSimpleName().toLowerCase();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = getClassifier().getId().toString();
        String classification = getClassification().getId().toString();
        URI userUri = ub.path(FacetContextResource.class).path(eeType).path(classifier).path(classification).build();
        return userUri.toASCIIString();
    }

    private String getIri() {
        return getIri(existential, this);
    }

    private String getIri(ExistentialRuleform<?, ?> child, Aspect<?> aspect) {
        String eeType = getClassification().getClass().getSimpleName().toLowerCase();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = getClassifier().getId().toString();
        String classification = getClassification().getId().toString();
        URI userUri = ub.path(FacetNodeResource.class).path(eeType).path(child.getId().toString()).path(classifier).path(classification).build();
        return userUri.toASCIIString();
    }

    @SuppressWarnings("unchecked")
    private void writeAgencyAuths(JsonGenerator gen) throws IOException {
        AgencyModel agencyModel = model.getAgencyModel();
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths((Aspect<Agency>) this)) {
            writeTo(auth, gen);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths((Aspect<Agency>) this)) {
            writeTo(auth, gen);
        }
    }

    /**
     * @param networkedModel2
     * @param gen
     * @throws IOException
     */
    private void writeAttributes(NetworkedModel<RuleForm, Network, ?, ?> networkedModel2,
                                 JsonGenerator gen) throws IOException {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            gen.writeStringField(auth.getAuthorizedAttribute().getName(),
                                 networkedModel.getAttributeValue(existential,
                                                                  auth.getAuthorizedAttribute()).toString());
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeFrom(AgencyLocationAuthorization auth,
                           JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> {
                                                     try {
                                                         gen.writeString(getIri(child,
                                                                                new Aspect<Agency>(auth.getFromRelationship(),
                                                                                                   auth.getFromParent())));
                                                     } catch (Exception e) {
                                                         throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                       e));
                                                     }
                                                 });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Agency> children = networkedModel.getAuthorizedAgencies(existential,
                                                                         auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Agency>(auth.getFromRelationship(),
                                                               auth.getFromParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeFrom(AgencyProductAuthorization auth,
                           JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> {
                                                     try {
                                                         gen.writeString(getIri(child,
                                                                                new Aspect<Agency>(auth.getFromRelationship(),
                                                                                                   auth.getFromParent())));
                                                     } catch (Exception e) {
                                                         throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                       e));
                                                     }
                                                 });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Agency> children = networkedModel.getAuthorizedAgencies(existential,
                                                                         auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Agency>(auth.getFromRelationship(),
                                                               auth.getFromParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeFrom(ProductLocationAuthorization auth,
                           JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> {
                                                     try {
                                                         gen.writeString(getIri(child,
                                                                                new Aspect<Product>(auth.getFromRelationship(),
                                                                                                    auth.getFromParent())));
                                                     } catch (Exception e) {
                                                         throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                       e));
                                                     }
                                                 });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Product> children = networkedModel.getAuthorizedProducts(existential,
                                                                          auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Product>(auth.getFromRelationship(),
                                                                auth.getFromParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeFrom(ProductRelationshipAuthorization auth,
                           JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> {
                                                     try {
                                                         gen.writeString(getIri(child,
                                                                                new Aspect<Product>(auth.getFromRelationship(),
                                                                                                    auth.getFromParent())));
                                                     } catch (Exception e) {
                                                         throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                       e));
                                                     }
                                                 });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Product> children = networkedModel.getAuthorizedProducts(existential,
                                                                          auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Product>(auth.getFromRelationship(),
                                                                auth.getFromParent())));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeLocationAuths(JsonGenerator gen) throws IOException {
        for (AgencyLocationAuthorization auth : model.getLocationModel().getLocationAgencyAuths((Aspect<Location>) this)) {
            writeFrom(auth, gen);
        }
        for (ProductLocationAuthorization auth : model.getLocationModel().getLocationProductAuths((Aspect<Location>) this)) {
            writeFrom(auth, gen);
        }
    }

    /**
     * @param networkedModel
     * @param gen
     * @throws IOException
     */
    private void writeNetworkAuths(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                                   JsonGenerator gen) throws IOException {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            if (auth.getName() == null) {
                continue;
            }
            if (auth.getCardinality() == Cardinality.N) {
                gen.writeArrayFieldStart(auth.getName());
                networkedModel.getChildren(existential,
                                           auth.getChildRelationship()).forEach(child -> {
                                               try {
                                                   gen.writeString(getIri(child,
                                                                          new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                                               auth.getAuthorizedParent())));
                                               } catch (Exception e) {
                                                   throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                 e));
                                               }
                                           });
                gen.writeEndArray();
            } else if (auth.getCardinality() == Cardinality.ONE) {
                List<RuleForm> children = networkedModel.getImmediateChildren(existential,
                                                                              auth.getChildRelationship());
                if (!children.isEmpty()) {
                    gen.writeStringField(auth.getName(),
                                         getIri(children.get(0),
                                                new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                                     auth.getAuthorizedParent())));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeProductAuths(JsonGenerator gen) throws IOException {
        for (AgencyProductAuthorization auth : model.getProductModel().getProductAgencyAuths((Aspect<Product>) this)) {
            writeFrom(auth, gen);
        }
        for (ProductLocationAuthorization auth : model.getProductModel().getProductLocationAuths((Aspect<Product>) this)) {
            writeTo(auth, gen);
        }
        for (ProductRelationshipAuthorization auth : model.getProductModel().getProductRelationshipAuths((Aspect<Product>) this)) {
            writeTo(auth, gen);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeRelationshipAuths(JsonGenerator gen) throws IOException {
        for (ProductRelationshipAuthorization auth : model.getRelationshipModel().getRelationshipProductAuths((Aspect<Relationship>) this)) {
            writeFrom(auth, gen);
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeTo(AgencyLocationAuthorization auth,
                         JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> {
                                                      try {
                                                          gen.writeString(getIri(child,
                                                                                 new Aspect<Location>(auth.getToRelationship(),
                                                                                                      auth.getToParent())));
                                                      } catch (Exception e) {
                                                          throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                        e));
                                                      }
                                                  });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Location> children = networkedModel.getAuthorizedLocations(existential,
                                                                            auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Location>(auth.getToRelationship(),
                                                                 auth.getToParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeTo(AgencyProductAuthorization auth,
                         JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> {
                                                     try {
                                                         gen.writeString(getIri(child,
                                                                                new Aspect<Product>(auth.getToRelationship(),
                                                                                                    auth.getToParent())));
                                                     } catch (Exception e) {
                                                         throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                       e));
                                                     }
                                                 });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Product> children = networkedModel.getAuthorizedProducts(existential,
                                                                          auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Product>(auth.getToRelationship(),
                                                                auth.getToParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeTo(ProductLocationAuthorization auth,
                         JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> {
                                                      try {
                                                          gen.writeString(getIri(child,
                                                                                 new Aspect<Location>(auth.getToRelationship(),
                                                                                                      auth.getToParent())));
                                                      } catch (Exception e) {
                                                          throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                        e));
                                                      }
                                                  });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Location> children = networkedModel.getAuthorizedLocations(existential,
                                                                            auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Location>(auth.getToRelationship(),
                                                                 auth.getToParent())));
            }
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeTo(ProductRelationshipAuthorization auth,
                         JsonGenerator gen) throws IOException {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        if (auth.getCardinality() == Cardinality.N) {
            gen.writeArrayFieldStart(auth.getName());
            networkedModel.getAuthorizedRelationships(existential,
                                                      auth.getConnection()).forEach(child -> {
                                                          try {
                                                              gen.writeString(getIri(child,
                                                                                     new Aspect<Relationship>(auth.getToRelationship(),
                                                                                                              auth.getToParent())));
                                                          } catch (Exception e) {
                                                              throw new IllegalStateException(String.format("Error writing facet %s",
                                                                                                            e));
                                                          }
                                                      });
            gen.writeEndArray();
        } else if (auth.getCardinality() == Cardinality.ONE) {
            List<Relationship> children = networkedModel.getAuthorizedRelationships(existential,
                                                                                    auth.getConnection());
            if (!children.isEmpty()) {
                gen.writeStringField(auth.getName(),
                                     getIri(children.get(0),
                                            new Aspect<Relationship>(auth.getToRelationship(),
                                                                     auth.getToParent())));
            }
        }
    }

    /**
     * @param networkedModel
     * @param gen
     * @throws IOException
     */
    private void writeXdAuths(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                              JsonGenerator gen) throws IOException {
        if (getClassification() instanceof Agency) {
            writeAgencyAuths(gen);
        } else if (getClassification() instanceof Product) {
            writeProductAuths(gen);
        } else if (getClassification() instanceof Location) {
            writeLocationAuths(gen);
        } else if (getClassification() instanceof Relationship) {
            writeRelationshipAuths(gen);
        }
    }
}
