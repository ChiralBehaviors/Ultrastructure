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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.UsOAuthFactory;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.security.Credential;

import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class AuthxResource extends TransactionalResource {

    public static class CapabilityRequest {
        public List<UUID> capabilities = Collections.emptyList();
        public String     password;
        public String     username;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthxResource.class);
    private final Attribute     login;

    public AuthxResource(EntityManagerFactory emf) {
        super(emf);
        Model model = new ModelImpl(emf);
        try {
            login = model.getKernel()
                         .getLogin();
        } finally {
            model.getEntityManager()
                 .close();
        }
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public UUID loginForToken(@FormParam("username") String username,
                              @FormParam("password") String password,
                              @Context HttpServletRequest httpRequest) {
        return perform(null, model -> {
            Credential cred = new Credential();
            cred.ip = httpRequest.getRemoteAddr();
            return generateToken(cred, authenticate(username, password, model),
                                 model).getId();
        });
    }

    @POST
    @Path("deauthorize")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deauthorize(@Auth AuthorizedPrincipal principal,
                            @HeaderParam(HttpHeaders.AUTHORIZATION) String bearerToken) {
        perform(principal, model -> {
            UUID uuid = UUID.fromString(UsOAuthFactory.parse(bearerToken));
            EntityManager em = model.getEntityManager();
            em.remove(em.find(AgencyAttribute.class, uuid));
            Agency user = principal.getPrincipal();
            log.info("Deauthorized {} for {}:{}", uuid, user.getId(),
                     user.getName());
            return null;
        });
    }

    @POST
    @Path("capability")
    @Consumes(MediaType.APPLICATION_JSON)
    public UUID requestCapability(CapabilityRequest request,
                                  @Context HttpServletRequest httpRequest) {
        return perform(null, model -> {
            Credential cred = new Credential();
            cred.capabilities = request.capabilities;
            cred.ip = httpRequest.getRemoteAddr();
            return generateToken(cred,
                                 authenticate(request.username,
                                              request.password, model),
                                 model).getId();
        });
    }

    private CoreUser authenticate(String username, String password,
                                  Model model) {
        AgencyAttribute attributeValue = new AgencyAttribute(login);
        attributeValue.setValue(username);
        List<Agency> agencies = model.find(attributeValue);
        if (agencies.size() > 1) {
            log.error(String.format("Multiple agencies with login name %s",
                                    username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        if (agencies.size() == 0) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        CoreUser user = (CoreUser) model.wrap(CoreUser.class, agencies.get(0));

        if (!AgencyBasicAuthenticator.authenticate(user, password)) {
            log.warn(String.format("Invalid attempt to login from username %s",
                                   username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        return user;
    }

    private AgencyAttribute generateToken(Credential cred, CoreUser user,
                                          Model model) {
        EntityManager em = model.getEntityManager();
        em.getTransaction()
          .begin();
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
        accessToken.setValue(cred);
        accessToken.setUpdated(new Timestamp(System.currentTimeMillis()));
        accessToken.setSequenceNumber(seqNum);
        model.getEntityManager()
             .persist(accessToken);
        em.getTransaction()
          .commit();
        return accessToken;
    }
}