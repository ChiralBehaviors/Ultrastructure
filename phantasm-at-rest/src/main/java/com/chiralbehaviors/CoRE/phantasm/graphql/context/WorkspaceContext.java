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

package com.chiralbehaviors.CoRE.phantasm.graphql.context;

import static com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema.ctx;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.Role;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema.Mutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema.Queries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.JobChronology;

import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author halhildebrand
 *
 */
public class WorkspaceContext extends ExistentialContext
        implements Queries, Mutations {

    public WorkspaceContext(Model model, Product workspace) {
        super(model, workspace);
    }

    @Override
    public Boolean authorized(UUID permission, UUID existential,
                              DataFetchingEnvironment env) {
        Model model = ctx(env);
        return model.checkPermission(model.getCurrentPrincipal()
                                          .getAsserted(),
                                     (ExistentialRecord) model.records()
                                                              .resolve(existential),
                                     (Relationship) model.records()
                                                         .resolve(permission));
    }

    @Override
    public Boolean authorizedIfActive(UUID permission, UUID existential,
                                      List<UUID> roleIds,
                                      DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Agency> roleAgencies = roleIds.stream()
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
        return model.checkExistentialPermission(roleAgencies, model.records()
                                                                   .resolve(existential),
                                                model.records()
                                                     .resolve(permission));
    }

    @Override
    public Job createJob(JobState state, DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        JobRecord record = model.getJobModel()
                                .newInitializedJob(model.records()
                                                        .resolve(state.getService()));
        state.update(record);
        record.update();
        return new Job(record);
    }

    @Override
    public CoreUser getCurrentUser(DataFetchingEnvironment env) {
        return ctx(env).wrap(CoreUser.class, ctx(env).getCurrentPrincipal()
                                                     .getPrincipal());
    }

    @Override
    public Boolean hasRole(@NotNull @GraphQLName("role") UUID roleId,
                           DataFetchingEnvironment env) {
        Model model = ctx(env);
        Role role = Optional.of(roleId)
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

    @Override
    public Boolean hasRoles(List<UUID> roleIds, DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Role> roles = roleIds.stream()
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

    @Override
    public Boolean inRoles(List<UUID> roleIds, DataFetchingEnvironment env) {
        Model model = ctx(env);
        List<Agency> roles = roleIds.stream()
                                    .map(id -> model.records()
                                                    .resolve(id))
                                    .map(e -> (Agency) e)
                                    .collect(Collectors.toList());
        return model.getCurrentPrincipal()
                    .getAsserted()
                    .containsAll(roles);
    }

    @Override
    public Job job(UUID id, DataFetchingEnvironment env) {
        return new Job(Job.fetch(env, id));
    }

    @Override
    public List<JobChronology> JobChronologies(List<UUID> ids,
                                               DataFetchingEnvironment env) {
        return ids.stream()
                  .map(id -> JobChronology.fetch(env, id))
                  .collect(Collectors.toList());
    }

    @Override
    public JobChronology jobChronology(UUID id, DataFetchingEnvironment env) {
        return JobChronology.fetch(env, id);
    }

    @Override
    public List<Job> jobs(List<UUID> ids, DataFetchingEnvironment env) {
        return ids.stream()
                  .map(id -> Job.fetch(env, id))
                  .map(r -> new Job(r))
                  .collect(Collectors.toList());
    }

    @Override
    public Boolean removeJob(UUID id, DataFetchingEnvironment env) {
        Job.fetch(env, id)
           .delete();
        return true;
    }

    @Override
    public CoreUser setUpdatePassword(String oldPassword, String newPassword,
                                      DataFetchingEnvironment env) {
        CoreUser currentUser = ctx(env).wrap(CoreUser.class,
                                             ctx(env).getCurrentPrincipal()
                                                     .getPrincipal());
        AgencyBasicAuthenticator.updatePassword(currentUser, newPassword,
                                                oldPassword);
        // force reauthentication
        currentUser.get_Properties()
                   .setAccessToken(Collections.emptyList());
        return currentUser;
    }

    @Override
    public Job updateJob(JobUpdateState state, DataFetchingEnvironment env) {
        JobRecord record = WorkspaceSchema.ctx(env)
                                          .create()
                                          .selectFrom(Tables.JOB)
                                          .where(Tables.JOB.ID.eq(state.getId()))
                                          .fetchOne();
        if (record == null) {
            return null;
        }
        state.update(record);
        record.update();
        return new Job(record);
    }
}
