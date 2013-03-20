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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author hhildebrand
 * 
 */
public abstract class AbstractParam<V> {
    private final String originalParam;
    private final V      value;

    public AbstractParam(String param) throws WebApplicationException {
        this.originalParam = param;
        try {
            this.value = parse(param);
        } catch (Throwable e) {
            throw new WebApplicationException(onError(param, e));
        }
    }

    public String getOriginalParam() {
        return originalParam;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    protected String getErrorMessage(String param, Throwable e) {
        return "Invalid parameter: " + param + " (" + e.getMessage() + ")";
    }

    protected Response onError(String param, Throwable e) {
        return Response.status(Status.BAD_REQUEST).entity(getErrorMessage(param,
                                                                          e)).build();
    }

    protected abstract V parse(String param) throws Throwable;
}