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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.janus.CompositeAssembler;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {
    private final List<Class<?>>                                             mixins = new ArrayList<>();
    private final List<StateDefinition<RuleForm, NetworkRuleform<RuleForm>>> facets = new ArrayList<>();
    private final Class<PhantasmBase<RuleForm>>                              phantasm;

    public PhantasmDefinition(Class<PhantasmBase<RuleForm>> phantasm) {
        this.phantasm = phantasm;
    }

    public PhantasmBase<RuleForm> construct(RuleForm ruleform, Model model) {
        CompositeAssembler<PhantasmBase<RuleForm>> assembler = new CompositeAssembler<>(
                                                                                        phantasm);
        Object[] instances = new Object[mixins.size() + facets.size()];
        int i = 0;
        for (StateDefinition<RuleForm, NetworkRuleform<RuleForm>> facet : facets) {
            instances[i++] = facet.construct(ruleform, model);
        }
        for (Class<?> mixin : mixins) {
            instances[i++] = constructInstanceOf(mixin);
        }
        return assembler.construct(instances);
    }

    private Object constructInstanceOf(Class<?> mixin) {
        Constructor<?> constructor;
        try {
            constructor = mixin.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(
                                            String.format("Cannot get no argument constructor for mixin %s of %s",
                                                          mixin, phantasm), e);
        }
    }
}
