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
package com.hellblazer.CoRE.location.access;

import static com.hellblazer.CoRE.location.access.LocationAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.location.Location;

/**
 * @author hparry
 * 
 */
@NamedQueries({

@NamedQuery(name = GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP, query = "SELECT auth "
                                                                               + "FROM LocationAccessAuthorization auth "
                                                                               + "WHERE auth.relationship = :r "
                                                                               + "AND auth.parent = :rf ") })
@Entity
public abstract class LocationAccessAuthorization<Child extends ExistentialRuleform<Child, ?>>
        extends AccessAuthorization<Location, Child> {
    public static final String LOCATION_ACCESS_AUTHORIZATION_PREFIX               = "locationAccessAuthorization";
    public static final String GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP = LOCATION_ACCESS_AUTHORIZATION_PREFIX
                                                                                    + GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX;

    private static final long  serialVersionUID                                   = 1L;

    @ManyToOne
    @JoinColumn(name = "location1")
    private Location           parent;

    /**
     * @return the parent
     */
    @Override
    public Location getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Location parent) {
        this.parent = parent;
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
        if (parent != null) {
            parent = (Location) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }
}
