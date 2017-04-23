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

package com.chiralbehaviors.CoRE.phantasm.graphql.jooq;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLBinary;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLJson;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLTimestamp;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLID;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.graphql.UuidUtil;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceTypeFunction;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;

/**
 * @author halhildebrand
 *
 */
public class JooqSchema {
    private static final Set<String>           IGNORE       = new HashSet<String>() {
                                                                private static final long serialVersionUID = 1L;

                                                                {
                                                                    add("key");
                                                                    add("table");
                                                                    add("class");
                                                                    add("value");
                                                                }

                                                            };

    private static final WorkspaceTypeFunction typeFunction = new WorkspaceTypeFunction() {
                                                                {

                                                                    register(UUID.class,
                                                                             (u,
                                                                              t) -> GraphQLID);
                                                                    ;
                                                                }
                                                            };
    private static final ObjectMapper          MAPPER       = new ObjectMapper();

    public void contributeTo(GraphQLObjectType.Builder query,
                             GraphQLObjectType.Builder mutation) {
        Ruleform.RULEFORM.getTables()
                         .stream()
                         .map(table -> table.getRecordType())
                         .forEach(record -> contributeTo(query, record,
                                                         mutation));
    }

    private GraphQLFieldDefinition.Builder build(GraphQLFieldDefinition.Builder builder,
                                                 PropertyDescriptor field) {
        builder.name(field.getName())
               .type((GraphQLOutputType) type(field))
               .dataFetcher(env -> {
                   Object record = env.getSource();
                   Object result;
                   try {
                       result = field.getReadMethod()
                                     .invoke(record);
                   } catch (IllegalAccessException | IllegalArgumentException
                           | InvocationTargetException e) {
                       throw new IllegalStateException(String.format("unable to invoke %s",
                                                                     field.getReadMethod()
                                                                          .toGenericString()),
                                                       e);
                   }
                   return encode((GraphQLOutputType) type(field), result);
               });
        return builder;
    }

    private void contributeCreate(Builder mutation, Class<?> record,
                                  GraphQLObjectType type,
                                  List<PropertyDescriptor> fields,
                                  Map<String, GraphQLType> types) {
        GraphQLInputObjectType.Builder updateBuilder = GraphQLInputObjectType.newInputObject()
                                                                             .name(String.format("create%sState",
                                                                                                 translated(record)));

        fields.stream()
              .filter(field -> !field.getName()
                                     .equals("id"))
              .forEach(field -> {
                  updateBuilder.field(b -> {
                      return b.name(field.getName())
                              .type((GraphQLInputType) type(field));
                  });
              });
        GraphQLInputObjectType update = updateBuilder.build();
        mutation.field(b -> b.name(String.format("create%s",
                                                 translated(record)))
                             .type(type)
                             .argument(a -> a.name("state")
                                             .type(update)
                                             .description(String.format("Create state of the %s",
                                                                        translated(record))))
                             .dataFetcher(env -> {
                                 return create(record, types, env);
                             }));
    }

    private String translated(Class<?> record) {
        String simpleName = record.getSimpleName()
                                  .substring(0, record.getSimpleName()
                                                      .lastIndexOf("Record"));
        if (!simpleName.startsWith("Existential")) {
            return simpleName;
        }
        return simpleName != "Existential" ? simpleName.substring("Existential".length())
                                           : simpleName;
    }

    private void contributeDelete(Builder mutation, Class<?> record,
                                  GraphQLObjectType type,
                                  List<PropertyDescriptor> fields,
                                  Map<String, GraphQLType> types) {
        mutation.field(b -> b.name(String.format("delete%s",
                                                 translated(record)))
                             .type(GraphQLBoolean)
                             .argument(a -> a.name("id")
                                             .type(new GraphQLNonNull(GraphQLID))
                                             .description(String.format("ID of the %s",
                                                                        translated(record))))
                             .dataFetcher(env -> {
                                 return delete(record, types, env);
                             }));
    }

    private void contributeMutations(Builder mutation, Class<?> record,
                                     GraphQLObjectType type,
                                     List<PropertyDescriptor> fields) {
        Map<String, GraphQLType> types = fields.stream()
                                               .collect(Collectors.toMap(field -> field.getName(),
                                                                         field -> type(field)));
        contributeCreate(mutation, record, type, fields, types);
        contributeUpdate(mutation, record, type, fields, types);
        contributeDelete(mutation, record, type, fields, types);
    }

    @SuppressWarnings("unchecked")
    private void contributeQueries(Builder query, Class<?> record,
                                   GraphQLObjectType type) {
        query.field(b -> b.name(translated(record))
                          .type(type)
                          .argument(a -> a.name("id")
                                          .type(new GraphQLNonNull(GraphQLID))
                                          .description(String.format("ID of the %s",
                                                                     translated(record))))
                          .dataFetcher(env -> {
                              UUID id = decode(GraphQLID,
                                               env.getArgument("id"));
                              return fetch(id, record, env);
                          }));
        query.field(b -> b.name(String.format("%ss", translated(record)))
                          .type(new GraphQLList(type))
                          .argument(a -> a.name("ids")
                                          .type(new GraphQLList(GraphQLID))
                                          .description(String.format("IDs of the %s",
                                                                     translated(record))))
                          .dataFetcher(env -> {
                              List<String> ids = (List<String>) env.getArgument("ids");
                              if (ids == null) {
                                  return fetchAll(record, env);
                              }

                              return ids.stream()
                                        .map(s -> (UUID) decode(GraphQLID, s))
                                        .map(id -> fetch(id, record, env))
                                        .collect(Collectors.toList());
                          }));
    }

    private void contributeTo(Builder query, Class<?> record,
                              Builder mutation) {
        List<PropertyDescriptor> fields = Arrays.asList(PropertyUtils.getPropertyDescriptors(record))
                                                .stream()
                                                .filter(field -> !IGNORE.contains(field.getName()))
                                                .collect(Collectors.toList());
        GraphQLObjectType type = objectType(record, fields);
        contributeQueries(query, record, type);
        contributeMutations(mutation, record, type, fields);
    }

    private void contributeUpdate(Builder mutation, Class<?> record,
                                  GraphQLObjectType type,
                                  List<PropertyDescriptor> fields,
                                  Map<String, GraphQLType> types) {
        GraphQLInputObjectType.Builder updateBuilder = GraphQLInputObjectType.newInputObject()
                                                                             .name(String.format("update%sState",
                                                                                                 translated(record)));

        fields.forEach(field -> {
            updateBuilder.field(b -> {
                return b.name(field.getName())
                        .type((GraphQLInputType) type(field));
            });
        });
        GraphQLInputObjectType update = updateBuilder.build();
        mutation.field(b -> b.name(String.format("update%s",
                                                 translated(record)))
                             .type(type)
                             .argument(a -> a.name("state")
                                             .type(update)
                                             .description(String.format("Update state of the %s",
                                                                        translated(record))))
                             .dataFetcher(env -> {
                                 return update(record, types, env);
                             }));
    }

    @SuppressWarnings({ "unchecked" })
    private <T> T create(Class<?> record, Map<String, GraphQLType> types,
                         DataFetchingEnvironment env) {
        DSLContext create = WorkspaceSchema.ctx(env)
                                           .create();
        UpdatableRecord<?> instance;
        try {
            instance = (UpdatableRecord<?>) record.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Cannot create test instance on %s",
                                                          record.getCanonicalName()),
                                            e);
        }
        Table<?> table = instance.getTable();
        instance = (UpdatableRecord<?>) create.newRecord(table);
        try {
            PropertyUtils.setProperty(instance, "id",
                                      RecordsFactory.GENERATOR.generate());
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalStateException();
        }
        set(instance, types, env);
        instance.insert();
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    private <T> T decode(GraphQLType type, Object value) {
        if (value == null) {
            return null;
        }
        if (type.equals(GraphQLID)) {
            return (T) UuidUtil.decode(((String) value));
        }
        if (type.equals(GraphQLJson)) {
            try {
                return (T) MAPPER.readTree((String) value);
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }
        return (T) value;
    }

    private Object delete(Class<?> record, Map<String, GraphQLType> types,
                          DataFetchingEnvironment env) {
        UUID id = decode(GraphQLID, env.getArgument("id"));
        UpdatableRecord<?> instance = fetch(id, record, env);
        instance.delete();
        return true;
    }

    private Object encode(GraphQLOutputType type, Object value) {
        if (value == null) {
            return null;
        }
        if (type.equals(GraphQLID)) {
            return UuidUtil.encode(((UUID) value));
        }
        if (type.equals(GraphQLBinary)) {
            return Base64.getEncoder()
                         .encodeToString((byte[]) value);
        }
        if (type.equals(GraphQLTimestamp)) {
            return ((Timestamp) value).getTime();
        }
        return value;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> T fetch(UUID id, Class<?> record, DataFetchingEnvironment env) {
        UpdatableRecord<?> instance;
        try {
            instance = (UpdatableRecord<?>) record.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Cannot create test instance on %s",
                                                          record.getCanonicalName()),
                                            e);
        }
        Table<?> table = instance.getTable();
        Field field;
        try {
            field = (Field) table.getClass()
                                 .getField("ID")
                                 .get(table);
        } catch (DataAccessException | IllegalArgumentException
                | IllegalAccessException | NoSuchFieldException
                | SecurityException e) {
            throw new IllegalStateException(String.format("Cannot access 'id' field on %s",
                                                          record.getCanonicalName()),
                                            e);
        }
        return (T) WorkspaceSchema.ctx(env)
                                  .create()
                                  .selectFrom(table)
                                  .where(field.eq(id))
                                  .fetchOne();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> T fetchAll(Class<?> record, DataFetchingEnvironment env) {
        UpdatableRecord<?> instance;
        try {
            instance = (UpdatableRecord<?>) record.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Cannot create test instance on %s",
                                                          record.getCanonicalName()),
                                            e);
        }
        Table<?> table = instance.getTable();
        Field field;
        try {
            field = (Field) table.getClass()
                                 .getField("WORKSPACE")
                                 .get(table);
        } catch (DataAccessException | IllegalArgumentException
                | IllegalAccessException | NoSuchFieldException
                | SecurityException e) {
            throw new IllegalStateException(String.format("Cannot access 'id' field on %s",
                                                          record.getCanonicalName()),
                                            e);
        }
        return (T) WorkspaceSchema.ctx(env)
                                  .create()
                                  .selectFrom(table)
                                  .where(field.eq(WorkspaceContext.getWorkspace(env)
                                                                  .getId()))
                                  .fetch()
                                  .stream()
                                  .collect(Collectors.toList());
    }

    private GraphQLObjectType objectType(Class<?> clazz,
                                         List<PropertyDescriptor> fields) {
        GraphQLObjectType.Builder builder = new GraphQLObjectType.Builder();
        builder.name(clazz.getSimpleName());
        fields.forEach(field -> {
            builder.field(f -> build(f, field));
        });
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private void set(UpdatableRecord<?> instance,
                     Map<String, GraphQLType> types,
                     DataFetchingEnvironment env) {
        ((Map<String, Object>) env.getArgument("state")).entrySet()
                                                        .stream()
                                                        .filter(entry -> !entry.getKey()
                                                                               .equals("id"))
                                                        .forEach(entry -> {
                                                            try {
                                                                PropertyUtils.setProperty(instance,
                                                                                          entry.getKey(),
                                                                                          decode(types.get(entry.getKey()),
                                                                                                 entry.getValue()));
                                                            } catch (
                                                                    IllegalAccessException
                                                                    | InvocationTargetException
                                                                    | NoSuchMethodException e) {
                                                                throw new IllegalArgumentException(String.format("Illegal property: %s",
                                                                                                                 entry));
                                                            }
                                                        });
    }

    private GraphQLType type(PropertyDescriptor field) {
        Method readMethod = field.getReadMethod();
        return typeFunction.apply(readMethod.getReturnType(),
                                  readMethod.getAnnotatedReturnType());
    }

    private Object update(Class<?> record, Map<String, GraphQLType> types,
                          DataFetchingEnvironment env) {
        @SuppressWarnings("unchecked")
        UUID id = decode(GraphQLID,
                         ((Map<String, Object>) env.getArgument("state")).get("id"));
        UpdatableRecord<?> instance = fetch(id, record, env);
        set(instance, types, env);
        instance.update();
        return instance;
    }
}
