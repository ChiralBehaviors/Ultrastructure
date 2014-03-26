/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.hellblazer.CoRE.access.resource.ruleform.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.access.resource.ruleform.ExistentialRuleformResource;
import com.hellblazer.CoRE.meta.NetworkedModel;

/**
 * @author hparry
 * 
 */
public abstract class AbstractRuleformResource<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>>
		implements
		ExistentialRuleformResource<RuleForm, Network, AttributeAuthorization, AttributeType> {

	protected final NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> model;
	protected final EntityManager em;

	/**
	 * @param emf
	 */
	public AbstractRuleformResource(
			EntityManager em,
			NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> model) {
		this.em = em;
		this.model = model;
	}

	@Override
	public List<RuleForm> getAll() throws ClassNotFoundException {
		return model.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hellblazer.CoRE.access.resource.ruleform.ExistentialRuleformResource
	 * #getNetworks(long)
	 */
	@Override
	public Collection<Network> getNetworks(long id) {
		return model.getImmediateNetworkEdges(model.find(id));
	}

	@Override
	public RuleForm getResource(long id) throws ClassNotFoundException {
		return model.find(id);
	}

	@Override
	public final long insert(RuleForm rf) {
		em.getTransaction().begin();
		Map<Ruleform, Ruleform> map = new HashMap<Ruleform, Ruleform>();
		rf.manageEntity(em, map);
		em.getTransaction().commit();
		em.refresh(rf);
		return rf.getId();
	}

}