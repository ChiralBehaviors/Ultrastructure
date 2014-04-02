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
package com.chiralbehaviors.CoRE.object;

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * Creates a workspace object from data in the database
 * 
 * @author hparry
 * 
 */
public class WorkspaceLoader implements Workspace {

	private Product workspaceProduct;
	private Relationship workspaceOf;
	private Model model;

	public WorkspaceLoader(Product workspaceProduct, Relationship workspaceOf,
			Model model) {
		this.workspaceProduct = workspaceProduct;
		this.workspaceOf = workspaceOf;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getAllEntities(java.lang
	 * .Class)
	 */
	@Override
	public <T extends ExistentialRuleform<?, ?>> Collection<T> getAllEntities(
			Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllMetaProtocols()
	 */
	@Override
	public Collection<MetaProtocol> getAllMetaProtocols() {
		return model.getWorkspaceModel().getMetaProtocols(workspaceProduct,
				workspaceOf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllProtocols()
	 */
	@Override
	public Collection<Protocol> getAllProtocols() {
		return model.getWorkspaceModel().getProtocols(workspaceProduct,
				workspaceOf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getEntityByName(java.lang
	 * .Class, java.lang.String)
	 */
	@Override
	public <T extends ExistentialRuleform<?, ?>> T getEntityByName(
			Class<T> clazz, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getGraph(com.chiralbehaviors
	 * .CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
	 */
	@Override
	public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Graph<RuleForm, Network> getGraph(
			RuleForm parent, Relationship relationship) {
		//TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getMetaProtocolsFor(com.
	 * chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public List<MetaProtocol> getMetaProtocolsFor(Product service) {
		return model.getJobModel().getMetaProtocolsFor(service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getProtocolsFor(com.
	 * chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public List<Protocol> getProtocolsFor(Product service) {
		return model.getJobModel().getProtocolsFor(service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getRootedNetworksFor(com
	 * .chiralbehaviors.CoRE.ExistentialRuleform)
	 */
	@Override
	public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Graph<RuleForm, Network>> getRootedNetworksFor(
			RuleForm entity) {
		//TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getStatusCodeGraph(com.
	 * chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public Graph<StatusCode, StatusCodeSequencing> getStatusCodeGraph(
			Product service) {
		return model.getWorkspaceModel().getStatusCodeGraph(service);
	}

}
