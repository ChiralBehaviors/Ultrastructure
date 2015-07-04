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
import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
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
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.utils.English;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class FacetNode<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Aspect<RuleForm> {
    private final RuleForm existential;
    private final Model    model;

    private final UriInfo uriInfo;

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
        return new FacetContext<RuleForm, Network>(this, model, uriInfo);
    }

    public String getIri() {
        return FacetContext.getNodeIri(existential, this, uriInfo);
    }

    public ObjectNode toNode() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        writeValue(node);
        return node;
    }

    public void writeRuleformAttributes(ObjectNode node) {
        if (existential.getName() != null) {
            node.put("name", existential.getName());
        }
        if (existential.getDescription() != null) {
            node.put("description", existential.getDescription());
        }
        if (existential.getNotes() != null) {
            node.put("notes", existential.getNotes());
        }
        node.put("updated-by",
                 RuleformNode.getIri(existential.getUpdatedBy(), uriInfo));
    }

    @SuppressWarnings("unchecked")
    private void writeAgencyAuths(ObjectNode node) {
        AgencyModel agencyModel = model.getAgencyModel();
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths((Aspect<Agency>) this)) {
            writeTo(auth, node);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths((Aspect<Agency>) this)) {
            writeTo(auth, node);
        }
    }

    /**
     * @param networkedModel2
     * @param node
     * @throws IOException
     */
    private void writeAttributes(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                                 ObjectNode node) {
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            @SuppressWarnings("unchecked")
            List<AttributeValue<RuleForm>> attributeValues = (List<AttributeValue<RuleForm>>) networkedModel.getAttributeValues(existential,
                                                                                                                                auth.getAuthorizedAttribute());
            if (attributeValues.size() == 1) {
                Object value = networkedModel.getAttributeValue(existential,
                                                                auth.getAuthorizedAttribute()).getValue();
                node.put(auth.getAuthorizedAttribute().getName(),
                         value == null ? null : value.toString());
            } else if (!attributeValues.isEmpty()) {
                ArrayNode array = node.putArray(auth.getAuthorizedAttribute().getName());
                for (AttributeValue<RuleForm> attr : attributeValues) {
                    Object value = attr.getValue();
                    array.add(value == null ? null : value.toString());
                }
            }
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeFrom(AgencyLocationAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Agency> aspect = new Aspect<Agency>(auth.getFromRelationship(),
                                                   auth.getFromParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                 FacetContext.getNodeIri(child,
                                                                                                                         aspect,
                                                                                                                         uriInfo)));
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeFrom(AgencyProductAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Agency> aspect = new Aspect<Agency>(auth.getFromRelationship(),
                                                   auth.getFromParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedAgencies(existential,
                                                 auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                 FacetContext.getNodeIri(child,
                                                                                                                         aspect,
                                                                                                                         uriInfo)));
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeFrom(ProductLocationAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Product> aspect = new Aspect<Product>(auth.getFromRelationship(),
                                                     auth.getFromParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                 FacetContext.getNodeIri(child,
                                                                                                                         aspect,
                                                                                                                         uriInfo)));
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeFrom(ProductRelationshipAuthorization auth,
                           ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Product> aspect = new Aspect<Product>(auth.getFromRelationship(),
                                                     auth.getFromParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                 FacetContext.getNodeIri(child,
                                                                                                                         aspect,
                                                                                                                         uriInfo)));
        }

    }

    @SuppressWarnings("unchecked")
    private void writeLocationAuths(ObjectNode node) {
        for (AgencyLocationAuthorization auth : model.getLocationModel().getLocationAgencyAuths((Aspect<Location>) this)) {
            writeFrom(auth, node);
        }
        for (ProductLocationAuthorization auth : model.getLocationModel().getLocationProductAuths((Aspect<Location>) this)) {
            writeFrom(auth, node);
        }
    }

    /**
     * @param networkedModel
     * @param node
     * @throws IOException
     */
    private void writeNetworkAuths(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                                   ObjectNode node) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            if (auth.getName() == null) {
                continue;
            }
            Aspect<RuleForm> aspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                           auth.getAuthorizedParent());
            if (auth.getCardinality() == Cardinality.N) {
                String term = English.plural(auth.getName());
                ArrayNode array = node.putArray(term);
                networkedModel.getChildren(existential,
                                           auth.getChildRelationship()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                           aspect,
                                                                                                                           uriInfo)));
            } else if (auth.getCardinality() == Cardinality.ONE) {
                networkedModel.getImmediateChildren(existential,
                                                    auth.getChildRelationship()).forEach(child -> node.put(auth.getName(),
                                                                                                           FacetContext.getNodeIri(child,
                                                                                                                                   aspect,
                                                                                                                                   uriInfo)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeProductAuths(ObjectNode node) {
        for (AgencyProductAuthorization auth : model.getProductModel().getProductAgencyAuths((Aspect<Product>) this)) {
            writeFrom(auth, node);
        }
        for (ProductLocationAuthorization auth : model.getProductModel().getProductLocationAuths((Aspect<Product>) this)) {
            writeTo(auth, node);
        }
        for (ProductRelationshipAuthorization auth : model.getProductModel().getProductRelationshipAuths((Aspect<Product>) this)) {
            writeTo(auth, node);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeRelationshipAuths(ObjectNode node) {
        for (ProductRelationshipAuthorization auth : model.getRelationshipModel().getRelationshipProductAuths((Aspect<Relationship>) this)) {
            writeFrom(auth, node);
        }
    }

    /**
     * @param auth
     * @param gen
     * @throws IOException
     */
    private void writeTo(AgencyLocationAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Location> aspect = new Aspect<Location>(auth.getToRelationship(),
                                                       auth.getToParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                           aspect,
                                                                                                                           uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                  FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeTo(AgencyProductAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Product> aspect = new Aspect<Product>(auth.getToRelationship(),
                                                     auth.getToParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedProducts(existential,
                                                 auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                 FacetContext.getNodeIri(child,
                                                                                                                         aspect,
                                                                                                                         uriInfo)));
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeTo(ProductLocationAuthorization auth, ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Location> aspect = new Aspect<Location>(auth.getToRelationship(),
                                                       auth.getToParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                           aspect,
                                                                                                                           uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedLocations(existential,
                                                  auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                  FacetContext.getNodeIri(child,
                                                                                                                          aspect,
                                                                                                                          uriInfo)));
        }
    }

    /**
     * @param auth
     * @param node
     * @throws IOException
     */
    private void writeTo(ProductRelationshipAuthorization auth,
                         ObjectNode node) {
        if (auth.getName() == null) {
            return;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        Aspect<Relationship> aspect = new Aspect<>(auth.getToRelationship(),
                                                   auth.getToParent());
        if (auth.getCardinality() == Cardinality.N) {
            String term = English.plural(auth.getName());
            ArrayNode array = node.putArray(term);
            networkedModel.getAuthorizedRelationships(existential,
                                                      auth.getConnection()).forEach(child -> array.add(FacetContext.getNodeIri(child,
                                                                                                                               aspect,
                                                                                                                               uriInfo)));
        } else if (auth.getCardinality() == Cardinality.ONE) {
            networkedModel.getAuthorizedRelationships(existential,
                                                      auth.getConnection()).forEach(child -> node.put(auth.getName(),
                                                                                                      FacetContext.getNodeIri(child,
                                                                                                                              aspect,
                                                                                                                              uriInfo)));
        }
    }

    private void writeValue(ObjectNode node) {
        node.put(Constants.CONTEXT, FacetContext.getContextIri(this, uriInfo));
        node.put(Constants.ID, getIri());
        writeRuleformAttributes(node);
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(existential);
        writeAttributes(networkedModel, node);
        writeNetworkAuths(networkedModel, node);
        writeXdAuths(networkedModel, node);
    }

    /**
     * @param networkedModel
     * @param node
     * @throws IOException
     */
    private void writeXdAuths(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                              ObjectNode node) {
        if (getClassification() instanceof Agency) {
            writeAgencyAuths(node);
        } else if (getClassification() instanceof Product) {
            writeProductAuths(node);
        } else if (getClassification() instanceof Location) {
            writeLocationAuths(node);
        } else if (getClassification() instanceof Relationship) {
            writeRelationshipAuths(node);
        }
    }
}
