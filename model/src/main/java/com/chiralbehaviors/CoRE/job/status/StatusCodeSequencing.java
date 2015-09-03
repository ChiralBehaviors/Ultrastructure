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
package com.chiralbehaviors.CoRE.job.status;

import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.ENSURE_VALID_SERVICE_STATUS;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_CHILD_STATUS_CODES_SERVICE;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_PARENT_STATUS_CODES_SERVICE;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE;
import static com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing.IS_VALID_NEXT_STATUS;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The persistent class for the status_code_sequencing database table.
 *
 */
@NamedQueries({ @NamedQuery(name = ENSURE_VALID_SERVICE_STATUS, query = "SELECT COUNT(scs.id) "
                                                                        + "FROM StatusCodeSequencing AS scs "
                                                                        + "WHERE scs.service = :service "
                                                                        + "  AND (scs.parentCode = :code "
                                                                        + "       OR scs.childCode = :code)"),
                @NamedQuery(name = IS_VALID_NEXT_STATUS, query = "SELECT COUNT(scs.id) "
                                                                 + "FROM StatusCodeSequencing AS scs "
                                                                 + "WHERE scs.service = :service "
                                                                 + "  AND scs.parentCode = :parentCode "
                                                                 + "  AND scs.childCode = :childCode"),
                @NamedQuery(name = GET_PARENT_STATUS_CODES_SERVICE, query = "SELECT DISTINCT(scs.parentCode) "
                                                                            + " FROM StatusCodeSequencing scs "
                                                                            + " WHERE scs.service = :service"),
                @NamedQuery(name = GET_CHILD_STATUS_CODES_SERVICE, query = "SELECT DISTINCT(scs.childCode) "
                                                                           + " FROM StatusCodeSequencing scs "
                                                                           + " WHERE scs.service = :service"),
                @NamedQuery(name = GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                                     + " WHERE scs.service = :service"
                                                                                     + "   AND scs.childCode = :statusCode"),
                @NamedQuery(name = GET_PARENT_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                              + " WHERE scs.parentCode = :statusCode"),
                @NamedQuery(name = GET_CHILD_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                             + " WHERE scs.childCode = :statusCode"),
                @NamedQuery(name = GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                                      + " WHERE scs.service = :service"
                                                                                      + "   AND scs.parentCode = :statusCode"),
                @NamedQuery(name = GET_ALL_STATUS_CODE_SEQUENCING, query = "SELECT scs "
                                                                           + " FROM StatusCodeSequencing scs "
                                                                           + " WHERE scs.service = :service") })
@Entity
@Table(name = "status_code_sequencing", schema = "ruleform")
public class StatusCodeSequencing extends Ruleform {
    public static final String ENSURE_VALID_SERVICE_STATUS               = "statusCodeSequencing.ensureValidServiceAndStatus";
    public static final String GET_ALL_STATUS_CODE_SEQUENCING            = "statusCodeSequencing.getAllStatusCodeSequencing";
    public static final String GET_CHILD_STATUS_CODE_SEQUENCING          = "statusCodeSequencing.getChildStatusCodeSequencing";
    public static final String GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE  = "statusCodeSequencing.getChildStatusCodeSequencingService";
    public static final String GET_CHILD_STATUS_CODES_SERVICE            = "statusCodeSequencing.getChildStatusCodes";
    public static final String GET_PARENT_STATUS_CODE_SEQUENCING         = "statusCodeSequencing.getParentStatusCodeSequencing";
    public static final String GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE = "statusCodeSequencing.getParentStatusCodeSequencingService";
    public static final String GET_PARENT_STATUS_CODES_SERVICE           = "statusCodeSequencing.getParentStatusCodes";
    public static final String IS_VALID_NEXT_STATUS                      = "statusCodeSequencing.isValidNextStatus";

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child_code")
    private StatusCode childCode;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent_code")
    private StatusCode parentCode;

    // bi-directional many-to-one association to Event
    @ManyToOne(optional = false, cascade = { CascadeType.PERSIST,
                                             CascadeType.DETACH })
    @JoinColumn(name = "service")
    private Product service;

    public StatusCodeSequencing() {
    }

    /**
     * @param updatedBy
     */
    public StatusCodeSequencing(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCodeSequencing(Product service, StatusCode parent,
                                StatusCode child, Agency updatedBy) {
        super(updatedBy);
        this.service = service;
        parentCode = parent;
        childCode = child;
    }

    /**
     * @param notes
     */
    public StatusCodeSequencing(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public StatusCodeSequencing(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public StatusCodeSequencing(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public StatusCodeSequencing(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public StatusCode getChildCode() {
        return childCode;
    }

    public StatusCode getParentCode() {
        return parentCode;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    public void setChildCode(StatusCode statusCode1) {
        childCode = statusCode1;
    }

    public void setParentCode(StatusCode statusCode2) {
        parentCode = statusCode2;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Product service) {
        this.service = service;
    }
}
