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
import java.util.List;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
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
import com.chiralbehaviors.CoRE.event.JobAttribute;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.ProtocolAttribute;
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
import com.chiralbehaviors.CoRE.location.LocationRelationship;
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
import com.chiralbehaviors.CoRE.product.ProductLocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationNetwork;
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
 * @author hparry
 * 
 */
public interface Workspace {

    Collection<Agency> getAgencies();

    List<AgencyAttributeAuthorization> getAgencyAttributeAuthorizations();

    List<AgencyAttribute> getAgencyAttributes();

    List<AgencyLocationAccessAuthorization> getAgencyLocationAccessAuthorizations();

    List<AgencyNetworkAuthorization> getAgencyNetworkAuthorizations();

    List<AgencyNetwork> getAgencyNetworks();

    List<AgencyProductAccessAuthorization> getAgencyProductAccessAuthorizations();

    List<AttributeMetaAttributeAuthorization> getAttributeMetaAttributeAuthorizations();

    List<AttributeMetaAttribute> getAttributeMetaAttributes();

    List<AttributeNetwork> getAttributeNetworks();

    Collection<Attribute> getAttributes();

    List<JobAttribute> getJobAttributes();

    List<JobChronology> getJobChronologies();

    List<Job> getJobs();

    List<LocationAgencyAccessAuthorization> getLocationAgencyAccessAuthorizations();

    List<LocationAttributeAuthorization> getLocationAttributeAuthorizations();

    List<LocationAttribute> getLocationAttributes();

    List<LocationNetworkAuthorization> getLocationNetworkAuthorizations();

    List<LocationNetwork> getLocationNetworks();

    List<LocationProductAccessAuthorization> getLocationProductAccessAuthorizations();

    List<LocationRelationship> getLocationRelationships();

    Collection<Location> getLocations();

    List<MetaProtocol> getMetaProtocols();

    List<ProductAgencyAccessAuthorization> getProductAgencyAccessAuthorizations();

    List<ProductAttributeAccessAuthorization> getProductAttributeAccessAuthorizations();

    List<ProductAttributeAuthorization> getProductAttributeAuthorizations();

    List<ProductAttribute> getProductAttributes();

    List<ProductChildSequencingAuthorization> getProductChildSequencingAuthorizations();

    List<ProductLocationAccessAuthorization> getProductLocationAccessAuthorizations();

    List<ProductLocationAttributeAuthorization> getProductLocationAttributeAuthorizations();

    List<ProductLocationAttribute> getProductLocationAttributes();

    List<ProductLocationNetwork> getProductLocationNetworks();

    List<ProductLocation> getProductLocations();

    List<ProductNetworkAttribute> getProductNetworkAttributes();

    List<ProductNetworkAuthorization> getProductNetworkAuthorizations();

    List<ProductNetwork> getProductNetworks();

    List<ProductParentSequencingAuthorization> getProductParentSequencingAuthorizations();

    List<ProductRelationshipAccessAuthorization> getProductRelationshipAccessAuthorizations();

    Collection<Product> getProducts();

    List<ProductSiblingSequencingAuthorization> getProductSiblingSequencingAuthorizations();

    List<ProductStatusCodeAccessAuthorization> getProductStatusCodeAccessAuthorizations();

    List<ProductUnitAccessAuthorization> getProductUnitAccessAuthorizations();

    List<ProtocolAttribute> getProtocolAttributes();

    List<Protocol> getProtocols();

    List<RelationshipAttributeAuthorization> getRelationshipAttributeAuthorizations();

    List<RelationshipAttribute> getRelationshipAttributes();

    List<RelationshipNetwork> getRelationshipNetworks();

    Collection<Relationship> getRelationships();

    List<StatusCodeAttributeAuthorization> getStatusCodeAttributeAuthorizations();

    List<StatusCodeAttribute> getStatusCodeAttributes();

    List<StatusCodeNetwork> getStatusCodeNetworks();

    Collection<StatusCode> getStatusCodes();

    List<StatusCodeSequencing> getStatusCodeSequencings();

    List<UnitAttributeAuthorization> getUnitAttributeAuthorizations();

    List<UnitAttribute> getUnitAttributes();

    List<UnitNetwork> getUnitNetworks();

    Collection<Unit> getUnits();

    List<UnitValue> getUnitValues();

    Product getWorkspaceProduct();

    Relationship getWorkspaceRelationship();

    void setAgencies(List<Agency> agencies);

    void setAgencyAttributeAuthorizations(List<AgencyAttributeAuthorization> agencyAttributeAuthorizations);

    void setAgencyAttributes(List<AgencyAttribute> agencyAttributes);

    void setAgencyLocationAccessAuthorizations(List<AgencyLocationAccessAuthorization> agencyLocationAccessAuthorizations);

    void setAgencyNetworkAuthorizations(List<AgencyNetworkAuthorization> agencyNetworkAuthorizations);

    void setAgencyNetworks(List<AgencyNetwork> agencyNetworks);

    void setAgencyProductAccessAuthorizations(List<AgencyProductAccessAuthorization> agencyProductAccessAuthorizations);

    void setAttributeMetaAttributeAuthorizations(List<AttributeMetaAttributeAuthorization> attributeMetaAttributeAuthorizations);

    void setAttributeMetaAttributes(List<AttributeMetaAttribute> attributeMetaAttributes);

    void setAttributeNetworks(List<AttributeNetwork> attributeNetworks);

    void setAttributes(List<Attribute> attributes);

    void setJobAttributes(List<JobAttribute> jobAttributes);

    void setJobChronologies(List<JobChronology> jobChronologies);

    void setJobs(List<Job> jobs);

    void setLocationAgencyAccessAuthorizations(List<LocationAgencyAccessAuthorization> locationAgencyAccessAuthorizations);

    void setLocationAttributeAuthorizations(List<LocationAttributeAuthorization> locationAttributeAuthorizations);

    void setLocationAttributes(List<LocationAttribute> locationAttributes);

    void setLocationNetworkAuthorizations(List<LocationNetworkAuthorization> locationNetworkAuthorizations);

    void setLocationNetworks(List<LocationNetwork> locationNetworks);

    void setLocationProductAccessAuthorizations(List<LocationProductAccessAuthorization> locationProductAccessAuthorizations);

    void setLocationRelationships(List<LocationRelationship> locationRelationships);

    void setLocations(List<Location> locations);

    void setMetaProtocols(List<MetaProtocol> metaProtocols);

    void setProductAgencyAccessAuthorizations(List<ProductAgencyAccessAuthorization> productAgencyAccessAuthorizations);

    void setProductAttributeAccessAuthorizations(List<ProductAttributeAccessAuthorization> productAttributeAccessAuthorizations);

    void setProductAttributeAuthorizations(List<ProductAttributeAuthorization> productAttributeAuthorizations);

    void setProductAttributes(List<ProductAttribute> productAttributes);

    void setProductChildSequencingAuthorizations(List<ProductChildSequencingAuthorization> productChildSequencingAuthorizations);

    void setProductLocationAccessAuthorizations(List<ProductLocationAccessAuthorization> productLocationAccessAuthorizations);

    void setProductLocationAttributeAuthorizations(List<ProductLocationAttributeAuthorization> productLocationAttributeAuthorizations);

    void setProductLocationAttributes(List<ProductLocationAttribute> productLocationAttributes);

    void setProductLocationNetworks(List<ProductLocationNetwork> productLocationNetworks);

    void setProductLocations(List<ProductLocation> productLocations);

    void setProductNetworkAttributes(List<ProductNetworkAttribute> productNetworkAttributes);

    void setProductNetworkAuthorizations(List<ProductNetworkAuthorization> productNetworkAuthorizations);

    void setProductNetworks(List<ProductNetwork> productNetworks);

    void setProductParentSequencingAuthorizations(List<ProductParentSequencingAuthorization> productParentSequencingAuthorizations);

    void setProductRelationshipAccessAuthorizations(List<ProductRelationshipAccessAuthorization> productRelationshipAccessAuthorizations);

    void setProducts(List<Product> products);

    void setProductSiblingSequencingAuthorizations(List<ProductSiblingSequencingAuthorization> productSiblingSequencingAuthorizations);

    void setProductStatusCodeAccessAuthorizations(List<ProductStatusCodeAccessAuthorization> productStatusCodeAccessAuthorizations);

    void setProductUnitAccessAuthorizations(List<ProductUnitAccessAuthorization> productUnitAccessAuthorizations);

    void setProtocolAttributes(List<ProtocolAttribute> protocolAttributes);

    void setProtocols(List<Protocol> protocols);

    void setRelationshipAttributeAuthorizations(List<RelationshipAttributeAuthorization> relationshipAttributeAuthorizations);

    void setRelationshipAttributes(List<RelationshipAttribute> relationshipAttributes);

    void setRelationshipNetworks(List<RelationshipNetwork> relationshipNetworks);

    void setRelationships(List<Relationship> relationships);

    void setStatusCodeAttributeAuthorizations(List<StatusCodeAttributeAuthorization> statusCodeAttributeAuthorizations);

    void setStatusCodeAttributes(List<StatusCodeAttribute> statusCodeAttributes);

    void setStatusCodeNetworks(List<StatusCodeNetwork> statusCodeNetworks);

    void setStatusCodes(List<StatusCode> statusCodes);

    void setStatusCodeSequencings(List<StatusCodeSequencing> statusCodeSequencings);

    void setUnitAttributeAuthorizations(List<UnitAttributeAuthorization> unitAttributeAuthorizations);

    void setUnitAttributes(List<UnitAttribute> unitAttributes);

    void setUnitNetworks(List<UnitNetwork> unitNetworks);

    void setUnits(List<Unit> units);

    void setUnitValues(List<UnitValue> unitValues);

    void setWorkspaceProduct(Product workspaceProduct);

    void setWorkspaceRelationship(Relationship workspaceRelationship);

}
