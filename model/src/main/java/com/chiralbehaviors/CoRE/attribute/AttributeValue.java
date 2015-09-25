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
package com.chiralbehaviors.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The abstract attribute value.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
public abstract class AttributeValue<RuleForm extends Ruleform>
        extends Ruleform {
    public static final String GET_ATTRIBUTE_SUFFIX = ".getAttribute";

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute")
    private Attribute attribute;

    @Column(name = "binary_value")
    private byte[] binaryValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Column(name = "integer_value")
    private Integer integerValue;

    @Column(name = "key")
    private String key;

    @Column(name = "numeric_value")
    private BigDecimal numericValue;

    @Column(name = "sequence_number")
    private Integer sequenceNumber = 0;

    @Column(name = "text_value")
    private String textValue;

    @Column(name = "timestamp_value")
    private Timestamp timestampValue;

    @Column(name = "json_value")
    @Type(type = "jsonbType")
    private Object jsonValue;

    // bi-directional many-to-one association to Unit
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit")
    private Unit unit;

    @Column(name = "updated")
    private Timestamp updated = new Timestamp(System.currentTimeMillis());

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
        setValue(value);
    }

    public AttributeValue(Attribute attribute, Boolean value,
                          Agency updatedBy) {
        this(attribute, updatedBy);
        setValue(value);
    }

    public AttributeValue(Attribute attribute, int value, Agency updatedBy) {
        this(attribute, updatedBy);
        setValue(value);
    }

    public AttributeValue(Attribute attribute, String value, Agency updatedBy) {
        this(attribute, updatedBy);
        setValue(value);
    }

    public AttributeValue(Attribute attribute, Unit unit) {
        this(attribute);
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

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    @JsonIgnore
    abstract public SingularAttribute<? extends AttributeValue<RuleForm>, RuleForm> getRuleformAttribute();

    /**
     * This method exists because generics is not a runtime capability.
     *
     * @return the concrete class that this attribute is associated with
     */
    @JsonIgnore
    abstract public Class<RuleForm> getRuleformClass();

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Unit getUnit() {
        return unit;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty
    public <T extends Object> T getValue() {
        if (attribute == null) {
            return null; // For frontier traversal on workspace serialization
        }
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
            case JSON:
                return (T) getJsonValue();
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    public final void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public void setValue(Object value) {
        switch (getAttribute().getValueType()) {
            case BINARY:
                setBinaryValue((byte[]) value);
                break;
            case BOOLEAN:
                setBooleanValue((Boolean) value);
                break;
            case INTEGER:
                setIntegerValue((Integer) value);
                break;
            case NUMERIC:
                setNumericValue((BigDecimal) value);
                break;
            case TEXT:
                setTextValue((String) value);
                return;
            case TIMESTAMP:
                setTimestampValue((Timestamp) value);
                break;
            case JSON:
                setJsonValue(value);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              getAttribute().getValueType()));
        }
        setUpdated(new Timestamp(System.currentTimeMillis()));
    }

    public void setValueFromString(String value) {
        switch (getAttribute().getValueType()) {
            case BINARY:
                setValue(value.getBytes());
                return;
            case BOOLEAN:
                setValue(Boolean.valueOf(value));
                return;
            case INTEGER:
                setValue(Integer.parseInt(value));
                return;
            case NUMERIC:
                setValue(BigDecimal.valueOf(Long.parseLong(value)));
                return;
            case TEXT:
            case JSON:
                setValue(value);
                return;
            case TIMESTAMP:
                throw new UnsupportedOperationException("Timestamps are a PITA");
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              getAttribute().getValueType()));
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]: %s", getClass().getSimpleName(),
                             getAttribute().getName(), getValue());
    }

    @JsonIgnore
    private byte[] getBinaryValue() {
        return binaryValue;
    }

    @JsonIgnore
    private Boolean getBooleanValue() {
        return booleanValue;
    }

    @JsonIgnore
    private Integer getIntegerValue() {
        return integerValue;
    }

    @JsonIgnore
    private BigDecimal getNumericValue() {
        return numericValue;
    }

    @JsonIgnore
    private String getTextValue() {
        return textValue;
    }

    @JsonIgnore
    private Timestamp getTimestampValue() {
        return timestampValue;
    }

    private void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    private void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    private void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    private void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    private void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    private void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

    private Object getJsonValue() {
        return jsonValue;
    }

    private void setJsonValue(Object jsonValue) {
        this.jsonValue = jsonValue;
    }
}
