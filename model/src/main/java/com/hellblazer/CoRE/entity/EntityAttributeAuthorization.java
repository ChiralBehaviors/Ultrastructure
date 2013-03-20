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
package com.hellblazer.CoRE.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_attribute_authorization database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "entity_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_attribute_authorization_id_seq", sequenceName = "entity_attribute_authorization_id_seq")
public class EntityAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Entity> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Entity
    @ManyToOne
    @JoinColumn(name = "classifier")
    private Entity            classifier;

    @Id
    @GeneratedValue(generator = "entity_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    public EntityAttributeAuthorization() {
    }

    /**
     * @param id
     */
    public EntityAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public EntityAttributeAuthorization(Relationship classification,
                                        Attribute authorized, Resource updatedBy) {
        super(classification, authorized, updatedBy);
    }

    public EntityAttributeAuthorization(Relationship classification,
                                        Entity classifier,
                                        Attribute authorized, Resource updatedBy) {
        super(classification, authorized, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public EntityAttributeAuthorization(Relationship classification,
                                        Resource updatedBy) {
        super(classification, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public EntityAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#getClassifier()
     */
    @Override
    public Entity getClassifier() {
        return classifier;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#setClassifier(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Entity classifier) {
        this.classifier = classifier;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}