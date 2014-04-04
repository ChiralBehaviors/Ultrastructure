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
package com.chiralbehaviors.CoRE.workspace;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.meta.graph.util.GraphUtil;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * A util class useful for deserializing Workspace objects clientside. Comes with handy 
 * convenience methods. Read only. 
 * @author hparry
 * 
 */
public class WorkspaceSnapshot implements Workspace {

	private Map<Product, List<MetaProtocol>> metaProtocolsByProduct;
	private Map<String, Collection<NetworkRuleform<?>>> networksByEntity;
	private Map<Product, List<Protocol>> protocolsByProduct;
	private Map<Product, Graph<StatusCode, StatusCodeSequencing>> statusCodesByProduct;
	private Map<String, Map<String, ExistentialRuleform<?, ?>>> existentialRuleforms;
	private Map<Product, List<StatusCodeSequencing>> statusCodeSequencingByProduct;
	

	/**
	 * @param metaProtocolsByProduct
	 * @param networksByEntity
	 * @param protocolsByProduct
	 * @param statusCodesByProduct
	 * @param existentialRuleforms
	 */
	public WorkspaceSnapshot(
			Map<Product, List<MetaProtocol>> metaProtocolsByProduct,
			Map<String, Collection<NetworkRuleform<?>>> networksByEntity,
			Map<Product, List<Protocol>> protocolsByProduct,
			Map<Product, Graph<StatusCode, StatusCodeSequencing>> statusCodesByProduct,
			Map<String, Map<String, ExistentialRuleform<?, ?>>> existentialRuleforms,
			Map<Product, List<StatusCodeSequencing>> statusCodeSequencingByProduct) {
		super();
		this.metaProtocolsByProduct = metaProtocolsByProduct;
		this.networksByEntity = networksByEntity;
		this.protocolsByProduct = protocolsByProduct;
		this.statusCodesByProduct = statusCodesByProduct;
		this.existentialRuleforms = existentialRuleforms;
		this.statusCodeSequencingByProduct = statusCodeSequencingByProduct;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getAllEntities(java.lang
	 * .Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExistentialRuleform<?, ?>> Collection<T> getAllEntitiesOfType(
			Class<T> clazz) {

		String rf = clazz.getSimpleName();
		return (Collection<T>) existentialRuleforms.get(rf).values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllMetaProtocols()
	 */
	@Override
	public Collection<MetaProtocol> getAllMetaProtocols() {
		List<MetaProtocol> metaprotocols = new LinkedList<MetaProtocol>();
		for (List<MetaProtocol> ps : metaProtocolsByProduct.values()) {
			metaprotocols.addAll(ps);
		}

		return metaprotocols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllProtocols()
	 */
	@Override
	public Collection<Protocol> getAllProtocols() {
		List<Protocol> protocols = new LinkedList<Protocol>();
		for (List<Protocol> ps : protocolsByProduct.values()) {
			protocols.addAll(ps);
		}

		return protocols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getEntityByName(java.lang
	 * .Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExistentialRuleform<?, ?>> T getEntityByName(
			Class<T> clazz, String name) {
		String rf = clazz.getSimpleName();
		return (T) existentialRuleforms.get(rf).get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.workspace.Workspace#getGraph(com.chiralbehaviors
	 * .CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Graph<RuleForm, Network> getGraph(
			RuleForm parent, Relationship relationship) {
		List<NetworkRuleform<?>> edges = new LinkedList<NetworkRuleform<?>>();
		
		for (NetworkRuleform<?> e : networksByEntity.get(parent.getClass().getSimpleName())) {
			if (e.getParent().equals(parent) && e.getRelationship().equals(relationship)) {
				edges.add(e);
			}
		}
		return (Graph<RuleForm, Network>) GraphUtil.graphFromNetworks(edges);
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
		return metaProtocolsByProduct.get(service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getProtocolsFor(com.
	 * chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public List<Protocol> getProtocolsFor(Product service) {
		return protocolsByProduct.get(service);
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
		return statusCodesByProduct.get(service);
	}

	@Override
	public Map<String, Map<String, ExistentialRuleform<?, ?>>> getAllExistentialRuleforms() {
		return existentialRuleforms;
	}

	/* (non-Javadoc)
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllNetworks()
	 */
	@Override
	public Map<String, Collection<NetworkRuleform<?>>> getAllNetworks() {
		return networksByEntity;
	}

	/* (non-Javadoc)
	 * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAllStatusCodeSequencings()
	 */
	@Override
	public Collection<StatusCodeSequencing> getAllStatusCodeSequencings() {
		List<StatusCodeSequencing> sequences = new LinkedList<StatusCodeSequencing>();
		
		for (List<StatusCodeSequencing> list : statusCodeSequencingByProduct.values()) {
			sequences.addAll(list);
		}
		
		return sequences;
	}

}
