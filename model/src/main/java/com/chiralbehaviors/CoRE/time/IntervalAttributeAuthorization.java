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
package com.chiralbehaviors.CoRE.time;

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
 * The authorizations for attributes on entities.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "interval_attribute_authorization", schema = "ruleform")
public class IntervalAttributeAuthorization
        extends AttributeAuthorization<Interval, IntervalNetwork> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_authorization")
    private IntervalNetworkAuthorization networkAuthorization;

    public IntervalAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public IntervalAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     * @param coreModel
     */
    public IntervalAttributeAuthorization(Attribute attribute, Agency agency) {
        super(attribute, agency);
    }

    /**
     * @param id
     */
    public IntervalAttributeAuthorization(UUID id) {
        super(id);
    }

    @Override
    public NetworkAuthorization<Interval> getNetworkAuthorization() {
        return networkAuthorization;
    }

    @Override
    public void setNetworkAuthorization(NetworkAuthorization<Interval> auth) {
        networkAuthorization = (IntervalNetworkAuthorization) auth;
    }
}