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
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;

/**
 * Cannonical tranform of Phantasm metadata into GraphQL metadata.
 * 
 * @author hhildebrand
 *
 */
public class FacetType<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements PhantasmTraversal.PhantasmVisitor<RuleForm, Network> {

    private static final String ADD_TEMPLATE       = "add%s";
    private static final String APPLY_MUTATION     = "Apply%s";
    private static final String AT_RULEFORM        = "@ruleform";
    private static final String CREATE_MUTATION    = "Create%s";
    private static final String DESCRIPTION        = "description";
    private static final String ID                 = "id";
    private static final String INSTANCES_OF_QUERY = "InstancesOf%s";
    private static final String NAME               = "name";
    private static final String REMOVE_MUTATION    = "Remove%s";
    private static final String REMOVE_TEMPLATE    = "remove%s";
    private static final String SET_DESCRIPTION;
    private static final String SET_NAME;
    private static final String SET_TEMPLATE       = "set%s";
    private static final String STATE              = "state";
    private static final String UPDATE_QUERY       = "Update%s";
    private static final String UPDATE_TYPE        = "%sUpdate";

    static {
        SET_NAME = String.format(SET_TEMPLATE, capitalized(NAME));
        SET_DESCRIPTION = String.format(SET_TEMPLATE, capitalized(DESCRIPTION));
    }

    private static String capitalized(String field) {
        char[] chars = field.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private final NetworkAuthorization<RuleForm>                                                          facet;
    private final Model                                                                                   model;
    private final Set<NetworkAuthorization<?>>                                                            references     = new HashSet<>();
    private Builder                                                                                       typeBuilder;
    private final Map<String, BiFunction<PhantasmCRUD<RuleForm, Network>, Map<String, Object>, RuleForm>> updateTemplate = new HashMap<>();

    private graphql.schema.GraphQLInputObjectType.Builder updateTypeBuilder;

    public FacetType(NetworkAuthorization<RuleForm> facet, Model model) {
        this.model = model;
        this.facet = facet;
        typeBuilder = newObject().name(facet.getName())
                                 .description(facet.getNotes());
        updateTypeBuilder = newInputObject().name(String.format(UPDATE_TYPE,
                                                                facet.getName()))
                                            .description(facet.getNotes());
    }

    /**
     * Build the top level queries and mutations
     * 
     * @param query
     *            - top level query
     * @param mutation
     *            - top level mutation
     * @return the references this facet has to other facets.
     */
    public Set<NetworkAuthorization<?>> build(Builder query, Builder mutation) {
        buildRuleformAttributes();
        new PhantasmTraversal<RuleForm, Network>(model).traverse(facet, this);
        GraphQLObjectType type = typeBuilder.build();

        query.field(instance(type));
        query.field(instances());

        mutation.field(createInstance());
        mutation.field(apply());
        mutation.field(update());
        mutation.field(remove());
        return references;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PhantasmCRUD<RuleForm, Network> ctx(DataFetchingEnvironment env) {
        return (PhantasmCRUD) env.getContext();
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

    @SuppressWarnings("unchecked")
    @Override
    public void visit(AttributeAuthorization<RuleForm, Network> auth,
                      String fieldName) {
        Attribute attribute = auth.getAuthorizedAttribute();
        GraphQLOutputType type = typeOf(attribute);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(attribute.getDescription())
                                              .dataFetcher(env -> ctx(env).getAttributeValue(facet,
                                                                                             (RuleForm) env.getSource(), auth))
                                              .build());

        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        GraphQLInputType inputType;
        if (auth.getAuthorizedAttribute()
                .getIndexed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth, (List<Object>) update.get(setter)));
            inputType = new GraphQLList(GraphQLString);
        } else if (auth.getAuthorizedAttribute()
                       .getKeyed()) {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth, (Map<String, Object>) update.get(setter)));
            inputType = GraphQLString;
        } else {
            updateTemplate.put(setter,
                               (crud,
                                update) -> crud.setAttributeValue(facet,
                                                                  (RuleForm) update.get(AT_RULEFORM),
                                                                  auth, (Object) update.get(setter)));
            inputType = new GraphQLList(GraphQLString);
        }
        updateTypeBuilder.field(newInputObjectField().type(inputType)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child,
                              String singularFieldName) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        type = new GraphQLList(type);
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       (RuleForm) env.getSource(), auth))
                                              .description(auth.getNotes())
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                             (List<String>) update.get(setter))));

        String add = String.format(ADD_TEMPLATE,
                                   capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(add)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     (RuleForm) update.get(AT_RULEFORM),
                                                     auth, (RuleForm) crud.lookup(auth,
                                                                            (String) update.get(add))));

        String remove = String.format(REMOVE_TEMPLATE,
                                      capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, (RuleForm) crud.lookup(auth,
                                                                               (String) update.get(remove))));

        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           (RuleForm) update.get(AT_RULEFORM),
                                                           auth, (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                                (List<String>) update.get(removeChildren))));

        String addChildren = String.format(ADD_TEMPLATE,
                                           capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(addChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, (List<RuleForm>) crud.lookupRuleForm(auth,
                                                                                             (List<String>) update.get(addChildren))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitChildren(XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child,
                              String singularFieldName) {
        GraphQLList type = new GraphQLList(new GraphQLTypeReference(child.getName()));
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(auth.getNotes())
                                              .dataFetcher(env -> ctx(env).getChildren(facet,
                                                                                       (RuleForm) env.getSource(), auth))
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, crud.lookup(child,
                                                                    (List<String>) update.get(setter))));

        String add = String.format(ADD_TEMPLATE,
                                   capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(add)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(add,
                           (crud,
                            update) -> crud.addChild(facet,
                                                     (RuleForm) update.get(AT_RULEFORM),
                                                     auth, crud.lookup(child,
                                                                 (String) update.get(add))));

        String remove = String.format(REMOVE_TEMPLATE,
                                      capitalized(singularFieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(remove)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(remove,
                           (crud,
                            update) -> crud.removeChild(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, crud.lookup(child,
                                                                    (String) update.get(remove))));

        String removeChildren = String.format(REMOVE_TEMPLATE,
                                              capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(removeChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(removeChildren,
                           (crud,
                            update) -> crud.removeChildren(facet,
                                                           (RuleForm) update.get(AT_RULEFORM),
                                                           auth, crud.lookup(child,
                                                                       (List<String>) update.get(removeChildren))));

        String addChildren = String.format(ADD_TEMPLATE,
                                           capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(new GraphQLList(GraphQLString))
                                                     .name(addChildren)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(addChildren,
                           (crud,
                            update) -> crud.addChildren(facet,
                                                        (RuleForm) update.get(AT_RULEFORM),
                                                        auth, crud.lookup(child,
                                                                    (List<String>) update.get(addChildren))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        GraphQLOutputType type = new GraphQLTypeReference(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .dataFetcher(env -> ctx(env).getSingularChild(facet,
                                                                                            (RuleForm) env.getSource(), auth))
                                              .description(auth.getNotes())
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setSingularChild(facet,
                                                             (RuleForm) update.get(AT_RULEFORM),
                                                             auth, (RuleForm) crud.lookup(auth,
                                                                                    (String) update.get(setter))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSingular(XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        GraphQLTypeReference type = new GraphQLTypeReference(child.getName());
        typeBuilder.field(newFieldDefinition().type(type)
                                              .name(fieldName)
                                              .description(auth.getNotes())
                                              .dataFetcher(env -> ctx(env).getSingularChild((RuleForm) env.getSource(),
                                                                                            auth, facet))
                                              .build());
        String setter = String.format(SET_TEMPLATE, capitalized(fieldName));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(setter)
                                                     .description(auth.getNotes())
                                                     .build());
        updateTemplate.put(setter,
                           (crud,
                            update) -> crud.setSingularChild(facet,
                                                             (RuleForm) update.get(AT_RULEFORM),
                                                             auth, crud.lookup(child,
                                                                         (String) update.get(setter))));
        references.add(child);
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition apply() {
        return newFieldDefinition().name(String.format(APPLY_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).apply(facet,
                                                                      (RuleForm) ctx(env).lookup(facet,
                                                                                                 (String) env.getArgument(ID))))
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private void buildRuleformAttributes() {
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(ID)
                                              .description("The id of the facet instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(NAME)
                                              .description("The name of the facet instance")
                                              .build());
        typeBuilder.field(newFieldDefinition().type(GraphQLString)
                                              .name(DESCRIPTION)
                                              .description("The description of the facet instance")
                                              .build());

        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(ID)
                                                     .description(String.format("the id of the updated %s",
                                                                                facet.getName()))
                                                     .build());
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_NAME)
                                                     .description(String.format("the name to update on %s",
                                                                                facet.getName()))
                                                     .build());
        updateTemplate.put(SET_NAME,
                           (crud,
                            update) -> crud.setName((RuleForm) update.get(AT_RULEFORM),
                                                    (String) update.get(SET_NAME)));
        updateTypeBuilder.field(newInputObjectField().type(GraphQLString)
                                                     .name(SET_DESCRIPTION)
                                                     .description(String.format("the description to update on %s",
                                                                                facet.getName()))
                                                     .build());
        updateTemplate.put(SET_DESCRIPTION,
                           (crud,
                            update) -> crud.setDescription((RuleForm) update.get(AT_RULEFORM),
                                                           (String) update.get(SET_DESCRIPTION)));
    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition createInstance() {
        return newFieldDefinition().name(String.format(CREATE_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply to the new instance")
                                                          .type(new GraphQLNonNull(updateTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD<RuleForm, Network> crud = ctx(env);
                                       RuleForm ruleform = crud.createInstance(facet,
                                                                               (String) updateState.get(SET_NAME),
                                                                               (String) updateState.get(SET_DESCRIPTION));
                                       updateState.remove(SET_NAME);
                                       updateState.remove(SET_DESCRIPTION);
                                       return ruleform == null ? null
                                                               : update(ruleform,
                                                                        updateState,
                                                                        crud);
                                   })
                                   .build();
    }

    private GraphQLFieldDefinition instance(GraphQLObjectType type) {
        return newFieldDefinition().name(facet.getName())
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the facet")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).lookup(facet,
                                                                       (String) env.getArgument(ID)))
                                   .build();
    }

    private GraphQLFieldDefinition instances() {
        return newFieldDefinition().name(String.format(INSTANCES_OF_QUERY,
                                                       facet.getName()))
                                   .type(new GraphQLList(new GraphQLTypeReference(facet.getName())))
                                   .dataFetcher(context -> ctx(context).getInstances(facet))
                                   .build();

    }

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(String.format(REMOVE_MUTATION,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> ctx(env).remove(facet,
                                                                       (RuleForm) ctx(env).lookup(facet,
                                                                                                  (String) env.getArgument(ID)),
                                                                       true))
                                   .build();
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

    @SuppressWarnings("unchecked")
    private GraphQLFieldDefinition update() {
        return newFieldDefinition().name(String.format(UPDATE_QUERY,
                                                       facet.getName()))
                                   .type(new GraphQLTypeReference(facet.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(updateTypeBuilder.build()))
                                                          .build())
                                   .dataFetcher(env -> {
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       PhantasmCRUD<RuleForm, Network> crud = ctx(env);
                                       RuleForm ruleform = (RuleForm) crud.lookup(facet,
                                                                                  (String) updateState.get(ID));
                                       return update(ruleform, updateState,
                                                     crud);
                                   })
                                   .build();
    }

    private Object update(RuleForm ruleform, Map<String, Object> updateState,
                          PhantasmCRUD<RuleForm, Network> crud) {
        updateState.put(AT_RULEFORM, ruleform);
        for (String field : updateState.keySet()) {
            if (!field.equals(ID) && !field.equals(AT_RULEFORM)
                && updateState.containsKey(field)) {
                updateTemplate.get(field)
                              .apply(crud, updateState);
            }
        }
        return ruleform;
    }
}
