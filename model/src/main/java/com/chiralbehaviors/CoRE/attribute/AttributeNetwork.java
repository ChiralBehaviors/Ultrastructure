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

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GENERATE_NETWORK_INVERSES;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.INFERENCE_STEP;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.INFERENCE_STEP_FROM_LAST_PASS;
import static com.chiralbehaviors.CoRE.attribute.AttributeNetwork.INSERT_NEW_NETWORK_RULES;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * The network relationships of attributes.
 * 
 * @author hhildebrand
 * 
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = INFERENCE_STEP, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
				+ "     SELECT "
				+ "         premise1.parent, "
				+ "         deduction.inference, "
				+ "         premise2.child, "
				+ "         premise1.id, "
				+ "         premise2.id "
				+ "     FROM  (SELECT n.id, n.parent, n.relationship, n.child "
				+ "              FROM ruleform.attribute_network AS n) as premise1 "
				+ "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
				+ "            FROM ruleform.attribute_network AS n "
				+ "            WHERE n.inferred = 0) as premise2  "
				+ "         ON premise2.parent = premise1.child "
				+ "         AND premise2.child <> premise1.parent "
				+ "     JOIN ruleform.network_inference AS deduction "
				+ "         ON premise1.relationship = deduction.premise1 "
				+ "         AND premise2.relationship = deduction.premise2 "),
		@NamedNativeQuery(name = INFERENCE_STEP_FROM_LAST_PASS, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
				+ "     SELECT "
				+ "         premise1.parent, "
				+ "         deduction.inference, "
				+ "         premise2.child, "
				+ "         premise1.id, "
				+ "         premise2.id "
				+ "     FROM  (SELECT n.id, n.parent, n.relationship, n.child"
				+ "              FROM last_pass_rules AS n) as premise1 "
				+ "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
				+ "            FROM ruleform.attribute_network AS n "
				+ "            WHERE n.inferred = 0) as premise2  "
				+ "         ON premise2.parent = premise1.child "
				+ "         AND premise2.child <> premise1.parent "
				+ "     JOIN ruleform.network_inference AS deduction "
				+ "         ON premise1.relationship = deduction.premise1 "
				+ "         AND premise2.relationship = deduction.premise2 "),
		@NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
				+ "SELECT exist.id, wm.* "
				+ "FROM working_memory AS wm "
				+ "JOIN ruleform.attribute_network AS exist "
				+ "    ON wm.parent = exist.parent "
				+ "    AND wm.relationship = exist.relationship "
				+ "    AND wm.child = exist.child"),
		@NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
				+ "    SELECT nextval('ruleform.attribute_network_id_seq'), wm.* "
				+ "    FROM (SELECT parent, relationship, child"
				+ "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
				+ "    LEFT OUTER JOIN ruleform.attribute_network AS exist "
				+ "         ON wm.parent = exist.parent "
				+ "         AND wm.relationship = exist.relationship "
				+ "         AND wm.child = exist.child "
				+ "     WHERE exist.parent IS NULL "
				+ "     AND exist.relationship IS NULL "
				+ "     AND exist.child IS NULL"),
		@NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "WITH upsert AS "
				+ "       (UPDATE ruleform.attribute_network n  "
				+ "        SET id = n.id, parent = n.parent, child= n.child "
				+ "        FROM current_pass_rules cpr "
				+ "        WHERE n.parent = cpr.parent "
				+ "          AND n.relationship = cpr.relationship "
				+ "          AND n.child = cpr.child "
				+ "        RETURNING n.*) "
				+ "INSERT INTO ruleform.attribute_network(id, parent, relationship, child, inferred, updated_by) "
				+ "        SELECT cpr.id, cpr.parent, cpr.relationship, cpr.child, 1, ?1 "
				+ "    FROM current_pass_rules cpr "
				+ "    LEFT OUTER JOIN upsert AS exist "
				+ "        ON cpr.parent = exist.parent "
				+ "        AND cpr.relationship = exist.relationship "
				+ "        AND cpr.child = exist.child "
				+ "     WHERE exist.parent IS NULL "
				+ "     AND exist.relationship IS NULL "
				+ "     AND exist.child IS NULL"),
		@NamedNativeQuery(name = GENERATE_NETWORK_INVERSES, query = "INSERT INTO ruleform.attribute_network(parent, relationship, child, updated_by, inferred) "
				+ "SELECT net.child as parent, "
				+ "    rel.inverse as relationship, "
				+ "    net.parent as child, "
				+ "    ?1 as updated_by,"
				+ "    net.inferred "
				+ "FROM ruleform.attribute_network AS net "
				+ "JOIN ruleform.relationship AS rel ON net.relationship = rel.id "
				+ "LEFT OUTER JOIN ruleform.attribute_network AS exist "
				+ "    ON net.child = exist.parent "
				+ "    AND rel.inverse = exist.relationship "
				+ "    AND net.parent = exist.child "
				+ " WHERE exist.parent IS NULL "
				+ "  AND exist.relationship IS NULL "
				+ "  AND exist.child IS NULL") })
@Entity
@Table(name = "attribute_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attribute_network_id_seq", sequenceName = "attribute_network_id_seq")
@NamedQueries({
		@NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "SELECT n FROM AttributeNetwork n "
				+ "WHERE n.parent = :attribute and n.inferred = 0 and n.relationship.preferred = 0 "
				+ "ORDER by n.parent.name, n.relationship.name, n.child.name"),
		@NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM AttributeNetwork n "
				+ "WHERE n.parent = :parent "
				+ "AND n.relationship = :relationship") })
public class AttributeNetwork extends NetworkRuleform<Attribute> {
	public static final String DEDUCE_NEW_NETWORK_RULES = "attributeNetwork"
			+ DEDUCE_NEW_NETWORK_RULES_SUFFIX;
	public static final String GATHER_EXISTING_NETWORK_RULES = "attributeNetwork"
			+ GATHER_EXISTING_NETWORK_RULES_SUFFIX;
	public static final String GENERATE_NETWORK_INVERSES = "attributeNetwork"
			+ GENERATE_NETWORK_INVERSES_SUFFIX;
	public static final String GET_CHILDREN = "attributeNetwork"
			+ GET_CHILDREN_SUFFIX;
	public static final String GET_USED_RELATIONSHIPS = "attributeNetwork.getUsedRelationships";
	public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "attribute.immediateChildrenNetworkRules";
	public static final String INFERENCE_STEP = "attributeNetwork"
			+ INFERENCE_STEP_SUFFIX;
	public static final String INFERENCE_STEP_FROM_LAST_PASS = "attributeNetwork"
			+ INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
	public static final String INSERT_NEW_NETWORK_RULES = "attributeNetwork"
			+ INSERT_NEW_NETWORK_RULES_SUFFIX;
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Attribute
	@ManyToOne
	@JoinColumn(name = "child")
	private Attribute child;

	@Id
	@GeneratedValue(generator = "attribute_network_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	// bi-directional many-to-one association to Attribute
	@ManyToOne
	@JoinColumn(name = "parent")
	private Attribute parent;

	public AttributeNetwork() {
	}

	/**
	 * @param updatedBy
	 */
	public AttributeNetwork(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param relationship
	 * @param updatedBy
	 */
	public AttributeNetwork(Attribute parent, Relationship relationship,
			Attribute child, Agency updatedBy) {
		super(relationship, updatedBy);
		this.parent = parent;
		this.child = child;
	}

	/**
	 * @param id
	 */
	public AttributeNetwork(Long id) {
		super(id);
	}

	/**
	 * @param relationship
	 * @param updatedBy
	 */
	public AttributeNetwork(Relationship relationship, Agency updatedBy) {
		super(relationship, updatedBy);
	}

	@Override
	public Attribute getChild() {
		return child;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Attribute getParent() {
		return parent;
	}

	@Override
	public void setChild(Attribute child) {
		this.child = child;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setParent(Attribute parent) {
		this.parent = parent;
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
		if (child != null) {
			child = (Attribute) child.manageEntity(em, knownObjects);
		}

		if (parent != null) {
			parent = (Attribute) parent.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}
