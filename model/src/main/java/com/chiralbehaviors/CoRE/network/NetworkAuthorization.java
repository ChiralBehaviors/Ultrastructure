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
package com.chiralbehaviors.CoRE.network;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;

/**
 * 
 * The abstract super class of all network authorizations.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class NetworkAuthorization<RuleForm extends ExistentialRuleform<RuleForm, ?>>
		extends Ruleform {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "authorized_relationship")
	private Relationship authorizedRelationship;

	@ManyToOne
	@JoinColumn(name = "classification")
	private Relationship classification;

	// bi-directional many-to-one association to Agency
	@ManyToOne
	@JoinColumn(name = "grouping_agency")
	private Agency groupingAgency;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	public NetworkAuthorization() {
		super();
	}

	/**
	 * @param updatedBy
	 */
	public NetworkAuthorization(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param id
	 */
	public NetworkAuthorization(Long id) {
		super(id);
	}

	abstract public RuleForm getAuthorizedParent();

	public Relationship getAuthorizedRelationship() {
		return authorizedRelationship;
	}

	public Relationship getClassification() {
		return classification;
	}

	abstract public RuleForm getClassifier();

	public Agency getGroupingAgency() {
		return groupingAgency;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	abstract public void setAuthorizedParent(RuleForm parent);

	public void setAuthorizedRelationship(Relationship authorizedRelationship) {
		this.authorizedRelationship = authorizedRelationship;
	}

	public void setClassification(Relationship classification) {
		this.classification = classification;
	}

	abstract public void setClassifier(RuleForm classifier);

	public void setGroupingAgency(Agency groupingAgency) {
		this.groupingAgency = groupingAgency;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
	 * EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (authorizedRelationship != null) {
			authorizedRelationship = (Relationship) authorizedRelationship
					.manageEntity(em, knownObjects);
		}
		if (classification != null) {
			classification = (Relationship) classification.manageEntity(em,
					knownObjects);
		}
		if (groupingAgency != null) {
			groupingAgency = (Agency) groupingAgency.manageEntity(em,
					knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}
