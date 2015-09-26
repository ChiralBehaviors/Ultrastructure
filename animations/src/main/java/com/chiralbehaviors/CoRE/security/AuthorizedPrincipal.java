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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;

/**
 * Represents the Agency and the authorized active aspects the principal has
 * enabled
 *
 * @author hhildebrand
 *
 */
public class AuthorizedPrincipal implements Cloneable {
    private final List<AgencyNetworkAuthorization> asserted;
    private final List<Agency>                     capabilities;
    private final Agency                           principal;

    /**
     * @param principal
     */
    public AuthorizedPrincipal(Agency principal) {
        this(principal, Collections.<AgencyNetworkAuthorization> emptyList());
    }

    /**
     * @param principal
     * @param asserted
     */
    public AuthorizedPrincipal(Agency principal,
                               List<AgencyNetworkAuthorization> asserted) {
        this.principal = principal;
        this.asserted = new ArrayList<>(asserted);
        capabilities = this.asserted.stream()
                                       .map(auth -> auth.getClassification())
                                       .collect(Collectors.toList());
        capabilities.add(0, this.principal);
    }

    public List<AgencyNetworkAuthorization> getActiveRoles() {
        return asserted;
    }

    public Agency getPrincipal() {
        return principal;
    }

    public List<Agency> getCapabilities() {
        return capabilities;
    }
}
