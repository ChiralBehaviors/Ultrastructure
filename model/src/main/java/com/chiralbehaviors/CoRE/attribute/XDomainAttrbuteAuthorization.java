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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * The abstract authorization for attributes on the authorization network
 * between two existential entities.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class XDomainAttrbuteAuthorization<From extends ExistentialRuleform<From, ? extends NetworkRuleform<From>>, To extends ExistentialRuleform<To, ? extends NetworkRuleform<To>>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_attribute")
    private Attribute authorizedAttribute;

    @Column(name = "binary_value")
    private byte[] binaryValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "grouping_agency")
    private Agency groupingAgency;

    @Column(name = "integer_value")
    private Integer integerValue;

    @Column(name = "numeric_value")
    private BigDecimal numericValue;

    @Column(name = "text_value")
    private String textValue;

    @Column(name = "timestamp_value")
    private Timestamp timestampValue;

    public XDomainAttrbuteAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public XDomainAttrbuteAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public XDomainAttrbuteAuthorization(Attribute authorized,
                                        Agency updatedBy) {
        super(updatedBy);
        authorizedAttribute = authorized;
    }

    /**
     * @param id
     */
    public XDomainAttrbuteAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public XDomainAttrbuteAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    @JsonGetter
    public Attribute getAuthorizedAttribute() {
        return authorizedAttribute;
    }

    /**
     * @return the binaryValue
     */
    @JsonIgnore
    public byte[] getBinaryValue() {
        return binaryValue;
    }

    /**
     * @return the booleanValue
     */
    @JsonIgnore
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    @JsonGetter
    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    @JsonIgnore
    public Integer getIntegerValue() {
        return integerValue;
    }

    @JsonGetter
    abstract public XDomainNetworkAuthorization<From, To> getNetworkAuthorization();

    @JsonIgnore
    public BigDecimal getNumericValue() {
        return numericValue;
    }

    @JsonIgnore
    public String getTextValue() {
        return textValue;
    }

    @JsonIgnore
    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    @SuppressWarnings("unchecked")
    @JsonGetter
    public <T> T getValue() {
        switch (getAuthorizedAttribute().getValueType()) {
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
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              getAuthorizedAttribute().getValueType()));
        }
    }

    public void setAuthorizedAttribute(Attribute productAttributeType3) {
        authorizedAttribute = productAttributeType3;
    }

    /**
     * @param binaryValue
     *            the binaryValue to set
     */
    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    /**
     * @param booleanValue
     *            the booleanValue to set
     */
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setGroupingAgency(Agency agency) {
        groupingAgency = agency;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    abstract public <T extends XDomainNetworkAuthorization<From, To>> void setNetworkAuthorization(T auth);

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @param timestampValue
     *            the timestampValue to set
     */
    public void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

    public void setValue(Object value) {
        switch (getAuthorizedAttribute().getValueType()) {
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
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              getAuthorizedAttribute().getValueType()));
        }
    }
}
