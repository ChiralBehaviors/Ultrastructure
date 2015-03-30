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
package com.chiralbehaviors.CoRE.attribute.unit;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The value of a unit.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_values", schema = "ruleform")
public class UnitValue extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Column(name = "boolean_value")
    private Integer           booleanValue;

    @Column(name = "integer_value")
    private Integer           integerValue;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    @Column(name = "text_value")
    private String            textValue;

    // bi-directional many-to-one association to Unit
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "unit")
    private Unit              unit;

    public UnitValue() {
    }

    public Boolean getBooleanValue() {
        return toBoolean(booleanValue);
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getTextValue() {
        return textValue;
    }

    public Unit getUnit() {
        return unit;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, UnitValue> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = toInteger(booleanValue);
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}