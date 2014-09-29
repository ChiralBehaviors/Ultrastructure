/**
 * Copyright (c) 2014 Chiral Behaviors, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.object.painter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hparry
 *
 */
public class WorkspaceManifester {

    private final Logger  log = LoggerFactory.getLogger(getClass());
    private Object        workspace;
    private EntityManager em;
    private Product       parent;

    public WorkspaceManifester(Product parent, Object workspace,
                               EntityManager em, Model model) {
        this.parent = parent;
        this.workspace = workspace;
        this.em = em;
    }

    public Product manifestWorkspace() {
        em.persist(parent);
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(workspace.getClass());

            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                try {
                    @SuppressWarnings("unused")
                    Ruleform rf = (Ruleform) pd.getReadMethod().invoke(workspace,
                                                                       (Object[]) null);
                    WorkspaceAuthorization auth = new WorkspaceAuthorization();
                    auth.setDefiningProduct(parent);
                    //                switch (rf.getClass().getSimpleName()) {
                    //                    case 
                    //                }
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Unable to create authorization for field $s",
                                                pd.getReadMethod().getName()));
                    }
                }
            }
        } catch (IntrospectionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return parent;
    }
}
