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
import java.util.Arrays;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.AgencyAttribute;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.security.Credential;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */
public class AgencyBearerTokenAuthenticator
        implements Authenticator<RequestCredentials, AuthorizedPrincipal> {
    public static final int     ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;
    private final static Logger log                          = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private final EntityManagerFactory emf;
    private final CoreInstance         coreInstance;
    private final Relationship         loginTo;

    /**
     * @param emf
     */
    public AgencyBearerTokenAuthenticator(EntityManagerFactory emf) {
        this.emf = emf;
        try (Model model = new ModelImpl(emf)) {
            coreInstance = model.getCoreInstance();
            loginTo = model.getKernel()
                           .getLOGIN_TO();
        }
    }

    @Override
    public Optional<AuthorizedPrincipal> authenticate(RequestCredentials credentials) throws AuthenticationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(credentials.bearerToken);
        } catch (IllegalArgumentException e) {
            // Must be a valid UUID
            log.warn("requested access token {} invalid bearer token",
                     credentials);
            return Optional.absent();
        }
        try (Model model = new ModelImpl(emf);) {
            EntityManager em = model.getDSLContext();
            em.getTransaction()
              .begin();

            AgencyAttribute accessToken = em.find(AgencyAttribute.class, uuid);
            if (accessToken == null) {
                log.warn("requested access token {} not found", credentials);
                return Optional.absent();
            }
            Optional<AuthorizedPrincipal> returned = validate(accessToken,
                                                              credentials, em,
                                                              model);
            em.getTransaction()
              .commit();
            return returned;
        }
    }

    public AuthorizedPrincipal principalFrom(Agency agency,
                                             AgencyAttribute accessToken,
                                             Model model) {
        Credential credential = accessToken.getJsonValue(Credential.class);
        return credential == null ? new AuthorizedPrincipal(agency)
                                  : model.principalFrom(agency,
                                                        credential.capabilities);
    }

    private boolean validate(Credential credential,
                             RequestCredentials onRequest) {
        if (credential == null) {
            log.warn("Invalid access token {}", onRequest);
            return false;
        }
        if (credential.ip == null) {
            log.warn("Invalid access token {}", onRequest);
            return false;
        }
        return credential.ip.equals(onRequest.remoteIp);
    }

    private Optional<AuthorizedPrincipal> validate(AgencyAttribute accessToken,
                                                   RequestCredentials requestCredentials,
                                                   EntityManager em,
                                                   Model model) {
        // Validate the credential
        Credential credential = (accessToken.getJsonValue(Credential.class));
        if (!validate(credential, requestCredentials)) {
            em.remove(accessToken);
            log.warn("Invalid access token {}", requestCredentials);
            return Optional.absent();
        }

        // Validate time to live
        Agency agency = accessToken.getAgency();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (!credential.isValid(accessToken.getUpdated(), currentTime)) {
            log.warn("requested access token {} for {}:{} has timed out",
                     requestCredentials, agency.getId(), agency);
            em.remove(accessToken);
            return Optional.absent();
        }

        // Validate agency has login cap to this core instance
        if (!model.getAgencyModel()
                  .checkCapability(Arrays.asList(agency),
                                   coreInstance.getRuleform(), loginTo)) {
            log.warn("requested access token {} for {}:{} has no login capability",
                     requestCredentials, agency.getId(), agency);
            em.remove(accessToken);
            return Optional.absent();
        }

        // Update and auth
        accessToken.setUpdated(currentTime);
        log.info("requested access token {} refreshed for {}:{}",
                 requestCredentials, agency.getId(), agency);
        return Optional.of(principalFrom(agency, accessToken, model));
    }

}
