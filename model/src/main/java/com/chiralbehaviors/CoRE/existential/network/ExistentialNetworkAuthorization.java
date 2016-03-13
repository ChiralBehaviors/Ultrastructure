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
package com.chiralbehaviors.CoRE.existential.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.MetaValue;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Interval;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;
import com.chiralbehaviors.CoRE.existential.domain.Unit;

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
@Entity
@Table(name = "existential_network_authorization", schema = "ruleform")
public class ExistentialNetworkAuthorization<P extends ExistentialRuleform<P>, C extends ExistentialRuleform<C>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @Any(metaColumn = @Column(name = "p_domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "authorized_parent")
    private P                 authorizedParent;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "authorized_relationship")
    private Relationship      authorizedRelationship;

    @Column(insertable = false, updatable = false)
    private char              c_domain;

    private Cardinality       cardinality      = Cardinality.ZERO;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "child_relationship")
    private Relationship      childRelationship;

    @NotNull
    @Any(metaColumn = @Column(name = "c_domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "classification")
    private C                 classification;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "classifier")
    private Relationship      classifier;
    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

    private String            name;

    @Column(insertable = false, updatable = false)
    private char              p_domain;

    public ExistentialNetworkAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ExistentialNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public ExistentialNetworkAuthorization(UUID id) {
        super(id);
    }

    public P getAuthorizedParent() {
        return authorizedParent;
    }

    public Relationship getAuthorizedRelationship() {
        return authorizedRelationship;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public Relationship getChildRelationship() {
        return childRelationship;
    }

    public C getClassification() {
        return classification;
    }

    public Relationship getClassifier() {
        return classifier;
    }

    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public String getName() {
        return name;
    }

    public void setAuthorizedParent(P parent) {
        this.authorizedParent = parent;
    }

    public void setAuthorizedRelationship(Relationship authorizedRelationship) {
        this.authorizedRelationship = authorizedRelationship;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public void setChildRelationship(Relationship childRelationship) {
        this.childRelationship = childRelationship;
    }

    public void setClassification(C classification) {
        this.classification = classification;
    }

    public void setClassifier(Relationship classifier) {
        this.classifier = classifier;
    }

    public void setGroupingAgency(Agency groupingAgency) {
        this.groupingAgency = groupingAgency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toFacetString() {
        return String.format("%s.%s", getClassifier().getName(),
                             getClassification().getName());
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
