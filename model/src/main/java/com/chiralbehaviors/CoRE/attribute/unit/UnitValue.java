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
import com.chiralbehaviors.CoRE.product.Product;
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

    private String            notes;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product_value")
    private Product           productValue;

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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, UnitValue> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitValue;
    }

    public Boolean getBooleanValue() {
        return toBoolean(booleanValue);
    }

    public Product getEntityValue() {
        return productValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    @Override
    public String getNotes() {
        return notes;
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

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = toInteger(booleanValue);
    }

    public void setEntityValue(Product product) {
        productValue = product;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
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