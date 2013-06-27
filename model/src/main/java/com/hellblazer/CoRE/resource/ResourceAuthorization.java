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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The authorization relating agencies to other existential ruleforms via a Relationship
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "resource_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_authorization_id_seq", sequenceName = "resource_authorization_id_seq")
@DiscriminatorColumn(name = "ruleform_type")
public abstract class ResourceAuthorization extends Ruleform {
    private static final long serialVersionUID = 1L;
   
    @Column(name="ruleform_type")
    private String ruleformType;
    

    /**
	 * @return the ruleformType
	 */
	public String getRuleformType() {
		return ruleformType;
	}

	/**
	 * @param ruleformType the ruleformType to set
	 */
	public void setRuleformType(String ruleformType) {
		this.ruleformType = ruleformType;
	}

	@Id
    @GeneratedValue(generator = "resource_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;



    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource          resource;


    @Override
    public Long getId() {
        return id;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}