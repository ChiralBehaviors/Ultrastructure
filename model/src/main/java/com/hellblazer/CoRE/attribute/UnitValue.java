/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.attribute;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.product.Product;

/**
 * The value of a unit.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "unit_values", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_value_id_seq", sequenceName = "unit_value_id_seq")
public class UnitValue extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Column(name = "boolean_value")
    private Boolean           booleanValue;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    @Id
    @GeneratedValue(generator = "unit_value_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    @Column(name = "integer_value")
    private Integer           integerValue;

    private String            notes;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    @Column(name = "text_value")
    private String            textValue;

    //bi-directional many-to-one association to Unit
    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit              unit;

    public UnitValue() {
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public Product getEntityValue() {
        return productValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    @Override
    public String getNotes() {
        return notes;
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

    public Unit getUnit() {
        return unit;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setEntityValue(Product product) {
        productValue = product;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
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

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (productValue != null) productValue = (Product) productValue.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}

}