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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.JobTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLNonNull;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface JobMutations {

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    default Job createJob(@GraphQLNonNull @GraphQLName("state") JobState state,
                          DataFetchingEnvironment env) {
        JobRecord record = ctx(env).records()
                                   .newJob();
        state.update(record);
        record.insert();
        return new Job(record);
    }

    @GraphQLField
    default Boolean removeJob(@GraphQLName("id") String id,
                              DataFetchingEnvironment env) {
        Job.fetch(env, UUID.fromString(id))
           .delete();
        return true;
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    default Job updateJob(@GraphQLNonNull @GraphQLName("state") JobUpdateState state,
                          DataFetchingEnvironment env) {
        JobRecord record = ctx(env).create()
                                   .selectFrom(Tables.JOB)
                                   .where(Tables.JOB.ID.equal(UUID.fromString(state.id)))
                                   .fetchOne();
        state.update(record);
        record.insert();
        return new Job(record);
    }
}
