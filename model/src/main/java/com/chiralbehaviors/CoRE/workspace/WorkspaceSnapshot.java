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

import java.util.List;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * A util class useful for de/serializing Workspace objects.
 * 
 * @author hparry
 * 
 */
public class WorkspaceSnapshot implements Workspace {

    private Product                    workspaceProduct;
    private Relationship               workspaceRelationship;

    private List<Agency>               agencies;
    private List<Attribute>            attributes;
    private List<Location>             locations;
    private List<Product>              products;
    private List<Relationship>         relationships;
    private List<ProductNetwork>       productNetworks;
    private List<AgencyNetwork>        agencyNetworks;
    private List<AttributeNetwork>     attributeNetworks;
    private List<LocationNetwork>      locationNetworks;
    private List<RelationshipNetwork>  relationshipNetworks;
    private List<StatusCode>           statusCodes;
    private List<StatusCodeNetwork>    statusCodeNetworks;
    private List<StatusCodeSequencing> statusCodeSequencings;
    private List<Unit>                 units;
    private List<UnitNetwork>          unitNetworks;

    public WorkspaceSnapshot() {

    }

    /**
     * @return the agencies
     */
    @Override
    public List<Agency> getAgencies() {
        return agencies;
    }

    /**
     * @return the agencyNetworks
     */
    @Override
    public List<AgencyNetwork> getAgencyNetworks() {
        return agencyNetworks;
    }

    /**
     * @return the attributeNetworks
     */
    @Override
    public List<AttributeNetwork> getAttributeNetworks() {
        return attributeNetworks;
    }

    /**
     * @return the attributes
     */
    @Override
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @return the locationNetworks
     */
    @Override
    public List<LocationNetwork> getLocationNetworks() {
        return locationNetworks;
    }

    /**
     * @return the locations
     */
    @Override
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * @return the productNetworks
     */
    @Override
    public List<ProductNetwork> getProductNetworks() {
        return productNetworks;
    }

    /**
     * @return the products
     */
    @Override
    public List<Product> getProducts() {
        return products;
    }

    /**
     * @return the relationshipNetworks
     */
    @Override
    public List<RelationshipNetwork> getRelationshipNetworks() {
        return relationshipNetworks;
    }

    /**
     * @return the relationships
     */
    @Override
    public List<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * @return the statusCodeNetworks
     */
    @Override
    public List<StatusCodeNetwork> getStatusCodeNetworks() {
        return statusCodeNetworks;
    }

    /**
     * @return the statusCodes
     */
    @Override
    public List<StatusCode> getStatusCodes() {
        return statusCodes;
    }

    /**
     * @return the statusCodeSequencings
     */
    @Override
    public List<StatusCodeSequencing> getStatusCodeSequencings() {
        return statusCodeSequencings;
    }

    /**
     * @return the unitNetworks
     */
    @Override
    public List<UnitNetwork> getUnitNetworks() {
        return unitNetworks;
    }

    /**
     * @return the units
     */
    @Override
    public List<Unit> getUnits() {
        return units;
    }

    /**
     * @return the workspaceProduct
     */
    @Override
    public Product getWorkspaceProduct() {
        return workspaceProduct;
    }

    /**
     * @return the workspaceRelationship
     */
    @Override
    public Relationship getWorkspaceRelationship() {
        return workspaceRelationship;
    }

    /**
     * @param agencies
     *            the agencies to set
     */
    @Override
    public void setAgencies(List<Agency> agencies) {
        this.agencies = agencies;
    }

    /**
     * @param agencyNetworks
     *            the agencyNetworks to set
     */
    @Override
    public void setAgencyNetworks(List<AgencyNetwork> agencyNetworks) {
        this.agencyNetworks = agencyNetworks;
    }

    /**
     * @param attributeNetworks
     *            the attributeNetworks to set
     */
    @Override
    public void setAttributeNetworks(List<AttributeNetwork> attributeNetworks) {
        this.attributeNetworks = attributeNetworks;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    @Override
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param locationNetworks
     *            the locationNetworks to set
     */
    @Override
    public void setLocationNetworks(List<LocationNetwork> locationNetworks) {
        this.locationNetworks = locationNetworks;
    }

    /**
     * @param locations
     *            the locations to set
     */
    @Override
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    /**
     * @param productNetworks
     *            the productNetworks to set
     */
    @Override
    public void setProductNetworks(List<ProductNetwork> productNetworks) {
        this.productNetworks = productNetworks;
    }

    /**
     * @param products
     *            the products to set
     */
    @Override
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * @param relationshipNetworks
     *            the relationshipNetworks to set
     */
    @Override
    public void setRelationshipNetworks(List<RelationshipNetwork> relationshipNetworks) {
        this.relationshipNetworks = relationshipNetworks;
    }

    /**
     * @param relationships
     *            the relationships to set
     */
    @Override
    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    /**
     * @param statusCodeNetworks
     *            the statusCodeNetworks to set
     */
    @Override
    public void setStatusCodeNetworks(List<StatusCodeNetwork> statusCodeNetworks) {
        this.statusCodeNetworks = statusCodeNetworks;
    }

    /**
     * @param statusCodes
     *            the statusCodes to set
     */
    @Override
    public void setStatusCodes(List<StatusCode> statusCodes) {
        this.statusCodes = statusCodes;
    }

    /**
     * @param statusCodeSequencings
     *            the statusCodeSequencings to set
     */
    @Override
    public void setStatusCodeSequencings(List<StatusCodeSequencing> statusCodeSequencings) {
        this.statusCodeSequencings = statusCodeSequencings;
    }

    /**
     * @param unitNetworks
     *            the unitNetworks to set
     */
    @Override
    public void setUnitNetworks(List<UnitNetwork> unitNetworks) {
        this.unitNetworks = unitNetworks;
    }

    /**
     * @param units
     *            the units to set
     */
    @Override
    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    /**
     * @param workspaceProduct
     *            the workspaceProduct to set
     */
    @Override
    public void setWorkspaceProduct(Product workspaceProduct) {
        this.workspaceProduct = workspaceProduct;
    }

    /**
     * @param workspaceRelationship
     *            the workspaceRelationship to set
     */
    @Override
    public void setWorkspaceRelationship(Relationship workspaceRelationship) {
        this.workspaceRelationship = workspaceRelationship;
    }

}
