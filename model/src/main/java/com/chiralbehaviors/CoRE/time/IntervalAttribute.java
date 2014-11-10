/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from IntervalAttribute ra where ra.interval = :inteval and ra.attribute = :attribute") })
public class IntervalAttribute extends AttributeValue<Interval> {
    public static final String GET_ATTRIBUTE    = "intervalAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, IntervalAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.intervalAttribute;
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

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
