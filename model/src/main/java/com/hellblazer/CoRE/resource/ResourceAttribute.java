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
package com.hellblazer.CoRE.resource;

import static com.hellblazer.CoRE.resource.ResourceAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.Unit;

/**
 * The attribute value of an agency attribute
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "resource_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_attribute_id_seq", sequenceName = "resource_attribute_id_seq")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from ResourceAttribute ra where ra.resource = :resource and ra.attribute = :attribute") })
public class ResourceAttribute extends AttributeValue<Resource> {
    private static final long  serialVersionUID = 1L;
    public static final String GET_ATTRIBUTE    = "resourceAttribute.getAttribute";

    @Id
    @GeneratedValue(generator = "resource_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource           resource;

    public ResourceAttribute() {
    }

    /**
     * @param attribute
     */
    public ResourceAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ResourceAttribute(Attribute attribute, BigDecimal value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ResourceAttribute(Attribute attribute, boolean value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ResourceAttribute(Attribute attribute, int value, Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ResourceAttribute(Attribute attribute, Resource updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ResourceAttribute(Attribute attribute, String value,
                             Resource updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ResourceAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ResourceAttribute(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ResourceAttribute(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ResourceAttribute, Resource> getRuleformAttribute() {
        return ResourceAttribute_.resource;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Resource> getRuleformClass() {
        return Resource.class;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (resource != null) resource = (Resource) resource.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}