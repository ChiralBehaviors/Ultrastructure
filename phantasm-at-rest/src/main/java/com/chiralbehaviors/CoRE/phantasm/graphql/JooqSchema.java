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

import static com.chiralbehaviors.CoRE.jooq.Ruleform.RULEFORM;
import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspsacScalarTypes.GraphQLUuid;
import static graphql.Scalars.GraphQLBoolean;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

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
import graphql.schema.GraphQLTypeReference;

/**
 * @author halhildebrand
 *
 */
public class JooqSchema {
    private final static Converter<String, String> converter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);
    private static final Set<String>               IGNORE    = new HashSet<String>() {
                                                                 private static final long serialVersionUID = 1L;

                                                                 {
                                                                     add("key");
                                                                     add("table");
                                                                     add("class");
                                                                     add("value");
                                                                 }

                                                             };
    private static final Logger                    log       = LoggerFactory.getLogger(JooqSchema.class);
    private static final Map<Class<?>, Table<?>>   TABLES    = new HashMap<>();
    static {
        Ruleform.RULEFORM.getTables()
                         .forEach(table -> TABLES.put(table.getRecordType(),
                                                      table));
    }

    public static JooqSchema meta() {
        List<Table<?>> manifested = Ruleform.RULEFORM.getTables();
        manifested.removeAll(Arrays.asList(new Table[] { RULEFORM.EXISTENTIAL,
                                                         RULEFORM.EXISTENTIAL_ATTRIBUTE,
                                                         RULEFORM.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION,
                                                         RULEFORM.EXISTENTIAL_NETWORK_ATTRIBUTE,
                                                         RULEFORM.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION,
                                                         RULEFORM.EXISTENTIAL_NETWORK_AUTHORIZATION,
                                                         RULEFORM.EXISTENTIAL_NETWORK,
                                                         RULEFORM.WORKSPACE_LABEL,
                                                         RULEFORM.JOB,
                                                         RULEFORM.JOB_CHRONOLOGY }));
        return new JooqSchema(manifested);
    }

    private static String camel(String snake) {
        return Introspector.decapitalize(converter.convert(snake));
    }

    private final List<Table<?>>   manifested;
    private final Set<GraphQLType> types = new HashSet<>();

    public JooqSchema(List<Table<?>> manifested) {
        this.manifested = manifested;
        manifested.stream()
                  .forEach(table -> {
                      GraphQLType type = new GraphQLTypeReference(translated(table.getRecordType()));
                      PhantasmProcessor.getSingleton()
                                       .registerType(new ZtypeFunction(table.getRecordType(),
                                                                       type));
                  });
    }

    public void contributeTo(GraphQLObjectType.Builder query,
                             GraphQLObjectType.Builder mutation) {
        manifested.stream()
                  .map(table -> table.getRecordType())
                  .forEach(record -> contributeTo(query, record, mutation));
    }

    public Set<GraphQLType> getTypes() {
        return types;
    }

    private GraphQLFieldDefinition.Builder buildPrimitive(GraphQLFieldDefinition.Builder builder,
                                                          PropertyDescriptor field) {
        builder.name(field.getName())
               .type((GraphQLOutputType) type(field))
               .dataFetcher(env -> {
                   Object record = env.getSource();
                   try {
                       return field.getReadMethod()
                                   .invoke(record);
                   } catch (IllegalAccessException | IllegalArgumentException
                           | InvocationTargetException e) {
                       throw new IllegalStateException(String.format("unable to invoke %s",
                                                                     field.getReadMethod()
                                                                          .toGenericString()),
                                                       e);
                   }
               });
        return builder;
    }

    private GraphQLFieldDefinition.Builder buildReference(GraphQLFieldDefinition.Builder builder,
                                                          PropertyDescriptor field) {
        GraphQLOutputType type = (GraphQLOutputType) PhantasmProcessor.getSingleton()
                                                                      .typeFor(Existential.class);
        builder.name(field.getName())
               .type(type)
               .dataFetcher(env -> {
                   Object record = env.getSource();
                   UUID fk;
                   try {
                       fk = (UUID) field.getReadMethod()
                                        .invoke(record);
                   } catch (IllegalAccessException | IllegalArgumentException
                           | InvocationTargetException e) {
                       throw new IllegalStateException(String.format("unable to invoke %s",
                                                                     field.getReadMethod()
                                                                          .toGenericString()),
                                                       e);
                   }
                   return Existential.wrap(Existential.resolve(env, fk));
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
        mutation.field(b -> {
            String fieldName = String.format("create%s", translated(record));
            log.info("Contributing {} to query", fieldName);
            return b.name(fieldName)
                    .type(type)
                    .argument(a -> a.name("state")
                                    .type(update)
                                    .description(String.format("Create state of the %s",
                                                               translated(record))))
                    .dataFetcher(env -> {
                        return create(record, types, env);
                    });
        });
    }

    private void contributeDelete(Builder mutation, Class<?> record,
                                  GraphQLObjectType type,
                                  List<PropertyDescriptor> fields,
                                  Map<String, GraphQLType> types) {
        mutation.field(b -> b.name(String.format("delete%s",
                                                 translated(record)))
                             .type(GraphQLBoolean)
                             .argument(a -> a.name("id")
                                             .type(new GraphQLNonNull(GraphQLUuid))
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
        query.field(b -> b.name(Introspector.decapitalize(translated(record)))
                          .type(type)
                          .argument(a -> a.name("id")
                                          .type(new GraphQLNonNull(GraphQLUuid))
                                          .description(String.format("ID of the %s",
                                                                     translated(record))))
                          .dataFetcher(env -> {
                              UUID id = env.getArgument("id");
                              return fetch(id, record, env);
                          }));
        query.field(b -> b.name(Introspector.decapitalize(String.format("%ss",
                                                                        translated(record))))
                          .type(new GraphQLList(type))
                          .argument(a -> a.name("ids")
                                          .type(new GraphQLList(GraphQLUuid))
                                          .description(String.format("IDs of the %s",
                                                                     translated(record))))
                          .dataFetcher(env -> {
                              List<UUID> ids = (List<UUID>) env.getArgument("ids");
                              if (ids == null) {
                                  return fetchAll(record, env);
                              }

                              return ids.stream()
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
        types.add(type);
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
        Model ctx = WorkspaceSchema.ctx(env);
        DSLContext create = ctx.create();
        Table<?> table = TABLES.get(record);
        UpdatableRecord<?> instance = (UpdatableRecord<?>) create.newRecord(table);
        initialize(instance, ctx.getCurrentPrincipal()
                                .getPrincipal()
                                .getId());
        Map<String, Object> state = (Map<String, Object>) env.getArgument("state");
        state.remove("id");
        set(instance, types, state);
        instance.insert();
        return (T) instance;
    }

    private Object delete(Class<?> record, Map<String, GraphQLType> types,
                          DataFetchingEnvironment env) {
        UpdatableRecord<?> instance = fetch(env.getArgument("id"), record, env);
        instance.delete();
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> T fetch(UUID id, Class<?> record, DataFetchingEnvironment env) {
        Table<?> table = TABLES.get(record);
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
        Table<?> table = TABLES.get(record);
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
                                  .where(field.eq(PhantasmContext.getWorkspace(env)
                                                                 .getId()))
                                  .fetch()
                                  .stream()
                                  .collect(Collectors.toList());
    }

    private void initialize(UpdatableRecord<?> instance, UUID updatedBy) {
        try {
            PropertyUtils.setProperty(instance, "id",
                                      RecordsFactory.GENERATOR.generate());
            PropertyUtils.setProperty(instance, "updatedBy", updatedBy);
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalStateException();
        }
    }

    private GraphQLObjectType objectType(Class<?> record,
                                         List<PropertyDescriptor> fields) {
        Set<String> references = TABLES.get(record)
                                       .getReferences()
                                       .stream()
                                       .map(fk -> camel(fk.getFields()
                                                          .get(0)
                                                          .getName()))
                                       .collect(Collectors.toSet());
        GraphQLObjectType.Builder builder = new GraphQLObjectType.Builder();
        builder.name(translated(record));
        fields.forEach(field -> {
            builder.field(f -> references.contains(field.getName()) ? buildReference(f,
                                                                                     field)
                                                                    : buildPrimitive(f,
                                                                                     field));
        });
        return builder.build();
    }

    private void set(UpdatableRecord<?> instance,
                     Map<String, GraphQLType> types,
                     Map<String, Object> state) {
        state.entrySet()
             .stream()
             .filter(entry -> !entry.getKey()
                                    .equals("id"))
             .forEach(entry -> {
                 try {
                     PropertyUtils.setProperty(instance, entry.getKey(),
                                               entry.getValue());
                 } catch (IllegalAccessException | InvocationTargetException
                         | NoSuchMethodException e) {
                     throw new IllegalArgumentException(String.format("Illegal property: %s",
                                                                      entry));
                 }
             });
    }

    private String translated(Class<?> record) {
        String simpleName = record.getSimpleName()
                                  .substring(0, record.getSimpleName()
                                                      .lastIndexOf("Record"));
        if (!simpleName.startsWith("Existential")) {
            return simpleName;
        }
        return simpleName.equals("Existential") ? simpleName
                                                : simpleName.substring("Existential".length());
    }

    private GraphQLType type(PropertyDescriptor field) {
        Method readMethod = field.getReadMethod();
        return PhantasmProcessor.getSingleton()
                                .typeFor(readMethod.getReturnType());
    }

    @SuppressWarnings("unchecked")
    private Object update(Class<?> record, Map<String, GraphQLType> types,
                          DataFetchingEnvironment env) {
        UUID id = (UUID) ((Map<?, ?>) env.getArgument("state")).get("id");
        UpdatableRecord<?> instance = fetch(id, record, env);
        set(instance, types, (Map<String, Object>) env.getArgument("state"));
        instance.update();
        return instance;
    }
}
