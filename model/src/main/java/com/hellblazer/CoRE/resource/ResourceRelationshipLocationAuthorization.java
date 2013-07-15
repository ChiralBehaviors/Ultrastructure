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

package com.hellblazer.CoRE.resource;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.location.Location;

/**
 * @author Halloran Parry
 * 
 */
@javax.persistence.Entity
@DiscriminatorValue("location")
public class ResourceRelationshipLocationAuthorization extends
        ResourceAuthorization {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location          location;

    public ResourceRelationshipLocationAuthorization() {
        setRuleformType("location");
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		location.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}

}
