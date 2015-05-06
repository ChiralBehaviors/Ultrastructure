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
package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * The abstract super class of all network authorizations.
 * 
 * from left to right: classification, classifier, auth parent, auth
 * relationship, child relationship, cardinality
 * 
 * {classification, classifier} is the “parent”
 * 
 * {auth parent, auth relationship} is the “child”
 * 
 * The child relationship is the authorized relationship between the parent and
 * the child. The child cardinality is the cardinality of those children
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class NetworkAuthorization<RuleForm extends ExistentialRuleform<RuleForm, ?>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_relationship")
    private Relationship      authorizedRelationship;

    private Cardinality       cardinality;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child_relationship")
    private Relationship      childRelationship;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classification")
    private Relationship      classification;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

    @Column(name = "sequence_number")
    private int               sequenceNumber   = 0;

    public NetworkAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public NetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public NetworkAuthorization(UUID id) {
        super(id);
    }

    abstract public RuleForm getAuthorizedParent();

    @JsonIgnore
    abstract public SingularAttribute<? extends NetworkAuthorization<RuleForm>, ? extends RuleForm> getAuthorizedParentAttribute();

    public Relationship getAuthorizedRelationship() {
        return authorizedRelationship;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public Relationship getChildRelationship() {
        return childRelationship;
    }

    public Relationship getClassification() {
        return classification;
    }

    abstract public RuleForm getClassifier();

    @JsonIgnore
    abstract public SingularAttribute<? extends NetworkAuthorization<RuleForm>, ? extends RuleForm> getClassifierAttribute();

    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    abstract public void setAuthorizedParent(RuleForm parent);

    public void setAuthorizedRelationship(Relationship authorizedRelationship) {
        this.authorizedRelationship = authorizedRelationship;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public void setChildRelationship(Relationship childRelationship) {
        this.childRelationship = childRelationship;
    }

    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    abstract public void setClassifier(RuleForm classifier);

    public void setGroupingAgency(Agency groupingAgency) {
        this.groupingAgency = groupingAgency;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
