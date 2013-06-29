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
package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.event.Protocol.GET;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The Protocol ruleform.
 * 
 * The factors of this ruleform are {consumer, service, product1, product2}
 * 
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
                                                + "WHERE p.requestedService = :requestedService "
                                                + "    AND p.product =:product"
                                                + "    AND p.requester = :requester"
                                                + "    AND p.deliverFrom = :deliverFrom"
                                                + "    AND p.deliverTo = :deliverTo"
                                                + " ORDER BY p.sequenceNumber") })
@javax.persistence.Entity
@Table(name = "protocol", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_id_seq", sequenceName = "protocol_id_seq")
public class Protocol extends Ruleform {
    public static final String     GET              = "protocol.get";

    private static final long      serialVersionUID = 1L;

    /**
     * The resource to assign to the job represented by this instance
     */
    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Resource               assignTo;

    /**
     * The attributes of this protocol
     */
    @OneToMany(mappedBy = "protocol")
    @JsonIgnore
    private Set<ProtocolAttribute> attributes;

    @Column(name = "copy_attributes")
    private boolean                copyAttributes   = false;

    /**
     * the location to deliver the product from
     */
    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Location               deliverFrom;

    /**
     * The location to deliver the product to
     */
    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Location               deliverTo;

    @Id
    @GeneratedValue(generator = "protocol_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    /**
     * The product of the service
     */
    @ManyToOne
    @JoinColumn(name = "product")
    private Product                product;

    /**
     * The ordered product
     */
    @ManyToOne
    @JoinColumn(name = "requested_product")
    private Product                requestedProduct;

    /**
     * The requested service to be performed
     */
    @ManyToOne
    @JoinColumn(name = "requested_service")
    private Product                requestedService;

    /**
     * The resource that requested the product of this service
     */
    @ManyToOne
    @JoinColumn(name = "requester")
    private Resource               requester;

    @Column(name = "sequence_number")
    private Integer                sequenceNumber   = 1;

    /**
     * The service to be performed
     */
    @ManyToOne
    @JoinColumn(name = "service")
    private Product                service;

    public Protocol() {
    }

    /**
     * @param id
     */
    public Protocol(Long id) {
        super(id);
    }

    public Protocol(Product requestedService, Resource requester,
                    Product requestedProduct, Location deliverTo,
                    Location deliverFrom, Resource assignTo, Product service,
                    Product product, boolean copyAttributes, Resource updatedBy) {
        this(requestedService, requester, requestedProduct, deliverTo,
             deliverFrom, assignTo, service, product, updatedBy);
        setCopyAttributes(copyAttributes);
    }

    public Protocol(Product requestedService, Resource requester,
                    Product requestedProduct, Location deliverTo,
                    Location deliverFrom, Resource assignTo, Product service,
                    Product product, Resource updatedBy) {
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

    /**
     * @param updatedBy
     */
    public Protocol(Resource updatedBy) {
        super(updatedBy);
    }

    public Resource getAssignTo() {
        return assignTo;
    }

    public Set<ProtocolAttribute> getAttributes() {
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
    public Resource getRequester() {
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
        return copyAttributes;
    }

    public void setAssignTo(Resource assignTo) {
        this.assignTo = assignTo;
    }

    public void setAttributes(Set<ProtocolAttribute> protocolAttributes) {
        attributes = protocolAttributes;
    }

    public void setCopyAttributes(boolean copyAttributes) {
        this.copyAttributes = copyAttributes;
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
     *            the resource requesting the service
     */
    public void setRequester(Resource requester) {
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
}