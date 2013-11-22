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

import javax.persistence.Column;
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

/**
 * The persistent class for the coordinate_nesting database table.
 * 
 */
@Entity
@Table(name = "coordinate_nesting", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_nesting_id_seq", sequenceName = "coordinate_nesting_id_seq")
public class CoordinateNesting extends Ruleform {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    @Id
    @GeneratedValue(generator = "coordinate_nesting_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "inner_attribute")
    private Attribute         innerAttribute;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne
    @JoinColumn(name = "kind")
    private CoordinateKind    kind;

    private String            operation;

    //bi-directional many-to-one association to Attribute
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
    public CoordinateNesting(Long id) {
        super(id);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Attribute getInnerAttribute() {
        return innerAttribute;
    }

    public CoordinateKind getKind() {
        return kind;
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

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInnerAttribute(Attribute attribute2) {
        innerAttribute = attribute2;
    }

    public void setKind(CoordinateKind coordinateKind) {
        kind = coordinateKind;
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
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
        if (kind != null) {
            kind = (CoordinateKind) kind.manageEntity(em, knownObjects);
        }
        if (outerAttribute != null) {
            outerAttribute = (Attribute) outerAttribute.manageEntity(em,
                                                                     knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}