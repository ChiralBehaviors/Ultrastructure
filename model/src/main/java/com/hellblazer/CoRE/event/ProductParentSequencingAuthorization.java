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

import static com.hellblazer.CoRE.event.ProductParentSequencingAuthorization.GET_PARENT_ACTIONS;

import java.util.Map;

import javax.persistence.Column;
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
@NamedQueries({ @NamedQuery(name = GET_PARENT_ACTIONS, query = "SELECT seq FROM ProductParentSequencingAuthorization AS seq "
                                                               + " WHERE seq.parent = :service"
                                                               + "   AND seq.statusCode = :status "
                                                               + "ORDER BY seq.myParent") })
@javax.persistence.Entity
@Table(name = "product_parent_sequencing_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_parent_sequencing_authorization_id_seq", sequenceName = "product_parent_sequencing_authorization_id_seq")
public class ProductParentSequencingAuthorization extends Ruleform {
    public static final String GET_PARENT_ACTIONS = "productParentSequencingAuthorization.getParentActions";

    private static final long  serialVersionUID   = 1L;

    @Id
    @GeneratedValue(generator = "product_child_sequencing_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @ManyToOne
    @JoinColumn(name = "my_parent")
    private Product            myParent;

    @ManyToOne
    @JoinColumn(name = "parent")
    private Product            parent;

    @ManyToOne
    @JoinColumn(name = "parent_status_to_set")
    private StatusCode         parentStatusToSet;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber     = 1;

    @Column(name = "set_if_active_siblings")
    private Boolean            setIfActiveSiblings;
    @ManyToOne
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    /**
     * 
     */
    public ProductParentSequencingAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public ProductParentSequencingAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    public ProductParentSequencingAuthorization(Product parent,
                                                StatusCode statusCode,
                                                Product myParent,
                                                StatusCode parentStatusToSet,
                                                Resource updatedBy) {
        super(updatedBy);
        setParent(parent);
        setStatusCode(statusCode);
        setMyParent(myParent);
        setParentStatusToSet(parentStatusToSet);
    }

    /**
     * @param updatedBy
     */
    public ProductParentSequencingAuthorization(Resource updatedBy) {
        super(updatedBy);
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
    public ProductParentSequencingAuthorization(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Product getMyParent() {
        return myParent;
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

    public Boolean getSetIfActiveSiblings() {
        return setIfActiveSiblings;
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

    public void setMyParent(Product myParent) {
        this.myParent = myParent;
    }

    public void setParent(Product parent) {
        this.parent = parent;
    }

    public void setParentStatusToSet(StatusCode parentStatusToSet) {
        this.parentStatusToSet = parentStatusToSet;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSetIfActiveSiblings(Boolean setIfActiveSiblings) {
        this.setIfActiveSiblings = setIfActiveSiblings;
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
		myParent.manageEntity(em, knownObjects);
		parent.manageEntity(em, knownObjects);
		parentStatusToSet.manageEntity(em, knownObjects);
		statusCode.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}

}
