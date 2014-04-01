/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.attribute;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;

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

    @ManyToOne
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
     * @param id
     */
    public ClassifiedAttributeAuthorization(Long id) {
        super(id);
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

    public Relationship getClassification() {
        return classification;
    }

    abstract public RuleForm getClassifier();

    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    abstract public void setClassifier(RuleForm classifier);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (classification != null) {
            classification = (Relationship) classification.manageEntity(em,
                                                                        knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}
