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

package com.chiralbehaviors.CoRE.meta.models;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 *         A workspace backed by a database
 */
public class ModelBackedWorkspace implements NeuvoWorkspace {

    private class EntityList<T extends Ruleform> extends AbstractList<T> {
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

    private final Product definingProduct;
    private final Model   model;

    public ModelBackedWorkspace(Product definingProduct, Model model) {
        this.model = model;
        this.definingProduct = definingProduct;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#add(com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void add(T ruleform) {
        WorkspaceAuthorization authorization = new WorkspaceAuthorization();
        authorization.setEntity(ruleform);
        authorization.setDefiningProduct(definingProduct);
        model.getEntityManager().persist(authorization);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#get(java.lang.String)
     */
    @Override
    public <T extends Ruleform> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        TypedQuery<WorkspaceAuthorization> query = model.getEntityManager().createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                                                + "WHERE auth.key = :key "
                                                                                                + "AND auth.definingProduct = :definingProduct",
                                                                                        WorkspaceAuthorization.class);
        query.setParameter("key", key);
        query.setParameter("definingProduct", definingProduct);
        try {
            WorkspaceAuthorization authorization = query.getSingleResult();
            return authorization.getEntity();
        } catch (NoResultException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getAttributeAuthorizations(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Attribute> getAttributeAuthorizations(RuleForm parent,
                                                                                                                                                           Relationship relationship) {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getAttributes()
     */
    @Override
    public <Value extends AttributeValue<?>, RuleForm extends ExistentialRuleform<RuleForm, ?>> Value getAttributes() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getChildren(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(RuleForm parent,
                                                                                                                                           Relationship relationship) {
        CriteriaBuilder cb = model.getEntityManager().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<RuleForm> query = (CriteriaQuery<RuleForm>) cb.createQuery(parent.getClass());
        @SuppressWarnings("unchecked")
        Root<NetworkRuleform<RuleForm>> networkRoot = (Root<NetworkRuleform<RuleForm>>) query.from(parent.getNetworkClass());
        // Join<RuleForm, WorkspaceAuthorization> inWorkspace = networkRoot.join(networkRoot.fetch("parent"), null);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                 parent),
                                        cb.equal(networkRoot.get("relationship"),
                                                 relationship),
                                        cb.equal(networkRoot.get("inference").get("id"),
                                                 "AAAAAAAAAAAAAAAAAAAAAA")));
        TypedQuery<RuleForm> q = model.getEntityManager().createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getCollection(java.lang.Class)
     */
    @Override
    public <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass) {
        TypedQuery<WorkspaceAuthorization> query = model.getEntityManager().createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                                                + "WHERE auth.type = :type "
                                                                                                + "AND auth.definingProduct = :definingProduct",
                                                                                        WorkspaceAuthorization.class);
        query.setParameter("type", ruleformClass.getSimpleName());
        query.setParameter("definingProduct", definingProduct);
        return new EntityList<T>(query.getResultList());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getParents(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getParents(RuleForm child,
                                                                                                                                          Relationship relationship) {
        return Collections.emptyList();
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
        model.getEntityManager().persist(authorization);
    }
}
