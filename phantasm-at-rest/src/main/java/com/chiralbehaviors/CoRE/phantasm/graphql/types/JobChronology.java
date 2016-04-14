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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;

import java.lang.reflect.AnnotatedType;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job.JobTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class JobChronology {
    class JobChronologyTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return JobChronologyType;
        }
    }

    public static final graphql.schema.GraphQLType JobChronologyType = Existential.objectTypeOf(JobChronology.class);

    private final JobChronologyRecord              record;

    public JobChronology(JobChronologyRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
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
    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    @GraphQLType(JobTypeFunction.class)
    public Job getJob(DataFetchingEnvironment env) {
        return new Job(Job.fetch(env, record.getJob()));
    }

    @GraphQLField
    public String getNotes(DataFetchingEnvironment env) {
        return record.getNotes();
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
    public Integer getSequenceNumber(DataFetchingEnvironment env) {
        return record.getSequenceNumber();
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
    public Long getUpdateDate() {
        return record.getUpdateDate()
                     .getTime();
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
