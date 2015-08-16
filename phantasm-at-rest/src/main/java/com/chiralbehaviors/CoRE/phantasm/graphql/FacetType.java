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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private final NetworkAuthorization<RuleForm>          facet;
    private final Model                                   model;
    private final NetworkedModel<RuleForm, Network, ?, ?> networkedModel;
    private GraphQLObjectType                             query;
    private final Set<NetworkAuthorization<?>>            references = new HashSet<>();
    GraphQLObjectType.Builder                             builder;

    public FacetType(NetworkAuthorization<RuleForm> facet, Model model) {
        super(facet.getClassifier(), facet.getClassification());
        this.model = model;
        networkedModel = this.model.getNetworkedModel(getClassification());
        this.facet = facet;
        builder = newObject().name(facet.getName())
                             .description(facet.getNotes());
    }

    public GraphQLObjectType build(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                   Set<NetworkAuthorization<?>> traversed) {
        traversed.add(facet);
        buildAttributes();
        buildRuleformAttributes();
        buildNetworkAuths(resolved, traversed);
        buildXdAuths(resolved, traversed);
        return builder.build();
    }

    public NetworkAuthorization<RuleForm> getFacet() {
        return facet;
    }

    public String getName() {
        return facet.getName();
    }

    public GraphQLObjectType getQuery() {
        return query;
    }

    public Collection<NetworkAuthorization<?>> referenced() {
        return references;
    }

    public Collection<NetworkAuthorization<?>> resolve() {
        if (!references.isEmpty()) {
            return references;
        }
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            references.add(resolve(auth));
        }
        resolveXdAuths();
        return references;
    }

    @Override
    public String toString() {
        return String.format("FacetType [name=%s]", getName());
    }

    @SuppressWarnings("unchecked")
    private void buildAgencyAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                  Set<NetworkAuthorization<?>> traversed) {
        AgencyModel agencyModel = model.getAgencyModel();
        Aspect<Agency> aspect = (Aspect<Agency>) this;
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect)) {
            buildXdAuth(auth, resolveTo(auth), resolved, traversed);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect)) {
            buildXdAuth(auth, resolveTo(auth), resolved, traversed);
        }
    }

    private void buildAttributes() {
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            Attribute attribute = auth.getAuthorizedAttribute();
            builder.field(newFieldDefinition().type(typeOf(attribute))
                                              .name(attribute.getName())
                                              .description(attribute.getDescription())
                                              .build());
        }
    }

    private void buildLocationAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                    Set<NetworkAuthorization<?>> traversed) {
        LocationModel locationModel = model.getLocationModel();
        @SuppressWarnings("unchecked")
        Aspect<Location> aspect = (Aspect<Location>) this;
        for (AgencyLocationAuthorization auth : locationModel.getLocationAgencyAuths(aspect)) {
            buildXdAuth(auth, resolveFrom(auth), resolved, traversed);
        }
        for (ProductLocationAuthorization auth : locationModel.getLocationProductAuths(aspect)) {
            buildXdAuth(auth, resolveFrom(auth), resolved, traversed);
        }
    }

    private void buildNetworkAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                   Set<NetworkAuthorization<?>> traversed) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            String term = auth.getName();
            if (term == null) {
                continue;
            }
            GraphQLOutputType type;
            NetworkAuthorization<RuleForm> child = resolve(auth);
            if (traversed.contains(child)) {
                type = new GraphQLTypeReference(child.getName());
            } else {
                type = resolved.get(child)
                               .build(resolved, traversed);
            }
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
                type = new GraphQLList(type);
            }
            builder.field(newFieldDefinition().type(type)
                                              .name(term)
                                              .description(auth.getNotes())
                                              .build());
        }
    }

    private void buildProductAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                   Set<NetworkAuthorization<?>> traversed) {
        ProductModel productModel = model.getProductModel();
        @SuppressWarnings("unchecked")
        Aspect<Product> aspect = (Aspect<Product>) this;
        for (ProductLocationAuthorization auth : productModel.getProductLocationAuths(aspect)) {
            buildXdAuth(auth, resolveTo(auth), resolved, traversed);
        }
        for (ProductRelationshipAuthorization auth : productModel.getProductRelationshipAuths(aspect)) {
            buildXdAuth(auth, resolveTo(auth), resolved, traversed);
        }
        for (AgencyProductAuthorization auth : productModel.getProductAgencyAuths(aspect)) {
            buildXdAuth(auth, resolveFrom(auth), resolved, traversed);
        }
    }

    private void buildRelationshipAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                                        Set<NetworkAuthorization<?>> traversed) {
        RelationshipModel agencyModel = model.getRelationshipModel();
        @SuppressWarnings("unchecked")
        Aspect<Relationship> aspect = (Aspect<Relationship>) this;
        for (ProductRelationshipAuthorization auth : agencyModel.getRelationshipProductAuths(aspect)) {
            buildXdAuth(auth, resolveFrom(auth), resolved, traversed);
        }
    }

    private void buildRuleformAttributes() {

        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name("id")
                                          .description("The id of the facet instance")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name("name")
                                          .description("The name of the facet instance")
                                          .build());
        builder.field(newFieldDefinition().type(GraphQLString)
                                          .name("description")
                                          .description("The description of the facet instance")
                                          .build());
    }

    private void buildXdAuth(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth,
                             NetworkAuthorization<?> child,
                             Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                             Set<NetworkAuthorization<?>> traversed) {
        GraphQLOutputType type;
        if (traversed.contains(child)) {
            type = new GraphQLTypeReference(child.getName());
        } else {
            type = resolved.get(child)
                           .build(resolved, traversed);
        }
        String term = auth.getName();
        if (auth.getCardinality() == Cardinality.N) {
            term = English.plural(term);
            type = new GraphQLList(type);
        }
        builder.field(newFieldDefinition().type(type)
                                          .name(term)
                                          .description(auth.getNotes())
                                          .build());
    }

    private void buildXdAuths(Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved,
                              Set<NetworkAuthorization<?>> traversed) {
        if (getClassification() instanceof Agency) {
            buildAgencyAuths(resolved, traversed);
        } else if (getClassification() instanceof Product) {
            buildProductAuths(resolved, traversed);
        } else if (getClassification() instanceof Location) {
            buildLocationAuths(resolved, traversed);
        } else if (getClassification() instanceof Relationship) {
            buildRelationshipAuths(resolved, traversed);
        }
    }

    private NetworkAuthorization<RuleForm> resolve(NetworkAuthorization<RuleForm> auth) {
        Aspect<RuleForm> aspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                               auth.getAuthorizedParent());
        NetworkAuthorization<RuleForm> facet = networkedModel.getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private void resolveAgencyAuths() {
        AgencyModel agencyModel = model.getAgencyModel();
        @SuppressWarnings("unchecked")
        Aspect<Agency> aspect = (Aspect<Agency>) this;
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect)) {
            references.add(resolveTo(auth));
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect)) {
            references.add(resolveTo(auth));
        }
    }

    @SuppressWarnings("unchecked")
    private NetworkAuthorization<?> resolveFrom(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth) {
        Aspect<?> aspect = new Aspect<>(auth.getFromRelationship(),
                                        auth.getFromParent());
        @SuppressWarnings("rawtypes")
        NetworkAuthorization facet = model.getNetworkedModel(auth.getFromParent())
                                          .getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    /**
     */
    private void resolveLocationAuths() {
        LocationModel locationModel = model.getLocationModel();
        @SuppressWarnings("unchecked")
        Aspect<Location> aspect = (Aspect<Location>) this;
        for (AgencyLocationAuthorization auth : locationModel.getLocationAgencyAuths(aspect)) {
            references.add(resolveFrom(auth));
        }
        for (ProductLocationAuthorization auth : locationModel.getLocationProductAuths(aspect)) {
            references.add(resolveFrom(auth));
        }
    }

    /**
     */
    private void resolveProductAuths() {
        ProductModel productModel = model.getProductModel();
        @SuppressWarnings("unchecked")
        Aspect<Product> aspect = (Aspect<Product>) this;
        for (ProductLocationAuthorization auth : productModel.getProductLocationAuths(aspect)) {
            references.add(resolveTo(auth));
        }
        for (ProductRelationshipAuthorization auth : productModel.getProductRelationshipAuths(aspect)) {
            references.add(resolveTo(auth));
        }
        for (AgencyProductAuthorization auth : productModel.getProductAgencyAuths(aspect)) {
            references.add(resolveFrom(auth));
        }
    }

    private void resolveRelationshipAuths() {
        RelationshipModel agencyModel = model.getRelationshipModel();
        @SuppressWarnings("unchecked")
        Aspect<Relationship> aspect = (Aspect<Relationship>) this;
        for (ProductRelationshipAuthorization auth : agencyModel.getRelationshipProductAuths(aspect)) {
            references.add(resolveFrom(auth));
        }
    }

    @SuppressWarnings("unchecked")
    private NetworkAuthorization<?> resolveTo(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth) {
        Aspect<?> aspect = new Aspect<>(auth.getToRelationship(),
                                        auth.getToParent());
        @SuppressWarnings("rawtypes")
        NetworkAuthorization facet = model.getNetworkedModel(auth.getToParent())
                                          .getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private void resolveXdAuths() {
        if (getClassification() instanceof Agency) {
            resolveAgencyAuths();
        } else if (getClassification() instanceof Product) {
            resolveProductAuths();
        } else if (getClassification() instanceof Location) {
            resolveLocationAuths();
        } else if (getClassification() instanceof Relationship) {
            resolveRelationshipAuths();
        }
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
