/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;

import graphql.annotations.BatchedMethodDataFetcher;
import graphql.annotations.BatchedTypeFunction;
import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.GraphQLBatched;
import graphql.annotations.GraphQLDataFetcher;
import graphql.annotations.GraphQLDeprecate;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLInvokeDetached;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.TypeResolver;

/**
 * @author halhildebrand
 *
 */
public class PhantasmProcessor extends GraphQLAnnotations
        implements TypeResolver {

    public PhantasmProcessor() {
        super();
        instance = this; // stupid, but true
    }

    /* (non-Javadoc)
     * @see graphql.schema.TypeResolver#getType(java.lang.Object)
     */
    @Override
    public GraphQLObjectType getType(Object object) {
        return (GraphQLObjectType) defaultTypeFunction.apply(object.getClass(),
                                                             null);
    }

    public graphql.schema.GraphQLType typeFor(Class<?> clazz) {
        return defaultTypeFunction.apply(clazz, null);
    }

    public TypeFunction typeResolver() {
        return defaultTypeFunction;
    }

    @SuppressWarnings("deprecation")
    public GraphQLFieldDefinition getPluginField(Method method,
                                                 Class<? extends Phantasm> phantasm) throws InstantiationException,
                                                                                     IllegalAccessException {
        GraphQLFieldDefinition.Builder builder = newFieldDefinition();

        String name = method.getName()
                            .replaceFirst("^(is|get|set)(.+)", "$2");
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        GraphQLName nameAnn = method.getAnnotation(GraphQLName.class);
        builder.name(nameAnn == null ? name : nameAnn.value());

        GraphQLType annotation = method.getAnnotation(GraphQLType.class);
        TypeFunction typeFunction = defaultTypeFunction;

        if (annotation != null) {
            typeFunction = annotation.value()
                                     .newInstance();
        }
        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

        TypeFunction outputTypeFunction;
        if (method.getAnnotation(GraphQLBatched.class) != null) {
            outputTypeFunction = new BatchedTypeFunction(typeFunction);
        } else {
            outputTypeFunction = typeFunction;
        }

        GraphQLOutputType type = (GraphQLOutputType) outputTypeFunction.apply(method.getReturnType(),
                                                                              annotatedReturnType);
        GraphQLOutputType outputType = method.getAnnotation(NotNull.class) == null ? type
                                                                                   : new GraphQLNonNull(type);

        builder.type(outputType);

        TypeFunction finalTypeFunction = typeFunction;
        List<GraphQLArgument> args = Arrays.asList(method.getParameters())
                                           .stream()
                                           .filter(p -> !DataFetchingEnvironment.class.isAssignableFrom(p.getType()))
                                           .map(p -> {
                                               Class<?> t = p.getType();
                                               graphql.schema.GraphQLType graphQLType = finalTypeFunction.apply(t,
                                                                                                                p.getAnnotatedType());
                                               if (graphQLType instanceof GraphQLObjectType) {
                                                   GraphQLInputObjectType inputObject = getInputObject((GraphQLObjectType) graphQLType);
                                                   graphQLType = inputObject;
                                               }
                                               try {
                                                   return getArgument(p,
                                                                      graphQLType);
                                               } catch (IllegalAccessException
                                                       | InstantiationException e) {
                                                   throw new IllegalStateException(e);
                                               }
                                           })
                                           .collect(Collectors.toList());
        builder.argument(args);

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
        DataFetcher actualDataFetcher;
        if (dataFetcher == null
            && method.getAnnotation(GraphQLBatched.class) != null) {
            actualDataFetcher = new BatchedMethodDataFetcher(method,
                                                             typeFunction);
        } else if (dataFetcher == null) {
            actualDataFetcher = new MethodDataFetcher(method, typeFunction,
                                                      phantasm);
        } else {
            actualDataFetcher = dataFetcher.value()
                                           .newInstance();
        }

        builder.dataFetcher(actualDataFetcher);

        return new GraphQLFieldDefinitionWrapper(builder.build());
    }

    static class MethodDataFetcher implements DataFetcher {
        private final Method                    method;
        private final TypeFunction              typeFunction;
        private final Class<? extends Phantasm> phantasm;

        public MethodDataFetcher(Method method, TypeFunction typeFunction,
                                 Class<? extends Phantasm> phantasm) {
            this.method = method;
            this.typeFunction = typeFunction;
            this.phantasm = phantasm;
        }

        @SuppressWarnings("deprecation")
        @Override
        public Object get(DataFetchingEnvironment environment) {
            environment = new DataFetchingEnvironment(WorkspaceSchema.ctx(environment)
                                                                     .wrap(phantasm,
                                                                           (ExistentialRuleform) environment.getSource()),
                                                      environment.getArguments(),
                                                      environment.getContext(),
                                                      environment.getFields(),
                                                      environment.getFieldType(),
                                                      environment.getParentType(),
                                                      environment.getGraphQLSchema());
            try {
                Object obj;

                if (Modifier.isStatic(method.getModifiers())) {
                    obj = null;
                } else if (method.getAnnotation(GraphQLInvokeDetached.class) == null) {
                    obj = environment.getSource();
                    if (obj == null) {
                        return null;
                    }
                } else {
                    obj = method.getDeclaringClass()
                                .newInstance();
                }
                return method.invoke(obj, invocationArgs(environment));
            } catch (IllegalAccessException | InvocationTargetException
                    | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        private Object[] invocationArgs(DataFetchingEnvironment environment) {
            List<Object> result = new ArrayList<>();
            Iterator<Object> envArgs = environment.getArguments()
                                                  .values()
                                                  .iterator();
            for (Parameter p : method.getParameters()) {
                Class<?> paramType = p.getType();
                if (DataFetchingEnvironment.class.isAssignableFrom(paramType)) {
                    result.add(environment);
                    continue;
                }
                graphql.schema.GraphQLType graphQLType = typeFunction.apply(paramType,
                                                                            p.getAnnotatedType());
                if (graphQLType instanceof GraphQLObjectType) {
                    Constructor<?> constructor;
                    try {
                        constructor = paramType.getConstructor(HashMap.class);
                        result.add(constructor.newInstance(envArgs.next()));
                    } catch (NoSuchMethodException | SecurityException
                            | InstantiationException | IllegalAccessException
                            | IllegalArgumentException
                            | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }

                } else {
                    result.add(envArgs.next());
                }
            }
            return result.toArray();
        }
    }
}
