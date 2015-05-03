/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.job;

import static com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization.GET_CHILD_ACTIONS;
import static com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization.GET_SEQUENCES;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = GET_CHILD_ACTIONS, query = "SELECT seq FROM ProductChildSequencingAuthorization AS seq "
                                                             + "WHERE seq.parent = :service"
                                                             + "  AND seq.statusCode = :status "
                                                             + "ORDER BY seq.nextChild, seq.sequenceNumber"),
               @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductChildSequencingAuthorization AS seq "
                                                         + " WHERE seq.parent = :service"
                                                         + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_child_sequencing_authorization", schema = "ruleform")
public class ProductChildSequencingAuthorization extends Ruleform {
    public static final String GET_CHILD_ACTIONS = "productChildSequencingAuthorization.getChildActions";
    public static final String GET_SEQUENCES     = "productChildSequencingAuthorization.getSequences";
    private static final long  serialVersionUID  = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "next_child")
    private Product            nextChild;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "next_child_status")
    private StatusCode         nextChildStatus;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Product            parent;

    @Column(name = "replace_product")
    private Integer            replaceProduct    = FALSE;

    @Column(name = "sequence_number")
    private int                sequenceNumber    = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    /**
     *
     */
    public ProductChildSequencingAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ProductChildSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductChildSequencingAuthorization(Product parent,
                                               StatusCode statusCode,
                                               Product nextChild,
                                               StatusCode nextChildStatus,
                                               Agency updatedBy) {
        super(updatedBy);
        setParent(parent);
        setStatusCode(statusCode);
        setNextChild(nextChild);
        setNextChildStatus(nextChildStatus);
    }

    /**
     * @param notes
     */
    public ProductChildSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public ProductChildSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public ProductChildSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductChildSequencingAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Product getNextChild() {
        return nextChild;
    }

    public StatusCode getNextChildStatus() {
        return nextChildStatus;
    }

    public Product getParent() {
        return parent;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductChildSequencingAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productChildSequencingAuthorization;
    }

    /**
     * @return the replaceProduct
     */
    public boolean isReplaceProduct() {
        return replaceProduct.equals(TRUE);
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    public void setNextChild(Product nextChild) {
        this.nextChild = nextChild;
    }

    public void setNextChildStatus(StatusCode nextChildStatus) {
        this.nextChildStatus = nextChildStatus;
    }

    public void setParent(Product parent) {
        this.parent = parent;
    }

    /**
     * @param replaceProduct
     *            the replaceProduct to set
     */
    public void setReplaceProduct(boolean replaceProduct) {
        this.replaceProduct = replaceProduct ? TRUE : FALSE;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.format("ProductChildSequencingAuthorization [parent=%s, statusCode=%s, nextChild=%s, nextChildStatus=%s, replaceProduct=%s, sequenceNumber=%s]",
                             getParent().getName(), getStatusCode().getName(),
                             getNextChild().getName(),
                             getNextChildStatus().getName(),
                             isReplaceProduct(), getSequenceNumber());
    }
}
