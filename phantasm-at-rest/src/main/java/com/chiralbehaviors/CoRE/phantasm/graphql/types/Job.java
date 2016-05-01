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
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.JobChronology.JobChronologyTypeFunction;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Job {

    public static class JobState {
        @GraphQLField
        public String assignTo;
        @GraphQLField
        public String deliverFrom;
        @GraphQLField
        public String deliverTo;
        @GraphQLField
        public String notes;
        @GraphQLField
        public String product;
        @GraphQLField
        public Float  quantity;
        @GraphQLField
        public String requester;
        @GraphQLField
        public String service;
        @GraphQLField
        public String status;
        @GraphQLField
        public String unit;

        public void update(JobRecord r) {
            if (assignTo != null) {
                r.setAssignTo(UUID.fromString(assignTo));
            }
            if (deliverFrom != null) {
                r.setDeliverFrom(UUID.fromString(deliverFrom));
            }
            if (deliverTo != null) {
                r.setDeliverTo(UUID.fromString(deliverTo));
            }
            if (notes != null) {
                r.setNotes(notes);
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (quantity != null) {
                r.setQuantity(BigDecimal.valueOf(quantity));
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (requester != null) {
                r.setRequester(UUID.fromString(requester));
            }
            if (service != null) {
                r.setService(UUID.fromString(service));
            }
            if (status != null) {
                r.setStatus(UUID.fromString(status));
            }
            if (unit != null) {
                r.setQuantityUnit(UUID.fromString(unit));
            }
        }
    }

    public static class JobUpdateState extends JobState {
        @GraphQLField
        public String id;
    }

    public static JobRecord fetch(DataFetchingEnvironment env, UUID uuid) {
        return ctx(env).create()
                       .selectFrom(Tables.JOB)
                       .where(Tables.JOB.ID.equal(uuid))
                       .fetchOne();
    }

    private final JobRecord record;

    public Job(JobRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField
    public List<Job> getActiveChildren(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getActiveSubJobsOf(record)
                       .stream()
                       .map(r -> new Job(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField

    public List<Job> getAllChildren(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getAllChildren(record)
                       .stream()
                       .map(r -> new Job(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
    }

    @GraphQLField

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
    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField
    public Location getDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverTo()));
    }

    @GraphQLField
    public Integer getDepth(DataFetchingEnvironment env) {
        return record.getDepth();
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
    }

    @GraphQLField

    public List<Protocol> getMatchingProtocols(DataFetchingEnvironment env) {
        return ctx(env).getJobModel()
                       .getProtocols(record)
                       .keySet()
                       .stream()
                       .map(r -> new Protocol(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField

    public Job getParent(DataFetchingEnvironment env) {
        UUID parent = record.getParent();
        return parent == null ? null : new Job(fetch(env, parent));
    }

    @GraphQLField

    public Product getProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getProduct()));
    }

    @GraphQLField
    public Long getQuantity() {
        BigDecimal quantity = record.getQuantity();
        return quantity == null ? null : quantity.longValue();
    }

    @GraphQLField
    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField
    public Agency getRequester(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getRequester()));
    }

    @GraphQLField

    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getService()));
    }

    @GraphQLField

    public StatusCode getStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getStatus()));
    }

    @GraphQLField
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return record.getVersion();
    }
}
