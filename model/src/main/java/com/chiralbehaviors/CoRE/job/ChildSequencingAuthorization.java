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

import static com.chiralbehaviors.CoRE.job.ChildSequencingAuthorization.GET_CHILD_ACTIONS;
import static com.chiralbehaviors.CoRE.job.ChildSequencingAuthorization.GET_SEQUENCES;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({ @NamedQuery(name = GET_CHILD_ACTIONS, query = "SELECT seq FROM ChildSequencingAuthorization AS seq "
                                                              + "WHERE seq.parent = :service"
                                                              + "  AND seq.statusCode = :status "
                                                              + "ORDER BY seq.nextChild, seq.sequenceNumber"),
                @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ChildSequencingAuthorization AS seq "
                                                          + " WHERE seq.parent = :service"
                                                          + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "child_sequencing_authorization", schema = "ruleform")
public class ChildSequencingAuthorization extends Ruleform {
    public static final String GET_CHILD_ACTIONS = "childSequencingAuthorization.getChildActions";
    public static final String GET_SEQUENCES     = "childSequencingAuthorization.getSequences";
    private static final long  serialVersionUID  = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "next_child")
    private Product            nextChild;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "next_child_status")
    private StatusCode         nextChildStatus;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Product            parent;

    @Column(name = "replace_product")
    private boolean            replaceProduct    = false;

    @Column(name = "sequence_number")
    private int                sequenceNumber    = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    /**
     *
     */
    public ChildSequencingAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ChildSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ChildSequencingAuthorization(Product parent, StatusCode statusCode,
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
    public ChildSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public ChildSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public ChildSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ChildSequencingAuthorization(UUID id, Agency updatedBy) {
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

    /**
     * @return the replaceProduct
     */
    public boolean isReplaceProduct() {
        return replaceProduct;
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
        this.replaceProduct = replaceProduct;
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
                             getNextChildStatus().getName(), isReplaceProduct(),
                             getSequenceNumber());
    }
}
