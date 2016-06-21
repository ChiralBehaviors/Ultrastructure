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

package com.chiralbehaviors.CoRE.phantasm.graphql.mutations;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ctx;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
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
public interface CoreUserAdmin {
    @GraphQLField
    @GraphQLDescription("Update the password of the Current User of the session")
    default CoreUser setUpdatePassword(@NotNull @GraphQLName("oldPassword") String oldPassword,
                                       @NotNull @GraphQLName("newPassword") String newPassword,
                                       DataFetchingEnvironment env) {
        CoreUser currentUser = ctx(env).wrap(CoreUser.class,
                                             ctx(env).getCurrentPrincipal()
                                                     .getPrincipal());
        AgencyBasicAuthenticator.updatePassword(currentUser, newPassword,
                                                oldPassword);
        // force reauthentication
        currentUser.setAccessToken(new Object[0]);
        return currentUser;
    }
}
