/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta.models;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceScope;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final EntityManager em;

    public WorkspaceModelImpl(Model model) {
        em = model.getEntityManager();
    }

    @Override
    public WorkspaceAuthorization get(Product definingProduct, String key) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATION,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("key", key);
        return query.getSingleResult();
    }

    @Override
    public List<WorkspaceAuthorization> getByType(Product definingProduct,
                                                  String type) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATIONS_BY_TYPE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("type", type);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getScoped(java.util.UUID)
     */
    @Override
    public WorkspaceScope getScoped(Product definingProduct) {
        return new WorkspaceScope(null, null,
                                  new DatabaseBackedWorkspace(definingProduct,
                                                              em));
    }

    @Override
    public List<WorkspaceAuthorization> getWorkspace(Product definingProduct) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_WORKSPACE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        return query.getResultList();
    }
}
