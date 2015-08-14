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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.utils.English;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class FacetType<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Aspect<RuleForm> {

    private final Model                                   model;
    private final NetworkedModel<RuleForm, Network, ?, ?> networkedModel;
    private final GraphQLObjectType.Builder               readBuilder;
    private final Map<String, Aspect<?>>                  referencedTypes = new HashMap<>();

    public FacetType(Relationship classifier, RuleForm classification,
                     Model model) {
        super(classifier, classification);
        this.model = model;
        networkedModel = this.model.getNetworkedModel(classification);
        NetworkAuthorization<RuleForm> facet = networkedModel.getFacetDeclaration(this);
        readBuilder = newObject().name(facet.getName()).description(facet.getNotes());
    }

    public void buid() {
        buildRuleformAttributes();
        buildAttributes();
        buildNetworkAuths();
        buildXdAuths();
    }

    @SuppressWarnings("unchecked")
    public void buildAgencyAuths() {
        AgencyModel agencyModel = model.getAgencyModel();
        Aspect<Agency> aspect = (Aspect<Agency>) this;
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect)) {
            buildXdAuth(auth, typeNameOfTo(auth));
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect)) {
            buildXdAuth(auth, typeNameOfTo(auth));
        }
    }

    public void buildAttributes() {
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            Attribute attribute = auth.getAuthorizedAttribute();
            readBuilder.field(newFieldDefinition().type(typeOf(attribute)).name(attribute.getName()).description(attribute.getDescription()).build());
        }
    }

    public void buildLocationAuths() {
        LocationModel locationModel = model.getLocationModel();
        @SuppressWarnings("unchecked")
        Aspect<Location> aspect = (Aspect<Location>) this;
        for (AgencyLocationAuthorization auth : locationModel.getLocationAgencyAuths(aspect)) {
            buildXdAuth(auth, typeNameOfFrom(auth));
        }
        for (ProductLocationAuthorization auth : locationModel.getLocationProductAuths(aspect)) {
            buildXdAuth(auth, typeNameOfFrom(auth));
        }
    }

    public void buildNetworkAuths() {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            String term = auth.getName();
            if (term == null) {
                continue;
            }
            GraphQLOutputType type = new GraphQLTypeReference(typeNameOf(auth));
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
                type = new GraphQLList(type);
            }
            readBuilder.field(newFieldDefinition().type(type).name(term).description(auth.getNotes()).build());
        }
    }

    public void buildProductAuths() {
        ProductModel productModel = model.getProductModel();
        @SuppressWarnings("unchecked")
        Aspect<Product> aspect = (Aspect<Product>) this;
        for (ProductLocationAuthorization auth : productModel.getProductLocationAuths(aspect)) {
            buildXdAuth(auth, typeNameOfTo(auth));
        }
        for (ProductRelationshipAuthorization auth : productModel.getProductRelationshipAuths(aspect)) {
            buildXdAuth(auth, typeNameOfTo(auth));
        }
        for (AgencyProductAuthorization auth : productModel.getProductAgencyAuths(aspect)) {
            buildXdAuth(auth, typeNameOfFrom(auth));
        }
    }

    public void buildRelationshipAuths() {
        RelationshipModel agencyModel = model.getRelationshipModel();
        @SuppressWarnings("unchecked")
        Aspect<Relationship> aspect = (Aspect<Relationship>) this;
        for (ProductRelationshipAuthorization auth : agencyModel.getRelationshipProductAuths(aspect)) {
            buildXdAuth(auth, typeNameOfFrom(auth));
        }
    }

    public void buildRuleformAttributes() {

        readBuilder.field(newFieldDefinition().type(GraphQLString).name("id").description("The id of the facet instance").build());
        readBuilder.field(newFieldDefinition().type(GraphQLString).name("name").description("The name of the facet instance").build());
        readBuilder.field(newFieldDefinition().type(GraphQLString).name("description").description("The description of the facet instance").build());
    }

    public void buildXdAuth(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth,
                            String typeName) {
        GraphQLOutputType type = new GraphQLTypeReference(typeName);
        String term = auth.getName();
        if (auth.getCardinality() == Cardinality.N) {
            term = English.plural(term);
            type = new GraphQLList(type);
        }
        readBuilder.field(newFieldDefinition().type(type).name(term).description(auth.getNotes()).build());
    }

    /**
     * @param facet
     * @param model
     * @param networkedModel
     * @param builder
     */
    public void buildXdAuths() {
        if (getClassification() instanceof Agency) {
            buildAgencyAuths();
        } else if (getClassification() instanceof Product) {
            buildProductAuths();
        } else if (getClassification() instanceof Location) {
            buildLocationAuths();
        } else if (getClassification() instanceof Relationship) {
            buildRelationshipAuths();
        }
    }

    public GraphQLObjectType getReadType() {
        return readBuilder.build();
    }

    public Map<String, Aspect<?>> getReferencedTypes() {
        return referencedTypes;
    }

    private String typeNameOf(NetworkAuthorization<RuleForm> auth) {
        Aspect<RuleForm> aspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                               auth.getAuthorizedParent());
        NetworkAuthorization<RuleForm> facet = networkedModel.getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        String name = facet.getName();
        referencedTypes.put(name, aspect);
        return name;
    }

    @SuppressWarnings("unchecked")
    private String typeNameOfFrom(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth) {
        Aspect<?> aspect = new Aspect<>(auth.getFromRelationship(),
                                        auth.getFromParent());
        @SuppressWarnings("rawtypes")
        NetworkAuthorization facet = model.getNetworkedModel(auth.getFromParent()).getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        String name = facet.getName();
        referencedTypes.put(name, aspect);
        return name;
    }

    @SuppressWarnings("unchecked")
    private String typeNameOfTo(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth) {
        Aspect<?> aspect = new Aspect<>(auth.getToRelationship(),
                                        auth.getToParent());
        @SuppressWarnings("rawtypes")
        NetworkAuthorization facet = model.getNetworkedModel(auth.getToParent()).getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        String name = facet.getName();
        referencedTypes.put(name, aspect);
        return name;
    }

    private GraphQLOutputType typeOf(Attribute attribute) {
        GraphQLOutputType type;
        switch (attribute.getValueType()) {
            case BINARY:
                type = GraphQLString; // encoded binary
                break;
            case BOOLEAN:
                type = GraphQLBoolean;
                break;
            case INTEGER:
                type = GraphQLInt;
                break;
            case NUMERIC:
                type = GraphQLFloat;
                break;
            case TEXT:
                type = GraphQLString;
                break;
            case TIMESTAMP:
                type = GraphQLString;
                break;
            default:
                throw new IllegalStateException(String.format("Cannot resolved the value type: %s for %s",
                                                              attribute.getValueType(),
                                                              attribute));
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }
}
