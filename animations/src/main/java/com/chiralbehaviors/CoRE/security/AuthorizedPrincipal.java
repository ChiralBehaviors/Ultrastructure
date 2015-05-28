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

package com.chiralbehaviors.CoRE.security;

import java.util.Collections;
import java.util.List;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Aspect;

/**
 * Represents the Agency and the authorized active aspects the principal has
 * enabled
 *
 * @author hhildebrand
 *
 */
public class AuthorizedPrincipal {
    private final List<Aspect<Agency>> activeRoles;
    private final Agency               principal;

    /**
     * @param principal
     */
    public AuthorizedPrincipal(Agency principal) {
        this(principal, Collections.<Aspect<Agency>> emptyList());
    }

    /**
     * @param principal
     * @param activeRoles
     */
    public AuthorizedPrincipal(Agency principal, List<Aspect<Agency>> activeRoles) {
        this.principal = principal;
        this.activeRoles = Collections.unmodifiableList(activeRoles);
    }

    public List<Aspect<Agency>> getActiveRoles() {
        return activeRoles;
    }

    public Agency getPrincipal() {
        return principal;
    }
}
