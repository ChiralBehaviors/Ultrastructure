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

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceScalarTypes.GraphQLUuid;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.graphql.EdgeTypeResolver;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmInitializer;
import com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessor;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceScalarTypes;
import com.chiralbehaviors.CoRE.phantasm.graphql.ZtypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.PhantasmContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.context.PhantasmContext.Traversal;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Initializer;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle;
import com.chiralbehaviors.CoRE.utils.English;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.Scalars;
import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
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

    static class Resolution {
        final Function<Object, JsonNode> inverse;
        final Function<JsonNode, Object> transform;
        final GraphQLType                type;

        Resolution(Function<JsonNode, Object> transform,
                   Function<Object, JsonNode> inverse, GraphQLType type) {
            this.inverse = inverse;
            this.transform = transform;
            this.type = type;
        }
    }

    private static final String _EDGE              = "_edge";
    private static final String _EXT               = "_ext";
    private static final String ADD_TEMPLATE       = "add%s";
    private static final String APPLY_MUTATION     = "apply%s";
    private static final String ARRAY              = "array";

    private static final String AT_RULEFORM        = "@ruleform";

    private static final String BOOLEAN            = "boolean";
    private static final String CREATE_MUTATION    = "create%s";
    private static final String CREATE_TYPE        = "%sCreate";
    private static final String EXISTENTIAL        = "Existential";
    private static final String ID                 = "id";
    private static final String IDS                = "ids";
    private static final String IMMEDIATE_TEMPLATE = "immediate%s";
    private static final String INTEGER            = "integer";
    private static final String ITEMS              = "items";
    private static final String NUMBER             = "number";
    private static final String OBJECT             = "object";
    private static final String REMOVE_MUTATION    = "remove%s";
    private static final String REMOVE_TEMPLATE    = "remove%s";
    private static final String SET_TEMPLATE       = "set%s";
    private static final String STATE              = "state";
    private static final String STRING             = "string";
    private static final String TYPE               = "type";
    private static final String UPDATE_MUTATION    = "update%s";
    private static final String UPDATE_TYPE        = "%sUpdate";

    public static String capitalized(String baseName) {
        return Character.toUpperCase(baseName.charAt(0))
               + (baseName.length() == 1 ? "" : baseName.substring(1));
    }

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

    public static List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> processPlugin(Class<?> plugin,
                                                                                               GraphQLObjectType.Builder builder,
                                                                                               PhantasmProcessor processor,
                                                                                               GraphQLType type) {
        Plugin annotation = plugin.getAnnotation(Plugin.class);
        if (annotation == null) {
            throw new IllegalArgumentException(String.format("Class not annotated with @Plugin: %s",
                                                             plugin.getCanonicalName()));
        }
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers = new ArrayList<>();
        Class<? extends Phantasm> phantasm = annotation.value();
        processor.registerType(new ZtypeFunction(phantasm, type));
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

    public static String toFieldName(String name) {
        return Introspector.decapitalize(name.replaceAll("\\s", ""));
    }

    public static String toTypeName(String name) {
        char chars[] = toValidName(name).toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String toValidName(String name) {
        name = name.replaceAll("\\s", "");
        StringBuilder sb = new StringBuilder();
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            sb.append("_");
        }
        for (char c : name.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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

    private graphql.schema.GraphQLInputObjectType.Builder                  createTypeBuilder;
    private List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers   = new ArrayList<>();
    private String                                                         name;
    private Set<FacetRecord>                                               references     = new HashSet<>();
    private Builder                                                        typeBuilder;

    private Map<String, BiConsumer<PhantasmCRUD, Map<String, Object>>>     updateTemplate = new HashMap<>();

    private graphql.schema.GraphQLInputObjectType.Builder                  updateTypeBuilder;

    public FacetFields(Aspect facet) {
        super(facet);
        name = toTypeName(facet.getName());
        typeBuilder = newObject().name(name);
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                name));
        createTypeBuilder = newInputObject().name(String.format(CREATE_TYPE,
                                                                name));
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
        return new GraphQLTypeReference(toTypeName(facet.getName()));
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

    private void addAttribute(String name, JsonNode value) {
        String set = String.format(SET_TEMPLATE, capitalized(name));
        Resolution resolution = outputTypeFor(value);
        typeBuilder.field(new GraphQLFieldDefinition.Builder().type((GraphQLOutputType) resolution.type)
                                                              .name(name)
                                                              .dataFetcher(env -> {
                                                                  JsonNode properties = ctx(env).getFacetProperty(facet,
                                                                                                                  ((Phantasm) env.getSource()).getRuleform());
                                                                  return properties == null ? null
                                                                                            : resolution.transform.apply(properties.get(name));
                                                              })
                                                              .description(name
                                                                           + " property")
                                                              .build());
        GraphQLInputObjectField field = newInputObjectField().type((GraphQLInputType) resolution.type)
                                                             .name(set)
                                                             .description("set "
                                                                          + name
                                                                          + " property")
                                                             .build();
        updateTypeBuilder.field(field);
        createTypeBuilder.field(field);
        updateTemplate.put(set, (crud, update) -> {
            ExistentialRuleform instance = ((Phantasm) update.get(AT_RULEFORM)).getRuleform();
            ObjectNode properties = (ObjectNode) crud.getFacetProperty(facet,
                                                                       instance);
            if (properties == null) {
                properties = JsonNodeFactory.instance.objectNode();
            }
            properties.set(name, resolution.inverse.apply(update.get(set)));
            crud.setFacetProperty(facet, instance, properties);
        });
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
                                                          processor,
                                                          new GraphQLTypeReference(name)));
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

    private Function<Object, JsonNode> arrayInverse(Function<Object, JsonNode> transform) {
        return obj -> {
            if (obj == null) {
                return null;
            }
            ArrayNode result = JsonNodeFactory.instance.arrayNode();
            ((List<?>) obj).forEach(o -> result.add(transform.apply(o)));
            return result;
        };
    }

    private Function<JsonNode, Object> arrayTransform(Function<JsonNode, Object> transform) {
        return array -> {
            if (array == null) {
                return null;
            }
            List<Object> result = new ArrayList<>();
            ((ArrayNode) array).forEach(n -> {
                result.add(transform.apply(n));
            });
            return result;
        };
    }

    private Resolution arrayType(JsonNode value) {
        JsonNode items = value.get(ITEMS);
        if (!items.get(TYPE)
                  .textValue()
                  .equals(ARRAY)) {
            Resolution inner = outputTypeFor(items);
            return new Resolution(arrayTransform(inner.transform),
                                  arrayInverse(inner.inverse),
                                  new GraphQLList(inner.type));
        }
        Resolution inner = arrayType(items);
        return new Resolution(arrayTransform(inner.transform),
                              arrayInverse(inner.inverse),
                              new GraphQLList(inner.type));
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
        buildAttributes();
        childAuthorizations.values()
                           .forEach(auth -> visitChildren(auth));
        singularAuthorizations.values()
                              .forEach(auth -> visitSingular(auth));
    }

    private void buildAttributes() {
        if (schema == null) {
            return;
        }
        ObjectNode props = (ObjectNode) schema.get("properties");
        props.fields()
             .forEachRemaining(entry -> {
                 addAttribute(entry.getKey(), entry.getValue());
             });
    }

    private void buildEdge(NetworkAuthorization auth,
                           EdgeTypeResolver edgeTypeResolver,
                           GraphQLUnionType.Builder union) {
        String facetName = toFieldName(name);
        Builder edgeTypeBuilder = newObject().name(String.format("_%s_%s",
                                                                 facetName,
                                                                 auth.getFieldName()))
                                             .description(String.format("The edge \"%s\" from facet \"%s\"",
                                                                        auth.getFieldName(),
                                                                        facetName));
        JsonNode propertySchema = facet.getFacet()
                                       .getSchema();
        if (propertySchema != null) {
            edgeTypeBuilder.field(newFieldDefinition().type(edgePropertyType(auth,
                                                                             propertySchema))
                                                      .name("properties")
                                                      .description("Json properties for edge")
                                                      .dataFetcher(env -> {
                                                          PhantasmContext ctx = ctx(env);
                                                          Traversal edge = (Traversal) env.getSource();
                                                          if (edge == null
                                                              || !edge.auth.equals(auth)) {
                                                              return null;
                                                          }
                                                          return ctx.getProperties(edge.parent,
                                                                                   edge.auth,
                                                                                   edge.child);
                                                      })
                                                      .build());
        } else {
            edgeTypeBuilder.field(newFieldDefinition().type(WorkspaceScalarTypes.GraphQLJson)
                                                      .name("properties")
                                                      .description("Json properties for edge")
                                                      .dataFetcher(env -> {
                                                          PhantasmContext ctx = ctx(env);
                                                          Traversal edge = (Traversal) env.getSource();
                                                          if (edge == null
                                                              || !edge.auth.equals(auth)) {
                                                              return null;
                                                          }
                                                          return ctx.getProperties(edge.parent,
                                                                                   edge.auth,
                                                                                   edge.child);
                                                      })
                                                      .build());
        }
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

    private GraphQLOutputType edgePropertyType(NetworkAuthorization auth,
                                               JsonNode propertySchema) {
        return WorkspaceScalarTypes.GraphQLJson;
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

    private Resolution outputTypeFor(JsonNode value) {
        switch (value.get(TYPE)
                     .asText()) {
            case OBJECT: {
                return new Resolution(node -> node, node -> (JsonNode) node,
                                      WorkspaceScalarTypes.GraphQLJson);
            }
            case STRING: {
                return new Resolution(node -> (node == null) ? null
                                                             : node.asText(),
                                      s -> JsonNodeFactory.instance.textNode((String) s),
                                      Scalars.GraphQLString);
            }
            case NUMBER: {
                return new Resolution(node -> (node == null) ? null
                                                             : node.asDouble(),
                                      s -> JsonNodeFactory.instance.numberNode((Double) s),
                                      Scalars.GraphQLFloat);
            }
            case BOOLEAN: {
                return new Resolution(node -> (node == null) ? null
                                                             : node.asBoolean(),
                                      s -> JsonNodeFactory.instance.booleanNode((Boolean) s),
                                      Scalars.GraphQLBoolean);
            }
            case INTEGER: {
                return new Resolution(node -> (node == null) ? null
                                                             : node.asInt(),
                                      s -> JsonNodeFactory.instance.numberNode((Integer) s),
                                      Scalars.GraphQLInt);
            }
            case ARRAY: {
                return arrayType(value);
            }
            default:
                throw new IllegalArgumentException("Cannot convert to a GraphQLType: "
                                                   + value);
        }
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
}
