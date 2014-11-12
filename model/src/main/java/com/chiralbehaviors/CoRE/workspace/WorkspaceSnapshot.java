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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshot {
    private final List<WorkspaceAuthorization> auths;

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
