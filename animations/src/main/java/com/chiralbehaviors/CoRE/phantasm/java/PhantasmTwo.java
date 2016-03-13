/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.java;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

/**
 * @author hhildebrand
 *
 */
public class PhantasmTwo<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        extends PhantasmCRUD<RuleForm, NetworkRuleform<RuleForm>>
        implements InvocationHandler, ScopedPhantasm<RuleForm> {
    private final PhantasmDefinition<RuleForm> definition;
    private final RuleForm                     ruleform;

    public PhantasmTwo(RuleForm ruleform,
                       PhantasmDefinition<RuleForm> definition, Model model) {
        super(model);
        this.ruleform = ruleform;
        this.definition = definition;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm#cast(java.lang.Class)
     */
    @Override
    public <T extends Phantasm<RuleForm>> T cast(Class<T> toPhantasm) {
        return model.cast(ruleform, toPhantasm);
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

    public Object getScope(PhantasmDefinition<RuleForm> facetDefinition) {
        return model.getWorkspaceModel()
                    .getScoped(facetDefinition.getWorkspace());
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
    public Object invoke(Object proxy, Method method,
                         Object[] args) throws Throwable {
        if (method.getName()
                  .equals("getClass")
            && method.getParameterCount() == 0) {
            return definition.getPhantasm();
        }
        StateFunction<RuleForm> function = definition.methods.get(method);
        if (function != null) {
            WorkspaceScope scope = model.getWorkspaceModel()
                                        .getScoped(definition.getWorkspace());
            return function.invoke(this, scope, args);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        if (method.isDefault()) {
            return invokeDefault(proxy, method, args, declaringClass);
        }
        // equals() and hashCode().  Becauase invariance.
        if (method.getName()
                  .equals("equals")
            && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return (args[0] instanceof Phantasm) ? ruleform.equals(((Phantasm<?>) args[0]).getRuleform())
                                                 : false;
        } else if (method.getName()
                         .equals("hashCode")
                   && (args == null || args.length == 0)) {
            return ruleform.hashCode();
        } else if (method.getName()
                         .equals("toString")
                   && (args == null || args.length == 0)) {
            return String.format("%s[%s]", definition.getPhantasm()
                                                     .getSimpleName(),
                                 ruleform.getName());
        }
        try {
            return method.invoke(this, args);
        } catch (IllegalArgumentException | IllegalAccessException
                | InvocationTargetException e) {
            throw new IllegalStateException(String.format("Error invoking: %s",
                                                          method.toGenericString()),
                                            e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", definition.getPhantasm()
                                                 .getSimpleName(),
                             ruleform.toString());
    }

    /**
     * @param returnPhantasm
     * @param ruleform2
     * @return
     */
    public <T extends ExistentialRuleform<?, ?>, R extends Phantasm<?>> R wrap(Class<R> phantasm,
                                                                               ExistentialRuleform<?, ?> ruleform) {
        return model.wrap(phantasm, ruleform);
    }

    private Object invokeDefault(Object proxy, Method method, Object[] args,
                                 final Class<?> declaringClass) throws NoSuchMethodException,
                                                                Throwable,
                                                                IllegalAccessException,
                                                                InstantiationException,
                                                                InvocationTargetException {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,
                                                                                                          int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(declaringClass,
                                       MethodHandles.Lookup.PRIVATE)
                          .unreflectSpecial(method, declaringClass)
                          .bindTo(proxy)
                          .invokeWithArguments(args);
    }
}
