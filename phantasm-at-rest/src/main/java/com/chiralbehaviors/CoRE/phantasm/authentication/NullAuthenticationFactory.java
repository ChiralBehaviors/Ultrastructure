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

import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 *
 */
public final class NullAuthenticationFactory
        extends AuthFactory<BasicCredentials, AuthorizedPrincipal> {

    public NullAuthenticationFactory() {
        super(cred -> Optional.absent());
    }

    @Override
    public AuthFactory<BasicCredentials, AuthorizedPrincipal> clone(boolean required) {
        return this;
    }

    @Override
    public Class<AuthorizedPrincipal> getGeneratedClass() {
        return AuthorizedPrincipal.class;
    }

    @Override
    public AuthorizedPrincipal provide() {
        return null;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
    }
}