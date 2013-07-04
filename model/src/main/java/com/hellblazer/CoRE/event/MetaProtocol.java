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

import static com.hellblazer.CoRE.event.MetaProtocol.FOR_JOB;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the meta_protocol database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "meta_protocol", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "meta_protocol_id_seq", sequenceName = "meta_protocol_id_seq")
@NamedQueries({ @NamedQuery(name = FOR_JOB, query = "SELECT mp from MetaProtocol mp "
                                                    + "WHERE mp.service = :service "
                                                    + "ORDER BY mp.sequenceNumber") })
public class MetaProtocol extends Ruleform {

    public static final String FOR_JOB          = "metaprotocol.getForJob";

    private static final long  serialVersionUID = 1L;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "deliver_from")
    private Relationship       deliverFrom;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "deliver_to")
    private Relationship       deliverTo;

    @Id
    @GeneratedValue(generator = "meta_protocol_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    /**
     * The relationship that transforms the product ordered
     */
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "product_ordered")
    private Relationship       productOrdered;

    /**
     * the relationship that transforms the requesting resource
     */
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "requesting_resource")
    private Relationship       requestingResource;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber   = 1;

    /**
     * The service factor for this rule
     */
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "service")
    private Product            service;

    /**
     * the relationship that transforms the service type
     */
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "service_type")
    private Relationship       serviceType;

    /**
     * Indicates no further transformations should be applied
     */
    @Column(name = "stop_on_match")
    private Boolean            stopOnMatch      = false;

    public MetaProtocol() {
    }

    /**
     * @param id
     */
    public MetaProtocol(Long id) {
        super(id);
    }

    public MetaProtocol(Product service, int sequenceNumber,
                        Relationship productOrdered,
                        Relationship requestingResource,
                        Relationship serviceType, Relationship deliverTo,
                        Relationship deliverFrom, Resource updatedBy) {
        super(updatedBy);
        setService(service);
        setSequenceNumber(sequenceNumber);
        setProductOrdered(productOrdered);
        setRequestingResource(requestingResource);
        setServiceType(serviceType);
        setDeliverTo(deliverTo);
        setDeliverFrom(deliverFrom);
    }

    public MetaProtocol(Product service, Relationship requestingResource,
                        Relationship serviceType, Relationship deliverTo,
                        Relationship deliverFrom, Resource updatedBy) {
        super(updatedBy);
        setService(service);
        setRequestingResource(requestingResource);
        setServiceType(serviceType);
        setDeliverTo(deliverTo);
        setDeliverFrom(deliverFrom);
    }

    /**
     * @param updatedBy
     */
    public MetaProtocol(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @return the deliverFrom
     */
    public Relationship getDeliverFrom() {
        return deliverFrom;
    }

    /**
     * @return the deliverTo
     */
    public Relationship getDeliverTo() {
        return deliverTo;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return the productOrdered
     */
    public Relationship getProductOrdered() {
        return productOrdered;
    }

    /**
     * @return the requestingResource
     */
    public Relationship getRequestingResource() {
        return requestingResource;
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

    /**
     * @return the serviceType
     */
    public Relationship getServiceType() {
        return serviceType;
    }

    /**
     * @return the stopOnMatch
     */
    public Boolean getStopOnMatch() {
        return stopOnMatch;
    }

    /**
     * @param deliverFrom
     *            the deliverFrom to set
     */
    public void setDeliverFrom(Relationship deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    /**
     * @param deliverTo
     *            the deliverTo to set
     */
    public void setDeliverTo(Relationship deliverTo) {
        this.deliverTo = deliverTo;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param productOrdered
     *            the productOrdered to set
     */
    public void setProductOrdered(Relationship productOrdered) {
        this.productOrdered = productOrdered;
    }

    /**
     * @param requestingResource
     *            the requestingResource to set
     */
    public void setRequestingResource(Relationship requestingResource) {
        this.requestingResource = requestingResource;
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

    /**
     * @param serviceType
     *            the serviceType to set
     */
    public void setServiceType(Relationship serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param stopOnMatch
     *            the stopOnMatch to set
     */
    public void setStopOnMatch(Boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }
}