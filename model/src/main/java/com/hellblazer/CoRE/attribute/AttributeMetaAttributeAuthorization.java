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
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The authorization for attributes on attributes
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "attr_meta_attr_auth", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attr_meta_attr_auth_id_seq", sequenceName = "attr_meta_attr_auth_id_seq")
public class AttributeMetaAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Attribute> {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "classifier")
    private Attribute         classifier;

    @Id
    @GeneratedValue(generator = "attr_meta_attr_auth_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    /**
     * 
     */
    public AttributeMetaAttributeAuthorization() {
        super();
    }

    /**
     * @param classifier
     * @param classification
     * @param attribute
     * @param coreModel
     */
    public AttributeMetaAttributeAuthorization(Attribute classifier,
                                               Relationship classification,
                                               Attribute authorizedAttribute,
                                               Resource updatedBy) {
        super(classification, authorizedAttribute, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     */
    public AttributeMetaAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Relationship classification,
                                               Attribute authorized,
                                               Resource updatedBy) {
        super(classification, authorized, updatedBy);
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Relationship classification,
                                               Resource updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#getClassifier()
     */
    @Override
    public Attribute getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#setClassifier(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Attribute classifier) {
        this.classifier = classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (classifier != null) {
			classifier = (Attribute) classifier.manageEntity(em, knownObjects);
		}
		super.traverseForeignKeys(em, knownObjects);
	}
}
