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
package com.chiralbehaviors.CoRE.attribute;

import static com.chiralbehaviors.CoRE.attribute.TransformationMetarule.GET_BY_EVENT;

import java.io.Serializable;
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
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The persistent class for the transformation_metarule database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = GET_BY_EVENT, query = "SELECT tm FROM TransformationMetarule tm "
		+ "WHERE tm.service = :service " + "ORDER BY tm.sequenceNumber") })
@Entity
@Table(name = "transformation_metarule", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transformation_metarule_id_seq", sequenceName = "transformation_metarule_id_seq")
public class TransformationMetarule extends Ruleform implements Serializable {
	public static final String GET_BY_EVENT = "tranformationMetarule.getByEvent";
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "transformation_metarule_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	// bi-directional many-to-one association to Relationship
	@ManyToOne
	@JoinColumn(name = "product_map")
	private Relationship productMap;

	// bi-directional many-to-one association to Agency
	@ManyToOne
	@JoinColumn(name = "product_network_agency")
	private Agency productNetworkAgency;

	// bi-directional many-to-one association to Relationship
	@ManyToOne
	@JoinColumn(name = "agency_map")
	private Relationship relationshipMap;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	/**
	 * The service performed
	 */
	@ManyToOne
	@JoinColumn(name = "service")
	private Product service;

	@Column(name = "stop_on_match")
	private Integer stopOnMatch;

	public TransformationMetarule() {
	}

	public Agency getEntityNetworkAgency() {
		return productNetworkAgency;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Relationship getProductMap() {
		return productMap;
	}

	public Relationship getRelationshipMap() {
		return relationshipMap;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @return the service
	 */
	public Product getService() {
		return service;
	}

	public Boolean getStopOnMatch() {
		return toBoolean(stopOnMatch);
	}

	public void setEntityMap(Relationship relationship2) {
		productMap = relationship2;
	}

	public void setEntityNetworkAgency(Agency agency) {
		productNetworkAgency = agency;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setRelationshipMap(Relationship relationship1) {
		relationshipMap = relationship1;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Product service) {
		this.service = service;
	}

	public void setStopOnMatch(Boolean stopOnMatch) {
		this.stopOnMatch = toInteger(stopOnMatch);
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
		if (productMap != null) {
			productMap = (Relationship) productMap.manageEntity(em,
					knownObjects);
		}
		if (productNetworkAgency != null) {
			productNetworkAgency = (Agency) productNetworkAgency.manageEntity(
					em, knownObjects);
		}
		if (relationshipMap != null) {
			relationshipMap = (Relationship) relationshipMap.manageEntity(em,
					knownObjects);
		}
		if (service != null) {
			service = (Product) service.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}

}