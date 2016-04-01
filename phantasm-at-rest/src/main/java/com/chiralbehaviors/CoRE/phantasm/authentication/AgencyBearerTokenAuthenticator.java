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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.security.Credential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */
public class AgencyBearerTokenAuthenticator
        implements Authenticator<String, AuthorizedPrincipal> {
    public static final int     ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;
    private final static Logger log                          = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private final Model         model;
    private final CoreInstance  coreInstance;
    private final Relationship  loginTo;
    @Context
    private HttpServletRequest  servletRequest;

    public AgencyBearerTokenAuthenticator(Model m) {
        model = m;
        coreInstance = model.getCoreInstance();
        loginTo = model.getKernel()
                       .getLOGIN_TO();
    }

    @Override
    public Optional<AuthorizedPrincipal> authenticate(String token) throws AuthenticationException {
        return authenticate(new RequestCredentials(servletRequest.getRemoteAddr(),
                                                   token));
    }

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
        ExistentialAttributeRecord accessToken = model.create()
                                                      .selectFrom(EXISTENTIAL_ATTRIBUTE)
                                                      .where(EXISTENTIAL_ATTRIBUTE.ID.eq(uuid))
                                                      .fetchOne();
        if (accessToken == null) {
            log.warn("requested access token {} not found", credentials);
            return Optional.absent();
        }
        return validate(accessToken, credentials);
    }

    public AuthorizedPrincipal principalFrom(Agency agency,
                                             ExistentialAttributeRecord accessToken,
                                             Model model) {
        Credential credential;
        try {
            credential = new ObjectMapper().treeToValue(accessToken.getJsonValue(),
                                                        Credential.class);
        } catch (JsonProcessingException e) {
            log.warn("unable to deserialize access token {}", accessToken);
            return null;
        }
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

    private Optional<AuthorizedPrincipal> validate(ExistentialAttributeRecord accessToken,
                                                   RequestCredentials requestCredentials) {
        // Validate the credential
        Credential credential;
        try {
            credential = new ObjectMapper().treeToValue(accessToken.getJsonValue(),
                                                        Credential.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot deserialize access token {}", accessToken);
            return Optional.absent();
        }
        if (!validate(credential, requestCredentials)) {
            accessToken.delete();
            log.warn("Invalid access token {}", requestCredentials);
            return Optional.absent();
        }

        // Validate time to live
        UUID agency = accessToken.getExistential();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (!credential.isValid(accessToken.getUpdated(), currentTime)) {
            log.warn("requested access token {} for {}:{} has timed out",
                     requestCredentials, agency, model.records()
                                                      .resolve(agency));
            accessToken.delete();
            return Optional.absent();
        }

        // Validate agency has login cap to this core instance
        if (!model.getPhantasmModel()
                  .checkCapability(Arrays.asList(model.records()
                                                      .resolve(agency)),
                                   (ExistentialRuleform) coreInstance.getRuleform(),
                                   loginTo)) {
            log.warn("requested access token {} for {}:{} has no login capability",
                     requestCredentials, agency, model.records()
                                                      .resolve(agency));
            accessToken.delete();
            return Optional.absent();
        }

        // Update and auth
        accessToken.setUpdated(currentTime);
        log.info("requested access token {} refreshed for {}:{}",
                 requestCredentials, agency, agency);
        return Optional.of(principalFrom(model.records()
                                              .resolve(agency),
                                         accessToken, model));
    }

}
