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

import java.math.BigDecimal;
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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The Protocol ruleform.
 *
 * The factors of this ruleform are {consumer, service, product1, product2}
 *
 */
@NamedQueries({
               @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
                                               + "WHERE p.service = :service "
                                               + "    AND p.product =:product"
                                               + "    AND p.requester = :requester"
                                               + "    AND p.deliverFrom = :deliverFrom"
                                               + "    AND p.deliverTo = :deliverTo"
                                               + "    AND p.assignTo = :assignTo"
                                               + "    AND p.productAttribute = :productAttribute"
                                               + "    AND p.assignToAttribute = :assignToAttribute"
                                               + "    AND p.requesterAttribute = :requesterAttribute"
                                               + "    AND p.deliverToAttribute = :deliverToAttribute"
                                               + "    AND p.deliverFromAttribute = :deliverFromAttribute"
                                               + " ORDER BY p.sequenceNumber"),
               @NamedQuery(name = GET_FOR_SERVICE, query = "SELECT p FROM Protocol p "
                                                           + "WHERE p.service = :service "
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

    @ManyToOne
    @JoinColumn(name = "child_assign_to")
    private Agency             childAssignTo;

    @ManyToOne
    @JoinColumn(name = "child_assign_to_attribute")
    private Attribute          childAssignToAttribute;

    @ManyToOne
    @JoinColumn(name = "child_deliver_from")
    private Location           childDeliverFrom;

    @ManyToOne
    @JoinColumn(name = "child_deliver_from_attribute")
    private Attribute          childDeliverFromAttribute;

    @ManyToOne
    @JoinColumn(name = "child_deliver_to")
    private Location           childDeliverTo;

    @ManyToOne
    @JoinColumn(name = "child_deliver_to_attribute")
    private Attribute          childDeliverToAttribute;

    @ManyToOne
    @JoinColumn(name = "child_product")
    private Product            childProduct;

    @ManyToOne
    @JoinColumn(name = "child_product_attribute")
    private Attribute          childProductAttribute;

    @Column(name = "child_quantity")
    private BigDecimal         childQuantity;

    @ManyToOne
    @JoinColumn(name = "child_quantity_unit")
    private Unit               childQuantityUnit;

    @ManyToOne
    @JoinColumn(name = "children_relationship")
    private Relationship       childrenRelationship;
    /**
     * The service of the child job
     */
    @ManyToOne
    @JoinColumn(name = "child_service")
    private Product            childService;

    @ManyToOne
    @JoinColumn(name = "child_service_attribute")
    private Attribute          childServiceAttribute;

    @Column(name = "name")
    private String             name;

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

    public Agency getChildAssignTo() {
        return childAssignTo;
    }

    public Attribute getChildAssignToAttribute() {
        return childAssignToAttribute;
    }

    public Location getChildDeliverFrom() {
        return childDeliverFrom;
    }

    public Attribute getChildDeliverFromAttribute() {
        return childDeliverFromAttribute;
    }

    public Location getChildDeliverTo() {
        return childDeliverTo;
    }

    public Attribute getChildDeliverToAttribute() {
        return childDeliverToAttribute;
    }

    public Product getChildProduct() {
        return childProduct;
    }

    public Attribute getChildProductAttribute() {
        return childProductAttribute;
    }

    public BigDecimal getChildQuantity() {
        return childQuantity;
    }

    public Unit getChildQuantityUnit() {
        return childQuantityUnit;
    }

    public Relationship getChildrenRelationship() {
        return childrenRelationship;
    }

    public Product getChildService() {
        return childService;
    }

    public Attribute getChildServiceAttribute() {
        return childServiceAttribute;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setChildAssignTo(Agency childAssignTo) {
        this.childAssignTo = childAssignTo;
    }

    public void setChildAssignToAttribute(Attribute childAssignToAttribute) {
        this.childAssignToAttribute = childAssignToAttribute;
    }

    public void setChildDeliverFrom(Location childDeliverFrom) {
        this.childDeliverFrom = childDeliverFrom;
    }

    public void setChildDeliverFromAttribute(Attribute childDeliverFromAttribute) {
        this.childDeliverFromAttribute = childDeliverFromAttribute;
    }

    public void setChildDeliverTo(Location childDeliverTo) {
        this.childDeliverTo = childDeliverTo;
    }

    public void setChildDeliverToAttribute(Attribute childDeliverToAttribute) {
        this.childDeliverToAttribute = childDeliverToAttribute;
    }

    public void setChildProduct(Product childProduct) {
        this.childProduct = childProduct;
    }

    public void setChildProductAttribute(Attribute childProductAttribute) {
        this.childProductAttribute = childProductAttribute;
    }

    public void setChildQuantity(BigDecimal childQuantity) {
        this.childQuantity = childQuantity;
    }

    public void setChildQuantityUnit(Unit childQuantityUnit) {
        this.childQuantityUnit = childQuantityUnit;
    }

    public void setChildrenRelationship(Relationship childrenRelationship) {
        this.childrenRelationship = childrenRelationship;
    }

    public void setChildService(Product childService) {
        this.childService = childService;
    }

    public void setChildServiceAttribute(Attribute childServiceAttribute) {
        this.childServiceAttribute = childServiceAttribute;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Protocol [%s, sequenceNumber=%s, requestedProduct=%s, requestedService=%s]",
                             getToString(), sequenceNumber,
                             childProduct.getName(), childService.getName());
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
        if (childProduct != null) {
            childProduct = (Product) childProduct.manageEntity(em, knownObjects);
        }
        if (childProductAttribute != null) {
            childProductAttribute = (Attribute) childProductAttribute.manageEntity(em,
                                                                                   knownObjects);
        }
        if (childService != null) {
            childService = (Product) childService.manageEntity(em, knownObjects);
        }
        if (childServiceAttribute != null) {
            childServiceAttribute = (Attribute) childServiceAttribute.manageEntity(em,
                                                                                   knownObjects);
        }
        if (childAssignTo != null) {
            childAssignTo = (Agency) childAssignTo.manageEntity(em,
                                                                knownObjects);
        }
        if (childAssignToAttribute != null) {
            childAssignToAttribute = (Attribute) childAssignToAttribute.manageEntity(em,
                                                                                     knownObjects);
        }
        if (childDeliverFrom != null) {
            childDeliverFrom = (Location) childDeliverFrom.manageEntity(em,
                                                                        knownObjects);
        }
        if (childDeliverFromAttribute != null) {
            childDeliverFromAttribute = (Attribute) childDeliverFromAttribute.manageEntity(em,
                                                                                           knownObjects);
        }
        if (childDeliverTo != null) {
            childDeliverTo = (Location) childDeliverTo.manageEntity(em,
                                                                    knownObjects);
        }
        if (childDeliverToAttribute != null) {
            childDeliverToAttribute = (Attribute) childDeliverToAttribute.manageEntity(em,
                                                                                       knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
