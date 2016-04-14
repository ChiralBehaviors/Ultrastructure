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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.wrap;

import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Facet {
    private final FacetRecord record;

    public Facet(FacetRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAuthority(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    @GraphQLType(ExistentialTypeFunction.class)
    public Existential getClassification(DataFetchingEnvironment env) {
        ExistentialRuleform resolved = resolve(env, record.getClassifier());
        return wrap(resolved);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getClassifier(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getClassifier()));
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    public String getName() {
        return record.getName();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersin() {
        return record.getVersion();
    }

    private <T> T resolve(DataFetchingEnvironment env, UUID id) {
        return ctx(env).records()
                       .resolve(id);
    }
}
