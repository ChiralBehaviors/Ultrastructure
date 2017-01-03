/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.authentication;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */

@Priority(Priorities.AUTHENTICATION)
public class NullAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    /**
     * Builder for {@link NullAuthFilter}.
     * <p>
     * An {@link Authenticator} must be provided during the building process.
     * </p>
     *
     * @param <P>
     *            the principal
     */
    public static class Builder<P extends Principal>
            extends AuthFilterBuilder<String, P, NullAuthFilter<P>> {

        @Override
        protected NullAuthFilter<P> newInstance() {
            return new NullAuthFilter<>();
        }
    }

    private NullAuthFilter() {
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        Optional<P> principal;
        try {
            principal = authenticator.authenticate(null);
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.BASIC_AUTH;
            }

            @Override
            public Principal getUserPrincipal() {
                return principal.get();
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext()
                                     .isSecure();
            }

            @Override
            public boolean isUserInRole(String role) {
                return authorizer.authorize(principal.get(), role);
            }
        });
    }
}