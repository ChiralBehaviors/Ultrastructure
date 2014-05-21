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
package com.chiralbehaviors.CoRE.product;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * The authorization for product location attributes
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "product_location_attribute_authorization", schema = "ruleform")
public class ProductLocationAttributeAuthorization extends
        AttributeAuthorization {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "classification")
    private Relationship      classification;

    @ManyToOne
    @JoinColumn(name = "classifier")
    private Product           classifier;

    public ProductLocationAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public ProductLocationAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public ProductLocationAttributeAuthorization(UUID id) {
        super(id);
    }

    /**
     * @return the classification
     */
    public Relationship getClassification() {
        return classification;
    }

    /**
     * @return the classifier
     */
    public Product getClassifier() {
        return classifier;
    }

    /**
     * @param classification
     *            the classification to set
     */
    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    /**
     * @param classifier
     *            the classifier to set
     */
    public void setClassifier(Product classifier) {
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
            classifier = (Product) classifier.manageEntity(em, knownObjects);
        }
        if (classification != null) {
            classification = (Relationship) classification.manageEntity(em,
                                                                        knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}