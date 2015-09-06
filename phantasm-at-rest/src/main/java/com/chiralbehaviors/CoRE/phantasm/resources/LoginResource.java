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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.kernel.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;

/**
 * @author hhildebrand
 *
 */
@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {
    private static final Logger        log = LoggerFactory.getLogger(LoginResource.class);
    private final List<String>         allowedGrantTypes;
    private final EntityManagerFactory emf;

    public LoginResource(List<String> allowedGrantTypes,
                         EntityManagerFactory emf) {
        this.allowedGrantTypes = allowedGrantTypes;
        this.emf = emf;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postForToken(@FormParam("grant_type") String grantType,
                               @FormParam("username") String username,
                               @FormParam("password") String password,
                               @FormParam("client_id") String clientId) {
        if (!allowedGrantTypes.contains(grantType)) {
            Response response = Response.status(Status.METHOD_NOT_ALLOWED)
                                        .build();
            throw new WebApplicationException(response);
        }
        Model model = new ModelImpl(emf);
        try {
            AgencyAttribute attributeValue = new AgencyAttribute(model.getKernel()
                                                                      .getLogin());
            attributeValue.setTextValue(username);
            List<Agency> agencies = model.find(attributeValue);
            if (agencies.size() > 1) {
                log.error(String.format("Multiple agencies with username %s",
                                        username));
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            if (agencies.size() == 0) {
                log.warn(String.format("Attempt to login from non existent username %s",
                                       username));
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            CoreUser user = (CoreUser) model.wrap(CoreUser.class,
                                                  agencies.get(0));

            if (!AgencyBasicAuthenticator.authenticate(user, password)) {
                log.warn(String.format("Invalid attempt to login from username %s",
                                       username));
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            return generateToken(user, model).getId()
                                             .toString();
        } finally {
            model.getEntityManager()
                 .close();
        }
    }

    private AgencyAttribute generateToken(CoreUser user, Model model) {
        List<AgencyAttribute> values = model.getAgencyModel()
                                            .getAttributeValues(user.getRuleform(),
                                                                model.getKernel()
                                                                     .getAccessToken());
        int seqNum = values.isEmpty() ? 0 : values.get(values.size() - 1)
                                                  .getSequenceNumber()
                                            + 1;
        AgencyAttribute accessToken = new AgencyAttribute(user.getRuleform(),
                                                          model.getKernel()
                                                               .getAccessToken(),
                                                          model.getCurrentPrincipal()
                                                               .getPrincipal());
        accessToken.setTimestampValue(new Timestamp(System.currentTimeMillis()));
        accessToken.setSequenceNumber(seqNum);
        model.getEntityManager()
             .persist(accessToken);
        return accessToken;
    }
}