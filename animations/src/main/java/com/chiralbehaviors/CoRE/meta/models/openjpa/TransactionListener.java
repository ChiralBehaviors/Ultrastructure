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

package com.chiralbehaviors.CoRE.meta.models.openjpa;

import javax.persistence.EntityManager;

import org.apache.openjpa.event.EndTransactionListener;
import org.apache.openjpa.event.TransactionEvent;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;

import com.chiralbehaviors.CoRE.meta.models.Animations;

/**
 * @author hhildebrand
 *
 */
public class TransactionListener implements EndTransactionListener {
    private final Animations triggers;

    public TransactionListener(Animations triggers, EntityManager em) {
        this.triggers = triggers;
        OpenJPAEntityManagerSPI openJpaEm = em.unwrap(OpenJPAEntityManagerSPI.class);
        openJpaEm.addTransactionListener(this);
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#beforeCommit(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void beforeCommit(TransactionEvent event) {
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#afterCommit(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void afterCommit(TransactionEvent event) {
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#afterRollback(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void afterRollback(TransactionEvent event) {
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#afterStateTransitions(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void afterStateTransitions(TransactionEvent event) {
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#afterCommitComplete(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void afterCommitComplete(TransactionEvent event) {
        triggers.afterCommit();
    }

    /* (non-Javadoc)
     * @see org.apache.openjpa.event.EndTransactionListener#afterRollbackComplete(org.apache.openjpa.event.TransactionEvent)
     */
    @Override
    public void afterRollbackComplete(TransactionEvent event) {
        triggers.afterRollback();
    }
}
