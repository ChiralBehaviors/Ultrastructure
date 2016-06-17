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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.domain.Agency;

/**
 * Represents the Agency and the authorized active roles the principal has
 * enabled
 *
 * @author hhildebrand
 *
 */
public class AuthorizedPrincipal implements Cloneable, Principal {
    private final List<Agency> asserted;
    private final Agency       principal;

    public AuthorizedPrincipal(Agency principal) {
        this(principal, new ArrayList<>());
    }

    public AuthorizedPrincipal(Agency principal, List<Agency> asserted) {
        this.principal = principal;
        this.asserted = asserted;
    }

    public List<Agency> getAsserted() {
        return asserted;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public Agency getPrincipal() {
        return principal;
    }
}
