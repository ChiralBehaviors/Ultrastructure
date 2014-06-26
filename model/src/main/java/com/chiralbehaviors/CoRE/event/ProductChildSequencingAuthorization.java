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

import static com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization.GET_CHILD_ACTIONS;
import static com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization.GET_SEQUENCES;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = GET_CHILD_ACTIONS, query = "SELECT seq FROM ProductChildSequencingAuthorization AS seq "
                                                             + "WHERE seq.parent = :service"
                                                             + "  AND seq.statusCode = :status "
                                                             + "ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductChildSequencingAuthorization AS seq "
                                                         + " WHERE seq.parent = :service"
                                                         + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_child_sequencing_authorization", schema = "ruleform")
public class ProductChildSequencingAuthorization extends Ruleform {
    public static final String GET_CHILD_ACTIONS = "productChildSequencingAuthorization.getChildActions";
    public static final String GET_SEQUENCES     = "productChildSequencingAuthorization.getSequences";
    private static final long  serialVersionUID  = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_child")
    private Product            nextChild;

    @Override
    public String toString() {
        return String.format("ProductChildSequencingAuthorization [parent=%s, statusCode=%s, nextChild=%s, nextChildStatus=%s, replaceProduct=%s, sequenceNumber=%s]",
                             parent.getName(), statusCode.getName(),
                             nextChild.getName(), nextChildStatus.getName(),
                             replaceProduct, sequenceNumber);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_child_status")
    private StatusCode nextChildStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Product    parent;

    @Column(name = "replace_product")
    private Integer    replaceProduct = FALSE;

    @Column(name = "sequence_number")
    private Integer    sequenceNumber = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private StatusCode statusCode;

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

    /**
     * @return the replaceProduct
     */
    public boolean isReplaceProduct() {
        return replaceProduct.equals(TRUE);
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
        if (nextChild != null) {
            nextChild = (Product) nextChild.manageEntity(em, knownObjects);
        }
        if (nextChildStatus != null) {
            nextChildStatus = (StatusCode) nextChildStatus.manageEntity(em,
                                                                        knownObjects);
        }
        if (parent != null) {
            parent = (Product) parent.manageEntity(em, knownObjects);
        }
        if (statusCode != null) {
            statusCode = (StatusCode) statusCode.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
