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

import static com.chiralbehaviors.CoRE.job.SiblingSequencingAuthorization.GET_SEQUENCES;
import static com.chiralbehaviors.CoRE.job.SiblingSequencingAuthorization.GET_SIBLING_ACTIONS;

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
@NamedQueries({ @NamedQuery(name = GET_SIBLING_ACTIONS, query = "SELECT seq FROM SiblingSequencingAuthorization AS seq "
                                                                + " WHERE seq.parent = :parent"
                                                                + " AND seq.statusCode = :status "
                                                                + " ORDER BY seq.sequenceNumber"),
                @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM SiblingSequencingAuthorization AS seq "
                                                          + " WHERE seq.parent = :service"
                                                          + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "sibling_sequencing_authorization", schema = "ruleform")
public class SiblingSequencingAuthorization extends Ruleform {
    public static final String GET_SEQUENCES       = "siblingSequencingAuthorization.getSequences";
    public static final String GET_SIBLING_ACTIONS = "siblingSequencingAuthorization.getSiblingActions";

    private static final long  serialVersionUID    = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "next_sibling")
    private Product            nextSibling;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "next_sibling_status")
    private StatusCode         nextSiblingStatus;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Product            parent;

    @Column(name = "replace_product")
    private boolean            replaceProduct      = false;

    @Column(name = "sequence_number")
    private int                sequenceNumber      = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public SiblingSequencingAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public SiblingSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public SiblingSequencingAuthorization(Product parent, StatusCode statusCode,
                                          Product nextSibling,
                                          StatusCode nextSiblingStatus,
                                          Agency updatedBy) {
        super(updatedBy);
        setParent(parent);
        setStatusCode(statusCode);
        setNextSiblingStatus(nextSiblingStatus);
        setNextSibling(nextSibling);
    }

    /**
     * @param notes
     */
    public SiblingSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public SiblingSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public SiblingSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public SiblingSequencingAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Product getNextSibling() {
        return nextSibling;
    }

    public StatusCode getNextSiblingStatus() {
        return nextSiblingStatus;
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

    public void setNextSibling(Product nextSibling) {
        this.nextSibling = nextSibling;
    }

    public void setNextSiblingStatus(StatusCode nextSiblingStatus) {
        this.nextSiblingStatus = nextSiblingStatus;
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
        return String.format("ProductSiblingSequencingAuthorization [parent=%s, statusCode=%s, nextSibling=%s, nextSiblingStatus=%s, replaceProduct=%s, sequenceNumber=%s]",
                             getParent().getName(), getStatusCode().getName(),
                             getNextSibling().getName(),
                             getNextSiblingStatus().getName(),
                             isReplaceProduct(), getSequenceNumber());
    }
}
