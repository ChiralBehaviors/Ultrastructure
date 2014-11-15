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
package com.chiralbehaviors.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    @Column(name = "binary_value")
    private byte[]            binaryValue;

    @Column(name = "boolean_value")
    private Integer           booleanValue;

    @Column(name = "integer_value")
    private Integer           integerValue;

    @Column(name = "key")
    private String            key;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber   = 1;

    @Column(name = "text_value")
    private String            textValue;

    @Column(name = "timestamp_value")
    private Timestamp         timestampValue;

    // bi-directional many-to-one association to Unit
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "unit")
    private Unit              unit;

    /**
     *
     */
    public AttributeValue() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AttributeValue(Agency updatedBy) {
        super(updatedBy);
    }

    public AttributeValue(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttributeValue(Attribute attribute, Agency updatedBy) {
        this(updatedBy);
        this.attribute = attribute;
    }

    public AttributeValue(Attribute attribute, BigDecimal value,
                          Agency updatedBy) {
        this(attribute, updatedBy);
        numericValue = value;
    }

    public AttributeValue(Attribute attribute, Boolean value, Agency updatedBy) {
        this(attribute, updatedBy);
        booleanValue = toInteger(value);
    }

    public AttributeValue(Attribute attribute, int value, Agency updatedBy) {
        this(attribute, updatedBy);
        integerValue = value;
    }

    public AttributeValue(Attribute attribute, String value, Agency updatedBy) {
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
    public AttributeValue(UUID id) {
        super(id);
    }

    @JsonGetter
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonIgnore
    public byte[] getBinaryValue() {
        if (attribute.getValueType() != ValueType.BINARY) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BINARY,
                                                                  attribute.getValueType()));
        }
        return binaryValue;
    }

    @JsonIgnore
    public Boolean getBooleanValue() {
        if (attribute.getValueType() != ValueType.BOOLEAN) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BOOLEAN,
                                                                  attribute.getValueType()));
        }
        return toBoolean(booleanValue);
    }

    @JsonIgnore
    public Integer getIntegerValue() {
        if (attribute.getValueType() != ValueType.INTEGER) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.INTEGER,
                                                                  attribute.getValueType()));
        }
        return integerValue;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    @JsonIgnore
    public BigDecimal getNumericValue() {
        if (attribute.getValueType() != ValueType.NUMERIC) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.NUMERIC,
                                                                  attribute.getValueType()));
        }
        return numericValue;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getTextValue() {
        if (attribute.getValueType() != ValueType.TEXT) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.TEXT,
                                                                  attribute.getValueType()));
        }
        return textValue;
    }

    @JsonIgnore
    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    @JsonIgnore
    public Unit getUnit() {
        return unit;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        switch (attribute.getValueType()) {
            case BINARY:
                return (T) getBinaryValue();
            case BOOLEAN:
                return (T) getBooleanValue();
            case INTEGER:
                return (T) getIntegerValue();
            case NUMERIC:
                return (T) getNumericValue();
            case TEXT:
                return (T) getTextValue();
            case TIMESTAMP:
                return (T) getTimestampValue();
            default:
                throw new IllegalStateException(
                                                String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
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
        this.booleanValue = toInteger(booleanValue);
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

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
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
