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
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
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

    public FacetNode(RuleForm existential, Aspect<RuleForm> aspect, Model model,
                 UriInfo uriInfo) {
        this(existential, aspect.getClassifier(), aspect.getClassification(),
             model, uriInfo);
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

    private String getIri(RuleForm child, Aspect<RuleForm> aspect) {
        String eeType = getClassification().getClass().getSimpleName().toLowerCase();
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        String classifier = getClassifier().getId().toString();
        String classification = getClassification().getId().toString();
        URI userUri = ub.path(FacetNodeResource.class).path(eeType).path(child.getId().toString()).path(classifier).path(classification).build();
        return userUri.toASCIIString();
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

    /**
     * @param networkedModel
     * @param gen
     */
    private void writeXdAuths(NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                              JsonGenerator gen) {
        // TODO Auto-generated method stub

    }

}
