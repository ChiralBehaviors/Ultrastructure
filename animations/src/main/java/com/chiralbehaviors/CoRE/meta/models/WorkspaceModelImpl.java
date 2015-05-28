/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final EntityManager             em;
    private final Model                     model;
    private final Map<UUID, WorkspaceScope> scopes = new HashMap<>();

    public WorkspaceModelImpl(Model model) {
        this.model = model;
        em = model.getEntityManager();
    }

    @Override
    public WorkspaceScope createWorkspace(Product definingProduct,
                                          Agency updatedBy) {
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(
                                                                        definingProduct,
                                                                        model);
        workspace.add(definingProduct);
        workspace.add(model.getProductModel().link(definingProduct,
                                                   model.getKernel().getIsA(),
                                                   model.getKernel().getWorkspace(),
                                                   updatedBy));
        Aspect<Product> aspect = new Aspect<Product>(
                                                     model.getKernel().getIsA(),
                                                     model.getKernel().getWorkspace());
        model.getProductModel().initialize(definingProduct, aspect);
        model.getProductModel().getAttributesClassifiedBy(definingProduct,
                                                          aspect).forEach(attribute -> {
                                                                              workspace.add(attribute);
                                                                          });
        WorkspaceScope scope = workspace.getScope();
        scopes.put(definingProduct.getId(), scope);
        return scope;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#flush()
     */
    @Override
    public void flush() {
        for (WorkspaceScope scope : scopes.values()) {
            scope.getWorkspace().flushCache();
        }
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
        WorkspaceScope cached = scopes.get(definingProduct.getId());
        if (cached != null) {
            return cached;
        }
        WorkspaceScope scope = new WorkspaceScope(
                                                  null,
                                                  new DatabaseBackedWorkspace(
                                                                              definingProduct,
                                                                              model));
        scopes.put(definingProduct.getId(), scope);
        if (!definingProduct.getId().equals(WellKnownProduct.KERNEL_WORKSPACE.id())) { // special handling of kernel ::sigh::
            for (Product workspace : model.getProductModel().getChildren(definingProduct,
                                                                         model.getKernel().getImports())) {
                scope.add("", getScoped(workspace).getWorkspace());
            }
        }
        return scope;
    }

    @Override
    public WorkspaceScope getScoped(UUID definingProduct) {
        Product product = em.find(Product.class, definingProduct);
        if (product == null) {
            throw new IllegalStateException("Defining Product is null");
        }
        return getScoped(product);
    }

    @Override
    public List<WorkspaceAuthorization> getWorkspace(Product definingProduct) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_WORKSPACE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        return query.getResultList();
    }
}
