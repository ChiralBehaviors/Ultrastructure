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
package com.chiralbehaviors.CoRE.agency;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;

/**
 * The authorizations for attributes on entities.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_attribute_authorization", schema = "ruleform")
public class AgencyAttributeAuthorization
        extends AttributeAuthorization<Agency, AgencyNetwork> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "network_authorization")
    private AgencyNetworkAuthorization networkAuthorization;

    public AgencyAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public AgencyAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public AgencyAttributeAuthorization(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param id
     */
    public AgencyAttributeAuthorization(UUID id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.attribute.AttributeAuthorization#getNetworkAuthorization()
     */
    @Override
    public NetworkAuthorization<Agency> getNetworkAuthorization() {
        return networkAuthorization;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.attribute.AttributeAuthorization#setNetworkAuthorization(com.chiralbehaviors.CoRE.network.NetworkAuthorization)
     */
    @Override
    public void setNetworkAuthorization(NetworkAuthorization<Agency> auth) {
        networkAuthorization = (AgencyNetworkAuthorization) auth;
    }
}