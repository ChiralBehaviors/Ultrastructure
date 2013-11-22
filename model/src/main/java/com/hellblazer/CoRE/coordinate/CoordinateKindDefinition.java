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
 * The persistent class for the coordinate_kind_definition database table.
 * 
 */
@Entity
@Table(name = "coordinate_kind_definition", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_kind_definition_id_seq", sequenceName = "coordinate_kind_definition_id_seq")
public class CoordinateKindDefinition extends Ruleform {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    @Id
    @GeneratedValue(generator = "coordinate_kind_definition_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne
    @JoinColumn(name = "kind")
    private CoordinateKind    kind;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne
    @JoinColumn(name = "subordinate_coordinate_kind")
    private CoordinateKind    subordinateCoordinateKind;

    public CoordinateKindDefinition() {
    }

    /**
     * @param updatedBy
     */
    public CoordinateKindDefinition(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public CoordinateKindDefinition(Long id) {
        super(id);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Long getId() {
        return id;
    }

    public CoordinateKind getKind() {
        return kind;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public CoordinateKind getSubordinateCoordinateKind() {
        return subordinateCoordinateKind;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setKind(CoordinateKind coordinateKind2) {
        kind = coordinateKind2;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSubordinateCoordinateKind(CoordinateKind coordinateKind1) {
        subordinateCoordinateKind = coordinateKind1;
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
        if (kind != null) {
            kind = (CoordinateKind) kind.manageEntity(em, knownObjects);
        }
        if (subordinateCoordinateKind != null) {
            subordinateCoordinateKind = (CoordinateKind) subordinateCoordinateKind.manageEntity(em,
                                                                                                knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}