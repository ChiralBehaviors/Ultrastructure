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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Research;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the coordinate_relationship database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "coordinate_relationship", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_relationship_id_seq", sequenceName = "coordinate_relationship_id_seq")
public class CoordinateRelationship extends Ruleform {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Attribute
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    //bi-directional many-to-one association to Relationship
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "attribute_relationship")
    private Relationship      attributeRelationship;

    @Id
    @GeneratedValue(generator = "coordinate_relationship_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "kind")
    private CoordinateKind    kind;

    //bi-directional many-to-one association to Relationship
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "research")
    private Research          research;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "subordinate_coordinate_kind")
    private CoordinateKind    subordinateCoordinateKind;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "updated_by")
    private Resource          updatedBy;

    public CoordinateRelationship() {
    }

    /**
     * @param id
     */
    public CoordinateRelationship(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public CoordinateRelationship(Resource updatedBy) {
        super(updatedBy);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Relationship getAttributeRelationship() {
        return attributeRelationship;
    }

    @Override
    public Long getId() {
        return id;
    }

    public CoordinateKind getKind() {
        return kind;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * @return the research
     */
    @Override
    public Research getResearch() {
        return research;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public CoordinateKind getSubordinateCoordinateKind() {
        return subordinateCoordinateKind;
    }

    /**
     * @return the updatedBy
     */
    @Override
    public Resource getUpdatedBy() {
        return updatedBy;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setAttributeRelationship(Relationship relationship2) {
        attributeRelationship = relationship2;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setKind(CoordinateKind coordinateKind1) {
        kind = coordinateKind1;
    }

    public void setRelationship(Relationship relationship1) {
        relationship = relationship1;
    }

    /**
     * @param research
     *            the research to set
     */
    @Override
    public void setResearch(Research research) {
        this.research = research;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSubordinateCoordinateKind(CoordinateKind coordinateKind2) {
        subordinateCoordinateKind = coordinateKind2;
    }

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    @Override
    public void setUpdatedBy(Resource updatedBy) {
        this.updatedBy = updatedBy;
    }

}