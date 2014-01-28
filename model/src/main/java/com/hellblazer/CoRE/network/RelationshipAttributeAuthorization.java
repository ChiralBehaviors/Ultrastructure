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
package com.hellblazer.CoRE.network;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;

/**
 * The authorizations for attributes on entities.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "agency_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_attribute_authorization_id_seq", sequenceName = "relationship_attribute_authorization_id_seq")
public class RelationshipAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Relationship> {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "classifier")
    private Relationship      classifier;

    @Id
    @GeneratedValue(generator = "relationship_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

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
     */
    public RelationshipAttributeAuthorization(Long id) {
        super(id);
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#getClassifier()
     */
    @Override
    public Relationship getClassifier() {
        return classifier;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#setClassifier(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Relationship classifier) {
        this.classifier = classifier;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (classifier != null) {
            classifier = (Relationship) classifier.manageEntity(em,
                                                                knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}