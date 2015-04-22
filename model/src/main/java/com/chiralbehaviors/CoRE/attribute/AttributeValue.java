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
import com.chiralbehaviors.CoRE.Triggers;
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
    public static final String GET_ATTRIBUTE_SUFFIX = ".getAttribute";

    private static final long  serialVersionUID     = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    @Column(name = "binary_value")
    private byte[]             binaryValue;

    @Column(name = "boolean_value")
    private Integer            booleanValue;

    @Column(name = "integer_value")
    private Integer            integerValue;

    @Column(name = "key")
    private String             key;

    @Column(name = "numeric_value")
    private BigDecimal         numericValue;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber       = 0;

    @Column(name = "text_value")
    private String             textValue;

    @Column(name = "timestamp_value")
    private Timestamp          timestampValue;

    // bi-directional many-to-one association to Unit
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "unit")
    private Unit               unit;

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
        this.setAttribute(attribute);
    }

    public AttributeValue(Attribute attribute, Agency updatedBy) {
        this(updatedBy);
        this.setAttribute(attribute);
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
    public final Attribute getAttribute() {
        return attribute;
    }

    @JsonIgnore
    public byte[] getBinaryValue() {
        if (getAttribute().getValueType() != ValueType.BINARY) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BINARY,
                                                                  getAttribute().getValueType()));
        }
        return binaryValue;
    }

    @JsonIgnore
    public Boolean getBooleanValue() {
        if (getAttribute().getValueType() != ValueType.BOOLEAN) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.BOOLEAN,
                                                                  getAttribute().getValueType()));
        }
        return toBoolean(booleanValue);
    }

    @JsonIgnore
    public Integer getIntegerValue() {
        if (getAttribute().getValueType() != ValueType.INTEGER) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.INTEGER,
                                                                  getAttribute().getValueType()));
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
        if (getAttribute().getValueType() != ValueType.NUMERIC) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.NUMERIC,
                                                                  getAttribute().getValueType()));
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
        if (getAttribute().getValueType() != ValueType.TEXT) {
            throw new UnsupportedOperationException(
                                                    String.format("May not retrieve %s value for a %s attribute",
                                                                  ValueType.TEXT,
                                                                  getAttribute().getValueType()));
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
        switch (getAttribute().getValueType()) {
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
                                                              getAttribute().getValueType()));
        }
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    public final void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setBinaryValue(byte[] binaryValue) {
        if (getAttribute().getValueType() != ValueType.BINARY) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.BINARY,
                                                                  getAttribute().getValueType()));
        }
        this.binaryValue = binaryValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        if (getAttribute().getValueType() != ValueType.BOOLEAN) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.BOOLEAN,
                                                                  getAttribute().getValueType()));
        }
        this.booleanValue = toInteger(booleanValue);
    }

    public void setIntegerValue(Integer integerValue) {
        if (getAttribute().getValueType() != ValueType.INTEGER) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.INTEGER,
                                                                  getAttribute().getValueType()));
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
        if (getAttribute().getValueType() != ValueType.NUMERIC) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.NUMERIC,
                                                                  getAttribute().getValueType()));
        }
        this.numericValue = numericValue;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTextValue(String textValue) {
        if (getAttribute().getValueType() != ValueType.TEXT) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.TEXT,
                                                                  getAttribute().getValueType()));
        }
        this.textValue = textValue;
    }

    public void setTimestampValue(Timestamp timestampValue) {
        if (getAttribute().getValueType() != ValueType.TIMESTAMP) {
            throw new UnsupportedOperationException(
                                                    String.format("May not set %s value for a %s attribute",
                                                                  ValueType.TIMESTAMP,
                                                                  getAttribute().getValueType()));
        }
        this.timestampValue = timestampValue;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setValue(Object value) {
        switch (getAttribute().getValueType()) {
            case BINARY:
                setBinaryValue((byte[]) value);
                return;
            case BOOLEAN:
                setBooleanValue((Boolean) value);
                return;
            case INTEGER:
                setIntegerValue((Integer) value);
                return;
            case NUMERIC:
                setNumericValue((BigDecimal) value);
                return;
            case TEXT:
                setTextValue((String) value);
                return;
            case TIMESTAMP:
                setTimestampValue((Timestamp) value);
                return;
            default:
                throw new IllegalStateException(
                                                String.format("Invalid value type: %s",
                                                              getAttribute().getValueType()));
        }
    }

    public void setValueFromString(String value) {
        switch (getAttribute().getValueType()) {
            case BINARY:
                setBinaryValue(value.getBytes());
                return;
            case BOOLEAN:
                setBooleanValue(Boolean.valueOf(value));
                return;
            case INTEGER:
                setIntegerValue(Integer.parseInt(value));
                return;
            case NUMERIC:
                setNumericValue(BigDecimal.valueOf(Long.parseLong(value)));
                return;
            case TEXT:
                setTextValue(value);
                return;
            case TIMESTAMP:
                throw new UnsupportedOperationException("Timestamps are a PITA");
            default:
                throw new IllegalStateException(
                                                String.format("Invalid value type: %s",
                                                              getAttribute().getValueType()));
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]: %s", getClass().getSimpleName(),
                             getAttribute().getName(), getValue());
    }
}
