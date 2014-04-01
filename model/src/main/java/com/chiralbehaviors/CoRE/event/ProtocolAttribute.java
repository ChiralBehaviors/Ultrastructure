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
package com.chiralbehaviors.CoRE.event;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;

/**
 * The persistent class for the protocol_attribute database table.
 * 
 */
@Entity
@Table(name = "protocol_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_id_seq", sequenceName = "protocol_attribute_id_seq")
public class ProtocolAttribute extends AttributeValue<Protocol> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "protocol_attribute_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	// bi-directional many-to-one association to Protocol
	@ManyToOne
	@JoinColumn(name = "protocol")
	private Protocol protocol;

	public ProtocolAttribute() {
	}

	/**
	 * @param updatedBy
	 */
	public ProtocolAttribute(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param attribute
	 */
	public ProtocolAttribute(Attribute attribute) {
		super(attribute);
	}

	/**
	 * @param attribute
	 * @param updatedBy
	 */
	public ProtocolAttribute(Attribute attribute, Agency updatedBy) {
		super(attribute, updatedBy);
	}

	/**
	 * @param attribute
	 * @param value
	 * @param updatedBy
	 */
	public ProtocolAttribute(Attribute attribute, BigDecimal value,
			Agency updatedBy) {
		super(attribute, value, updatedBy);
	}

	/**
	 * @param attribute
	 * @param value
	 * @param updatedBy
	 */
	public ProtocolAttribute(Attribute attribute, boolean value,
			Agency updatedBy) {
		super(attribute, value, updatedBy);
	}

	/**
	 * @param attribute
	 * @param value
	 * @param updatedBy
	 */
	public ProtocolAttribute(Attribute attribute, int value, Agency updatedBy) {
		super(attribute, value, updatedBy);
	}

	/**
	 * @param attribute
	 * @param value
	 * @param updatedBy
	 */
	public ProtocolAttribute(Attribute attribute, String value, Agency updatedBy) {
		super(attribute, value, updatedBy);
	}

	/**
	 * @param attribute
	 * @param unit
	 */
	public ProtocolAttribute(Attribute attribute, Unit unit) {
		super(attribute, unit);
	}

	/**
	 * @param id
	 */
	public ProtocolAttribute(Long id) {
		super(id);
	}

	/**
	 * @return a new job attribute cloned from the receiver
	 */
	public JobAttribute createJobAttribute() {
		JobAttribute clone = new JobAttribute();
		clone.setAttribute(getAttribute());
		copyInto(clone);
		return clone;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
	 */
	@Override
	public SingularAttribute<? extends AttributeValue<Protocol>, Protocol> getRuleformAttribute() {
		return ProtocolAttribute_.protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
	 */
	@Override
	public Class<Protocol> getRuleformClass() {
		return Protocol.class;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setNumericValue(double value) {
		setNumericValue(BigDecimal.valueOf(value));
	}

	public void setNumericValue(float value) {
		setNumericValue(BigDecimal.valueOf(value));
	}

	public void setNumericValue(int value) {
		setNumericValue(BigDecimal.valueOf(value));
	}

	public void setNumericValue(long value) {
		setNumericValue(BigDecimal.valueOf(value));
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
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
		if (protocol != null) {
			protocol = (Protocol) protocol.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}