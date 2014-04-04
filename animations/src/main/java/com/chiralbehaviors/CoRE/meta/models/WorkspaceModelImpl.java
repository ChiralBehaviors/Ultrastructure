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
package com.chiralbehaviors.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.graph.Edge;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.meta.graph.Node;
import com.chiralbehaviors.CoRE.meta.graph.impl.EdgeImpl;
import com.chiralbehaviors.CoRE.meta.graph.impl.GraphImpl;
import com.chiralbehaviors.CoRE.meta.graph.impl.NodeImpl;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductUnitAccessAuthorization;

/**
 * @author hhildebrand
 * 
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final Model model;

    public WorkspaceModelImpl(Model model) {
        this.model = model;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getAgencies(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Agency> getAgencies(Product workspace,
                                          Relationship relationship) {
        List<Agency> agencies = new ArrayList<Agency>();
        for (ProductAgencyAccessAuthorization auth : model.getProductModel().getAgencyAccessAuths(workspace,
                                                                                                  relationship)) {
            for (Agency agency : model.getAgencyModel().getChildren(auth.getChild(),
                                                                    auth.getChildTransitiveRelationship())) {
                agencies.add(agency);
            }
        }
        return agencies;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getAttributes(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Attribute> getAttributes(Product workspace,
                                               Relationship relationship) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (ProductAttributeAccessAuthorization auth : model.getProductModel().getAttributeAccessAuths(workspace,
                                                                                                        relationship)) {
            for (Attribute attribute : model.getAttributeModel().getChildren(auth.getChild(),
                                                                             auth.getChildTransitiveRelationship())) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getJobs(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Job> getJobs(Product workspace, Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getLocationss(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Location> getLocations(Product workspace,
                                             Relationship relationship) {
        List<Location> locations = new ArrayList<Location>();
        for (ProductLocationAccessAuthorization auth : model.getProductModel().getLocationAccessAuths(workspace,
                                                                                                      relationship)) {
            for (Location location : model.getLocationModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                locations.add(location);
            }
        }
        return locations;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getMetaProtocols(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<MetaProtocol> getMetaProtocols(Product workspace,
                                                     Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getProductChildSequencingAuthorizations(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductChildSequencingAuthorization> getProductChildSequencingAuthorizations(Product workspace,
                                                                                                   Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getProductParentSequencingAuthorizations(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductParentSequencingAuthorization> getProductParentSequencingAuthorizations(Product workspace,
                                                                                                     Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getProducts(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Product> getProducts(Product workspace,
                                           Relationship relationship) {
        return model.getProductModel().getChildren(workspace, relationship);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getProductSiblingSequencingAuthorizations(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductSiblingSequencingAuthorization> getProductSiblingSequencingAuthorizations(Product workspace,
                                                                                                       Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getProtocols(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Protocol> getProtocols(Product workspace,
                                             Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getRelationships(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Relationship> getRelationships(Product workspace,
                                                     Relationship relationship) {
        List<Relationship> relationships = new ArrayList<Relationship>();
        for (ProductRelationshipAccessAuthorization auth : model.getProductModel().getRelationshipAccessAuths(workspace,
                                                                                                              relationship)) {
            for (Relationship r : model.getRelationshipModel().getChildren(auth.getChild(),
                                                                           auth.getChildTransitiveRelationship())) {
                relationships.add(r);
            }
        }
        return relationships;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getStatusCodes(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<StatusCode> getStatusCodes(Product workspace,
                                                 Relationship relationship) {
        List<StatusCode> statusCodes = new ArrayList<StatusCode>();
        for (ProductStatusCodeAccessAuthorization auth : model.getProductModel().getStatusCodeAccessAuths(workspace,
                                                                                                          relationship)) {
            for (StatusCode code : model.getStatusCodeModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                statusCodes.add(code);
            }
        }
        return statusCodes;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#findStatusCodeSequences(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<StatusCodeSequencing> getStatusCodeSequences(Product workspace,
                                                                   Relationship relationship) {
        Set<StatusCodeSequencing> sequences = new HashSet<StatusCodeSequencing>();
        for (ProductStatusCodeAccessAuthorization auth : model.getProductModel().getStatusCodeAccessAuths(workspace,
                                                                                                          relationship)) {
            for (StatusCode code : model.getStatusCodeModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                sequences.addAll(model.getStatusCodeModel().getStatusCodeSequencingChild(code));
                sequences.addAll(model.getStatusCodeModel().getStatusCodeSequencingParent(code));
            }
        }
        return sequences;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getUnits(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public Collection<Unit> getUnits(Product workspace,
                                     Relationship relationship) {
        List<Unit> units = new ArrayList<Unit>();
        for (ProductUnitAccessAuthorization auth : model.getProductModel().getUnitAccessAuths(workspace,
                                                                                              relationship)) {
            for (Unit unit : model.getUnitModel().getChildren(auth.getChild(),
                                                              auth.getChildTransitiveRelationship())) {
                units.add(unit);
            }
        }
        return units;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Graph<StatusCode, StatusCodeSequencing> getStatusCodeGraph(Product product) {
        Map<StatusCode, Node<StatusCode>> nodes = new HashMap<StatusCode, Node<StatusCode>>();
        List<Edge<StatusCodeSequencing>> edges = new ArrayList<Edge<StatusCodeSequencing>>();
        for (StatusCode currentCode : model.getJobModel().getStatusCodesFor(product)) {
            Node<StatusCode> parent = new NodeImpl<StatusCode>(currentCode);
            nodes.put(currentCode, parent);
            for (StatusCodeSequencing sequence : model.getStatusCodeModel().getStatusCodeSequencingParent(product,
                                                                                                          currentCode)) {
                StatusCode childCode = sequence.getChildCode();
                Node<StatusCode> child = nodes.get(childCode);
                if (child == null) {
                    child = new NodeImpl<StatusCode>(childCode);
                    nodes.put(childCode, child);
                }
                Edge<StatusCodeSequencing> edge = new EdgeImpl<StatusCodeSequencing>(
                                                                                     parent,
                                                                                     sequence,
                                                                                     child);
                edges.add(edge);
            }
        }
        List<Node<StatusCode>> nodeList = new ArrayList<Node<StatusCode>>();
        nodeList.addAll(nodes.values());
        return new GraphImpl<StatusCode, StatusCodeSequencing>(nodeList, edges);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Graph<Product, ?> getStatusCodeSequencingAuthorizationGraph(Product product) {
        Map<Product, Node<Product>> nodes = new HashMap<Product, Node<Product>>();
        List<Edge<?>> edges = new ArrayList<Edge<?>>();
        Node<Product> current = new NodeImpl<Product>(product);
        traverseStatusCodeSequencing(current, nodes, edges);
        List<Node<?>> nodeList = new ArrayList<Node<?>>();
        nodeList.addAll(nodes.values());
        return new GraphImpl(nodeList, edges);
    }

    /**
     * @param current
     * @param nodes
     * @param edges
     */
    private void traverseStatusCodeSequencing(Node<Product> current,
                                              Map<Product, Node<Product>> nodes,
                                              List<Edge<?>> edges) {
        if (nodes.containsKey(current.getNode())) {
            return;
        }
        for (ProductParentSequencingAuthorization auth : model.getJobModel().getParentActions(current.getNode())) {
            Node<Product> p = nodes.get(auth.getService());
            if (p == null) {
                p = new NodeImpl<Product>(auth.getService());
                traverseStatusCodeSequencing(p, nodes, edges);
            }
            edges.add(new EdgeImpl<ProductParentSequencingAuthorization>(p,
                                                                         auth,
                                                                         current));
        }
        for (ProductSiblingSequencingAuthorization auth : model.getJobModel().getSiblingActions(current.getNode())) {
            Node<Product> p = nodes.get(auth.getParent());
            if (p == null) {
                p = new NodeImpl<Product>(auth.getParent());
            }
            edges.add(new EdgeImpl<ProductSiblingSequencingAuthorization>(
                                                                          current,
                                                                          auth,
                                                                          p));
        }
        for (ProductChildSequencingAuthorization auth : model.getJobModel().getChildActions(current.getNode())) {
            Node<Product> p = nodes.get(auth.getParent());
            if (p == null) {
                p = new NodeImpl<Product>(auth.getParent());
            }
            edges.add(new EdgeImpl<ProductChildSequencingAuthorization>(
                                                                        current,
                                                                        auth, p));
        }
    }
}
