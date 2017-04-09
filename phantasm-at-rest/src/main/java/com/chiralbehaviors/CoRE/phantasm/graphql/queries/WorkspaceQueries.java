/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext.getWorkspace;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;

import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface WorkspaceQueries {

    @GraphQLField
    @GraphQLDescription("Lookup the ID of the item in the workspace by (potentially scoped) name")
    default String lookup(@GraphQLDescription("The namespace within the workspace imports") @GraphQLName("namespace") String namespace,
                          @GraphQLDescription("The name within the workspace") @NotNull @GraphQLName("name") String name,
                          DataFetchingEnvironment env) {

        Model model = WorkspaceSchema.ctx(env);
        Product wspProduct = getWorkspace(env);
        if (!model.checkRead(wspProduct)) {
            return null;
        }
        WorkspaceScope scoped = model.getWorkspaceModel()
                                     .getScoped(wspProduct);
        return namespace == null ? scoped.lookupId(name)
                                         .toString()
                                 : scoped.lookupId(namespace, name)
                                         .toString();
    }
}
