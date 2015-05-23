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

    private Cardinality       cardinality;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "connection")
    private Relationship      connection;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "from_relationship")
    private Relationship      fromRelationship;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

    @Column(name = "sequence_number")
    private int               sequenceNumber   = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "to_relationship")
    private Relationship      toRelationship;

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

    public Integer getSequenceNumber() {
        return sequenceNumber;
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

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    abstract public void setToParent(To to);

    public void setToRelationship(Relationship toRelationship) {
        this.toRelationship = toRelationship;
    }
}
