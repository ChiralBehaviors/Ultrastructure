/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.InvalidKeyException;

import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;

/**
 * @author hparry
 *
 */
public class WorkspaceAccessHandler implements InvocationHandler {
    @SuppressWarnings("unchecked")
    public static <T> T getAccesor(Class<T> accessorInterface,
                                   WorkspaceScope workspace) {
        return (T) Proxy.newProxyInstance(accessorInterface.getClassLoader(),
                                          new Class[] { accessorInterface },
                                          new WorkspaceAccessHandler(workspace));
    }

    private final WorkspaceScope workspace;

    public WorkspaceAccessHandler(WorkspaceScope workspace) {
        this.workspace = workspace;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method,
                         Object[] args) throws Throwable {
        Key key = method.getAnnotation(Key.class);
        if (key != null) {
            return workspace.lookup(key);
        }
        return getAsBeanAccessor(method);
    }

    protected Object getAsBeanAccessor(Method method) throws InvalidKeyException {
        String name = method.getName();
        if (!name.startsWith("get")) {
            throw new UnsupportedOperationException(String.format("Cannot create key for method: %s",
                                                                  method));
        }
        name = name.substring("get".length());
        Object ruleform = workspace.lookup(ReferenceType.Existential, name);
        return ruleform;
    }
}
