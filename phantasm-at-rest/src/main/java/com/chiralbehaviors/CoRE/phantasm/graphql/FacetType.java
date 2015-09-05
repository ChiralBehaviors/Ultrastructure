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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.kernel.product.Constructor;
import com.chiralbehaviors.CoRE.kernel.product.InstanceMethod;
import com.chiralbehaviors.CoRE.kernel.product.Plugin;
import com.chiralbehaviors.CoRE.kernel.product.StaticMethod;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;

import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
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
public class FacetType<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements PhantasmTraversal.PhantasmVisitor<RuleForm, Network> {

    private static final String ADD_TEMPLATE          = "add%s";
    private static final String APPLY_MUTATION        = "Apply%s";
    private static final String AT_RULEFORM           = "@ruleform";
    private static final String CREATE_MUTATION       = "Create%s";
    private static final String DESCRIPTION           = "description";
    private static final String ID                    = "id";
    private static final String IMMEDIATE_TEMPLATE    = "immediate%s";
    private static final String INSTANCES_OF_QUERY    = "InstancesOf%s";
    private static final Logger log                   = LoggerFactory.getLogger(FacetType.class);
    private static final String NAME                  = "name";
    private static final String REMOVE_MUTATION       = "Remove%s";
    private static final String REMOVE_TEMPLATE       = "remove%s";
    private static final String S_S_PLUGIN_CONVENTION = "%s.%s_Plugin";
    private static final String SET_DESCRIPTION;
    private static final String SET_NAME;
    private static final String SET_TEMPLATE          = "set%s";
    private static final String STATE                 = "state";
    private static final String UPDATE_QUERY          = "Update%s";
    private static final String UPDATE_TYPE           = "%sUpdate";

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
                                @SuppressWarnings("rawtypes") Phantasm instance) {
        try {
            return method.invoke(null, env, instance);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException());
        }
    }

    private static String capitalized(String field) {
        char[] chars = field.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private List<BiFunction<DataFetchingEnvironment, RuleForm, Object>> constructors = new ArrayList<>();
    private String                                                      name;
    private Set<NetworkAuthorization<?>>                                references   = new HashSet<>();

    private Builder                                                                                 typeBuilder;
    private Map<String, BiFunction<PhantasmCRUD<RuleForm, Network>, Map<String, Object>, RuleForm>> updateTemplate = new HashMap<>();

    private graphql.schema.GraphQLInputObjectType.Builder updateTypeBuilder;

    public FacetType(NetworkAuthorization<RuleForm> facet) {
        this.name = facet.getName();
        typeBuilder = newObject().name(facet.getName())
                                 .description(facet.getNotes());
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                facet.getName()))
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
    public Set<NetworkAuthorization<?>> build(Builder query, Builder mutation,
                                              NetworkAuthorization<?> facetUntyped,
                                              List<Plugin> plugins, Model model,
                                              Map<Plugin, ClassLoader> executionScopes) {
        @SuppressWarnings("unchecked")
        NetworkAuthorization<RuleForm> facet = (NetworkAuthorization<RuleForm>) facetUntyped;
        build(facet);
        new PhantasmTraversal<RuleForm, Network>(model).traverse(facet, this);

        addPlugins(facet, plugins, executionScopes);

        GraphQLObjectType type = typeBuilder.build();

        query.field(instance(facet, type));
        query.field(instances(facet));

        mutation.field(createInstance(facet));
        mutation.field(apply(facet));
        mutation.field(update(facet));
        mutation.field(remove(facet));
        Set<NetworkAuthorization<?>> referenced = references;
        clear();
        return referenced;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PhantasmCRUD<RuleForm, Network> ctx(DataFetchingEnvironment env) {
        return (PhantasmCRUD) env.getContext();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("FacetType [name=%s]", getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visit(NetworkAuthorization<RuleForm> facet,
                      AttributeAuthorization<RuleForm, Network> auth,
                      String fieldName) {
        Attribute attribute = auth.getAuthorizedAttribute();
        GraphQLOutputType type = typeOf(attribute);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(attribute.getDescription())
                                              .dataFetcher(env -> ctx(env).getAttributeValue(facet,
                                                                                             (RuleForm) env.getSource(),
                                                                                             auth))
                                              .build());

        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        GraphQLInputType inputType;
        if (auth.getAuthorizedAttribute()
                .getIndexed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (List<Object>) update.get(setter)));
            inputType = new GraphQLList(GraphQLString);
        } else if (auth.getAuthorizedAttribute()
                       .getKeyed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (Map<String, Object>) update.get(setter)));
            inputType = GraphQLString;
        } else {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth,
                                                                  (Object) update.get(setter)));
            inputType = GraphQLString;
        }
        updateTypeBuilder.field(newInputObjectField().type(inputType)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child,
                              String singularFieldName) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        type = new GraphQLList(type);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       (RuleForm) env.getSource(),
                                                                                       auth))
                                              .description(auth.getNotes())
                                              .build());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(String.format(IMMEDIATE_TEMPLATE,
                                                                  capitalized(fieldName)))
                                              .dataFetcher(env -> ctx(env).getImmediateChildren(facet,
                                                                                                (RuleForm) env.getSource(),
                                                                                                auth))
                                              .description(auth.getNotes())
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                             (List<String>) update.get(setter))));

        String add = String.format(ADD_TEMPLATE,
                                   capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(add)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     (RuleForm) update.get(AT_RULEFORM),
                                                     auth,
                                                     (RuleForm) crud.lookup(auth,
                                                                            (String) update.get(add))));

        String remove = String.format(REMOVE_TEMPLATE,
                                      capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        (RuleForm) crud.lookup(auth,
                                                                               (String) update.get(remove))));

        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           (RuleForm) update.get(AT_RULEFORM),
                                                           auth,
                                                           (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                                (List<String>) update.get(removeChildren))));

        String addChildren = String.format(ADD_TEMPLATE,
                                           capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(addChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                             (List<String>) update.get(addChildren))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child,
                              String singularFieldName) {
        GraphQLList type = new GraphQLList(new GraphQLTypeReference(child.getName()));
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(auth.getNotes())
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       (RuleForm) env.getSource(),
                                                                                       auth))
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup(child,
                                                                    (List<String>) update.get(setter))));

        String add = String.format(ADD_TEMPLATE,
                                   capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(add)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     (RuleForm) update.get(AT_RULEFORM),
                                                     auth,
                                                     crud.lookup(child,
                                                                 (String) update.get(add))));

        String remove = String.format(REMOVE_TEMPLATE,
                                      capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup(child,
                                                                    (String) update.get(remove))));

        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           (RuleForm) update.get(AT_RULEFORM),
                                                           auth,
                                                           crud.lookup(child,
                                                                       (List<String>) update.get(removeChildren))));

        String addChildren = String.format(ADD_TEMPLATE,
                                           capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(addChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth,
                                                        crud.lookup(child,
                                                                    (List<String>) update.get(addChildren))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getSingularChild(facet,
                                                                                            (RuleForm) env.getSource(),
                                                                                            auth))
                                              .description(auth.getNotes())
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setSingularChild(facet,
                                                             (RuleForm) update.get(AT_RULEFORM),
                                                             auth,
                                                             (RuleForm) crud.lookup(auth,
                                                                                    (String) update.get(setter))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        GraphQLTypeReference type = new GraphQLTypeReference(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(auth.getNotes())
                                              .dataFetcher(env -> ctx(env).getSingularChild((RuleForm) env.getSource(),
                                                                                            auth,
                                                                                            facet))
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setSingularChild(facet,
                                                             (RuleForm) update.get(AT_RULEFORM),
                                                             auth,
                                                             crud.lookup(child,
                                                                         (String) update.get(setter))));
        references.add(child);
    }

    private void addPlugins(NetworkAuthorization<RuleForm> facet,
                            List<Plugin> plugins,
                            Map<Plugin, ClassLoader> executionScopes) {
        plugins.forEach(plugin -> {
            ClassLoader executionScope = executionScopes.get(plugin);
            assert executionScope != null : String.format("%s execution scope is null!",
                                                          plugin);
            String defaultImplementation = Optional.of(plugin.getPackageName())
                                                   .map(pkg -> String.format(S_S_PLUGIN_CONVENTION,
                                                                             pkg,
                                                                             plugin.getFacetName()))
                                                   .orElse(null);
            build(plugin.getConstructor(), defaultImplementation,
                  executionScope);
            plugin.getStaticMethods()
                  .forEach(method -> {
                build(method, executionScope, defaultImplementation);
            });
            plugin.getInstanceMethods()
                  .forEach(method -> build(facet, method, defaultImplementation,
                                           executionScope));
        });
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition apply(NetworkAuthorization<RuleForm> facet) {
        return newFieldDefinition().name(String.format(APPLY_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).apply(facet,
                                                                      (RuleForm) ctx(env).lookup(facet,
                                                                                                 (String) env.getArgument(ID))))
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
            @SuppressWarnings("unchecked")
            Class<? extends Phantasm<RuleForm>> phantasm = (Class<? extends Phantasm<RuleForm>>) method.getParameterTypes()[1];
            return invoke(method, env, ctx(env).getModel()
                                               .wrap(phantasm, instance));
        });
    }

    @SuppressWarnings("unchecked")
    private void build(NetworkAuthorization<RuleForm> facet) {
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

        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(ID)
                                                     .description(String.format("the id of the updated %s",
                                                                                facet.getName()))
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_NAME)
                                                     .description(String.format("the name to update on %s",
                                                                                facet.getName()))
                                                     .build());
        updateTemplate.put(SET_NAME,
                           (crud,
                            update) -> crud.setName((RuleForm) update.get(AT_RULEFORM),
                                                    (String) update.get(SET_NAME)));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_DESCRIPTION)
                                                     .description(String.format("the description to update on %s",
                                                                                facet.getName()))
                                                     .build());
        updateTemplate.put(SET_DESCRIPTION,
                           (crud,
                            update) -> crud.setDescription((RuleForm) update.get(AT_RULEFORM),
                                                           (String) update.get(SET_DESCRIPTION)));
    }

    private void build(NetworkAuthorization<RuleForm> facet,
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
        arguments.add(newArgument().name(ID)
                                   .description("id of the facet")
                                   .type(GraphQLString)
                                   .build());
        @SuppressWarnings("unchecked")
        Class<? extends Phantasm<RuleForm>> phantasm = (Class<? extends Phantasm<RuleForm>>) method.getParameterTypes()[1];
        typeBuilder.field(newFieldDefinition().type(outputTypeOf(instanceMethod.getReturnType()))
                                              .argument(arguments)
                                              .name(instanceMethod.getName())
                                              .dataFetcher(env -> {
                                                  @SuppressWarnings("unchecked")
                                                  RuleForm instance = (RuleForm) ctx(env).lookup(facet,
                                                                                                 (String) env.getArgument(ID));
                                                  return instance == null ? null
                                                                          : invoke(method,
                                                                                   env,
                                                                                   ctx(env).getModel()
                                                                                           .wrap(phantasm,
                                                                                                 instance));
                                              })
                                              .description(instanceMethod.getDescription())
                                              .build());
    }

    private void build(StaticMethod staticMethod, ClassLoader executionScope,
                       String defaultImplementation) {
        Method method = getStaticMethod(Optional.ofNullable(staticMethod.getImplementationClass())
                                                .orElse(defaultImplementation),
                                        Optional.ofNullable(staticMethod.getImplementationMethod())
                                                .orElse(staticMethod.getName()),
                                        staticMethod.toString(),
                                        executionScope);
        GraphQLOutputType type = outputTypeOf(staticMethod.getReturnType());
        type = new GraphQLList(type);
        List<GraphQLArgument> arguments = staticMethod.getArguments()
                                                      .stream()
                                                      .map(arg -> newArgument().name(arg.getName())
                                                                               .description(arg.getDescription())
                                                                               .type(inputTypeOf(arg.getInputType()))
                                                                               .build())
                                                      .collect(Collectors.toList());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .argument(arguments)
                                              .name(staticMethod.getName())
                                              .dataFetcher(env -> invoke(method,
                                                                         env))
                                              .description(staticMethod.getDescription())
                                              .build());
    }

    private void clear() {
        this.references = null;
        this.typeBuilder = null;
        this.updateTypeBuilder = null;
        this.updateTemplate = null;
        this.constructors = null;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstance(NetworkAuthorization<RuleForm> facet) {
        Map<String, BiFunction<PhantasmCRUD<RuleForm, Network>, Map<String, Object>, RuleForm>> detachedUpdate = updateTemplate;
        List<BiFunction<DataFetchingEnvironment, RuleForm, Object>> detachedConstructors = constructors;
        return newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply to the new instance")
                                                          .type(new GraphQLNonNull(updateTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD<RuleForm, Network> crud = ctx(env);
                                       RuleForm ruleform = crud.createInstance(facet,
                                                                               (String) updateState.get(SET_NAME),
                                                                               (String) updateState.get(SET_DESCRIPTION));
                                       updateState.remove(SET_NAME);
                                       updateState.remove(SET_DESCRIPTION);
                                       update(ruleform, updateState, crud,
                                              detachedUpdate);
                                       detachedConstructors.forEach(constructor -> constructor.apply(env,
                                                                                                     ruleform));
                                       return ruleform;
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
                                        .peek(method -> System.out.println("1: "
                                                                           + method.toGenericString()))
                                        .filter(method -> Modifier.isStatic(method.getModifiers()))
                                        .peek(method -> System.out.println("2: "
                                                                           + method.toGenericString()))
                                        .filter(method -> method.getName()
                                                                .equals(implementationMethod))
                                        .peek(method -> System.out.println("3: "
                                                                           + method.toGenericString()))
                                        .filter(method -> method.getParameterTypes().length == 2)
                                        .peek(method -> System.out.println("4: "
                                                                           + method.toGenericString()))
                                        .filter(method -> method.getParameterTypes()[0].equals(DataFetchingEnvironment.class))
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

    private Method getStaticMethod(String implementationClass,
                                   String implementationMethod, String type,
                                   ClassLoader executionScope) {
        Method method;
        try {
            method = getImplementation(implementationClass, type,
                                       executionScope).getDeclaredMethod(implementationMethod,
                                                                         DataFetchingEnvironment.class);
        } catch (NoSuchMethodException | SecurityException e) {
            log.warn("Error plugging in  {} into {}", type, getName(), e);
            throw new IllegalStateException(String.format("Error plugging in %s into %s: %s",
                                                          type, getName(),
                                                          e.toString()),
                                            e);
        }
        if (method == null) {
            log.warn("No implementation found to plug {} into {}", type,
                     getName());
            throw new IllegalStateException(String.format("No static method implementation found to plug %s into %s",
                                                          type, getName()));
        }
        validateStatic(type, method);
        return method;
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
                return Scalars.GraphQLBoolean;
            default:
                throw new IllegalStateException(String.format("Invalid GraphQLType: %s",
                                                              type));
        }
    }

    private GraphQLFieldDefinition instance(NetworkAuthorization<RuleForm> facet,
                                            GraphQLObjectType type) {
        return newFieldDefinition().name(facet.getName())
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).lookup(facet,
                                                                       (String) env.getArgument(ID)))
                                   .build();
    }

    private GraphQLFieldDefinition instances(NetworkAuthorization<RuleForm> facet) {
        return newFieldDefinition().name(String.format(INSTANCES_OF_QUERY,
                                                       facet.getName()))
                                   .type(new GraphQLList(new GraphQLTypeReference(facet.getName())))
                                   .dataFetcher(context -> ctx(context).getInstances(facet))
                                   .build();

    }

    private GraphQLOutputType outputTypeOf(String type) {
        if (type == null) {
            return new GraphQLTypeReference(getName());
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
                return Scalars.GraphQLBoolean;
            default:
                return new GraphQLTypeReference(type);
        }
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition remove(NetworkAuthorization<RuleForm> facet) {
        return newFieldDefinition().name(String.format(REMOVE_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).remove(facet,
                                                                       (RuleForm) ctx(env).lookup(facet,
                                                                                                  (String) env.getArgument(ID)),
                                                                       true))
                                   .build();
    }

    private GraphQLOutputType typeOf(Attribute attribute) {
        GraphQLOutputType type;
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
            default:
                throw new IllegalStateException(String.format("Cannot resolved the value type: %s for %s",
                                                              attribute.getValueType(),
                                                              attribute));
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition update(NetworkAuthorization<RuleForm> facet) {
        Map<String, BiFunction<PhantasmCRUD<RuleForm, Network>, Map<String, Object>, RuleForm>> detachedUpdateTemplate = updateTemplate;
        return newFieldDefinition().name(String.format(UPDATE_QUERY,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(updateTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD<RuleForm, Network> crud = ctx(env);
                                       RuleForm ruleform = (RuleForm) crud.lookup(facet,
                                                                                  (String) updateState.get(ID));
                                       update(ruleform, updateState, crud,
                                              detachedUpdateTemplate);
                                       return ruleform;
                                   })
                                   .build();
    }

    private void update(RuleForm ruleform, Map<String, Object> updateState,
                        PhantasmCRUD<RuleForm, Network> crud,
                        Map<String, BiFunction<PhantasmCRUD<RuleForm, Network>, Map<String, Object>, RuleForm>> updateTemplate) {
        updateState.put(AT_RULEFORM, ruleform);
        updateState.keySet()
                   .stream()
                   .filter(field -> !field.equals(ID)
                                    && !field.equals(AT_RULEFORM)
                                    && updateState.containsKey(field))
                   .forEach(field -> updateTemplate.get(field)
                                                   .apply(crud, updateState));

    }

    private void validateStatic(String type, Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(String.format("Method %s is not a static method.  Cannot plugin %s",
                                                          method.toGenericString(),
                                                          type));
        }
    }
}
