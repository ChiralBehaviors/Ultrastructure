/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class PhantasmTwo<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        implements InvocationHandler, ScopedPhantasm<RuleForm> {
    private final Map<Class<?>, StateDefinition<RuleForm>> facets;
    private final Map<Method, StateFunction<RuleForm>>     methods;
    private final Model                                    model;
    private final RuleForm                                 ruleform;

    public PhantasmTwo(RuleForm ruleform,
                       Map<Class<?>, StateDefinition<RuleForm>> facets,
                       Map<Method, StateFunction<RuleForm>> methods, Model model) {
        this.ruleform = ruleform;
        this.methods = methods;
        this.model = model;
        this.facets = facets;
    }

    @Override
    public String getDescription() {
        return ruleform.getDescription();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getModel()
     */
    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String getName() {
        return ruleform.getName();
    }

    @Override
    public String getNotes() {
        return ruleform.getNotes();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getRuleform()
     */
    @Override
    public RuleForm getRuleform() {
        return ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        throw new IllegalStateException("This should have never been called");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getUpdatedBy()
     */
    @Override
    public Agency getUpdatedBy() {
        return ruleform.getUpdatedBy();
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        StateFunction<RuleForm> function = methods.get(method);
        if (function != null) {
            StateDefinition<RuleForm> stateDefinition = facets.get(method.getDeclaringClass());
            WorkspaceScope scope = null;
            if (stateDefinition != null) {
                scope = model.getWorkspaceModel().getScoped(stateDefinition.getWorkspace());
            }
            return function.invoke(this, scope, args);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        if (method.isDefault()) {
            return invokeDefault(proxy, method, args, declaringClass);
        }
        // equals() and hashCode().  Becauase invariance.
        if (method.getName().equals("equals") && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return (args[0] instanceof Phantasm) ? ruleform.equals(((Phantasm<?>) args[0]).getRuleform())
                                                : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        return method.invoke(this, args);
    }

    private Attribute getAttribute(String namespace, String key,
                                   WorkspaceScope scope) {
        Attribute attribute = scope.lookup(namespace, key);
        if (attribute == null) {
            throw new IllegalStateException(
                                            String.format("The attribute %s:%s does not exist in the workspace",
                                                          namespace == null ? ""
                                                                           : namespace,
                                                          key));
        }
        return attribute;
    }

    private NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> getNetworkedModel() {
        return model.getNetworkedModel(ruleform);
    }

    private Relationship getRelationship(String namepsace, String name,
                                         WorkspaceScope scope) {
        Relationship lookup = (Relationship) scope.lookup(namepsace, name);
        if (lookup == null) {
            throw new IllegalStateException(
                                            String.format("Unable to find relationship %s:%s",
                                                          namepsace, name));
        }
        return lookup;
    }

    private AttributeValue<RuleForm>[] getValueArray(Attribute attribute) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<RuleForm>> values = (List<AttributeValue<RuleForm>>) getNetworkedModel().getAttributeValues(ruleform,
                                                                                                                        attribute);
        int max = 0;
        for (AttributeValue<RuleForm> value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        @SuppressWarnings("unchecked")
        AttributeValue<RuleForm>[] returnValue = new AttributeValue[max];
        for (AttributeValue<RuleForm> form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private Map<String, AttributeValue<RuleForm>> getValueMap(Attribute attribute) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : getNetworkedModel().getAttributeValues(ruleform,
                                                                                     attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private AttributeValue<RuleForm> newAttributeValue(Attribute attribute,
                                                       int i) {
        AttributeValue<RuleForm> value = getNetworkedModel().create(ruleform,
                                                                    attribute,
                                                                    model.getCurrentPrincipal().getPrincipal());
        value.setSequenceNumber(i);
        return value;
    }

    private void removeImmediateChild(NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel,
                                      Relationship relationship,
                                      Phantasm<RuleForm> child) {
        NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(ruleform,
                                                                         relationship,
                                                                         child.getRuleform());
        if (link != null) {
            model.getEntityManager().remove(link);
        }
    }

    private void setValue(Attribute attribute, int i,
                          AttributeValue<RuleForm> existing, Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(attribute, i);
            model.getEntityManager().persist(existing);
        }
        existing.setValue(newValue);
    }

    private List<Phantasm<? super RuleForm>> wrap(List<RuleForm> queryResult,
                                                  Class<Phantasm<? extends RuleForm>> phantasm) {
        List<Phantasm<? super RuleForm>> result = new ArrayList<>(
                                                                  queryResult.size());
        for (RuleForm ruleform : queryResult) {
            result.add(model.wrap(phantasm, ruleform));
        }
        return result;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object addAgencyAuth(String namespace, String name,
                                   Phantasm<Agency> phantasm,
                                   WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorize(ruleform, relationship,
                                      phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object addAgencyAuths(String namespace, String name,
                                    List<Phantasm<Agency>> authorized,
                                    WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Agency> agencies = new ArrayList<>(authorized.size());
        for (Phantasm<Agency> phantasm : authorized) {
            agencies.add(phantasm.getRuleform());
        }
        getNetworkedModel().authorizeAgencies(ruleform, relationship, agencies);
        return null;
    }

    protected Object addChild(String namespace, String name, RuleForm child,
                              WorkspaceScope scope) {
        getNetworkedModel().link(ruleform,
                                 getRelationship(namespace, name, scope),
                                 child,
                                 model.getCurrentPrincipal().getPrincipal());
        return null;
    }

    protected Object addChildren(String s, String key,
                                 List<Phantasm<RuleForm>> children,
                                 WorkspaceScope scope) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = getNetworkedModel();
        Relationship relationship = getRelationship(s, key, scope);
        for (Phantasm<RuleForm> child : children) {
            networkedModel.link(ruleform, relationship, child.getRuleform(),
                                model.getCurrentPrincipal().getPrincipal());
        }
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object addLocationAuth(String namespace, String name,
                                     Phantasm<Location> phantasm,
                                     WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorize(ruleform, relationship,
                                      phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object addLocationAuths(String namespace, String name,
                                      List<Phantasm<Location>> authorized,
                                      WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Location> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Location> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().authorizeLocations(ruleform, relationship,
                                               locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object addProductAuth(String namespace, String name,
                                    Phantasm<Product> phantasm,
                                    WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorize(ruleform, relationship,
                                      phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object addProductAuths(String namespace, String name,
                                     List<Phantasm<Product>> authorized,
                                     WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Product> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Product> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().authorizeProducts(ruleform, relationship, locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object addRelationshipAuth(String namespace, String name,
                                         Phantasm<Relationship> phantasm,
                                         WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorize(ruleform, relationship,
                                      phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object addRelationshipAuths(String namespace,
                                          String name,
                                          List<Phantasm<Relationship>> authorized,
                                          WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Relationship> relationships = new ArrayList<>(authorized.size());
        for (Phantasm<Relationship> phantasm : authorized) {
            relationships.add(phantasm.getRuleform());
        }
        getNetworkedModel().authorizeRelationships(ruleform, relationship,
                                                   relationships);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<Phantasm<Agency>> getAgencyAuths(String namespace,
                                                    String name,
                                                    Class<? extends Phantasm<Agency>> phantasmReturned,
                                                    WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Agency> queryResult = getNetworkedModel().getAuthorizedAgencies(ruleform,
                                                                             relationship);
        List<Phantasm<Agency>> returned = new ArrayList<>(queryResult.size());
        for (Agency agency : queryResult) {
            returned.add((Phantasm<Agency>) model.wrap(phantasmReturned, agency));
        }
        return returned;
    }

    protected Object[] getAttributeArray(String namespace, String key,
                                         Class<?> type, WorkspaceScope scope) {
        Attribute attribute = getAttribute(namespace, key, scope);
        if (!attribute.getIndexed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not indexed",
                                                          namespace, key));
        }
        AttributeValue<RuleForm>[] attributeValues = getValueArray(attribute);

        Object[] values = (Object[]) Array.newInstance(type,
                                                       attributeValues.length);
        for (AttributeValue<RuleForm> value : attributeValues) {
            values[value.getSequenceNumber()] = value.getValue();
        }
        return values;
    }

    /**
     * @param object
     * @param key
     * @param returnType
     * @return
     */
    protected Map<String, ?> getAttributeMap(String namespace, String key,
                                             Class<?> returnType,
                                             WorkspaceScope scope) {
        Attribute attribute = getAttribute(namespace, key, scope);
        if (!attribute.getKeyed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not keyed",
                                                          namespace, key));
        }
        Map<String, ?> map = new HashMap<>();
        for (Map.Entry<String, AttributeValue<RuleForm>> entry : getValueMap(
                                                                             attribute).entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    protected Object getAttributeValue(String namespace, String name,
                                       WorkspaceScope scope) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) getNetworkedModel().getAttributeValues(ruleform,
                                                                                                          getAttribute(namespace,
                                                                                                                       name,
                                                                                                                       scope));
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             name));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             name));
        }
        return values.get(0).getValue();
    }

    protected Object getChild(String namespace, String name,
                              Class<Phantasm<? extends RuleForm>> phantasm,
                              WorkspaceScope scope) {
        return model.wrap(phantasm,
                          getNetworkedModel().getSingleChild(ruleform,
                                                             getRelationship(namespace,
                                                                             name,
                                                                             scope)));
    }

    protected List<Phantasm<? super RuleForm>> getChildren(String namespace,
                                                           String name,
                                                           Class<Phantasm<? extends RuleForm>> phantasm,
                                                           WorkspaceScope scope) {
        List<RuleForm> queryResult = getNetworkedModel().getChildren(ruleform,
                                                                     getRelationship(namespace,
                                                                                     name,
                                                                                     scope));
        return wrap(queryResult, phantasm);
    }

    protected Object getImmediateChild(String namespace,
                                       String name,
                                       Class<Phantasm<? extends RuleForm>> phantasm,
                                       WorkspaceScope scope) {
        return model.wrap(phantasm,
                          getNetworkedModel().getImmediateChild(ruleform,
                                                                getRelationship(namespace,
                                                                                name,
                                                                                scope)));
    }

    protected List<Phantasm<? super RuleForm>> getImmediateChildren(String namespace,
                                                                    String name,
                                                                    Class<Phantasm<? extends RuleForm>> phantasm,
                                                                    WorkspaceScope scope) {
        List<RuleForm> queryResult = getNetworkedModel().getImmediateChildren(ruleform,
                                                                              getRelationship(namespace,
                                                                                              name,
                                                                                              scope));
        return wrap(queryResult, phantasm);
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<Phantasm<Location>> getLocationAuths(String namespace,
                                                        String name,
                                                        Class<? extends Phantasm<Location>> phantasmReturned,
                                                        WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Location> queryResult = getNetworkedModel().getAuthorizedLocations(ruleform,
                                                                                relationship);
        List<Phantasm<Location>> returned = new ArrayList<>(queryResult.size());
        for (Location location : queryResult) {
            returned.add((Phantasm<Location>) model.wrap(phantasmReturned,
                                                         location));
        }
        return returned;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<Phantasm<Product>> getProductAuths(String namespace,
                                                      String name,
                                                      Class<? extends Phantasm<Product>> phantasmReturned,
                                                      WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Product> queryResult = getNetworkedModel().getAuthorizedProducts(ruleform,
                                                                              relationship);
        List<Phantasm<Product>> returned = new ArrayList<>(queryResult.size());
        for (Product product : queryResult) {
            returned.add((Phantasm<Product>) model.wrap(phantasmReturned,
                                                        product));
        }
        return returned;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<Phantasm<Relationship>> getRelationshipAuths(String namespace,
                                                                String name,
                                                                Class<? extends Phantasm<Relationship>> phantasmReturned,
                                                                WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Relationship> queryResult = getNetworkedModel().getAuthorizedRelationships(ruleform,
                                                                                        relationship);
        List<Phantasm<Relationship>> returned = new ArrayList<>(
                                                                queryResult.size());
        for (Relationship r : queryResult) {
            returned.add((Phantasm<Relationship>) model.wrap(phantasmReturned,
                                                             r));
        }
        return returned;
    }

    protected WorkspaceScope getScope(StateDefinition<RuleForm> definition) {
        return model.getWorkspaceModel().getScoped(definition.getWorkspace());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularAgencyAuth(String namespace,
                                           String name,
                                           Class<? extends Phantasm<Agency>> phantasmReturned,
                                           WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        Agency authorized = getNetworkedModel().getAuthorizedAgency(ruleform,
                                                                    relationship);
        if (authorized == null) {
            return null;
        }
        return model.wrap(phantasmReturned, authorized);
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularLocationAuth(String namespace,
                                             String name,
                                             Class<? extends Phantasm<Location>> phantasmReturned,
                                             WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        Location authorized = getNetworkedModel().getAuthorizedLocation(ruleform,
                                                                        relationship);
        if (authorized == null) {
            return null;
        }
        return model.wrap(phantasmReturned, authorized);
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularProductAuth(String namespace,
                                            String name,
                                            Class<? extends Phantasm<Product>> phantasmReturned,
                                            WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        Product authorized = getNetworkedModel().getAuthorizedProduct(ruleform,
                                                                      relationship);
        if (authorized == null) {
            return null;
        }
        return model.wrap(phantasmReturned, authorized);
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularRelationshipAuth(String namespace,
                                                 String name,
                                                 Class<? extends Phantasm<Relationship>> phantasmReturned,
                                                 WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        Relationship authorized = getNetworkedModel().getAuthorizedRelationship(ruleform,
                                                                                relationship);
        if (authorized == null) {
            return null;
        }
        return model.wrap(phantasmReturned, authorized);
    }

    protected Object invokeDefault(Object proxy, Method method, Object[] args,
                                   final Class<?> declaringClass)
                                                                 throws NoSuchMethodException,
                                                                 Throwable,
                                                                 IllegalAccessException,
                                                                 InstantiationException,
                                                                 InvocationTargetException {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,
                                                                                                          int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(declaringClass,
                                       MethodHandles.Lookup.PRIVATE).unreflectSpecial(method,
                                                                                      declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object removeAgencyAuth(String namespace, String name,
                                      Phantasm<Agency> phantasm,
                                      WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().deauthorize(ruleform, relationship,
                                        phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object removeAgencyAuths(String namespace, String name,
                                       List<Phantasm<Agency>> authorized,
                                       WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Agency> agencies = new ArrayList<>(authorized.size());
        for (Phantasm<Agency> phantasm : authorized) {
            agencies.add(phantasm.getRuleform());
        }
        getNetworkedModel().deauthorizeAgencies(ruleform, relationship,
                                                agencies);
        return null;
    }

    protected Object removeChild(String namespace, String name, RuleForm child,
                                 WorkspaceScope scope) {
        removeImmediateChild(getNetworkedModel(),
                             getRelationship(namespace, name, scope), child);
        return null;
    }

    protected Object removeChildren(String s, String key,
                                    List<Phantasm<RuleForm>> children,
                                    WorkspaceScope scope) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = getNetworkedModel();
        Relationship relationship = getRelationship(s, key, scope);
        for (Phantasm<RuleForm> child : children) {
            removeImmediateChild(networkedModel, relationship, child);
        }
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeLocationAuth(String namespace, String name,
                                        Phantasm<Location> phantasm,
                                        WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().deauthorize(ruleform, relationship,
                                        phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeLocationAuths(String namespace, String name,
                                         List<Phantasm<Location>> authorized,
                                         WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Location> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Location> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().deauthorizeLocations(ruleform, relationship,
                                                 locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeProductAuth(String namespace, String name,
                                       Phantasm<Product> phantasm,
                                       WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().deauthorize(ruleform, relationship,
                                        phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeProductAuths(String namespace, String name,
                                        List<Phantasm<Product>> authorized,
                                        WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Product> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Product> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().deauthorizeProducts(ruleform, relationship,
                                                locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeRelationshipAuth(String namespace, String name,
                                            Phantasm<Relationship> phantasm,
                                            WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().deauthorize(ruleform, relationship,
                                        phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object removeRelationshipAuths(String namespace,
                                             String name,
                                             List<Phantasm<Relationship>> authorized,
                                             WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Relationship> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Relationship> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().deauthorizeRelationships(ruleform, relationship,
                                                     locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setAgencyAuths(String namespace, String name,
                                    List<Phantasm<Agency>> authorized,
                                    WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Agency> agencies = new ArrayList<>(authorized.size());
        for (Phantasm<Agency> phantasm : authorized) {
            agencies.add(phantasm.getRuleform());
        }
        getNetworkedModel().setAuthorizedAgencies(ruleform, relationship,
                                                  agencies);
        return null;
    }

    protected Object setAttributeArray(String namespace, String key,
                                       Object[] values, WorkspaceScope scope) {
        Attribute attribute = getAttribute(namespace, key, scope);
        if (!attribute.getIndexed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not indexed",
                                                          namespace, key));
        }
        AttributeValue<RuleForm>[] old = getValueArray(attribute);
        if (values == null) {
            if (old != null) {
                for (AttributeValue<RuleForm> value : old) {
                    model.getEntityManager().remove(value);
                }
            }
        } else if (old == null) {
            for (int i = 0; i < values.length; i++) {
                setValue(attribute, i, null, values[i]);
            }
        } else if (old.length == values.length) {
            for (int i = 0; i < values.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
        } else if (old.length < values.length) {
            int i;
            for (i = 0; i < old.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
            for (; i < values.length; i++) {
                setValue(attribute, i, null, values[i]);
            }
        } else if (old.length > values.length) {
            int i;
            for (i = 0; i < values.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
            for (; i < old.length; i++) {
                model.getEntityManager().remove(old[i]);
            }
        }
        return null;
    }

    protected Object setAttributeMap(String namespace, String key,
                                     Map<String, Object> values,
                                     WorkspaceScope scope) {
        Attribute attribute = getAttribute(namespace, key, scope);
        if (!attribute.getKeyed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not keyed",
                                                          namespace, key));
        }
        Map<String, AttributeValue<RuleForm>> valueMap = getValueMap(attribute);
        values.keySet().stream().filter(keyName -> !valueMap.containsKey(keyName)).forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (AttributeValue<RuleForm> value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            AttributeValue<RuleForm> value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(attribute, ++maxSeq);
                model.getEntityManager().persist(value);
                value.setKey(entry.getKey());
            }
            value.setValue(entry.getValue());
        }
        return null;
    }

    protected Object setAttributeValue(String namespace, String key,
                                       Object value, WorkspaceScope scope) {
        Attribute attribute = getAttribute(namespace, key, scope);
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) getNetworkedModel().getAttributeValues(ruleform,
                                                                                                          attribute);
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             key));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             key));
        }
        values.get(0).setValue(value);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param object
     * @return
     */
    protected Object setImmediateChild(String namespace, String name,
                                       Phantasm<RuleForm> phantasm,
                                       WorkspaceScope scope) {
        RuleForm child = phantasm.getRuleform();
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = getNetworkedModel();
        networkedModel.setImmediateChild(ruleform,
                                         getRelationship(namespace, name, scope),
                                         child,
                                         model.getCurrentPrincipal().getPrincipal());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param object
     * @return
     */
    protected Object setImmediateChildren(String namespace, String name,
                                          List<Phantasm<RuleForm>> arguments,
                                          WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        for (Phantasm<RuleForm> phantasm : arguments) {
            RuleForm child = phantasm.getRuleform();
            NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = getNetworkedModel();
            networkedModel.setImmediateChild(ruleform,
                                             relationship,
                                             child,
                                             model.getCurrentPrincipal().getPrincipal());
        }
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setLocationAuths(String namespace, String name,
                                      List<Phantasm<Location>> authorized,
                                      WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Location> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Location> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().setAuthorizedLocations(ruleform, relationship,
                                                   locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setProductAuths(String namespace, String name,
                                     List<Phantasm<Product>> authorized,
                                     WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Product> locations = new ArrayList<>(authorized.size());
        for (Phantasm<Product> phantasm : authorized) {
            locations.add(phantasm.getRuleform());
        }
        getNetworkedModel().setAuthorizedProducts(ruleform, relationship,
                                                  locations);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setRelationshipAuths(String namespace,
                                          String name,
                                          List<Phantasm<Relationship>> authorized,
                                          WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        List<Relationship> relationships = new ArrayList<>(authorized.size());
        for (Phantasm<Relationship> phantasm : authorized) {
            relationships.add(phantasm.getRuleform());
        }
        getNetworkedModel().setAuthorizedRelationships(ruleform, relationship,
                                                       relationships);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setSingularAgencyAuth(String namespace, String name,
                                           Phantasm<Agency> phantasm,
                                           WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorizeSingular(ruleform, relationship,
                                              phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularLocationAuth(String namespace, String name,
                                             Phantasm<Location> phantasm,
                                             WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorizeSingular(ruleform, relationship,
                                              phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularProductAuth(String namespace, String name,
                                            Phantasm<Product> phantasm,
                                            WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorizeSingular(ruleform, relationship,
                                              phantasm.getRuleform());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularRelationshipAuth(String namespace,
                                                 String name,
                                                 Phantasm<Relationship> phantasm,
                                                 WorkspaceScope scope) {
        Relationship relationship = getRelationship(namespace, name, scope);
        getNetworkedModel().authorizeSingular(ruleform, relationship,
                                              phantasm.getRuleform());
        return null;
    }
}
