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

package com.chiralbehaviors.CoRE.phantasm.authentication;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Context;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.bcrypt.BCrypt;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AgencyBasicAuthenticator
        implements Authenticator<BasicCredentials, AuthorizedPrincipal> {
    private static final long   DWELL = (long) (Math.random() * 10000);
    private final static Logger log   = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    public static boolean authenticate(CoreUser user, String password) {
        return BCrypt.checkpw(password, user.getPasswordHash());
    }

    public static void resetPassword(CoreUser user, String newPassword) {
        user.setPasswordHash(BCrypt.hashpw(newPassword,
                                           BCrypt.gensalt(user.getPasswordRounds())));
    }

    public static void updatePassword(CoreUser user, String newPassword,
                                      String oldPassword) {
        if (BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            user.setPasswordHash(BCrypt.hashpw(newPassword,
                                               BCrypt.gensalt(user.getPasswordRounds())));
        }
    }

    protected void setCreate(DSLContext create) {
        this.create = create;
    }

    @Context
    private DSLContext create;

    @Override
    public Optional<AuthorizedPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        try (Model model = new ModelImpl(create)) {
            return authenticate(credentials, model);
        }
    }

    private Optional<AuthorizedPrincipal> absent() {
        try {
            Thread.sleep(DWELL);
        } catch (InterruptedException e) {
            return Optional.absent();
        }
        return Optional.absent();
    }

    private Optional<AuthorizedPrincipal> authenticate(BasicCredentials credentials,
                                                       Model model) {
        String username = credentials.getUsername();
        List<? extends ExistentialRuleform> agencies = model.getPhantasmModel()
                                                            .findByAttributeValue(model.getKernel()
                                                                                       .getLogin(),
                                                                                  username);
        if (agencies.size() > 1) {
            log.error(String.format("Multiple agencies with username %s",
                                    username));
            return absent();
        }
        if (agencies.size() == 0) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   username));
            return absent();
        }
        ExistentialRuleform agency = agencies.get(0);
        if (agency.getDomain() != ExistentialDomain.Agency) {
            log.warn(String.format("Attempt to login from non existent agency %s",
                                   username));
            return absent();
        }
        CoreUser user = (CoreUser) model.wrap(CoreUser.class, agency);

        if (!canLoginToInstance(user, model)) {
            return noCapability(username, user);
        }

        boolean authenticated = authenticate(user, credentials.getPassword());
        if (authenticated) {
            return authenticatedPrincipal(user, username);
        } else {
            return authenticationFailure(username, user);
        }
    }

    private Optional<AuthorizedPrincipal> authenticatedPrincipal(CoreUser user,
                                                                 String username) {
        log.info(String.format("Authentication success for %s:%s",
                               user.getRuleform()
                                   .getId(),
                               username));
        return Optional.of(new AuthorizedPrincipal((Agency) user.getRuleform()));
    }

    private Optional<AuthorizedPrincipal> authenticationFailure(String username,
                                                                CoreUser user) {
        log.warn(String.format("Authentication failure for %s:%s",
                               user.getRuleform()
                                   .getId(),
                               username));
        return absent();
    }

    private boolean canLoginToInstance(CoreUser user, Model model) {
        return model.getPhantasmModel()
                    .checkCapability(Arrays.asList((Agency) user.getRuleform()),
                                     (ExistentialRuleform) model.getCoreInstance()
                                                                .getRuleform(),
                                     model.getKernel()
                                          .getLOGIN_TO());
    }

    private Optional<AuthorizedPrincipal> noCapability(String username,
                                                       CoreUser user) {
        log.warn(String.format("Authentication failure for %s:%s - no login capability",
                               user.getRuleform()
                                   .getId(),
                               username));
        return absent();
    }
}
