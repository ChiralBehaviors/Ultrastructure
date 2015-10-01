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
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

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

@NamedQueries({ @NamedQuery(name = "AgencyLocationAttributeAuthorization.checkCap", query = "SELECT COUNT(required.groupingAgency) FROM AgencyLocationAttributeAuthorization required "
                                                                                            + "  WHERE required.groupingAgency IS NOT NULL "
                                                                                            + "  AND required.networkAuthorization = :facet "
                                                                                            + "  AND required.authorizedAttribute = :attribute "
                                                                                            + "  AND NOT EXISTS( "
                                                                                            + "      SELECT required.groupingAgency from AgencyNetwork authorized "
                                                                                            + "         WHERE authorized.parent IN :agencies "
                                                                                            + "         AND authorized.relationship = :capability "
                                                                                            + "         AND authorized.child = required.groupingAgency "
                                                                                            + "  )"),
                @NamedQuery(name = "AgencyProductAttributeAuthorization.checkCap", query = "SELECT COUNT(required.groupingAgency) FROM AgencyProductAttributeAuthorization required "
                                                                                           + "  WHERE required.groupingAgency IS NOT NULL "
                                                                                           + "  AND required.networkAuthorization = :facet "
                                                                                           + "  AND required.authorizedAttribute = :attribute "
                                                                                           + "  AND NOT EXISTS( "
                                                                                           + "      SELECT required.groupingAgency from AgencyNetwork authorized "
                                                                                           + "         WHERE authorized.parent IN :agencies "
                                                                                           + "         AND authorized.relationship = :capability "
                                                                                           + "         AND authorized.child = required.groupingAgency "
                                                                                           + "  )"),
                @NamedQuery(name = "ProductLocationAttributeAuthorization.checkCap", query = "SELECT COUNT(required.groupingAgency) FROM ProductLocationAttributeAuthorization required "
                                                                                             + "  WHERE required.groupingAgency IS NOT NULL "
                                                                                             + "  AND required.networkAuthorization = :facet "
                                                                                             + "  AND required.authorizedAttribute = :attribute "
                                                                                             + "  AND NOT EXISTS( "
                                                                                             + "      SELECT required.groupingAgency from AgencyNetwork authorized "
                                                                                             + "         WHERE authorized.parent IN :agencies "
                                                                                             + "         AND authorized.relationship = :capability "
                                                                                             + "         AND authorized.child = required.groupingAgency "
                                                                                             + "  )"),
                @NamedQuery(name = "ProductRelationshipAttributeAuthorization.checkCap", query = "SELECT COUNT(required.groupingAgency) FROM ProductRelationshipAttributeAuthorization required "
                                                                                                 + "  WHERE required.groupingAgency IS NOT NULL "
                                                                                                 + "  AND required.networkAuthorization = :facet "
                                                                                                 + "  AND required.authorizedAttribute = :attribute "
                                                                                                 + "  AND NOT EXISTS( "
                                                                                                 + "      SELECT required.groupingAgency from AgencyNetwork authorized "
                                                                                                 + "         WHERE authorized.parent IN :agencies "
                                                                                                 + "         AND authorized.relationship = :capability "
                                                                                                 + "         AND authorized.child = required.groupingAgency "
                                                                                                 + "  )") })
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class XDomainAttrbuteAuthorization<From extends ExistentialRuleform<From, ? extends NetworkRuleform<From>>, To extends ExistentialRuleform<To, ? extends NetworkRuleform<To>>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "authorized_attribute")
    private Attribute authorizedAttribute;

    @Column(name = "binary_value")
    private byte[] binaryValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency groupingAgency;

    @Column(name = "integer_value")
    private Integer integerValue;

    @Column(name = "json_value")
    @Type(type = "jsonbType")
    private Object jsonValue;

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

    @JsonGetter
    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    @JsonGetter
    abstract public XDomainNetworkAuthorization<From, To> getNetworkAuthorization();

    @SuppressWarnings("unchecked")
    @JsonGetter
    public <T> T getValue() {
        if (authorizedAttribute == null) {
            return null; // For frontier traversal on workspace serialization
        }
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

    public void setAuthorizedAttribute(Attribute productAttributeType3) {
        authorizedAttribute = productAttributeType3;
    }

    public void setGroupingAgency(Agency agency) {
        groupingAgency = agency;
    }

    abstract public <T extends XDomainNetworkAuthorization<From, To>> void setNetworkAuthorization(T auth);

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
            case JSON:
                setJsonValue(value);
                return;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              getAuthorizedAttribute().getValueType()));
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
