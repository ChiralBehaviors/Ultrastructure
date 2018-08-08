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

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.utils.English;

/**
 * Bare bones metadata representation of a Phantasm facet in Java
 *
 * @author hhildebrand
 *
 */
public class Phantasmagoria {
    public static String toFieldName(String name) {
        return Introspector.decapitalize(name.replaceAll("\\s", ""));
    }

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
            return facet.getConstrainTo();
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

    public static class NetworkAuthorization {
        private final ExistentialNetworkAuthorizationRecord auth;
        private final Aspect                                child;
        private final String                                fieldName;
        private final FacetRecord                           parent;
        private final Relationship                          relationship;

        public NetworkAuthorization(String fieldName, DSLContext create,
                                    ExistentialNetworkAuthorizationRecord auth,
                                    Aspect child) {
            this(fieldName, auth, resolveFacet(create, auth.getParent()),
                 resolve(create, auth.getRelationship()), child);
        }

        public NetworkAuthorization(String fieldName,
                                    ExistentialNetworkAuthorizationRecord auth,
                                    FacetRecord parent,
                                    Relationship relationship, Aspect child) {
            this.fieldName = fieldName;
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

        public String getFieldName() {
            return fieldName;
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

        public String plural() {
            return English.plural(fieldName);
        }

        @Override
        public String toString() {
            return String.format("Network Auth[%s->%s]", relationship.getName(),
                                 child);
        }
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

    public final Map<String, NetworkAuthorization> childAuthorizations    = new HashMap<>();
    public final Aspect                            facet;
    public final Map<String, NetworkAuthorization> singularAuthorizations = new HashMap<>();

    public Phantasmagoria(Aspect facet) {
        this.facet = facet;
    }

    public void traverse(Model model) {
        traverseNetworkAuths(model);

    }

    private void traverseNetworkAuths(Model model) {

        DSLContext create = model.create();
        model.getPhantasmModel()
             .getNetworkAuthorizations(facet.getFacet(), false)
             .forEach(auth -> {
                 Aspect child = new Aspect(create, auth.getChild());
                 String fieldName = toFieldName(auth.getName());
                 NetworkAuthorization networkAuth = new NetworkAuthorization(fieldName,
                                                                             create,
                                                                             auth,
                                                                             child);

                 if (auth.getCardinality() == Cardinality.N) {
                     childAuthorizations.put(networkAuth.getFieldName(), networkAuth);
                 } else {
                     singularAuthorizations.put(networkAuth.getFieldName(),
                                                networkAuth);
                 }
             });
    }

    public static String capitalized(String field) {
        char[] chars = field.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
