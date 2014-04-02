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
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.coordinate.Coordinate;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.time.Interval;

/**
 * @author hparry
 * 
 */
public class WorkspaceSnapshot implements Workspace {

    private Map<String, Agency>                                   agenciesByName;
    private Map<String, Attribute>                                attributesByName;
    private Map<String, Coordinate>                               coordinatesByName;
    private Map<String, Interval>                                 intervalsByName;
    private Map<String, Location>                                 locationsByName;
    private Map<Product, List<MetaProtocol>>                      metaProtocolsByProduct;
    private Map<Object, Object>                                   networksByEntity;
    private Map<String, Product>                                  productsByName;
    private Map<Product, List<Protocol>>                          protocolsByProduct;

    /**
	 * @param agenciesByName
	 * @param attributesByName
	 * @param coordinatesByName
	 * @param intervalsByName
	 * @param locationsByName
	 * @param metaProtocolsByProduct
	 * @param networksByEntity
	 * @param productsByName
	 * @param protocolsByProduct
	 * @param relationshipsByName
	 * @param statusCodesByName
	 * @param statusCodesByProduct
	 * @param unitsByName
	 */
	public WorkspaceSnapshot(
			Map<String, Agency> agenciesByName,
			Map<String, Attribute> attributesByName,
			Map<String, Coordinate> coordinatesByName,
			Map<String, Interval> intervalsByName,
			Map<String, Location> locationsByName,
			Map<Product, List<MetaProtocol>> metaProtocolsByProduct,
			Map<Object, Object> networksByEntity,
			Map<String, Product> productsByName,
			Map<Product, List<Protocol>> protocolsByProduct,
			Map<String, Relationship> relationshipsByName,
			Map<String, StatusCode> statusCodesByName,
			Map<Product, Graph<StatusCode, StatusCodeSequencing>> statusCodesByProduct,
			Map<String, Unit> unitsByName) {
		super();
		this.agenciesByName = agenciesByName;
		this.attributesByName = attributesByName;
		this.coordinatesByName = coordinatesByName;
		this.intervalsByName = intervalsByName;
		this.locationsByName = locationsByName;
		this.metaProtocolsByProduct = metaProtocolsByProduct;
		this.networksByEntity = networksByEntity;
		this.productsByName = productsByName;
		this.protocolsByProduct = protocolsByProduct;
		this.relationshipsByName = relationshipsByName;
		this.statusCodesByName = statusCodesByName;
		this.statusCodesByProduct = statusCodesByProduct;
		this.unitsByName = unitsByName;
	}

	private Map<String, Relationship>                             relationshipsByName;
    private Map<String, StatusCode>                               statusCodesByName;
    private Map<Product, Graph<StatusCode, StatusCodeSequencing>> statusCodesByProduct;
    private Map<String, Unit>                                     unitsByName;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.workspace.Workspace#getAllEntities(java.lang
     * .Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform<?, ?>> Collection<T> getAllEntities(Class<T> clazz) {

        String rf = clazz.getSimpleName();
        switch (rf) {
        // On March 31, 2014 Hal and Hallie said this was ok because you can't
        // put variable expressions in case statements
            case "Agency":
                return (Collection<T>) agenciesByName.values();
            case "Attribute":
                return (Collection<T>) attributesByName.values();
            case "Coordinate":
                return (Collection<T>) coordinatesByName.values();
            case "Interval":
                return (Collection<T>) intervalsByName.values();
            case "Location":
                return (Collection<T>) locationsByName.values();
            case "Product":
                return (Collection<T>) productsByName.values();
            case "Relationship":
                return (Collection<T>) relationshipsByName.values();
            case "StatusCode":
                return (Collection<T>) statusCodesByName.values();
            case "Unit":
                return (Collection<T>) unitsByName.values();
            default:
                return null;
        }
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
    public <T extends ExistentialRuleform<?, ?>> T getEntityByName(Class<T> clazz,
                                                                   String name) {
        String rf = clazz.getSimpleName();
        switch (rf) {
        // On March 31, 2014 Hal and Hallie said this was ok because you can't
        // put variable expressions in case statements
            case "Agency":
                return (T) agenciesByName.get(name);
            case "Attribute":
                return (T) attributesByName.get(name);
            case "Coordinate":
                return (T) coordinatesByName.get(name);
            case "Interval":
                return (T) intervalsByName.get(name);
            case "Location":
                return (T) locationsByName.get(name);
            case "Product":
                return (T) productsByName.get(name);
            case "Relationship":
                return (T) relationshipsByName.get(name);
            case "StatusCode":
                return (T) statusCodesByName.get(name);
            case "Unit":
                return (T) unitsByName.get(name);
            default:
                return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.workspace.Workspace#getGraph(com.chiralbehaviors
     * .CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Graph<RuleForm, Network> getGraph(RuleForm parent,
                                                                                                                                                  Relationship relationship) {
        // TODO
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
     * @see
     * com.chiralbehaviors.CoRE.workspace.Workspace#getRootedNetworksFor(com
     * .chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Graph<RuleForm, Network>> getRootedNetworksFor(RuleForm entity) {
        return (List<Graph<RuleForm, Network>>) networksByEntity.get(entity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getStatusCodeGraph(com.
     * chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public Graph<StatusCode, StatusCodeSequencing> getStatusCodeGraph(Product service) {
        return statusCodesByProduct.get(service);
    }

}
