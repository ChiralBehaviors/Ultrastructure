/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.utils.Util;

/**
 * @author hhildebrand
 */
public class TransactionalResource {

    @FunctionalInterface
    public interface Transactionally<T> {
        T exec(Model model) throws SQLException;
    }

    private final EntityManagerFactory emf;
    protected final Model              readOnlyModel;

    public TransactionalResource(EntityManagerFactory emf) {
        this.emf = emf;
        readOnlyModel = new ModelImpl(emf);
    }

    public void close() {
        readOnlyModel.getEntityManager().close();
    }

    protected Model getNewModel() {
        return new ModelImpl(emf);
    }

    protected UUID insert(final Ruleform ruleform) {
        return perform((Model model) -> Util.smartMerge(model.getEntityManager(), ruleform, new HashMap<>()).getId());
    }

    protected <T> T perform(Transactionally<T> txn) throws WebApplicationException {
        Model model = new ModelImpl(emf);
        EntityManager em = model.getEntityManager();
        em.getTransaction().begin();
        try {
            T value = txn.exec(model);
            em.getTransaction().commit();
            return value;
        } catch (Throwable e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new WebApplicationException(e, 500);
        } finally {
            em.close();
        }
    }

}