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

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The value of a unit.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "unit_values", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_value_id_seq", sequenceName = "unit_value_id_seq")
public class UnitValue extends Ruleform {
	private static final long serialVersionUID = 1L;

	@Column(name = "boolean_value")
	private Integer booleanValue;

	@Id
	@GeneratedValue(generator = "unit_value_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "integer_value")
	private Integer integerValue;

	private String notes;

	@Column(name = "numeric_value")
	private BigDecimal numericValue;

	// bi-directional many-to-one association to Product
	@ManyToOne
	@JoinColumn(name = "product_value")
	private Product productValue;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@Column(name = "text_value")
	private String textValue;

	// bi-directional many-to-one association to Unit
	@ManyToOne
	@JoinColumn(name = "unit")
	private Unit unit;

	public UnitValue() {
	}

	public Boolean getBooleanValue() {
		return toBoolean(booleanValue);
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
		this.booleanValue = toInteger(booleanValue);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
	 * .EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (productValue != null) {
			productValue = (Product) productValue
					.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}

}