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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {
    private final List<StateDefinition<RuleForm>> facets = new ArrayList<>();
    private final Class<PhantasmBase<RuleForm>>   phantasm;

    @SuppressWarnings("unchecked")
    public PhantasmDefinition(Class<PhantasmBase<RuleForm>> phantasm) {
        this.phantasm = phantasm;
        if (phantasm.getAnnotation(State.class) != null) {
            StateDefinition<RuleForm> facet = new StateDefinition<RuleForm>(
                                                                            phantasm);
            facets.add(facet);
        }
        for (Class<?> iFace : phantasm.getInterfaces()) {
            if (iFace.getAnnotation(State.class) != null) {
                facets.add(new StateDefinition<RuleForm>(
                                                         (Class<PhantasmBase<RuleForm>>) iFace));
            }
        }
    }

    /**
     * @param ruleform
     * @param modelImpl
     * @param updatedBy
     * @return
     */
    public PhantasmBase<RuleForm> construct(ExistentialRuleform<?, ?> ruleform,
                                            Model model, Agency updatedBy) {
        @SuppressWarnings("unchecked")
        RuleForm form = (RuleForm) ruleform;
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(form);
        for (StateDefinition<RuleForm> facet : facets) {
            for (Aspect<RuleForm> aspect : facet.getAspects(model)) {
                networkedModel.initialize(form, aspect, updatedBy);
            }
        }
        return wrap(ruleform, model);
    }

    @SuppressWarnings("unchecked")
    public PhantasmBase<RuleForm> wrap(ExistentialRuleform<?, ?> ruleform,
                                       Model model) {
        Map<Class<?>, Object> stateMap = new HashMap<>();
        Object[] instances = new Object[facets.size()];
        int i = 0;
        for (StateDefinition<RuleForm> facet : facets) {
            Object state = facet.construct((RuleForm) ruleform, model);
            instances[i++] = state;
            stateMap.put(facet.getStateInterface(), state);
        }
        return (PhantasmBase<RuleForm>) Proxy.newProxyInstance(phantasm.getClassLoader(),
                                                               new Class[] { phantasm },
                                                               new Phantasm(
                                                                            stateMap, ruleform));

    }
}
