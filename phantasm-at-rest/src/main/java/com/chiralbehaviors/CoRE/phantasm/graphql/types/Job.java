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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Job {

    public static class JobState {
        private static final String         ASSIGN_TO    = "assignTo";
        private static final String         AUTHORITY    = "authority";
        private static final String         DELIVER_FROM = "deliverFrom";
        private static final String         DELIVER_TO   = "deliverTo";
        private static final String         NOTES        = "notes";
        private static final String         PRODUCT      = "product";
        private static final String         QUANTITY     = "quantity";
        private static final String         REQUESTER    = "requester";
        private static final String         SERVICE      = "service";
        private static final String         STATUS       = "status";
        private static final String         UNIT         = "unit";
        protected final Map<String, Object> state;

        public JobState(HashMap<String, Object> state) {
            this.state = state;
        }

        @GraphQLField
        public UUID getAssignTo() {
            return (UUID) state.get(ASSIGN_TO);
        }

        @GraphQLField
        public UUID getAuthority() {
            return (UUID) state.get(AUTHORITY);
        }

        @GraphQLField
        public UUID getDeliverFrom() {
            return (UUID) state.get(DELIVER_FROM);
        }

        @GraphQLField
        public UUID getDeliverTo() {
            return (UUID) state.get(DELIVER_TO);
        }

        @GraphQLField
        public String getNotes() {
            return (String) state.get(NOTES);
        }

        @GraphQLField
        public UUID getProduct() {
            return (UUID) state.get(PRODUCT);
        }

        @GraphQLField
        public Float getQuantity() {
            return (Float) state.get(QUANTITY);
        }

        @GraphQLField
        public UUID getRequester() {
            return (UUID) state.get(REQUESTER);
        }

        @GraphQLField
        public UUID getService() {
            return (UUID) state.get(SERVICE);
        }

        @GraphQLField
        public UUID getStatus() {
            return (UUID) state.get(STATUS);
        }

        @GraphQLField
        public UUID getUnit() {
            return (UUID) state.get(UNIT);
        }

        public void update(JobRecord r) {
            if (state.containsKey(AUTHORITY)) {
                r.setAuthority((UUID) state.get(AUTHORITY));
            }
            if (state.containsKey(ASSIGN_TO)) {
                r.setAssignTo((UUID) state.get(ASSIGN_TO));
            }
            if (state.containsKey(DELIVER_TO)) {
                r.setDeliverTo((UUID) state.get(DELIVER_TO));
            }
            if (state.containsKey(DELIVER_FROM)) {
                r.setDeliverFrom((UUID) state.get(DELIVER_FROM));
            }
            if (state.containsKey(NOTES)) {
                r.setNotes((String) state.get(NOTES));
            }
            if (state.containsKey(PRODUCT)) {
                r.setProduct((UUID) state.get(PRODUCT));
            }
            if (state.containsKey(QUANTITY)) {
                r.setQuantity((BigDecimal) state.get(QUANTITY));
            }
            if (state.containsKey(REQUESTER)) {
                r.setRequester((UUID) state.get(REQUESTER));
            }
            if (state.containsKey(SERVICE)) {
                r.setService((UUID) state.get(SERVICE));
            }
            if (state.containsKey(STATUS)) {
                r.setStatus((UUID) state.get(STATUS));
            }
            if (state.containsKey(UNIT)) {
                r.setQuantityUnit((UUID) state.get(UNIT));
            }
        }
    }

    public static class JobUpdateState extends JobState {

        public JobUpdateState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getId() {
            return (UUID) state.get(ID);
        }
    }

    private static final String ID = "id";

    public static JobRecord fetch(DataFetchingEnvironment env, UUID uuid) {
        return WorkspaceSchema.ctx(env)
                              .create()
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
        return WorkspaceSchema.ctx(env)
                              .getJobModel()
                              .getActiveSubJobsOf(record)
                              .stream()
                              .map(r -> new Job(r))
                              .collect(Collectors.toList());
    }

    @GraphQLField

    public List<Job> getAllChildren(DataFetchingEnvironment env) {
        return WorkspaceSchema.ctx(env)
                              .getJobModel()
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
    public Agency getAuthority(DataFetchingEnvironment env) {
        ExistentialRecord a = resolve(env, record.getAuthority());
        if (a == null) {
            return null;
        }
        return new Agency(a);
    }

    @GraphQLField

    public List<Job> getChildren(DataFetchingEnvironment env) {
        return WorkspaceSchema.ctx(env)
                              .getJobModel()
                              .getChildren(record)
                              .stream()
                              .map(r -> new Job(r))
                              .collect(Collectors.toList());
    }

    @GraphQLField
    public List<JobChronology> getChronology(DataFetchingEnvironment env) {
        return WorkspaceSchema.ctx(env)
                              .getJobModel()
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
    public UUID getId() {
        return record.getId();
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
