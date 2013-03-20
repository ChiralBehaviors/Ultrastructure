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
package com.hellblazer.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the unit_values database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "unit_values", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_value_id_seq", sequenceName = "unit_value_id_seq")
public class UnitValue extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Column(name = "boolean_value")
    private Boolean           booleanValue;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "entity_value")
    private Entity            entityValue;

    @Id
    @GeneratedValue(generator = "unit_value_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    @Column(name = "integer_value")
    private Integer           integerValue;

    private String            notes;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    //bi-directional many-to-one association to Research
    @ManyToOne
    @JoinColumn(name = "research")
    private Research          research;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    @Column(name = "text_value")
    private String            textValue;

    //bi-directional many-to-one association to Unit
    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit              unit;

    @Column(name = "update_date")
    private Timestamp         updateDate;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Resource          updatedBy;

    public UnitValue() {
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public Entity getEntityValue() {
        return entityValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    @Override
    public Research getResearch() {
        return research;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getTextValue() {
        return textValue;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public Timestamp getUpdateDate() {
        return updateDate;
    }

    @Override
    public Resource getUpdatedBy() {
        return updatedBy;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setEntityValue(Entity entity) {
        entityValue = entity;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public void setResearch(Research research) {
        this.research = research;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public void setUpdatedBy(Resource resource) {
        updatedBy = resource;
    }

}