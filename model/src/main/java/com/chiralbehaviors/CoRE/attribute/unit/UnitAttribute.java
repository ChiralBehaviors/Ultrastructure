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
package com.chiralbehaviors.CoRE.attribute.unit;

import static com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute.GET_ATTRIBUTE;

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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_attribute", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "SELECT ra FROM UnitAttribute ra "
                                                          + "WHERE ra.unitRf = :ruleform "
                                                          + "AND ra.attribute = :attribute "
                                                          + "ORDER BY ra.sequenceNumber") })
public class UnitAttribute extends AttributeValue<Unit> {
    public static final String GET_ATTRIBUTE    = "unitAttribute"
                                                  + GET_ATTRIBUTE_SUFFIX;
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    // bi-directional many-to-one association to Unit
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "unit_rf")
    private Unit               unitRf;

    public UnitAttribute() {
        super();
    }

    public UnitAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public UnitAttribute(Attribute attribute) {
        super(attribute);
    }

    public UnitAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public UnitAttribute(Attribute attribute, BigDecimal value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public UnitAttribute(Attribute attribute, boolean value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public UnitAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public UnitAttribute(Attribute attribute, String value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public UnitAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public UnitAttribute(UUID id) {
        super(id);
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Unit>, Unit> getRuleformAttribute() {
        return UnitAttribute_.unitRf;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Unit> getRuleformClass() {
        return Unit.class;
    }

    @JsonGetter
    public Unit getUnitRf() {
        return unitRf;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, UnitAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitAttribute;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setUnitRf(Unit unit) {
        unitRf = unit;
    }
}
