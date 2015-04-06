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
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;

/**
 * @author hhildebrand
 *
 */
public class StateImpl<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements InvocationHandler, PhantasmBase<RuleForm> {

    private final StateDefinition<RuleForm, Network> definition;
    private final Model                              model;
    private final RuleForm                           ruleform;

    public StateImpl(RuleForm ruleform, Model model,
                     StateDefinition<RuleForm, Network> definition) {
        this.ruleform = ruleform;
        this.model = model;
        this.definition = definition;
    }

    @Override
    public String getDescription() {
        return ruleform.getDescription();
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
            return (args[0] instanceof PhantasmBase) ? ((PhantasmBase<?>) args[0]).getRuleform().equals(ruleform)
                                                    : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        Object returnValue = definition.methods.get(method).invoke(args);
        // always maintain proxy discipline.  Because identity.
        return returnValue == this ? proxy : returnValue;
    }

    @SuppressWarnings("unused")
    private void addChild(Relationship r, RuleForm child, Agency updatedBy) {
        model.getNetworkedModel(ruleform).link(ruleform, r, child, updatedBy);
    }

    @SuppressWarnings("unused")
    private void addChildren(Relationship r, List<RuleForm> children,
                             Agency updatedBy) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        for (RuleForm child : children) {
            networkedModel.link(ruleform, r, child, updatedBy);
        }
    }

    @SuppressWarnings("unused")
    private RuleForm getChild(Relationship r) {
        return model.getNetworkedModel(ruleform).getSingleChild(ruleform, r);
    }

    @SuppressWarnings("unused")
    private List<RuleForm> getChildren(Relationship r) {
        return model.getNetworkedModel(ruleform).getChildren(ruleform, r);
    }

    @SuppressWarnings("unused")
    private List<RuleForm> getChildren(Relationship r,
                                       @SuppressWarnings("unchecked") Aspect<RuleForm>... constraints) {
        return model.getNetworkedModel(ruleform).getChildren(ruleform, r);
    }
}
