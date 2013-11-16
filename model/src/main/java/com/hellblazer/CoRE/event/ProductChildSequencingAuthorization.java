/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

import static com.hellblazer.CoRE.event.ProductChildSequencingAuthorization.GET_CHILD_ACTIONS;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
 * @author hhildebrand
 * 
 */
@NamedQueries({ @NamedQuery(name = GET_CHILD_ACTIONS, query = "SELECT seq FROM ProductChildSequencingAuthorization AS seq "
                                                              + "WHERE seq.parent = :service"
                                                              + "  AND seq.statusCode = :status "
                                                              + "ORDER BY seq.sequenceNumber") })
@Entity
@Table(name = "product_child_sequencing_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_child_sequencing_authorization_id_seq", sequenceName = "product_child_sequencing_authorization_id_seq")
public class ProductChildSequencingAuthorization extends Ruleform {
    public static final String GET_CHILD_ACTIONS = "productChildSequencingAuthorization.getChildActions";
    private static final long  serialVersionUID  = 1L;

    @Id
    @GeneratedValue(generator = "product_child_sequencing_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @ManyToOne
    @JoinColumn(name = "next_child")
    private Product            nextChild;

    @ManyToOne
    @JoinColumn(name = "next_child_status")
    private StatusCode         nextChildStatus;

    @ManyToOne
    @JoinColumn(name = "parent")
    private Product            parent;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber    = 1;

    @ManyToOne
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    /**
     * 
     */
    public ProductChildSequencingAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public ProductChildSequencingAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductChildSequencingAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    public ProductChildSequencingAuthorization(Product parent,
                                               StatusCode statusCode,
                                               Product nextChild,
                                               StatusCode nextChildStatus,
                                               Resource updatedBy) {
        super(updatedBy);
        setParent(parent);
        setStatusCode(statusCode);
        setNextChild(nextChild);
        setNextChildStatus(nextChildStatus);
    }

    /**
     * @param updatedBy
     */
    public ProductChildSequencingAuthorization(Resource updatedBy) {
        super(updatedBy);
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
    public ProductChildSequencingAuthorization(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
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

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
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
