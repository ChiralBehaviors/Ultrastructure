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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Constructor;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.InstanceMethod;
import com.chiralbehaviors.CoRE.kernel.phantasm.product.Plugin;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
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
public class FacetType implements PhantasmTraversal.PhantasmVisitor {

    private static final String ADD_TEMPLATE              = "add%s";
    private static final String APPLY_MUTATION            = "Apply%s";
    private static final String AT_RULEFORM               = "@ruleform";
    private static final String CREATE_INSTANCES_MUTATION = "CreateInstancesOf%s";
    private static final String CREATE_MUTATION           = "Create%s";
    private static final String CREATE_TYPE               = "%sCreate";
    private static final String DESCRIPTION               = "description";
    private static final String ID                        = "id";
    private static final String IDS                       = "ids";
    private static final String IMMEDIATE_TEMPLATE        = "immediate%s";
    private static final String INSTANCES_OF_QUERY        = "InstancesOf%s";
    private static final Logger log                       = LoggerFactory.getLogger(FacetType.class);
    private static final String NAME                      = "name";
    private static final String REMOVE_MUTATION           = "Remove%s";
    private static final String REMOVE_TEMPLATE           = "remove%s";
    private static final String S_S_PLUGIN_CONVENTION     = "%s.%s_Plugin";
    private static final String SET_DESCRIPTION;
    @SuppressWarnings("unused")
    private static final String SET_INDEX_TEMPLATE        = "set%sIndex";
    @SuppressWarnings("unused")
    private static final String SET_KEY_TEMPLATE          = "set%sKey";
    private static final String SET_NAME;
    private static final String SET_TEMPLATE              = "set%s";
    private static final String STATE                     = "state";
    private static final String UPDATE_INSTANCES_MUTATION = "UpdateInstancesOf%s";
    private static final String UPDATE_MUTATION           = "Update%s";
    private static final String UPDATE_TYPE               = "%sUpdate";

    static {
        SET_NAME = String.format(SET_TEMPLATE, capitalized(NAME));
        SET_DESCRIPTION = String.format(SET_TEMPLATE, capitalized(DESCRIPTION));
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
                                Model model,
                                @SuppressWarnings("rawtypes") Phantasm instance) {
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

    private List<BiFunction<DataFetchingEnvironment, ExistentialRuleform, Object>>          constructors   = new ArrayList<>();
    private graphql.schema.GraphQLInputObjectType.Builder                                   createTypeBuilder;
    private String                                                                          name;
    private Set<ExistentialNetworkAuthorization>                                            references     = new HashSet<>();
    private Builder                                                                         typeBuilder;
    private Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> updateTemplate = new HashMap<>();

    private graphql.schema.GraphQLInputObjectType.Builder                                   updateTypeBuilder;

    public FacetType(FacetRecord facet) {
        this.name = WorkspacePresentation.toTypeName(facet.getName());
        typeBuilder = newObject().name(getName())
                                 .description(facet.getNotes());
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                getName()))
                                            .description(facet.getNotes());
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                getName()))
                                            .description(facet.getNotes());
    }

    /**
     * Build the top level queries and mutations
     * 
     * @param query
     *            - top level query
     * @param mutation
     *            - top level mutation
     * @param facet
     * @return the references this facet has to other facets.
     */
    public Set<ExistentialNetworkAuthorization> build(Builder query,
                                                      Builder mutation,
                                                      FacetRecord facet,
                                                      List<Plugin> plugins,
                                                      Model model,
                                                      ClassLoader executionScope) {
        build(facet);
        new PhantasmTraversal(model).traverse(new Aspect(model.create(), facet),
                                              this);

        addPlugins(facet, plugins, executionScope);

        GraphQLObjectType type = typeBuilder.build();

        query.field(instance(facet, type));
        query.field(instances(facet));

        mutation.field(createInstance(facet));
        mutation.field(createInstances(facet));
        mutation.field(apply(facet));
        mutation.field(update(facet));
        mutation.field(updateInstances(facet));
        mutation.field(remove(facet));
        Set<ExistentialNetworkAuthorization> referenced = references;
        clear();
        return referenced;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PhantasmCRUD ctx(DataFetchingEnvironment env) {
        return (PhantasmCRUD) env.getContext();
    }

    public String getName() {
        return name;
    }

    public GraphQLTypeReference referenceToType(String typeName) {
        return new GraphQLTypeReference(WorkspacePresentation.toTypeName(typeName));
    }

    public GraphQLTypeReference referenceToUpdateType(String typeName) {
        return new GraphQLTypeReference(String.format(UPDATE_TYPE,
                                                      WorkspacePresentation.toTypeName(typeName)));
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
                                                                                            (ExistentialRuleform) env.getSource(),
                                                                                            auth);
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
                                              })
                                              .build());

        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        graphql.schema.GraphQLInputObjectField.Builder builder = newInputObjectField().name(setter)
                                                                                      .description(auth.getNotes());

        Function<Object, Object> converter = attribute.getValueType() == ValueType.JSON ? object -> {
            try {
                return new ObjectMapper().readValue((String) object, Map.class);
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Cannot deserialize %s",
                                                              object),
                                                e);
            }
        } : object -> object;
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
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (ExistentialRuleform) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (Object) converter.apply(update.get(setter))));
            builder.type(GraphQLString);
        }
        GraphQLInputObjectField field = builder.build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitChildren(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child,
                              String singularFieldName) {
        GraphQLOutputType type = referenceToType(child.getName());
        type = new GraphQLList(type);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       (ExistentialRuleform) env.getSource(),
                                                                                       auth))
                                              .description(auth.getNotes())
                                              .build());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(String.format(IMMEDIATE_TEMPLATE,
                                                                  capitalized(fieldName)))
                                              .dataFetcher(env -> ctx(env).getImmediateChildren(facet,
                                                                                                (ExistentialRuleform) env.getSource(),
                                                                                                auth))
                                              .description(auth.getNotes())
                                              .build());
        setChildren(facet, auth, fieldName);
        addChild(facet, auth, singularFieldName);
        addChildren(facet, auth, fieldName);
        removeChild(facet, auth, singularFieldName);
        removeChildren(facet, auth, fieldName);
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSingular(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child) {
        GraphQLOutputType type = referenceToType(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getSingularChild(facet,
                                                                                            (ExistentialRuleform) env.getSource(),
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
            return crud.setSingularChild(facet,
                                         (ExistentialRuleform) update.get(AT_RULEFORM),
                                         auth,
                                         id == null ? null
                                                    : (ExistentialRuleform) crud.lookup(id));
        });
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    private void addChild(ExistentialNetworkAuthorization facet,
                          ExistentialNetworkAuthorization auth,
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
                                                     (ExistentialRuleform) crud.lookup((String) update.get(add))));
    }

    @SuppressWarnings("unchecked")
    private void addChildren(ExistentialNetworkAuthorization facet,
                             ExistentialNetworkAuthorization auth,
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
                                                        (List<ExistentialRuleform>) crud.lookupRuleForm((List<String>) update.get(addChildren))));
    }

    private void addPlugins(FacetRecord facet, List<Plugin> plugins,
                            ClassLoader executionScope) {
        plugins.forEach(plugin -> {
            String defaultImplementation = Optional.of(plugin.getPackageName())
                                                   .map(pkg -> String.format(S_S_PLUGIN_CONVENTION,
                                                                             pkg,
                                                                             plugin.getFacetName()))
                                                   .orElse(null);
            if (defaultImplementation == null) {
                return;
            }
            build(plugin.getConstructor(), defaultImplementation,
                  executionScope);
            plugin.getInstanceMethods()
                  .forEach(method -> build(facet, method, defaultImplementation,
                                           executionScope));
        });
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition apply(FacetRecord facet) {
        List<BiFunction<DataFetchingEnvironment, ExistentialRuleform, Object>> detachedConstructors = constructors;
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
                                       ExistentialRuleform ruleform = (ExistentialRuleform) ctx(env).lookup((String) env.getArgument(ID));
                                       PhantasmCRUD crud = ctx(env);
                                       return crud.apply(facet, ruleform,
                                                         instance -> {
                                                             detachedConstructors.forEach(constructor -> constructor.apply(env,
                                                                                                                           instance));
                                                             return ruleform;
                                                         });
                                   })
                                   .build();
    }

    private void build(Constructor constructor, String defaultImplementation,
                       ClassLoader executionScope) {
        Method method = getInstanceMethod(Optional.ofNullable(constructor.getImplementationClass())
                                                  .orElse(defaultImplementation),
                                          Optional.ofNullable(constructor.getImplementationMethod())
                                                  .orElse(constructor.getName()),
                                          constructor.toString(),
                                          executionScope);

        constructors.add((env, instance) -> {
            ClassLoader prev = Thread.currentThread()
                                     .getContextClassLoader();
            Thread.currentThread()
                  .setContextClassLoader(executionScope);
            try {
                PhantasmCRUD crud = ctx(env);
                if (!checkInvoke(constructor, crud)) {
                    log.info(String.format("Failed invoking %s by: %s",
                                           constructor, crud.getModel()
                                                            .getCurrentPrincipal()));
                    return null;

                }
                @SuppressWarnings("unchecked")
                Class<? extends Phantasm> phantasm = (Class<? extends Phantasm>) method.getParameterTypes()[2];
                Model model = ctx(env).getModel();
                return invoke(method, env, model,
                              model.wrap(phantasm, instance));
            } finally {
                Thread.currentThread()
                      .setContextClassLoader(prev);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void build(FacetRecord facet) {
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(ID)
                                              .description("The id of the facet instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(NAME)
                                              .description("The name of the facet instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(DESCRIPTION)
                                              .description("The description of the facet instance")
                                              .build());

        updateTypeBuilder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                                     .name(ID)
                                                     .description(String.format("the id of the updated %s",
                                                                                WorkspacePresentation.toTypeName(facet.getName())))
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_NAME)
                                                     .description(String.format("the name to update on %s",
                                                                                WorkspacePresentation.toTypeName(facet.getName())))
                                                     .build());
        createTypeBuilder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                                     .name(SET_NAME)
                                                     .description(String.format("the name to update on %s",
                                                                                WorkspacePresentation.toTypeName(facet.getName())))
                                                     .build());
        updateTemplate.put(SET_NAME,
                           (crud,
                            update) -> crud.setName((ExistentialRuleform) update.get(AT_RULEFORM),
                                                    (String) update.get(SET_NAME)));
        GraphQLInputObjectField field = newInputObjectField().type(GraphQLString)
                                                             .name(SET_DESCRIPTION)
                                                             .description(String.format("the description to update on %s",
                                                                                        WorkspacePresentation.toTypeName(facet.getName())))
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(SET_DESCRIPTION,
                           (crud,
                            update) -> crud.setDescription((ExistentialRuleform) update.get(AT_RULEFORM),
                                                           (String) update.get(SET_DESCRIPTION)));
    }

    private void build(ExistentialNetworkAuthorization facet,
                       InstanceMethod instanceMethod,
                       String defaultImplementation,
                       ClassLoader executionScope) {
        Method method = getInstanceMethod(Optional.ofNullable(instanceMethod.getImplementationClass())
                                                  .orElse(defaultImplementation),
                                          Optional.ofNullable(instanceMethod.getImplementationMethod())
                                                  .orElse(instanceMethod.getName()),
                                          instanceMethod.toString(),
                                          executionScope);
        List<GraphQLArgument> arguments = instanceMethod.getArguments()
                                                        .stream()
                                                        .map(arg -> newArgument().name(arg.getName())
                                                                                 .description(arg.getDescription())
                                                                                 .type(inputTypeOf(arg.getInputType()))
                                                                                 .build())
                                                        .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        Class<? extends Phantasm> phantasm = (Class<? extends Phantasm>) method.getParameterTypes()[2];
        typeBuilder.field(newFieldDefinition().type(outputTypeOf(instanceMethod.getReturnType()))
                                              .argument(arguments)
                                              .name(instanceMethod.getName())
                                              .dataFetcher(env -> {
                                                  @SuppressWarnings("unchecked")
                                                  ExistentialRuleform instance = (ExistentialRuleform) env.getSource();
                                                  PhantasmCRUD crud = ctx(env);
                                                  if (!checkInvoke(facet,
                                                                   instanceMethod,
                                                                   instance,
                                                                   crud)) {
                                                      log.info(String.format("Failed invoking %s by: %s",
                                                                             instanceMethod,
                                                                             crud.getModel()
                                                                                 .getCurrentPrincipal()));
                                                      return null;

                                                  }
                                                  Model model = ctx(env).getModel();

                                                  ClassLoader prev = Thread.currentThread()
                                                                           .getContextClassLoader();
                                                  Thread.currentThread()
                                                        .setContextClassLoader(executionScope);
                                                  try {
                                                      return instance == null ? null
                                                                              : invoke(method,
                                                                                       env,
                                                                                       model,
                                                                                       model.wrap(phantasm,
                                                                                                  instance));
                                                  } finally {
                                                      Thread.currentThread()
                                                            .setContextClassLoader(prev);
                                                  }
                                              })
                                              .description(instanceMethod.getDescription())
                                              .build());
    }

    private boolean checkInvoke(Constructor constructor, PhantasmCRUD crud) {
        return crud.getModel()
                   .getPhantasmModel()
                   .checkCapability(constructor.getRuleform(),
                                    crud.getINVOKE());
    }

    private boolean checkInvoke(ExistentialNetworkAuthorization facet,
                                InstanceMethod method,
                                ExistentialRuleform instance,
                                PhantasmCRUD crud) {
        Model model = crud.getModel();
        PhantasmModel networkedModel = model.getPhantasmModel();
        Relationship invoke = crud.getINVOKE();
        return networkedModel.checkCapability(method.getRuleform(), invoke)
               && crud.checkInvoke(facet, instance);
    }

    private void clear() {
        this.references = null;
        this.typeBuilder = null;
        this.updateTypeBuilder = null;
        this.createTypeBuilder = null;
        this.updateTemplate = null;
        this.constructors = null;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstance(FacetRecord facet) {
        Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> detachedUpdate = updateTemplate;
        List<BiFunction<DataFetchingEnvironment, ExistentialRuleform, Object>> detachedConstructors = constructors;
        return newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .description(String.format("Create an instance of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(referenceToType(facet.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state to apply to the new instance")
                                                          .type(new GraphQLNonNull(createTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       return crud.createInstance(facet,
                                                                  (String) createState.get(SET_NAME),
                                                                  (String) createState.get(SET_DESCRIPTION),
                                                                  instance -> {
                                                                      createState.remove(SET_NAME);
                                                                      createState.remove(SET_DESCRIPTION);
                                                                      update(instance,
                                                                             createState,
                                                                             crud,
                                                                             detachedUpdate);
                                                                      detachedConstructors.forEach(constructor -> constructor.apply(env,
                                                                                                                                    instance));
                                                                      return instance;
                                                                  });

                                   })
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstances(FacetRecord facet) {
        Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> detachedUpdate = updateTemplate;
        List<BiFunction<DataFetchingEnvironment, ExistentialRuleform, Object>> detachedConstructors = constructors;
        return newFieldDefinition().name(String.format(CREATE_INSTANCES_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .description(String.format("Create instances of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(new GraphQLList(referenceToType(facet.getName())))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state to apply to the new instance")
                                                          .type(new GraphQLNonNull(new GraphQLList(createTypeBuilder.build())))
                                                          .build())
                                   .dataFetcher(env -> {
                                       List<Map<String, Object>> createStates = (List<Map<String, Object>>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       return createStates.stream()
                                                          .map(createState -> crud.createInstance(facet,
                                                                                                  (String) createState.get(SET_NAME),
                                                                                                  (String) createState.get(SET_DESCRIPTION),
                                                                                                  instance -> {
                                                                                                      createState.remove(SET_NAME);
                                                                                                      createState.remove(SET_DESCRIPTION);
                                                                                                      update(instance,
                                                                                                             createState,
                                                                                                             crud,
                                                                                                             detachedUpdate);
                                                                                                      detachedConstructors.forEach(constructor -> constructor.apply(env,
                                                                                                                                                                    instance));
                                                                                                      return instance;
                                                                                                  }))
                                                          .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private Class<?> getImplementation(String implementationClass, String type,
                                       ClassLoader executionScope) {
        if (implementationClass == null) {

            throw new IllegalStateException(String.format("No implementation class could be determined for %s in %s",
                                                          type, getName()));
        }
        Class<?> clazz;
        try {
            clazz = executionScope.loadClass(implementationClass);
        } catch (ClassNotFoundException e) {
            log.warn("Error plugging in constructor {} into {}", type,
                     getName(), e);
            throw new IllegalStateException(String.format("Error plugging in %s into %s: %s",
                                                          type, getName(),
                                                          e.toString()),
                                            e);
        }
        return clazz;
    }

    private Method getInstanceMethod(String implementationClass,
                                     String implementationMethod, String type,
                                     ClassLoader executionScope) {

        Class<?> clazz = getImplementation(implementationClass, type,
                                           executionScope);
        List<Method> candidates = Arrays.asList(clazz.getDeclaredMethods())
                                        .stream()
                                        .filter(method -> Modifier.isStatic(method.getModifiers()))
                                        .filter(method -> method.getName()
                                                                .equals(implementationMethod))
                                        .filter(method -> method.getParameterTypes().length == 3)
                                        .filter(method -> method.getParameterTypes()[0].equals(DataFetchingEnvironment.class))
                                        .filter(method -> method.getParameterTypes()[1].equals(Model.class))
                                        .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            log.warn("Error plugging in {} into {}, no static method matches for {} in {}",
                     type, getName(), implementationMethod,
                     implementationClass);
            throw new IllegalStateException(String.format("Error plugging in %s into %s, no static method matches for method '%s' in %s",
                                                          type, getName(),
                                                          implementationMethod,
                                                          implementationClass));
        }
        if (candidates.size() > 1) {
            log.warn("Error plugging in {} into {}, multiple matches for {} in {}",
                     type, getName(), implementationMethod,
                     implementationClass);
            throw new IllegalStateException(String.format("Error plugging in %s into %s, multiple matches for static method '%s' in %s",
                                                          type, getName(),
                                                          implementationMethod,
                                                          implementationClass));
        }
        return candidates.get(0);
    }

    private GraphQLInputType inputTypeOf(String type) {
        type = type.trim();
        if (type.startsWith("[")) {
            return new GraphQLList(inputTypeOf(type.substring(1, type.length()
                                                                 - 1)));
        }
        switch (type) {
            case "Int":
                return Scalars.GraphQLInt;
            case "String":
                return Scalars.GraphQLString;
            case "Boolean":
                return Scalars.GraphQLBoolean;
            case "Float":
                return Scalars.GraphQLFloat;
            default:
                throw new IllegalStateException(String.format("Invalid GraphQLType: %s",
                                                              type));
        }
    }

    private GraphQLFieldDefinition instance(FacetRecord facet,
                                            GraphQLObjectType type) {
        return newFieldDefinition().name(WorkspacePresentation.toTypeName(facet.getName()))
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> ctx(env).lookup((String) env.getArgument(ID)))
                                   .build();
    }

    private GraphQLFieldDefinition instances(FacetRecord facet) {
        return newFieldDefinition().name(String.format(INSTANCES_OF_QUERY,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .description(String.format("Return the instances of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .argument(newArgument().name(IDS)
                                                          .description("list of ids of the instances to query")
                                                          .type(new GraphQLList(GraphQLString))
                                                          .build())
                                   .type(new GraphQLList(referenceToType(facet.getName())))
                                   .dataFetcher(context -> {
                                       @SuppressWarnings("unchecked")
                                       List<String> ids = ((List<String>) context.getArgument(ID));
                                       return ids != null ? ctx(context).lookup(ids)
                                                          : ctx(context).getInstances(facet);
                                   })
                                   .build();

    }

    private GraphQLOutputType outputTypeOf(String type) {
        if (type == null) {
            return Scalars.GraphQLString;
        }
        type = type.trim();
        if (type.startsWith("[")) {
            return new GraphQLList(inputTypeOf(type.substring(1, type.length()
                                                                 - 1)));
        }
        switch (type) {
            case "Int":
                return Scalars.GraphQLInt;
            case "String":
                return Scalars.GraphQLString;
            case "Boolean":
                return Scalars.GraphQLBoolean;
            case "Float":
                return Scalars.GraphQLFloat;
            default:
                return referenceToType(type);
        }
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition remove(FacetRecord facet) {
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
                                                                       (ExistentialRuleform) ctx(env).lookup((String) env.getArgument(ID)),
                                                                       true))
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private void removeChild(ExistentialNetworkAuthorization facet,
                             ExistentialNetworkAuthorization auth,
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
                                                        (ExistentialRuleform) crud.lookup((String) update.get(remove))));
    }

    @SuppressWarnings("unchecked")
    private void removeChildren(ExistentialNetworkAuthorization facet,
                                ExistentialNetworkAuthorization auth,
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
                                                           (List<ExistentialRuleform>) crud.lookupRuleForm((List<String>) update.get(removeChildren))));
    }

    @SuppressWarnings("unchecked")
    private void setChildren(ExistentialNetworkAuthorization facet,
                             ExistentialNetworkAuthorization auth,
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
                                                        (List<ExistentialRuleform>) crud.lookupRuleForm((List<String>) update.get(setter))));
    }

    private GraphQLOutputType typeOf(Attribute attribute) {
        GraphQLOutputType type = null;
        switch (attribute.getValueType()) {
            case BINARY:
                type = GraphQLString; // encoded binary
                break;
            case BOOLEAN:
                type = GraphQLBoolean;
                break;
            case INTEGER:
                type = GraphQLInt;
                break;
            case NUMERIC:
                type = GraphQLFloat;
                break;
            case TEXT:
                type = GraphQLString;
                break;
            case TIMESTAMP:
                type = GraphQLString;
                break;
            case JSON:
                type = GraphQLString;
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition update(FacetRecord facet) {
        Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> detachedUpdateTemplate = updateTemplate;
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
                        Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> updateTemplate) {
        updateState.put(AT_RULEFORM, ruleform);
        updateState.keySet()
                   .stream()
                   .filter(field -> !field.equals(ID)
                                    && !field.equals(AT_RULEFORM)
                                    && updateState.containsKey(field))
                   .forEach(field -> updateTemplate.get(field)
                                                   .apply(crud, updateState));

    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition updateInstances(FacetRecord facet) {
        Map<String, BiFunction<PhantasmCRUD, Map<String, Object>, ExistentialRuleform>> detachedUpdateTemplate = updateTemplate;
        return newFieldDefinition().name(String.format(UPDATE_INSTANCES_MUTATION,
                                                       WorkspacePresentation.toTypeName(facet.getName())))
                                   .type(referenceToType(facet.getName()))
                                   .description(String.format("Update the instances of %s",
                                                              WorkspacePresentation.toTypeName(facet.getName())))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(new GraphQLList(updateTypeBuilder.build())))
                                                          .build())
                                   .dataFetcher(env -> {
                                       List<Map<String, Object>> updateStates = (List<Map<String, Object>>) env.getArgument(STATE);
                                       PhantasmCRUD crud = ctx(env);
                                       return updateStates.stream()
                                                          .map(updateState -> {
                                                              ExistentialRuleform ruleform = (ExistentialRuleform) crud.lookup((String) updateState.get(ID));
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
