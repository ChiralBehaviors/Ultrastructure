/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.network;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author Halloran Parry
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "transitive_relationship_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transitive_relationship_authorization_id_seq", sequenceName = "transitive_relationship_authorization_id_seq", allocationSize = 1)
public class TransitiveRelationshipAuthorization extends Ruleform {

    private static final long serialVersionUID = 1L;
    @Id
    private long              id;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "authorized_relationship")
    private Relationship      authorizedRelationship;

    @Column(name = "is_transitive")
    private boolean           isTransitive     = true;

    public TransitiveRelationshipAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public TransitiveRelationshipAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public TransitiveRelationshipAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    /**
     * @param relationship
     * @param authorizedRelationship
     * @param isTransitive
     */
    public TransitiveRelationshipAuthorization(Relationship relationship,
                                               Relationship authorizedRelationship,
                                               boolean allowed) {
        super();
        this.relationship = relationship;
        this.authorizedRelationship = authorizedRelationship;
        isTransitive = allowed;
    }

    /**
     * @param relationship
     * @param authorizedRelationship
     * @param isTransitive
     */
    public TransitiveRelationshipAuthorization(Relationship relationship,
                                               Relationship authorizedRelationship,
                                               boolean allowed,
                                               Resource updatedBy) {
        super(updatedBy);
        this.relationship = relationship;
        this.authorizedRelationship = authorizedRelationship;
        isTransitive = allowed;
    }

    /**
     * @param updatedBy
     */
    public TransitiveRelationshipAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param notes
     */
    public TransitiveRelationshipAuthorization(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public TransitiveRelationshipAuthorization(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    public Relationship getAuthorized_relationship() {
        return authorizedRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public boolean isTransitive() {
        return isTransitive;
    }

    public void setAuthorized_relationship(Relationship authorizedRelationship) {
        this.authorizedRelationship = authorizedRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setTransitive(boolean isTransitive) {
        this.isTransitive = isTransitive;
    }

}
