/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.meta.security;

import java.util.Collections;
import java.util.List;

import com.hellblazer.CoRE.meta.Aspect;
import com.hellblazer.CoRE.resource.Resource;

/**
 * Represents the authenticated Resource and the authorized active aspects the
 * principal has enabled
 * 
 * @author hhildebrand
 * 
 */
public class AuthenticatedPrincipal {
    private final Resource               principal;
    private final List<Aspect<Resource>> activeRoles;

    /**
     * @param principal
     */
    public AuthenticatedPrincipal(Resource principal) {
        this(principal, Collections.<Aspect<Resource>> emptyList());
    }

    /**
     * @param principal
     * @param activeRoles
     */
    public AuthenticatedPrincipal(Resource principal,
                                  List<Aspect<Resource>> activeRoles) {
        this.principal = principal;
        this.activeRoles = Collections.unmodifiableList(activeRoles);
    }

    public List<Aspect<Resource>> getActiveRoles() {
        return activeRoles;
    }

    public Resource getPrincipal() {
        return principal;
    }
}
