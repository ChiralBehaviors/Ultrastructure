/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.location;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hparry
 * 
 */
@Entity
@DiscriminatorValue(AccessAuthorization.LOCATION_AGENCY)
public class LocationAgencyAccessAuthorization extends
        LocationAccessAuthorization {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "agency2")
    private Agency            child;
    {
        setAuthorizationType(AccessAuthorization.LOCATION_AGENCY);
    }

    public LocationAgencyAccessAuthorization() {
        super();
    }

    /**
     * @param Agency
     * @param Relationship
     * @param Product
     * @param updatedBy
     */
    public LocationAgencyAccessAuthorization(Location parent,
                                             Relationship relationship,
                                             Agency child, Agency updatedBy) {
        this();
        setParent(parent);
        setRelationship(relationship);
        setChild(child);
        setUpdatedBy(updatedBy);
    }

    /**
     * @return the child
     */
    @Override
    public Agency getChild() {
        return child;
    }

    /**
     * @param child
     *            the child to set
     */
    public void setChild(Agency child) {
        this.child = child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (child != null) {
            child = (Agency) child.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }
}
