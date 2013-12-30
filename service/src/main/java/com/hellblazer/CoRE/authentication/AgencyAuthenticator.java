/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.authentication;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.access.AgencyAttribute;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.security.AuthenticatedPrincipal;
import com.hellblazer.CoRE.utils.Util;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

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
