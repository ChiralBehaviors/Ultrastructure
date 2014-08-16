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
package com.chiralbehaviors.CoRE.agency.access;

import static com.chiralbehaviors.CoRE.agency.access.AgencyAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;

/**
 * @author hparry
 *
 */
@NamedQueries({

    @NamedQuery(name = GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP, query = "SELECT auth "
            + "FROM AgencyAccessAuthorization auth "
            + "WHERE auth.relationship = :r "
            + "AND auth.parent = :rf ") })
@Entity
public abstract class AgencyAccessAuthorization<Child extends ExistentialRuleform<Child, ?>>
extends AccessAuthorization<Agency, Child> {

    public static final String AGENCY_ACCESS_AUTHORIZATION_PREFIX                 = "agencyAccessAuthorization";
    public static final String GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP = AGENCY_ACCESS_AUTHORIZATION_PREFIX
            + GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX;

    private static final long  serialVersionUID                                   = 1L;

    @ManyToOne
    @JoinColumn(name = "agency1")
    private Agency             parent;

    public AgencyAccessAuthorization() {
        super();
    }

    /**
     * @return the parent
     */
    @Override
    public Agency getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Agency parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (parent != null) {
            parent = (Agency) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}
