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

import static com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource.find;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.meta.AuthnModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle.ModelAuthenticator;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 *
 */
public class AgencyBasicAuthenticator implements ModelAuthenticator,
        Authenticator<BasicCredentials, AuthorizedPrincipal> {
    private static final long   DWELL = (long) (Math.random() * 1000);
    private final static Logger log   = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private Model               model;

    @Override
    public Optional<AuthorizedPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        return authenticate(credentials, model);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    private Optional<AuthorizedPrincipal> absent() {
        try {
            Thread.sleep(DWELL);
        } catch (InterruptedException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<AuthorizedPrincipal> authenticate(BasicCredentials credentials,
                                                       Model model) {

        AuthnModel authnModel = model.getAuthnModel();
        List<CoreUser> agencies = find(credentials.getUsername(), model);
        if (agencies.size() > 1) {
            log.error(String.format("Multiple agencies with login name %s",
                                    credentials.getUsername()));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        if (agencies.size() == 0) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   credentials.getUsername()));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        CoreUser user = agencies.get(0);
        if (!authnModel.authenticate(user, credentials.getPassword()
                                                      .toCharArray())) {
            log.warn(String.format("Faild authentication from username %s",
                                   credentials.getUsername(), user.getRuleform()
                                                                  .getId()));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }

        if (!canLoginToInstance(user, model)) {
            return noCapability(credentials.getUsername(), user);
        }

        boolean authenticated = model.getAuthnModel()
                                     .authenticate(user,
                                                   credentials.getPassword()
                                                              .toCharArray());
        if (authenticated) {
            return authenticatedPrincipal(user, credentials.getUsername());
        } else {
            return authenticationFailure(credentials.getUsername(), user);
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
        return model.checkExistentialPermission(user.getRoles()
                                                    .stream()
                                                    .map(r -> r.getRuleform())
                                                    .map(e -> (Agency) e)
                                                    .collect(Collectors.toList()),
                                                model.getCoreInstance()
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
