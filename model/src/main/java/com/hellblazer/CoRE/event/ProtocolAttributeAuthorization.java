/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.event.ProtocolAttributeAuthorization.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.hellblazer.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;

import java.util.Map;

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

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeAuthorization;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       ProtocolAttribute attrValue, "
                                                                            + "       ProtocolAttributeAuthorization auth, "
                                                                            + "       ProductNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.serviceClassification AND "
                                                                            + "        network.parent = auth.service AND"
                                                                            + "        network.relationship = auth.productClassification AND "
                                                                            + "        network.parent = auth.product AND"
                                                                            + "        attrValue.protocol = :ruleform "),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_VALUES, query = "select attr from ProtocolAttributeAuthorization attr where "
                                                                         + "attr.product = :ruleform "
                                                                         + "AND attr.id IN ("
                                                                         + "select ea.authorizedAttribute from ProductAttributeAuthorization ea "
                                                                         + "WHERE ea.groupingResource = :resource)"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                 + "WHERE ea.groupingResource = :groupingResource"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProtocolAttributeAuthorization ea "
                                                                                               + "WHERE ea.groupingResource = :groupingResource AND ea.authorizedAttribute = :attribute") })
@javax.persistence.Entity
@Table(name = "protocol_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_authorization_id_seq", sequenceName = "protocol_attribute_authorization_id_seq")
public class ProtocolAttributeAuthorization extends AttributeAuthorization {
    public static final String FIND_ATTRIBUTE_AUTHORIZATIONS                          = "protocolAttributeAuthorization.findAttributeAuthorizations";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE    = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_VALUES                          = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;

    private static final long  serialVersionUID                                       = 1L;

    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Location           deliverFrom;

    @ManyToOne
    @JoinColumn(name = "deliver_from_classification")
    private Relationship       deliverFromClassification;

    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Location           deliverTo;

    @ManyToOne
    @JoinColumn(name = "deliver_to_classification")
    private Relationship       deliverToClassification;

    @Id
    @GeneratedValue(generator = "protocol_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @ManyToOne
    @JoinColumn(name = "product_classification")
    private Relationship       productClassification;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    @ManyToOne
    @JoinColumn(name = "requester")
    private Resource           requester;

    @ManyToOne
    @JoinColumn(name = "requester_classification")
    private Relationship       requesterClassification;

    @ManyToOne
    @JoinColumn(name = "service")
    private Product            service;

    @ManyToOne
    @JoinColumn(name = "service_classification")
    private Relationship       serviceClassification;

    /**
     * 
     */
    public ProtocolAttributeAuthorization() {
        super();
    }

    /**
     * @param authorized
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Attribute authorized,
                                          Resource updatedBy) {
        super(authorized, updatedBy);
    }

    /**
     * @param id
     */
    public ProtocolAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    public Location getDeliverFrom() {
        return deliverFrom;
    }

    public Relationship getDeliverFromClassification() {
        return deliverFromClassification;
    }

    public Location getDeliverTo() {
        return deliverTo;
    }

    public Relationship getDeliverToClassification() {
        return deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Relationship getProductClassification() {
        return productClassification;
    }

    public Resource getRequester() {
        return requester;
    }

    public Relationship getRequesterClassification() {
        return requesterClassification;
    }

    public Product getService() {
        return service;
    }

    public Relationship getServiceClassification() {
        return serviceClassification;
    }

    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    public void setDeliverFromClassification(Relationship deliverFromClassification) {
        this.deliverFromClassification = deliverFromClassification;
    }

    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    public void setDeliverToClassification(Relationship deliverToClassification) {
        this.deliverToClassification = deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProductClassification(Relationship procuctClassification) {
        productClassification = procuctClassification;
    }

    public void setRequester(Resource requester) {
        this.requester = requester;
    }

    public void setRequesterClassification(Relationship requesterClassification) {
        this.requesterClassification = requesterClassification;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public void setServiceClassification(Relationship serviceClassification) {
        this.serviceClassification = serviceClassification;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (deliverFrom != null) deliverFrom = (Location) deliverFrom.manageEntity(em, knownObjects);
		if (deliverFromClassification != null) deliverFromClassification = (Relationship) deliverFromClassification.manageEntity(em, knownObjects);
		if (deliverTo != null) deliverTo = (Location) deliverTo.manageEntity(em, knownObjects);
		if (deliverToClassification != null) deliverToClassification = (Relationship) deliverToClassification.manageEntity(em, knownObjects);
		if (productClassification != null) productClassification = (Relationship) productClassification.manageEntity(em, knownObjects);
		if (product != null) product = (Product) product.manageEntity(em, knownObjects);
		if (requester != null) requester = (Resource) requester.manageEntity(em, knownObjects);
		if (requesterClassification != null) requesterClassification = (Relationship) requesterClassification.manageEntity(em, knownObjects);
		if (service != null) service = (Product) service.manageEntity(em, knownObjects);
		if (serviceClassification != null) serviceClassification = (Relationship) serviceClassification.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}

}
