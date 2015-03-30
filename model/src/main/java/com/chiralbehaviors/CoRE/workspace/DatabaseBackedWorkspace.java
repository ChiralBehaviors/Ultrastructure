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
public class DatabaseBackedWorkspace implements EditableWorkspace {

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
