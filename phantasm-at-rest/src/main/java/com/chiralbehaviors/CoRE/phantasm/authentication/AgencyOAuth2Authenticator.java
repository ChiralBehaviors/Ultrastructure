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

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */
public class AgencyOAuth2Authenticator
        implements Authenticator<String, AuthorizedPrincipal> {
    private final static Logger log = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private final EntityManagerFactory emf;

    /**
     * @param emf
     */
    public AgencyOAuth2Authenticator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /* (non-Javadoc)
     * @see io.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
     */
    @Override
    public Optional<AuthorizedPrincipal> authenticate(String credentials) throws AuthenticationException {
        // TODO Auto-generated method stub
        return null;
    }

}
