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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;

/**
 * @author hhildebrand
 *
 */
public class StateDefinition<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {

    private final Class<PhantasmBase<RuleForm>> accessorInterface;
    private final List<Aspect<RuleForm>>        aspects = new ArrayList<Aspect<RuleForm>>();
    protected final Map<Method, QueryFunction>  methods = new HashMap<>();

    public StateDefinition(Class<PhantasmBase<RuleForm>> accessorInterface) {
        this.accessorInterface = accessorInterface;
    }

    public void constrain(Model model, RuleForm ruleform) {
        NetworkedModel<RuleForm, Network, ?, ?> networked = model.getNetworkedModel(ruleform);
        List<Aspect<RuleForm>> failures = new ArrayList<>();
        for (Aspect<RuleForm> constraint : aspects) {
            if (!networked.isAccessible(constraint.getClassifier(),
                                        constraint.getClassification(),
                                        ruleform)) {
                failures.add(constraint);
            }
        }
        if (failures.isEmpty()) {
            throw new RuntimeException(
                                       String.format("%s does not have required aspects: ",
                                                     failures));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object construct(RuleForm ruleform, Model model) {
        constrain(model, ruleform);
        return Proxy.newProxyInstance(accessorInterface.getClassLoader(),
                                      new Class[] { accessorInterface },
                                      new StateImpl(ruleform, model, this));

    }
}
