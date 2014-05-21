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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;

/**
 * The persistent class for the coordinate_nesting database table.
 * 
 */
@Entity
@Table(name = "coordinate_nesting", schema = "ruleform")
public class CoordinateNesting extends Ruleform {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "inner_attribute")
    private Attribute         innerAttribute;

    private String            operation;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "outer_attribute")
    private Attribute         outerAttribute;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    public CoordinateNesting() {
    }

    /**
     * @param updatedBy
     */
    public CoordinateNesting(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public CoordinateNesting(UUID id) {
        super(id);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Attribute getInnerAttribute() {
        return innerAttribute;
    }

    public String getOperation() {
        return operation;
    }

    public Attribute getOuterAttribute() {
        return outerAttribute;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setAttribute(Attribute attribute3) {
        attribute = attribute3;
    }

    public void setInnerAttribute(Attribute attribute2) {
        innerAttribute = attribute2;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setOuterAttribute(Attribute attribute1) {
        outerAttribute = attribute1;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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
        if (attribute != null) {
            attribute = (Attribute) attribute.manageEntity(em, knownObjects);
        }
        if (innerAttribute != null) {
            innerAttribute = (Attribute) innerAttribute.manageEntity(em,
                                                                     knownObjects);
        }
        if (outerAttribute != null) {
            outerAttribute = (Attribute) outerAttribute.manageEntity(em,
                                                                     knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}