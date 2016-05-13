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

import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLString;
import static graphql.annotations.DefaultTypeFunction.register;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Plugin;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Workspace;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.AttributeAuthorizationMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ChildSequencingMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ExistentialMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.FacetMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.JobMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.MetaProtocolMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.NetworkAttributeAuthorizationMutations;
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
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobChronologyQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.MetaProtocolQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.NetworkAttributeAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.NetworkAuthorizationQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ParentSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ProtocolQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.SelfSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.SiblingSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.StatusCodeSequencingQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ChildSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.JobChronology;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.MetaProtocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ParentSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Protocol;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SelfSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;

import graphql.annotations.GraphQLAnnotations2;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public final class WorkspaceSchema {

    public static class TypeProxy extends GraphQLObjectType {
        private final GraphQLObjectType target;

        public TypeProxy(GraphQLObjectType target) {
            super("", "", Collections.emptyList(), Collections.emptyList());
            this.target = target;
        }

        @Override
        public String getDescription() {
            return target.getDescription();
        }

        @Override
        public GraphQLFieldDefinition getFieldDefinition(String name) {
            return target.getFieldDefinition(name);
        }

        @Override
        public List<GraphQLFieldDefinition> getFieldDefinitions() {
            return target.getFieldDefinitions();
        }

        @Override
        public List<GraphQLInterfaceType> getInterfaces() {
            return target.getInterfaces();
        }

        @Override
        public String getName() {
            return target.getName();
        }

        @Override
        public String toString() {
            return "GraphQLObjectType{" + "name='" + getName() + '\''
                   + ", description='" + getDescription() + '\''
                   + ", fieldDefinitions=" + getFieldDefinitions()
                   + ", interfaces=" + getInterfaces() + '}';
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                     + ((target == null) ? 0 : target.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof GraphQLObjectType)) {
                return false;
            }
            if (obj instanceof TypeProxy) {
                return target.equals(((TypeProxy) obj).target);
            }
            if (!target.equals(obj)) {
                return false;
            }
            return true;
        }
    }

    public interface MetaMutations extends ExistentialMutations, FacetMutations,
            AttributeAuthorizationMutations, NetworkAuthorizationMutations,
            ChildSequencingMutations, ParentSequencingMutations,
            SelfSequencingMutations, SiblingSequencingMutations,
            ProtocolMutations, MetaProtocolMutations,
            StatusCodeSequencingMutations,
            NetworkAttributeAuthorizationMutations, JobMutations {
    }

    public interface MetaQueries extends ExistentialQueries, FacetQueries,
            AttributeAuthorizationQueries, NetworkAuthorizationQueries,
            ChildSequencingQueries, ParentSequencingQueries,
            SelfSequencingQueries, SiblingSequencingQueries, ProtocolQueries,
            MetaProtocolQueries, StatusCodeSequencingQueries,
            NetworkAttributeAuthorizationQueries, JobQueries,
            JobChronologyQueries {
    }

    public interface Mutations extends ExistentialMutations, JobMutations {
    }

    public interface Queries
            extends ExistentialQueries, JobQueries, JobChronologyQueries {
    }

    private static final GraphQLObjectType                                      AgencyType;
    private static final GraphQLObjectType                                      AttributeAuthorizationType;
    private static final GraphQLObjectType                                      AttributeType;
    private static final GraphQLObjectType                                      ChildSequencingType;
    private static final ThreadLocal<Map<ExistentialDomain, GraphQLObjectType>> Existentials = new ThreadLocal<>();
    private static final GraphQLObjectType                                      FacetType;
    private static final GraphQLObjectType                                      IntervalType;
    private static final GraphQLObjectType                                      JobChronologyType;
    private static final GraphQLObjectType                                      JobType;
    private static final GraphQLObjectType                                      LocationType;
    private static final GraphQLObjectType                                      MetaProtocolType;
    private static final GraphQLObjectType                                      NetworkAttributeAuthorizationType;
    private static final GraphQLObjectType                                      NetworkAuthorizationType;
    private static final GraphQLObjectType                                      ParentSequencingType;
    private static final GraphQLObjectType                                      ProductType;
    private static final GraphQLObjectType                                      ProtocolType;
    private static final GraphQLObjectType                                      RelationshipType;
    private static final GraphQLObjectType                                      SelfSequencingType;
    private static final GraphQLObjectType                                      SiblingSequencingType;
    private static final GraphQLObjectType                                      StatusCodeSequencingType;
    private static final GraphQLObjectType                                      StatusCodeType;
    private static final GraphQLObjectType                                      UnitType;

    // Type conversion initialization is kinda tricky because recursion.
    // Be careful how you manage the static initialization of this class
    static {
        // primitive types must be registered first - obviously
        register(Double.class, (u, t) -> GraphQLFloat);
        register(UUID.class, (u, t) -> GraphQLString);
        register(ValueType.class, (u, t) -> GraphQLString);
        register(Cardinality.class, (u, t) -> GraphQLString);
        register(ReferenceType.class, (u, t) -> GraphQLString);

        GraphQLInterfaceType vanillaExistential = interfaceTypeOf(Existential.class);
        register(Existential.class, (u, t) -> {
            return vanillaExistential;
        });

        // Agency is recursive and referred to by everything
        AgencyType = objectTypeOf(Agency.class);
        register(Agency.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? AgencyType
                                                   : workspace.get(ExistentialDomain.Agency));
        });

        AttributeType = objectTypeOf(Attribute.class);
        register(Attribute.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? AttributeType
                                                   : workspace.get(ExistentialDomain.Attribute));
        });

        IntervalType = objectTypeOf(Interval.class);
        register(Interval.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? IntervalType
                                                   : workspace.get(ExistentialDomain.Interval));
        });

        LocationType = objectTypeOf(Location.class);
        register(Location.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? LocationType
                                                   : workspace.get(ExistentialDomain.Location));
        });

        ProductType = objectTypeOf(Product.class);
        register(com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product.class,
                 (u, t) -> {
                     Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
                     return new TypeProxy(workspace == null ? ProductType
                                                            : workspace.get(ExistentialDomain.Product));
                 });

        RelationshipType = objectTypeOf(Relationship.class);
        register(Relationship.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? RelationshipType
                                                   : workspace.get(ExistentialDomain.Relationship));
        });

        StatusCodeType = objectTypeOf(StatusCode.class);
        register(StatusCode.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? StatusCodeType
                                                   : workspace.get(ExistentialDomain.StatusCode));
        });

        UnitType = objectTypeOf(Unit.class);
        register(Unit.class, (u, t) -> {
            Map<ExistentialDomain, GraphQLObjectType> workspace = Existentials.get();
            return new TypeProxy(workspace == null ? UnitType
                                                   : workspace.get(ExistentialDomain.Unit));
        });

        FacetType = objectTypeOf(Facet.class);
        register(Facet.class, (u, t) -> FacetType);

        AttributeAuthorizationType = objectTypeOf(AttributeAuthorization.class);
        register(AttributeAuthorization.class,
                 (u, t) -> AttributeAuthorizationType);

        ChildSequencingType = objectTypeOf(ChildSequencing.class);
        register(ChildSequencing.class, (u, t) -> ChildSequencingType);

        JobType = objectTypeOf(Job.class);
        register(Job.class, (u, t) -> JobType);

        MetaProtocolType = objectTypeOf(MetaProtocol.class);
        register(MetaProtocol.class, (u, t) -> MetaProtocolType);

        NetworkAuthorizationType = objectTypeOf(NetworkAuthorization.class);
        register(NetworkAuthorization.class,
                 (u, t) -> NetworkAuthorizationType);

        ParentSequencingType = objectTypeOf(ParentSequencing.class);
        register(ParentSequencing.class, (u, t) -> ParentSequencingType);

        ProtocolType = objectTypeOf(Protocol.class);
        register(Protocol.class, (u, t) -> ProtocolType);

        SelfSequencingType = objectTypeOf(SelfSequencing.class);
        register(SelfSequencing.class, (u, t) -> SelfSequencingType);

        SiblingSequencingType = objectTypeOf(SiblingSequencing.class);
        register(SiblingSequencing.class, (u, t) -> SiblingSequencingType);

        StatusCodeSequencingType = objectTypeOf(StatusCodeSequencing.class);
        register(StatusCodeSequencing.class,
                 (u, t) -> StatusCodeSequencingType);

        NetworkAttributeAuthorizationType = objectTypeOf(NetworkAttributeAuthorization.class);
        register(NetworkAttributeAuthorization.class,
                 (u, t) -> NetworkAttributeAuthorizationType);

        JobChronologyType = WorkspaceSchema.objectTypeOf(JobChronology.class);
        register(JobChronology.class, (u, t) -> JobChronologyType);
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
            type.resolve(facet, facetPlugins, model, executionScope)
                .stream()
                .filter(auth -> !resolved.containsKey(auth))
                .forEach(auth -> unresolved.add(auth));
        }

        Existentials.set(existentials(resolved));
        Builder topLevelQuery = GraphQLAnnotations2.objectBuilder(Queries.class);
        Builder topLevelMutation = GraphQLAnnotations2.objectBuilder(Mutations.class);
        GraphQLSchema schema;
        try {
            resolved.entrySet()
                    .stream()
                    .forEach(e -> e.getValue()
                                   .build(new Aspect(model.create(),
                                                     e.getKey()),
                                          resolved, topLevelQuery,
                                          topLevelMutation));
            schema = GraphQLSchema.newSchema()
                                  .query(topLevelQuery.build())
                                  .mutation(topLevelMutation.build())
                                  .build();
        } finally {
            Existentials.set(null);
        }
        return schema;
    }

    public static GraphQLSchema buildMeta() throws Exception {
        return GraphQLSchema.newSchema()
                            .query(GraphQLAnnotations2.object(MetaQueries.class))
                            .mutation(GraphQLAnnotations2.object(MetaMutations.class))
                            .build();
    }

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    public static GraphQLObjectType existentialType(ExistentialDomain domain) {
        Map<ExistentialDomain, GraphQLObjectType> map = Existentials.get();
        if (map == null) {
            switch (domain) {
                case Agency:
                    return AgencyType;
                case Attribute:
                    return AttributeType;
                case Interval:
                    return IntervalType;
                case Location:
                    return LocationType;
                case Product:
                    return ProductType;
                case Relationship:
                    return RelationshipType;
                case StatusCode:
                    return StatusCodeType;
                case Unit:
                    return UnitType;
                default:
                    throw new IllegalStateException(String.format("invalid domain: %s",
                                                                  domain));
            }
        }
        return map.get(domain);
    }

    public static GraphQLInterfaceType interfaceTypeOf(Class<?> clazz) {
        try {
            return GraphQLAnnotations2.iface(clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(String.format("Unable to create interface  type for %s",
                                                          clazz.getSimpleName()));
        }
    }

    public static GraphQLObjectType objectTypeOf(Class<?> clazz) {
        try {
            return GraphQLAnnotations2.object(clazz);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Unable to create object type for %s",
                                                          clazz.getSimpleName()));
        }
    }

    private static void addPhantasmCast(Builder typeBuilder,
                                        Entry<FacetRecord, FacetFields> entry) {
        typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                .name(String.format("as%s",
                                                                    WorkspacePresentation.toTypeName(entry.getKey()
                                                                                                          .getName())))
                                                .description(String.format("Cast to the %s facet",
                                                                           entry.getKey()
                                                                                .getName()))
                                                .type(new GraphQLTypeReference(entry.getValue()
                                                                                    .getName()))
                                                .dataFetcher(env -> {
                                                    ExistentialRuleform existential = (ExistentialRuleform) env.getSource();
                                                    PhantasmCRUD crud = FacetFields.ctx(env);
                                                    crud.cast(existential,
                                                              new Aspect(crud.getModel()
                                                                             .create(),
                                                                         entry.getKey()));
                                                    return existential;
                                                })
                                                .build());
    }

    private static Map<ExistentialDomain, GraphQLObjectType> existentials(Map<FacetRecord, FacetFields> resolved) throws NoSuchMethodException,
                                                                                                                  InstantiationException,
                                                                                                                  IllegalAccessException {
        Map<ExistentialDomain, GraphQLObjectType> existentials = new HashMap<>();
        existentials.put(ExistentialDomain.Agency,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Agency.class)));
        existentials.put(ExistentialDomain.Attribute,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Attribute.class)));
        existentials.put(ExistentialDomain.Interval,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Interval.class)));
        existentials.put(ExistentialDomain.Location,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Location.class)));
        existentials.put(ExistentialDomain.Product,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product.class)));
        existentials.put(ExistentialDomain.Relationship,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Relationship.class)));
        existentials.put(ExistentialDomain.StatusCode,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(StatusCode.class)));
        existentials.put(ExistentialDomain.Unit,
                         phantasm(resolved,
                                  GraphQLAnnotations2.objectBuilder(Unit.class)));
        return existentials;
    }

    private static GraphQLObjectType phantasm(Map<FacetRecord, FacetFields> resolved,
                                              Builder objectBuilder) {
        resolved.entrySet()
                .forEach(e -> addPhantasmCast(objectBuilder, e));
        return objectBuilder.build();
    }

    private WorkspaceSchema() {
    }
}
