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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 *
 * The abstract super class of all network authorizations.
 * 
 * from left to right: fromRelationship, fromParent, toRelationship, toParent,
 * connection relationship, cardinality
 * 
 * {fromRelationship, fromParent} is the “from” parent
 * 
 * {toRelationship, toParent} is the “child”
 * 
 * The connection relationship is the authorized relationship between the from
 * and the to. The cardinality is the cardinality of the number of from, through
 * the connecting relationship
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class XDomainNetworkAuthorization<From extends ExistentialRuleform<From, ? extends NetworkRuleform<From>>, To extends ExistentialRuleform<To, ? extends NetworkRuleform<To>>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    private Cardinality cardinality;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "connection")
    private Relationship connection;

    private boolean forward = true;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_relationship")
    private Relationship fromRelationship;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency groupingAgency;

    private String name;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_relationship")
    private Relationship toRelationship;

    public XDomainNetworkAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public XDomainNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public XDomainNetworkAuthorization(UUID id) {
        super(id);
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public Relationship getConnection() {
        return connection;
    }

    abstract public From getFromParent();

    public Relationship getFromRelationship() {
        return fromRelationship;
    }

    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public String getName() {
        return name;
    }

    abstract public To getToParent();

    public Relationship getToRelationship() {
        return toRelationship;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public void setConnection(Relationship connection) {
        this.connection = connection;
    }

    abstract public void setFromParent(From to);

    public void setFromRelationship(Relationship fromRelationship) {
        this.fromRelationship = fromRelationship;
    }

    public void setGroupingAgency(Agency groupingAgency) {
        this.groupingAgency = groupingAgency;
    }

    public void setName(String name) {
        this.name = name;
    }

    abstract public void setToParent(To to);

    public void setToRelationship(Relationship toRelationship) {
        this.toRelationship = toRelationship;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }
}
