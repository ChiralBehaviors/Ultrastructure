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

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
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

import graphql.annotations.GraphQLAnnotations2;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchema {

    public interface MetaMutations extends ExistentialMutations, FacetMutations,
            AttributeAuthorizationMutations, NetworkAuthorizationMutations,
            ChildSequencingMutations, ParentSequencingMutations,
            SelfSequencingMutations, SiblingSequencingMutations,
            ProtocolMutations, MetaProtocolMutations,
            StatusCodeSequencingMutations,
            NetworkAttributeAuthorizationMutations {
    }

    public interface MetaQueries extends ExistentialQueries, FacetQueries,
            AttributeAuthorizationQueries, NetworkAuthorizationQueries,
            ChildSequencingQueries, ParentSequencingQueries,
            SelfSequencingQueries, SiblingSequencingQueries, ProtocolQueries,
            MetaProtocolQueries, StatusCodeSequencingQueries,
            NetworkAttributeAuthorizationQueries {
    }

    public interface Mutations extends ExistentialMutations, JobMutations {
    }

    public interface Queries
            extends ExistentialQueries, JobQueries, JobChronologyQueries {
    }

    public static final GraphQLObjectType          AgencyType;
    public static final GraphQLObjectType          AttributeAuthorizationType;
    public static final GraphQLObjectType          AttributeType;
    public static final GraphQLObjectType          ChildSequencingType;
    public static final GraphQLInterfaceType       ExistentialType;
    public static final GraphQLObjectType          FacetType;
    public static final GraphQLObjectType          IntervalType;
    public static final GraphQLObjectType          JobType;
    public static final GraphQLObjectType          LocationType;
    public static final GraphQLObjectType          MetaProtocolType;
    public static final GraphQLObjectType          NetworkAuthorizationType;
    public static final GraphQLObjectType          ParentSequencingType;
    public static final GraphQLObjectType          ProductType;
    public static final GraphQLObjectType          ProtocolType;
    public static final GraphQLObjectType          RelationshipType;
    public static final GraphQLObjectType          SelfSequencingType;
    public static final GraphQLObjectType          SiblingSequencingType;
    public static final GraphQLObjectType          StatusCodeSequencingType;
    public static final GraphQLObjectType          StatusCodeType;
    public static final GraphQLObjectType          UnitType;
    public static final GraphQLObjectType          NetworkAttributeAuthorizationType;
    public static final graphql.schema.GraphQLType JobChronologyType;

    // Type conversion initialization is kinda tricky because recursion.
    // Be careful how you manage the static initialization of this class
    static {
        // primitive types must be registered first - obviously
        register(Double.class, (u, t) -> GraphQLFloat);
        register(UUID.class, (u, t) -> GraphQLString);
        register(ValueType.class, (u, t) -> GraphQLString);
        register(Cardinality.class, (u, t) -> GraphQLString);
        register(ReferenceType.class, (u, t) -> GraphQLString);

        // Agency is recursive and referred to by everything
        AgencyType = objectTypeOf(Agency.class);
        register(Agency.class, (u, t) -> AgencyType);

        AttributeType = objectTypeOf(Attribute.class);
        register(Attribute.class, (u, t) -> AttributeType);

        IntervalType = objectTypeOf(Interval.class);
        register(Interval.class, (u, t) -> IntervalType);

        LocationType = objectTypeOf(Location.class);
        register(Location.class, (u, t) -> LocationType);

        ProductType = objectTypeOf(Product.class);
        register(Product.class, (u, t) -> ProductType);

        // Recursive, but seems to be fin
        RelationshipType = objectTypeOf(Relationship.class);
        register(Relationship.class, (u, t) -> RelationshipType);

        StatusCodeType = objectTypeOf(StatusCode.class);
        register(StatusCode.class, (u, t) -> StatusCodeType);

        UnitType = objectTypeOf(Unit.class);
        register(Unit.class, (u, t) -> UnitType);

        ExistentialType = interfaceTypeOf(Existential.class);
        register(Existential.class, (u, t) -> ExistentialType);

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
        buildPhantasm(topLevelQuery, resolved);
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

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
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

    private static void buildPhantasm(Builder topLevelQuery,
                                      Map<FacetRecord, FacetFields> resolved) {
    }
}
