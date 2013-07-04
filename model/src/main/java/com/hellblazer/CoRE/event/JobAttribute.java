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
package com.hellblazer.CoRE.event;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the job_attribute database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "job_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "job_attribute_id_seq", sequenceName = "job_attribute_id_seq")
public class JobAttribute extends AttributeValue<Job> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "job_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Job
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "job")
    private Job               job;

    public JobAttribute() {
    }

    /**
     * @param attribute
     */
    public JobAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public JobAttribute(Attribute attribute, BigDecimal value,
                        Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public JobAttribute(Attribute attribute, boolean value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public JobAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public JobAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public JobAttribute(Attribute attribute, String value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public JobAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public JobAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public JobAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<JobAttribute, Job> getRuleformAttribute() {
        return JobAttribute_.job;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Job> getRuleformClass() {
        return Job.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}