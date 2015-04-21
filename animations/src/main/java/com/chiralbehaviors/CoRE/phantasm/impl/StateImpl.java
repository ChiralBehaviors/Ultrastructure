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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class StateImpl<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        implements InvocationHandler, ScopedPhantasm<RuleForm> {

    private final Map<Method, StateFunction<RuleForm>> methods;
    private final Model                                model;
    private final RuleForm                             ruleform;
    private final WorkspaceScope                       scope;

    public StateImpl(RuleForm ruleform, Model model,
                     Map<Method, StateFunction<RuleForm>> methods,
                     WorkspaceScope scope) {
        this.ruleform = ruleform;
        this.model = model;
        this.methods = methods;
        this.scope = scope;
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
        return scope;
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
        // Hard override (final) equals() and hashCode().  Becauase invariance.
        if (method.getName().equals("equals") && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return (args[0] instanceof Phantasm) ? ruleform.equals(((Phantasm<?>) args[0]).getRuleform())
                                                : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        if (method.isDefault()) {
            final Class<?> declaringClass = method.getDeclaringClass();
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,
                                                                                                              int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(declaringClass,
                                           MethodHandles.Lookup.PRIVATE).unreflectSpecial(method,
                                                                                          declaringClass).bindTo(proxy).invokeWithArguments(args);
        }
        StateFunction<RuleForm> function = methods.get(method);
        Object returnValue = (function != null) ? function.invoke(this, args)
                                               : method.invoke(this, args);

        // always maintain proxy discipline.  Because identity.
        return returnValue == this ? proxy : returnValue;
    }

    /**
     * @param namespace
     * @param name
     * @param object
     * @return
     */
    public Object setImmediateChild(String scope, String key, Object object) {
        if (!(object instanceof Phantasm)) {
            throw new ClassCastException(
                                         String.format("%s does not implement %s",
                                                       object.getClass().getCanonicalName(),
                                                       Phantasm.class.getCanonicalName()));
        }
        @SuppressWarnings("unchecked")
        Phantasm<RuleForm> phantasm = (Phantasm<RuleForm>) object;
        RuleForm child = phantasm.getRuleform();
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        networkedModel.setImmediateChild(ruleform,
                                         getRelationship(scope, key),
                                         child,
                                         model.getKernel().getCoreAnimationSoftware());
        return null;
    }

    private Attribute getAttribute(String s, String key) {
        return (Attribute) scope.lookup(s, key);
    }

    private Relationship getRelationship(String s, String key) {
        return (Relationship) scope.lookup(s, key);
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

    protected void addChild(String scope, String key, boolean singular,
                            RuleForm child) {
        model.getNetworkedModel(ruleform).link(ruleform,
                                               getRelationship(scope, key),
                                               child,
                                               model.getKernel().getCore());
    }

    protected void addChildren(String s, String key, List<RuleForm> children,
                               Agency updatedBy) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        for (RuleForm child : children) {
            networkedModel.link(ruleform, getRelationship(s, key), child,
                                updatedBy);
        }
    }

    protected Object[] getAttributeArray(String scope, String key, Class<?> type) {
        AttributeValue<RuleForm>[] attributeValues = getValueArray(scope, key);

        Object[] values = (Object[]) Array.newInstance(type,
                                                       attributeValues.length);
        for (AttributeValue<RuleForm> value : attributeValues) {
            values[value.getSequenceNumber() - 1] = value.getValue();
        }
        return values;
    }

    /**
     * @param object
     * @param key
     * @param returnType
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, ?> getAttributeMap(String namespace, String key,
                                             Class<?> returnType) {
        Map<String, ?> map;
        try {
            map = (Map<String, ?>) returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(
                                            String.format("Cannot create a new instance of %s",
                                                          returnType.toGenericString()));
        }
        for (Map.Entry<String, AttributeValue<RuleForm>> entry : getValueMap(
                                                                             namespace,
                                                                             key).entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    private Map<String, AttributeValue<RuleForm>> getValueMap(String namespace,
                                                              String key) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                   getAttribute(namespace,
                                                                                                                key))) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private AttributeValue<RuleForm>[] getValueArray(String namespace,
                                                     String key) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<RuleForm>> values = (List<AttributeValue<RuleForm>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                                      getAttribute(namespace,
                                                                                                                                                   key));
        int max = 0;
        for (AttributeValue<RuleForm> value : values) {
            max = Math.max(max, value.getSequenceNumber());
        }
        @SuppressWarnings("unchecked")
        AttributeValue<RuleForm>[] returnValue = new AttributeValue[max];
        return returnValue;
    }

    protected Object getAttributeValue(String s, String key) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                        getAttribute(s,
                                                                                                                                     key));
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        }
        return values.get(0).getValue();
    }

    protected Object getChild(String scope, String key,
                              Class<Phantasm<? extends RuleForm>> phantasm) {
        return model.wrap(phantasm,
                          model.getNetworkedModel(ruleform).getSingleChild(ruleform,
                                                                           getRelationship(scope,
                                                                                           key)));
    }

    protected List<Phantasm<? super RuleForm>> getChildren(String scope,
                                                           String key,
                                                           Class<Phantasm<? extends RuleForm>> phantasm) {
        List<RuleForm> queryResult = model.getNetworkedModel(ruleform).getChildren(ruleform,
                                                                                   getRelationship(scope,
                                                                                                   key));
        return wrap(queryResult, phantasm);
    }

    protected Object getImmediateChild(String namespace,
                                       String name,
                                       Class<Phantasm<? extends RuleForm>> phantasm) {
        return model.wrap(phantasm,
                          model.getNetworkedModel(ruleform).getImmediateChild(ruleform,
                                                                              getRelationship(namespace,
                                                                                              name)));
    }

    protected List<Phantasm<? super RuleForm>> getImmediateChildren(String scope,
                                                                    String key,
                                                                    Class<Phantasm<? extends RuleForm>> phantasm) {
        List<RuleForm> queryResult = model.getNetworkedModel(ruleform).getImmediateChildren(ruleform,
                                                                                            getRelationship(scope,
                                                                                                            key));
        return wrap(queryResult, phantasm);
    }

    protected Object setAttributeValue(String scope, String key, Object value) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                        getAttribute(scope,
                                                                                                                                     key));
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        }
        values.get(0).setValue(value);
        return null;
    }

    protected Object setAttributeArray(String scope, String key, Object[] values) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Object setAttributeMap(String scope, String key,
                                     Map<String, Object> values) {
        // TODO Auto-generated method stub
        return null;
    }

    protected void setChild(String scope, String key,
                            Phantasm<RuleForm> phantasm) {
        model.getNetworkedModel(ruleform).getSingleChild(ruleform,
                                                         getRelationship(scope,
                                                                         key));
    }
}
