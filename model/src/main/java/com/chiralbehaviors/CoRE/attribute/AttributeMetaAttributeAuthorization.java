/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.attribute;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Attribute         classifier;

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeMetaAttributeAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeMetaAttributeAuthorization;
    }

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
    public Attribute getClassifier() {
        return classifier;
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
