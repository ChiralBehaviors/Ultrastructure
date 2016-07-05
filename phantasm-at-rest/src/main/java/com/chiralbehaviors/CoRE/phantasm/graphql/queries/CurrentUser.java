/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.graphql.queries;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ctx;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.Role;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;

import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
@GraphQLDescription("Queries for the current user.  The current user is defined as the authenticated principal of the session.  "
                    + "The authenticated principal is the authenticated CoRE User and any asserted Roles that have been granted to that user")
public interface CurrentUser {
    @GraphQLField
    @GraphQLDescription("Return true if the current user is authorized to exercise the permission on the entity, granted through the actively asserted roles of the current user")
    default Boolean authorized(@NotNull @GraphQLName("permission") String permission,
                               @NotNull @GraphQLName("entity") String existential,
                               DataFetchingEnvironment env) {
        Model model = ctx(env);
        return model.checkPermission(model.getCurrentPrincipal()
                                          .getAsserted(),
                                     (ExistentialRuleform) model.records()
                                                                .resolve(UUID.fromString(existential)),
                                     (Relationship) model.records()
                                                         .resolve(UUID.fromString(permission)));
    }

    @GraphQLField
    @GraphQLDescription("Return true if the current user is authorized to exercise the permission on the entity, through the roles granted to the current user")
    default Boolean authorizedIfActive(@NotNull @GraphQLName("permission") String permission,
                                       @NotNull @GraphQLName("entity") String existential,
                                       @NotNull @GraphQLName("roles") List<String> roleIds,
                                       DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Agency> roleAgencies = roleIds.stream()
                                           .map(s -> UUID.fromString(s))
                                           .map(id -> model.records()
                                                           .resolve(id))
                                           .map(e -> (Agency) e)
                                           .collect(Collectors.toList());
        List<Role> roles = roleAgencies.stream()
                                       .map(a -> model.wrap(Role.class, a))
                                       .collect(Collectors.toList());
        if (!model.wrap(CoreUser.class, model.getCurrentPrincipal()
                                             .getPrincipal())
                  .getRoles()
                  .containsAll(roles)) {
            return false;
        }
        return model.checkPermission(roleAgencies,
                                     (ExistentialRuleform) model.records()
                                                                .resolve(UUID.fromString(existential)),
                                     (Relationship) model.records()
                                                         .resolve(UUID.fromString(permission)));
    }

    @GraphQLField
    @GraphQLDescription("Return the current user")
    default CoreUser getCurrentUser(DataFetchingEnvironment env) {
        return ctx(env).wrap(CoreUser.class, ctx(env).getCurrentPrincipal()
                                                     .getPrincipal());
    }

    @GraphQLField
    @GraphQLDescription("Return true if the current user has been granted the role")
    default Boolean hasRole(@NotNull @GraphQLName("role") String roleId,
                            DataFetchingEnvironment env) {
        Model model = ctx(env);
        Role role = Optional.of(roleId)
                            .map(s -> UUID.fromString(s))
                            .map(id -> model.records()
                                            .resolve(id))
                            .map(e -> (Agency) e)
                            .map(a -> model.wrap(Role.class, a))
                            .get();
        CoreUser authenticated = model.wrap(CoreUser.class,
                                            model.getCurrentPrincipal()
                                                 .getPrincipal());
        return authenticated.getRoles()
                            .contains(role);
    }

    @GraphQLField
    @GraphQLDescription("Return true if the current user has all the roles")
    default Boolean hasRoles(@NotNull @GraphQLName("roles") List<String> roleIds,
                             DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Role> roles = roleIds.stream()
                                  .map(s -> UUID.fromString(s))
                                  .map(id -> model.records()
                                                  .resolve(id))
                                  .map(e -> (Agency) e)
                                  .map(a -> model.wrap(Role.class, a))
                                  .collect(Collectors.toList());
        CoreUser authenticated = model.wrap(CoreUser.class,
                                            model.getCurrentPrincipal()
                                                 .getPrincipal());
        return authenticated.getRoles()
                            .containsAll(roles);
    }

    @GraphQLField
    @GraphQLDescription("Return true if the current user has all the roles provided as actively asserted roles")
    default Boolean inRoles(@NotNull @GraphQLName("roles") List<String> roleIds,
                            DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Agency> roles = roleIds.stream()
                                    .map(s -> UUID.fromString(s))
                                    .map(id -> model.records()
                                                    .resolve(id))
                                    .map(e -> (Agency) e)
                                    .collect(Collectors.toList());
        return model.getCurrentPrincipal()
                    .getAsserted()
                    .containsAll(roles);
    }
}
