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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.ctx;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AttributeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;

import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author halhildebrand
 *
 */
public class ExistentialContext extends WorkspaceContext {
    public ExistentialContext(Model model,
                              com.chiralbehaviors.CoRE.domain.Product workspace) {
        super(model, workspace);
    }

    @Override
    public Agency createAgency(ExistentialState state,
                               DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newAgency();
        state.update(record);
        record.insert();
        return new Agency(record);
    }

    @Override
    public Attribute createAttribute(AttributeState state,
                                     DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newAttribute();
        state.update(record);
        record.insert();
        return new Attribute(record);
    }

    @Override
    public Interval createInterval(ExistentialState state,
                                   DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newInterval();
        state.update(record);
        record.insert();
        return new Interval(record);
    }

    @Override
    public Location createLocation(ExistentialState state,
                                   DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newLocation();
        state.update(record);
        record.insert();
        return new Location(record);
    }

    @Override
    public Product createProduct(ExistentialState state,
                                 DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newProduct();
        state.update(record);
        record.insert();
        return new Product(record);
    }

    @Override
    public Relationship createRelationship(RelationshipState state,
                                           DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newRelationship();
        state.update(record);
        record.insert();
        return new Relationship(record);
    }

    @Override
    public StatusCode createStatusCode(StatusCodeState state,
                                       DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newStatusCode();
        state.update(record);
        record.insert();
        return new StatusCode(record);
    }

    @Override
    public Unit createUnit(ExistentialState state,
                           DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        ExistentialRecord record = model.records()
                                        .newUnit();
        state.update(record);
        record.insert();
        return new Unit(record);
    }

    @Override
    public Boolean removeAgency(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeAttribute(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeInterval(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeLocation(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeProduct(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeRelationship(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeStatusCode(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Boolean removeUnit(UUID id, DataFetchingEnvironment env) {
        resolve(env, id).delete();
        return true;
    }

    @Override
    public Agency updateAgency(ExistentialUpdateState state,
                               DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Agency(record);
    }

    @Override
    public Attribute updateAttribute(AttributeUpdateState state,
                                     DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Attribute(record);
    }

    @Override
    public Interval updateInterval(ExistentialUpdateState state,
                                   DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Interval(record);
    }

    @Override
    public Location updateLocation(ExistentialUpdateState state,
                                   DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Location(record);
    }

    @Override
    public Product updateProduct(ExistentialUpdateState state,
                                 DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Product(record);
    }

    @Override
    public Relationship updateRelationship(RelationshipUpdateState state,
                                           DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Relationship(record);
    }

    @Override
    public StatusCode updateStatusCode(StatusCodeUpdateState state,
                                       DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new StatusCode(record);
    }

    @Override
    public Unit updateUnit(ExistentialUpdateState state,
                           DataFetchingEnvironment env) {
        ExistentialRecord record = resolve(env, state.getId());
        state.update(record);
        record.update();
        return new Unit(record);
    }

    static List<ExistentialRecord> resolved(DataFetchingEnvironment env,
                                            ExistentialDomain domain) {
        Model model = WorkspaceSchema.ctx(env);
        return model.create()
                    .selectFrom(Tables.EXISTENTIAL)
                    .where(Tables.EXISTENTIAL.WORKSPACE.eq(WorkspaceContext.getWorkspace(env)
                                                                           .getId()))
                    .and(Tables.EXISTENTIAL.DOMAIN.equal(domain))
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .filter(r -> model.checkRead(r))
                    .collect(Collectors.toList());
    }

    @Override
    public List<Agency> agencies(@GraphQLName("ids") List<UUID> ids,
                                 DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Agency).stream()
                                                          .map(r -> new Agency(r))
                                                          .collect(Collectors.toList());
        }
        Model model = WorkspaceSchema.ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Agency((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Agency agency(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Agency(resolved) : null;
    }

    @Override
    public Attribute attribute(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Attribute(resolved) : null;
    }

    @Override
    public List<Attribute> attributes(@GraphQLName("ids") List<UUID> ids,
                                      DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Attribute).stream()
                                                             .map(r -> new Attribute(r))
                                                             .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Attribute((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Interval interval(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Interval(resolved) : null;
    }

    @Override
    public List<Interval> intervals(@GraphQLName("ids") List<UUID> ids,
                                    DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Interval).stream()
                                                            .map(r -> new Interval(r))
                                                            .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Interval((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Location location(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Location(resolved) : null;
    }

    @Override
    public List<Location> locations(@GraphQLName("ids") List<UUID> ids,
                                    DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Location).stream()
                                                            .map(r -> new Location(r))
                                                            .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Location((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Product product(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Product(resolved) : null;
    }

    @Override
    public List<Product> products(@GraphQLName("ids") List<UUID> ids,
                                  DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Product).stream()
                                                           .map(r -> new Product(r))
                                                           .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Product((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Relationship relationship(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Relationship(resolved) : null;
    }

    @Override
    public List<Relationship> relationships(@GraphQLName("ids") List<UUID> ids,
                                            DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Relationship).stream()
                                                                .map(r -> new Relationship(r))
                                                                .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Relationship((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public StatusCode statusCode(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new StatusCode(resolved) : null;
    }

    @Override
    public List<StatusCode> statusCodes(@GraphQLName("ids") List<UUID> ids,
                                        DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.StatusCode).stream()
                                                              .map(r -> new StatusCode(r))
                                                              .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new StatusCode((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }

    @Override
    public Unit unit(UUID id, DataFetchingEnvironment env) {
        ExistentialRecord resolved = resolve(env, id);
        return ctx(env).checkRead(resolved) ? new Unit(resolved) : null;
    }

    @Override
    public List<Unit> units(@GraphQLName("ids") List<UUID> ids,
                            DataFetchingEnvironment env) {
        if (ids == null) {
            return resolved(env, ExistentialDomain.Unit).stream()
                                                        .map(r -> new Unit(r))
                                                        .collect(Collectors.toList());
        }
        Model model = ctx(env);
        return ids.stream()
                  .map(s -> s)
                  .map(id -> resolve(env, id))
                  .filter(r -> model.checkRead((UpdatableRecord<?>) r))
                  .map(r -> new Unit((ExistentialRecord) r))
                  .collect(Collectors.toList());
    }
}
