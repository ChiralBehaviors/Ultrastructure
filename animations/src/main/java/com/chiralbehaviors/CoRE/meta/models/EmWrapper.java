/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * @author hhildebrand
 *
 */
public class EmWrapper implements EntityManager {
    private class EtWrapper implements EntityTransaction {
        private EntityTransaction txn;

        @Override
        public void begin() {
            animations.begin();
            txn.begin();
        }

        @Override
        public void commit() {
            animations.commit();
            txn.commit();
        }

        @Override
        public boolean getRollbackOnly() {
            return txn.getRollbackOnly();
        }

        @Override
        public boolean isActive() {
            return txn.isActive();
        }

        @Override
        public void rollback() {
            animations.rollback();
            txn.rollback();
        }

        @Override
        public void setRollbackOnly() {
            txn.setRollbackOnly();
        }

        private void setTxn(EntityTransaction txn) {
            this.txn = txn;
        }
    }

    private final Animations    animations;

    private final EntityManager em;
    private final EtWrapper     etxn = new EtWrapper();

    public EmWrapper(Animations animpations, EntityManager em) {
        this.animations = animpations;
        this.em = em;
    }

    @Override
    public void clear() {
        animations.flush();
        em.clear();
    }

    @Override
    public void close() {
        em.close();
    }

    @Override
    public boolean contains(Object paramObject) {
        return em.contains(paramObject);
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> arg0) {
        return em.createEntityGraph(arg0);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String arg0) {
        return em.createEntityGraph(arg0);
    }

    @Override
    public Query createNamedQuery(String paramString) {
        return em.createNamedQuery(paramString);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String paramString,
                                              Class<T> paramClass) {
        return em.createNamedQuery(paramString, paramClass);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String arg0) {
        return em.createNamedStoredProcedureQuery(arg0);
    }

    @Override
    public Query createNativeQuery(String paramString) {
        return em.createNativeQuery(paramString);
    }

    @Override
    public Query createNativeQuery(String paramString,
                                   @SuppressWarnings("rawtypes") Class paramClass) {
        return em.createNativeQuery(paramString, paramClass);
    }

    @Override
    public Query createNativeQuery(String paramString1, String paramString2) {
        return em.createNativeQuery(paramString1, paramString2);
    }

    @Override
    public Query createQuery(@SuppressWarnings("rawtypes") CriteriaDelete arg0) {
        return em.createQuery(arg0);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> paramCriteriaQuery) {
        return em.createQuery(paramCriteriaQuery);
    }

    @Override
    public Query createQuery(@SuppressWarnings("rawtypes") CriteriaUpdate arg0) {
        return em.createQuery(arg0);
    }

    @Override
    public Query createQuery(String paramString) {
        return em.createQuery(paramString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String paramString, Class<T> paramClass) {
        return em.createQuery(paramString, paramClass);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String arg0) {
        return em.createStoredProcedureQuery(arg0);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String arg0,
                                                           @SuppressWarnings("rawtypes") Class... arg1) {
        return em.createStoredProcedureQuery(arg0, arg1);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String arg0,
                                                           String... arg1) {
        return em.createStoredProcedureQuery(arg0, arg1);
    }

    @Override
    public void detach(Object paramObject) {
        em.detach(paramObject);
    }

    @Override
    public <T> T find(Class<T> paramClass, Object paramObject) {
        return em.find(paramClass, paramObject);
    }

    @Override
    public <T> T find(Class<T> paramClass, Object paramObject,
                      LockModeType paramLockModeType) {
        return em.find(paramClass, paramObject, paramLockModeType);
    }

    @Override
    public <T> T find(Class<T> paramClass, Object paramObject,
                      LockModeType paramLockModeType,
                      Map<String, Object> paramMap) {
        return em.find(paramClass, paramObject, paramLockModeType, paramMap);
    }

    @Override
    public <T> T find(Class<T> paramClass, Object paramObject,
                      Map<String, Object> paramMap) {
        return em.find(paramClass, paramObject, paramMap);
    }

    @Override
    public void flush() {
        animations.flush();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public Object getDelegate() {
        return em.getDelegate();
    }

    @Override
    public EntityGraph<?> getEntityGraph(String arg0) {
        return em.getEntityGraph(arg0);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> arg0) {
        return em.getEntityGraphs(arg0);
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return em.getEntityManagerFactory();
    }

    @Override
    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    @Override
    public LockModeType getLockMode(Object paramObject) {
        return em.getLockMode(paramObject);
    }

    @Override
    public Metamodel getMetamodel() {
        return em.getMetamodel();
    }

    @Override
    public Map<String, Object> getProperties() {
        return em.getProperties();
    }

    @Override
    public <T> T getReference(Class<T> paramClass, Object paramObject) {
        return em.getReference(paramClass, paramObject);
    }

    @Override
    public EntityTransaction getTransaction() {
        etxn.setTxn(em.getTransaction());
        return etxn;
    }

    @Override
    public boolean isJoinedToTransaction() {
        return em.isJoinedToTransaction();
    }

    @Override
    public boolean isOpen() {
        return em.isOpen();
    }

    @Override
    public void joinTransaction() {
        em.joinTransaction();
    }

    @Override
    public void lock(Object paramObject, LockModeType paramLockModeType) {
        em.lock(paramObject, paramLockModeType);
    }

    @Override
    public void lock(Object paramObject, LockModeType paramLockModeType,
                     Map<String, Object> paramMap) {
        em.lock(paramObject, paramLockModeType, paramMap);
    }

    @Override
    public <T> T merge(T paramT) {
        return em.merge(paramT);
    }

    @Override
    public void persist(Object paramObject) {
        em.persist(paramObject);
    }

    @Override
    public void refresh(Object paramObject) {
        em.refresh(paramObject);
    }

    @Override
    public void refresh(Object paramObject, LockModeType paramLockModeType) {
        em.refresh(paramObject, paramLockModeType);
    }

    @Override
    public void refresh(Object paramObject, LockModeType paramLockModeType,
                        Map<String, Object> paramMap) {
        em.refresh(paramObject, paramLockModeType, paramMap);
    }

    @Override
    public void refresh(Object paramObject, Map<String, Object> paramMap) {
        em.refresh(paramObject, paramMap);
    }

    @Override
    public void remove(Object paramObject) {
        em.remove(paramObject);
    }

    @Override
    public void setFlushMode(FlushModeType paramFlushModeType) {
        em.setFlushMode(paramFlushModeType);
    }

    @Override
    public void setProperty(String paramString, Object paramObject) {
        em.setProperty(paramString, paramObject);
    }

    @Override
    public <T> T unwrap(Class<T> paramClass) {
        return em.unwrap(paramClass);
    }

}
