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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hparry
 * 
 */
public class WorkspaceInvocationHandler implements InvocationHandler {

    private Workspace w;

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
        if (type.getSimpleName().equals("Agency")) {
           for (Agency a : w.getAgencies()) {
               if (a.getName().equals(name)) {
                   return a;
               }
           }
        }
        return null;
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
