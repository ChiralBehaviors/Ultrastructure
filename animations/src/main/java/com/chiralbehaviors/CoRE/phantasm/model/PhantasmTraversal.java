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

import static com.chiralbehaviors.CoRE.RecordsFactory.resolve;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.Cardinality;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.utils.English;

/**
 * A programmable traversal mechanism for phantasm metadata
 * 
 * @author hhildebrand
 *
 */
public class PhantasmTraversal<RuleForm extends ExistentialRuleform> {

    public static class AttributeAuthorization {
        private final Attribute                               attribute;
        private final ExistentialAttributeAuthorizationRecord auth;

        public AttributeAuthorization(ExistentialAttributeAuthorizationRecord auth,
                                      Attribute attribute) {
            this.auth = auth;
            this.attribute = attribute;
        }

        /**
         * @param dslContext
         * @param auth2
         */
        public AttributeAuthorization(DSLContext create,
                                      ExistentialAttributeAuthorizationRecord auth) {
            this(auth, resolve(create, auth.getAuthorizedAttribute()));
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public ExistentialAttributeAuthorizationRecord getAuth() {
            return auth;
        }
    }

    public static class NetworkAuthorization {
        private final ExistentialNetworkAuthorizationRecord auth;
        private final ExistentialRuleform                   authorizedParent;
        private final Relationship                          authorizedRelationship;
        private final Relationship                          childRelationship;
        private final ExistentialRuleform                   classification;
        private final Relationship                          classifier;

        public NetworkAuthorization(DSLContext create,
                                    ExistentialNetworkAuthorizationRecord auth) {
            this(auth, resolve(create, auth.getClassification()),
                 resolve(create, auth.getClassifier()),
                 resolve(create, auth.getChildRelationship()),
                 resolve(create, auth.getAuthorizedParent()),
                 resolve(create, auth.getChildRelationship()));
        }

        public NetworkAuthorization(ExistentialNetworkAuthorizationRecord auth,
                                    ExistentialRuleform classification,
                                    Relationship classifier,
                                    Relationship childRelationship,
                                    ExistentialRuleform authorizedParent,
                                    Relationship authorizedRelationship) {
            this.auth = auth;
            this.classification = classification;
            this.classifier = classifier;
            this.childRelationship = childRelationship;
            this.authorizedParent = authorizedParent;
            this.authorizedRelationship = authorizedRelationship;
        }

        public ExistentialNetworkAuthorizationRecord getAuth() {
            return auth;
        }

        public ExistentialRuleform getAuthorizedParent() {
            return authorizedParent;
        }

        public Relationship getAuthorizedRelationship() {
            return authorizedRelationship;
        }

        public Relationship getChildRelationship() {
            return childRelationship;
        }

        public ExistentialRuleform getClassification() {
            return classification;
        }

        public Relationship getClassifier() {
            return classifier;
        }
    }

    public static interface PhantasmVisitor<RuleForm extends ExistentialRuleform> {

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
        void visit(NetworkAuthorization facet, AttributeAuthorization auth,
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
        void visitChildren(NetworkAuthorization facet,
                           NetworkAuthorization auth, String fieldName,
                           NetworkAuthorization child,
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
        void visitSingular(NetworkAuthorization facet,
                           NetworkAuthorization auth, String fieldName,
                           NetworkAuthorization child);

    }

    private final Model model;

    public PhantasmTraversal(Model model) {
        this.model = model;
    }

    public void traverse(NetworkAuthorization facet,
                         PhantasmVisitor<RuleForm> visitor) {
        traverseAttributes(facet, visitor);
        traverseNetworkAuths(facet, visitor);

    }

    private ExistentialNetworkAuthorizationRecord resolveNetworkAuth(ExistentialNetworkAuthorizationRecord auth) {
        Aspect<RuleForm> aspect = new Aspect<>(auth.getAuthorizedRelationship(),
                                               auth.getAuthorizedParent());
        ExistentialNetworkAuthorizationRecord facet = model.getPhantasmModel()
                                                           .getFacetDeclaration(aspect);
        if (facet == null) {
            throw new IllegalStateException(String.format("%s does not exist as a facet",
                                                          aspect));
        }
        return facet;
    }

    private void traverseAttributes(NetworkAuthorization facet,
                                    PhantasmVisitor<RuleForm> visitor) {

        for (ExistentialAttributeAuthorizationRecord auth : model.getPhantasmModel()
                                                                 .getAttributeAuthorizations(facet.auth,
                                                                                             false)) {
            visitor.visit(facet,
                          new AttributeAuthorization(model.getDSLContext(),
                                                     auth),
                          WorkspacePresentation.toFieldName(model.getAttribute(auth.getAuthorizedAttribute())
                                                                 .getName()));
        }
    }

    private void traverseNetworkAuths(NetworkAuthorization facet,
                                      PhantasmVisitor<RuleForm> visitor) {

        DSLContext create = model.getDSLContext();
        Aspect<RuleForm> aspect = new Aspect<>(facet.getClassifier(),
                                               facet.getClassification());
        for (ExistentialNetworkAuthorizationRecord auth : model.getPhantasmModel()
                                                               .getNetworkAuthorizations(aspect,
                                                                                         false)) {
            ExistentialNetworkAuthorizationRecord child = resolveNetworkAuth(auth);
            String fieldName = WorkspacePresentation.toFieldName(auth.getName());
            if (auth.getCardinality() == Cardinality.N.ordinal()) {
                visitor.visitChildren(facet,
                                      new NetworkAuthorization(create, auth),
                                      English.plural(fieldName),
                                      new NetworkAuthorization(create, child),
                                      fieldName);
            } else {
                visitor.visitSingular(facet,
                                      new NetworkAuthorization(create, auth),
                                      fieldName,
                                      new NetworkAuthorization(create, child));
            }
        }
    }
}
