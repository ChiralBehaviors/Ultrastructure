/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.coordinate;

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
import com.hellblazer.CoRE.network.Relationship;

/**
 * 
 * The authorization of an attribute for a coordinate.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "coordinate_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_attribute_authorization_id_seq", sequenceName = "coordinate_attribute_authorization_id_seq")
public class CoordinateAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Coordinate> {

    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "classifier")
    private Coordinate        classifier;

    @Id
    @GeneratedValue(generator = "coordinate_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

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

    /**
     * @param id
     */
    public CoordinateAttributeAuthorization(Long id) {
        super(id);
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

    public Coordinate getClassificationCoordinate() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#getClassifier()
     */
    @Override
    public Coordinate getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public void setClassificationCoordinate(Coordinate classificationCoordinate) {
        classifier = classificationCoordinate;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#setClassifier(com.hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public void setClassifier(Coordinate classifier) {
        this.classifier = classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
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
            classifier = (Coordinate) classifier.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
