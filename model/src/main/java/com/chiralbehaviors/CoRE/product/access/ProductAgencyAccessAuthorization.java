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
package com.chiralbehaviors.CoRE.product.access;

import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_PARENT_CHILD_NETWORKS;
import static com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization.FIND_RULEFORMS_REFERENCED_BY_AUTH;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hparry
 * 
 */
@NamedQueries({
		@NamedQuery(name = FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth "
				+ "WHERE auth.parent = :parent "
				+ "AND auth.relationship = :relationship "
				+ "AND auth.child = :child "
				+ "AND auth.parentTransitiveRelationship = :parentRelationship "
				+ "AND auth.childTransitiveRelationship = :childRelationship"),
		@NamedQuery(name = FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth "
				+ "WHERE auth.parent = :parent "
				+ "AND auth.relationship = :relationship "
				+ "AND auth.child = :child "),
		@NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth, ProductNetwork net "
				+ "WHERE auth.relationship = :relationship "
				+ "AND auth.child = :child "
				+ "AND net.relationship = :netRelationship "
				+ "AND net.child = :netChild "
				+ "AND auth.parent = net.parent "),
		@NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_CHILD, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth, AgencyNetwork net "
				+ "WHERE auth.relationship = :relationship "
				+ "AND auth.parent = :parent "
				+ "AND net.relationship = :netRelationship "
				+ "AND net.child = :netChild " + "AND auth.child = net.parent "),
		@NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth, ProductNetwork parentNet, AgencyNetwork childNet "
				+ "WHERE auth.relationship = :relationship "
				+ "AND parentNet.relationship = :parentNetRelationship "
				+ "AND parentNet.child = :parentNetChild "
				+ "AND childNet.relationship = :childNetRelationship "
				+ "AND childNet.child = :childNetChild "
				+ "AND auth.parent = parentNet.parent "
				+ "AND auth.child = childNet.parent "),
		@NamedQuery(name = FIND_RULEFORMS_REFERENCED_BY_AUTH, query = "select (p, auth.parent) AS products, (a, auth.child) AS agencies"
				+ " FROM Product p, Agency a, ProductAgencyAccessAuthorization auth, ProductNetwork pnet, AgencyNetwork anet "
				+ "WHERE "
				+ "auth.relationship = :relationship "
				+ "AND auth.parent = :parent "
				+ "AND "
				+ "  (auth.parentTransitiveRelationship IS NULL "
				+ "  OR (auth.parent = pnet.parent  "
				+ "  AND pnet.relationship = auth.parentTransitiveRelationship)) "
				+ "AND "
				+ "  (auth.childTransitiveRelationship IS NULL "
				+ "  OR (auth.child = anet.parent  "
				+ "  AND anet.relationship = auth.childTransitiveRelationship)) "
				+ "AND p = pnet.child " + "AND a = anet.child "),
		@NamedQuery(name = FIND_PARENT_CHILD_NETWORKS, query = "SELECT pnet, anet "
				+ "FROM ProductAgencyAccessAuthorization auth, ProductNetwork pnet, AgencyNetwork anet "
				+ "WHERE auth.parent = :parent "
				+ "AND auth.relationship = :relationship "
				+ "AND auth.parent = pnet.parent "
				+ "AND auth.child = anet.parent"),
		@NamedQuery(name = FIND_AUTHORIZATION, query = "SELECT auth "
				+ "FROM ProductAgencyAccessAuthorization auth "
				+ "WHERE auth.parent = :parent "
				+ "AND auth.relationship = :relationship ") })
@Entity
@DiscriminatorValue(AccessAuthorization.PRODUCT_AGENCY)
public class ProductAgencyAccessAuthorization extends
		ProductAccessAuthorization<Agency> {
	public static final String PRODUCT_AGENCY_ACCESS_AUTH_PREFIX = "productAgencyAccessAuthorization";

	public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX;
	public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS_SUFFIX;
	public static final String FIND_AUTHS_FOR_INDIRECT_CHILD = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX;
	public static final String FIND_AUTHS_FOR_INDIRECT_PARENT = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX;
	public static final String FIND_AUTHORIZATION = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_AUTHORIZATION_SUFFIX;

	public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD = PRODUCT_AGENCY_ACCESS_AUTH_PREFIX
			+ FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX;
	public static final String FIND_PARENT_CHILD_NETWORKS = "test2";

	public static final String FIND_RULEFORMS_REFERENCED_BY_AUTH = "test";
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "agency2")
	private Agency child;

	// bi-directional many-to-one association to ProductNetwork
	@OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<AgencyNetwork> networkByChild;

	{
		setAuthorizationType(AccessAuthorization.PRODUCT_AGENCY);
	}

	public ProductAgencyAccessAuthorization() {
		super();
	}

	/**
	 * @param Agency
	 * @param Relationship
	 * @param Product
	 * @param updatedBy
	 */
	public ProductAgencyAccessAuthorization(Product parent,
			Relationship relationship, Agency child, Agency updatedBy) {
		this();
		setParent(parent);
		setRelationship(relationship);
		setChild(child);
		setUpdatedBy(updatedBy);
	}

	public ProductAgencyAccessAuthorization(Product parent,
			Relationship parentRelationship, Relationship relationship,
			Agency child, Relationship childRelationship, Agency updatedBy) {
		this();
		setParent(parent);
		setParentTransitiveRelationship(parentRelationship);
		setRelationship(relationship);
		setChild(child);
		setChildTransitiveRelationship(childRelationship);
		setUpdatedBy(updatedBy);
	}

	/**
	 * @return the child
	 */
	@Override
	public Agency getChild() {
		return child;
	}

	/**
	 * @return the networkByChild
	 */
	public Set<AgencyNetwork> getNetworkByChild() {
		return networkByChild;
	}

	/**
	 * @param child
	 *            the child to set
	 */
	public void setChild(Agency child) {
		this.child = child;
	}

	/**
	 * @param networkByChild
	 *            the networkByChild to set
	 */
	public void setNetworkByChild(Set<AgencyNetwork> networkByChild) {
		this.networkByChild = networkByChild;
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
		if (child != null) {
			child = (Agency) child.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);
	}
}
