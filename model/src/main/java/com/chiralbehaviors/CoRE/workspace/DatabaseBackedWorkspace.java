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

import java.util.AbstractList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class DatabaseBackedWorkspace implements Workspace {

    public class EntityList<T extends Ruleform> extends AbstractList<T> {
        private final List<WorkspaceAuthorization> backingList;

        public EntityList(List<WorkspaceAuthorization> backingList) {
            this.backingList = backingList;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractList#get(int)
         */
        @SuppressWarnings("unchecked")
        @Override
        public T get(int index) {
            return (T) backingList.get(index).getEntity();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return backingList.size();
        }
    }

    protected final Product       definingProduct;
    protected final EntityManager em;

    public DatabaseBackedWorkspace(Product definingProduct, EntityManager em) {
        assert definingProduct != null;
        this.definingProduct = definingProduct;
        this.em = em;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#add(com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void add(T ruleform) {
        WorkspaceAuthorization authorization = new WorkspaceAuthorization();
        authorization.setEntity(ruleform);
        authorization.setDefiningProduct(definingProduct);
        em.persist(authorization);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#get(java.lang.String)
     */
    @Override
    public <T extends Ruleform> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkspaceAuthorization> query = cb.createQuery(WorkspaceAuthorization.class);
        Root<WorkspaceAuthorization> from = query.from(WorkspaceAuthorization.class);
        query.select(from).where(cb.and(cb.equal(from.get(WorkspaceAuthorization_.key),
                                                 key),
                                        cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                                 definingProduct)));
        try {
            WorkspaceAuthorization authorization = em.createQuery(query).getSingleResult();
            return authorization.getEntity();
        } catch (NoResultException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAccesor(java.lang.Class)
     */
    @Override
    public <T> T getAccesor(Class<T> accessorInterface) {
        return WorkspaceAccessHandler.getAccesor(accessorInterface, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getCollection(java.lang.Class)
     */
    @Override
    public <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkspaceAuthorization> query = cb.createQuery(WorkspaceAuthorization.class);
        Root<WorkspaceAuthorization> from = query.from(WorkspaceAuthorization.class);
        query.select(from).where(cb.and(cb.equal(from.get(WorkspaceAuthorization_.type),
                                                 ruleformClass.getSimpleName()),
                                        cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                                 definingProduct)));
        return new EntityList<T>(em.createQuery(query).getResultList());
    }

    @Override
    public WorkspaceSnapshot getSnapshot() {
        return new WorkspaceSnapshot(definingProduct, em);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void put(String key, T ruleform) {
        WorkspaceAuthorization authorization = new WorkspaceAuthorization();
        authorization.setDefiningProduct(definingProduct);
        authorization.setEntity(ruleform);
        authorization.setKey(key);
        em.persist(authorization);
    }
}
