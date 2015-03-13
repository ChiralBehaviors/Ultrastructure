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

import static com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization.GET_SEQUENCES;
import static com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization.GET_SIBLING_ACTIONS;

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
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = GET_SIBLING_ACTIONS, query = "SELECT seq FROM ProductSiblingSequencingAuthorization AS seq "
                                                               + " WHERE seq.parent = :parent"
                                                               + " AND seq.statusCode = :status "
                                                               + " ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductSiblingSequencingAuthorization AS seq "
                                                         + " WHERE seq.parent = :service"
                                                         + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_sibling_sequencing_authorization", schema = "ruleform")
public class ProductSiblingSequencingAuthorization extends Ruleform {
    public static final String GET_SEQUENCES       = "productSequencingAuthorization.getSequences";
    public static final String GET_SIBLING_ACTIONS = "productSequencingAuthorization.getSiblingActions";

    private static final long  serialVersionUID    = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "next_sibling")
    private Product            nextSibling;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "next_sibling_status")
    private StatusCode         nextSiblingStatus;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Product            parent;

    @Column(name = "replace_product")
    private Integer            replaceProduct      = FALSE;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber      = 1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public ProductSiblingSequencingAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public ProductSiblingSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductSiblingSequencingAuthorization(Product parent,
                                                 StatusCode statusCode,
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
    public ProductSiblingSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public ProductSiblingSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public ProductSiblingSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductSiblingSequencingAuthorization(UUID id, Agency updatedBy) {
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductSiblingSequencingAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productSiblingSequencingAuthorization;
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
        return String.format("ProductSiblingSequencingAuthorization [parent=%s, statusCode=%s, nextSibling=%s, nextSiblingStatus=%s, replaceProduct=%s, sequenceNumber=%s]",
                             getParent().getName(), getStatusCode().getName(),
                             getNextSibling().getName(),
                             getNextSiblingStatus().getName(),
                             isReplaceProduct(), getSequenceNumber());
    }
}
