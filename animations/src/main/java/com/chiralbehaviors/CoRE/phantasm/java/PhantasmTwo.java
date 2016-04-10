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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

/**
 * @author hhildebrand
 *
 */
public class PhantasmTwo extends PhantasmCRUD
        implements InvocationHandler, ScopedPhantasm {
    private final PhantasmDefinition  definition;
    private final ExistentialRuleform ruleform;

    public PhantasmTwo(ExistentialRuleform ruleform,
                       PhantasmDefinition definition, Model model) {
        super(model);
        this.ruleform = ruleform;
        this.definition = definition;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm#cast(java.lang.Class)
     */
    @Override
    public <T extends Phantasm> T cast(Class<T> toPhantasm) {
        return model.cast(getRuleform(), toPhantasm);
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
    public ExistentialRuleform getRuleform() {
        return ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        throw new IllegalStateException("This should have never been called");
    }

    public Object getScope(PhantasmDefinition facetDefinition) {
        return model.getWorkspaceModel()
                    .getScoped(facetDefinition.getWorkspace());
    }

    public Agency getUpdatedBy() {
        return model.create()
                    .selectFrom(EXISTENTIAL)
                    .where(EXISTENTIAL.ID.equal(ruleform.getUpdatedBy()))
                    .fetchOne()
                    .into(Agency.class);
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
        if (method.getName()
                  .equals("getRuleform")
            && method.getParameterCount() == 0) {
            return ruleform;
        }
        StateFunction function = definition.methods.get(method);
        if (function != null) {
            WorkspaceScope scope = model.getWorkspaceModel()
                                        .getScoped(definition.getWorkspace());
            return function.invoke(this, scope, args);
        }
        // equals() and hashCode().  Becauase invariance.
        if (method.getName()
                  .equals("equals")
            && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return args[0] instanceof Phantasm ? ruleform.getId()
                                                         .equals(((Phantasm) args[0]).getRuleform()
                                                                                     .getId())
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
                             ruleform.getName());
    }

    /**
     * @param returnPhantasm
     * @param ruleform2
     * @return
     */
    public <T extends ExistentialRuleform, R extends Phantasm> R wrap(Class<R> phantasm,
                                                                      T ruleform) {
        return model.wrap(phantasm, ruleform);
    }
}
