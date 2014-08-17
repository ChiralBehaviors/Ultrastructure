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

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.workspace.api.WorkspaceInvocationHandler;

/**
 * @author hparry
 * 
 */
public class ApiTest {

    @Test
    public void testApi() {
        Agency a = new Agency("RogueAgency", null, null);
        WorkspaceSnapshot w = new WorkspaceSnapshot();
        w.setAgencies(Arrays.asList(new Agency[] { a }));

        WorkspaceInvocationHandler handler = new WorkspaceInvocationHandler(w);
        RogueKernel kernel = (RogueKernel) Proxy.newProxyInstance(RogueKernel.class.getClassLoader(),
                                                                  new Class<?>[] { RogueKernel.class },
                                                                  handler);
        assertNotNull(kernel.getRogueAgency());
    }

}
