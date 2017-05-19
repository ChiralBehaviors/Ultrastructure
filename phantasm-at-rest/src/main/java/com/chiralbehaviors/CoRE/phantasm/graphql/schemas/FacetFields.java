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

package com.chiralbehaviors.CoRE.phantasm.graphql.schemas;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLUuid;
import static graphql.Scalars.GraphQLBigDecimal;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.graphql.EdgeTypeResolver;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmContext.Traversal;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmInitializer;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessor;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Initializer;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle;
import com.chiralbehaviors.CoRE.utils.English;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;

/**
 * Cannonical tranform of Phantasm metadata into GraphQL metadata. Provides
 * framework for Phantasm Plugin model;
 *
 * @author hhildebrand
 *
 */
public class FacetFields extends Phantasmagoria {

    private static final String _EDGE              = "_edge";
    private static final String _EXT               = "_ext";
    private static final String ADD_TEMPLATE       = "add%s";
    private static final String APPLY_MUTATION     = "apply%s";
    private static final String AT_RULEFORM        = "@ruleform";
    private static final String CREATE_MUTATION    = "create%s";
    private static final String CREATE_TYPE        = "%sCreate";
    private static final String EXISTENTIAL        = "Existential";
    private static final String ID                 = "id";
    private static final String IDS                = "ids";
    private static final String IMMEDIATE_TEMPLATE = "immediate%s";
    private static final String REMOVE_MUTATION    = "remove%s";
    private static final String REMOVE_TEMPLATE    = "remove%s";
    @SuppressWarnings("unused")
    private static final String SET_INDEX_TEMPLATE = "set%sIndex";
    @SuppressWarnings("unused")
    private static final String SET_KEY_TEMPLATE   = "set%sKey";
    private static final String SET_TEMPLATE       = "set%s";
    private static final String STATE              = "state";
    private static final String UPDATE_MUTATION    = "update%s";
    private static final String UPDATE_TYPE        = "%sUpdate";

    public static URLClassLoader configureExecutionScope(List<String> urlStrings) {
        ClassLoader parent = Thread.currentThread()
                                   .getContextClassLoader();
        if (parent == null) {
            parent = PhantasmBundle.class.getClassLoader();
        }
        List<URL> urls = new ArrayList<>();
        for (String url : urlStrings) {
            URL resolved;
            try {
                resolved = new URL(url);
            } catch (MalformedURLException e) {
                try {
                    resolved = new File(url).toURI()
                                            .toURL();
                } catch (MalformedURLException e1) {
                    PhantasmBundle.log.error("Invalid configured execution scope url: {}",
                                             url, e1);
                    throw new IllegalArgumentException(String.format("Invalid configured execution scope url: %s",
                                                                     url),
                                                       e1);
                }
            }
            urls.add(resolved);
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    public static PhantasmContext ctx(DataFetchingEnvironment env) {
        return (PhantasmContext) env.getContext();
    }

    public static Deque<FacetRecord> initialState(WorkspaceAccessor workspace,
                                                  Model model) {
        Product definingProduct = workspace.getDefiningProduct();
        Deque<FacetRecord> unresolved = new ArrayDeque<>();
        unresolved.addAll(model.getPhantasmModel()
                               .getFacets(definingProduct));
        return unresolved;
    }

    private graphql.schema.GraphQLInputObjectType.Builder                  createTypeBuilder;
    private List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers   = new ArrayList<>();
    private String                                                         name;
    private Set<FacetRecord>                                               references     = new HashSet<>();
    private Builder                                                        typeBuilder;
    private Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>>     updateTemplate = new HashMap<>();
    private graphql.schema.GraphQLInputObjectType.Builder                  updateTypeBuilder;

    public FacetFields(Aspect facet) {
        super(facet);
        name = WorkspacePresentation.toTypeName(facet.getName());
        String notes = facet.getFacet()
                            .getNotes();
        typeBuilder = newObject().name(name)
                                 .description(notes);
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                name))
                                            .description(notes);
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                name))
                                            .description(notes);
    }

    public GraphQLObjectType build(Aspect aspect, Builder query,
                                   Builder mutation) {
        GraphQLObjectType type = typeBuilder.build();
        query.field(instance());
        query.field(instances());
        mutation.field(createInstance());
        mutation.field(createInstances());
        mutation.field(apply());
        GraphQLFieldDefinition update = update();
        if (update != null) {
            mutation.field(update);
        }
        update = updateInstances();
        if (update != null) {
            mutation.field(update);
        }
        mutation.field(remove());
        clear();
        return type;
    }

    public String getName() {
        return name;
    }

    public GraphQLTypeReference referenceToType(Aspect facet) {
        return new GraphQLTypeReference(WorkspacePresentation.toTypeName(facet.getName()));
    }

    public Set<FacetRecord> resolve(FacetRecord facet, List<Class<?>> plugins,
                                    Model model, GraphQLUnionType.Builder union,
                                    EdgeTypeResolver edgeTypeResolver,
                                    PhantasmProcessor processor) {
        traverse(model);
        buildEdges(edgeTypeResolver, union);
        build();

        addPlugins(plugins, processor);
        return references;
    }

    @Override
    public String toString() {
        return "FacetFields [" + name + "]";
    }

    @Override
    public void traverse(Model model) {
        super.traverse(model);
        childAuthorizations.values()
                           .forEach(auth -> references.add(auth.getChild()
                                                               .getFacet()));
        singularAuthorizations.values()
                              .forEach(auth -> references.add(auth.getChild()
                                                                  .getFacet()));
    }

    private void addChild(NetworkAuthorization auth) {
        String add = String.format(ADD_TEMPLATE,
                                   Phantasmagoria.capitalized(auth.getFieldName()));
        GraphQLInputObjectField field = newInputObjectField().type(GraphQLUuid)
                                                             .name(add)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                     auth,
                                                     crud.lookup((UUID) update.get(add))));
    }

    @SuppressWarnings("unchecked")
    private void addChildren(NetworkAuthorization auth) {
        String addChildren = String.format(ADD_TEMPLATE,
                                           Phantasmagoria.capitalized(auth.plural()));
        GraphQLInputObjectField field = newInputObjectField().type(new GraphQLList(GraphQLUuid))
                                                             .name(addChildren)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                        auth,
                                                        crud.lookupList((List<UUID>) update.get(addChildren))));
    }

    private void addPlugins(List<Class<?>> plugins,
                            PhantasmProcessor processor) {
        plugins.forEach(plugin -> {
            initializers.addAll(FacetFields.processPlugin(plugin, typeBuilder,
                                                          processor));
        });
    }

    private GraphQLFieldDefinition apply() {
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detached = initializers;
        return newFieldDefinition().name(String.format(APPLY_MUTATION, name))
                                   .description(String.format("Apply %s facet to the instance",
                                                              name))
                                   .type(referenceToType(facet))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLUuid)
                                                          .build())
                                   .dataFetcher(env -> {
                                       ExistentialRuleform ruleform = ctx(env).lookup(env.getArgument(ID));
                                       PhantasmCRUD crud = ctx(env);
                                       return crud.apply(facet, ruleform,
                                                         instance -> {
                                                             detached.forEach(initializer -> initializer.accept(env,
                                                                                                                instance));
                                                             return ruleform;
                                                         });
                                   })
                                   .build();
    }

    private void build() {
        typeBuilder.field(newFieldDefinition().type(new GraphQLTypeReference(WorkspaceSchema.EDGE))
                                              .name(_EDGE)
                                              .description("The currently traversed edge to this instance")
                                              .dataFetcher(env -> ctx(env).getCurrentEdge())
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLUuid)
                                              .name(ID)
                                              .description("The id of the facet instance")
                                              .dataFetcher(env -> ((Phantasm) env.getSource()).getRuleform()
                                                                                              .getId())
                                              .build());
        typeBuilder.field(newFieldDefinition().type(new GraphQLTypeReference(EXISTENTIAL))
                                              .name(_EXT)
                                              .description("Cast the instance as an Existential")
                                              .dataFetcher(env -> Existential.wrap((ExistentialRecord) env.getSource()))
                                              .build());

        updateTypeBuilder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLUuid))
                                                     .name(ID)
                                                     .description(String.format("the id of the updated %s",
                                                                                name))
                                                     .build());
        attributes.values()
                  .forEach(attr -> visit(attr));
        childAuthorizations.values()
                           .forEach(auth -> visitChildren(auth));
        singularAuthorizations.values()
                              .forEach(auth -> visitSingular(auth));
    }

    private void buildEdge(NetworkAuthorization auth,
                           EdgeTypeResolver edgeTypeResolver,
                           GraphQLUnionType.Builder union) {
        String facetName = WorkspacePresentation.toFieldName(name);
        Builder edgeTypeBuilder = newObject().name(String.format("_%s_%s",
                                                                 facetName,
                                                                 auth.getFieldName()))
                                             .description(String.format("The edge \"%s\" from facet \"%s\"",
                                                                        auth.getFieldName(),
                                                                        facetName));
        auth.getAttributes()
            .forEach(attr -> {
                Attribute attribute = attr.getAttribute();
                String fieldName = WorkspacePresentation.toFieldName(attr.getAttribute()
                                                                         .getName());
                GraphQLOutputType type = typeOf(attribute);
                edgeTypeBuilder.field(newFieldDefinition().type(type)
                                                          .name(fieldName)
                                                          .description(attribute.getDescription())
                                                          .dataFetcher(env -> {
                                                              PhantasmContext ctx = ctx(env);
                                                              Traversal edge = (Traversal) env.getSource();
                                                              if (edge == null
                                                                  || !edge.auth.equals(auth)) {
                                                                  return null;
                                                              }
                                                              Object value = ctx.getAttributeValue(facet,
                                                                                                   edge.parent,
                                                                                                   attr,
                                                                                                   edge.child);
                                                              return resolve(attribute,
                                                                             value);
                                                          })
                                                          .build());
            });
        GraphQLObjectType edgeType = edgeTypeBuilder.build();
        if (!edgeType.getFieldDefinitions()
                     .isEmpty()) {
            union.possibleType(edgeType);
            edgeTypeResolver.register(auth, edgeType);
        }
    }

    private void buildEdges(EdgeTypeResolver edgeTypeResolver,
                            GraphQLUnionType.Builder union) {
        childAuthorizations.values()
                           .forEach(auth -> buildEdge(auth, edgeTypeResolver,
                                                      union));
        singularAuthorizations.values()
                              .forEach(auth -> buildEdge(auth, edgeTypeResolver,
                                                         union));

    }

    private void clear() {
        references = null;
        typeBuilder = null;
        updateTypeBuilder = null;
        createTypeBuilder = null;
        updateTemplate = null;
        initializers = null;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstance() {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdate = updateTemplate;
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detachedConstructors = initializers;
        graphql.schema.GraphQLFieldDefinition.Builder field = newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                                                                      name))
                                                                                  .description(String.format("Create an instance of %s",
                                                                                                             name));

        GraphQLInputObjectType createType = createTypeBuilder.build();
        if (!createType.getFields()
                       .isEmpty()) {
            field.argument(newArgument().name(STATE)
                                        .description("the initial state to apply to the new instance")
                                        .type(new GraphQLNonNull(createType))
                                        .build());
        }
        return field.type(new GraphQLTypeReference(name))
                    .dataFetcher(env -> {
                        Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                        PhantasmCRUD crud = ctx(env);
                        ExistentialRuleform constructed = crud.createInstance(facet,
                                                                              null,
                                                                              null,
                                                                              instance -> {
                                                                                  update(instance,
                                                                                         createState,
                                                                                         crud,
                                                                                         detachedUpdate);
                                                                              });
                        if (!detachedConstructors.isEmpty()) {
                            if (((PhantasmCRUD) env.getContext()).checkInvoke(facet,
                                                                              constructed)) {
                                detachedConstructors.forEach(initializer -> initializer.accept(env,
                                                                                               constructed));
                            }
                        }
                        return constructed;

                    })
                    .build();
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstances() {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdate = updateTemplate;
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detachedConstructors = initializers;
        graphql.schema.GraphQLFieldDefinition.Builder field = newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                                                                      English.plural(name)))
                                                                                  .description(String.format("Create instances of %s",
                                                                                                             name))
                                                                                  .type(new GraphQLList(new GraphQLTypeReference(name)));

        GraphQLInputObjectType createType = createTypeBuilder.build();
        if (!createType.getFields()
                       .isEmpty()) {
            field.argument(newArgument().name(STATE)
                                        .description("the initial state to apply to the new instance")
                                        .type(new GraphQLNonNull(new GraphQLList(createType)))
                                        .build());
        }
        return field.dataFetcher(env -> {
            List<Map<String, Object>> createStates = (List<Map<String, Object>>) env.getArgument(STATE);
            PhantasmCRUD crud = ctx(env);
            return createStates.stream()
                               .map(createState -> {
                                   ExistentialRuleform constructed = crud.createInstance(facet,
                                                                                         null,
                                                                                         null,
                                                                                         instance -> {
                                                                                             update(instance,
                                                                                                    createState,
                                                                                                    crud,
                                                                                                    detachedUpdate);
                                                                                         });
                                   if (!detachedConstructors.isEmpty()) {
                                       if (((PhantasmCRUD) env.getContext()).checkInvoke(facet,
                                                                                         constructed)) {
                                           detachedConstructors.forEach(initializer -> initializer.accept(env,
                                                                                                          constructed));
                                       }
                                   }
                                   return constructed;
                               })
                               .collect(Collectors.toList());
        })
                    .build();
    }

    private GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(Introspector.decapitalize(name))
                                   .type(new GraphQLTypeReference(name))
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(new GraphQLNonNull(GraphQLUuid))
                                                          .build())
                                   .dataFetcher(env -> ctx(env).lookup(env.getArgument(ID)))
                                   .build();
    }

    private GraphQLFieldDefinition instances() {
        return newFieldDefinition().name(Introspector.decapitalize(English.plural(name)))
                                   .description(String.format("Return the instances of %s",
                                                              name))
                                   .argument(newArgument().name(IDS)
                                                          .description("list of ids of the instances to query")
                                                          .type(new GraphQLList(GraphQLUuid))
                                                          .build())
                                   .type(new GraphQLList(referenceToType(facet)))
                                   .dataFetcher(context -> {
                                       @SuppressWarnings("unchecked")
                                       List<UUID> ids = (List<UUID>) context.getArgument(ID);
                                       return (ids != null ? ctx(context).lookupList(ids)
                                                           : ctx(context).getInstances(facet)).stream()
                                                                                              .collect(Collectors.toList());
                                   })
                                   .build();

    }

    private GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(String.format(REMOVE_MUTATION, name))
                                   .type(referenceToType(facet))
                                   .description(String.format("Remove the %s facet from the instance",
                                                              name))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLUuid)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).remove(facet,
                                                                       ctx(env).lookup(env.getArgument(ID)),
                                                                       true))
                                   .build();
    }

    private void removeChild(NetworkAuthorization auth) {
        String remove = String.format(REMOVE_TEMPLATE,
                                      Phantasmagoria.capitalized(auth.getFieldName()));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLUuid)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                        auth,
                                                        crud.lookup((UUID) update.get(remove))));
    }

    @SuppressWarnings("unchecked")
    private void removeChildren(NetworkAuthorization auth) {
        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              Phantasmagoria.capitalized(auth.plural()));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLUuid))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                           auth,
                                                           crud.lookupList((List<UUID>) update.get(removeChildren))));
    }

    private Object resolve(Attribute attribute, Object value) {
        if (value == null) {
            return null;
        }
        switch (attribute.getValueType()) {
            case Numeric:
                // GraphQL does not have a NUMERIC return type, so convert to float - ugly
                return ((BigDecimal) value).floatValue();
            case Timestamp:
            case JSON:
                // GraphQL does not have a generic map or timestamp return type, so stringify it.
                try {
                    return new ObjectMapper().writeValueAsString(value);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write json value",
                                                    e);
                }
            default:
                return value;
        }
    }

    @SuppressWarnings("unchecked")
    private void setChildren(NetworkAuthorization auth) {
        String setter = String.format(SET_TEMPLATE,
                                      Phantasmagoria.capitalized(auth.plural()));
        GraphQLInputObjectField field = newInputObjectField().type(new GraphQLList(GraphQLUuid))
                                                             .name(setter)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                        auth,
                                                        crud.lookupList((List<UUID>) update.get(setter))));
    }

    private GraphQLOutputType typeOf(Attribute attribute) {
        GraphQLOutputType type = null;
        switch (attribute.getValueType()) {
            case Binary:
                type = GraphQLString; // encoded binary
                break;
            case Boolean:
                type = GraphQLBoolean;
                break;
            case Integer:
                type = GraphQLInt;
                break;
            case Numeric:
                type = GraphQLFloat;
                break;
            case Text:
                type = GraphQLString;
                break;
            case Timestamp:
                type = GraphQLString;
                break;
            case JSON:
                type = WorkspsacScalarTypes.GraphQLJson;
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition update() {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdateTemplate = updateTemplate;
        GraphQLInputObjectType updateType = updateTypeBuilder.build();
        if (updateType.getFields()
                      .isEmpty()) {
            return null;
        }
        return newFieldDefinition().name(String.format(UPDATE_MUTATION, name))
                                   .type(referenceToType(facet))
                                   .description(String.format("Update the instance of %s",
                                                              name))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(updateType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       ExistentialRuleform ruleform = (ExistentialRuleform) crud.lookup((UUID) updateState.get(ID));
                                       if (ruleform == null) {
                                           return null;
                                       }
                                       update(ruleform, updateState, crud,
                                              detachedUpdateTemplate);
                                       return ruleform;
                                   })
                                   .build();
    }

    private void update(ExistentialRuleform ruleform,
                        Map<String, Object> updateState, PhantasmCRUD crud,
                        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> updateTemplate) {
        updateState.put(AT_RULEFORM, ruleform);
        updateState.keySet()
                   .stream()
                   .filter(field -> !field.equals(ID)
                                    && !field.equals(AT_RULEFORM)
                                    && updateState.containsKey(field))
                   .forEach(field -> updateTemplate.get(field)
                                                   .accept(crud, updateState));

    }

    private GraphQLFieldDefinition updateInstances() {
        GraphQLInputObjectType updateType = updateTypeBuilder.build();
        if (updateType.getFields()
                      .isEmpty()) {
            return null;
        }
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdateTemplate = updateTemplate;
        graphql.schema.GraphQLFieldDefinition.Builder field = newFieldDefinition().name(String.format(UPDATE_MUTATION,
                                                                                                      English.plural(name)))
                                                                                  .type(referenceToType(facet))
                                                                                  .description(String.format("Update the instances of %s",
                                                                                                             name));

        return field.argument(newArgument().name(STATE)
                                           .description("the update state to apply")
                                           .type(new GraphQLNonNull(new GraphQLList(updateType)))
                                           .build())
                    .dataFetcher(env -> {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> updateStates = (List<Map<String, Object>>) env.getArgument(STATE);
                        PhantasmCRUD crud = ctx(env);
                        return updateStates.stream()
                                           .map(updateState -> {
                                               ExistentialRuleform ruleform = ((ExistentialRuleform) crud.lookup((UUID) updateState.get(ID)));
                                               update(ruleform, updateState,
                                                      crud,
                                                      detachedUpdateTemplate);
                                               return ruleform;
                                           })
                                           .collect(Collectors.toList());
                    })
                    .build();
    }

    @SuppressWarnings("unchecked")
    private void visit(AttributeAuthorization auth) {
        Attribute attribute = auth.getAttribute();
        GraphQLOutputType type = typeOf(attribute);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(auth.getFieldName())
                                              .description(attribute.getDescription())
                                              .dataFetcher(env -> {
                                                  Object value = ctx(env).getAttributeValue(facet,
                                                                                            ((Phantasm) env.getSource()).getRuleform(),
                                                                                            auth);
                                                  return resolve(attribute,
                                                                 value);
                                              })
                                              .build());

        String setter = String.format(SET_TEMPLATE,
                                      Phantasmagoria.capitalized(auth.getFieldName()));
        graphql.schema.GraphQLInputObjectField.Builder builder = newInputObjectField().name(setter)
                                                                                      .description(auth.getNotes());

        if (auth.getAttribute()
                .getIndexed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                                  auth,
                                                                  (List<Object>) update.get(setter)));
            builder.type(new GraphQLList(GraphQLString));
        } else if (auth.getAttribute()
                       .getKeyed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                                  auth,
                                                                  (Map<String, Object>) update.get(setter)));
            builder.type(GraphQLString);
        } else {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                                                  auth,
                                                                  update.get(setter)));
            switch (auth.getAttribute()
                        .getValueType()) {
                case Binary:
                    builder.type(GraphQLString);
                    break;
                case Boolean:
                    builder.type(GraphQLBoolean);
                    break;
                case Integer:
                    builder.type(GraphQLInt);
                    break;
                case JSON:
                    builder.type(WorkspsacScalarTypes.GraphQLJson);
                    break;
                case Numeric:
                    builder.type(GraphQLBigDecimal);
                    break;
                case Text:
                    builder.type(GraphQLString);
                    break;
                case Timestamp:
                    builder.type(GraphQLLong);
                    break;
                default:
                    break;

            }
        }
        GraphQLInputObjectField field = builder.build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
    }

    private void visitChildren(NetworkAuthorization auth) {
        GraphQLOutputType type = referenceToType(auth.getChild());
        type = new GraphQLList(type);
        typeBuilder.field(PhantasmContext.newEdgeFieldDefinition()
                                         .auth(auth)
                                         .type(type)
                                         .name(auth.plural())
                                         .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                  ((Phantasm) env.getSource()).getRuleform(),
                                                                                  auth)
                                                                     .stream()
                                                                     .collect(Collectors.toList()))
                                         .description(auth.getNotes())
                                         .build());
        typeBuilder.field(PhantasmContext.newEdgeFieldDefinition()
                                         .auth(auth)
                                         .type(type)
                                         .name(String.format(IMMEDIATE_TEMPLATE,
                                                             Phantasmagoria.capitalized(auth.plural())))
                                         .dataFetcher(env -> ctx(env).getImmediateChildren(facet,
                                                                                           ((Phantasm) env.getSource()).getRuleform(),
                                                                                           auth)
                                                                     .stream()
                                                                     .collect(Collectors.toList()))
                                         .description(auth.getNotes())
                                         .build());
        setChildren(auth);
        addChild(auth);
        addChildren(auth);
        removeChild(auth);
        removeChildren(auth);
    }

    private void visitSingular(NetworkAuthorization auth) {
        GraphQLOutputType type = referenceToType(auth.getChild());
        typeBuilder.field(PhantasmContext.newEdgeFieldDefinition()
                                         .auth(auth)
                                         .type(type)
                                         .name(auth.getFieldName())
                                         .dataFetcher(env -> ctx(env).getSingularChild(facet,
                                                                                       ((Phantasm) env.getSource()).getRuleform(),
                                                                                       auth))
                                         .description(auth.getNotes())
                                         .build());
        String setter = String.format(SET_TEMPLATE,
                                      Phantasmagoria.capitalized(auth.getFieldName()));
        GraphQLInputObjectField field = newInputObjectField().type(GraphQLUuid)
                                                             .name(setter)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(setter, (crud, update) -> {
            UUID id = (UUID) update.get(setter);
            crud.setSingularChild(facet,
                                  ((Phantasm) update.get(AT_RULEFORM)).getRuleform(),
                                  auth,
                                  id == null ? null
                                             : (ExistentialRuleform) crud.lookup(id));
        });
        references.add(auth.getChild()
                           .getFacet());
    }

    static Class<?> getDeclaringClass(Method method) {
        Class<?> object = method.getDeclaringClass();
        Class<?> declaringClass = object;
        for (Class<?> iface : object.getInterfaces()) {
            try {
                iface.getMethod(method.getName(), method.getParameterTypes());
                declaringClass = iface;
            } catch (NoSuchMethodException e) {
            }
        }

        try {
            if (object.getSuperclass() != null) {
                object.getSuperclass()
                      .getMethod(method.getName(), method.getParameterTypes());
                declaringClass = object.getSuperclass();
            }
        } catch (NoSuchMethodException e) {
        }
        return declaringClass;

    }

    public static List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> processPlugin(Class<?> plugin,
                                                                                               GraphQLObjectType.Builder builder,
                                                                                               PhantasmProcessor processor) {
        Plugin annotation = plugin.getAnnotation(Plugin.class);
        if (annotation == null) {
            throw new IllegalArgumentException(String.format("Class not annotated with @Plugin: %s",
                                                             plugin.getCanonicalName()));
        }
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers = new ArrayList<>();
        Class<? extends Phantasm> phantasm = annotation.value();
        for (Method method : plugin.getMethods()) {

            Class<?> declaringClass = FacetFields.getDeclaringClass(method);

            boolean valid;
            try {
                valid = Modifier.isStatic(method.getModifiers())
                        && (method.getAnnotation(GraphQLField.class) != null
                            || declaringClass.getMethod(method.getName(),
                                                        method.getParameterTypes())
                                             .getAnnotation(GraphQLField.class) != null);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }

            if (valid) {
                try {
                    builder.field(processor.getPluginField(method, phantasm));
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            } else if (method.getAnnotation(Initializer.class) != null) {
                PhantasmInitializer initializer = new PhantasmInitializer(method);
                initializers.add((env,
                                  rf) -> initializer.get(env,
                                                         WorkspaceSchema.ctx(env)
                                                                        .wrap(phantasm,
                                                                              rf)));
            }
        }
        return initializers;

    }
}
