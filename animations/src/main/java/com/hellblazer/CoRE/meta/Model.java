/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta;

import java.util.List;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.kernel.Kernel;

/**
 * The meta model for the CoRE
 * 
 * @author hhildebrand
 * 
 */
public interface Model {

	/**
	 * Find the ruleform instances that match the supplied attribute
	 * 
	 * @param attributeValue
	 *            - the attribute value to match
	 * @return the collection of ruleform instances that match the attribute
	 */
	<AttributeType extends AttributeValue<RuleForm>, RuleForm extends Ruleform> List<RuleForm> find(
			AttributeType attributeValue);

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
	<RuleForm extends ExistentialRuleform> RuleForm find(String name,
			Class<RuleForm> ruleform);

	/**
	 * Find the instances of the ruleform that are flagged for research
	 * 
	 * @param name
	 * @return the instances that have non null research values
	 */
	<RuleForm extends Ruleform> List<RuleForm> findFlagged(
			Class<RuleForm> ruleform);

	/**
	 * Find all the instances of the RuleForm that have been updated by the
	 * agency
	 * 
	 * @param updatedBy
	 * @param ruleform
	 * @return
	 */
	<RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(
			Agency updatedBy, Class<Ruleform> ruleform);

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
	 * @return the Product model
	 */
	ProductModel getProductModel();

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
	 * @return the Agency model
	 */
	AgencyModel getAgencyModel();

}
