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
package com.chiralbehaviors.CoRE.phantasm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class StateImpl<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements InvocationHandler {
    @SafeVarargs
    public static <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void constrain(Model model,
                                                                                                                                      RuleForm ruleform,
                                                                                                                                      Aspect<RuleForm> aspect,
                                                                                                                                      Aspect<RuleForm>... aspects) {
        NetworkedModel<RuleForm, Network, ?, ?> networked = model.getNetworkedModel(ruleform);
        List<Aspect<RuleForm>> failures = new ArrayList<>();
        if (!networked.isAccessible(aspect.getClassifier(),
                                    aspect.getClassification(), ruleform)) {
            failures.add(aspect);
        }
        if (aspects != null) {
            for (Aspect<RuleForm> constraint : aspects) {
                if (!networked.isAccessible(constraint.getClassifier(),
                                            constraint.getClassification(),
                                            ruleform)) {
                    failures.add(constraint);
                }
            }
        }
        if (failures.isEmpty()) {
            throw new RuntimeException(
                                       String.format("%s does not have required aspects: ",
                                                     failures));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T, RuleForm extends ExistentialRuleform<RuleForm, ?>> T getAccessor(Class<T> accessorInterface,
                                                                                       RuleForm ruleform,
                                                                                       Model model) {
        return (T) Proxy.newProxyInstance(accessorInterface.getClassLoader(),
                                          new Class[] { accessorInterface },
                                          new StateImpl(ruleform, model));
    }

    private final RuleForm ruleform;

    protected final Model  model;

    public StateImpl(RuleForm ruleform, Model model) {
        this.ruleform = ruleform;
        this.model = model;
    }

    public void addChild(Relationship r, RuleForm child, Agency updatedBy) {
        model.getNetworkedModel(ruleform).link(ruleform, r, child, updatedBy);
    }

    public void addChildren(Relationship r, List<RuleForm> children,
                            Agency updatedBy) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        for (RuleForm child : children) {
            networkedModel.link(ruleform, r, child, updatedBy);
        }
    }

    public RuleForm getChild(Relationship r) {
        return model.getNetworkedModel(ruleform).getSingleChild(ruleform, r);
    }

    public List<RuleForm> getChildren(Relationship r) {
        return model.getNetworkedModel(ruleform).getChildren(ruleform, r);
    }

    public String getDescription() {
        return ruleform.getDescription();
    }

    public String getName() {
        return ruleform.getName();
    }

    public String getNotes() {
        return ruleform.getNotes();
    }

    public RuleForm getRuleform() {
        return ruleform;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
}
