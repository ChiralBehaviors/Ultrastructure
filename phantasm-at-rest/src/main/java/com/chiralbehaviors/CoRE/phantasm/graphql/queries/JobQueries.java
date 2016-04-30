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
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.JobTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface JobQueries {

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    default Job job(@NotNull @GraphQLName("id") String id,
                    DataFetchingEnvironment env) {
        return new Job(Job.fetch(env, UUID.fromString(id)));
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    default List<Job> jobs(@NotNull @GraphQLName("ids") List<String> ids,
                           DataFetchingEnvironment env) {
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> Job.fetch(env, id))
                  .map(r -> new Job(r))
                  .collect(Collectors.toList());
    }
}
