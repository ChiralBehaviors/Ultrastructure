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

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * The meta model for the CoRE
 *
 * @author hhildebrand
 *
 */
public interface Model {

    /**
     * Deregister the workspace with the model
     *
     * @param workspace
     */
    void deregister(Workspace workspace);

    /**
     * Find the ruleform instances that match the supplied attribute
     *
     * @param attributeValue
     *            - the attribute value to match
     * @return the collection of ruleform instances that match the attribute
     */
    <AttributeType extends AttributeValue<RuleForm>, RuleForm extends Ruleform> List<RuleForm> find(AttributeType attributeValue);

    /**
     * Find an instance using the id
     *
     * @param id
     * @return the instance corresponding to the supplied id, or null if the
     *         instance does not exist
     */
    <RuleForm extends Ruleform> RuleForm find(Long id, Class<RuleForm> ruleform);

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
     * Find the instances of the ruleform that are flagged for research
     *
     * @param name
     * @return the instances that have non null research values
     */
    <RuleForm extends Ruleform> List<RuleForm> findFlagged(Class<RuleForm> ruleform);

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
     * Refresh the workspaces - i.e. retarget to the em
     */
    void refreshWorkspaces();

    /**
     * Register the workspace with the model to be refreshed on commmit
     *
     * @param workspace
     */
    void register(Workspace workspace);

}
