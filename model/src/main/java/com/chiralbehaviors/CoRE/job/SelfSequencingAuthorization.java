/**
 * Copyright (c) 2014 Halloran Parry, all rights reserved.
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

import static com.chiralbehaviors.CoRE.job.SelfSequencingAuthorization.GET_SELF_ACTIONS;
import static com.chiralbehaviors.CoRE.job.SelfSequencingAuthorization.GET_SEQUENCES;

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
 * @author hparry
 *
 */
@NamedQueries({ @NamedQuery(name = GET_SELF_ACTIONS, query = "SELECT seq FROM SelfSequencingAuthorization AS seq"
                                                             + " WHERE seq.service = :service"
                                                             + "   AND seq.statusCode = :status"
                                                             + " ORDER BY seq.sequenceNumber"),
                @NamedQuery(name = GET_SEQUENCES, query = "SELECT seq FROM SelfSequencingAuthorization AS seq"
                                                          + " WHERE seq.service = :service"
                                                          + " ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "self_sequencing_authorization", schema = "ruleform")
public class SelfSequencingAuthorization extends Ruleform {
    public static final String GET_SELF_ACTIONS = "selfSequencingAuthorization.getSelfActions";
    public static final String GET_SEQUENCES    = "selfSequencingAuthorization.getSequences";

    private static final long  serialVersionUID = 1L;

    @Column(name = "sequence_number")
    private int                sequenceNumber   = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "service")
    private Product            service;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_to_set")
    private StatusCode         statusToSet;

    public SelfSequencingAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public SelfSequencingAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public SelfSequencingAuthorization(Product service, StatusCode statusCode,
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
    public SelfSequencingAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public SelfSequencingAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public SelfSequencingAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public SelfSequencingAuthorization(UUID id, Agency updatedBy) {
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