/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.job.status;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.network.NetworkAttribute;

/**
 * The attribute value for unit networks
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_network_attribute", schema = "ruleform")
public class StatusCodeNetworkAttribute
        extends NetworkAttribute<StatusCodeNetwork> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to StatusCodeNetwork
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "network_rule")
    private StatusCodeNetwork network;

    public StatusCodeNetworkAttribute() {
    }

    /**
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     */
    public StatusCodeNetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Attribute attribute, BigDecimal value,
                                      Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Attribute attribute, boolean value,
                                      Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Attribute attribute, int value,
                                      Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public StatusCodeNetworkAttribute(Attribute attribute, String value,
                                      Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public StatusCodeNetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public StatusCodeNetworkAttribute(UUID id) {
        super(id);
    }

    public StatusCodeNetwork getNetwork() {
        return network;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<StatusCodeNetworkAttribute, StatusCodeNetwork> getRuleformAttribute() {
        return StatusCodeNetworkAttribute_.network;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<StatusCodeNetwork> getRuleformClass() {
        return StatusCodeNetwork.class;
    }

    public void setNetwork(StatusCodeNetwork network) {
        this.network = network;
    }
}