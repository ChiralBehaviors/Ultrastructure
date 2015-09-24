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

package com.chiralbehaviors.CoRE.phantasm.model;

import java.beans.Introspector;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
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

/**
 * A programmable traversal mechanism for phantasm metadata
 * 
 * @author hhildebrand
 *
 */
public class PhantasmTraversal<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {

    public static interface PhantasmVisitor<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {

        /**
         * Visit the attribute authorization
         * 
         * @param facet
         *            - the facet
         * @param auth
         *            - the attribute authorization
         * @param fieldName
         *            - the normalized field name
         */
        void visit(NetworkAuthorization<RuleForm> facet,
                   AttributeAuthorization<RuleForm, Network> auth,
                   String fieldName);

        /**
         * Visit the multiple child authorization
         * 
         * @param auth
         *            - the authorization
         * @param fieldName
         *            - the normalized field name
         * @param child
         *            - the child facet
         * @param singularFieldName
         *            - the singular form of the field name
         */
        void visitChildren(NetworkAuthorization<RuleForm> facet,
                           NetworkAuthorization<RuleForm> auth,
                           String fieldName,
                           NetworkAuthorization<RuleForm> child,
                           String singularFieldName);

        /**
         * Visit the multiple child xd authorization
         * 
         * @param facet
         * @param auth
         *            - the authorization
         * @param fieldName
         *            - the normalized field name
         * @param child
         *            - the child facet
         * @param singularFieldName
         */
        void visitChildren(NetworkAuthorization<RuleForm> facet,
                           XDomainNetworkAuthorization<?, ?> auth,
                           String fieldName, NetworkAuthorization<?> child,
                           String singularFieldName);

        /**
         * Visit the singular child network authorization
         * 
         * @param auth
         *            - the network authorization
         * @param fieldName
         *            - the normalized field name
         * @param child
         *            - the child facet
         */
        void visitSingular(NetworkAuthorization<RuleForm> facet,
                           NetworkAuthorization<RuleForm> auth,
                           String fieldName,
                           NetworkAuthorization<RuleForm> child);

        /**
         * Visit the singular child xd authorization
         * 
         * @param auth
         *            - the xd authorization
         * @param child
         *            - the facet type
         * @param fieldName
         *            - the normalized field name
         */
        void visitSingular(NetworkAuthorization<RuleForm> facet,
                           XDomainNetworkAuthorization<?, ?> auth,
                           String fieldName, NetworkAuthorization<?> child);

    }

    public static String toFieldName(String name) {
        return Introspector.decapitalize(toValidName(name));
    }

    public static String toTypeName(String name) {
        char chars[] = toValidName(name).toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String toValidName(String name) {
        name = name.replaceAll("\\s", "");
        StringBuilder sb = new StringBuilder();
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            sb.append("_");
        }
        for (char c : name.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static NetworkAuthorization<?> resolveFrom(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth,
                                                       Model model) {
        Aspect<?> aspect = new Aspect<>(auth.getFromRelationship(),
                                        auth.getFromParent());
        @SuppressWarnings({ "rawtypes", "unchecked" })
        NetworkAuthorization facet = model.getNetworkedModel(auth.getFromParent())
                                          .getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private static NetworkAuthorization<?> resolveTo(@SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth,
                                                     Model model) {
        Aspect<?> aspect = new Aspect<>(auth.getToRelationship(),
                                        auth.getToParent());
        @SuppressWarnings({ "rawtypes", "unchecked" })
        NetworkAuthorization facet = model.getNetworkedModel(auth.getToParent())
                                          .getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private final Model model;

    public PhantasmTraversal(Model model) {
        this.model = model;
    }

    public void traverse(NetworkAuthorization<RuleForm> facet,
                         PhantasmVisitor<RuleForm, Network> visitor) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        traverseAttributes(facet, visitor, networkedModel);
        traverseNetworkAuths(facet, visitor, networkedModel);
        traverseXdAuths(facet, visitor, networkedModel);

    }

    private NetworkAuthorization<RuleForm> resolve(NetworkAuthorization<RuleForm> auth,
                                                   NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {
        Aspect<RuleForm> aspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                               auth.getAuthorizedParent());
        NetworkAuthorization<RuleForm> facet = networkedModel.getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private void traverseAgencyAuths(NetworkAuthorization<RuleForm> facet,
                                     PhantasmVisitor<RuleForm, Network> visitor,
                                     NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {
        AgencyModel agencyModel = model.getAgencyModel();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Aspect<Agency> aspect = new Aspect(facet.getClassifier(),
                                           facet.getClassification());
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect,
                                                                                   false)) {
            traverseXdAuth(facet, auth, resolveTo(auth, model), visitor);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect,
                                                                                 false)) {
            traverseXdAuth(facet, auth, resolveTo(auth, model), visitor);
        }
    }

    private void traverseAttributes(NetworkAuthorization<RuleForm> facet,
                                    PhantasmVisitor<RuleForm, Network> visitor,
                                    NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {

        for (AttributeAuthorization<RuleForm, Network> auth : networkedModel.getAttributeAuthorizations(facet,
                                                                                                        false)) {
            auth = Ruleform.initializeAndUnproxy(auth);
            visitor.visit(facet, auth, toFieldName(auth.getAuthorizedAttribute()
                                                       .getName()));
        }
    }

    private void traverseLocationAuths(NetworkAuthorization<RuleForm> facet,
                                       PhantasmVisitor<RuleForm, Network> visitor,
                                       NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {
        LocationModel locationModel = model.getLocationModel();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Aspect<Location> aspect = new Aspect(facet.getClassifier(),
                                             facet.getClassification());
        for (AgencyLocationAuthorization auth : locationModel.getLocationAgencyAuths(aspect,
                                                                                     false)) {
            auth = Ruleform.initializeAndUnproxy(auth);
            traverseXdAuth(facet, auth, resolveFrom(auth, model), visitor);
        }
        for (ProductLocationAuthorization auth : locationModel.getLocationProductAuths(aspect,
                                                                                       false)) {
            auth = Ruleform.initializeAndUnproxy(auth);
            traverseXdAuth(facet, auth, resolveFrom(auth, model), visitor);
        }
    }

    private void traverseNetworkAuths(NetworkAuthorization<RuleForm> facet,
                                      PhantasmVisitor<RuleForm, Network> visitor,
                                      NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {

        Aspect<RuleForm> aspect = new Aspect<>(facet.getClassifier(),
                                               facet.getClassification());
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(aspect,
                                                                                           false)) {
            auth = Ruleform.initializeAndUnproxy(auth);
            NetworkAuthorization<RuleForm> child = resolve(auth,
                                                           networkedModel);
            child = Ruleform.initializeAndUnproxy(child);
            String fieldName = toFieldName(auth.getName());
            if (auth.getCardinality() == Cardinality.N) {
                visitor.visitChildren(facet, auth, English.plural(fieldName),
                                      child, fieldName);
            } else {
                visitor.visitSingular(facet, auth, fieldName, child);
            }
        }
    }

    private void traverseProductAuths(NetworkAuthorization<RuleForm> facet,
                                      PhantasmVisitor<RuleForm, Network> visitor,
                                      NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {

        ProductModel productModel = model.getProductModel();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Aspect<Product> aspect = new Aspect(facet.getClassifier(),
                                            facet.getClassification());
        for (ProductLocationAuthorization auth : productModel.getProductLocationAuths(aspect,
                                                                                      false)) {
            traverseXdAuth(facet, auth, resolveTo(auth, model), visitor);
        }
        for (ProductRelationshipAuthorization auth : productModel.getProductRelationshipAuths(aspect,
                                                                                              false)) {
            traverseXdAuth(facet, auth, resolveTo(auth, model), visitor);
        }
        for (AgencyProductAuthorization auth : productModel.getProductAgencyAuths(aspect,
                                                                                  false)) {
            traverseXdAuth(facet, auth, resolveFrom(auth, model), visitor);
        }
    }

    private void traverseRelationshipAuths(NetworkAuthorization<RuleForm> facet,
                                           PhantasmVisitor<RuleForm, Network> visitor,
                                           NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {

        RelationshipModel agencyModel = model.getRelationshipModel();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Aspect<Relationship> aspect = new Aspect(facet.getClassifier(),
                                                 facet.getClassification());
        for (ProductRelationshipAuthorization auth : agencyModel.getRelationshipProductAuths(aspect,
                                                                                             false)) {
            traverseXdAuth(facet, auth, resolveFrom(auth, model), visitor);
        }
    }

    private void traverseXdAuth(NetworkAuthorization<RuleForm> facet,
                                @SuppressWarnings("rawtypes") XDomainNetworkAuthorization auth,
                                NetworkAuthorization<?> child,
                                PhantasmVisitor<RuleForm, Network> visitor) {
        String fieldName = toFieldName(auth.getName());
        auth = Ruleform.initializeAndUnproxy(auth);
        child = Ruleform.initializeAndUnproxy(child);
        if (auth.getCardinality() == Cardinality.N) {
            visitor.visitChildren(facet, auth, English.plural(fieldName), child,
                                  fieldName);
        } else {
            visitor.visitSingular(facet, auth, fieldName, child);
        }
    }

    private void traverseXdAuths(NetworkAuthorization<RuleForm> facet,
                                 PhantasmVisitor<RuleForm, Network> visitor,
                                 NetworkedModel<RuleForm, Network, ?, ?> networkedModel) {

        if (facet.getClassification() instanceof Agency) {
            traverseAgencyAuths(facet, visitor, networkedModel);
        } else if (facet.getClassification() instanceof Product) {
            traverseProductAuths(facet, visitor, networkedModel);
        } else if (facet.getClassification() instanceof Location) {
            traverseLocationAuths(facet, visitor, networkedModel);
        } else if (facet.getClassification() instanceof Relationship) {
            traverseRelationshipAuths(facet, visitor, networkedModel);
        }
    }
}
