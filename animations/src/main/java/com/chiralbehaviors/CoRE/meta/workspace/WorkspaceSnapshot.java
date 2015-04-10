/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshot {
    protected final List<WorkspaceAuthorization> auths;

    public WorkspaceSnapshot() {
        auths = null;
    }

    public WorkspaceSnapshot(List<WorkspaceAuthorization> auths) {
        this.auths = auths;
    }

    public WorkspaceSnapshot(Product definingProduct, EntityManager em) {
        TypedQuery<WorkspaceAuthorization> query = em.createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                          + "WHERE auth.definingProduct = :product",
                                                                  WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        auths = new ArrayList<>(query.getResultList());
    }

    public List<WorkspaceAuthorization> getAuths() {
        return auths;
    }

    public void retarget(EntityManager em) {
        for (WorkspaceAuthorization auth : auths) {
            em.persist(auth);
        }
    }
}
