/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.event.ServiceSequencingAuthorization.GET_CHILD_ACTIONS;
import static com.hellblazer.CoRE.event.ServiceSequencingAuthorization.GET_PARENT_ACTIONS;
import static com.hellblazer.CoRE.event.ServiceSequencingAuthorization.GET_SIBLING_ACTIONS;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the service_sequencing_authorization database table.
 * 
 */
@NamedQueries({
               @NamedQuery(name = GET_SIBLING_ACTIONS, query = "SELECT seq FROM ServiceSequencingAuthorization AS seq "
                                                               + "WHERE seq.parent = :service"
                                                               + " AND seq.statusCode = :status "
                                                               + "  AND seq.nextSibling IS NOT NULL "
                                                               + "  AND seq.nextSiblingStatus IS NOT NULL "
                                                               + "ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_CHILD_ACTIONS, query = "SELECT seq FROM ServiceSequencingAuthorization AS seq "
                                                             + "WHERE seq.parent = :service"
                                                             + "  AND seq.statusCode = :status "
                                                             + "  AND seq.nextChild IS NOT NULL"
                                                             + "  AND seq.nextChildStatus IS NOT NULL "
                                                             + "ORDER BY seq.sequenceNumber"),
               @NamedQuery(name = GET_PARENT_ACTIONS, query = "SELECT seq FROM ServiceSequencingAuthorization AS seq "
                                                              + " WHERE seq.parent = :service"
                                                              + "   AND seq.statusCode = :status "
                                                              + "   AND ("
                                                              + "         (seq.parentStatusToSet NOT NULL) OR "
                                                              + "         (seq.setIfActiveSiblings NOT NULL) OR "
                                                              + "         (seq.myParent NOT NULL) "
                                                              + "    ) "
                                                              + "ORDER BY seq.myParent") })
@javax.persistence.Entity
@Table(name = "service_sequencing_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "service_sequencing_authorization_id_seq", sequenceName = "service_sequencing_authorization_id_seq")
public class ServiceSequencingAuthorization extends Ruleform {
    public static final String GET_CHILD_ACTIONS   = "serviceSequencingAuthorization.getChildActions";
    public static final String GET_PARENT_ACTIONS  = "serviceSequencingAuthorization.getParentActions";
    public static final String GET_SIBLING_ACTIONS = "serviceSequencingAuthorization.getSiblingActions";
    private static final long  serialVersionUID    = 1L;

    @Id
    @GeneratedValue(generator = "service_sequencing_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "my_parent")
    private Product             myParent;

    //bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "next_child")
    private Product             nextChild;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "next_child_status")
    private StatusCode         nextChildStatus;

    //bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "next_sibling")
    private Product             nextSibling;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "next_sibling_status")
    private StatusCode         nextSiblingStatus;

    //bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "parent")
    private Product             parent;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "parent_status_to_set")
    private StatusCode         parentStatusToSet;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber      = 1;

    @Column(name = "set_if_active_siblings")
    private Boolean            setIfActiveSiblings;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public ServiceSequencingAuthorization() {
    }

    /**
     * @param id
     */
    public ServiceSequencingAuthorization(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ServiceSequencingAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return the myParent
     */
    public Product getMyParent() {
        return myParent;
    }

    /**
     * @return the nextChild
     */
    public Product getNextChild() {
        return nextChild;
    }

    public StatusCode getNextChildStatus() {
        return nextChildStatus;
    }

    /**
     * @return the nextSibling
     */
    public Product getNextSibling() {
        return nextSibling;
    }

    public StatusCode getNextSiblingStatus() {
        return nextSiblingStatus;
    }

    /**
     * @return the parent
     */
    public Product getParent() {
        return parent;
    }

    public StatusCode getParentStatusToSet() {
        return parentStatusToSet;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public Boolean getSetIfActiveSiblings() {
        return setIfActiveSiblings;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param myParent
     *            the myParent to set
     */
    public void setMyParent(Product myParent) {
        this.myParent = myParent;
    }

    /**
     * @param nextChild
     *            the nextChild to set
     */
    public void setNextChild(Product nextChild) {
        this.nextChild = nextChild;
    }

    public void setNextChildStatus(StatusCode statusCode1) {
        nextChildStatus = statusCode1;
    }

    /**
     * @param nextSibling
     *            the nextSibling to set
     */
    public void setNextSibling(Product nextSibling) {
        this.nextSibling = nextSibling;
    }

    public void setNextSiblingStatus(StatusCode statusCode3) {
        nextSiblingStatus = statusCode3;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Product parent) {
        this.parent = parent;
    }

    public void setParentStatusToSet(StatusCode statusCode2) {
        parentStatusToSet = statusCode2;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSetIfActiveSiblings(Boolean setIfActiveSiblings) {
        this.setIfActiveSiblings = setIfActiveSiblings;
    }

    public void setStatusCode(StatusCode statusCode4) {
        statusCode = statusCode4;
    }
}