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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * Attribute authorization that can be classified in a network. The network
 * match is of the form A relationship B, where the relationship is the
 * classification of the authorization and the B target is the classifer.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class ClassifiedAttributeAuthorization<RuleForm extends Networked<RuleForm, ?>>
        extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "classification")
    private Relationship      classification;

    public ClassifiedAttributeAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public ClassifiedAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Relationship classification,
                                            Attribute authorized,
                                            Resource updatedBy) {
        super(authorized, updatedBy);
        this.classification = classification;
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Relationship classification,
                                            Resource updatedBy) {
        this.classification = classification;
        setUpdatedBy(updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    public Relationship getClassification() {
        return classification;
    }

    abstract public RuleForm getClassifier();

    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    abstract public void setClassifier(RuleForm classifier);

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (classification != null) classification = (Relationship) classification.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
    
    

}
