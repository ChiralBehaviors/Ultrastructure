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

package com.hellblazer.CoRE.security;

import java.util.Collections;
import java.util.List;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Aspect;

/**
 * Represents the authenticated Agency and the authorized active aspects the
 * principal has enabled
 * 
 * @author hhildebrand
 * 
 */
public class AuthenticatedPrincipal {
    private final List<Aspect<Agency>> activeRoles;
    private final Agency               principal;

    /**
     * @param principal
     */
    public AuthenticatedPrincipal(Agency principal) {
        this(principal, Collections.<Aspect<Agency>> emptyList());
    }

    /**
     * @param principal
     * @param activeRoles
     */
    public AuthenticatedPrincipal(Agency principal,
                                  List<Aspect<Agency>> activeRoles) {
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
