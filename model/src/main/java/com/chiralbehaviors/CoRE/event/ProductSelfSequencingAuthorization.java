/**
 * Copyright (c) 2014 Halloran Parry, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.event;

import static com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization.GET_SELF_ACTIONS;
import static com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization.GET_SEQUENCES;

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
 * @author hparry
 *
 */
@NamedQueries({
               @NamedQuery(name = GET_SELF_ACTIONS, query = "SELECT seq FROM ProductSelfSequencingAuthorization AS seq"
                                                            + " WHERE seq.service = :service"
                                                            + "   AND seq.statusCode = :status"
                                                            + " ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM ProductSelfSequencingAuthorization AS seq"
                                                         + " WHERE seq.service = :service"
                                                         + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_self_sequencing_authorization", schema = "ruleform")
public class ProductSelfSequencingAuthorization extends Ruleform {
    public static final String GET_SELF_ACTIONS = "productSelfSequencingAuthorization.getSelfActions";
    public static final String GET_SEQUENCES    = "productSelfSequencingAuthorization.getSequences";

    private static final long  serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_to_set")
    private StatusCode         statusToSet;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber   = 1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product            service;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public ProductSelfSequencingAuthorization() {
        super();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductSelfSequencingAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productSelfSequencingAuthorization;
    }

    /**
     * @param updatedBy
     */
    public ProductSelfSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductSelfSequencingAuthorization(Product service,
                                              StatusCode statusCode,
                                              StatusCode statusToSet,
                                              Agency updatedBy) {
        super(updatedBy);
        setService(service);
        setStatusCode(statusCode);
        setStatusToSet(statusToSet);
    }

    /**
     * @param notes
     */
    public ProductSelfSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public ProductSelfSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public ProductSelfSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductSelfSequencingAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public StatusCode getStatusToSet() {
        return statusToSet;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Product getService() {
        return service;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusToSet(StatusCode statusToSet) {
        this.statusToSet = statusToSet;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.format("ProductSelfSequencingAuthorization [service=%s, statusCode=%s, statusToSet=%s, sequenceNumber=%s]",
                             service.getName(), statusCode.getName(),
                             statusToSet.getName(), sequenceNumber);
    }
}