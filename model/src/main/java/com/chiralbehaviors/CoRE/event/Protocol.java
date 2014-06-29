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
import static com.chiralbehaviors.CoRE.event.Protocol.GET_FOR_SERVICE;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The Protocol ruleform.
 *
 * The factors of this ruleform are {consumer, service, product1, product2}
 *
 */
@NamedQueries({
               @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
                                               + "WHERE p.requestedService = :requestedService "
                                               + "    AND p.product =:product"
                                               + "    AND p.requester = :requester"
                                               + "    AND p.deliverFrom = :deliverFrom"
                                               + "    AND p.deliverTo = :deliverTo"
                                               + "    AND p.productAttribute = :productAttribute"
                                               + "    AND p.assignToAttribute = :assignToAttribute"
                                               + "    AND p.requesterAttribute = :requesterAttribute"
                                               + "    AND p.deliverToAttribute = :deliverToAttribute"
                                               + "    AND p.deliverFromAttribute = :deliverFromAttribute"
                                               + " ORDER BY p.sequenceNumber"),
               @NamedQuery(name = GET_FOR_SERVICE, query = "SELECT p FROM Protocol p "
                                                           + "WHERE p.requestedService = :requestedService "
                                                           + " ORDER BY p.sequenceNumber") })
@NamedNativeQueries({ @NamedNativeQuery(name = GET_FOR_JOB, query = "WITH deliverFrom AS "
                                                                    + "(SELECT ln.child from ruleform.location_network ln "
                                                                    + "WHERE ln.parent = ? " // job.deliverFrom
                                                                    + "AND ln.relationship = ? " // metaprotocol.deliverFrom
                                                                    + "), "
                                                                    + "deliverTo AS "
                                                                    + "(SELECT ln.child from ruleform.location_network ln "
                                                                    + "WHERE ln.parent = ? " // job.deliverTo
                                                                    + "AND ln.relationship = ? " // metaprotocol.deliverTo
                                                                    + "), "
                                                                    + "productOrdered AS "
                                                                    + "(SELECT pn.child from ruleform.product_network pn "
                                                                    + "WHERE pn.parent = ? " // job.product
                                                                    + "AND pn.relationship = ? " // metaprotocol.productOrdered
                                                                    + "), "
                                                                    + "requestingAgency AS "
                                                                    + "(SELECT an.child from ruleform.agency_network an "
                                                                    + "WHERE an.parent = ? " // job.requester
                                                                    + "AND an.relationship = ? " // metaprotocol.requestingAgency
                                                                    + ") "
                                                                    + "SELECT * from ruleform.protocol p "
                                                                    + "WHERE p.requested_service = ? " // job.service
                                                                    + "AND (p.deliver_from IN (SELECT child FROM deliverFrom) "
                                                                    + "OR p.deliver_from IN (?, ?)) " // same, any
                                                                    + "AND (p.deliver_to IN (SELECT child FROM deliverTo) "
                                                                    + "OR p.deliver_to IN (?, ?)) "// same, any
                                                                    + "AND (p.product IN (SELECT child FROM productOrdered) "
                                                                    + "OR p.product IN (?, ?)) " // kernel.any, kernel.same
                                                                    + "AND (p.requester IN (SELECT child FROM requestingAgency) "
                                                                    + "OR p.requester IN (?, ?))") })
@Entity
@Table(name = "protocol", schema = "ruleform")
public class Protocol extends AbstractProtocol {
    public static final String GET              = "protocol.get";
    public static final String GET_FOR_JOB      = "protocol.getForJob";
    public static final String GET_FOR_SERVICE  = "protocol.getForService";

    private static final long  serialVersionUID = 1L;

    @Column(name = "name")
    private String             name;

    /**
     * The ordered product
     */
    @ManyToOne
    @JoinColumn(name = "requested_product")
    private Product            requestedProduct;
    /**
     * The requested service to be performed
     */
    @ManyToOne
    @JoinColumn(name = "requested_service")
    private Product            requestedService;
    @Column(name = "sequence_number")
    private int                sequenceNumber   = 1;

    public Protocol() {
    }

    /**
     * @param updatedBy
     */
    public Protocol(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param notes
     */
    public Protocol(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public Protocol(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public Protocol(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public Protocol(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public String getName() {
        return name;
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
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param requestedProduct
     *            the requestedProduct to set
     */
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
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Protocol [%s, sequenceNumber=%s, requestedProduct=%s, requestedService=%s]",
                             getToString(), sequenceNumber,
                             requestedProduct.getName(),
                             requestedService.getName());
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
        if (requestedProduct != null) {
            requestedProduct = (Product) requestedProduct.manageEntity(em,
                                                                       knownObjects);
        }
        if (requestedService != null) {
            requestedService = (Product) requestedService.manageEntity(em,
                                                                       knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
