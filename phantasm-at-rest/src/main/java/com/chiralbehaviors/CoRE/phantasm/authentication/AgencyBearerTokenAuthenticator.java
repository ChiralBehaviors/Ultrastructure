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
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle.ModelAuthenticator;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.setup.Environment;

/**
 * @author hhildebrand
 *
 */
public class AgencyBearerTokenAuthenticator implements ModelAuthenticator,
        Authenticator<String, AuthorizedPrincipal> {
    private class AuthenticatorFeature implements Feature {

        @Override
        public boolean configure(FeatureContext ctx) {
            ServiceLocator locator = ServiceLocatorProvider.getServiceLocator(ctx);
            locator.inject(AgencyBearerTokenAuthenticator.this);
            return true;
        }
    }

    public static final int           ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;
    private static final long         DWELL                        = (long) (Math.random()
                                                                             * 1000);
    private final static Logger       log                          = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);
    private final static ObjectMapper OBJECT_MAPPER                = new ObjectMapper();

    public static Optional<AuthorizedPrincipal> absent() {
        try {
            Thread.sleep(DWELL);
        } catch (InterruptedException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static boolean validate(Credential credential,
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

    public static Optional<AuthorizedPrincipal> validate(RequestCredentials requestCredentials,
                                                         ExistentialAttributeRecord accessToken,
                                                         Model model) {
        // Validate the credential
        Credential credential;
        try {
            credential = OBJECT_MAPPER.treeToValue(accessToken.getJsonValue(),
                                                   Credential.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot deserialize access token {}", accessToken);
            return absent();
        }
        if (!validate(credential, requestCredentials)) {
            accessToken.delete();
            log.warn("Invalid access token {}", requestCredentials);
            return absent();
        }

        // Validate time to live
        UUID agency = accessToken.getExistential();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (!credential.isValid(accessToken.getUpdated(), currentTime)) {
            log.warn("requested access token {} for {}:{} has timed out",
                     requestCredentials, agency, model.records()
                                                      .existentialName(agency));
            accessToken.delete();
            return absent();
        }

        // Validate agency has login cap to this core instance
        if (!model.checkExistentialPermission(credential.roles.stream()
                                                   .map(id -> model.records()
                                                                   .resolve(id))
                                                   .filter(a -> a != null)
                                                   .map(e -> (Agency) e)
                                                   .collect(Collectors.toList()),
                                   model.getCoreInstance()
                                        .getRuleform(),
                                   model.getKernel()
                                        .getLOGIN_TO())) {
            log.warn("requested access token {} for {}:{} has no login capability",
                     requestCredentials, agency, model.records()
                                                      .existentialName(agency));
            accessToken.delete();
            return absent();
        }

        // Update and auth
        accessToken.setUpdated(currentTime);
        log.info("requested access token {} refreshed for {}:{}",
                 requestCredentials, agency, agency);
        return Optional.of(principalFrom(model.records()
                                              .resolve(agency),
                                         accessToken, model));
    }

    private static AuthorizedPrincipal principalFrom(Agency agency,
                                                     ExistentialAttributeRecord accessToken,
                                                     Model model) {
        Credential credential;
        try {
            credential = OBJECT_MAPPER.treeToValue(accessToken.getJsonValue(),
                                                   Credential.class);
        } catch (JsonProcessingException e) {
            log.warn("unable to deserialize access token {}", accessToken);
            return null;
        }
        return credential == null ? new AuthorizedPrincipal(agency,
                                                            Collections.singletonList(model.getKernel()
                                                                                           .getLoginRole()))
                                  : model.principalFromIds(agency,
                                                           credential.roles);
    }

    private Model              model;

    @Context
    private HttpServletRequest request;

    public Optional<AuthorizedPrincipal> authenticate(RequestCredentials credentials) throws AuthenticationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(credentials.bearerToken);
        } catch (IllegalArgumentException e) {
            // Must be a valid UUID
            log.warn("requested access token {} invalid bearer token",
                     credentials);
            return absent();
        }
        return model.create()
                    .transactionResult(c -> {
                        ExistentialAttributeRecord accessToken;
                        accessToken = model.create()
                                           .selectFrom(EXISTENTIAL_ATTRIBUTE)
                                           .where(EXISTENTIAL_ATTRIBUTE.ID.eq(uuid))
                                           .fetchOne();
                        if (accessToken == null) {
                            log.warn("requested access token {} not found",
                                     credentials);
                            return absent();
                        }
                        return validate(credentials, accessToken, model);
                    });

    }

    @Override
    public Optional<AuthorizedPrincipal> authenticate(String token) throws AuthenticationException {
        return authenticate(new RequestCredentials(request.getRemoteAddr(),
                                                   token));
    }

    public void register(Environment environment) {
        environment.jersey()
                   .register(new AuthenticatorFeature());
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
