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
package com.chiralbehaviors.CoRE.workspace.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hparry
 * 
 */
public class WorkspaceInvocationHandler implements InvocationHandler {

    private Workspace w;
    private static final Map<String, String> functionMap;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Agency.class.getSimpleName(), "getAgency");
        map.put(Attribute.class.getSimpleName(), "getAttribute");
        map.put(Location.class.getSimpleName(), "getLocation");
        map.put(Product.class.getSimpleName(), "getProduct");
        map.put(Relationship.class.getSimpleName(), "getRelationship");
        map.put(StatusCode.class.getSimpleName(), "getStatusCode");
        map.put(Unit.class.getSimpleName(), "getUnit");
        functionMap = map;
        
    }

    public WorkspaceInvocationHandler(Workspace workspace) {
        this.w = workspace;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        
        Class<?> type = method.getReturnType();
        
        String name = getObjectName(method);
        
        String functionName = functionMap.get(type.getSimpleName());
        Method getter = w.getClass().getMethod(functionName, String.class);
        return getter.invoke(w, name);
    }

    /**
     * @param method
     * @return
     */
    private String getObjectName(Method method) {
        String methodName = method.getName();
        return methodName.split("get")[1];
    }

}
