/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorizations for attributes on entities.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_attribute_authorization", schema = "ruleform")
public class RelationshipAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Relationship> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Relationship      classifier;

    public RelationshipAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public RelationshipAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     * @param classification
     * @param updatedBy
     */
    public RelationshipAttributeAuthorization(Relationship classification,
                                              Agency updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param id
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public RelationshipAttributeAuthorization(Relationship classification,
                                              Attribute authorized,
                                              Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    public RelationshipAttributeAuthorization(Relationship classification,
                                              Relationship classifier,
                                              Attribute authorized,
                                              Agency updatedBy) {
        this(classification, authorized, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     */
    public RelationshipAttributeAuthorization(UUID id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * getClassifier()
     */
    @Override
    @JsonGetter
    public Relationship getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, RelationshipAttributeAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.relationshipAttributeAuthorization;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * setClassifier(com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Relationship classifier) {
        this.classifier = classifier;
    }
}