/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

import static com.chiralbehaviors.CoRE.jooq.Tables.CHILD_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB_CHRONOLOGY;
import static com.chiralbehaviors.CoRE.jooq.Tables.META_PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.NETWORK_INFERENCE;
import static com.chiralbehaviors.CoRE.jooq.Tables.PARENT_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.SELF_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.SIBLING_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
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

/**
 * A traversal visitor for Ultrastructure records, computing the closure of the
 * visited graph
 *
 * @author hhildebrand
 *
 */
public interface SystemVisitor {
    default void childSequencing(UUID id, DSLContext create,
                                 Collection<UUID> traversed,
                                 List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(CHILD_SEQUENCING_AUTHORIZATION)
                       .where(CHILD_SEQUENCING_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    }

    default UUID definingProduct(DSLContext create, UUID workspace) {
        if (workspace == null) {
            return null;
        }
        return create.select(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT)
                     .from(WORKSPACE_AUTHORIZATION)
                     .where(WORKSPACE_AUTHORIZATION.ID.eq(workspace))
                     .fetchOne()
                     .value1();
    }

    default void existential(UUID id, DSLContext create,
                             Collection<UUID> traversed,
                             List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL)
                       .where(EXISTENTIAL.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    }

    default void existentialAttribute(UUID id, DSLContext create,
                                      Collection<UUID> traversed,
                                      List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_ATTRIBUTE)
                       .where(EXISTENTIAL_ATTRIBUTE.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    }

    default void existentialAttributeAuthorization(UUID id, DSLContext create,
                                                   Collection<UUID> traversed,
                                                   List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                       .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void existentialNetwork(UUID id, DSLContext create,
                                    Collection<UUID> traversed,
                                    List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_NETWORK)
                       .where(EXISTENTIAL_NETWORK.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    }

    default void existentialNetworkAttribute(UUID id, DSLContext create,
                                             Collection<UUID> traversed,
                                             List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE)
                       .where(EXISTENTIAL_NETWORK_ATTRIBUTE.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void existentialNetworkAttributeAuthorization(UUID id,
                                                          DSLContext create,
                                                          Collection<UUID> traversed,
                                                          List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                       .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void existentialNetworkAuthorization(UUID id, DSLContext create,
                                                 Collection<UUID> traversed,
                                                 List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                       .where(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void job(UUID id, DSLContext create, Collection<UUID> traversed,
                     List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(JOB)
                       .where(JOB.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void jobChronology(UUID id, DSLContext create,
                               Collection<UUID> traversed,
                               List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(JOB_CHRONOLOGY)
                       .where(JOB_CHRONOLOGY.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void metaProtocol(UUID id, DSLContext create,
                              Collection<UUID> traversed,
                              List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(META_PROTOCOL)
                       .where(META_PROTOCOL.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void networkInference(UUID id, DSLContext create,
                                  Collection<UUID> traversed,
                                  List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(NETWORK_INFERENCE)
                       .where(NETWORK_INFERENCE.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void parentSequencingAuthorization(UUID id, DSLContext create,
                                               Collection<UUID> traversed,
                                               List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(PARENT_SEQUENCING_AUTHORIZATION)
                       .where(PARENT_SEQUENCING_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void protocol(UUID id, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(PROTOCOL)
                       .where(PROTOCOL.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void selfSequencingAuthorization(UUID id, DSLContext create,
                                             Collection<UUID> traversed,
                                             List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(SELF_SEQUENCING_AUTHORIZATION)
                       .where(SELF_SEQUENCING_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void siblingSequencingAuthorization(UUID id, DSLContext create,
                                                Collection<UUID> traversed,
                                                List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(SIBLING_SEQUENCING_AUTHORIZATION)
                       .where(SIBLING_SEQUENCING_AUTHORIZATION.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void statusCodeSequencing(UUID id, DSLContext create,
                                      Collection<UUID> traversed,
                                      List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse(create.selectFrom(STATUS_CODE_SEQUENCING)
                       .where(STATUS_CODE_SEQUENCING.ID.eq(id))
                       .fetchOne(),
                 create, traversed, closure);
    };

    default void traverse(AgencyExistentialGroupingRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAuthority(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ChildSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getNextChild(), create, traversed, closure);
        existential(record.getNextChildStatus(), create, traversed, closure);
        existential(record.getParent(), create, traversed, closure);
        existential(record.getStatusCode(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialAttributeAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAuthorizedAttribute(), create, traversed,
                    closure);
        existential(record.getAuthority(), create, traversed, closure);
        existentialNetworkAuthorization(record.getNetworkAuthorization(),
                                        create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialAttributeRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAttribute(), create, traversed, closure);
        existential(record.getExistential(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialNetworkAttributeAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAuthorizedAttribute(), create, traversed,
                    closure);
        existential(record.getAuthority(), create, traversed, closure);
        existentialNetworkAuthorization(record.getNetworkAuthorization(),
                                        create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialNetworkAttributeRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAttribute(), create, traversed, closure);
        existentialNetwork(record.getEdge(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        existential(record.getUnit(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialNetworkAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAuthorizedParent(), create, traversed, closure);
        existential(record.getAuthorizedRelationship(), create, traversed,
                    closure);
        existential(record.getChildRelationship(), create, traversed, closure);
        existential(record.getClassification(), create, traversed, closure);
        existential(record.getClassifier(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialNetworkRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getChild(), create, traversed, closure);
        networkInference(record.getInference(), create, traversed, closure);
        existential(record.getParent(), create, traversed, closure);
        existentialNetwork(record.getPremise1(), create, traversed, closure);
        existentialNetwork(record.getPremise2(), create, traversed, closure);
        existential(record.getRelationship(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ExistentialRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getInverse(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        existential(record.getAuthority(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(JobChronologyRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAssignTo(), create, traversed, closure);
        existential(record.getDeliverFrom(), create, traversed, closure);
        existential(record.getDeliverTo(), create, traversed, closure);
        job(record.getJob(), create, traversed, closure);
        existential(record.getProduct(), create, traversed, closure);
        existential(record.getQuantityUnit(), create, traversed, closure);
        existential(record.getRequester(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getStatus(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(JobRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAssignTo(), create, traversed, closure);
        existential(record.getDeliverFrom(), create, traversed, closure);
        existential(record.getDeliverTo(), create, traversed, closure);
        job(record.getParent(), create, traversed, closure);
        existential(record.getProduct(), create, traversed, closure);
        protocol(record.getProtocol(), create, traversed, closure);
        existential(record.getQuantityUnit(), create, traversed, closure);
        existential(record.getRequester(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getStatus(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(MetaProtocolRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAssignTo(), create, traversed, closure);
        existential(record.getDeliverFrom(), create, traversed, closure);
        existential(record.getDeliverTo(), create, traversed, closure);
        existential(record.getProduct(), create, traversed, closure);
        existential(record.getQuantityUnit(), create, traversed, closure);
        existential(record.getRequester(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getServiceType(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(NetworkInferenceRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getInference(), create, traversed, closure);
        existential(record.getPremise1(), create, traversed, closure);
        existential(record.getPremise2(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ParentSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getParent(), create, traversed, closure);
        existential(record.getParentStatusToSet(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getStatusCode(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(ProtocolRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getAssignTo(), create, traversed, closure);
        existential(record.getChildAssignTo(), create, traversed, closure);
        existential(record.getChildDeliverFrom(), create, traversed, closure);
        existential(record.getChildDeliverTo(), create, traversed, closure);
        existential(record.getChildProduct(), create, traversed, closure);
        existential(record.getChildQuantityUnit(), create, traversed, closure);
        existential(record.getChildrenRelationship(), create, traversed,
                    closure);
        existential(record.getChildService(), create, traversed, closure);
        existential(record.getDeliverFrom(), create, traversed, closure);
        existential(record.getDeliverTo(), create, traversed, closure);
        existential(record.getProduct(), create, traversed, closure);
        existential(record.getQuantityUnit(), create, traversed, closure);
        existential(record.getRequester(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(SelfSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getService(), create, traversed, closure);
        existential(record.getStatusCode(), create, traversed, closure);
        existential(record.getStatusToSet(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(SiblingSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getNextSibling(), create, traversed, closure);
        existential(record.getNextSiblingStatus(), create, traversed, closure);
        existential(record.getParent(), create, traversed, closure);
        existential(record.getStatusCode(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default void traverse(StatusCodeSequencingRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getChild(), create, traversed, closure);
        existential(record.getParent(), create, traversed, closure);
        existential(record.getService(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);
        workspace(record.getWorkspace(), create, traversed, closure);

    };

    default boolean traversed(UUID id, Collection<UUID> traversed) {
        if (id == null) {
            return true;
        }
        if (!traversed.contains(id)) {
            traversed.add(id);
            return false;
        }
        return true;
    };

    default void traverse(WorkspaceAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          List<TableRecord<?>> closure) {
        closure.add(record);
        existential(record.getDefiningProduct(), create, traversed, closure);
        existential(record.getUpdatedBy(), create, traversed, closure);

    };

    default void workspace(UUID id, DSLContext create,
                           Collection<UUID> traversed,
                           List<TableRecord<?>> closure) {
        if (traversed(id, traversed)) {
            return;
        }
        traverse((WorkspaceAuthorizationRecord) create.select()
                                                      .from(WORKSPACE_AUTHORIZATION)
                                                      .where(WORKSPACE_AUTHORIZATION.ID.eq(id))
                                                      .fetchOne(),
                 create, traversed, closure);
    };
}
