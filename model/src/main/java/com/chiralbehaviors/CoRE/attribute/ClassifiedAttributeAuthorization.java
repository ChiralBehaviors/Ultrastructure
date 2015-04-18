/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.attribute;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonGetter;

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
abstract public class ClassifiedAttributeAuthorization<RuleForm extends ExistentialRuleform<RuleForm, ?>>
        extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classification")
    private Relationship      classification;

    public ClassifiedAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param classification
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Relationship classification,
                                            Agency updatedBy) {
        this.classification = classification;
        setUpdatedBy(updatedBy);
    }

    /**
     * @param classification
     * @param authorized
     * @param updatedBy
     */
    public ClassifiedAttributeAuthorization(Relationship classification,
                                            Attribute authorized,
                                            Agency updatedBy) {
        super(authorized, updatedBy);
        this.classification = classification;
    }

    /**
     * @param id
     */
    public ClassifiedAttributeAuthorization(UUID id) {
        super(id);
    }

    public Relationship getClassification() {
        return classification;
    }

    @JsonGetter
    abstract public RuleForm getClassifier();

    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    abstract public void setClassifier(RuleForm classifier);
}
