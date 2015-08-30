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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.util.UUID;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

/**
 * @author hhildebrand
 */
public class TransactionalResource {
    private final static Logger log = LoggerFactory.getLogger(TransactionalResource.class);

    private final EntityManagerFactory emf;

    public TransactionalResource(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected <RuleForm extends ExistentialRuleform<RuleForm, ?>> Aspect<RuleForm> getAspect(UUID classifier,
                                                                                             UUID classification,
                                                                                             NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        try {
            return networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Aspect<RuleForm> getAspect(String ruleformType,
                                                                                                                                              UUID relationship,
                                                                                                                                              UUID ruleform,
                                                                                                                                              Model readOnlyModel) {
        Class<ExistentialRuleform> ruleformClass = (Class<ExistentialRuleform>) RuleformResource.entityMap.get(ruleformType);
        if (ruleformClass == null) {
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformType),
                                              Status.NOT_FOUND);
        }
        Relationship classifier = readOnlyModel.getEntityManager()
                                               .find(Relationship.class,
                                                     relationship);
        if (classifier == null) {
            throw new WebApplicationException(String.format("classifier does not exist: %s",
                                                            relationship),
                                              Status.NOT_FOUND);
        }
        ExistentialRuleform classification = readOnlyModel.getEntityManager()
                                                          .find(ruleformClass,
                                                                ruleform);
        if (classification == null) {
            throw new WebApplicationException(String.format("classification does not exist: %s",
                                                            ruleform),
                                              Status.NOT_FOUND);
        }
        Aspect aspect = new Aspect(classifier, classification);
        return aspect;
    }

    protected Model getNewModel() {
        return new ModelImpl(emf);
    }

    protected <T> T perform(AuthorizedPrincipal principal,
                            Function<Model, T> txn) throws WebApplicationException {
        Model model = new ModelImpl(emf);
        EntityManager em = model.getEntityManager();
        em.getTransaction()
          .begin();
        if (principal == null) {
            principal = new AuthorizedPrincipal(model.getKernel()
                                                     .getUnauthenticatedAgency());
        } else {
            principal = em.merge(principal);
        }
        try {
            return model.executeAs(principal, () -> {
                try {
                    T value = txn.apply(model);
                    em.getTransaction()
                      .commit();
                    return value;
                } catch (Throwable e) {
                    log.error("error applying transaction", e);
                    throw new WebApplicationException(e, 500);
                } finally {
                    em.close();
                }
            });
        } catch (Exception e) {
            log.error("error applying transaction", e);
            throw new WebApplicationException(e, 500);
        }
    }

    protected <T> T readOnly(AuthorizedPrincipal principal,
                             Function<Model, T> txn) throws WebApplicationException {
        Model model = new ModelImpl(emf);
        EntityManager em = model.getEntityManager();
        em.getTransaction()
          .begin();
        em.getTransaction()
          .setRollbackOnly();
        if (principal == null) {
            principal = new AuthorizedPrincipal(model.getKernel()
                                                     .getUnauthenticatedAgency());
        } else {
            principal = principal.merge(em);
        }
        try {
            return model.executeAs(principal, () -> {
                try {
                    T value = txn.apply(model);
                    return value;
                } catch (Throwable e) {
                    log.error("error applying transaction", e);
                    throw new WebApplicationException(e, 500);
                } finally {
                    em.close();
                }
            });
        } catch (Exception e) {
            log.error("error applying transaction", e);
            throw new WebApplicationException(e, 500);
        }
    }
}