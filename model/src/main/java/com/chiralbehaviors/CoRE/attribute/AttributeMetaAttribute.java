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

import static com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * An attribute value on an attribute
 *
 */
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "SELECT ra FROM AttributeMetaAttribute ra "
                                                          + "WHERE ra.attribute = :attribute "
                                                          + "AND ra.metaAttribute = :ruleform "
                                                          + "ORDER BY ra.sequenceNumber") })
@Entity
@Table(name = "attribute_meta_attribute", schema = "ruleform")
public class AttributeMetaAttribute extends AttributeValue<Attribute> {
    public static final String GET_ATTRIBUTE    = "attributeMetaAttribute"
                                                  + GET_ATTRIBUTE_SUFFIX;
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_attribute")
    @NotNull
    private Attribute metaAttribute;

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
     * @param attribute
     * @param updatedBy
     */
    public AttributeMetaAttribute(Attribute attribute, Attribute metaAttribute,
                                  Agency updatedBy) {
        super(metaAttribute, updatedBy);
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

    public void setMetaAttribute(Attribute attribute3) {
        metaAttribute = attribute3;
    }
}