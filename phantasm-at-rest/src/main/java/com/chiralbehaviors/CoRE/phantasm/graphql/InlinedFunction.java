/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

class InlinedFunction implements DataFetcher {
    private final int                                                 crudIndex;
    private final int                                                 envIndex;
    private final Map<Integer, Function<Map<String, Object>, Object>> inputTxfms;
    private final Method                                              method;
    @SuppressWarnings("rawtypes")
    private final Class                                               phantasm;
    private final int                                                 phantasmIndex;
    private final Object                                              standIn;

    public InlinedFunction(Method method,
                           Map<Integer, Function<Map<String, Object>, Object>> inputTxfms,
                           Class<?> phantasm, Object instance) {
        this.method = method;
        List<Class<?>> parameterTypes = Arrays.asList(method.getParameters())
                                              .stream()
                                              .map(Parameter::getType)
                                              .collect(Collectors.toList());
        this.inputTxfms = inputTxfms;
        standIn = instance;
        this.phantasm = phantasm;
        envIndex = parameterTypes.indexOf(DataFetchingEnvironment.class);
        phantasmIndex = parameterTypes.indexOf(phantasm);
        crudIndex = parameterTypes.indexOf(PhantasmCRUD.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(DataFetchingEnvironment environment) {
        return get(environment, WorkspaceSchema.ctx(environment)
                                               .wrap(phantasm,
                                                     (ExistentialRuleform) environment.getSource()),
                   false);
    }

    @SuppressWarnings("unchecked")
    public Object get(DataFetchingEnvironment environment, Phantasm instance,
                      boolean initializer) {
        if (environment.getSource() == null) {
            return null;
        }
        Object[] argv = new Object[method.getParameterCount()];
        if (!initializer) {
            Object[] values = environment.getArguments()
                                         .values()
                                         .toArray();

            for (int i = 0; i < values.length; i++) {
                argv[i] = values[i];
            }
        }
        if (envIndex >= 0) {
            argv[envIndex] = environment;
        }
        if (phantasmIndex >= 0) {
            argv[phantasmIndex] = instance;
        }
        if (crudIndex >= 0) {
            argv[crudIndex] = environment.getContext();
        }
        for (int i = 0; i < argv.length; i++) {
            Function<Map<String, Object>, Object> txfm = inputTxfms.get(i);
            if (txfm != null) {
                argv[i] = txfm.apply((Map<String, Object>) argv[i]);
            }
        }
        try {
            return method.invoke(standIn, argv);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    protected Object invokeDefault(Object proxy, Method method, Object[] args,
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
