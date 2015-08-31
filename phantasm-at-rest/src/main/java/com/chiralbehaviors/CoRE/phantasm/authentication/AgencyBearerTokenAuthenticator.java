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

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */
public class AgencyBearerTokenAuthenticator
        implements Authenticator<String, AuthorizedPrincipal> {
    private final static Logger log = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private final EntityManagerFactory emf;

    /**
     * @param emf
     */
    public AgencyBearerTokenAuthenticator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static final int ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;

    @Override
    public Optional<AuthorizedPrincipal> authenticate(String id) throws AuthenticationException {
        Model model = new ModelImpl(emf);
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.info("requested access token {} not found", id);
            // Must be a valid UUID
            return Optional.absent();
        }
        AgencyAttribute accessToken = model.getEntityManager()
                                           .find(AgencyAttribute.class, uuid);

        if (accessToken.getTimestampValue()
                       .compareTo(new Timestamp(System.currentTimeMillis())) < 0) {
            model.getEntityManager()
                 .remove(accessToken);
            return Optional.absent();
        }

        return Optional.of(new AuthorizedPrincipal(accessToken.getAgency()));
    }

}
