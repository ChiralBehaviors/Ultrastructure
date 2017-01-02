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

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Mutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Queries;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.CoreUserAdmin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SimpleExecutionStrategy;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceContext extends PhantasmCRUD implements Queries,
        Mutations, MetaQueries, MetaMutations, CoreUserAdmin {
    public static class EdgeFieldBuilder
            extends GraphQLFieldDefinition.Builder {
        private NetworkAuthorization auth;

        @Override
        public GraphQLFieldDefinition build() {
            return new GraphQLFieldEdgeDefinition(super.build(), auth);
        }

        public EdgeFieldBuilder setAuth(NetworkAuthorization auth) {
            this.auth = auth;
            return this;
        }
    }

    public static class Traversal {
        public final ExistentialRuleform parent;
        public final Relationship        relationship;

        public Traversal(ExistentialRuleform parent,
                         Relationship relationship) {
            this.parent = parent;
            this.relationship = relationship;
        }
    }

    private static class GraphQLFieldEdgeDefinition
            extends GraphQLFieldDefinition {

        private final NetworkAuthorization auth;

        public GraphQLFieldEdgeDefinition(GraphQLFieldDefinition def,
                                          NetworkAuthorization auth) {
            super(def.getName(), def.getDescription(), def.getType(),
                  def.getDataFetcher(), def.getArguments(),
                  def.getDeprecationReason());
            this.auth = auth;
        }

        public NetworkAuthorization getAuth() {
            return auth;
        }
    }

    private class SimpleTraversalStrategy extends SimpleExecutionStrategy {

        @Override
        protected ExecutionResult resolveField(ExecutionContext executionContext,
                                               GraphQLObjectType parentType,
                                               Object source,
                                               List<Field> fields) {
            GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(),
                                                          parentType,
                                                          fields.get(0));

            Map<String, Object> argumentValues = valuesResolver.getArgumentValues(fieldDef.getArguments(),
                                                                                  fields.get(0)
                                                                                        .getArguments(),
                                                                                  executionContext.getVariables());
            DataFetchingEnvironment environment = new DataFetchingEnvironment(source,
                                                                              argumentValues,
                                                                              executionContext.getRoot(),
                                                                              fields,
                                                                              fieldDef.getType(),
                                                                              parentType,
                                                                              executionContext.getGraphQLSchema());

            Object resolvedValue = null;
            boolean pop = false;
            try {
                resolvedValue = fieldDef.getDataFetcher()
                                        .get(environment);
                if (fieldDef instanceof GraphQLFieldEdgeDefinition) {
                    pop = true;
                    path.push(new Traversal((ExistentialRuleform) source,
                                            ((GraphQLFieldEdgeDefinition) fieldDef).getAuth()
                                                                                   .getRelationship()));
                }
            } catch (Exception e) {
                log.warn("Exception while fetching data", e);
                executionContext.addError(new ExceptionWhileDataFetching(e));
            } finally {
                if (pop) {
                    path.pop();
                }
            }

            return completeValue(executionContext, fieldDef.getType(), fields,
                                 resolvedValue);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(WorkspaceContext.class);

    public static Product getWorkspace(DataFetchingEnvironment env) {
        return ((WorkspaceContext) env.getContext()).getWorkspace();
    }

    public static EdgeFieldBuilder newEdgeFieldDefinition() {
        return new EdgeFieldBuilder();
    }

    private final Stack<Traversal> path = new Stack<>();
    private final Product          workspace;

    public WorkspaceContext(Model model, Product workspace) {
        super(model);
        this.workspace = workspace;
    }

    public ExecutionResult execute(GraphQLSchema schema, String query,
                                   Map<String, Object> variables) {
        ExecutionResult result = new GraphQL(schema,
                                             getStrategy()).execute(query, this,
                                                                    variables);
        if (result.getErrors()
                  .isEmpty()) {
            return result;
        }
        return result;
    }

    public Traversal getCurrentEdge() {
        return path.isEmpty() ? null : path.peek();
    }

    public ExecutionStrategy getStrategy() {
        return new SimpleTraversalStrategy();
    }

    public Product getWorkspace() {
        return workspace;
    }
}
