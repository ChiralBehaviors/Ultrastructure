/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.attribute.unit;

import static com.hellblazer.CoRE.attribute.unit.UnitAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "unit_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_attribute_id_seq", sequenceName = "unit_attribute_id_seq")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from UnitAttribute ra where ra.unitRf = :unit and ra.attribute = :attribute") })
public class UnitAttribute extends AttributeValue<Unit> {
    public static final String GET_ATTRIBUTE    = "unitAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "unit_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Unit
    @ManyToOne
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

    public UnitAttribute(Long id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Unit>, Unit> getRuleformAttribute() {
        return UnitAttribute_.unitRf;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Unit> getRuleformClass() {
        return Unit.class;
    }

    public Unit getUnitRf() {
        return unitRf;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setUnitRf(Unit unit) {
        unitRf = unit;
    }
}
