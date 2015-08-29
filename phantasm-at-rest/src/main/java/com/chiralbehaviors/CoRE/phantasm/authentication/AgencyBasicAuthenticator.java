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

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
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
    private final static Logger log = LoggerFactory.getLogger(AgencyBasicAuthenticator.class);

    private final EntityManagerFactory emf;

    /**
     * @param emf
     */
    public AgencyBasicAuthenticator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Optional<AuthorizedPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        String username = credentials.getUsername();
        Model model = new ModelImpl(emf);
        try {
            AgencyAttribute attributeValue = new AgencyAttribute(model.getKernel()
                                                                      .getLogin());
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
            CoreUser user = (CoreUser) model.wrap(CoreUser.class,
                                                  agencies.get(0));

            return user.authenticate(credentials.getPassword()) ? Optional.of(new AuthorizedPrincipal(user.getRuleform()))
                                                                : Optional.absent();
        } finally {
            model.getEntityManager()
                 .close();
        }
    }
}
