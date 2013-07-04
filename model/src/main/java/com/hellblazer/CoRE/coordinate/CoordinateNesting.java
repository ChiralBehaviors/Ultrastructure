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
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the coordinate_nesting database table.
 * 
 */
@javax.persistence.Entity
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
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "inner_attribute")
    private Attribute         innerAttribute;

    //bi-directional many-to-one association to CoordinateKind
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "kind")
    private CoordinateKind    kind;

    private String            operation;

    //bi-directional many-to-one association to Attribute
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "outer_attribute")
    private Attribute         outerAttribute;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "research")
    private Research          research;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "updated_by")
    private Resource          updatedBy;

    public CoordinateNesting() {
    }

    /**
     * @param id
     */
    public CoordinateNesting(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public CoordinateNesting(Resource updatedBy) {
        super(updatedBy);
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

    /**
     * @return the updatedBy
     */
    @Override
    public Resource getUpdatedBy() {
        return updatedBy;
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

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    @Override
    public void setUpdatedBy(Resource updatedBy) {
        this.updatedBy = updatedBy;
    }

}