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

import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET_PROPERTY;
import static com.chiralbehaviors.CoRE.postgres.JsonExtensions.contains;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.meta.AuthnModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.authentication.Credential;
import com.chiralbehaviors.CoRE.phantasm.graphql.UuidUtil;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class AuthxResource extends TransactionalResource {
    public static class CapabilityRequest {
        public List<UUID> capabilities = Collections.emptyList();
        public String     password;
        public String     username;
    }

    private static final Logger log    = LoggerFactory.getLogger(AuthxResource.class);

    private static final String LOGIN  = "login";
    private final static String PREFIX = "Bearer";

    public static CoreUser authenticate(String username, String password,
                                        Model model) {
        List<CoreUser> agencies = find(username, model);
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
        CoreUser user = agencies.get(0);
        if (!model.getAuthnModel()
                  .authenticate(user, password.toCharArray())) {
            log.warn(String.format("Invalid attempt to login from username %s",
                                   username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        log.info(String.format("Login successful for %s:%s", username,
                               user.getRuleform()
                                   .getId()));
        return user;
    }

    public static void deauthorize(AuthorizedPrincipal principal, String token,
                                   Model model) {
        if (token == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        UUID uuid = UuidUtil.decode(parse(token));
        model.getAuthnModel()
             .deauthorize(uuid);

        Agency user = principal.getPrincipal();
        log.info("Deauthorized token {} for {}:{}", uuid, user.getId(),
                 user.getName());
    }

    public static List<CoreUser> find(String userName, Model model) {
        ObjectNode target = JsonNodeFactory.instance.objectNode();
        target.put(LOGIN, userName);
        return model.create()
                    .selectDistinct(EXISTENTIAL.ID)
                    .from(EXISTENTIAL, FACET_PROPERTY, EDGE)
                    .where(EDGE.PARENT.equal(EXISTENTIAL.ID))
                    .and(EDGE.CHILD.equal(model.getKernel()
                                               .getCoreUser()
                                               .getId()))
                    .and(EDGE.RELATIONSHIP.equal(model.getKernel()
                                                      .getIsA()
                                                      .getId()))
                    .and(FACET_PROPERTY.EXISTENTIAL.eq(EXISTENTIAL.ID))
                    .and(contains(FACET_PROPERTY.PROPERTIES, target))
                    .fetch()
                    .stream()
                    .map(row -> row.component1())
                    .map(id -> (ExistentialRuleform) model.records()
                                                          .resolve(id))
                    .map(ex -> model.wrap(CoreUser.class, ex))
                    .collect(Collectors.toList());
    }

    public static UUID loginUuidForToken(String username, String password,
                                         HttpServletRequest httpRequest,
                                         Model model) {
        Credential cred = new Credential();
        cred.roles.add(model.getKernel()
                            .getLoginRole()
                            .getId());
        cred.ip = httpRequest.getRemoteAddr();

        AuthnModel authnModel = model.getAuthnModel();
        List<CoreUser> agencies = find(username, model);
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
        CoreUser user = agencies.get(0);
        if (!authnModel.authenticate(user, password.toCharArray())) {
            log.warn(String.format("Faild authentication from username %s",
                                   username, user.getRuleform()
                                                 .getId()));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        return authnModel.mintToken(user, cred.ip, 60, UUID.randomUUID(),
                                    cred.roles.toArray(new UUID[cred.roles.size()]))
                         .getId();
    }

    public static String parse(String header) {
        if (header == null) {
            return null;
        }
        final int space = header.indexOf(' ');
        if (space > 0) {
            final String method = header.substring(0, space);
            if (PREFIX.equalsIgnoreCase(method)) {
                return header.substring(space + 1);
            }
        }
        return null;
    }

    public static UUID requestCapability(CapabilityRequest request,
                                         HttpServletRequest httpRequest,
                                         Model model) {
        Credential cred = new Credential();
        cred.roles.add(model.getKernel()
                            .getLoginRole()
                            .getId());
        cred.roles.addAll(request.capabilities);
        cred.ip = httpRequest.getRemoteAddr();
        AuthnModel authnModel = model.getAuthnModel();
        List<CoreUser> agencies = find(request.username, model);
        if (agencies.size() > 1) {
            log.error(String.format("Multiple agencies with login name %s",
                                    request.username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        if (agencies.size() == 0) {
            log.warn(String.format("Attempt to login from non existent username %s",
                                   request.username));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        CoreUser user = agencies.get(0);
        if (!authnModel.authenticate(user, request.password.toCharArray())) {
            log.warn(String.format("Faild authentication from username %s",
                                   request.username, user.getRuleform()
                                                         .getId()));
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        return authnModel.mintToken(user, cred.ip, 60, UUID.randomUUID(),
                                    cred.roles.toArray(new UUID[cred.roles.size()]))
                         .getId();
    }

    @POST
    @Path("deauthorize")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deauthorize(@Auth AuthorizedPrincipal principal,
                            @HeaderParam(HttpHeaders.AUTHORIZATION) String bearerToken,
                            @Context DSLContext create) {
        mutate(principal, model -> {
            deauthorize(principal, bearerToken, model);
            return null;
        }, create);
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String loginForToken(@FormParam("username") String username,
                                @FormParam("password") String password,
                                @Context HttpServletRequest httpRequest,
                                @Context DSLContext create) {
        return UuidUtil.encode(mutate(null, model -> {
            return loginUuidForToken(username, password, httpRequest, model);
        }, create));
    }

    @POST
    @Path("capability")
    @Consumes(MediaType.APPLICATION_JSON)
    public String requestCapability(CapabilityRequest request,
                                    @Context HttpServletRequest httpRequest,
                                    @Context DSLContext create) {
        return UuidUtil.encode(mutate(null, model -> {
            return requestCapability(request, httpRequest, model);
        }, create));
    }
}