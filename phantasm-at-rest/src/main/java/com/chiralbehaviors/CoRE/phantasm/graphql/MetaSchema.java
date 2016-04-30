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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static graphql.Scalars.GraphQLString;
import static graphql.annotations.DefaultTypeFunction.register;

import java.lang.reflect.AnnotatedType;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.AttributeAuthorizationMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ExistentialMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.FacetMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.NetworkAuthorizationMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.AttributeAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ExistentialQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.FacetQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.NetworkAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization;

import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class MetaSchema {

    public class AttributeAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return MetaSchema.AttributeAuthorizationType;
        }
    }

    public class FacetTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return FacetType;
        }
    }

    public interface Mutations extends ExistentialMutations, FacetMutations,
            AttributeAuthorizationMutations, NetworkAuthorizationMutations {
    }

    public class NetworkAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return NetworkAuthorizationType;
        }
    }

    public interface Queries extends ExistentialQueries, FacetQueries,
            AttributeAuthorizationQueries, NetworkAuthorizationQueries {
    }

    public static final GraphQLObjectType AttributeAuthorizationType = Existential.objectTypeOf(AttributeAuthorization.class);
    public static final GraphQLObjectType FacetType                  = Existential.objectTypeOf(Facet.class);
    public static final GraphQLObjectType NetworkAuthorizationType   = Existential.objectTypeOf(NetworkAuthorization.class);

    static {
        register(UUID.class, (u, t) -> GraphQLString);
        register(ValueType.class, (u, t) -> GraphQLString);
        register(Cardinality.class, (u, t) -> GraphQLString);
        register(Facet.class, (u, t) -> FacetType);
        register(AttributeAuthorization.class,
                 (u, t) -> AttributeAuthorizationType);
        register(NetworkAuthorization.class,
                 (u, t) -> NetworkAuthorizationType);
    }

    public static GraphQLSchema build() throws Exception {
        return GraphQLSchema.newSchema()
                            .query(GraphQLAnnotations.object(Queries.class))
                            .mutation(GraphQLAnnotations.object(Mutations.class))
                            .build();
    }
}
