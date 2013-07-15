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
package com.hellblazer.CoRE.location;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.AttributeAuthorization;
import com.hellblazer.CoRE.resource.Resource;

/**
 * 
 * The persistent class for the context_attribute_authorization database table.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "context_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "context_attribute_authorization_id_seq", sequenceName = "context_attribute_authorization_id_seq")
public class ContextAttributeAuthorization extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "context_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;

    /**
     * 
     */
    public ContextAttributeAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public ContextAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ContextAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		resource.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}
