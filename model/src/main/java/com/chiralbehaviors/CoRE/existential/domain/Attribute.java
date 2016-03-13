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
package com.chiralbehaviors.CoRE.existential.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.attribute.ValueType;

/**
 * Existential ruleform for all attributes in the CoRE database. This table
 * defines and describes all attributes.
 *
 * @author hhildebrand
 *
 */
@Entity
@DiscriminatorValue("T")
@Table(name = "existential", schema = "ruleform")
public class Attribute extends ExistentialRuleform<Attribute> {
    public Attribute(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    private static final long serialVersionUID = 1L;

    @Column(name = "indexed")
    private boolean           indexed          = false;

    @Column(name = "keyed")
    private boolean           keyed            = false;

    @Column(name = "value_type")
    @Enumerated(EnumType.ORDINAL)
    private ValueType         valueType;

    public Attribute() {
    }

    public Attribute(String name) {
        super(name);
    }

    public Attribute(String name, String description) {
        super(name, description);
    }

    public Attribute(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param string
     * @param string2
     * @param json
     * @param core
     */
    public Attribute(String name, String description, ValueType type,
                     Agency updatedBy) {
        this(name, description, updatedBy);
        this.valueType = type;
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getAnyId()
     */
    @Override
    public UUID getAnyId() {
        return WellKnownAttribute.ANY.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownAttribute.COPY.id();
    }

    public boolean getIndexed() {
        return indexed;
    }

    public boolean getKeyed() {
        return keyed;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownAttribute.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownAttribute.SAME.id();
    }

    public ValueType getValueType() {
        return valueType;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownAttribute.ANY.id()
                                     .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownAttribute.ANY.id()
                                     .equals(getId())
               || WellKnownAttribute.SAME.id()
                                         .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownAttribute.COPY.id()
                                      .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownAttribute.NOT_APPLICABLE.id()
                                                .equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownAttribute.SAME.id()
                                      .equals(getId());
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public void setKeyed(boolean keyed) {
        this.keyed = keyed;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * @return
     */
    public Class<?> valueClass() {
        switch (valueType) {
            case BINARY:
                return byte[].class;
            case BOOLEAN:
                return Boolean.class;
            case INTEGER:
                return Integer.class;
            case NUMERIC:
                return BigDecimal.class;
            case TEXT:
                return String.class;
            case TIMESTAMP:
                return Timestamp.class;
            case JSON:
                return Map.class;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              valueType));
        }
    }
}