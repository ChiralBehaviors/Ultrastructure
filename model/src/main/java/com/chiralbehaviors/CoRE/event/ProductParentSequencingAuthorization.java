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

import static com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization.GET_PARENT_ACTIONS;
import static com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization.GET_SEQUENCES;

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
               @NamedQuery(name = GET_PARENT_ACTIONS, query = "SELECT seq FROM ProductParentSequencingAuthorization AS seq"
                                                              + " WHERE seq.service = :service"
                                                              + "   AND seq.statusCode = :status"
                                                              + " ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductParentSequencingAuthorization AS seq"
                                                         + " WHERE seq.service = :service"
                                                         + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_parent_sequencing_authorization", schema = "ruleform")
public class ProductParentSequencingAuthorization extends Ruleform {
    public static final String GET_PARENT_ACTIONS  = "productParentSequencingAuthorization.getParentActions";
    public static final String GET_SEQUENCES       = "productParentSequencingAuthorization.getSequences";

    private static final long  serialVersionUID    = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Product            parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent_status_to_set")
    private StatusCode         parentStatusToSet;

    @Column(name = "replace_product")
    private Integer            replaceProduct      = FALSE;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber      = 1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product            service;

    @Column(name = "set_if_active_siblings")
    private Integer            setIfActiveSiblings = TRUE;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public ProductParentSequencingAuthorization() {
        super();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductParentSequencingAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productParentSequencingAuthorization;
    }

    /**
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductParentSequencingAuthorization(Product parent,
                                                StatusCode statusCode,
                                                Product myParent,
                                                StatusCode parentStatusToSet,
                                                Agency updatedBy) {
        super(updatedBy);
        setService(parent);
        setStatusCode(statusCode);
        setParent(myParent);
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
    public ProductParentSequencingAuthorization(String notes, Agency updatedBy) {
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
        return toBoolean(setIfActiveSiblings);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * @return the replaceProduct
     */
    public boolean isReplaceProduct() {
        return replaceProduct.equals(TRUE);
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
        this.replaceProduct = replaceProduct ? TRUE : FALSE;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public void setSetIfActiveSiblings(Boolean setIfActiveSiblings) {
        this.setIfActiveSiblings = toInteger(setIfActiveSiblings);
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
