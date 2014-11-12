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
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An attribute value on an attribute
 *
 */
@Entity
@Table(name = "attribute_meta_attribute", schema = "ruleform")
public class AttributeMetaAttribute extends AttributeValue<Attribute> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "attribute_value")
    private Attribute         attributeValue;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "meta_attribute")
    private Attribute         metaAttribute;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    public AttributeMetaAttribute() {
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeMetaAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeMetaAttribute;
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttribute(Attribute attribute, Agency updatedBy) {
        super(updatedBy);
        this.attribute = attribute;
    }

    public AttributeMetaAttribute(Attribute attribute, BigDecimal value,
                                  Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, boolean value,
                                  Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, int value,
                                  Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, String value,
                                  Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public AttributeMetaAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public AttributeMetaAttribute(UUID id) {
        super(id);
    }

    @Override
    @JsonGetter
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonGetter
    public Attribute getAttributeValue() {
        return attributeValue;
    }

    @JsonGetter
    public Attribute getMetaAttribute() {
        return metaAttribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<AttributeMetaAttribute, Attribute> getRuleformAttribute() {
        return AttributeMetaAttribute_.attribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Attribute> getRuleformClass() {
        return Attribute.class;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public void setAttribute(Attribute attribute1) {
        attribute = attribute1;
    }

    public void setAttributeValue(Attribute attribute2) {
        attributeValue = attribute2;
    }

    public void setMetaAttribute(Attribute attribute3) {
        metaAttribute = attribute3;
    }

    @Override
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}