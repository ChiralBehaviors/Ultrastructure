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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static com.chiralbehaviors.CoRE.phantasm.graphql.Existential.ctx;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.UnitTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.JobChronology.JobChronologyTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Job {
    class JobTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return JobType;
        }
    }

    public static final graphql.schema.GraphQLType JobType = Existential.objectTypeOf(Job.class);

    public static JobRecord fetch(DataFetchingEnvironment env, UUID uuid) {
        return ctx(env).create()
                       .selectFrom(Tables.JOB)
                       .where(Tables.JOB.ID.equal(uuid))
                       .fetchOne();
    }

    private final JobRecord record;

    public Job(JobRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public List<Job> getActiveSubJobs(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getActiveSubJobsOf(record)
                       .stream()
                       .map(r -> new Job(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public List<Job> getAllChildren(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getAllChildren(record)
                       .stream()
                       .map(r -> new Job(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public List<Job> getChildren(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getChildren(record)
                       .stream()
                       .map(r -> new Job(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(JobChronologyTypeFunction.class)
    public List<JobChronology> getChronology(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getChronologyForJob(record)
                       .stream()
                       .map(r -> new JobChronology(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverTo()));
    }

    @GraphQLField
    public Integer getDepth(DataFetchingEnvironment env) {
        return record.getDepth();
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public Job getParent(DataFetchingEnvironment env) {
        return new Job(fetch(env, record.getParent()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getProduct()));
    }

    @GraphQLField
    public Long getQuantity() {
        return record.getQuantity()
                     .longValue();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getRequester(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getRequester()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getService()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getStatus()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return record.getVersion();
    }

    private <T> T resolve(DataFetchingEnvironment env, UUID id) {
        return ctx(env).records()
                       .resolve(id);
    }
}
