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

import static com.chiralbehaviors.CoRE.job.ProductParentSequencingAuthorization.GET_PARENT_ACTIONS;
import static com.chiralbehaviors.CoRE.job.ProductParentSequencingAuthorization.GET_SEQUENCES;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({ @NamedQuery(name = GET_PARENT_ACTIONS, query = "SELECT seq FROM ProductParentSequencingAuthorization AS seq"
                                                               + " WHERE seq.service = :service"
                                                               + "   AND seq.statusCode = :status"
                                                               + " ORDER BY seq.parent, seq.sequenceNumber"),
                @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductParentSequencingAuthorization AS seq"
                                                          + " WHERE seq.service = :service"
                                                          + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_parent_sequencing_authorization", schema = "ruleform")
public class ProductParentSequencingAuthorization extends Ruleform {
    public static final String GET_PARENT_ACTIONS = "productParentSequencingAuthorization.getParentActions";
    public static final String GET_SEQUENCES      = "productParentSequencingAuthorization.getSequences";

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Product parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent_status_to_set")
    private StatusCode parentStatusToSet;

    @Column(name = "replace_product")
    private boolean replaceProduct = false;

    @Column(name = "sequence_number")
    private int sequenceNumber = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product service;

    @Column(name = "set_if_active_siblings")
    private boolean setIfActiveSiblings = true;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode statusCode;

    public ProductParentSequencingAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductParentSequencingAuthorization(Product service,
                                                StatusCode statusCode,
                                                Product parent,
                                                StatusCode parentStatusToSet,
                                                Agency updatedBy) {
        super(updatedBy);
        setService(service);
        setStatusCode(statusCode);
        setParent(parent);
        setParentStatusToSet(parentStatusToSet);
    }

    /**
     * @param notes
     */
    public ProductParentSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(String notes,
                                                Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public ProductParentSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Product getParent() {
        return parent;
    }

    public StatusCode getParentStatusToSet() {
        return parentStatusToSet;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Product getService() {
        return service;
    }

    public Boolean getSetIfActiveSiblings() {
        return setIfActiveSiblings;
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

    public void setParent(Product myParent) {
        parent = myParent;
    }

    public void setParentStatusToSet(StatusCode parentStatusToSet) {
        this.parentStatusToSet = parentStatusToSet;
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

    public void setService(Product service) {
        this.service = service;
    }

    public void setSetIfActiveSiblings(Boolean setIfActiveSiblings) {
        this.setIfActiveSiblings = setIfActiveSiblings;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.format("ProductParentSequencingAuthorization [service=%s, statusCode=%s, parent=%s, parentStatusToSet=%s, setIfActiveSiblings=%s, replaceProduct=%s, sequenceNumber=%s]",
                             getService().getName(), getStatusCode().getName(),
                             getParent().getName(),
                             getParentStatusToSet().getName(),
                             getSetIfActiveSiblings(), isReplaceProduct(),
                             getSequenceNumber());
    }
}
