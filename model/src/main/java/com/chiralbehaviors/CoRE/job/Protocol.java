/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.job;

import static com.chiralbehaviors.CoRE.job.Protocol.GET;
import static com.chiralbehaviors.CoRE.job.Protocol.GET_FOR_SERVICE;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * The Protocol ruleform.
 *
 * The factors of this ruleform are {consumer, service, product1, product2}
 *
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT p FROM Protocol p "
                                                + "WHERE p.service = :service "
                                                + "    AND p.product =:product"
                                                + "    AND p.requester = :requester"
                                                + "    AND p.deliverFrom = :deliverFrom"
                                                + "    AND p.deliverTo = :deliverTo"
                                                + "    AND p.assignTo = :assignTo"
                                                + " ORDER BY p.sequenceNumber"),
                @NamedQuery(name = GET_FOR_SERVICE, query = "SELECT p FROM Protocol p "
                                                            + "WHERE p.service = :service "
                                                            + " ORDER BY p.sequenceNumber") })
@Entity
@Table(name = "protocol", schema = "ruleform")
public class Protocol extends AbstractProtocol {
    public static final String GET             = "protocol.get";
    public static final String GET_FOR_SERVICE = "protocol.getForService";

    private static final long serialVersionUID = 1L;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_assign_to")
    private Agency childAssignTo;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_deliver_from")
    private Location childDeliverFrom;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_deliver_to")
    private Location childDeliverTo;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_product")
    private Product childProduct;

    @Column(name = "child_quantity")
    private BigDecimal childQuantity = BigDecimal.ZERO;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_quantity_unit")
    private Unit childQuantityUnit;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "children_relationship")
    private Relationship childrenRelationship;
    /**
     * The service of the child job
     */
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_service")
    private Product      childService;

    @Column(name = "name")
    private String name;

    @Column(name = "sequence_number")
    private int sequenceNumber = 0;

    public Protocol() {
    }

    public Agency getChildAssignTo() {
        return childAssignTo;
    }

    public Location getChildDeliverFrom() {
        return childDeliverFrom;
    }

    public Location getChildDeliverTo() {
        return childDeliverTo;
    }

    public Product getChildProduct() {
        return childProduct;
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

    public void setChildDeliverFrom(Location childDeliverFrom) {
        this.childDeliverFrom = childDeliverFrom;
    }

    public void setChildDeliverTo(Location childDeliverTo) {
        this.childDeliverTo = childDeliverTo;
    }

    public void setChildProduct(Product childProduct) {
        this.childProduct = childProduct;
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
                             getChildProduct().getName(),
                             getChildService().getName());
    }
}
