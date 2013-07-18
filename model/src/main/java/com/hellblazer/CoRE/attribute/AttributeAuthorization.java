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
import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * 
 * The abstract authorization for attributes on entities.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class AttributeAuthorization extends Ruleform {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "authorized_attribute")
	private Attribute authorizedAttribute;

	// bi-directional many-to-one association to Resource
	@ManyToOne
	@JoinColumn(name = "grouping_resource")
	private Resource groupingResource;

	@Column(name = "integer_value")
	private Integer integerValue;

	@Column(name = "numeric_value")
	private BigDecimal numericValue;

	@Column(name = "sequence_number")
	private Integer sequenceNumber = 1;

	@Column(name = "text_value")
	private String textValue;

	@Column(name = "timestamp_value")
	private Timestamp timestampValue;

	public AttributeAuthorization() {
		super();
	}

	public AttributeAuthorization(Attribute authorized, Resource updatedBy) {
		super(updatedBy);
		authorizedAttribute = authorized;
	}

	/**
	 * @param id
	 */
	public AttributeAuthorization(Long id) {
		super(id);
	}

	/**
	 * @param updatedBy
	 */
	public AttributeAuthorization(Long id, Resource updatedBy) {
		super(id, updatedBy);
	}

	/**
	 * @param updatedBy
	 */
	public AttributeAuthorization(Resource updatedBy) {
		super(updatedBy);
	}

	public Attribute getAuthorizedAttribute() {
		return authorizedAttribute;
	}

	public Resource getGroupingResource() {
		return groupingResource;
	}

	public Integer getIntegerValue() {
		return integerValue;
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

	public Timestamp getTimestampValue() {
		return timestampValue;
	}

	public void setAuthorizedAttribute(Attribute productAttributeType3) {
		authorizedAttribute = productAttributeType3;
	}

	public void setGroupingResource(Resource resource) {
		groupingResource = resource;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
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

	/**
	 * @param timestampValue
	 *            the timestampValue to set
	 */
	public void setTimestampValue(Timestamp timestampValue) {
		this.timestampValue = timestampValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
	 * EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (authorizedAttribute != null) {
			authorizedAttribute = (Attribute) authorizedAttribute.manageEntity(
					em, knownObjects);
		}
		if (groupingResource != null) {
			groupingResource = (Resource) groupingResource.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}
