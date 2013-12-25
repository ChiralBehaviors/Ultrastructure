/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.event.status;

import static com.hellblazer.CoRE.event.status.StatusCodeAttribute.GET_ATTRIBUTE;

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
@Table(name = "status_code_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_attribute_id_seq", sequenceName = "status_code_attribute_id_seq")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from StatusCodeAttribute ra where ra.statusCode = :inteval and ra.attribute = :attribute") })
public class StatusCodeAttribute extends AttributeValue<StatusCode> {
    public static final String GET_ATTRIBUTE    = "statusCodeAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "status_code_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public StatusCodeAttribute() {
        super();
    }

    public StatusCodeAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute) {
        super(attribute);
    }

    public StatusCodeAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, BigDecimal value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, boolean value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, String value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public StatusCodeAttribute(Long id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<StatusCode>, StatusCode> getRuleformAttribute() {
        return StatusCodeAttribute_.statusCode;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<StatusCode> getRuleformClass() {
        return StatusCode.class;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatusCode(StatusCode interval) {
        statusCode = interval;
    }
}
