/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.util;

import javax.ws.rs.core.Context;

import com.sun.jersey.api.uri.UriComponent.Type;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @author hhildebrand
 * 
 */
public abstract class AbstractInjectableProvider<E> extends
        AbstractHttpContextInjectable<E> implements
        InjectableProvider<Context, Type> {

    private final Type t;

    public AbstractInjectableProvider(Type t) {
        this.t = t;
    }

    public Injectable<E> getInjectable(ComponentContext ic, Context a) {
        return this;
    }

    @Override
    public Injectable<E> getInjectable(ComponentContext ic, Context a, Type c) {
        if (c.equals(t)) {
            return getInjectable(ic, a);
        }

        return null;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }
}
