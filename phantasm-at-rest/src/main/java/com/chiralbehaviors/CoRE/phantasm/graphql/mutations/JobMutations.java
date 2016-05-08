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

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface JobMutations {

    @GraphQLField
    default Job createJob(@NotNull @GraphQLName("state") JobState state,
                          DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        JobRecord record = model.getJobModel()
                                .newInitializedJob(model.records()
                                                        .resolve(UUID.fromString(state.service)));
        state.update(record);
        record.update();
        return new Job(record);
    }

    @GraphQLField
    default Boolean removeJob(@NotNull @GraphQLName("id") String id,
                              DataFetchingEnvironment env) {
        Job.fetch(env, UUID.fromString(id))
           .delete();
        return true;
    }

    @GraphQLField
    default Job updateJob(@NotNull @GraphQLName("state") JobUpdateState state,
                          DataFetchingEnvironment env) {
        JobRecord record = WorkspaceSchema.ctx(env)
                                          .create()
                                          .selectFrom(Tables.JOB)
                                          .where(Tables.JOB.ID.equal(UUID.fromString(state.id)))
                                          .fetchOne();
        state.update(record);
        record.update();
        return new Job(record);
    }
}
