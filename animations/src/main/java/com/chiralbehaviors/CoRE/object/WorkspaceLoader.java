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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * Creates a workspace object from data in the database
 * 
 * @author hparry
 * 
 */
public class WorkspaceLoader {

	private Product workspaceProduct;
	private Relationship workspaceOf;
	private Model model;
	private WorkspaceSnapshot workspace;

	private Map<Product, List<MetaProtocol>> metaProtocolsByProduct;
	private Map<String, Collection<NetworkRuleform<?>>> networksByEntity;
	private Map<Product, List<Protocol>> protocolsByProduct;
	private Map<Product, Graph<StatusCode, StatusCodeSequencing>> statusCodesByProduct;
	private Map<String, Map<String, ExistentialRuleform<?, ?>>> existentialRuleforms;
	private Map<Product, List<StatusCodeSequencing>> statusCodeSequencingByProduct;

	public WorkspaceLoader(Product workspaceProduct, Relationship workspaceOf,
			Model model) {
		this.workspaceProduct = workspaceProduct;
		this.workspaceOf = workspaceOf;
		this.model = model;
		load();
	}

	/**
	 * Reloads the workspace data from the database. Useful for refreshing the
	 * entity map if you're changed something.
	 */
	public void load() {

		existentialRuleforms = new HashMap<String, Map<String, ExistentialRuleform<?, ?>>>();

		List<Product> products = model.getProductModel().getChildren(
				workspaceProduct, workspaceOf);
		Map<String, ExistentialRuleform<?, ?>> productMap = new HashMap<String, ExistentialRuleform<?, ?>>();
		for (Product p : products) {
			if (!productMap.containsKey(p.getName())) {
				productMap.put(p.getName(), p);
			}
		}

		existentialRuleforms.put(Product.class.getSimpleName(), productMap);
		List<AccessAuthorization<?, ?>> auths = new LinkedList<AccessAuthorization<?, ?>>();
		auths.addAll(model.getProductModel().getAgencyAccessAuths(
				workspaceProduct, workspaceOf));
		existentialRuleforms.put(Agency.class.getSimpleName(), toMap(auths));

		auths = new LinkedList<AccessAuthorization<?, ?>>();
		auths.addAll(model.getProductModel().getAttributeAccessAuths(
				workspaceProduct, workspaceOf));
		existentialRuleforms.put(Attribute.class.getSimpleName(), toMap(auths));

		auths = new LinkedList<AccessAuthorization<?, ?>>();
		auths.addAll(model.getProductModel().getLocationAccessAuths(
				workspaceProduct, workspaceOf));
		existentialRuleforms.put(Location.class.getSimpleName(), toMap(auths));

		auths = new LinkedList<AccessAuthorization<?, ?>>();
		auths.addAll(model.getProductModel().getRelationshipAccessAuths(
				workspaceProduct, workspaceOf));
		existentialRuleforms.put(Relationship.class.getSimpleName(),
				toMap(auths));

		auths = new LinkedList<AccessAuthorization<?, ?>>();
		auths.addAll(model.getProductModel().getStatusCodeAccessAuths(
				workspaceProduct, workspaceOf));
		existentialRuleforms
				.put(StatusCode.class.getSimpleName(), toMap(auths));

		workspace = new WorkspaceSnapshot(metaProtocolsByProduct, networksByEntity,
				protocolsByProduct, statusCodesByProduct, existentialRuleforms,
				statusCodeSequencingByProduct);

	}

	/**
	 * @param agencyAuths
	 */
	private Map<String, ExistentialRuleform<?, ?>> toMap(
			List<AccessAuthorization<?, ?>> auths) {
		Map<String, ExistentialRuleform<?, ?>> map = new HashMap<String, ExistentialRuleform<?, ?>>();
		for (AccessAuthorization<?, ?> auth : auths) {
			if (!map.containsKey(auth.getChild().getName())) {
				map.put(auth.getChild().getName(), auth.getChild());
			}
		}

		return map;

	}
	
	public WorkspaceSnapshot getWorkspace() {
		return workspace;
	}

}
