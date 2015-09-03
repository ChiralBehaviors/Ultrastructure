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
package com.chiralbehaviors.CoRE.job.status;

import static com.chiralbehaviors.CoRE.job.status.StatusCodeAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "status_code_attribute", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "SELECT ra FROM StatusCodeAttribute ra WHERE ra.statusCode = :ruleform AND ra.attribute = :attribute ORDER BY ra.sequenceNumber") })
public class StatusCodeAttribute extends AttributeValue<StatusCode> {
    public static final String GET_ATTRIBUTE    = "statusCodeAttribute"
                                                  + GET_ATTRIBUTE_SUFFIX;
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode statusCode;

    public StatusCodeAttribute() {
        super();
    }

    public StatusCodeAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute) {
        super(attribute);
    }

    public StatusCodeAttribute(Attribute attribute, BigDecimal value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, boolean value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, int value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, String value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public StatusCodeAttribute(StatusCode statusCode, Attribute attribute,
                               Agency updatedBy) {
        super(attribute, updatedBy);
        setStatusCode(statusCode);
    }

    public StatusCodeAttribute(UUID id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<StatusCode>, StatusCode> getRuleformAttribute() {
        return StatusCodeAttribute_.statusCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<StatusCode> getRuleformClass() {
        return StatusCode.class;
    }

    @JsonGetter
    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode interval) {
        statusCode = interval;
    }
}
