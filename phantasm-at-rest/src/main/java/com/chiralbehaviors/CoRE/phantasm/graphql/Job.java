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
import java.math.BigDecimal;
import java.util.UUID;

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

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getAssignTo());
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getDeliverFrom());
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverTo(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getDeliverTo());
    }

    @GraphQLField
    public Integer getDepth(DataFetchingEnvironment env) {
        return fetch(env).getDepth();
    }

    @GraphQLField
    public UUID getId(DataFetchingEnvironment env) {
        return fetch(env).getId();
    }

    @GraphQLField
    public String getNotes(DataFetchingEnvironment env) {
        return fetch(env).getNotes();
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public JobRecord getParent(DataFetchingEnvironment env) {
        return fetch(env, fetch(env).getParent());
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getProduct(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getProduct());
    }

    @GraphQLField
    public BigDecimal getQuantity(DataFetchingEnvironment env) {
        return fetch(env).getQuantity();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getQuantityUnit());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getRequester(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getRequester());
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getService());
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getStatus(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getStatus());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return resolve(env, fetch(env).getUpdatedBy());
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return fetch(env).getVersion();
    }

    private JobRecord fetch(DataFetchingEnvironment env) {
        return (JobRecord) env.getSource();
    }

    private <T> T resolve(DataFetchingEnvironment env, UUID id) {
        return ctx(env).records()
                       .resolve(id);
    }
}
