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

package com.chiralbehaviors.CoRE.authentication;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.access.AgencyAttribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.chiralbehaviors.CoRE.utils.Util;
import com.google.common.base.Optional;

/**
 * @author hhildebrand
 * 
 */
public class AgencyAuthenticator implements
        Authenticator<BasicCredentials, AuthenticatedPrincipal> {
    private final static Logger log = LoggerFactory.getLogger(AgencyAuthenticator.class);

    private final Model         model;

    /**
     * @param model
     */
    public AgencyAuthenticator(Model model) {
        this.model = model;
    }

    @Override
    public Optional<AuthenticatedPrincipal> authenticate(BasicCredentials credentials)
                                                                                      throws AuthenticationException {
        String username = credentials.getUsername();
        AgencyAttribute attributeValue = new AgencyAttribute(
                                                             model.getKernel().getLoginAttribute());
        attributeValue.setTextValue(username);
        List<Agency> agencies = model.find(attributeValue);
        if (agencies.size() > 1) {
            log.error(String.format("Multiple agencys with username %s",
                                    username));
            return Optional.absent();
        }
        if (agencies.size() == 0) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   username));
            return Optional.absent();
        }
        Aspect<Agency> loginAspect = new Aspect<Agency>(
                                                        model.getKernel().getIsA(),
                                                        model.getKernel().getCoreUser());
        Facet<Agency, AgencyAttribute> loginFacet = model.getAgencyModel().getFacet(agencies.get(0),
                                                                                    loginAspect);
        AgencyAttribute storedHashValue = loginFacet.getValue(model.getKernel().getPasswordHashAttribute());

        String providedHash;
        try {
            providedHash = Util.md5Hash(credentials.getPassword());
        } catch (IOException e) {
            log.warn("Cannot create hash from password", e);
            return Optional.absent();
        }
        if (!providedHash.equals(storedHashValue.getTextValue())) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   username));
            return Optional.absent();
        }

        return Optional.of(new AuthenticatedPrincipal(loginFacet.asRuleform()));
    }
}
