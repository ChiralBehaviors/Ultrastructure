/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableRecord;

import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * A state trasnsducer that gathers state in a workspace
 * 
 * @author hhildebrand
 *
 */
public class WorkspaceClosure implements SystemVisitor {
    private final UUID       definingProduct;
    private final List<UUID> frontier;

    public WorkspaceClosure(UUID definingProduct, List<UUID> frontier) {
        if (definingProduct == null) {
            throw new IllegalArgumentException("Defining product cannot be null");
        }
        this.definingProduct = definingProduct;
        this.frontier = frontier;
    }

    public List<TableRecord<?>> compute(DSLContext create,
                                        Result<WorkspaceAuthorizationRecord> authorizations) {
        List<TableRecord<?>> closure = new ArrayList<>();
        OaHashSet<UUID> traversed = new OaHashSet<>(1024);
        authorizations.forEach(r -> traverse(r, create, traversed, closure));
        return closure;
    }

    @Override
    public void traverse(AgencyExistentialGroupingRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ChildSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialAttributeAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialAttributeRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialNetworkAttributeAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialNetworkAttributeRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialNetworkAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialNetworkRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ExistentialRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(JobChronologyRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(JobRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(MetaProtocolRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(NetworkInferenceRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ParentSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(ProtocolRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(SelfSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(SiblingSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }

    @Override
    public void traverse(StatusCodeSequencingRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         List<TableRecord<?>> closure) {
        if (!definingProduct.equals(definingProduct(create,
                                                    record.getWorkspace()))) {
            frontier.add(record.getId());
            return;
        }
        SystemVisitor.super.traverse(record, create, traversed, closure);
    }
}
