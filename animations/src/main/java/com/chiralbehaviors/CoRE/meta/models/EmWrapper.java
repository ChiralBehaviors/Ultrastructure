/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

/**
 * @author hhildebrand
 *
 */
public class EmWrapper implements EntityManager {
    public EmWrapper(Animations animpations, EntityManager em) {
        this.animations = animpations;
        this.em = em;
    }

    private class EtWrapper implements EntityTransaction {
        private EntityTransaction txn;

        private void setTxn(EntityTransaction txn) {
            this.txn = txn;
        }

        @Override
        public void begin() {
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
    }

    private final Animations    animations;
    private final EntityManager em;
    private final EtWrapper     etxn = new EtWrapper();

    @Override
    public void clear() {
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
    public Query createNamedQuery(String paramString) {
        return em.createNamedQuery(paramString);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String paramString,
                                              Class<T> paramClass) {
        return em.createNamedQuery(paramString, paramClass);
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
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> paramCriteriaQuery) {
        return em.createQuery(paramCriteriaQuery);
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
