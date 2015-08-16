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

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSchemaBuilder {
    private final Model                                         model;
    private final Map<NetworkAuthorization<?>, FacetType<?, ?>> resolved   = new HashMap<>();
    private final Deque<NetworkAuthorization<?>>                unresolved = new ArrayDeque<>();
    private final Workspace                                     workspace;

    public WorkspaceSchemaBuilder(String urn, Model model) {
        this(Workspace.uuidOf(urn), model);
    }

    public WorkspaceSchemaBuilder(UUID uuid, Model model) {
        this.model = model;
        workspace = model.getWorkspaceModel().getScoped(uuid).getWorkspace();
    }

    public GraphQLSchema build() {
        initialState();
        while (!unresolved.isEmpty()) {
            NetworkAuthorization<?> facet = unresolved.pop();
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FacetType<?, ?> type = new FacetType(facet, model);
            resolved.put(facet, type);
            for (NetworkAuthorization<?> auth : type.resolve()) {
                if (!resolved.containsKey(auth)) {
                    unresolved.add(auth);
                }
            }
        }
        Builder topLevelQuery = newObject().name(workspace.getDefiningProduct().getName()).description(String.format("Top level query for %s",
                                                                                                                     workspace.getDefiningProduct().getName()));
        resolved.values().forEach(facet -> {
            Set<NetworkAuthorization<?>> traversed = new HashSet<>();
            GraphQLObjectType queryType = facet.build(resolved, traversed);
            topLevelQuery.field(newFieldDefinition().name(facet.getName()).type(queryType).argument(newArgument().name("id").description("id of the facet").type(new GraphQLNonNull(GraphQLString)).build()).dataFetcher(context -> ctx(context).getInstance(context,
                                                                                                                                                                                                                                                             facet.getFacet())).build());
        });
        clear();
        return GraphQLSchema.newSchema().query(topLevelQuery.build()).build();
    }

    private WorkspaceContext ctx(DataFetchingEnvironment env) {
        return (WorkspaceContext) env.getContext();
    }

    private void clear() {
        resolved.clear();
        unresolved.clear();
    }

    private void initialState() {
        clear();
        Product definingProduct = workspace.getDefiningProduct();
        unresolved.addAll(model.getAgencyModel().getFacets(definingProduct));
        unresolved.addAll(model.getAttributeModel().getFacets(definingProduct));
        unresolved.addAll(model.getIntervalModel().getFacets(definingProduct));
        unresolved.addAll(model.getLocationModel().getFacets(definingProduct));
        unresolved.addAll(model.getProductModel().getFacets(definingProduct));
        unresolved.addAll(model.getRelationshipModel().getFacets(definingProduct));
        unresolved.addAll(model.getStatusCodeModel().getFacets(definingProduct));
        unresolved.addAll(model.getUnitModel().getFacets(definingProduct));
    }
}
