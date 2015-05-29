/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.product;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.XDomainAttrbuteAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hhildebrand
 *
 */
@Table(name = "product_location_attribute_authorization", schema = "ruleform")
@Entity
public class ProductLocationAttributeAuthorization extends
        XDomainAttrbuteAuthorization<Product, Location> {

    private static final long            serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_authorization")
    private ProductLocationAuthorization networkAuthorization;

    public ProductLocationAttributeAuthorization() {
        super();
    }

    public ProductLocationAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductLocationAttributeAuthorization(Attribute authorized,
                                                 Agency updatedBy) {
        super(authorized, updatedBy);
    }

    public ProductLocationAttributeAuthorization(UUID id) {
        super(id);
    }

    public ProductLocationAttributeAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.attribute.XDomainAttrbuteAuthorization#getNetworkAuthorization()
     */
    @Override
    public XDomainNetworkAuthorization<Product, Location> getNetworkAuthorization() {
        return networkAuthorization;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productLocationAttributeAuthorization;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.attribute.XDomainAttrbuteAuthorization#setNetworkAuthorization(com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization)
     */
    @Override
    public <T extends XDomainNetworkAuthorization<Product, Location>> void setNetworkAuthorization(@JsonProperty("networkAuthorization") T auth) {
        networkAuthorization = (ProductLocationAuthorization) auth;
    }

}
