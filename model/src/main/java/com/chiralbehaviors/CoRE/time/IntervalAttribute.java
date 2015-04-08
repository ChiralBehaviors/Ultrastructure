/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.time;

import static com.chiralbehaviors.CoRE.time.IntervalAttribute.GET_ATTRIBUTE;

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
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "interval_attribute", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "SELECT ra FROM IntervalAttribute ra WHERE ra.interval = :ruleform AND ra.attribute = :attribute ORDER BY ra.sequenceNumber") })
public class IntervalAttribute extends AttributeValue<Interval> {
    public static final String GET_ATTRIBUTE    = "intervalAttribute"
                                                  + GET_ATTRIBUTE_SUFFIX;
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    // bi-directional many-to-one association to Interval
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "interval")
    private Interval           interval;

    public IntervalAttribute() {
        super();
    }

    public IntervalAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public IntervalAttribute(Attribute attribute) {
        super(attribute);
    }

    public IntervalAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public IntervalAttribute(Attribute attribute, BigDecimal value,
                             Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public IntervalAttribute(Attribute attribute, boolean value,
                             Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public IntervalAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public IntervalAttribute(Attribute attribute, String value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public IntervalAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public IntervalAttribute(UUID id) {
        super(id);
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonGetter
    public Interval getInterval() {
        return interval;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Interval>, Interval> getRuleformAttribute() {
        return IntervalAttribute_.interval;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Interval> getRuleformClass() {
        return Interval.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, IntervalAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.intervalAttribute;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
