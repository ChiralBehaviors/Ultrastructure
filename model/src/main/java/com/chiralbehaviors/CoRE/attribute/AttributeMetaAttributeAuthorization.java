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

package com.chiralbehaviors.CoRE.attribute;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization for attributes on attributes
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "attr_meta_attr_auth", schema = "ruleform")
public class AttributeMetaAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Attribute> {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Attribute         classifier;

    /**
     *
     */
    public AttributeMetaAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param classifier
     * @param classification
     * @param attribute
     * @param coreModel
     */
    public AttributeMetaAttributeAuthorization(Attribute classifier,
                                               Relationship classification,
                                               Attribute authorizedAttribute,
                                               Agency updatedBy) {
        super(classification, authorizedAttribute, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Relationship classification,
                                               Agency updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Relationship classification,
                                               Attribute authorized,
                                               Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    /**
     * @param id
     */
    public AttributeMetaAttributeAuthorization(UUID id) {
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
    public Attribute getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeMetaAttributeAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeMetaAttributeAuthorization;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * setClassifier(com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Attribute classifier) {
        this.classifier = classifier;
    }
}
