/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * The abstract super class of all network authorizations.
 *
 * from left to right: classifier, classification, auth relationship, auth
 * parent, child relationship, cardinality
 *
 * {classifier, classification} is the “parent”
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

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_relationship")
    private Relationship authorizedRelationship;

    private Cardinality cardinality = Cardinality.ZERO;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_relationship")
    private Relationship childRelationship;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "classifier")
    private Relationship classifier;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency groupingAgency;

    private String name;

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

    abstract public RuleForm getClassification();

    public Relationship getClassifier() {
        return classifier;
    }

    @JsonIgnore
    abstract public SingularAttribute<? extends NetworkAuthorization<RuleForm>, ? extends RuleForm> getClassifierAttribute();

    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public String getName() {
        return name;
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

    abstract public void setClassification(RuleForm classification);

    public void setClassifier(Relationship classifier) {
        this.classifier = classifier;
    }

    public void setGroupingAgency(Agency groupingAgency) {
        this.groupingAgency = groupingAgency;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("NetworkAuthorization [name=%s, classifier=%s, classification=%s, cardinality=%s, childRelationship=%s, AuthorizedRelationship=%s, AuthorizedParent=%s, groupingAgency=%s]",
                             getName(), getClassifier(), getClassification(),
                             getCardinality(), getChildRelationship(),
                             getAuthorizedRelationship(), getAuthorizedParent(),
                             getGroupingAgency());
    }

}
