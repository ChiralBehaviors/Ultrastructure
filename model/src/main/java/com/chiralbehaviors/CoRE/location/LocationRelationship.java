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
package com.chiralbehaviors.CoRE.location;

import static com.chiralbehaviors.CoRE.location.LocationRelationship.RULES;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The persistent class for the location_relationship database table.
 * 
 */
@Entity
@Table(name = "location_relationship", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_relationship_id_seq", sequenceName = "location_relationship_id_seq")
@NamedQueries({ @NamedQuery(name = RULES, query = "select lr FROM LocationRelationship AS lr "
		+ "WHERE lr.relationship = :relationship "
		+ "AND lr.productMappedValue = :mappedEntityValue "
		+ "ORDER BY lr.sequenceNumber") })
public class LocationRelationship extends Ruleform {
	public static final String AVAILABLE_RELATIONSHIPS = "locationRelationship.availableRelationships";
	public static final String FIND_BY_ID = "locationRelationship.findById";
	public static final String FIND_BY_NAME = "locationRelationship.findByName";
	public static final String RULES = "locationRelationship.rules";
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Relationship
	@ManyToOne
	@JoinColumn(name = "attribute_relationship")
	private Relationship attributeRelationship;

	@Id
	@GeneratedValue(generator = "location_relationship_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	// bi-directional many-to-one association to Attribute
	@ManyToOne
	@JoinColumn(name = "location_1_attribute")
	private Attribute location1Attribute;

	// bi-directional many-to-one association to Attribute
	@ManyToOne
	@JoinColumn(name = "location_2_attribute")
	private Attribute location2Attribute;

	// bi-directional many-to-one association to Product
	@ManyToOne
	@JoinColumn(name = "product_mapped_value")
	private Product productMappedValue;

	// bi-directional many-to-one association to Relationship
	@ManyToOne
	@JoinColumn(name = "relationship")
	private Relationship relationship;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	public LocationRelationship() {
	}

	/**
	 * @param updatedBy
	 */
	public LocationRelationship(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param id
	 */
	public LocationRelationship(Long id) {
		super(id);
	}

	public Relationship getAttributeRelationship() {
		return attributeRelationship;
	}

	public Product getEntityMappedValue() {
		return productMappedValue;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Attribute getLocation1Attribute() {
		return location1Attribute;
	}

	public Attribute getLocation2Attribute() {
		return location2Attribute;
	}

	public Relationship getRelationship() {
		return relationship;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setAttributeRelationship(Relationship relationship1) {
		attributeRelationship = relationship1;
	}

	public void setEntityMappedValue(Product product) {
		productMappedValue = product;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setLocation1Attribute(Attribute attribute1) {
		location1Attribute = attribute1;
	}

	public void setLocation2Attribute(Attribute attribute2) {
		location2Attribute = attribute2;
	}

	public void setRelationship(Relationship relationship2) {
		relationship = relationship2;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
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
		if (attributeRelationship != null) {
			attributeRelationship = (Relationship) attributeRelationship
					.manageEntity(em, knownObjects);
		}
		if (productMappedValue != null) {
			productMappedValue = (Product) productMappedValue.manageEntity(em,
					knownObjects);
		}
		if (location1Attribute != null) {
			location1Attribute = (Attribute) location1Attribute.manageEntity(
					em, knownObjects);
		}
		if (location2Attribute != null) {
			location2Attribute = (Attribute) location2Attribute.manageEntity(
					em, knownObjects);
		}
		if (relationship != null) {
			relationship = (Relationship) relationship.manageEntity(em,
					knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}