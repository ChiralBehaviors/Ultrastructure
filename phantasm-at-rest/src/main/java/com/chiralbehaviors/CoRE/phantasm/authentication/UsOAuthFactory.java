/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.UnauthorizedHandler;

/**
 * @author hhildebrand
 *
 */
/**
 * A Jersey provider for OAuth2 bearer tokens the passes the remote IP from the
 * request
 *
 * @param <T>
 *            the principal type.
 */
public final class UsOAuthFactory<T>
        extends AuthFactory<RequestCredentials, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsOAuthFactory.class);

    private final boolean       required;
    private final Class<T>      generatedClass;
    private final String        realm;
    private final static String PREFIX              = "Bearer";
    private UnauthorizedHandler unauthorizedHandler = new DefaultUnauthorizedHandler();

    public static String parse(String header) {
        if (header == null) {
            return null;
        }
        final int space = header.indexOf(' ');
        if (space > 0) {
            final String method = header.substring(0, space);
            if (PREFIX.equalsIgnoreCase(method)) {
                return header.substring(space + 1);
            }
        }
        return null;
    }

    @Context
    private HttpServletRequest request;

    public UsOAuthFactory(final Authenticator<RequestCredentials, T> authenticator,
                          final String realm, final Class<T> generatedClass) {
        super(authenticator);
        this.required = false;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    private UsOAuthFactory(final boolean required,
                           final Authenticator<RequestCredentials, T> authenticator,
                           final String realm, final Class<T> generatedClass) {
        super(authenticator);
        this.required = required;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    public UsOAuthFactory<T> responseBuilder(UnauthorizedHandler unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
        return this;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public AuthFactory<RequestCredentials, T> clone(boolean required) {
        return new UsOAuthFactory<>(required, authenticator(), this.realm,
                                    this.generatedClass).responseBuilder(unauthorizedHandler);
    }

    @Override
    public T provide() {
        try {
            final String token = parse(request.getHeader(HttpHeaders.AUTHORIZATION));
            if (token != null) {
                final RequestCredentials credentials = new RequestCredentials(request.getRemoteAddr(),
                                                                              token);
                final Optional<T> result = authenticator().authenticate(credentials);
                if (result.isPresent()) {
                    return result.get();
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.warn("Error authenticating credentials", e);
            throw new InternalServerErrorException();
        }

        if (required) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(PREFIX,
                                                                                realm));
        }

        return null;
    }

    @Override
    public Class<T> getGeneratedClass() {
        return generatedClass;
    }
}
