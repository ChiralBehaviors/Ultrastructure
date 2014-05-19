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
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * @author hparry
 * 
 */
public interface Workspace {

    List<Agency> getAgencies();

    List<AgencyNetwork> getAgencyNetworks();

    List<AttributeNetwork> getAttributeNetworks();

    List<Attribute> getAttributes();

    List<LocationNetwork> getLocationNetworks();

    List<Location> getLocations();

    List<ProductNetwork> getProductNetworks();

    List<Product> getProducts();

    List<RelationshipNetwork> getRelationshipNetworks();

    List<Relationship> getRelationships();

    List<StatusCode> getStatusCodes();

    List<StatusCodeSequencing> getStatusCodeSequencings();

    Product getWorkspaceProduct();

    Relationship getWorkspaceRelationship();

    void setAgencies(List<Agency> agencies);

    void setAgencyNetworks(List<AgencyNetwork> agencyNetworks);

    void setAttributeNetworks(List<AttributeNetwork> attributeNetworks);

    void setAttributes(List<Attribute> attributes);

    void setLocationNetworks(List<LocationNetwork> locationNetworks);

    void setLocations(List<Location> locations);

    void setProductNetworks(List<ProductNetwork> productNetworks);

    void setProducts(List<Product> products);

    void setRelationshipNetworks(List<RelationshipNetwork> relationshipNetworks);

    void setRelationships(List<Relationship> relationships);

    void setStatusCodes(List<StatusCode> statusCodes);

    void setStatusCodeSequencings(List<StatusCodeSequencing> statusCodeSequencings);

    void setWorkspaceProduct(Product workspaceProduct);

    void setWorkspaceRelationship(Relationship workspaceRelationship);

}
