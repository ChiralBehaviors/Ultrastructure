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
package com.hellblazer.CoRE.agency;

import java.util.Map;

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
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The authorizations for attributes on entities.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "agency_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "agency_attribute_authorization_id_seq", sequenceName = "agency_attribute_authorization_id_seq")
public class AgencyAttributeAuthorization extends
        ClassifiedAttributeAuthorization<Agency> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "classifier")
    private Agency            classifier;

    @Id
    @GeneratedValue(generator = "agency_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    public AgencyAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public AgencyAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public AgencyAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param classification
     * @param updatedBy
     */
    public AgencyAttributeAuthorization(Relationship classification,
                                        Agency updatedBy) {
        super(classification, updatedBy);
    }

    public AgencyAttributeAuthorization(Relationship classification,
                                        Agency classifier,
                                        Attribute authorized, Agency updatedBy) {
        this(classification, authorized, updatedBy);
        this.classifier = classifier;
    }

    /**
     * @param id
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public AgencyAttributeAuthorization(Relationship classification,
                                        Attribute authorized, Agency updatedBy) {
        super(classification, authorized, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization#getClassifier()
     */
    @Override
    public Agency getClassifier() {
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
    public void setClassifier(Agency classifier) {
        this.classifier = classifier;
    }

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
            classifier = (Agency) classifier.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}