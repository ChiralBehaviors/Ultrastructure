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
package com.chiralbehaviors.CoRE.coordinate;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * 
 * The authorization of an attribute for a coordinate.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "coordinate_attribute_authorization", schema = "ruleform")
public class CoordinateAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Coordinate> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "classifier")
    private Coordinate        classifier;

    /**
     * 
     */
    public CoordinateAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public CoordinateAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public CoordinateAttributeAuthorization(Relationship classification,
                                            Agency updatedBy) {
        super(classification, updatedBy);
    }

    public CoordinateAttributeAuthorization(Relationship classification,
                                            Attribute authorized,
                                            Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    /**
     * @param classification
     * @param classifier2
     * @param attribute
     * @param coreModel
     */
    public CoordinateAttributeAuthorization(Relationship classification,
                                            Coordinate classifier,
                                            Attribute attribute,
                                            Agency updatedBy) {
        this(classification, attribute, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     */
    public CoordinateAttributeAuthorization(UUID id) {
        super(id);
    }

    public Coordinate getClassificationCoordinate() {
        return classifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * getClassifier()
     */
    @Override
    public Coordinate getClassifier() {
        return classifier;
    }

    public void setClassificationCoordinate(Coordinate classificationCoordinate) {
        classifier = classificationCoordinate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization#
     * setClassifier(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setClassifier(Coordinate classifier) {
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
            classifier = (Coordinate) classifier.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
