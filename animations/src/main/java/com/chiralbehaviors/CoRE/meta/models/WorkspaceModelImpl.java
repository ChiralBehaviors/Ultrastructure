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
package com.chiralbehaviors.CoRE.meta.models;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final EntityManager em;

    public WorkspaceModelImpl(Model model) {
        em = model.getEntityManager();
    }

    public WorkspaceAuthorization get(Product definingProduct, String key) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATION,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("key", key);
        return query.getSingleResult();
    }

    public List<WorkspaceAuthorization> getByType(Product definingProduct,
                                                  String type) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATIONS_BY_TYPE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<WorkspaceAuthorization> getWorkspace(Product definingProduct) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_WORKSPACE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        return query.getResultList();
    }
}
