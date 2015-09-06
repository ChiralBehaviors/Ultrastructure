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
package com.chiralbehaviors.CoRE.agency;

import static com.chiralbehaviors.CoRE.agency.AgencyLocation.AGENCIES_AT_LOCATION;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization rule form that defines rules for relating products to
 * locations.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_location", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = AGENCIES_AT_LOCATION, query = "SELECT n.agency "
                                                                 + "FROM AgencyLocation n "
                                                                 + "WHERE n.relationship = :relationship "
                                                                 + "AND n.location = :location"), })
public class AgencyLocation extends Ruleform
        implements Attributable<AgencyLocationAttribute> {
    public static final String AGENCIES_AT_LOCATION = "agencyLocation.agenciesAtLocation";
    private static final long  serialVersionUID     = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency agency;

    // bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agencyLocation")
    @JsonIgnore
    private Set<AgencyLocationAttribute> attributes;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "authority")
    private Agency authority;

    // bi-directional many-to-one association to Location
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "location")
    private Location location;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "relationship")
    private Relationship relationship;

    public AgencyLocation() {
    }

    /**
     * @param updatedBy
     */
    public AgencyLocation(Agency updatedBy) {
        super(updatedBy);
    }

    public AgencyLocation(Agency authority, Agency agency,
                          Relationship relationship, Location location,
                          Agency updatedBy) {
        super(updatedBy);
        this.authority = authority;
        this.relationship = relationship;
        this.location = location;
        this.agency = agency;
    }

    /**
     * @param id
     */
    public AgencyLocation(UUID id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public Set<AgencyLocationAttribute> getAttributes() {
        return attributes;
    }

    public Location getLocation() {
        return location;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AgencyLocationAttribute> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<AgencyLocationAttribute>) attributes;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}