/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.location;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;

/**
 *
 * The persistent class for the location_attribute_authorization database table.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "location_attribute_authorization", schema = "ruleform")
public class LocationAttributeAuthorization
        extends AttributeAuthorization<Location, LocationNetwork> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_authorization")
    private LocationNetworkAuthorization networkAuthorization;

    /**
     *
     */
    public LocationAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public LocationAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     * @param coreModel
     */
    public LocationAttributeAuthorization(Attribute attribute, Agency agency) {
        super(attribute, agency);
    }

    /**
     * @param id
     */
    public LocationAttributeAuthorization(UUID id) {
        super(id);
    }

    @Override
    public NetworkAuthorization<Location> getNetworkAuthorization() {
        return networkAuthorization;
    }

    @Override
    public void setNetworkAuthorization(NetworkAuthorization<Location> auth) {
        networkAuthorization = (LocationNetworkAuthorization) auth;
    }
}
