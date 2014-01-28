/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
package com.hellblazer.CoRE.time;

import static com.hellblazer.CoRE.time.IntervalAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.unit.Unit;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "interval_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "interval_attribute_id_seq", sequenceName = "interval_attribute_id_seq")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from IntervalAttribute ra where ra.interval = :inteval and ra.attribute = :attribute") })
public class IntervalAttribute extends AttributeValue<Interval> {
    public static final String GET_ATTRIBUTE    = "intervalAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "interval_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Interval
    @ManyToOne
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

    public IntervalAttribute(Long id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Interval getInterval() {
        return interval;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Interval>, Interval> getRuleformAttribute() {
        return IntervalAttribute_.interval;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Interval> getRuleformClass() {
        return Interval.class;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
