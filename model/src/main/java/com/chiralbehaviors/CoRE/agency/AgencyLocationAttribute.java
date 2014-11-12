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
package com.chiralbehaviors.CoRE.agency;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The attribute value for product location attributes
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_location_attribute", schema = "ruleform")
public class AgencyLocationAttribute extends AttributeValue<AgencyLocation> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency            agency;

    // bi-directional many-to-one association to AgencyLocation
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "agency_location")
    private AgencyLocation    agencyLocation;

    public AgencyLocationAttribute() {
    }

    /**
     * @param updatedBy
     */
    public AgencyLocationAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     */
    public AgencyLocationAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public AgencyLocationAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public AgencyLocationAttribute(Attribute attribute, BigDecimal value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public AgencyLocationAttribute(Attribute attribute, boolean value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public AgencyLocationAttribute(Attribute attribute, int value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public AgencyLocationAttribute(Attribute attribute, String value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public AgencyLocationAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public AgencyLocationAttribute(UUID id) {
        super(id);
    }

    @JsonGetter
    public Agency getAgency() {
        return agency;
    }

    @JsonGetter
    public AgencyLocation getEntityLocation() {
        return agencyLocation;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<AgencyLocationAttribute, AgencyLocation> getRuleformAttribute() {
        return AgencyLocationAttribute_.agencyLocation;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<AgencyLocation> getRuleformClass() {
        return AgencyLocation.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AgencyLocationAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.agencyLocationAttribute;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    public void setEntityLocation(AgencyLocation AgencyLocation) {
        agencyLocation = AgencyLocation;
    }
}