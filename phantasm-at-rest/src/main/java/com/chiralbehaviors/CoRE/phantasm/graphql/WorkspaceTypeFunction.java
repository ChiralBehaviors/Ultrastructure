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

import static com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessing.iface;
import static com.chiralbehaviors.CoRE.phantasm.graphql.PhantasmProcessing.object;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLBinary;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLJson;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLTimestamp;
import static graphql.schema.GraphQLEnumType.newEnum;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import graphql.Scalars;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLNonNull;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.TypeResolver;

/**
 * @author hhildebrand
 *
 */

public class WorkspaceTypeFunction implements TypeFunction, TypeResolver {
    private static class EnumFunction implements TypeFunction {

        @Override
        public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) aClass;
            GraphQLEnumType.Builder builder = newEnum();

            GraphQLName name = aClass.getAnnotation(GraphQLName.class);
            builder.name(name == null ? aClass.getSimpleName() : name.value());

            GraphQLDescription description = aClass.getAnnotation(GraphQLDescription.class);
            if (description != null) {
                builder.description(description.value());
            }

            List<Enum<?>> constants = Arrays.asList(enumClass.getEnumConstants());

            Arrays.stream(enumClass.getEnumConstants())
                  .map(Enum::name)
                  .forEachOrdered(n -> {
                      try {
                          Field field = aClass.getField(n);
                          GraphQLName fieldName = field.getAnnotation(GraphQLName.class);
                          GraphQLDescription fieldDescription = field.getAnnotation(GraphQLDescription.class);
                          Enum<?> constant = constants.stream()
                                                      .filter(c -> c.name()
                                                                    .contentEquals(n))
                                                      .findFirst()
                                                      .get();
                          String name_ = fieldName == null ? n
                                                           : fieldName.value();
                          builder.value(name_, constant.ordinal(),
                                        fieldDescription == null ? name_
                                                                 : fieldDescription.value());
                      } catch (NoSuchFieldException e) {
                      }
                  });

            return builder.build();
        }
    }

    private class ListFunction implements TypeFunction {

        @Override
        public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
            if (!(annotatedType instanceof AnnotatedParameterizedType)) {
                throw new IllegalArgumentException("List type parameter should be specified");
            }
            AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) annotatedType;
            AnnotatedType arg = parameterizedType.getAnnotatedActualTypeArguments()[0];
            Class<?> klass;
            if (arg.getType() instanceof ParameterizedType) {
                klass = (Class<?>) ((ParameterizedType) (arg.getType())).getRawType();
            } else {
                klass = (Class<?>) arg.getType();
            }
            return new GraphQLList(WorkspaceTypeFunction.this.apply(klass,
                                                                    arg));
        }
    }

    private class ObjectFunction implements TypeFunction {

        private final Map<String, GraphQLTypeReference> processing = new HashMap<>();

        @Override
        public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
            if (types.containsKey(aClass)) {
                return types.get(aClass);
            }
            GraphQLName name = aClass.getAnnotation(GraphQLName.class);
            String typeName = name == null ? aClass.getSimpleName()
                                           : name.value();
            if (processing.containsKey(typeName)) {
                return processing.get(typeName);
            } else {
                processing.put(typeName, new GraphQLTypeReference(typeName));
                GraphQLType type;
                if (aClass.isInterface()
                    && aClass.getAnnotation(GraphQLInterface.class) != null) {
                    type = iface(aClass, WorkspaceTypeFunction.this,
                                 WorkspaceTypeFunction.this);
                } else {
                    type = object(aClass, WorkspaceTypeFunction.this,
                                  WorkspaceTypeFunction.this);
                }
                processing.remove(typeName);
                types.put(aClass, type);
                return type;
            }
        }
    }

    private class OptionalFunction implements TypeFunction {

        @Override
        public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
            if (!(annotatedType instanceof AnnotatedParameterizedType)) {
                throw new IllegalArgumentException("Optional type parameter should be specified");
            }
            AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) annotatedType;
            AnnotatedType arg = parameterizedType.getAnnotatedActualTypeArguments()[0];
            Class<?> klass;
            if (arg.getType() instanceof ParameterizedType) {
                klass = (Class<?>) ((ParameterizedType) (arg.getType())).getRawType();
            } else {
                klass = (Class<?>) arg.getType();
            }
            return WorkspaceTypeFunction.this.apply(klass, arg);
        }
    }

    private class StreamFunction implements TypeFunction {

        @Override
        public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
            if (!(annotatedType instanceof AnnotatedParameterizedType)) {
                throw new IllegalArgumentException("Stream type parameter should be specified");
            }
            AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) annotatedType;
            AnnotatedType arg = parameterizedType.getAnnotatedActualTypeArguments()[0];
            Class<?> klass;
            if (arg.getType() instanceof ParameterizedType) {
                klass = (Class<?>) ((ParameterizedType) (arg.getType())).getRawType();
            } else {
                klass = (Class<?>) arg.getType();
            }
            return new GraphQLList(WorkspaceTypeFunction.this.apply(klass,
                                                                    arg));
        }
    }

    private final Map<String, BiFunction<Class<?>, AnnotatedType, GraphQLType>> registry;
    private final Map<Class<?>, GraphQLType>                                    types = new HashMap<>();

    {
        registry = new HashMap<>();
        register(String.class, (u, t) -> Scalars.GraphQLString);
        register(Boolean.class, (u, t) -> Scalars.GraphQLBoolean);
        register(boolean.class, (u, t) -> Scalars.GraphQLBoolean);
        register(Float.class, (u, t) -> Scalars.GraphQLFloat);
        register(float.class, (u, t) -> Scalars.GraphQLFloat);
        register(Integer.class, (u, t) -> Scalars.GraphQLInt);
        register(int.class, (u, t) -> Scalars.GraphQLInt);
        register(Long.class, (u, t) -> Scalars.GraphQLLong);
        register(long.class, (u, t) -> Scalars.GraphQLLong);
        register(BigDecimal.class, (u, t) -> Scalars.GraphQLBigDecimal);
        register(JsonNode.class, (u, t) -> GraphQLJson);
        register(Timestamp.class, (u, t) -> GraphQLTimestamp);
        register(byte[].class, (u, t) -> GraphQLBinary);
        register(AbstractList.class, new ListFunction());
        register(List.class, new ListFunction());
        register(Stream.class, new StreamFunction());
        register(Enum.class, new EnumFunction());
        register(Optional.class, new OptionalFunction());
        register(Object.class, new ObjectFunction());
    }

    @Override
    public GraphQLType apply(Class<?> klass, AnnotatedType annotatedType) {
        Class<?> t = klass;

        while (!registry.containsKey(t.getName())) {
            if (t.getSuperclass() == null && t.isInterface()) {
                t = Object.class;
                continue;
            }
            t = t.getSuperclass();
            if (t == null) {
                throw new IllegalArgumentException("unsupported type");
            }
        }

        GraphQLType result = registry.get(t.getName())
                                     .apply(klass, annotatedType);

        if (klass.getAnnotation(GraphQLNonNull.class) != null
            || (annotatedType != null
                && annotatedType.getAnnotation(GraphQLNonNull.class) != null)) {
            result = new graphql.schema.GraphQLNonNull(result);
        }

        return result;
    }

    public GraphQLType getType(Class<?> type) {
        return types.get(type);
    }

    @Override
    public GraphQLObjectType getType(Object object) {
        return (GraphQLObjectType) apply(object.getClass(), null);
    }

    public Class<WorkspaceTypeFunction> register(Class<?> klass,
                                                 TypeFunction function) {
        registry.put(klass.getName(), function);
        return WorkspaceTypeFunction.class;
    }
}