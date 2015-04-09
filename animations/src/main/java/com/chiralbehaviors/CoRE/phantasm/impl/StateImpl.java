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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.CoRE.workspace.WorkspaceScope;

/**
 * @author hhildebrand
 *
 */
public class StateImpl<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        implements InvocationHandler, PhantasmBase<RuleForm> {

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
     * @see com.chiralbehaviors.CoRE.phantasm.PhantasmBase#getModel()
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
     * @see com.chiralbehaviors.CoRE.phantasm.PhantasmBase#getRuleform()
     */
    @Override
    public RuleForm getRuleform() {
        return ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.PhantasmBase#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        return scope;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.PhantasmBase#getUpdatedBy()
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
            return (args[0] instanceof PhantasmBase) ? ruleform.equals(((PhantasmBase<?>) args[0]).getRuleform())
                                                    : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        StateFunction<RuleForm> function = methods.get(method);
        Object returnValue = (function != null) ? function.invoke(this, args)
                                               : method.invoke(this, args);

        // always maintain proxy discipline.  Because identity.
        return returnValue == this ? proxy : returnValue;
    }

    private Attribute getAttribute(String s, String key) {
        return (Attribute) scope.lookup(s, key);
    }

    private Relationship getRelationship(String s, String key) {
        return (Relationship) scope.lookup(s, key);
    }

    protected void addChild(String scope, String key, boolean singular,
                            RuleForm child) {
        model.getNetworkedModel(ruleform).link(ruleform,
                                               getRelationship(scope, key),
                                               child,
                                               model.getKernel().getCore());
    }

    protected void addChildren(String scope, String key,
                               List<RuleForm> children, Agency updatedBy) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        for (RuleForm child : children) {
            networkedModel.link(ruleform, getRelationship(scope, key), child,
                                updatedBy);
        }
    }

    protected Object getAttributeValue(String scope, String key) {
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
        return values.get(0).getValue();
    }

    protected List<Object> getAttributeValues(String scope, String key) {
        List<Object> values = new ArrayList<>();
        for (AttributeValue<RuleForm> value : model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                   getAttribute(scope,
                                                                                                                key))) {
            values.add(value.getValue());
        }
        return values;
    }

    protected RuleForm getChild(String scope, String key, boolean singular) {
        return model.getNetworkedModel(ruleform).getSingleChild(ruleform,
                                                                getRelationship(scope,
                                                                                key));
    }

    protected List<RuleForm> getChildren(String scope, String key) {
        return model.getNetworkedModel(ruleform).getChildren(ruleform,
                                                             getRelationship(scope,
                                                                             key));
    }

    protected Object setAttributeValue(String scope, String key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Object setAttributeValues(String scope, String key, List<?> values) {
        // TODO Auto-generated method stub
        return null;
    }
}
