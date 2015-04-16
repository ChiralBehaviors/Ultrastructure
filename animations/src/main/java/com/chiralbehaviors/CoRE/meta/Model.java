/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;

/**
 * The meta model for the CoRE
 *
 * @author hhildebrand
 *
 */
public interface Model {

    /**
     * Create a new instance of the phantasm's existential ruleform type using
     * the model
     * 
     * @param phantasm
     * @param ruleform
     * @return
     * @throws InstantiationException
     */
    public <T extends ExistentialRuleform<T, ?>, X extends PhantasmBase<T>> X construct(Class<? extends X> phantasm,
                                                                                        String name,
                                                                                        String description,
                                                                                        Agency updatedBy)
                                                                                                         throws InstantiationException;

    /**
     * Wrap the ruleform with an instance of a phantasm using the model
     * 
     * @param phantasm
     * @param ruleform
     * @return
     */
    public <T extends ExistentialRuleform<T, ?>, X extends PhantasmBase<T>> X wrap(Class<X> phantasm,
                                                                                   T ruleform);

    /**
     * Find the ruleform instances that match the supplied attribute
     *
     * @param attributeValue
     *            - the attribute value to match
     * @return the collection of ruleform instances that match the attribute
     */
    <AttributeType extends AttributeValue<RuleForm>, RuleForm extends Ruleform> List<RuleForm> find(AttributeType attributeValue);

    /**
     * Find an instance of the ExistentialRuleform using the name
     *
     * @param name
     * @return the instance that has the supplied name, or null if the instance
     *         does not exist
     */
    <RuleForm extends ExistentialRuleform<?, ?>> RuleForm find(String name,
                                                               Class<RuleForm> ruleform);

    /**
     * Find an instance using the id
     *
     * @param id
     * @return the instance corresponding to the supplied id, or null if the
     *         instance does not exist
     */
    <RuleForm extends Ruleform> RuleForm find(UUID id, Class<RuleForm> ruleform);

    /**
     * Find all the instances of the RuleForm that have been updated by the
     * agency
     *
     * @param updatedBy
     * @param ruleform
     * @return
     */
    <RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(Agency updatedBy,
                                                             Class<Ruleform> ruleform);

    /**
     * @return the Agency model
     */
    AgencyModel getAgencyModel();

    /**
     * @return the Attribute model
     */
    AttributeModel getAttributeModel();

    /**
     * Answer the product manager used for this model instance
     *
     * @return
     */
    EntityManager getEntityManager();

    /**
     * @return the Interval model
     */
    IntervalModel getIntervalModel();

    /**
     * @return the Job Model
     */
    JobModel getJobModel();

    /**
     * Answer the access model for the kernel rules
     *
     * @return the kernel definition
     */
    Kernel getKernel();

    /**
     * @return the Location model
     */
    LocationModel getLocationModel();

    /**
     * Answer the appropriate networked model for the existential ruleform
     * 
     * @param ruleform
     * @return
     */
    <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> NetworkedModel<RuleForm, Network, ?, ?> getNetworkedModel(ExistentialRuleform<RuleForm, Network> ruleform);

    /**
     * @return the Product model
     */
    ProductModel getProductModel();

    /**
     * @return the Relationship model
     */
    RelationshipModel getRelationshipModel();

    /**
     * @return the StatusCode model
     */
    StatusCodeModel getStatusCodeModel();

    /**
     * @return the UnitCode model
     */
    UnitModel getUnitModel();

    /**
     * @return the UnitCode model
     */
    WorkspaceModel getWorkspaceModel();

    /**
     * Infer networks for the existential ruleform
     * 
     * @param ruleform
     */
    void inferNetworks(ExistentialRuleform<?, ?> ruleform);

}
