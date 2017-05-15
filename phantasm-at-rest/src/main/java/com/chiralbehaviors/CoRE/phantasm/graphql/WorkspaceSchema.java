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

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLUuid;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;

import java.math.BigDecimal;
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
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.CoreUserAdmin;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.ExistentialMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.JobMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.CurrentUser;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.ExistentialQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobChronologyQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.queries.JobQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.JobChronology;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.Aspect;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
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

    public interface Queries extends ExistentialQueries, CurrentUser,
            JobQueries, JobChronologyQueries {
    }

    public static final String EDGE = "_Edge";

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

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
        Set<GraphQLType> dictionary = new HashSet<>();
        Map<FacetRecord, FacetFields> resolved = new HashMap<>();
        Product definingProduct = accessor.getDefiningProduct();
        Workspace root = model.wrap(Workspace.class, definingProduct);
        Set<Class<?>> plugins = reflections.getTypesAnnotatedWith(Plugin.class);
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
                             edgeTypeResolver)
                    .stream()
                    .filter(auth -> !resolved.containsKey(auth))
                    .forEach(auth -> unresolved.add(auth));
            }
        });
        registerTypes(resolved);
        Builder topLevelQuery = PhantasmProcessor.getSingleton()
                                                 .getObjectBuilder(Queries.class);
        Builder topLevelMutation = PhantasmProcessor.getSingleton()
                                                    .getObjectBuilder(Mutations.class);
        GraphQLSchema schema;
        resolved.entrySet()
                .stream()
                .forEach(e -> dictionary.add(e.getValue()
                                              .build(new Aspect(model.create(),
                                                                e.getKey()),
                                                     topLevelQuery,
                                                     topLevelMutation)));

        GraphQLUnionType edgeUnion = edgeUnionBuilder.build();
        if (!edgeUnion.getTypes()
                      .isEmpty()) {
            dictionary.add(edgeUnion);
        }
        schema = GraphQLSchema.newSchema()
                              .query(topLevelQuery.build())
                              .mutation(topLevelMutation.build())
                              .build(dictionary);
        return schema;
    }

    public GraphQLSchema buildMeta() throws Exception {
        registerTypes(Collections.emptyMap());

        GraphQLObjectType.Builder query = PhantasmProcessor.getSingleton()
                                                           .getObjectBuilder(MetaContext.MetaQueries.class);
        GraphQLObjectType.Builder mutation = PhantasmProcessor.getSingleton()
                                                              .getObjectBuilder(MetaContext.MetaMutations.class);
        JooqSchema jooqSchema = new JooqSchema();
        jooqSchema.contributeTo(query, mutation);
        return GraphQLSchema.newSchema()
                            .query(query.build())
                            .mutation(mutation.build())
                            .build(jooqSchema.getTypes());
    }

    public void registerTypes(Map<FacetRecord, FacetFields> resolved) throws NoSuchMethodException,
                                                                      InstantiationException,
                                                                      IllegalAccessException {

        PhantasmProcessor.register(new ZtypeFunction(double.class,
                                                     GraphQLFloat));
        PhantasmProcessor.register(new ZtypeFunction(Double.class,
                                                     GraphQLFloat));
        PhantasmProcessor.register(new ZtypeFunction(BigDecimal.class,
                                                     GraphQLFloat));
        PhantasmProcessor.register(new ZtypeFunction(int.class, GraphQLInt));
        PhantasmProcessor.register(new ZtypeFunction(UUID.class, GraphQLUuid));

        GraphQLType existentialType = PhantasmProcessor.iface(Existential.class);
        PhantasmProcessor.register(new ZtypeFunction(Existential.class,
                                                     existentialType));
        PhantasmProcessor.register(new ZtypeFunction(ExistentialRecord.class,
                                                     existentialType));
        GraphQLObjectType agencyType = phantasm(resolved,
                                                PhantasmProcessor.getSingleton()
                                                                 .getObjectBuilder(Agency.class));
        PhantasmProcessor.register(new ZtypeFunction(Agency.class, agencyType));

        GraphQLObjectType attrType = phantasm(resolved,
                                              PhantasmProcessor.getSingleton()
                                                               .getObjectBuilder(Attribute.class));
        PhantasmProcessor.register(new ZtypeFunction(Attribute.class,
                                                     attrType));

        GraphQLObjectType intervalType = phantasm(resolved,
                                                  PhantasmProcessor.getSingleton()
                                                                   .getObjectBuilder(Interval.class));
        PhantasmProcessor.register(new ZtypeFunction(Interval.class,
                                                     intervalType));

        GraphQLObjectType locationType = phantasm(resolved,
                                                  PhantasmProcessor.getSingleton()
                                                                   .getObjectBuilder(Location.class));
        PhantasmProcessor.register(new ZtypeFunction(Location.class,
                                                     locationType));

        GraphQLObjectType productType = phantasm(resolved,
                                                 PhantasmProcessor.getSingleton()
                                                                  .getObjectBuilder(Existential.Product.class));
        PhantasmProcessor.register(new ZtypeFunction(Existential.Product.class,
                                                     productType));

        GraphQLObjectType relationshipType = phantasm(resolved,
                                                      PhantasmProcessor.getSingleton()
                                                                       .getObjectBuilder(Relationship.class));
        PhantasmProcessor.register(new ZtypeFunction(Relationship.class,
                                                     relationshipType));

        GraphQLObjectType statusCodeType = phantasm(resolved,
                                                    PhantasmProcessor.getSingleton()
                                                                     .getObjectBuilder(StatusCode.class));
        PhantasmProcessor.register(new ZtypeFunction(StatusCode.class,
                                                     statusCodeType));

        GraphQLObjectType unitType = phantasm(resolved,
                                              PhantasmProcessor.getSingleton()
                                                               .getObjectBuilder(Unit.class));
        PhantasmProcessor.register(new ZtypeFunction(Unit.class, unitType));

        GraphQLObjectType jobType = PhantasmProcessor.getSingleton()
                                                     .getObject(Job.class);
        PhantasmProcessor.register(new ZtypeFunction(Job.class, jobType));

        GraphQLObjectType chronType = PhantasmProcessor.getSingleton()
                                                       .getObject(JobChronology.class);
        PhantasmProcessor.register(new ZtypeFunction(JobChronology.class,
                                                     chronType));
        existentialType(resolved);
    }

    private void addPhantasmCast(Builder typeBuilder,
                                 Entry<FacetRecord, FacetFields> resolved) {
        typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                .name(String.format("as%s",
                                                                    WorkspacePresentation.toTypeName(resolved.getKey()
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
                                                                WorkspacePresentation.toTypeName(resolved.getKey()
                                                                                                         .getName())))
                                            .description(String.format("Cast to the %s facet",
                                                                       resolved.getKey()
                                                                               .getName()))
                                            .type(new GraphQLTypeReference(resolved.getValue()
                                                                                   .getName()))
                                            .build());
    }

    private GraphQLInterfaceType existentialType(Map<FacetRecord, FacetFields> resolved) {
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
               .typeResolver(PhantasmProcessor.getSingleton());

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

    private boolean pluginFor(Class<?> plugin, Workspace ws, FacetRecord facet,
                              WorkspaceScope scope, Model model) {
        Plugin annotation = plugin.getAnnotation(Plugin.class);
        assert annotation != null;
        com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet facetAnnotation = annotation.value()
                                                                                             .getAnnotation(com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet.class);
        if (ws.getRuleform()
              .getId()
              .equals(WorkspaceAccessor.uuidOf(facetAnnotation.workspace()))) {
            FacetRecord declaration = model.getPhantasmModel()
                                           .getFacetDeclaration(scope.lookup(facetAnnotation.classifier()),
                                                                scope.lookup(facetAnnotation.classification()));
            return facet.equals(declaration);

        }
        return false;
    }
}
