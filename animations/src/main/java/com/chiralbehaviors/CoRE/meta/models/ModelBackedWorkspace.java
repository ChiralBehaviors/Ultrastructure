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

import java.util.Collections;
import java.util.List;

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
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 *         A workspace backed by a database
 */
public class ModelBackedWorkspace extends DatabaseBackedWorkspace {

    public static String getAttributeColumnName(Ruleform ruleform) {
        return ruleform.getClass().getSimpleName().toLowerCase();
    }

    private final Model model;

    public ModelBackedWorkspace(Product definingProduct, Model model) {
        super(definingProduct, model.getEntityManager());
        this.model = model;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Attribute> getAttributeAuthorizations(RuleForm parent,
                                                                                                                                                           Relationship relationship) {
        return Collections.emptyList();
    }

    public <Value extends AttributeValue<RuleForm>, RuleForm extends ExistentialRuleform<RuleForm, ?>> List<Value> getAttributes(RuleForm ruleform) {
        CriteriaBuilder cb = model.getEntityManager().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<Value> query = (CriteriaQuery<Value>) cb.createQuery(ruleform.getAttributeValueClass());
        @SuppressWarnings("unchecked")
        Root<Value> attributeRoot = (Root<Value>) query.from(ruleform.getAttributeValueClass());
        Root<WorkspaceAuthorization> workspaceAuthRoot = query.from(WorkspaceAuthorization.class);
        query.select(attributeRoot).where(cb.and(cb.equal(attributeRoot.get(getAttributeColumnName(ruleform)),
                                                          ruleform),
                                                 cb.equal(workspaceAuthRoot.get(WorkspaceAuthorization.getWorkspaceAuthorizationColumnName(ruleform.getAttributeValueClass())),
                                                          attributeRoot),
                                                 cb.equal(workspaceAuthRoot.get(WorkspaceAuthorization_.definingProduct),
                                                          definingProduct)));
        TypedQuery<Value> q = model.getEntityManager().createQuery(query);
        return q.getResultList();
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(RuleForm parent,
                                                                                                                                           Relationship relationship) {
        CriteriaBuilder cb = model.getEntityManager().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<RuleForm> query = (CriteriaQuery<RuleForm>) cb.createQuery(parent.getClass());
        @SuppressWarnings("unchecked")
        Root<NetworkRuleform<RuleForm>> networkRoot = (Root<NetworkRuleform<RuleForm>>) query.from(parent.getNetworkClass());
        Root<WorkspaceAuthorization> workspaceAuthRoot = query.from(WorkspaceAuthorization.class);
        Path<RuleForm> childPath = networkRoot.get("child");
        Path<Product> definingProductPath = workspaceAuthRoot.get(WorkspaceAuthorization_.definingProduct);
        query.select(childPath).where(cb.and(cb.equal(definingProductPath,
                                                      definingProduct),
                                             cb.equal(workspaceAuthRoot.get(parent.getNetworkWorkspaceAuthAttribute()),
                                                      networkRoot),
                                             cb.equal(networkRoot.get("parent"),
                                                      parent),
                                             cb.equal(networkRoot.get("relationship"),
                                                      relationship),
                                             cb.equal(networkRoot.get("inference").get("id"),
                                                      "AAAAAAAAAAAAAAAAAAAAAA")));
        TypedQuery<RuleForm> q = model.getEntityManager().createQuery(query);
        return q.getResultList();
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getParents(RuleForm child,
                                                                                                                                          Relationship relationship) {
        CriteriaBuilder cb = model.getEntityManager().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<RuleForm> query = (CriteriaQuery<RuleForm>) cb.createQuery(child.getClass());
        @SuppressWarnings("unchecked")
        Root<NetworkRuleform<RuleForm>> networkRoot = (Root<NetworkRuleform<RuleForm>>) query.from(child.getNetworkClass());
        Root<WorkspaceAuthorization> workspaceAuthRoot = query.from(WorkspaceAuthorization.class);
        Path<RuleForm> parentPath = networkRoot.get("parent");
        Path<Product> definingProductPath = workspaceAuthRoot.get(WorkspaceAuthorization_.definingProduct);
        query.select(parentPath).where(cb.and(cb.equal(definingProductPath,
                                                       definingProduct),
                                              cb.equal(workspaceAuthRoot.get(child.getNetworkWorkspaceAuthAttribute()),
                                                       networkRoot),
                                              cb.equal(networkRoot.get("child"),
                                                       child),
                                              cb.equal(networkRoot.get("relationship"),
                                                       relationship),
                                              cb.equal(networkRoot.get("inference").get("id"),
                                                       "AAAAAAAAAAAAAAAAAAAAAA")));
        TypedQuery<RuleForm> q = model.getEntityManager().createQuery(query);
        return q.getResultList();
    }
}
