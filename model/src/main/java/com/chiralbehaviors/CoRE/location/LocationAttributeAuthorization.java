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
package com.chiralbehaviors.CoRE.location;

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
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * The persistent class for the location_attribute_authorization database table.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "location_attribute_authorization", schema = "ruleform")
public class LocationAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Location> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Location          classifier;

    /**
     *
     */
    public LocationAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public LocationAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public LocationAttributeAuthorization(Relationship classification,
                                          Agency updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public LocationAttributeAuthorization(Relationship classification,
                                          Attribute authorized, Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    public LocationAttributeAuthorization(Relationship classification,
                                          Location classifier,
                                          Attribute authorized, Agency updatedBy) {
        this(classification, authorized, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     */
    public LocationAttributeAuthorization(UUID id) {
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
    public Location getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, LocationAttributeAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.locationAttributeAuthorization;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * setClassifier(com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Location classifier) {
        this.classifier = classifier;
    }
}
