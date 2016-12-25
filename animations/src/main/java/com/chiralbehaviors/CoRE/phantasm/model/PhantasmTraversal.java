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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;

import java.util.UUID;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.utils.English;

/**
 * A programmable traversal mechanism for phantasm metadata
 *
 * @author hhildebrand
 *
 */
public class PhantasmTraversal {

    public static class Aspect {
        private final ExistentialRuleform classification;
        private final Relationship        classifier;
        private final FacetRecord         facet;

        public Aspect(DSLContext create, FacetRecord record) {
            this(record, resolve(create, record.getClassifier()),
                 resolveExistential(create, record.getClassification()));
        }

        public Aspect(DSLContext create, UUID id) {
            this(create, resolveFacet(create, id));
        }

        public Aspect(FacetRecord facet, Relationship classifier,
                      ExistentialRuleform classification) {
            this.facet = facet;
            this.classifier = classifier;
            this.classification = classification;
        }

        public ExistentialRuleform getClassification() {
            return classification;
        }

        public Relationship getClassifier() {
            return classifier;
        }

        public ExistentialDomain getDomain() {
            return classification.getDomain();
        }

        public FacetRecord getFacet() {
            return facet;
        }

        public String getName() {
            return facet.getName();
        }

        @Override
        public String toString() {
            return String.format("Aspect[%s:%s]", classifier.getName(),
                                 classification.getName());
        }
    }

    public static class AttributeAuthorization {
        private final Attribute                               attribute;
        private final ExistentialAttributeAuthorizationRecord auth;

        public AttributeAuthorization(DSLContext create,
                                      ExistentialAttributeAuthorizationRecord auth) {
            this(auth,
                 (Attribute) resolveExistential(create,
                                                auth.getAuthorizedAttribute()));
        }

        public AttributeAuthorization(ExistentialAttributeAuthorizationRecord auth,
                                      Attribute attribute) {
            this.auth = auth;
            this.attribute = attribute;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public ExistentialAttributeAuthorizationRecord getAuth() {
            return auth;
        }

        public String getNotes() {
            return auth.getNotes();
        }

        @Override
        public String toString() {
            return String.format("Attribute auth[%s]", attribute.getName());
        }
    }

    public static class NetworkAttributeAuthorization {
        private final Attribute                                      attribute;
        private final ExistentialNetworkAttributeAuthorizationRecord auth;
        private final NetworkAuthorization                           networkAuth;

        public NetworkAttributeAuthorization(DSLContext create,
                                             ExistentialNetworkAttributeAuthorizationRecord auth,
                                             NetworkAuthorization networkAuth) {
            this(auth, networkAuth,
                 (Attribute) resolveExistential(create,
                                                auth.getAuthorizedAttribute()));
        }

        public NetworkAttributeAuthorization(ExistentialNetworkAttributeAuthorizationRecord auth,
                                             NetworkAuthorization networkAuth,
                                             Attribute attribute) {
            this.auth = auth;
            this.networkAuth = networkAuth;
            this.attribute = attribute;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public ExistentialNetworkAttributeAuthorizationRecord getAuth() {
            return auth;
        }

        public NetworkAuthorization getNetworkAuth() {
            return networkAuth;
        }

        public String getNotes() {
            return auth.getNotes();
        }

        @Override
        public String toString() {
            return String.format("Attribute auth[%s]", attribute.getName());
        }
    }

    public static class NetworkAuthorization {
        private final ExistentialNetworkAuthorizationRecord auth;

        private final Aspect                                child;
        private final FacetRecord                           parent;

        private final Relationship                          relationship;

        public NetworkAuthorization(DSLContext create,
                                    ExistentialNetworkAuthorizationRecord auth,
                                    Aspect child) {
            this(auth, resolveFacet(create, auth.getParent()),
                 resolve(create, auth.getRelationship()), child);
        }

        public NetworkAuthorization(ExistentialNetworkAuthorizationRecord auth,
                                    FacetRecord parent,
                                    Relationship relationship, Aspect child) {
            this.auth = auth;
            this.parent = parent;
            this.relationship = relationship;
            this.child = child;

        }

        public ExistentialNetworkAuthorizationRecord getAuth() {
            return auth;
        }

        public Aspect getChild() {
            return child;
        }

        public ExistentialDomain getDomain() {
            return child.getDomain();
        }

        public String getNotes() {
            return auth.getNotes();
        }

        public FacetRecord getParent() {
            return parent;
        }

        public Relationship getRelationship() {
            return relationship;
        }

        @Override
        public String toString() {
            return String.format("Network Auth[%s->%s]", relationship.getName(),
                                 child);
        }
    }

    public static interface PhantasmVisitor {

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
        void visit(Aspect facet, AttributeAuthorization auth, String fieldName);

        /**
         * Visit the attribute auth on the network edge
         * 
         * @param facet
         * @param auth
         * @param edgeName
         *            - the normalized edge name
         */
        void visit(Aspect facet, NetworkAttributeAuthorization auth,
                   String edgeName);

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
        void visitChildren(Aspect facet, NetworkAuthorization auth,
                           String fieldName, Aspect child,
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
        void visitSingular(Aspect facet, NetworkAuthorization auth,
                           String fieldName, Aspect child);

    }

    private static Relationship resolve(DSLContext create, UUID id) {
        return create.selectFrom(EXISTENTIAL)
                     .where(EXISTENTIAL.ID.equal(id))
                     .fetchOne()
                     .into(Relationship.class);
    }

    private static ExistentialRuleform resolveExistential(DSLContext create,
                                                          UUID id) {
        return new RecordsFactory() {
            @Override
            public DSLContext create() {
                return create;
            }

            @Override
            public UUID currentPrincipalId() {
                return null;
            }
        }.resolve(id);
    }

    private static FacetRecord resolveFacet(DSLContext create, UUID id) {
        return create.selectFrom(FACET)
                     .where(FACET.ID.equal(id))
                     .fetchOne();
    }

    private final Model model;

    public PhantasmTraversal(Model model) {
        this.model = model;
    }

    public void traverse(Aspect facet, PhantasmVisitor visitor) {
        traverseAttributes(facet, visitor);
        traverseNetworkAuths(facet, visitor);

    }

    private void traverseAttributes(Aspect facet, PhantasmVisitor visitor) {

        for (ExistentialAttributeAuthorizationRecord auth : model.getPhantasmModel()
                                                                 .getAttributeAuthorizations(facet.facet,
                                                                                             false)) {
            visitor.visit(facet,
                          new AttributeAuthorization(model.create(), auth),
                          WorkspacePresentation.toFieldName(model.records()
                                                                 .resolve(auth.getAuthorizedAttribute())
                                                                 .getName()));
        }
    }

    private void traverseNetworkAuths(Aspect facet, PhantasmVisitor visitor) {

        DSLContext create = model.create();
        model.getPhantasmModel()
             .getNetworkAuthorizations(facet.facet, false)
             .forEach(auth -> {
                 Aspect child = new Aspect(create, auth.getChild());
                 String fieldName = WorkspacePresentation.toFieldName(auth.getName());
                 NetworkAuthorization networkAuth = new NetworkAuthorization(create,
                                                                             auth,
                                                                             child);

                 String edgeName = fieldName;
                 if (auth.getCardinality() == Cardinality.N) {
                     edgeName = English.plural(fieldName);
                     visitor.visitChildren(facet, networkAuth, edgeName, child,
                                           fieldName);
                 } else {
                     visitor.visitSingular(facet, networkAuth, fieldName,
                                           child);
                 }

                 model.getPhantasmModel()
                      .getNetworkAttributeAuthorizations(auth)
                      .forEach(na -> {
                          visitor.visit(facet,
                                        new NetworkAttributeAuthorization(create,
                                                                          na, networkAuth),
                                        fieldName);
                      });
             });
    }
}
