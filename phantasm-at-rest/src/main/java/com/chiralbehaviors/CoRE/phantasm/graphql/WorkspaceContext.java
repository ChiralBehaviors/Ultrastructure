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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Mutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Queries;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.CoreUserAdmin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.NetworkAuthorization;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SimpleExecutionStrategy;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceContext extends PhantasmCRUD implements Queries,
        Mutations, MetaQueries, MetaMutations, CoreUserAdmin {
    public static class EdgeFieldBuilder
            extends GraphQLFieldDefinition.Builder {
        private NetworkAuthorization auth;

        public EdgeFieldBuilder auth(NetworkAuthorization auth) {
            this.auth = auth;
            return this;
        }

        @Override
        public GraphQLFieldDefinition build() {
            return new GraphQLFieldEdgeDefinition(super.build(), auth);
        }
    }

    public static class Traversal {
        public final NetworkAuthorization auth;
        public ExistentialRuleform        child;
        public final ExistentialRuleform  parent;

        public Traversal(ExistentialRuleform parent,
                         NetworkAuthorization auth) {
            this.parent = parent;
            this.auth = auth;
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
        protected ExecutionResult completeValue(ExecutionContext executionContext,
                                                GraphQLType fieldType,
                                                List<Field> fields,
                                                Object result) {
            if (!path.isEmpty() && fieldType instanceof GraphQLObjectType) {
                Traversal edge = path.peek();
                if (edge != null) {
                    edge.child = (ExistentialRuleform) result;
                }
            }
            return super.completeValue(executionContext, fieldType, fields,
                                       result);
        }

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
            DataFetchingEnvironment environment = new DataFetchingEnvironmentImpl(source,
                                                                                  argumentValues,
                                                                                  executionContext.getRoot(),
                                                                                  fields,
                                                                                  fieldDef.getType(),
                                                                                  parentType,
                                                                                  executionContext.getGraphQLSchema());

            Object resolvedValue = null;
            boolean pop = false;
            try {
                try {
                    resolvedValue = fieldDef.getDataFetcher()
                                            .get(environment);
                    if (fieldDef instanceof GraphQLFieldEdgeDefinition) {
                        path.push(new Traversal((ExistentialRuleform) source,
                                                ((GraphQLFieldEdgeDefinition) fieldDef).getAuth()));
                        pop = true;
                    }
                } catch (Exception e) {
                    log.warn("Exception while fetching data", e);
                    executionContext.addError(new ExceptionWhileDataFetching(e));
                }

                return completeValue(executionContext, fieldDef.getType(),
                                     fields, resolvedValue);
            } finally {
                if (pop) {
                    path.pop();
                }
            }
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

    public ExecutionResult execute(GraphQLSchema schema, String query) {
        return execute(schema, query, Collections.emptyMap());
    }

    public ExecutionResult execute(GraphQLSchema schema, String query,
                                   Map<String, Object> variables) {
        ExecutionResult result = GraphQL.newGraphQL(schema)
                                        .queryExecutionStrategy(getStrategy())
                                        .build()
                                        .execute(query, this, variables);
        if (result.getErrors()
                  .isEmpty()) {
            return result;
        }
        return result;
    }

    public Traversal getCurrentEdge() {
        return path.isEmpty() ? null : path.peek();
    }

    public Product getWorkspace() {
        return workspace;
    }

    protected ExecutionStrategy getStrategy() {
        return new SimpleTraversalStrategy();
    }

}
