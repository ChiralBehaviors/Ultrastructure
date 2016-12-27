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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle;
import com.chiralbehaviors.CoRE.utils.English;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;

/**
 * Cannonical tranform of Phantasm metadata into GraphQL metadata. Provides
 * framework for Phantasm Plugin model;
 *
 * @author hhildebrand
 *
 */
public class FacetFields implements PhantasmTraversal.PhantasmVisitor {

    private static final String _EXT               = "_ext";
    private static final String ADD_TEMPLATE       = "add%s";
    private static final String APPLY_MUTATION     = "apply%s";
    private static final String AT_RULEFORM        = "@ruleform";
    private static final String CHILD              = "child";
    private static final String CREATE_MUTATION    = "create%s";
    private static final String CREATE_TYPE        = "%sCreate";
    private static final String ID                 = "id";
    private static final String IDS                = "ids";
    private static final String IMMEDIATE_TEMPLATE = "immediate%s";
    private static final Logger log                = LoggerFactory.getLogger(FacetFields.class);
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

    public static PhantasmCRUD ctx(DataFetchingEnvironment env) {
        return (PhantasmCRUD) env.getContext();
    }

    public static Deque<FacetRecord> initialState(WorkspaceAccessor workspace,
                                                  Model model) {
        Product definingProduct = workspace.getDefiningProduct();
        Deque<FacetRecord> unresolved = new ArrayDeque<>();
        unresolved.addAll(model.getPhantasmModel()
                               .getFacets(definingProduct));
        return unresolved;
    }

    public static Object invoke(Method method, DataFetchingEnvironment env) {
        try {
            return method.invoke(null, env);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException());
        }
    }

    public static Object invoke(Method method, DataFetchingEnvironment env,
                                Model model, Phantasm instance) {
        try {
            return method.invoke(null, env, model, instance);
        } catch (InvocationTargetException e) {
            log.error("error invoking {} plugin {}", instance.toString(),
                      method.toGenericString(), e.getTargetException());
            return null;
        } catch (Throwable e) {
            log.error("error invoking {} plugin {}", instance.toString(),
                      method.toGenericString(), e);
            return null;
        }
    }

    private static String capitalized(String field) {
        char[] chars = field.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private graphql.schema.GraphQLInputObjectType.Builder                  createTypeBuilder;
    private List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers   = new ArrayList<>();
    private String                                                         name;
    private Set<FacetRecord>                                               references     = new HashSet<>();
    private GraphQLObjectType                                              type;
    private Builder                                                        typeBuilder;
    private Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>>     updateTemplate = new HashMap<>();
    private graphql.schema.GraphQLInputObjectType.Builder                  updateTypeBuilder;

    public FacetFields(FacetRecord facet) {
        name = WorkspacePresentation.toTypeName(facet.getName());
        typeBuilder = newObject().name(getName())
                                 .description(facet.getNotes());
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                getName()))
                                            .description(facet.getNotes());
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                getName()))
                                            .description(facet.getNotes());
    }

    public void build(Aspect aspect, Builder query, Builder mutation) {
        type = typeBuilder.build();
        query.field(instance(aspect, type));
        query.field(instances(aspect));
        mutation.field(createInstance(aspect));
        mutation.field(createInstances(aspect));
        mutation.field(apply(aspect));
        mutation.field(update(aspect));
        mutation.field(updateInstances(aspect));
        mutation.field(remove(aspect));
        clear();
    }

    public String getName() {
        return name;
    }

    public GraphQLOutputType getType() {
        return type;
    }

    public GraphQLTypeReference referenceToType(String typeName) {
        return new GraphQLTypeReference(WorkspacePresentation.toTypeName(typeName));
    }

    public Set<FacetRecord> resolve(FacetRecord facet, List<Class<?>> plugins,
                                    Model model,
                                    WorkspaceTypeFunction typeFunction) {
        build(facet);
        Aspect aspect = new Aspect(model.create(), facet);
        new PhantasmTraversal(model).traverse(aspect, this);

        addPlugins(aspect, plugins, typeFunction);
        return references;
    }

    @Override
    public String toString() {
        return String.format("FacetType [name=%s]", getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(Aspect facet, AttributeAuthorization auth,
                      String fieldName) {
        Attribute attribute = auth.getAttribute();
        GraphQLOutputType type = typeOf(attribute);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(attribute.getDescription())
                                              .dataFetcher(env -> {
                                                  Object value = ctx(env).getAttributeValue(facet,
                                                                                            ((ExistentialRuleform) env.getSource()),
                                                                                            auth);
                                                  return resolve(attribute,
                                                                 value);
                                              })
                                              .build());

        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        graphql.schema.GraphQLInputObjectField.Builder builder = newInputObjectField().name(setter)
                                                                                      .description(auth.getNotes());

        if (auth.getAttribute()
                .getIndexed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (List<Object>) update.get(setter)));
            builder.type(new GraphQLList(GraphQLString));
        } else if (auth.getAttribute()
                       .getKeyed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (Map<String, Object>) update.get(setter)));
            builder.type(GraphQLString);

        } else {
            Function<Object, Object> converter = attribute.getValueType() == ValueType.JSON ? object -> {
                try {
                    return new ObjectMapper().readValue((String) object,
                                                        Map.class);
                } catch (IOException e) {
                    throw new IllegalStateException(String.format("Cannot deserialize %s",
                                                                  object),
                                                    e);
                }
            } : object -> object;
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  converter.apply(update.get(setter))));
            builder.type(GraphQLString);
        }
        GraphQLInputObjectField field = builder.build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(Aspect facet, NetworkAttributeAuthorization auth,
                      String edgeName) {
        Attribute attribute = auth.getAttribute();
        String fieldName = String.format("%sOf%s",
                                         WorkspacePresentation.toFieldName(auth.getAttribute()
                                                                               .getName()),
                                         edgeName);
        GraphQLOutputType type = typeOf(attribute);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(attribute.getDescription())
                                              .dataFetcher(env -> {
                                                  ExistentialRuleform rf = (ExistentialRuleform) env.getSource();
                                                  UUID child = UUID.fromString(env.getArgument(CHILD));
                                                  PhantasmCRUD ctx = ctx(env);
                                                  Object value = ctx.getAttributeValue(facet,
                                                                                       rf,
                                                                                       auth,
                                                                                       ctx.getModel()
                                                                                          .records()
                                                                                          .resolve(child));
                                                  return resolve(attribute,
                                                                 value);
                                              })
                                              .build());

        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        graphql.schema.GraphQLInputObjectField.Builder builder = newInputObjectField().name(setter)
                                                                                      .description(auth.getNotes());

        if (auth.getAttribute()
                .getIndexed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  UUID.fromString((String) update.get(CHILD)),
                                                                  auth,
                                                                  (List<Object>) update.get(setter)));
            builder.type(new GraphQLList(GraphQLString));
        } else if (auth.getAttribute()
                       .getKeyed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  crud.getModel()
                                                                      .records()
                                                                      .resolve(UUID.fromString((String) update.get(CHILD))),
                                                                  (Map<String, Object>) update.get(setter)));
            builder.type(GraphQLString);

        } else {
            Function<Object, Object> converter = attribute.getValueType() == ValueType.JSON ? object -> {
                try {
                    return new ObjectMapper().readValue((String) object,
                                                        Map.class);
                } catch (IOException e) {
                    throw new IllegalStateException(String.format("Cannot deserialize %s",
                                                                  object),
                                                    e);
                }
            } : object -> object;
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  crud.getModel()
                                                                      .records()
                                                                      .resolve(UUID.fromString((String) update.get(CHILD))),
                                                                  converter.apply(update.get(setter))));
            builder.type(GraphQLString);
        }
        GraphQLInputObjectField field = builder.build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
    }

    @Override
    public void visitChildren(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child,
                              String singularFieldName) {
        String childFacetName = child.getName();
        GraphQLOutputType type = referenceToType(childFacetName);
        type = new GraphQLList(type);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       ((ExistentialRuleform) env.getSource()),
                                                                                       auth)
                                                                          .stream()
                                                                          .collect(Collectors.toList()))
                                              .description(auth.getNotes())
                                              .build());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(String.format(IMMEDIATE_TEMPLATE,
                                                                  capitalized(fieldName)))
                                              .dataFetcher(env -> ctx(env).getImmediateChildren(facet,
                                                                                                ((ExistentialRuleform) env.getSource()),
                                                                                                auth)
                                                                          .stream()
                                                                          .collect(Collectors.toList()))
                                              .description(auth.getNotes())
                                              .build());
        setChildren(facet, auth, fieldName);
        addChild(facet, auth, singularFieldName);
        addChildren(facet, auth, fieldName);
        removeChild(facet, auth, singularFieldName);
        removeChildren(facet, auth, fieldName);
        references.add(child.getFacet());
    }

    @Override
    public void visitSingular(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child) {
        GraphQLOutputType type = referenceToType(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getSingularChild(facet,
                                                                                            ((ExistentialRuleform) env.getSource()),
                                                                                            auth))
                                              .description(auth.getNotes())
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        GraphQLInputObjectField field = newInputObjectField().type(GraphQLString)
                                                             .name(setter)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(setter, (crud, update) -> {
            String id = (String) update.get(setter);
            crud.setSingularChild(facet,
                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                  auth,
                                  id == null ? null
                                             : (ExistentialRuleform) crud.lookup(id));
        });
        references.add(child.getFacet());
    }

    private void addChild(Aspect facet, NetworkAuthorization auth,
                          String singularFieldName) {
        String add = String.format(ADD_TEMPLATE,
                                   capitalized(singularFieldName));
        GraphQLInputObjectField field = newInputObjectField().type(GraphQLString)
                                                             .name(add)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     (ExistentialRuleform) update.get(AT_RULEFORM),
                                                     auth,
                                                     crud.lookup((String) update.get(add))));
    }

    @SuppressWarnings("unchecked")
    private void addChildren(Aspect facet, NetworkAuthorization auth,
                             String fieldName) {
        String addChildren = String.format(ADD_TEMPLATE,
                                           capitalized(fieldName));
        GraphQLInputObjectField field = newInputObjectField().type(new GraphQLList(GraphQLString))
                                                             .name(addChildren)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        (ExistentialRuleform) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup((List<String>) update.get(addChildren))));
    }

    private void addPlugins(Aspect facet, List<Class<?>> plugins,
                            WorkspaceTypeFunction typeFunction) {
        plugins.forEach(plugin -> {
            initializers.addAll(PhantasmProcessing.processPlugin(plugin,
                                                                 typeFunction,
                                                                 typeFunction,
                                                                 typeBuilder));
        });
    }

    private GraphQLFieldDefinition apply(Aspect facet) {
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detached = initializers;
        return newFieldDefinition().name(String.format(APPLY_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .description(String.format("Apply %s facet to the instance",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(referenceToType(facet.getName()))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> {
                                       ExistentialRuleform ruleform = ctx(env).lookup((String) env.getArgument(ID));
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

    private void build(FacetRecord facet) {
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(ID)
                                              .description("The id of the facet instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(new GraphQLTypeReference("Existential"))
                                              .name(_EXT)
                                              .description("Cast the instance as an Existential")
                                              .dataFetcher(env -> Existential.wrap((ExistentialRecord) env.getSource()))
                                              .build());

        updateTypeBuilder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                                     .name(ID)
                                                     .description(String.format("the id of the updated %s",
                                                                                WorkspacePresentation.toTypeName(facet.getName())))
                                                     .build());
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
    private GraphQLFieldDefinition createInstance(Aspect facet) {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdate = updateTemplate;
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detachedConstructors = initializers;
        return newFieldDefinition().name(String.format(CREATE_MUTATION, name))
                                   .description(String.format("Create an instance of %s",
                                                              name))
                                   .type(type)
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state to apply to the new instance")
                                                          .type(new GraphQLNonNull(createTypeBuilder.build()))
                                                          .build())
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
    private GraphQLFieldDefinition createInstances(Aspect facet) {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdate = updateTemplate;
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> detachedConstructors = initializers;
        return newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                       English.plural(WorkspacePresentation.toTypeName(facet.getName()))))
                                   .description(String.format("Create instances of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(new GraphQLList(type))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state to apply to the new instance")
                                                          .type(new GraphQLNonNull(new GraphQLList(createTypeBuilder.build())))
                                                          .build())
                                   .dataFetcher(env -> {
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

    private GraphQLFieldDefinition instance(Aspect facet,
                                            GraphQLObjectType type) {
        return newFieldDefinition().name(Introspector.decapitalize(WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> ctx(env).lookup((String) env.getArgument(ID)))
                                   .build();
    }

    private GraphQLFieldDefinition instances(Aspect facet) {
        return newFieldDefinition().name(Introspector.decapitalize(English.plural(WorkspacePresentation.toTypeName(facet.getName()))))
                                   .description(String.format("Return the instances of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .argument(newArgument().name(IDS)
                                                          .description("list of ids of the instances to query")
                                                          .type(new GraphQLList(GraphQLString))
                                                          .build())
                                   .type(new GraphQLList(referenceToType(facet.getName())))
                                   .dataFetcher(context -> {
                                       @SuppressWarnings("unchecked")
                                       List<String> ids = (List<String>) context.getArgument(ID);
                                       return (ids != null ? ctx(context).lookup(ids)
                                                           : ctx(context).getInstances(facet)).stream()
                                                                                              .collect(Collectors.toList());
                                   })
                                   .build();

    }

    private GraphQLFieldDefinition remove(Aspect facet) {
        return newFieldDefinition().name(String.format(REMOVE_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(referenceToType(facet.getName()))
                                   .description(String.format("Remove the %s facet from the instance",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).remove(facet,
                                                                       ctx(env).lookup((String) env.getArgument(ID)),
                                                                       true))
                                   .build();
    }

    private void removeChild(Aspect facet, NetworkAuthorization auth,
                             String singularFieldName) {
        String remove = String.format(REMOVE_TEMPLATE,
                                      capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        (ExistentialRuleform) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup((String) update.get(remove))));
    }

    @SuppressWarnings("unchecked")
    private void removeChildren(Aspect facet, NetworkAuthorization auth,
                                String fieldName) {
        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           (ExistentialRuleform) update.get(AT_RULEFORM),
                                                           auth,
                                                           crud.lookup((List<String>) update.get(removeChildren))));
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
    private void setChildren(Aspect facet, NetworkAuthorization auth,
                             String fieldName) {
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        GraphQLInputObjectField field = newInputObjectField().type(new GraphQLList(GraphQLString))
                                                             .name(setter)
                                                             .description(auth.getNotes())
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        (ExistentialRuleform) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup((List<String>) update.get(setter))));
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
                type = GraphQLString;
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition update(Aspect facet) {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdateTemplate = updateTemplate;
        return newFieldDefinition().name(String.format(UPDATE_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(referenceToType(facet.getName()))
                                   .description(String.format("Update the instance of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(updateTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       ExistentialRuleform ruleform = (ExistentialRuleform) crud.lookup((String) updateState.get(ID));
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

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition updateInstances(Aspect facet) {
        Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>> detachedUpdateTemplate = updateTemplate;
        String typeName = WorkspacePresentation.toTypeName(facet.getName());
        return newFieldDefinition().name(String.format(UPDATE_MUTATION,
                                                       English.plural(typeName)))
                                   .type(referenceToType(facet.getName()))
                                   .description(String.format("Update the instances of %s",
                                                              typeName))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(new GraphQLList(updateTypeBuilder.build())))
                                                          .build())
                                   .dataFetcher(env -> {
                                       List<Map<String, Object>> updateStates = (List<Map<String, Object>>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       return updateStates.stream()
                                                          .map(updateState -> {
                                                              ExistentialRuleform ruleform = ((ExistentialRuleform) crud.lookup((String) updateState.get(ID)));
                                                              update(ruleform,
                                                                     updateState,
                                                                     crud,
                                                                     detachedUpdateTemplate);
                                                              return ruleform;
                                                          })
                                                          .collect(Collectors.toList());
                                   })
                                   .build();
    }
}
