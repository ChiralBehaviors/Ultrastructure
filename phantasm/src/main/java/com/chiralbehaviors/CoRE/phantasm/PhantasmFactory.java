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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.janus.CompositeAssembler;

/**
 * @author hhildebrand
 *
 */
public class PhantasmFactory {
    public <T, RuleForm extends ExistentialRuleform<RuleForm, ?>> T acquire(Model model,
                                                                            RuleForm ruleform,
                                                                            Class<T> phantasm,
                                                                            Class<?>... mixins) {
        List<Object> instances = constructPhantasmsOf(phantasm, ruleform);
        if (mixins != null) {
            for (Class<?> mixin : mixins) {
                instances.add(constructInstanceOf(mixin));
            }
        }
        CompositeAssembler<T> assembler = new CompositeAssembler<T>(phantasm);

        return assembler.construct(instances.toArray());
    }

    public <T, RuleForm extends ExistentialRuleform<RuleForm, ?>> T construct(Model model,
                                                                              Class<RuleForm> ruleform,
                                                                              String name,
                                                                              String description,
                                                                              Agency updatedBy,
                                                                              Class<T> phantasm,
                                                                              Class<?>... mixins) {
        RuleForm ruleformInstance = constructInstance(ruleform, name,
                                                      description, updatedBy);
        model.getEntityManager().persist(ruleformInstance);
        List<Object> instances = constructPhantasmsOf(phantasm,
                                                      ruleformInstance);
        if (mixins != null) {
            for (Class<?> mixin : mixins) {
                instances.add(constructInstanceOf(mixin));
            }
        }
        CompositeAssembler<T> assembler = new CompositeAssembler<T>(phantasm);

        return assembler.construct(instances.toArray());
    }

    private <T> T constructInstance(Class<T> ruleform, String name,
                                    String description, Agency updatedBy) {
        Constructor<T> constructor;
        try {
            constructor = ruleform.getConstructor(String.class, String.class,
                                                  Agency.class);
            return constructor.newInstance(name, description, updatedBy);
        } catch (NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(
                                            String.format("Cannot get constructor for %s",
                                                          ruleform), e);
        }
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
                                            String.format("Cannot get no argument constructor for %s",
                                                          mixin), e);
        }
    }

    private List<Object> constructPhantasmsOf(Class<?> phantasm,
                                              ExistentialRuleform<?, ?> ruleform) {
        List<Object> instances = new ArrayList<>();

        return instances;
    }
}
