/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.util;

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