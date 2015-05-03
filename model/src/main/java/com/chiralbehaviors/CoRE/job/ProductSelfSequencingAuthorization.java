/**
 * Copyright (c) 2014 Halloran Parry, all rights reserved.
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

import static com.chiralbehaviors.CoRE.job.ProductSelfSequencingAuthorization.GET_SELF_ACTIONS;
import static com.chiralbehaviors.CoRE.job.ProductSelfSequencingAuthorization.GET_SEQUENCES;

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

    @Column(name = "sequence_number")
    private int                sequenceNumber   = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product            service;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_to_set")
    private StatusCode         statusToSet;

    public ProductSelfSequencingAuthorization() {
        super();
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

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Product getService() {
        return service;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public StatusCode getStatusToSet() {
        return statusToSet;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductSelfSequencingAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productSelfSequencingAuthorization;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
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

    public void setStatusToSet(StatusCode statusToSet) {
        this.statusToSet = statusToSet;
    }

    @Override
    public String toString() {
        return String.format("ProductSelfSequencingAuthorization [service=%s, statusCode=%s, statusToSet=%s, sequenceNumber=%s]",
                             service.getName(), statusCode.getName(),
                             statusToSet.getName(), sequenceNumber);
    }
}