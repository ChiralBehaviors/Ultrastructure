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
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;

/**
 * 
 * The abstract authorization for attributes on entities.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class AttributeAuthorization extends Ruleform {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_attribute")
    private Attribute         authorizedAttribute;

    @Column(name = "binary_value")
    private byte[]            binaryValue;

    @Column(name = "boolean_value")
    private Integer           booleanValue;

    // bi-directional many-to-one association to Agency
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

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

    public AttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public AttributeAuthorization(Attribute authorized, Agency updatedBy) {
        super(updatedBy);
        authorizedAttribute = authorized;
    }

    /**
     * @param id
     */
    public AttributeAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public AttributeAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Attribute getAuthorizedAttribute() {
        return authorizedAttribute;
    }

    /**
     * @return the binaryValue
     */
    public byte[] getBinaryValue() {
        return binaryValue;
    }

    /**
     * @return the booleanValue
     */
    public Integer getBooleanValue() {
        return booleanValue;
    }

    public Agency getGroupingAgency() {
        return groupingAgency;
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

    public Timestamp getTimestampValue() {
        return timestampValue;
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
    public void setBooleanValue(Integer booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setGroupingAgency(Agency agency) {
        groupingAgency = agency;
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

    /**
     * @param timestampValue
     *            the timestampValue to set
     */
    public void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (authorizedAttribute != null) {
            authorizedAttribute = (Attribute) authorizedAttribute.manageEntity(em,
                                                                               knownObjects);
        }
        if (groupingAgency != null) {
            groupingAgency = (Agency) groupingAgency.manageEntity(em,
                                                                  knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
