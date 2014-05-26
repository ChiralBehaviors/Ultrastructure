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
package com.chiralbehaviors.CoRE.attribute.unit;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * The authorizations for attributes on entities.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "unit_attribute_authorization", schema = "ruleform")
public class UnitAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Unit> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classifier")
    private Unit              classifier;

    public UnitAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public UnitAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     * @param classification
     * @param updatedBy
     */
    public UnitAttributeAuthorization(Relationship classification,
                                      Agency updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param id
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public UnitAttributeAuthorization(Relationship classification,
                                      Attribute authorized, Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    public UnitAttributeAuthorization(Relationship classification,
                                      Unit classifier, Attribute authorized,
                                      Agency updatedBy) {
        this(classification, authorized, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     */
    public UnitAttributeAuthorization(UUID id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * getClassifier()
     */
    @Override
    public Unit getClassifier() {
        return classifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * setClassifier(com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Unit classifier) {
        this.classifier = classifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (classifier != null) {
            classifier = (Unit) classifier.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}