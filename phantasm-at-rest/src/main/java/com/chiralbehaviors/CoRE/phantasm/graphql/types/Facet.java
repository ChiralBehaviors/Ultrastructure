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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.wrap;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.ExistentialQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization.AttributeAuthorizationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization.NeworkAuthorizationTypeFunction;

import graphql.annotations.DefaultTypeFunction;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class Facet {

    private static final String IDS = "ids";

    class FacetTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return FacetType;
        }
    }

    public static final GraphQLObjectType FacetType;

    private static final String           CREATE             = "CreateFacet";

    private static final String           DELETE             = "DeleteFacet";
    private static final String           FACET_STATE        = "FacetState";
    private static final String           FACETS             = "Facets";
    private static final String           ID                 = "id";
    private static final String           INSTANCES          = "InstancesOfFacet";
    private static final String           SET_AUTHORITY      = "setAuthority";
    private static final String           SET_CLASSIFICATION = "setClassification";
    private static final String           SET_CLASSIFIER     = "setClassifier";
    private static final String           SET_NAME           = "setName";
    private static final String           SET_NOTES          = "setNotes";
    private static final String           STATE              = "state";
    private static final String           UPDATE             = "UpdateFacet";

    static {
        DefaultTypeFunction.register(Cardinality.class,
                                     (u, t) -> GraphQLString);
        FacetType = Existential.objectTypeOf(Facet.class);
    }

    public static void build(Builder query, Builder mutation,
                             ThreadLocal<Product> currentWorkspace) {
        Map<String, BiConsumer<FacetRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(facets(currentWorkspace));
        query.field(instance(currentWorkspace));
        query.field(instances(currentWorkspace));

        mutation.field(create(stateType, updateTemplate, currentWorkspace));
        mutation.field(update(stateType, updateTemplate, currentWorkspace));
        mutation.field(remove(currentWorkspace));
    }

    public static GraphQLSchema build(ThreadLocal<Product> currentWorkspace) {
        Builder topLevelQuery = newObject().name("Query")
                                           .description("Top level metadata query");
        Builder topLevelMutation = newObject().name("Mutation")
                                              .description("Top level metadata mutation");
        ExistentialQueries.build(topLevelQuery, topLevelMutation,
                                 currentWorkspace);
        AttributeAuthorization.build(topLevelQuery, topLevelMutation,
                                     currentWorkspace);
        NetworkAuthorization.build(topLevelQuery, topLevelMutation,
                                   currentWorkspace);
        Facet.build(topLevelQuery, topLevelMutation, currentWorkspace);
        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(topLevelQuery.build())
                                            .mutation(topLevelMutation.build())
                                            .build();
        return schema;
    }

    public static Facet fetch(DataFetchingEnvironment env, UUID id) {
        return new Facet(ctx(env).create()
                                 .selectFrom(Tables.FACET)
                                 .where(Tables.FACET.ID.equal(id))
                                 .fetchOne());
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(FACET_STATE)
                                                                                .description("Facet creation/update state");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
                                           .description("The name of the facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_AUTHORITY)
                                           .description("The relationship classifier of the facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CLASSIFIER)
                                           .description("The relationship classifier of the facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CLASSIFICATION)
                                           .description("The existential classification of the facet")
                                           .build());
        return builder.build();
    }

    private static Map<String, BiConsumer<FacetRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<FacetRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NAME, (e, value) -> e.setName((String) value));
        updateTemplate.put(SET_AUTHORITY,
                           (e,
                            value) -> e.setAuthority(UUID.fromString((String) value)));
        updateTemplate.put(SET_CLASSIFIER,
                           (e,
                            value) -> e.setClassification(UUID.fromString((String) value)));
        updateTemplate.put(SET_CLASSIFICATION,
                           (e,
                            value) -> e.setClassification(UUID.fromString((String) value)));
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        return updateTemplate;
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<FacetRecord, Object>> updateTemplate,
                                                 ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of Facet")
                                   .type(new GraphQLTypeReference(FacetType.getName()))
                                   .argument(newArgument().name(FACET_STATE)
                                                          .description("the initial state of the facet")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newFacet(env, createState,
                                                       updateTemplate);
                                   })
                                   .build();
    }

    private static GraphQLFieldDefinition facets(ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(FACETS)
                                   .type(new GraphQLList(FacetType))
                                   .dataFetcher(env -> {
                                       return ctx(env).getPhantasmModel()
                                                      .getFacets(currentWorkspace.get())
                                                      .stream()
                                                      .map(r -> new Facet(r))
                                                      .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static FacetRecord fetch(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.FACET)
                       .where(Tables.FACET.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance(ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(FacetType.getName())
                                   .type(FacetType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new Facet(fetch(env));
                                   })
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private static GraphQLFieldDefinition instances(ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(INSTANCES)
                                   .type(FacetType)
                                   .argument(newArgument().name(IDS)
                                                          .description("facet ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return ((List<String>) env.getArgument(IDS)).stream()
                                                                                   .map(s -> UUID.fromString(s))
                                                                                   .map(id -> fetch(env,
                                                                                                    id))
                                                                                   .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static Object newFacet(DataFetchingEnvironment env,
                                   Map<String, Object> createState,
                                   Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        FacetRecord record = ctx(env).records()
                                     .newFacet();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new Facet(record);
    }

    private static GraphQLFieldDefinition remove(ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(FacetType.getName()))
                                   .description("Delete the facet")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the facet instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<FacetRecord, Object>> updateTemplate,
                                                 ThreadLocal<Product> currentWorkspace) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(FacetType.getName()))
                                   .description("Update the instance of a facet")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(type))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateFacet(env, updateState,
                                                          updateTemplate);
                                   })
                                   .build();
    }

    private static Facet updateFacet(DataFetchingEnvironment env,
                                     Map<String, Object> updateState,
                                     Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        FacetRecord facet = ctx(env).create()
                                    .selectFrom(Tables.FACET)
                                    .where(Tables.FACET.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                    .fetchOne();
        updateState.remove(ID);
        if (facet == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(facet,
                                                            updateState.get(k)));
        facet.update();
        return new Facet(facet);
    }

    private final FacetRecord record;

    public Facet(FacetRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AttributeAuthorizationTypeFunction.class)
    public List<AttributeAuthorization> getAttributes(DataFetchingEnvironment env) {
        return ctx(env).getPhantasmModel()
                       .getAttributeAuthorizations(record, false)
                       .stream()
                       .map(r -> new AttributeAuthorization(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAuthority(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    @GraphQLType(NeworkAuthorizationTypeFunction.class)
    public List<NetworkAuthorization> getChildren(DataFetchingEnvironment env) {
        return ctx(env).getPhantasmModel()
                       .getNetworkAuthorizations(record, false)
                       .stream()
                       .map(r -> new NetworkAuthorization(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(ExistentialTypeFunction.class)
    public Existential getClassification(DataFetchingEnvironment env) {
        return wrap(resolve(env, record.getClassifier()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getClassifier(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getClassifier()));
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField
    public String getName() {
        return record.getName();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersin() {
        return record.getVersion();
    }
}
