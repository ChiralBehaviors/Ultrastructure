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

package com.chiralbehaviors.CoRE.phantasm.graphql.schemas;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceScalarTypes.GraphQLTimestamp;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceScalarTypes.GraphQLUuid;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.Classification;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreInstance;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.Permission;
import com.chiralbehaviors.CoRE.kernel.phantasm.Role;
import com.chiralbehaviors.CoRE.kernel.phantasm.ThisCoreInstance;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.graphql.EdgeTypeResolver;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessor;
import com.chiralbehaviors.CoRE.phantasm.graphql.ZtypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.CoreUserAdmin;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ExistentialMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.FacetMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.JobMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.CurrentUser;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ExistentialQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.FacetQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobChronologyQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.WorkspaceQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.Aspect;

import graphql.AssertException;
import graphql.annotations.GraphQLAnnotations;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchema {

    public interface Mutations
            extends ExistentialMutations, JobMutations, CoreUserAdmin {
    }

    public interface Queries extends ExistentialQueries, JobQueries,
            JobChronologyQueries, CurrentUser {
    }

    public static interface MetaMutations
            extends ExistentialMutations, FacetMutations {
    }

    public static interface MetaQueries
            extends ExistentialQueries, WorkspaceQueries, FacetQueries {
    }

    public static final String EDGE = "_Edge";

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    public WorkspaceSchema() {
    }

    public GraphQLSchema build(WorkspaceAccessor accessor, Model model,
                               Set<Class<?>> plugins) throws NoSuchMethodException,
                                                      InstantiationException,
                                                      IllegalAccessException {
        PhantasmProcessor processor = new PhantasmProcessor();
        Set<GraphQLType> dictionary = new HashSet<>();
        Map<FacetRecord, FacetFields> resolved = new HashMap<>();
        Product definingProduct = accessor.getDefiningProduct();
        Workspace root = model.wrap(Workspace.class, definingProduct);
        Set<Workspace> aggregate = new HashSet<>();
        gatherImports(root, aggregate);
        GraphQLUnionType.Builder edgeUnionBuilder = GraphQLUnionType.newUnionType();
        edgeUnionBuilder.name(EDGE);
        EdgeTypeResolver edgeTypeResolver = new EdgeTypeResolver();
        edgeUnionBuilder.typeResolver(edgeTypeResolver);
        aggregate.forEach(ws -> {
            WorkspaceScope scope = model.getWorkspaceModel()
                                        .getScoped((Product) ws.getRuleform());
            Deque<FacetRecord> unresolved = FacetFields.initialState(scope.getWorkspace(),
                                                                     model);
            while (!unresolved.isEmpty()) {
                FacetRecord facet = unresolved.pop();
                if (resolved.containsKey(facet)) {
                    continue;
                }
                FacetFields type = new FacetFields(new Aspect(model.create(),
                                                              facet));
                resolved.put(facet, type);
                List<Class<?>> facetPlugins = plugins.stream()
                                                     .filter(plugin -> pluginFor(plugin,
                                                                                 ws,
                                                                                 facet,
                                                                                 scope,
                                                                                 model))
                                                     .collect(Collectors.toList());
                type.resolve(facet, facetPlugins, model, edgeUnionBuilder,
                             edgeTypeResolver, processor)
                    .stream()
                    .filter(auth -> !resolved.containsKey(auth))
                    .forEach(auth -> unresolved.add(auth));
            }
        });
        registerWorkspaceTypes(resolved, processor);
        GraphQLObjectType.Builder topLevelQuery = processor.getObjectBuilder(Queries.class);
        GraphQLObjectType.Builder topLevelMutation = processor.getObjectBuilder(Mutations.class);
        resolved.entrySet()
                .stream()
                .forEach(e -> dictionary.add(e.getValue()
                                              .build(new Aspect(model.create(),
                                                                e.getKey()),
                                                     topLevelQuery,

                                                     topLevelMutation)));
        try {
            GraphQLUnionType edgeUnion = edgeUnionBuilder.build();
            if (!edgeUnion.getTypes()
                          .isEmpty()) {
                dictionary.add(edgeUnion);
            }
        } catch (AssertException e) {
            // only one type, ignore
        }
        return GraphQLSchema.newSchema()
                            .query(topLevelQuery.build())
                            .mutation(topLevelMutation.build())
                            .build(dictionary);
    }

    public GraphQLSchema buildMeta() throws Exception {
        PhantasmProcessor processor = new PhantasmProcessor();
        registerBaseTypes(Collections.emptyMap(), processor);

        GraphQLObjectType.Builder query = processor.getObjectBuilder(MetaQueries.class);
        GraphQLObjectType.Builder mutation = processor.getObjectBuilder(MetaMutations.class);
        JooqSchema jooqSchema = JooqSchema.meta(processor);
        jooqSchema.contributeTo(query, mutation, processor);
        return GraphQLSchema.newSchema()
                            .query(query.build())
                            .mutation(mutation.build())
                            .build(jooqSchema.getTypes());
    }

    private void registerWorkspaceTypes(Map<FacetRecord, FacetFields> resolved,
                                        PhantasmProcessor processor) throws NoSuchMethodException,
                                                                     InstantiationException,
                                                                     IllegalAccessException {
        registerBaseTypes(resolved, processor);

        processor.registerType(new ZtypeFunction(CoreUser.class,
                                                 new GraphQLTypeReference(CoreUser.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(Classification.class,
                                                 new GraphQLTypeReference(Classification.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(CoreInstance.class,
                                                 new GraphQLTypeReference(CoreInstance.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(Permission.class,
                                                 new GraphQLTypeReference(Permission.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(Role.class,
                                                 new GraphQLTypeReference(Role.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(ThisCoreInstance.class,
                                                 new GraphQLTypeReference(ThisCoreInstance.class.getSimpleName())));
        processor.registerType(new ZtypeFunction(Workspace.class,
                                                 new GraphQLTypeReference(Workspace.class.getSimpleName())));

        existentialType(resolved, processor);
    }

    private void registerBaseTypes(Map<FacetRecord, FacetFields> resolved,
                                   PhantasmProcessor processor) throws NoSuchMethodException,
                                                                InstantiationException,
                                                                IllegalAccessException {

        processor.registerType(new ZtypeFunction(double.class, GraphQLFloat));
        processor.registerType(new ZtypeFunction(Double.class, GraphQLFloat));
        processor.registerType(new ZtypeFunction(BigDecimal.class,
                                                 GraphQLFloat));
        processor.registerType(new ZtypeFunction(int.class, GraphQLInt));
        processor.registerType(new ZtypeFunction(UUID.class, GraphQLUuid));
        processor.registerType(new ZtypeFunction(new UUID[0].getClass(),
                                                 new GraphQLList(GraphQLUuid)));
        processor.registerType(new ZtypeFunction(OffsetDateTime.class,
                                                 GraphQLTimestamp));

        GraphQLType existentialType = GraphQLAnnotations.iface(Existential.class);
        processor.registerType(new ZtypeFunction(Existential.class,
                                                 existentialType));
        processor.registerType(new ZtypeFunction(ExistentialRecord.class,
                                                 existentialType));
        GraphQLObjectType agencyType = phantasm(resolved,
                                                processor.getObjectBuilder(Agency.class));
        processor.registerType(new ZtypeFunction(Agency.class, agencyType));

        GraphQLObjectType intervalType = phantasm(resolved,
                                                  processor.getObjectBuilder(Interval.class));
        processor.registerType(new ZtypeFunction(Interval.class, intervalType));

        GraphQLObjectType locationType = phantasm(resolved,
                                                  processor.getObjectBuilder(Location.class));
        processor.registerType(new ZtypeFunction(Location.class, locationType));

        GraphQLObjectType productType = phantasm(resolved,
                                                 processor.getObjectBuilder(Existential.Product.class));
        processor.registerType(new ZtypeFunction(Existential.Product.class,
                                                 productType));

        GraphQLObjectType relationshipType = phantasm(resolved,
                                                      processor.getObjectBuilder(Relationship.class));
        processor.registerType(new ZtypeFunction(Relationship.class,
                                                 relationshipType));

        GraphQLObjectType statusCodeType = phantasm(resolved,
                                                    processor.getObjectBuilder(StatusCode.class));
        processor.registerType(new ZtypeFunction(StatusCode.class,
                                                 statusCodeType));

        GraphQLObjectType unitType = phantasm(resolved,
                                              processor.getObjectBuilder(Unit.class));
        processor.registerType(new ZtypeFunction(Unit.class, unitType));
    }

    private void addPhantasmCast(GraphQLObjectType.Builder typeBuilder,
                                 Entry<FacetRecord, FacetFields> resolved) {
        typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                .name(String.format("as%s",
                                                                    FacetFields.toTypeName(resolved.getKey()
                                                                                                   .getName())))
                                                .description(String.format("Cast to the %s facet",
                                                                           resolved.getKey()
                                                                                   .getName()))
                                                .type(new GraphQLTypeReference(resolved.getValue()
                                                                                       .getName()))
                                                .dataFetcher(env -> {
                                                    Existential existential = (Existential) env.getSource();
                                                    PhantasmCRUD crud = FacetFields.ctx(env);
                                                    crud.cast(existential.getRecord(),
                                                              new Aspect(crud.getModel()
                                                                             .create(),
                                                                         resolved.getKey()));
                                                    return existential;
                                                })
                                                .build());
    }

    private void addPhantasmCast(GraphQLInterfaceType.Builder builder,
                                 Entry<FacetRecord, FacetFields> resolved) {
        builder.field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name(String.format("as%s",
                                                                FacetFields.toTypeName(resolved.getKey()
                                                                                               .getName())))
                                            .description(String.format("Cast to the %s facet",
                                                                       resolved.getKey()
                                                                               .getName()))
                                            .type(new GraphQLTypeReference(resolved.getValue()
                                                                                   .getName()))
                                            .build());
    }

    private GraphQLInterfaceType existentialType(Map<FacetRecord, FacetFields> resolved,
                                                 PhantasmProcessor processor) {
        GraphQLInterfaceType.Builder builder = GraphQLInterfaceType.newInterface();
        builder.name("Existential")
               .description("The Existential interface type")
               .field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("id")
                                            .description("Existential id")
                                            .type(GraphQLUuid)
                                            .build())
               .field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("name")
                                            .description("Existential name")
                                            .type(GraphQLString)
                                            .build())
               .field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("description")
                                            .description("Existential description")
                                            .type(GraphQLString)
                                            .build())
               .field(GraphQLFieldDefinition.newFieldDefinition()
                                            .name("updatedBy")
                                            .description("Agency that updated the Existential")
                                            .type(new GraphQLTypeReference("Agency"))
                                            .build())
               .typeResolver(processor);

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
                                       GraphQLObjectType.Builder objectBuilder) {
        resolved.entrySet()
                .forEach(e -> addPhantasmCast(objectBuilder, e));
        return objectBuilder.build();
    }

    private boolean pluginFor(Class<?> plugin, Workspace ws, FacetRecord facet,
                              WorkspaceScope scope, Model model) {
        Plugin annotation = plugin.getAnnotation(Plugin.class);
        assert annotation != null;
        com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet facetAnnotation = annotation.value()
                                                                                             .getAnnotation(com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet.class);
        if (ws.getRuleform()
              .getId()
              .equals(WorkspaceAccessor.uuidOf(facetAnnotation.workspace()))) {
            FacetRecord declaration;
            try {
                FacetRecord lookup = scope.lookup(ReferenceType.Facet,
                                                  facetAnnotation.key());
                declaration = model.records()
                                   .findFacetRecord(lookup.getId());
            } catch (Exception e) {
                throw new IllegalArgumentException(annotation.value()
                                                             .getName()
                                                   + " Facet annotation incomplete",
                                                   e);
            }
            return facet.equals(declaration);

        }
        return false;
    }
}
