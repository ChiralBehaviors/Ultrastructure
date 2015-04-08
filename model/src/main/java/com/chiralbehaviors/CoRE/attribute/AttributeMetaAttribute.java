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

import static com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "SELECT ra FROM AttributeMetaAttribute ra WHERE ra.attribute = :ruleform AND ra.metaAttribute = :attribute ORDER BY ra.sequenceNumber") })
@Entity
@Table(name = "attribute_meta_attribute", schema = "ruleform")
public class AttributeMetaAttribute extends AttributeValue<Attribute> {
    private static final long  serialVersionUID = 1L;
    public static final String GET_ATTRIBUTE    = "attributeMetaAttribute"
                                                  + GET_ATTRIBUTE_SUFFIX;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "meta_attribute")
    private Attribute          metaAttribute;

    public AttributeMetaAttribute() {
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
        setAttribute(attribute);
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
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonGetter
    public Attribute getMetaAttribute() {
        return metaAttribute;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Attribute>, Attribute> getRuleformAttribute() {
        return AttributeMetaAttribute_.metaAttribute;
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeMetaAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeMetaAttribute;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setMetaAttribute(Attribute attribute3) {
        metaAttribute = attribute3;
    }
}