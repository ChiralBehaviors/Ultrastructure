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

import static com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization.GET_SEQUENCES;
import static com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization.GET_SIBLING_ACTIONS;

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
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({
		@NamedQuery(name = GET_SIBLING_ACTIONS, query = "SELECT seq FROM ProductSiblingSequencingAuthorization AS seq "
				+ " WHERE seq.parent = :service"
				+ " AND seq.statusCode = :status "
				+ " ORDER BY seq.sequenceNumber"),
		@NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductSiblingSequencingAuthorization AS seq "
				+ " WHERE seq.parent = :service"
				+ " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_sibling_sequencing_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_sibling_sequencing_authorization_id_seq", sequenceName = "product_sibling_sequencing_authorization_id_seq")
public class ProductSiblingSequencingAuthorization extends Ruleform {
	public static final String GET_SIBLING_ACTIONS = "productSequencingAuthorization.getSiblingActions";
	public static final String GET_SEQUENCES = "productSequencingAuthorization.getSequences";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "product_sibling_sequencing_authorization_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "next_sibling")
	private Product nextSibling;

	@ManyToOne
	@JoinColumn(name = "next_sibling_status")
	private StatusCode nextSiblingStatus;

	@ManyToOne
	@JoinColumn(name = "parent")
	private Product parent;

	@Column(name = "sequence_number")
	private Integer sequenceNumber = 1;
	@ManyToOne
	@JoinColumn(name = "status_code")
	private StatusCode statusCode;

	/**
     * 
     */
	public ProductSiblingSequencingAuthorization() {
		super();
	}

	/**
	 * @param updatedBy
	 */
	public ProductSiblingSequencingAuthorization(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param id
	 */
	public ProductSiblingSequencingAuthorization(Long id) {
		super(id);
	}

	/**
	 * @param id
	 * @param updatedBy
	 */
	public ProductSiblingSequencingAuthorization(Long id, Agency updatedBy) {
		super(id, updatedBy);
	}

	public ProductSiblingSequencingAuthorization(Product parent,
			StatusCode statusCode, Product nextSibling,
			StatusCode nextSiblingStatus, Agency updatedBy) {
		super(updatedBy);
		setParent(parent);
		setStatusCode(statusCode);
		setNextSiblingStatus(nextSiblingStatus);
		setNextSibling(nextSibling);
	}

	/**
	 * @param notes
	 */
	public ProductSiblingSequencingAuthorization(String notes) {
		super(notes);
	}

	/**
	 * @param notes
	 * @param updatedBy
	 */
	public ProductSiblingSequencingAuthorization(String notes, Agency updatedBy) {
		super(notes, updatedBy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.Ruleform#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	public Product getNextSibling() {
		return nextSibling;
	}

	public StatusCode getNextSiblingStatus() {
		return nextSiblingStatus;
	}

	public Product getParent() {
		return parent;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.Ruleform#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setNextSibling(Product nextSibling) {
		this.nextSibling = nextSibling;
	}

	public void setNextSiblingStatus(StatusCode nextSiblingStatus) {
		this.nextSiblingStatus = nextSiblingStatus;
	}

	public void setParent(Product parent) {
		this.parent = parent;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
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
		if (nextSibling != null) {
			nextSibling = (Product) nextSibling.manageEntity(em, knownObjects);
		}
		if (nextSiblingStatus != null) {
			nextSiblingStatus = (StatusCode) nextSiblingStatus.manageEntity(em,
					knownObjects);
		}
		if (parent != null) {
			parent = (Product) parent.manageEntity(em, knownObjects);
		}
		if (statusCode != null) {
			statusCode = (StatusCode) statusCode.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}

}
