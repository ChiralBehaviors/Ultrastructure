/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This file is part of the Thoth Interest Management and Load Balancing
 * Framework.
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
package com.hellblazer.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.event.JobAttribute;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The abstract attribute value.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AttributeValue<RuleForm extends Ruleform> extends
        Ruleform {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    @Column(name = "binary_value")
    private byte[]            binaryValue;

    @Column(name = "boolean_value")
    private Boolean           booleanValue;

    @Column(name = "integer_value")
    private Integer           integerValue;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber   = 1;

    @Column(name = "text_value")
    private String            textValue;

    @Column(name = "timestamp_value")
    private Timestamp         timestampValue;

    //bi-directional many-to-one association to Unit
    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit              unit;

    /**
     * 
     */
    public AttributeValue() {
        super();
    }

    public AttributeValue(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttributeValue(Attribute attribute, BigDecimal value,
                          Resource updatedBy) {
        this(attribute, updatedBy);
        numericValue = value;
    }

    public AttributeValue(Attribute attribute, boolean value, Resource updatedBy) {
        this(attribute, updatedBy);
        booleanValue = value;
    }

    public AttributeValue(Attribute attribute, int value, Resource updatedBy) {
        this(attribute, updatedBy);
        integerValue = value;
    }

    public AttributeValue(Attribute attribute, Resource updatedBy) {
        this(updatedBy);
        this.attribute = attribute;
    }

    public AttributeValue(Attribute attribute, String value, Resource updatedBy) {
        this(attribute, updatedBy);
        textValue = value;
    }

    public AttributeValue(Attribute attribute, Unit unit) {
        this(attribute);
        this.unit = unit;
    }

    /**
     * @param id
     */
    public AttributeValue(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public AttributeValue(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * Copy the state of the receiver into the clone
     * 
     * @param clone
     */
    public void copyInto(JobAttribute clone) {
        clone.setAttribute(getAttribute());
        clone.setBinaryValue(getBinaryValue());
        clone.setBooleanValue(getBooleanValue());
        clone.setIntegerValue(getIntegerValue());
        clone.setNumericValue(getNumericValue());
        clone.setTextValue(getTextValue());
        clone.setTimestampValue(getTimestampValue());
        clone.setUnit(getUnit());
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public byte[] getBinaryValue() {
        if (attribute.getValueType() != ValueType.BINARY) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BINARY,
                                                                  attribute.getValueType()));
        }
        return binaryValue;
    }

    public Boolean getBooleanValue() {
        if (attribute.getValueType() != ValueType.BOOLEAN) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BOOLEAN,
                                                                  attribute.getValueType()));
        }
        return booleanValue;
    }

    public Integer getIntegerValue() {
        if (attribute.getValueType() != ValueType.INTEGER) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.INTEGER,
                                                                  attribute.getValueType()));
        }
        return integerValue;
    }

    public BigDecimal getNumericValue() {
        if (attribute.getValueType() != ValueType.NUMERIC) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.NUMERIC,
                                                                  attribute.getValueType()));
        }
        return numericValue;
    }

    abstract public SingularAttribute<? extends AttributeValue<RuleForm>, RuleForm> getRuleformAttribute();

    /**
     * This method exists because generics is not a runtime capability.
     * 
     * @return the concrete class that this attribute is associated with
     */
    abstract public Class<RuleForm> getRuleformClass();

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getTextValue() {
        if (attribute.getValueType() != ValueType.TEXT) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.TEXT,
                                                                  attribute.getValueType()));
        }
        return textValue;
    }

    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setBinaryValue(byte[] binaryValue) {
        if (attribute.getValueType() != ValueType.BINARY) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.BINARY,
                                                                  attribute.getValueType()));
        }
        this.binaryValue = binaryValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        if (attribute.getValueType() != ValueType.BOOLEAN) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.BOOLEAN,
                                                                  attribute.getValueType()));
        }
        this.booleanValue = booleanValue;
    }

    public void setIntegerValue(Integer integerValue) {
        if (attribute.getValueType() != ValueType.INTEGER) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.INTEGER,
                                                                  attribute.getValueType()));
        }
        this.integerValue = integerValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        if (attribute.getValueType() != ValueType.NUMERIC) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.NUMERIC,
                                                                  attribute.getValueType()));
        }
        this.numericValue = numericValue;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTextValue(String textValue) {
        if (attribute.getValueType() != ValueType.TEXT) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.TEXT,
                                                                  attribute.getValueType()));
        }
        this.textValue = textValue;
    }

    public void setTimestampValue(Timestamp timestampValue) {
        if (attribute.getValueType() != ValueType.TIMESTAMP) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.TIMESTAMP,
                                                                  attribute.getValueType()));
        }
        this.timestampValue = timestampValue;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

}
