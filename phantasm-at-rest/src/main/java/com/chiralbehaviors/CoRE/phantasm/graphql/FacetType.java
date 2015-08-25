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

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashSet;
import java.util.Set;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.PhantasmTraversal;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class FacetType<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements PhantasmTraversal.PhantasmVisitor<RuleForm, Network> {

    private final NetworkAuthorization<RuleForm> facet;
    private final Model                          model;
    private final Set<NetworkAuthorization<?>>   references = new HashSet<>();
    private Builder                              queryBuilder;

    public FacetType(NetworkAuthorization<RuleForm> facet, Model model) {
        this.model = model;
        this.facet = facet;
        queryBuilder = newObject().name(facet.getName())
                                  .description(facet.getNotes());
    }

    @Override
    public void visit(AttributeAuthorization<RuleForm, Network> auth,
                      String fieldName) {
        Attribute attribute = auth.getAuthorizedAttribute();
        queryBuilder.field(newFieldDefinition().type(typeOf(attribute))
                                               .name(fieldName)
                                               .description(attribute.getDescription())
                                               .dataFetcher(env -> FacetType.ctx(env)
                                                                            .getAttributeValue(env,
                                                                                               auth))
                                               .build());
    }

    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        type = new GraphQLList(type);
        queryBuilder.field(newFieldDefinition().type(type)
                                               .name(fieldName)
                                               .dataFetcher(env -> FacetType.ctx(env)
                                                                            .getChildren(env,
                                                                                         auth))
                                               .description(auth.getNotes())
                                               .build());
        references.add(child);
    }

    @Override
    public void visitChildren(XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        queryBuilder.field(newFieldDefinition().type(new GraphQLList(new GraphQLTypeReference(child.getName())))
                                               .name(fieldName)
                                               .description(auth.getNotes())
                                               .dataFetcher(env -> FacetType.ctx(env)
                                                                            .getXdChildren(env,
                                                                                           facet,
                                                                                           auth,
                                                                                           child))
                                               .build());
        references.add(child);
    }

    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        queryBuilder.field(newFieldDefinition().type(type)
                                               .name(fieldName)
                                               .dataFetcher(env -> FacetType.ctx(env)
                                                                            .getSingularChild(env,
                                                                                              auth))
                                               .description(auth.getNotes())
                                               .build());
        references.add(child);
    }

    @Override
    public void visitSingular(XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        queryBuilder.field(newFieldDefinition().type(new GraphQLTypeReference(child.getName()))
                                               .name(fieldName)
                                               .description(auth.getNotes())
                                               .dataFetcher(env -> FacetType.ctx(env)
                                                                            .getSingularXdChild(env,
                                                                                                facet,
                                                                                                auth,
                                                                                                child))
                                               .build());
        references.add(child);
    }

    public Set<NetworkAuthorization<?>> build(Builder topLevelQuery) {
        // Ruleform attributes for the facet
        queryBuilder.field(newFieldDefinition().type(GraphQLString)
                                               .name("id")
                                               .description("The id of the facet instance")
                                               .build());
        queryBuilder.field(newFieldDefinition().type(GraphQLString)
                                               .name("name")
                                               .description("The name of the facet instance")
                                               .build());
        queryBuilder.field(newFieldDefinition().type(GraphQLString)
                                               .name("description")
                                               .description("The description of the facet instance")
                                               .build());
        new PhantasmTraversal(model).traverse(facet, this);
        GraphQLObjectType queryType = queryBuilder.build();
        topLevelQuery.field(instanceQuery(queryType));
        topLevelQuery.field(instancesQuery(queryType));
        topLevelQuery.field(createInstanceQuery(queryType));
        return references;
    }

    /**
     * @param queryType
     * @return
     */
    private GraphQLFieldDefinition createInstanceQuery(GraphQLObjectType queryType) {
        return newFieldDefinition().name(String.format("Create%s",
                                                       facet.getName()))
                                   .type(queryType)
                                   .dataFetcher(context -> FacetType.ctx(context)
                                                                    .crceateInstance(context,
                                                                                     facet))
                                   .build();
    }

    public NetworkAuthorization<RuleForm> getFacet() {
        return facet;
    }

    public String getName() {
        return facet.getName();
    }

    @Override
    public String toString() {
        return String.format("FacetType [name=%s]", getName());
    }

    private GraphQLOutputType typeOf(Attribute attribute) {
        GraphQLOutputType type;
        switch (attribute.getValueType()) {
            case BINARY:
                type = GraphQLString; // encoded binary
                break;
            case BOOLEAN:
                type = GraphQLBoolean;
                break;
            case INTEGER:
                type = GraphQLInt;
                break;
            case NUMERIC:
                type = GraphQLFloat;
                break;
            case TEXT:
                type = GraphQLString;
                break;
            case TIMESTAMP:
                type = GraphQLString;
                break;
            default:
                throw new IllegalStateException(String.format("Cannot resolved the value type: %s for %s",
                                                              attribute.getValueType(),
                                                              attribute));
        }
        return attribute.getIndexed() ? new GraphQLList(type) : type;
    }

    private GraphQLFieldDefinition instanceQuery(GraphQLObjectType queryType) {
        return newFieldDefinition().name(facet.getName())
                                   .type(queryType)
                                   .argument(newArgument().name("id")
                                                          .description("id of the facet")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(context -> FacetType.ctx(context)
                                                                    .getInstance(context,
                                                                                 facet))
                                   .build();
    }

    private GraphQLFieldDefinition instancesQuery(GraphQLObjectType queryType) {
        return newFieldDefinition().name(String.format("InstancesOf%s",
                                                       facet.getName()))
                                   .type(new GraphQLList(queryType))
                                   .dataFetcher(context -> FacetType.ctx(context)
                                                                    .getInstances(context,
                                                                                  facet))
                                   .build();

    }

    public static WorkspaceContext ctx(DataFetchingEnvironment env) {
        return (WorkspaceContext) env.getContext();
    }
}
