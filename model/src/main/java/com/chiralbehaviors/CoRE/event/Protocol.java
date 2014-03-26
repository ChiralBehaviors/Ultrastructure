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

import static com.chiralbehaviors.CoRE.event.Protocol.GET;
import static com.chiralbehaviors.CoRE.event.Protocol.GET_FOR_JOB;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Protocol ruleform.
 * 
 * The factors of this ruleform are {consumer, service, product1, product2}
 * 
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
		+ "WHERE p.requestedService = :requestedService "
		+ "    AND p.product =:product" + "    AND p.requester = :requester"
		+ "    AND p.deliverFrom = :deliverFrom"
		+ "    AND p.deliverTo = :deliverTo" + " ORDER BY p.sequenceNumber") })
@NamedNativeQueries({ @NamedNativeQuery(name = GET_FOR_JOB, query = "WITH deliverFrom AS "
		+ "(SELECT ln.child from ruleform.location_network ln "
		+ "WHERE ln.parent = ? " //job.deliverFrom
		+ "AND ln.relationship = ? " //metaprotocol.deliverFrom
		+ "), "
		+ "deliverTo AS "
		+ "(SELECT ln.child from ruleform.location_network ln "
		+ "WHERE ln.parent = ? " //job.deliverTo
		+ "AND ln.relationship = ? " //metaprotocol.deliverTo
		+ "), "
		+ "productOrdered AS "
		+ "(SELECT pn.child from ruleform.product_network pn "
		+ "WHERE pn.parent = ? " //job.product
		+ "AND pn.relationship = ? " //metaprotocol.productOrdered
		+ "), "
		+ "requestingAgency AS "
		+ "(SELECT an.child from ruleform.agency_network an "
		+ "WHERE an.parent = ? " //job.requester
		+ "AND an.relationship = ? " //metaprotocol.requestingAgency
		+ ") "
		+ "SELECT * from ruleform.protocol p "
		+ "WHERE p.requested_service = ? " //job.service
		+ "AND (p.deliver_from IN (SELECT child FROM deliverFrom) "
		+ "OR p.deliver_from IN (?, ?)) " //same, any
		+ "AND (p.deliver_to IN (SELECT child FROM deliverTo) "
		+ "OR p.deliver_to IN (?, ?)) "//same, any
		+ "AND (p.product IN (SELECT child FROM productOrdered) "
		+ "OR p.product IN (?, ?)) " //kernel.any, kernel.same
		+ "AND (p.requester IN (SELECT child FROM requestingAgency) "
		+ "OR p.requester IN (?, ?))") }) //kernel.any, kernel.same
@Entity
@Table(name = "protocol", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_id_seq", sequenceName = "protocol_id_seq")
public class Protocol extends Ruleform {
	public static final String GET = "protocol.get";
	public static final String GET_FOR_JOB = "protocol.getForJob";

	private static final long serialVersionUID = 1L;

	/**
	 * The agency to assign to the job represented by this instance
	 */
	@ManyToOne
	@JoinColumn(name = "assign_to")
	private Agency assignTo;

	/**
	 * The attributes of this protocol
	 */
	@OneToMany(mappedBy = "protocol")
	@JsonIgnore
	private Set<ProtocolAttribute> attributes;

	@Column(name = "copy_attributes")
	private Integer copyAttributes = FALSE;

	/**
	 * the location to deliver the product from
	 */
	@ManyToOne
	@JoinColumn(name = "deliver_from")
	private Location deliverFrom;

	/**
	 * The location to deliver the product to
	 */
	@ManyToOne
	@JoinColumn(name = "deliver_to")
	private Location deliverTo;

	@Id
	@GeneratedValue(generator = "protocol_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	/**
	 * The product of the service
	 */
	@ManyToOne
	@JoinColumn(name = "product")
	private Product product;

	/**
	 * The ordered product
	 */
	@ManyToOne
	@JoinColumn(name = "requested_product")
	private Product requestedProduct;

	/**
	 * The requested service to be performed
	 */
	@ManyToOne
	@JoinColumn(name = "requested_service")
	private Product requestedService;

	/**
	 * The agency that requested the product of this service
	 */
	@ManyToOne
	@JoinColumn(name = "requester")
	private Agency requester;

	@Column(name = "sequence_number")
	private Integer sequenceNumber = 1;

	/**
	 * The service to be performed
	 */
	@ManyToOne
	@JoinColumn(name = "service")
	private Product service;

	public Protocol() {
	}

	/**
	 * @param updatedBy
	 */
	public Protocol(Agency updatedBy) {
		super(updatedBy);
	}

	/**
	 * @param id
	 */
	public Protocol(Long id) {
		super(id);
	}

	public Protocol(Product requestedService, Agency requester,
			Product requestedProduct, Location deliverTo, Location deliverFrom,
			Agency assignTo, Product service, Product product, Agency updatedBy) {
		super(updatedBy);
		assert requestedProduct != null;
		assert requester != null;
		assert requestedProduct != null;
		assert deliverTo != null;
		assert deliverFrom != null;
		assert assignTo != null;
		assert service != null;
		assert product != null;
		assert updatedBy != null;
		setRequestedService(requestedService);
		setRequester(requester);
		setRequestedProduct(requestedProduct);
		setDeliverTo(deliverTo);
		setDeliverFrom(deliverFrom);
		setAssignTo(assignTo);
		setService(service);
		setProduct(product);
	}

	public Protocol(Product requestedService, Agency requester,
			Product requestedProduct, Location deliverTo, Location deliverFrom,
			Agency assignTo, Product service, Product product,
			boolean copyAttributes, Agency updatedBy) {
		this(requestedService, requester, requestedProduct, deliverTo,
				deliverFrom, assignTo, service, product, updatedBy);
		setCopyAttributes(copyAttributes);
	}

	public Agency getAssignTo() {
		return assignTo;
	}

	public Set<ProtocolAttribute> getAttributes() {
		if (attributes == null) {
			return Collections.emptySet();
		}
		return attributes;
	}

	/**
	 * @return the deliverFrom
	 */
	public Location getDeliverFrom() {
		return deliverFrom;
	}

	/**
	 * @return the deliverTo
	 */
	public Location getDeliverTo() {
		return deliverTo;
	}

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	public Product getRequestedProduct() {
		return requestedProduct;
	}

	/**
	 * @return the requestedService
	 */
	public Product getRequestedService() {
		return requestedService;
	}

	/**
	 * @return the requester
	 */
	public Agency getRequester() {
		return requester;
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

	public boolean isCopyAttributes() {
		return toBoolean(copyAttributes);
	}

	public void setAssignTo(Agency assignTo) {
		this.assignTo = assignTo;
	}

	public void setAttributes(Set<ProtocolAttribute> protocolAttributes) {
		attributes = protocolAttributes;
	}

	public void setCopyAttributes(boolean copyAttributes) {
		this.copyAttributes = toInteger(copyAttributes);
	}

	/**
	 * @param deliverFrom
	 *            the deliverFrom to set
	 */
	public void setDeliverFrom(Location deliverFrom) {
		this.deliverFrom = deliverFrom;
	}

	/**
	 * @param deliverTo
	 *            the deliverTo to set
	 */
	public void setDeliverTo(Location deliverTo) {
		this.deliverTo = deliverTo;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	public void setRequestedProduct(Product requestedProduct) {
		this.requestedProduct = requestedProduct;
	}

	/**
	 * @param requestedService
	 *            the requestedService to set
	 */
	public void setRequestedService(Product requestedService) {
		this.requestedService = requestedService;
	}

	/**
	 * @param requester
	 *            the agency requesting the service
	 */
	public void setRequester(Agency requester) {
		this.requester = requester;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
	 * EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (assignTo != null) {
			assignTo = (Agency) assignTo.manageEntity(em, knownObjects);
		}
		if (deliverFrom != null) {
			deliverFrom = (Location) deliverFrom.manageEntity(em, knownObjects);
		}
		if (deliverTo != null) {
			deliverTo = (Location) deliverTo.manageEntity(em, knownObjects);
		}
		if (product != null) {
			product = (Product) product.manageEntity(em, knownObjects);
		}
		if (requestedProduct != null) {
			requestedProduct = (Product) requestedProduct.manageEntity(em,
					knownObjects);
		}
		if (requestedService != null) {
			requestedService = (Product) requestedService.manageEntity(em,
					knownObjects);
		}
		if (requester != null) {
			requester = (Agency) requester.manageEntity(em, knownObjects);
		}
		if (service != null) {
			service = (Product) service.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);

	}
}
