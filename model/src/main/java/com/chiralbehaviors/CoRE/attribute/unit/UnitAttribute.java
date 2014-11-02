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
package com.chiralbehaviors.CoRE.attribute.unit;

import static com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute.GET_ATTRIBUTE;

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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_attribute", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from UnitAttribute ra where ra.unitRf = :unit and ra.attribute = :attribute") })
public class UnitAttribute extends AttributeValue<Unit> {
    public static final String GET_ATTRIBUTE    = "unitAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Unit
    @ManyToOne(fetch = FetchType.LAZY, cascade ={ CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "unit_rf")
    private Unit               unitRf;

    public UnitAttribute() {
        super();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, UnitAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitAttribute;
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

    public Unit getUnitRf() {
        return unitRf;
    }

    public void setUnitRf(Unit unit) {
        unitRf = unit;
    }
}
