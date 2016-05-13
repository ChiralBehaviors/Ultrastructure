/**
 * Copyright 2016 Yurii Rashkovskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package graphql.annotations;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import graphql.relay.Relay;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldDataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;

/**
 * A utility class for extracting GraphQL data structures from annotated
 * elements.
 */
public class GraphQLAnnotations2 {

    private static class ConnectionDataFetcher implements DataFetcher {
        private final DataFetcher                 actualDataFetcher;
        private final Class<? extends Connection> connection;
        private final Constructor<Connection>     constructor;

        public ConnectionDataFetcher(Class<? extends Connection> connection,
                                     DataFetcher actualDataFetcher) {
            this.connection = connection;
            Optional<Constructor<Connection>> constructor = Arrays.asList(connection.getConstructors())
                                                                  .stream()
                                                                  .filter(c -> c.getParameterCount() == 1)
                                                                  .map(c -> (Constructor<Connection>) c)
                                                                  .findFirst();
            if (constructor.isPresent()) {
                this.constructor = constructor.get();
            } else {
                throw new IllegalArgumentException(connection
                                                   + " doesn't have a single argument constructor");
            }
            this.actualDataFetcher = actualDataFetcher;
        }

        @Override
        public Object get(DataFetchingEnvironment environment) {
            // Exclude arguments
            DataFetchingEnvironment env = new DataFetchingEnvironment(environment.getSource(),
                                                                      new HashMap<>(),
                                                                      environment.getContext(),
                                                                      environment.getFields(),
                                                                      environment.getFieldType(),
                                                                      environment.getParentType(),
                                                                      environment.getGraphQLSchema());
            Connection conn;
            try {
                conn = constructor.newInstance(actualDataFetcher.get(env));
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
            return conn.get(environment);
        }
    }

    private static class defaultGraphQLType implements GraphQLType {

        @Override
        public Class<? extends Annotation> annotationType() {
            return GraphQLType.class;
        }

        @Override
        public Class<? extends TypeFunction> value() {
            return DefaultTypeFunction.class;
        }
    }

    /**
     * Extract GraphQLInterfaceType from an interface
     *
     * @param iface
     *            interface
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     *             if <code>iface</code> is not an interface or doesn't have
     *             <code>@GraphTypeResolver</code> annotation
     */
    public static GraphQLInterfaceType iface(Class<?> iface) throws IllegalAccessException,
                                                             InstantiationException {
        GraphQLInterfaceType.Builder builder = ifaceBuilder(iface);
        return builder.build();
    }

    public static GraphQLInterfaceType.Builder ifaceBuilder(Class<?> iface) throws InstantiationException,
                                                                            IllegalAccessException {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface + " is not an interface");
        }
        GraphQLInterfaceType.Builder builder = newInterface();

        GraphQLName name = iface.getAnnotation(GraphQLName.class);
        builder.name(name == null ? iface.getSimpleName() : name.value());
        GraphQLDescription description = iface.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        for (Method method : iface.getMethods()) {
            boolean valid = !Modifier.isStatic(method.getModifiers())
                            && method.getAnnotation(GraphQLField.class) != null;
            if (valid) {
                builder.field(field(method));
            }
        }
        GraphQLTypeResolver typeResolver = iface.getAnnotation(GraphQLTypeResolver.class);
        if (typeResolver == null) {
            throw new IllegalArgumentException(iface
                                               + " should have @GraphQLTypeResolver annotation defined");
        }
        builder.typeResolver(typeResolver.value()
                                         .newInstance());
        return builder;
    }

    public static GraphQLInputObjectType inputObject(GraphQLObjectType graphQLType) {
        GraphQLObjectType object = graphQLType;
        return new GraphQLInputObjectType(object.getName(),
                                          object.getDescription(),
                                          object.getFieldDefinitions()
                                                .stream()
                                                .map(field -> {
                                                    GraphQLOutputType type = field.getType();
                                                    GraphQLInputType inputType;
                                                    if (type instanceof GraphQLObjectType) {
                                                        inputType = inputObject((GraphQLObjectType) type);
                                                    } else {
                                                        inputType = (GraphQLInputType) type;
                                                    }

                                                    return new GraphQLInputObjectField(field.getName(),
                                                                                       field.getDescription(),
                                                                                       inputType,
                                                                                       null);
                                                })
                                                .collect(Collectors.toList()));
    }

    /**
     * Extract GraphQLObjectType from a class
     *
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     */
    public static GraphQLObjectType object(Class<?> object) throws IllegalAccessException,
                                                            InstantiationException,
                                                            NoSuchMethodException {
        GraphQLObjectType.Builder builder = objectBuilder(object);

        return builder.build();
    }

    public static GraphQLObjectType.Builder objectBuilder(Class<?> object) throws NoSuchMethodException,
                                                                           InstantiationException,
                                                                           IllegalAccessException {
        GraphQLObjectType.Builder builder = newObject();
        GraphQLName name = object.getAnnotation(GraphQLName.class);
        builder.name(name == null ? object.getSimpleName() : name.value());
        GraphQLDescription description = object.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        for (Method method : object.getMethods()) {

            Class<?> declaringClass = getDeclaringClass(method);

            boolean valid = !Modifier.isStatic(method.getModifiers())
                            && (method.getAnnotation(GraphQLField.class) != null
                                || declaringClass.getMethod(method.getName(),
                                                            method.getParameterTypes())
                                                 .getAnnotation(GraphQLField.class) != null);

            if (valid) {
                builder.field(field(method));
            }
        }
        for (Field field : object.getFields()) {
            boolean valid = !Modifier.isStatic(field.getModifiers())
                            && field.getAnnotation(GraphQLField.class) != null;
            if (valid) {
                builder.field(field(field));
            }
        }
        Class<?> current = object;
        do {
            for (Class<?> iface : current.getInterfaces()) {
                if (iface.getAnnotation(GraphQLTypeResolver.class) != null) {
                    builder.withInterface(iface(iface));
                }
            }
            current = current.getSuperclass();
        } while (current != null);
        return builder;
    }

    private static Class<?> getDeclaringClass(Method method) {
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

    private static GraphQLOutputType getGraphQLConnection(boolean isConnection,
                                                          AccessibleObject field,
                                                          GraphQLOutputType type,
                                                          GraphQLOutputType outputType,
                                                          GraphQLFieldDefinition.Builder builder) {
        if (isConnection) {
            if (type instanceof GraphQLList) {
                graphql.schema.GraphQLType wrappedType = ((GraphQLList) type).getWrappedType();
                assert wrappedType instanceof GraphQLObjectType;
                String annValue = field.getAnnotation(GraphQLConnection.class)
                                       .name();
                String connectionName = annValue.isEmpty() ? wrappedType.getName()
                                                           : annValue;
                Relay relay = new Relay();
                GraphQLObjectType edgeType = relay.edgeType(connectionName,
                                                            (GraphQLOutputType) wrappedType,
                                                            null,
                                                            Collections.<GraphQLFieldDefinition> emptyList());
                outputType = relay.connectionType(connectionName, edgeType,
                                                  Collections.emptyList());
                builder.argument(relay.getConnectionFieldArguments());
            }
        }
        return outputType;
    }

    @SuppressWarnings("unchecked")
    private static Function<Map<String, Object>, Object> inputTxfm(Class<?> t,
                                                                   GraphQLObjectType object) {
        List<BiConsumer<Map<String, Object>, Object>> txfms = new ArrayList<>();
        for (GraphQLFieldDefinition f : object.getFieldDefinitions()) {
            Field field;
            try {
                field = t.getField(f.getName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            field.setAccessible(true);
            if (Number.class.isAssignableFrom(field.getType())) {
                txfms.add((m, in) -> {
                    try {
                        field.set(in,
                                  convert((Number) m.get(f.getName()),
                                          (Class<? extends Number>) field.getType()));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            } else {
                txfms.add((m, in) -> {
                    try {
                        field.set(in, m.get(f.getName()));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        }
        return m -> {
            Object in;
            try {
                in = t.getConstructor()
                      .newInstance();
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            txfms.forEach(f -> f.accept(m, in));
            return in;
        };
    }

    private static boolean isConnection(AccessibleObject obj, Class<?> klass,
                                        GraphQLOutputType type) {
        return obj.isAnnotationPresent(GraphQLConnection.class)
               && type instanceof GraphQLList
               && ((GraphQLList) type).getWrappedType() instanceof GraphQLObjectType;
    }

    protected static GraphQLArgument argument(Parameter parameter,
                                              graphql.schema.GraphQLType t) throws IllegalAccessException,
                                                                            InstantiationException {
        GraphQLArgument.Builder builder = newArgument();
        builder.name(parameter.getName());
        builder.type(parameter.getAnnotation(NotNull.class) == null ? (GraphQLInputType) t
                                                                    : new graphql.schema.GraphQLNonNull(t));
        GraphQLDescription description = parameter.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        GraphQLDefaultValue defaultValue = parameter.getAnnotation(GraphQLDefaultValue.class);
        if (defaultValue != null) {
            builder.defaultValue(defaultValue.value()
                                             .newInstance()
                                             .get());
        }
        GraphQLName name = parameter.getAnnotation(GraphQLName.class);
        if (name != null) {
            builder.name(name.value());
        }
        return builder.build();
    }

    protected static GraphQLFieldDefinition field(Field field) throws IllegalAccessException,
                                                               InstantiationException {
        GraphQLFieldDefinition.Builder builder = newFieldDefinition();
        GraphQLName name = field.getAnnotation(GraphQLName.class);
        builder.name(name == null ? field.getName() : name.value());
        GraphQLType annotation = field.getAnnotation(GraphQLType.class);
        if (annotation == null) {
            annotation = new defaultGraphQLType();
        }

        TypeFunction typeFunction = annotation.value()
                                              .newInstance();
        GraphQLOutputType type = (GraphQLOutputType) typeFunction.apply(field.getType(),
                                                                        field.getAnnotatedType());
        if (type instanceof GraphQLObjectType) {
            type = new GraphQLTypeReference(type.getName());
        }

        GraphQLOutputType outputType = field.getAnnotation(NotNull.class) == null ? type
                                                                                  : new GraphQLNonNull(type);

        boolean isConnection = isConnection(field, field.getType(), type);
        outputType = getGraphQLConnection(isConnection, field, type, outputType,
                                          builder);

        builder.type(outputType);

        GraphQLDescription description = field.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }

        GraphQLDeprecate deprecate = field.getAnnotation(GraphQLDeprecate.class);
        if (deprecate != null) {
            builder.deprecate(deprecate.value());
        }
        if (field.getAnnotation(Deprecated.class) != null) {
            builder.deprecate("Deprecated");
        }

        GraphQLDataFetcher dataFetcher = field.getAnnotation(GraphQLDataFetcher.class);
        DataFetcher actualDataFetcher = dataFetcher == null ? new FieldDataFetcher(field.getName())
                                                            : dataFetcher.value()
                                                                         .newInstance();

        if (isConnection) {
            actualDataFetcher = new ConnectionDataFetcher(field.getAnnotation(GraphQLConnection.class)
                                                               .connection(),
                                                          actualDataFetcher);
        }

        builder.dataFetcher(actualDataFetcher);

        return builder.build();
    }

    protected static GraphQLFieldDefinition field(Method method) throws InstantiationException,
                                                                 IllegalAccessException {
        GraphQLFieldDefinition.Builder builder = newFieldDefinition();

        String name = method.getName()
                            .replaceFirst("^(is|get|set)(.+)", "$2");
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        GraphQLName nameAnn = method.getAnnotation(GraphQLName.class);
        builder.name(nameAnn == null ? name : nameAnn.value());

        GraphQLType annotation = method.getAnnotation(GraphQLType.class);
        if (annotation == null) {
            annotation = new defaultGraphQLType();
        }
        TypeFunction typeFunction = annotation.value()
                                              .newInstance();
        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

        GraphQLOutputType type = (GraphQLOutputType) typeFunction.apply(method.getReturnType(),
                                                                        annotatedReturnType);
        if (type instanceof GraphQLObjectType) {
            type = new GraphQLTypeReference(type.getName());
        }

        GraphQLOutputType outputType = method.getAnnotation(NotNull.class) == null ? type
                                                                                   : new GraphQLNonNull(type);

        boolean isConnection = isConnection(method, method.getReturnType(),
                                            type);
        outputType = getGraphQLConnection(isConnection, method, type,
                                          outputType, builder);

        builder.type(outputType);

        Map<Integer, Function<Map<String, Object>, Object>> inputTxfms = new HashMap<>();
        AtomicInteger i = new AtomicInteger(-1);
        List<GraphQLArgument> args = Arrays.asList(method.getParameters())
                                           .stream()
                                           .peek(e -> i.incrementAndGet())
                                           .filter(p -> !DataFetchingEnvironment.class.isAssignableFrom(p.getType()))
                                           .map(new Function<Parameter, GraphQLArgument>() {
                                               @Override
                                               public GraphQLArgument apply(Parameter parameter) {
                                                   Class<?> t = parameter.getType();
                                                   graphql.schema.GraphQLType graphQLType = typeFunction.apply(t,
                                                                                                               parameter.getAnnotatedType());
                                                   if (graphQLType instanceof GraphQLObjectType) {
                                                       GraphQLObjectType objectType = (GraphQLObjectType) graphQLType;
                                                       GraphQLInputObjectType inputObject = inputObject(objectType);
                                                       graphQLType = inputObject;
                                                       inputTxfms.put(i.get(),
                                                                      inputTxfm(t,
                                                                                objectType));
                                                   }
                                                   try {
                                                       return argument(parameter,
                                                                       graphQLType);
                                                   } catch (
                                                           IllegalAccessException
                                                           | InstantiationException e) {
                                                       throw new IllegalStateException(e);
                                                   }
                                               }
                                           })
                                           .collect(Collectors.toList());

        GraphQLFieldDefinition relay = null;
        if (method.isAnnotationPresent(GraphQLRelayMutation.class)) {
            if (!(outputType instanceof GraphQLObjectType)) {
                throw new RuntimeException("outputType should be an object");
            }
            StringBuffer titleBuffer = new StringBuffer(method.getName());
            titleBuffer.setCharAt(0,
                                  Character.toUpperCase(titleBuffer.charAt(0)));
            String title = titleBuffer.toString();
            relay = new Relay().mutationWithClientMutationId(title,
                                                             method.getName(),
                                                             args.stream()
                                                                 .map(t -> new GraphQLInputObjectField(t.getName(),
                                                                                                       t.getType()))
                                                                 .collect(Collectors.toList()),
                                                             ((GraphQLObjectType) outputType).getFieldDefinitions(),
                                                             null);
            builder.argument(relay.getArguments());
            builder.type(relay.getType());
        } else {
            builder.argument(args);
        }

        GraphQLDescription description = method.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }

        GraphQLDeprecate deprecate = method.getAnnotation(GraphQLDeprecate.class);
        if (deprecate != null) {
            builder.deprecate(deprecate.value());
        }
        if (method.getAnnotation(Deprecated.class) != null) {
            builder.deprecate("Deprecated");
        }

        GraphQLDataFetcher dataFetcher = method.getAnnotation(GraphQLDataFetcher.class);
        DataFetcher actualDataFetcher = dataFetcher == null ? new MethodDataFetcher2(method,
                                                                                     inputTxfms)
                                                            : dataFetcher.value()
                                                                         .newInstance();

        if (method.isAnnotationPresent(GraphQLRelayMutation.class)
            && relay != null) {
            actualDataFetcher = new RelayMutationMethodDataFetcher(method, args,
                                                                   relay.getArgument("input")
                                                                        .getType(),
                                                                   relay.getType());
        }

        if (isConnection) {
            actualDataFetcher = new ConnectionDataFetcher(method.getAnnotation(GraphQLConnection.class)
                                                                .connection(),
                                                          actualDataFetcher);
        }

        builder.dataFetcher(actualDataFetcher);

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T convert(Number from, Class<T> to) {
        if (from == null) {
            return null;
        }
        if (to.equals(Byte.class)) {
            return (T) Byte.valueOf(from.byteValue());
        }
        if (to.equals(Double.class)) {
            return (T) Double.valueOf(from.doubleValue());
        }
        if (to.equals(Float.class)) {
            return (T) Float.valueOf(from.floatValue());
        }
        if (to.equals(Integer.class)) {
            return (T) Integer.valueOf(from.intValue());
        }
        if (to.equals(Long.class)) {
            return (T) Long.valueOf(from.longValue());
        }
        if (to.equals(Short.class)) {
            return (T) Short.valueOf(from.shortValue());
        }
        return null;
    }
}
