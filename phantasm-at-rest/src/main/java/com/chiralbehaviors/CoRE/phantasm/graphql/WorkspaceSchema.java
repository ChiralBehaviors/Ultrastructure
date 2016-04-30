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
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Plugin;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Workspace;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.AttributeAuthorizationMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ChildSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ExistentialMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.FacetMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.JobMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.MetaProtocolMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.NetworkAuthorizationMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ParentSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ProtocolMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.SelfSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.SiblingSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.StatusCodeSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.AttributeAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ChildSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ExistentialQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.FacetQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.MetaProtocolQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.NetworkAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ParentSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ProtocolQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.SelfSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.SiblingSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.StatusCodeSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ChildSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ParentSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Protocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SelfSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.StatusCodeSequencing;

import graphql.annotations.GraphQLAnnotations2;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchema {

    public class AttributeAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return WorkspaceSchema.AttributeAuthorizationType;
        }
    }

    public class ChildSequencingTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ChildSequencingType;
        }
    }

    public class FacetTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return FacetType;
        }
    }

    public class JobTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return JobType;
        }
    }

    public interface MetaMutations extends ExistentialMutations, FacetMutations,
            AttributeAuthorizationMutations, NetworkAuthorizationMutations,
            ChildSequencingMutations, ParentSequencingMutations,
            SelfSequencingMutations, SiblingSequencingMutations,
            ProtocolMutations, MetaProtocolMutations,
            StatusCodeSequencingMutations {
    }

    public class MetaProtocolTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return StatusCodeSequencingType;
        }
    }

    public interface MetaQueries extends ExistentialQueries, FacetQueries,
            AttributeAuthorizationQueries, NetworkAuthorizationQueries,
            ChildSequencingQueries, ParentSequencingQueries,
            SelfSequencingQueries, SiblingSequencingQueries, ProtocolQueries,
            MetaProtocolQueries, StatusCodeSequencingQueries {
    }

    public interface Mutations extends ExistentialMutations, JobMutations {
    }

    public class NetworkAuthorizationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return NetworkAuthorizationType;
        }
    }

    public class ParentSequencingTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ParentSequencingType;
        }
    }

    public class ProtocolTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ProtocolType;
        }
    }

    public interface Queries extends ExistentialQueries, JobQueries {
    }

    public class SelfSequencingTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return SelfSequencingType;
        }
    }

    public class SiblingSequencingTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return SiblingSequencingType;
        }
    }

    public class StatusCodeSequencingTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return StatusCodeSequencingType;
        }
    }

    public static final GraphQLObjectType AttributeAuthorizationType = Existential.objectTypeOf(AttributeAuthorization.class);
    public static final GraphQLObjectType ChildSequencingType        = Existential.objectTypeOf(ChildSequencing.class);
    public static final GraphQLObjectType FacetType                  = Existential.objectTypeOf(Facet.class);
    public static final GraphQLObjectType JobType                    = Existential.objectTypeOf(Job.class);
    public static final GraphQLObjectType MetaProtocolType           = Existential.objectTypeOf(MetaProtocol.class);
    public static final GraphQLObjectType NetworkAuthorizationType   = Existential.objectTypeOf(NetworkAuthorization.class);
    public static final GraphQLObjectType ParentSequencingType       = Existential.objectTypeOf(ParentSequencing.class);
    public static final GraphQLObjectType ProtocolType               = Existential.objectTypeOf(Protocol.class);
    public static final GraphQLObjectType SelfSequencingType         = Existential.objectTypeOf(SelfSequencing.class);
    public static final GraphQLObjectType SiblingSequencingType      = Existential.objectTypeOf(SiblingSequencing.class);
    public static final GraphQLObjectType StatusCodeSequencingType   = Existential.objectTypeOf(StatusCodeSequencing.class);

    static {
        register(UUID.class, (u, t) -> GraphQLString);
        register(ValueType.class, (u, t) -> GraphQLString);
        register(Cardinality.class, (u, t) -> GraphQLString);
        register(Facet.class, (u, t) -> FacetType);
        register(AttributeAuthorization.class,
                 (u, t) -> AttributeAuthorizationType);
        register(NetworkAuthorization.class,
                 (u, t) -> NetworkAuthorizationType);
        register(Job.class, (u, t) -> JobType);
    }

    public static GraphQLSchema build() throws Exception {
        Builder topLevelQueries = GraphQLAnnotations2.objectBuilder(Queries.class);
        Builder topLevelMutations = GraphQLAnnotations2.objectBuilder(Mutations.class);
        return GraphQLSchema.newSchema()
                            .query(topLevelQueries.build())
                            .mutation(topLevelMutations.build())
                            .build();
    }

    public static GraphQLSchema build(WorkspaceAccessor accessor, Model model,
                                      ClassLoader executionScope) throws NoSuchMethodException,
                                                                  InstantiationException,
                                                                  IllegalAccessException {
        Deque<FacetRecord> unresolved = FacetFields.initialState(accessor,
                                                                 model);
        Map<FacetRecord, FacetFields> resolved = new HashMap<>();
        Product definingProduct = accessor.getDefiningProduct();
        Workspace workspace = model.wrap(Workspace.class, definingProduct);
        Builder topLevelQuery = GraphQLAnnotations2.objectBuilder(Queries.class);
        Builder topLevelMutation = GraphQLAnnotations2.objectBuilder(Mutations.class);
        List<Plugin> plugins = workspace.getPlugins();
        while (!unresolved.isEmpty()) {
            FacetRecord facet = unresolved.pop();
            if (resolved.containsKey(facet)) {
                continue;
            }
            FacetFields type = new FacetFields(facet);
            resolved.put(facet, type);
            List<Plugin> facetPlugins = plugins.stream()
                                               .filter(plugin -> facet.getName()
                                                                      .equals(plugin.getFacetName()))
                                               .collect(Collectors.toList());
            type.build(topLevelQuery, topLevelMutation, facet, facetPlugins,
                       model, executionScope)
                .stream()
                .filter(auth -> !resolved.containsKey(auth))
                .forEach(auth -> unresolved.add(auth));
        }
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build();
        return schema;
    }

    public static GraphQLSchema buildMeta() throws Exception {
        return GraphQLSchema.newSchema()
                            .query(GraphQLAnnotations2.object(MetaQueries.class))
                            .mutation(GraphQLAnnotations2.object(MetaMutations.class))
                            .build();
    }
}
