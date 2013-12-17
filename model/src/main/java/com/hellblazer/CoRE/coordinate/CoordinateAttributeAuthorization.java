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
import com.hellblazer.CoRE.attribute.AttributeAuthorization;

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
public class CoordinateAttributeAuthorization extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency            agency;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "classification_coordinate")
    private Coordinate        classificationCoordinate;

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

    public Agency getAgency() {
        return agency;
    }

    public Coordinate getClassificationCoordinate() {
        return classificationCoordinate;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public void setClassificationCoordinate(Coordinate classificationCoordinate) {
        this.classificationCoordinate = classificationCoordinate;
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
        if (classificationCoordinate != null) {
            classificationCoordinate = (Coordinate) classificationCoordinate.manageEntity(em,
                                                                                          knownObjects);
        }
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
