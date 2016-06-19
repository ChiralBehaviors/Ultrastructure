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

import static com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessing.object;
import static com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessing.objectBuilder;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
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
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.CurrentUser;
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
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;

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
public class WorkspaceSchema {

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

    public interface Queries extends CurrentUser, ExistentialQueries,
            JobQueries, JobChronologyQueries {
    }

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    private final WorkspaceTypeFunction typeFunction = new WorkspaceTypeFunction();

    public WorkspaceSchema() {
    }

    public GraphQLSchema build(WorkspaceAccessor accessor,
                               Model model) throws NoSuchMethodException,
                                            InstantiationException,
                                            IllegalAccessException {
        return build(accessor, model, new Reflections());
    }

    public GraphQLSchema build(WorkspaceAccessor accessor, Model model,
                               Reflections reflections) throws NoSuchMethodException,
                                                        InstantiationException,
                                                        IllegalAccessException {
        Map<FacetRecord, FacetFields> resolved = new HashMap<>();
        Product definingProduct = accessor.getDefiningProduct();
        Workspace root = model.wrap(Workspace.class, definingProduct);
        Set<Class<?>> plugins = reflections.getTypesAnnotatedWith(Plugin.class);
        Set<Workspace> aggregate = new HashSet<>();
        gatherImports(root, aggregate);
        aggregate.forEach(ws -> {
            Deque<FacetRecord> unresolved = FacetFields.initialState(model.getWorkspaceModel()
                                                                          .getScoped((Product) ws.getRuleform())
                                                                          .getWorkspace(),
                                                                     model);
            while (!unresolved.isEmpty()) {
                FacetRecord facet = unresolved.pop();
                if (resolved.containsKey(facet)) {
                    continue;
                }
                FacetFields type = new FacetFields(facet);
                resolved.put(facet, type);
                List<Class<?>> facetPlugins = plugins.stream()
                                                     .filter(plugin -> pluginFor(plugin,
                                                                                 ws,
                                                                                 facet))
                                                     .collect(Collectors.toList());
                type.resolve(facet, facetPlugins, model, typeFunction)
                    .stream()
                    .filter(auth -> !resolved.containsKey(auth))
                    .forEach(auth -> unresolved.add(auth));
            }
        });
        registerTypes(resolved);
        Builder topLevelQuery = objectBuilder(Queries.class, typeFunction,
                                              typeFunction);
        Builder topLevelMutation = objectBuilder(Mutations.class, typeFunction,
                                                 typeFunction);
        GraphQLSchema schema;
        resolved.entrySet()
                .stream()
                .forEach(e -> e.getValue()
                               .build(new Aspect(model.create(), e.getKey()),
                                      topLevelQuery, topLevelMutation));
        topLevelQuery.field(newFieldDefinition().name("currentUser")
                                                .type(new GraphQLTypeReference("CoREUser"))
                                                .dataFetcher(env -> ctx(env).getCurrentPrincipal()
                                                                            .getPrincipal())
                                                .build());
        schema = GraphQLSchema.newSchema()
                              .query(topLevelQuery.build())
                              .mutation(topLevelMutation.build())
                              .build();
        return schema;
    }

    public GraphQLSchema buildMeta() throws Exception {
        registerTypes(Collections.emptyMap());
        return GraphQLSchema.newSchema()
                            .query(object(MetaQueries.class, typeFunction,
                                          typeFunction))
                            .mutation(object(MetaMutations.class, typeFunction,
                                             typeFunction))
                            .build();
    }

    private void addPhantasmCast(Builder typeBuilder,
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
                                                    Existential existential = (Existential) env.getSource();
                                                    PhantasmCRUD crud = FacetFields.ctx(env);
                                                    crud.cast(existential.getRecord(),
                                                              new Aspect(crud.getModel()
                                                                             .create(),
                                                                         entry.getKey()));
                                                    return existential;
                                                })
                                                .build());
    }

    private void addPhantasmCast(graphql.schema.GraphQLInterfaceType.Builder builder,
                                 Entry<FacetRecord, FacetFields> entry) {
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name(String.format("as%s",
                                                                WorkspacePresentation.toTypeName(entry.getKey()
                                                                                                      .getName())))
                                            .description(String.format("Cast to the %s facet",
                                                                       entry.getKey()
                                                                            .getName()))
                                            .type(new GraphQLTypeReference(entry.getValue()
                                                                                .getName()))
                                            .build());
    }

    private GraphQLInterfaceType existentialType(Map<FacetRecord, FacetFields> resolved) {
        graphql.schema.GraphQLInterfaceType.Builder builder = graphql.schema.GraphQLInterfaceType.newInterface();
        builder.name("Existential");
        builder.description("The Existential interface type");
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("id")
                                            .description("Existential id")
                                            .type(GraphQLString)
                                            .build());
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("name")
                                            .description("Existential name")
                                            .type(GraphQLString)
                                            .build());
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("description")
                                            .description("Existential description")
                                            .type(GraphQLString)
                                            .build());
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("updatedBy")
                                            .description("Agency that updated the Existential")
                                            .type(new GraphQLTypeReference("Agency"))
                                            .build());
        builder.typeResolver(typeFunction);

        resolved.entrySet()
                .forEach(e -> addPhantasmCast(builder, e));
        return builder.build();
    }

    private void gatherImports(Workspace workspace, Set<Workspace> traversed) {
        if (traversed.contains(workspace)) {
            return;
        }
        traversed.add(workspace);
        workspace.getImports()
                 .forEach(w -> gatherImports(w, traversed));
    }

    private GraphQLObjectType phantasm(Map<FacetRecord, FacetFields> resolved,
                                       Builder objectBuilder) {
        resolved.entrySet()
                .forEach(e -> addPhantasmCast(objectBuilder, e));
        return objectBuilder.build();
    }

    private boolean pluginFor(Class<?> plugin, Workspace ws,
                              FacetRecord facet) {
        return false;
    }

    private void registerTypes(Map<FacetRecord, FacetFields> resolved) throws NoSuchMethodException,
                                                                       InstantiationException,
                                                                       IllegalAccessException {

        GraphQLInterfaceType et = existentialType(resolved);
        typeFunction.register(Existential.class, (u, t) -> et);

        typeFunction.register(Double.class, (u, t) -> GraphQLFloat);
        typeFunction.register(UUID.class, (u, t) -> GraphQLString);
        typeFunction.register(ValueType.class, (u, t) -> GraphQLString);
        typeFunction.register(Cardinality.class, (u, t) -> GraphQLString);
        typeFunction.register(ReferenceType.class, (u, t) -> GraphQLString);

        GraphQLObjectType agencyType = phantasm(resolved,
                                                objectBuilder(Agency.class,
                                                              typeFunction,
                                                              typeFunction));
        typeFunction.register(Agency.class, (u, t) -> agencyType);

        GraphQLObjectType attrType = phantasm(resolved,
                                              objectBuilder(Attribute.class,
                                                            typeFunction,
                                                            typeFunction));
        typeFunction.register(Attribute.class, (u, t) -> attrType);

        GraphQLObjectType intervalType = phantasm(resolved,
                                                  objectBuilder(Interval.class,
                                                                typeFunction,
                                                                typeFunction));
        typeFunction.register(Interval.class, (u, t) -> intervalType);

        GraphQLObjectType locationType = phantasm(resolved,
                                                  objectBuilder(Location.class,
                                                                typeFunction,
                                                                typeFunction));
        typeFunction.register(Location.class, (u, t) -> locationType);

        GraphQLObjectType productType = phantasm(resolved,
                                                 objectBuilder(com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product.class,
                                                               typeFunction,
                                                               typeFunction));
        typeFunction.register(com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product.class,
                              (u, t) -> productType);

        GraphQLObjectType relationshipType = phantasm(resolved,
                                                      objectBuilder(Relationship.class,
                                                                    typeFunction,
                                                                    typeFunction));
        typeFunction.register(Relationship.class, (u, t) -> relationshipType);

        GraphQLObjectType statusCodeType = phantasm(resolved,
                                                    objectBuilder(StatusCode.class,
                                                                  typeFunction,
                                                                  typeFunction));
        typeFunction.register(StatusCode.class, (u, t) -> statusCodeType);

        GraphQLObjectType unitType = phantasm(resolved,
                                              objectBuilder(Unit.class,
                                                            typeFunction,
                                                            typeFunction));
        typeFunction.register(Unit.class, (u, t) -> unitType);

        GraphQLObjectType facteType = object(Facet.class, typeFunction,
                                             typeFunction);
        typeFunction.register(Facet.class, (u, t) -> {
            return facteType;
        });

        GraphQLObjectType attrAuthType = object(AttributeAuthorization.class,
                                                typeFunction, typeFunction);
        typeFunction.register(AttributeAuthorization.class, (u, t) -> {
            return attrAuthType;
        });

        GraphQLObjectType csType = object(ChildSequencing.class, typeFunction,
                                          typeFunction);
        typeFunction.register(ChildSequencing.class, (u, t) -> {
            return csType;
        });

        GraphQLObjectType job = object(Job.class, typeFunction, typeFunction);
        typeFunction.register(Job.class, (u, t) -> job);

        GraphQLObjectType metaType = object(MetaProtocol.class, typeFunction,
                                            typeFunction);
        typeFunction.register(MetaProtocol.class, (u, t) -> {
            return metaType;
        });

        GraphQLObjectType netAuthType = object(NetworkAuthorization.class,
                                               typeFunction, typeFunction);
        typeFunction.register(NetworkAuthorization.class, (u, t) -> {
            return netAuthType;
        });

        GraphQLObjectType psType = object(ParentSequencing.class, typeFunction,
                                          typeFunction);
        typeFunction.register(ParentSequencing.class, (u, t) -> {
            return psType;
        });

        GraphQLObjectType protocolType = object(Protocol.class, typeFunction,
                                                typeFunction);
        typeFunction.register(Protocol.class, (u, t) -> {
            return protocolType;
        });

        GraphQLObjectType ssType = object(SelfSequencing.class, typeFunction,
                                          typeFunction);
        typeFunction.register(SelfSequencing.class, (u, t) -> {
            return ssType;
        });

        GraphQLObjectType sibSeqType = object(SiblingSequencing.class,
                                              typeFunction, typeFunction);
        typeFunction.register(SiblingSequencing.class, (u, t) -> {
            return sibSeqType;
        });

        GraphQLObjectType scsType = object(StatusCodeSequencing.class,
                                           typeFunction, typeFunction);
        typeFunction.register(StatusCodeSequencing.class, (u, t) -> {
            return scsType;
        });

        GraphQLObjectType netAttAuthType = object(NetworkAttributeAuthorization.class,
                                                  typeFunction, typeFunction);
        typeFunction.register(NetworkAttributeAuthorization.class, (u, t) -> {
            return netAttAuthType;
        });

        GraphQLObjectType chronType = object(JobChronology.class, typeFunction,
                                             typeFunction);
        typeFunction.register(JobChronology.class, (u, t) -> {
            return chronType;
        });
    }
}
