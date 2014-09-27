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

package com.chiralbehaviors.workspace.swing.model.impl;

import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.workspace.swing.model.WorkspaceEditor;

/**
 * @author hhildebrand
 *
 */
public class DatabaseEditor implements WorkspaceEditor {
    @SuppressWarnings("unused")
    private final Product definingProduct;
    private final Model   model;

    public DatabaseEditor(Model model, Product definingProduct) {
        this.model = model;
        this.definingProduct = definingProduct;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.workspace.swing.model.WorkspaceEditor#getChildren(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(RuleForm parent,
                                                                                                                                           Relationship relationship) {
        return getNetworkedModel(parent.getClass()).getChildren(parent,
                                                                relationship);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.workspace.swing.model.WorkspaceEditor#getParents(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getParents(RuleForm child,
                                                                                                                                          Relationship relationship) {
        return getNetworkedModel(child.getClass()).getChildren(child,
                                                               relationship.getInverse());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.workspace.swing.model.WorkspaceEditor#getAuthorizedAttributes(com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Attribute> getAttributeAuthorizations(RuleForm parent,
                                                                                                                                                           Relationship relationship) {

        return getNetworkedModel(parent.getClass()).getAttributeAuthorizations(new Aspect<RuleForm>(
                                                                                                    relationship,
                                                                                                    parent));
    }

    @SuppressWarnings("unchecked")
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>> NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> getNetworkedModel(Class<RuleForm> entityClass) {
        if (entityClass.equals(Agency.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getAgencyModel();
        }
        if (entityClass.equals(Product.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getProductModel();
        }
        if (entityClass.equals(Attribute.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getAttributeModel();
        }
        if (entityClass.equals(Interval.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getIntervalModel();
        }
        if (entityClass.equals(Location.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getLocationModel();
        }
        if (entityClass.equals(Relationship.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getRelationshipModel();
        }
        if (entityClass.equals(StatusCode.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getStatusCodeModel();
        }
        if (entityClass.equals(Unit.class)) {
            return (NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType>) model.getUnitModel();
        }
        throw new IllegalArgumentException(
                                           String.format("Don't have a model for a %s",
                                                         entityClass.getSimpleName()));
    }
}
