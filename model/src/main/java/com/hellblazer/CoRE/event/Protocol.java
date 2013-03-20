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
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The Protocol ruleform.
 * 
 * The factors of this ruleform are {consumer, service, entity1, entity2}
 * 
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
                                                + "WHERE p.service = :service "
                                                + "    AND p.product =:product"
                                                + "    AND p.subService = :subService"
                                                + " ORDER BY p.sequenceNumber") })
@javax.persistence.Entity
@Table(name = "protocol", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_id_seq", sequenceName = "protocol_id_seq")
public class Protocol extends Ruleform {
    public static final String GET              = "protocol.get";
    private static final long  serialVersionUID = 1L;

    /**
     * The resource to assign to the job represented by this instance
     */
    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Resource           assignTo;

    /**
     * the location to deliver the product from
     */
    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Location           deliverFrom;

    /**
     * The location to deliver the product to
     */
    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Location           deliverTo;

    @Id
    @GeneratedValue(generator = "protocol_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    /**
     * The material for this job
     */
    @ManyToOne
    @JoinColumn(name = "material")
    private Entity             material;

    /**
     * The product of the service
     */
    @ManyToOne
    @JoinColumn(name = "product")
    private Entity             product;

    /**
     * The ordered product
     */
    @ManyToOne
    @JoinColumn(name = "product_ordered")
    private Entity             productOrdered;

    /**
     * The requested service to be performed
     */
    @ManyToOne
    @JoinColumn(name = "requested_service")
    private Entity             requestedService;

    /**
     * The resource that requested the product of this service
     */
    @ManyToOne
    @JoinColumn(name = "requester")
    private Resource           requester;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber   = 1;

    /**
     * The service to be performed
     */
    @ManyToOne
    @JoinColumn(name = "service")
    private Entity             service;

    /**
     * The sub service
     */
    @ManyToOne
    @JoinColumn(name = "sub_service")
    private Entity             subService;

    public Protocol() {
    }

    /**
     * @param id
     */
    public Protocol(Long id) {
        super(id);
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
     * @return the material
     */
    public Entity getMaterial() {
        return material;
    }

    /**
     * @return the product
     */
    public Entity getProduct() {
        return product;
    }

    /**
     * @return the productOrdered
     */
    public Entity getProductOrdered() {
        return productOrdered;
    }

    /**
     * @return the requestedService
     */
    public Entity getRequestedService() {
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
    public Entity getService() {
        return service;
    }

    /**
     * @return the subService
     */
    public Entity getSubService() {
        return subService;
    }

    public void setAssignTo(Resource assignTo) {
        this.assignTo = assignTo;
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
     * @param material
     *            the material to set
     */
    public void setMaterial(Entity material) {
        this.material = material;
    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(Entity product) {
        this.product = product;
    }

    /**
     * @param productOrdered
     *            the productOrdered to set
     */
    public void setProductOrdered(Entity productOrdered) {
        this.productOrdered = productOrdered;
    }

    /**
     * @param requestedService
     *            the requestedService to set
     */
    public void setRequestedService(Entity requestedService) {
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
    public void setService(Entity service) {
        this.service = service;
    }

    /**
     * @param subService
     *            the subService to set
     */
    public void setSubService(Entity subService) {
        this.subService = subService;
    }
}