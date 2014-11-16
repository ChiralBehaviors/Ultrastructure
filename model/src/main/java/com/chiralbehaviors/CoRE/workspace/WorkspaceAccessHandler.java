/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.workspace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.chiralbehaviors.CoRE.Ruleform;

/**
 * @author hparry
 *
 */
public class WorkspaceAccessHandler implements InvocationHandler {
    @SuppressWarnings("unchecked")
    public static <T> T getAccesor(Class<T> accessorInterface,
                                   Workspace workspace) {
        return (T) Proxy.newProxyInstance(accessorInterface.getClassLoader(),
                                          new Class[] { accessorInterface },
                                          new WorkspaceAccessHandler(workspace));
    }

    private final Workspace workspace;

    public WorkspaceAccessHandler(Workspace workspace) {
        this.workspace = workspace;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        Key key = method.getAnnotation(Key.class);
        if (key != null) {
            Ruleform ruleform = workspace.get(key.value());
            if (ruleform == null) {
                throw new NullPointerException(
                                               String.format("The value for %s is null",
                                                             key.value()));
            }
        }
        return getAsBeanAccessor(method);
    }

    protected Ruleform getAsBeanAccessor(Method method) {
        String name = method.getName();
        if (!name.startsWith("get")) {
            throw new UnsupportedOperationException(
                                                    String.format("Cannot create key for method: %s",
                                                                  method));
        }
        name = name.substring("get".length());
        Ruleform ruleform = workspace.get(name);
        if (ruleform == null) {
            throw new NullPointerException(
                                           String.format("The value for %s is null",
                                                         name));
        }
        return ruleform;
    }
}
