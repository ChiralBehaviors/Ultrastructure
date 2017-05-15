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

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;

import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLDescription("Queries for the current user.  The current user is defined as the authenticated principal of the session.  "
                    + "The authenticated principal is the authenticated CoRE User and any asserted Roles that have been granted to that user")
public interface CurrentUser {
    @GraphQLField
    @GraphQLDescription("Return true if the current user is authorized to exercise the permission on the entity, granted through the actively asserted roles of the current user")
    Boolean authorized(@NotNull @GraphQLName("permission") UUID permission,
                       @NotNull @GraphQLName("entity") UUID existential,
                       DataFetchingEnvironment env);

    @GraphQLField
    @GraphQLDescription("Return true if the current user is authorized to exercise the permission on the entity, through the roles granted to the current user")
    Boolean authorizedIfActive(@NotNull @GraphQLName("permission") UUID permission,
                               @NotNull @GraphQLName("entity") UUID existential,
                               @NotNull @GraphQLName("roles") List<UUID> roleIds,
                               DataFetchingEnvironment env);

    @GraphQLField
    @GraphQLDescription("Return the current user")
    CoreUser getCurrentUser(DataFetchingEnvironment env);

    @GraphQLField
    @GraphQLDescription("Return true if the current user has been granted the role")
    Boolean hasRole(@NotNull @GraphQLName("role") UUID roleId,
                    DataFetchingEnvironment env);

    @GraphQLField
    @GraphQLDescription("Return true if the current user has all the roles")
    Boolean hasRoles(@NotNull @GraphQLName("roles") List<UUID> roleIds,
                     DataFetchingEnvironment env);

    @GraphQLField
    @GraphQLDescription("Return true if the current user has all the roles provided as actively asserted roles")
    Boolean inRoles(@NotNull @GraphQLName("roles") List<UUID> roleIds,
                    DataFetchingEnvironment env);
}
