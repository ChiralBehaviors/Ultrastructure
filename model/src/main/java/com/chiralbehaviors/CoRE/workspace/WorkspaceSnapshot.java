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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocation;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProduct;
import com.chiralbehaviors.CoRE.agency.access.AgencyLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.agency.access.AgencyProductAccessAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitValue;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.location.access.LocationAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipAttribute;
import com.chiralbehaviors.CoRE.network.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocationAttribute;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAttribute;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductUnitAccessAuthorization;

/**
 * A util class useful for de/serializing Workspace objects.
 * 
 * @author hparry
 * 
 */
public class WorkspaceSnapshot implements Workspace {
    private static Logger                                log                                     = LoggerFactory.getLogger(WorkspaceSnapshot.class);

    private Product                                      workspaceProduct;
    private Relationship                                 workspaceRelationship;
    private Map<String, Agency>                          agencies                                = new HashMap<>();
    private List<AgencyAttribute>                        agencyAttributes                        = new ArrayList<>();
    private List<AgencyLocation>                         agencyLocations                         = new ArrayList<>();
    private List<AgencyLocationAccessAuthorization>      agencyLocationAccessAuthorizations      = new ArrayList<>();
    private List<AgencyProduct>                          agencyProducts                          = new ArrayList<>();
    private List<AgencyProductAccessAuthorization>       agencyProductAccessAuthorizations       = new ArrayList<>();
    private List<AgencyAttributeAuthorization>           agencyAttributeAuthorizations           = new ArrayList<>();
    private List<AgencyNetwork>                          agencyNetworks                          = new ArrayList<>();
    private List<AgencyNetworkAuthorization>             agencyNetworkAuthorizations             = new ArrayList<>();
    private Map<String, Attribute>                       attributes                              = new HashMap<>();
    private Map<String, Unit>                            units                                   = new HashMap<>();
    private List<UnitNetwork>                            unitNetworks                            = new ArrayList<>();
    private List<UnitAttribute>                          unitAttributes                          = new ArrayList<>();
    private List<UnitAttributeAuthorization>             unitAttributeAuthorizations             = new ArrayList<>();
    private List<UnitValue>                              unitValues                              = new ArrayList<>();
    private List<AttributeMetaAttribute>                 attributeMetaAttributes                 = new ArrayList<>();
    private List<AttributeMetaAttributeAuthorization>    attributeMetaAttributeAuthorizations    = new ArrayList<>();
    private List<AttributeNetwork>                       attributeNetworks                       = new ArrayList<>();
    private Map<String, StatusCode>                      statusCodes                             = new HashMap<>();
    private List<StatusCodeNetwork>                      statusCodeNetworks                      = new ArrayList<>();
    private List<StatusCodeSequencing>                   statusCodeSequencings                   = new ArrayList<>();
    private List<StatusCodeAttribute>                    statusCodeAttributes                    = new ArrayList<>();
    private List<StatusCodeAttributeAuthorization>       statusCodeAttributeAuthorizations       = new ArrayList<>();
    private List<Job>                                    jobs                                    = new ArrayList<>();
    private List<JobChronology>                          jobChronologies                         = new ArrayList<>();
    private List<MetaProtocol>                           metaProtocols                           = new ArrayList<>();
    private List<ProductChildSequencingAuthorization>    productChildSequencingAuthorizations    = new ArrayList<>();
    private List<ProductParentSequencingAuthorization>   productParentSequencingAuthorizations   = new ArrayList<>();
    private List<ProductSiblingSequencingAuthorization>  productSiblingSequencingAuthorizations  = new ArrayList<>();
    private List<Protocol>                               protocols                               = new ArrayList<>();
    private Map<String, Location>                        locations                               = new HashMap<>();
    private List<LocationNetwork>                        locationNetworks                        = new ArrayList<>();
    private List<LocationAgencyAccessAuthorization>      locationAgencyAccessAuthorizations      = new ArrayList<>();
    private List<LocationProductAccessAuthorization>     locationProductAccessAuthorizations     = new ArrayList<>();
    private List<LocationAttribute>                      locationAttributes                      = new ArrayList<>();
    private List<LocationAttributeAuthorization>         locationAttributeAuthorizations         = new ArrayList<>();
    private List<LocationNetworkAuthorization>           locationNetworkAuthorizations           = new ArrayList<>();
    private Map<String, Relationship>                    relationships                           = new HashMap<>();
    private List<RelationshipAttribute>                  relationshipAttributes                  = new ArrayList<>();
    private List<RelationshipAttributeAuthorization>     relationshipAttributeAuthorizations     = new ArrayList<>();
    private List<RelationshipNetwork>                    relationshipNetworks                    = new ArrayList<>();
    private Map<String, Product>                         products                                = new HashMap<>();
    private List<ProductNetwork>                         productNetworks                         = new ArrayList<>();
    private List<ProductAgencyAccessAuthorization>       productAgencyAccessAuthorizations       = new ArrayList<>();
    private List<ProductAttributeAccessAuthorization>    productAttributeAccessAuthorizations    = new ArrayList<>();
    private List<ProductLocationAccessAuthorization>     productLocationAccessAuthorizations     = new ArrayList<>();
    private List<ProductRelationshipAccessAuthorization> productRelationshipAccessAuthorizations = new ArrayList<>();
    private List<ProductStatusCodeAccessAuthorization>   productStatusCodeAccessAuthorizations   = new ArrayList<>();
    private List<ProductUnitAccessAuthorization>         productUnitAccessAuthorizations         = new ArrayList<>();
    private List<ProductAttribute>                       productAttributes                       = new ArrayList<>();
    private List<ProductAttributeAuthorization>          productAttributeAuthorizations          = new ArrayList<>();
    private List<ProductLocation>                        productLocations                        = new ArrayList<>();
    private List<ProductLocationAttribute>               productLocationAttributes               = new ArrayList<>();
    private List<ProductNetworkAttribute>                productNetworkAttributes                = new ArrayList<>();
    private List<ProductNetworkAuthorization>            productNetworkAuthorizations            = new ArrayList<>();

    /**
     * @return the agencies
     */
    @Override
    public Collection<Agency> getAgencies() {
        return agencies.values();
    }

    public Agency getAgency(String name) {
        return agencies.get(name);
    }

    /**
     * @return the agencyAttributeAuthorizations
     */
    @Override
    public List<AgencyAttributeAuthorization> getAgencyAttributeAuthorizations() {
        return agencyAttributeAuthorizations;
    }

    /**
     * @return the agencyAttributes
     */
    @Override
    public List<AgencyAttribute> getAgencyAttributes() {
        return agencyAttributes;
    }

    /**
     * @return the agencyLocationAccessAuthorizations
     */
    @Override
    public List<AgencyLocationAccessAuthorization> getAgencyLocationAccessAuthorizations() {
        return agencyLocationAccessAuthorizations;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAgencyLocations()
     */
    @Override
    public List<AgencyLocation> getAgencyLocations() {
        return agencyLocations;
    }

    /**
     * @return the agencyNetworkAuthorizations
     */
    @Override
    public List<AgencyNetworkAuthorization> getAgencyNetworkAuthorizations() {
        return agencyNetworkAuthorizations;
    }

    /**
     * @return the agencyNetworks
     */
    @Override
    public List<AgencyNetwork> getAgencyNetworks() {
        return agencyNetworks;
    }

    /**
     * @return the agencyProductAccessAuthorizations
     */
    @Override
    public List<AgencyProductAccessAuthorization> getAgencyProductAccessAuthorizations() {
        return agencyProductAccessAuthorizations;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAgencyProducts()
     */
    @Override
    public List<AgencyProduct> getAgencyProducts() {
        return agencyProducts;
    }

    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @return the attributeMetaAttributeAuthorizations
     */
    @Override
    public List<AttributeMetaAttributeAuthorization> getAttributeMetaAttributeAuthorizations() {
        return attributeMetaAttributeAuthorizations;
    }

    /**
     * @return the attributeMetaAttributes
     */
    @Override
    public List<AttributeMetaAttribute> getAttributeMetaAttributes() {
        return attributeMetaAttributes;
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
    public Collection<Attribute> getAttributes() {
        return attributes.values();
    }

    /**
     * @return the jobChronologies
     */
    @Override
    public List<JobChronology> getJobChronologies() {
        return jobChronologies;
    }

    /**
     * @return the jobs
     */
    @Override
    public List<Job> getJobs() {
        return jobs;
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    /**
     * @return the locationAgencyAccessAuthorizations
     */
    @Override
    public List<LocationAgencyAccessAuthorization> getLocationAgencyAccessAuthorizations() {
        return locationAgencyAccessAuthorizations;
    }

    /**
     * @return the locationAttributeAuthorizations
     */
    @Override
    public List<LocationAttributeAuthorization> getLocationAttributeAuthorizations() {
        return locationAttributeAuthorizations;
    }

    /**
     * @return the locationAttributes
     */
    @Override
    public List<LocationAttribute> getLocationAttributes() {
        return locationAttributes;
    }

    /**
     * @return the locationNetworkAuthorizations
     */
    @Override
    public List<LocationNetworkAuthorization> getLocationNetworkAuthorizations() {
        return locationNetworkAuthorizations;
    }

    /**
     * @return the locationNetworks
     */
    @Override
    public List<LocationNetwork> getLocationNetworks() {
        return locationNetworks;
    }

    /**
     * @return the locationProductAccessAuthorizations
     */
    @Override
    public List<LocationProductAccessAuthorization> getLocationProductAccessAuthorizations() {
        return locationProductAccessAuthorizations;
    }

    /**
     * @return the locations
     */
    @Override
    public Collection<Location> getLocations() {
        return locations.values();
    }

    /**
     * @return the metaProtocols
     */
    @Override
    public List<MetaProtocol> getMetaProtocols() {
        return metaProtocols;
    }

    public Product getProduct(String name) {
        return products.get(name);
    }

    /**
     * @return the productAgencyAccessAuthorizations
     */
    @Override
    public List<ProductAgencyAccessAuthorization> getProductAgencyAccessAuthorizations() {
        return productAgencyAccessAuthorizations;
    }

    /**
     * @return the productAttributeAccessAuthorizations
     */
    @Override
    public List<ProductAttributeAccessAuthorization> getProductAttributeAccessAuthorizations() {
        return productAttributeAccessAuthorizations;
    }

    /**
     * @return the productAttributeAuthorizations
     */
    @Override
    public List<ProductAttributeAuthorization> getProductAttributeAuthorizations() {
        return productAttributeAuthorizations;
    }

    /**
     * @return the productAttributes
     */
    @Override
    public List<ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    /**
     * @return the productChildSequencingAuthorizations
     */
    @Override
    public List<ProductChildSequencingAuthorization> getProductChildSequencingAuthorizations() {
        return productChildSequencingAuthorizations;
    }

    /**
     * @return the productLocationAccessAuthorizations
     */
    @Override
    public List<ProductLocationAccessAuthorization> getProductLocationAccessAuthorizations() {
        return productLocationAccessAuthorizations;
    }

    /**
     * @return the productLocationAttributes
     */
    @Override
    public List<ProductLocationAttribute> getProductLocationAttributes() {
        return productLocationAttributes;
    }

    /**
     * @return the productLocations
     */
    @Override
    public List<ProductLocation> getProductLocations() {
        return productLocations;
    }

    /**
     * @return the productNetworkAttributes
     */
    @Override
    public List<ProductNetworkAttribute> getProductNetworkAttributes() {
        return productNetworkAttributes;
    }

    /**
     * @return the productNetworkAuthorizations
     */
    @Override
    public List<ProductNetworkAuthorization> getProductNetworkAuthorizations() {
        return productNetworkAuthorizations;
    }

    /**
     * @return the productNetworks
     */
    @Override
    public List<ProductNetwork> getProductNetworks() {
        return productNetworks;
    }

    /**
     * @return the productParentSequencingAuthorizations
     */
    @Override
    public List<ProductParentSequencingAuthorization> getProductParentSequencingAuthorizations() {
        return productParentSequencingAuthorizations;
    }

    /**
     * @return the productRelationshipAccessAuthorizations
     */
    @Override
    public List<ProductRelationshipAccessAuthorization> getProductRelationshipAccessAuthorizations() {
        return productRelationshipAccessAuthorizations;
    }

    /**
     * @return the products
     */
    @Override
    public Collection<Product> getProducts() {
        return products.values();
    }

    /**
     * @return the productSiblingSequencingAuthorizations
     */
    @Override
    public List<ProductSiblingSequencingAuthorization> getProductSiblingSequencingAuthorizations() {
        return productSiblingSequencingAuthorizations;
    }

    /**
     * @return the productStatusCodeAccessAuthorizations
     */
    @Override
    public List<ProductStatusCodeAccessAuthorization> getProductStatusCodeAccessAuthorizations() {
        return productStatusCodeAccessAuthorizations;
    }

    /**
     * @return the productUnitAccessAuthorizations
     */
    @Override
    public List<ProductUnitAccessAuthorization> getProductUnitAccessAuthorizations() {
        return productUnitAccessAuthorizations;
    }

    /**
     * @return the protocols
     */
    @Override
    public List<Protocol> getProtocols() {
        return protocols;
    }

    public Relationship getRelationship(String name) {
        return relationships.get(name);
    }

    /**
     * @return the relationshipAttributeAuthorizations
     */
    @Override
    public List<RelationshipAttributeAuthorization> getRelationshipAttributeAuthorizations() {
        return relationshipAttributeAuthorizations;
    }

    /**
     * @return the relationshipAttributes
     */
    @Override
    public List<RelationshipAttribute> getRelationshipAttributes() {
        return relationshipAttributes;
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
    public Collection<Relationship> getRelationships() {
        return relationships.values();
    }

    public StatusCode getStatusCode(String name) {
        return statusCodes.get(name);
    }

    /**
     * @return the statusCodeAttributeAuthorizations
     */
    @Override
    public List<StatusCodeAttributeAuthorization> getStatusCodeAttributeAuthorizations() {
        return statusCodeAttributeAuthorizations;
    }

    /**
     * @return the statusCodeAttributes
     */
    @Override
    public List<StatusCodeAttribute> getStatusCodeAttributes() {
        return statusCodeAttributes;
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
    public Collection<StatusCode> getStatusCodes() {
        return statusCodes.values();
    }

    /**
     * @return the statusCodeSequencings
     */
    @Override
    public List<StatusCodeSequencing> getStatusCodeSequencings() {
        return statusCodeSequencings;
    }

    public Unit getUnit(String name) {
        return units.get(name);
    }

    /**
     * @return the unitAttributeAuthorizations
     */
    @Override
    public List<UnitAttributeAuthorization> getUnitAttributeAuthorizations() {
        return unitAttributeAuthorizations;
    }

    /**
     * @return the unitAttributes
     */
    @Override
    public List<UnitAttribute> getUnitAttributes() {
        return unitAttributes;
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
    public Collection<Unit> getUnits() {
        return units.values();
    }

    /**
     * @return the unitValues
     */
    @Override
    public List<UnitValue> getUnitValues() {
        return unitValues;
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
     * Merge this workspace into the supplied enterprise manager
     * 
     * @param em
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void merge(EntityManager em) throws IllegalArgumentException,
                                       IllegalAccessException {

        Map<Ruleform, Ruleform> merged = new HashMap<>();
        for (Field field : WorkspaceSnapshot.class.getDeclaredFields()) {
            log.debug(String.format("Merging: %s", field.getName()));
            if (List.class.isAssignableFrom(field.getType())) {
                mergeList(em, merged, field);
            } else {
                mergeEntity(em, merged, field);
            }
        }
    }

    /**
     * @param agencies
     *            the agencies to set
     */
    @Override
    public void setAgencies(List<Agency> agencies) {
        for (Agency a : agencies) {
            this.agencies.put(a.getName(), a);
        }
    }

    /**
     * @param agencyAttributeAuthorizations
     *            the agencyAttributeAuthorizations to set
     */
    @Override
    public void setAgencyAttributeAuthorizations(List<AgencyAttributeAuthorization> agencyAttributeAuthorizations) {
        this.agencyAttributeAuthorizations = agencyAttributeAuthorizations;
    }

    /**
     * @param agencyAttributes
     *            the agencyAttributes to set
     */
    @Override
    public void setAgencyAttributes(List<AgencyAttribute> agencyAttributes) {
        this.agencyAttributes = agencyAttributes;
    }

    /**
     * @param agencyLocationAccessAuthorizations
     *            the agencyLocationAccessAuthorizations to set
     */
    @Override
    public void setAgencyLocationAccessAuthorizations(List<AgencyLocationAccessAuthorization> agencyLocationAccessAuthorizations) {
        this.agencyLocationAccessAuthorizations = agencyLocationAccessAuthorizations;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#setAgencyLocations(java.util.List)
     */
    @Override
    public void setAgencyLocations(List<AgencyLocation> agencyLocations) {
        this.agencyLocations = agencyLocations;
    }

    /**
     * @param agencyNetworkAuthorizations
     *            the agencyNetworkAuthorizations to set
     */
    @Override
    public void setAgencyNetworkAuthorizations(List<AgencyNetworkAuthorization> agencyNetworkAuthorizations) {
        this.agencyNetworkAuthorizations = agencyNetworkAuthorizations;
    }

    /**
     * @param agencyNetworks
     *            the agencyNetworks to set
     */
    @Override
    public void setAgencyNetworks(List<AgencyNetwork> agencyNetworks) {
        this.agencyNetworks = agencyNetworks;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#setAgencyProduct(java.util.List)
     */
    @Override
    public void setAgencyProduct(List<AgencyProduct> agencyProducts) {
        this.agencyProducts = agencyProducts;
    }

    /**
     * @param agencyProductAccessAuthorizations
     *            the agencyProductAccessAuthorizations to set
     */
    @Override
    public void setAgencyProductAccessAuthorizations(List<AgencyProductAccessAuthorization> agencyProductAccessAuthorizations) {
        this.agencyProductAccessAuthorizations = agencyProductAccessAuthorizations;
    }

    /**
     * @param attributeMetaAttributeAuthorizations
     *            the attributeMetaAttributeAuthorizations to set
     */
    @Override
    public void setAttributeMetaAttributeAuthorizations(List<AttributeMetaAttributeAuthorization> attributeMetaAttributeAuthorizations) {
        this.attributeMetaAttributeAuthorizations = attributeMetaAttributeAuthorizations;
    }

    /**
     * @param attributeMetaAttributes
     *            the attributeMetaAttributes to set
     */
    @Override
    public void setAttributeMetaAttributes(List<AttributeMetaAttribute> attributeMetaAttributes) {
        this.attributeMetaAttributes = attributeMetaAttributes;
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
        for (Attribute a : attributes) {
            this.attributes.put(a.getName(), a);
        }
    }

    /**
     * @param jobChronologies
     *            the jobChronologies to set
     */
    @Override
    public void setJobChronologies(List<JobChronology> jobChronologies) {
        this.jobChronologies = jobChronologies;
    }

    /**
     * @param jobs
     *            the jobs to set
     */
    @Override
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * @param locationAgencyAccessAuthorizations
     *            the locationAgencyAccessAuthorizations to set
     */
    @Override
    public void setLocationAgencyAccessAuthorizations(List<LocationAgencyAccessAuthorization> locationAgencyAccessAuthorizations) {
        this.locationAgencyAccessAuthorizations = locationAgencyAccessAuthorizations;
    }

    /**
     * @param locationAttributeAuthorizations
     *            the locationAttributeAuthorizations to set
     */
    @Override
    public void setLocationAttributeAuthorizations(List<LocationAttributeAuthorization> locationAttributeAuthorizations) {
        this.locationAttributeAuthorizations = locationAttributeAuthorizations;
    }

    /**
     * @param locationAttributes
     *            the locationAttributes to set
     */
    @Override
    public void setLocationAttributes(List<LocationAttribute> locationAttributes) {
        this.locationAttributes = locationAttributes;
    }

    /**
     * @param locationNetworkAuthorizations
     *            the locationNetworkAuthorizations to set
     */
    @Override
    public void setLocationNetworkAuthorizations(List<LocationNetworkAuthorization> locationNetworkAuthorizations) {
        this.locationNetworkAuthorizations = locationNetworkAuthorizations;
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
     * @param locationProductAccessAuthorizations
     *            the locationProductAccessAuthorizations to set
     */
    @Override
    public void setLocationProductAccessAuthorizations(List<LocationProductAccessAuthorization> locationProductAccessAuthorizations) {
        this.locationProductAccessAuthorizations = locationProductAccessAuthorizations;
    }

    /**
     * @param locations
     *            the locations to set
     */
    @Override
    public void setLocations(List<Location> locations) {
        for (Location a : locations) {
            this.locations.put(a.getName(), a);
        }
    }

    /**
     * @param metaProtocols
     *            the metaProtocols to set
     */
    @Override
    public void setMetaProtocols(List<MetaProtocol> metaProtocols) {
        this.metaProtocols = metaProtocols;
    }

    /**
     * @param productAgencyAccessAuthorizations
     *            the productAgencyAccessAuthorizations to set
     */
    @Override
    public void setProductAgencyAccessAuthorizations(List<ProductAgencyAccessAuthorization> productAgencyAccessAuthorizations) {
        this.productAgencyAccessAuthorizations = productAgencyAccessAuthorizations;
    }

    /**
     * @param productAttributeAccessAuthorizations
     *            the productAttributeAccessAuthorizations to set
     */
    @Override
    public void setProductAttributeAccessAuthorizations(List<ProductAttributeAccessAuthorization> productAttributeAccessAuthorizations) {
        this.productAttributeAccessAuthorizations = productAttributeAccessAuthorizations;
    }

    /**
     * @param productAttributeAuthorizations
     *            the productAttributeAuthorizations to set
     */
    @Override
    public void setProductAttributeAuthorizations(List<ProductAttributeAuthorization> productAttributeAuthorizations) {
        this.productAttributeAuthorizations = productAttributeAuthorizations;
    }

    /**
     * @param productAttributes
     *            the productAttributes to set
     */
    @Override
    public void setProductAttributes(List<ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }

    /**
     * @param productChildSequencingAuthorizations
     *            the productChildSequencingAuthorizations to set
     */
    @Override
    public void setProductChildSequencingAuthorizations(List<ProductChildSequencingAuthorization> productChildSequencingAuthorizations) {
        this.productChildSequencingAuthorizations = productChildSequencingAuthorizations;
    }

    /**
     * @param productLocationAccessAuthorizations
     *            the productLocationAccessAuthorizations to set
     */
    @Override
    public void setProductLocationAccessAuthorizations(List<ProductLocationAccessAuthorization> productLocationAccessAuthorizations) {
        this.productLocationAccessAuthorizations = productLocationAccessAuthorizations;
    }

    /**
     * @param productLocationAttributes
     *            the productLocationAttributes to set
     */
    @Override
    public void setProductLocationAttributes(List<ProductLocationAttribute> productLocationAttributes) {
        this.productLocationAttributes = productLocationAttributes;
    }

    /**
     * @param productLocations
     *            the productLocations to set
     */
    @Override
    public void setProductLocations(List<ProductLocation> productLocations) {
        this.productLocations = productLocations;
    }

    /**
     * @param productNetworkAttributes
     *            the productNetworkAttributes to set
     */
    @Override
    public void setProductNetworkAttributes(List<ProductNetworkAttribute> productNetworkAttributes) {
        this.productNetworkAttributes = productNetworkAttributes;
    }

    /**
     * @param productNetworkAuthorizations
     *            the productNetworkAuthorizations to set
     */
    @Override
    public void setProductNetworkAuthorizations(List<ProductNetworkAuthorization> productNetworkAuthorizations) {
        this.productNetworkAuthorizations = productNetworkAuthorizations;
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
     * @param productParentSequencingAuthorizations
     *            the productParentSequencingAuthorizations to set
     */
    @Override
    public void setProductParentSequencingAuthorizations(List<ProductParentSequencingAuthorization> productParentSequencingAuthorizations) {
        this.productParentSequencingAuthorizations = productParentSequencingAuthorizations;
    }

    /**
     * @param productRelationshipAccessAuthorizations
     *            the productRelationshipAccessAuthorizations to set
     */
    @Override
    public void setProductRelationshipAccessAuthorizations(List<ProductRelationshipAccessAuthorization> productRelationshipAccessAuthorizations) {
        this.productRelationshipAccessAuthorizations = productRelationshipAccessAuthorizations;
    }

    /**
     * @param products
     *            the products to set
     */
    @Override
    public void setProducts(List<Product> products) {
        for (Product a : products) {
            this.products.put(a.getName(), a);
        }
    }

    /**
     * @param productSiblingSequencingAuthorizations
     *            the productSiblingSequencingAuthorizations to set
     */
    @Override
    public void setProductSiblingSequencingAuthorizations(List<ProductSiblingSequencingAuthorization> productSiblingSequencingAuthorizations) {
        this.productSiblingSequencingAuthorizations = productSiblingSequencingAuthorizations;
    }

    /**
     * @param productStatusCodeAccessAuthorizations
     *            the productStatusCodeAccessAuthorizations to set
     */
    @Override
    public void setProductStatusCodeAccessAuthorizations(List<ProductStatusCodeAccessAuthorization> productStatusCodeAccessAuthorizations) {
        this.productStatusCodeAccessAuthorizations = productStatusCodeAccessAuthorizations;
    }

    /**
     * @param productUnitAccessAuthorizations
     *            the productUnitAccessAuthorizations to set
     */
    @Override
    public void setProductUnitAccessAuthorizations(List<ProductUnitAccessAuthorization> productUnitAccessAuthorizations) {
        this.productUnitAccessAuthorizations = productUnitAccessAuthorizations;
    }

    /**
     * @param protocols
     *            the protocols to set
     */
    @Override
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    /**
     * @param relationshipAttributeAuthorizations
     *            the relationshipAttributeAuthorizations to set
     */
    @Override
    public void setRelationshipAttributeAuthorizations(List<RelationshipAttributeAuthorization> relationshipAttributeAuthorizations) {
        this.relationshipAttributeAuthorizations = relationshipAttributeAuthorizations;
    }

    /**
     * @param relationshipAttributes
     *            the relationshipAttributes to set
     */
    @Override
    public void setRelationshipAttributes(List<RelationshipAttribute> relationshipAttributes) {
        this.relationshipAttributes = relationshipAttributes;
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
        for (Relationship a : relationships) {
            this.relationships.put(a.getName(), a);
        }
    }

    /**
     * @param statusCodeAttributeAuthorizations
     *            the statusCodeAttributeAuthorizations to set
     */
    @Override
    public void setStatusCodeAttributeAuthorizations(List<StatusCodeAttributeAuthorization> statusCodeAttributeAuthorizations) {
        this.statusCodeAttributeAuthorizations = statusCodeAttributeAuthorizations;
    }

    /**
     * @param statusCodeAttributes
     *            the statusCodeAttributes to set
     */
    @Override
    public void setStatusCodeAttributes(List<StatusCodeAttribute> statusCodeAttributes) {
        this.statusCodeAttributes = statusCodeAttributes;
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
        for (StatusCode a : statusCodes) {
            this.statusCodes.put(a.getName(), a);
        }
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
     * @param unitAttributeAuthorizations
     *            the unitAttributeAuthorizations to set
     */
    @Override
    public void setUnitAttributeAuthorizations(List<UnitAttributeAuthorization> unitAttributeAuthorizations) {
        this.unitAttributeAuthorizations = unitAttributeAuthorizations;
    }

    /**
     * @param unitAttributes
     *            the unitAttributes to set
     */
    @Override
    public void setUnitAttributes(List<UnitAttribute> unitAttributes) {
        this.unitAttributes = unitAttributes;
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
        for (Unit a : units) {
            this.units.put(a.getName(), a);
        }
    }

    /**
     * @param unitValues
     *            the unitValues to set
     */
    @Override
    public void setUnitValues(List<UnitValue> unitValues) {
        this.unitValues = unitValues;
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

    private void mergeEntity(EntityManager em, Map<Ruleform, Ruleform> merged,
                             Field field) throws IllegalAccessException {
        field.set(this, ((Ruleform) field.get(this)).manageEntity(em, merged));
    }

    private void mergeList(EntityManager em, Map<Ruleform, Ruleform> merged,
                           Field field) throws IllegalArgumentException,
                                       IllegalAccessException {
        @SuppressWarnings("unchecked")
        List<Object> oldList = (List<Object>) field.get(this);
        List<Object> newList = new ArrayList<>(oldList.size());
        for (Object entity : oldList) {
            newList.add(((Ruleform) entity).manageEntity(em, merged));
        }
    }

}
