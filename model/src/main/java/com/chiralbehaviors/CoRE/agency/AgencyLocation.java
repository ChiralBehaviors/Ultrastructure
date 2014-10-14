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
package com.chiralbehaviors.CoRE.agency;

import static com.chiralbehaviors.CoRE.agency.AgencyLocation.AGENCIES_AT_LOCATION;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import com.chiralbehaviors.CoRE.network.Relationship;
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
public class AgencyLocation extends Ruleform implements
        Attributable<AgencyLocationAttribute> {
    public static final String           AGENCIES_AT_LOCATION = "agencyLocation.agenciesAtLocation";
    private static final long            serialVersionUID     = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "authority")
    private Agency                       authority;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency                       agency;

    // bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agencyLocation")
    @JsonIgnore
    private Set<AgencyLocationAttribute> attributes;

    // bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location                     location;

    // bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship                 relationship;

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
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        if (location != null) {
            location = (Location) location.manageEntity(em, knownObjects);
        }
        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        if (authority != null) {
            authority = (Agency) authority.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}