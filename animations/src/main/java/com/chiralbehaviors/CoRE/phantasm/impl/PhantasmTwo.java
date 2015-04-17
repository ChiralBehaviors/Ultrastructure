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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;

/**
 * @author hhildebrand
 *
 */
public class PhantasmTwo implements InvocationHandler {
    private final Map<Class<?>, Object> stateMap;
    private final Ruleform              ruleform;

    public PhantasmTwo(Map<Class<?>, Object> stateMap, Ruleform ruleform) {
        this.stateMap = stateMap;
        this.ruleform = ruleform;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        Object state = stateMap.get(method.getDeclaringClass());
        if (state != null) {
            return method.invoke(state, args);
        }
        if (method.isDefault()) {
            final Class<?> declaringClass = method.getDeclaringClass();
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,
                                                                                                              int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(declaringClass,
                                           MethodHandles.Lookup.PRIVATE).unreflectSpecial(method,
                                                                                          declaringClass).bindTo(proxy).invokeWithArguments(args);
        }
        // equals() and hashCode().  Becauase invariance.
        if (method.getName().equals("equals") && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return (args[0] instanceof Phantasm) ? ruleform.equals(((Phantasm<?>) args[0]).getRuleform())
                                                : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        throw new IllegalStateException(
                                        String.format("Not a default or state method: %s",
                                                      method.toGenericString()));
    }
}
