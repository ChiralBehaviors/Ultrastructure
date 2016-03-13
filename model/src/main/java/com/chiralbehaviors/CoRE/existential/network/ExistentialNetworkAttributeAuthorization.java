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
package com.chiralbehaviors.CoRE.existential.network;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.MetaValue;
import org.hibernate.annotations.Type;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.attribute.ValueType;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Interval;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;
import com.chiralbehaviors.CoRE.existential.domain.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * The abstract authorization for attributes on network edges.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "existential_network_attribute", schema = "ruleform")
public class ExistentialNetworkAttributeAuthorization<P extends ExistentialRuleform<P>, C extends ExistentialRuleform<C>>
        extends Ruleform {

    private static final long        serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute")
    private Attribute                authorizedAttribute;

    @Column(name = "binary_value")
    private byte[]                   binaryValue;

    @Column(name = "boolean_value")
    private Boolean                  booleanValue;

    @Any(metaColumn = @Column(name = "domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "edge")
    private ExistentialNetwork<P, C> edge;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency                   groupingAgency;

    @Column(name = "integer_value")
    private Integer                  integerValue;

    @Column(name = "json_value")
    @Type(type = "jsonbType")
    private Object                   jsonValue;

    @Column(name = "numeric_value")
    private BigDecimal               numericValue;

    @Column(name = "text_value")
    private String                   textValue;

    @Column(name = "timestamp_value")
    private Timestamp                timestampValue;

    public ExistentialNetworkAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ExistentialNetworkAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ExistentialNetworkAttributeAuthorization(Attribute authorized,
                                                    Agency updatedBy) {
        super(updatedBy);
        authorizedAttribute = authorized;
    }

    /**
     * @param id
     */
    public ExistentialNetworkAttributeAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ExistentialNetworkAttributeAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    @JsonGetter
    public Attribute getAuthorizedAttribute() {
        return authorizedAttribute;
    }

    public ExistentialNetwork<P, C> getEdge() {
        return edge;
    }

    @JsonGetter
    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    @SuppressWarnings("unchecked")
    @JsonGetter
    public <T> T getValue() {
        switch (authorizedAttribute.getValueType()) {
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
                                                              authorizedAttribute.getValueType()));
        }
    }

    @JsonIgnore
    public ValueType getValueType() {
        return authorizedAttribute.getValueType();
    }

    public void setAuthorizedAttribute(Attribute productAttributeType3) {
        authorizedAttribute = productAttributeType3;
    }

    public void setEdge(ExistentialNetwork<P, C> edge) {
        this.edge = edge;
    }

    public void setGroupingAgency(Agency agency) {
        groupingAgency = agency;
    }

    public void setValue(Object value) {
        switch (authorizedAttribute.getValueType()) {
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
            case JSON:
                setJsonValue(value);
                return;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              authorizedAttribute.getValueType()));
        }
    }

    public void setValueFromString(String value) {
        switch (getValueType()) {
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
                                                              getValueType()));
        }
    }

    /**
     * @return the binaryValue
     */
    @JsonIgnore
    private byte[] getBinaryValue() {
        return binaryValue;
    }

    /**
     * @return the booleanValue
     */
    @JsonIgnore
    private Boolean getBooleanValue() {
        return booleanValue;
    }

    @JsonIgnore
    private Integer getIntegerValue() {
        return integerValue;
    }

    @JsonIgnore
    private Object getJsonValue() {
        return jsonValue;
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

    /**
     * @param binaryValue
     *            the binaryValue to set
     */
    private void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    /**
     * @param booleanValue
     *            the booleanValue to set
     */
    private void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    private void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    private void setJsonValue(Object jsonValue) {
        this.jsonValue = jsonValue;
    }

    private void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    private void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @param timestampValue
     *            the timestampValue to set
     */
    private void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }
}
