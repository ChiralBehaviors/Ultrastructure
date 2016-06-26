/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.wrap;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Facet {
    public static class FacetState {
        @GraphQLField
        public String authority;

        @GraphQLField
        public String classification;

        @GraphQLField
        public String classifier;

        @GraphQLField
        public String name;

        @GraphQLField
        public String notes;

        public void update(FacetRecord record) {
            if (authority != null) {
                record.setAuthority(UUID.fromString(authority));
            }
            if (classification != null) {
                record.setClassification(UUID.fromString(classification));
            }
            if (classifier != null) {
                record.setClassifier(UUID.fromString(classifier));
            }
            if (name != null) {
                record.setName(name);
            }
            if (notes != null) {
                record.setName(notes);
            }
        }
    }

    public static class FacetUpdateState extends FacetState {
        @GraphQLField
        public String id;
    }

    public static Facet fetch(DataFetchingEnvironment env, UUID id) {
        Model model = WorkspaceSchema.ctx(env);
        FacetRecord fetchOne = model.create()
                                    .selectFrom(Tables.FACET)
                                    .where(Tables.FACET.ID.equal(id))
                                    .fetchOne();
        return model.checkRead(fetchOne) ? new Facet(fetchOne) : null;
    }

    private final FacetRecord record;

    public Facet(FacetRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public List<AttributeAuthorization> getAttributes(DataFetchingEnvironment env) {
        return WorkspaceSchema.ctx(env)
                              .getPhantasmModel()
                              .getAttributeAuthorizations(record, false)
                              .stream()
                              .map(r -> new AttributeAuthorization(r))
                              .collect(Collectors.toList());
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        if (record.getAuthority() == null) {
            return null;
        }
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    public List<NetworkAuthorization> getChildren(DataFetchingEnvironment env) {
        return WorkspaceSchema.ctx(env)
                              .getPhantasmModel()
                              .getNetworkAuthorizations(record, false)
                              .stream()
                              .map(r -> new NetworkAuthorization(r))
                              .collect(Collectors.toList());
    }

    @GraphQLField
    public Existential getClassification(DataFetchingEnvironment env) {
        return wrap(resolve(env, record.getClassification()));
    }

    @GraphQLField
    public Relationship getClassifier(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getClassifier()));
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public String getName() {
        return record.getName();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    public FacetRecord getRecord() {
        return record;
    }

    @GraphQLField
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersin() {
        return record.getVersion();
    }
}
