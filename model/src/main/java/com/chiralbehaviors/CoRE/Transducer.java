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

package com.chiralbehaviors.CoRE;

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
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;

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
 * A traversal visitor for Ultrastructure records
 *
 * @author hhildebrand
 *
 */
public interface Transducer {
    default UUID childSequencing(UUID id, DSLContext create,
                                 Collection<UUID> traversed,
                                 Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ChildSequencingAuthorizationRecord) create.select()
                                                                                         .from(CHILD_SEQUENCING_AUTHORIZATION)
                                                                                         .where(CHILD_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                                                                         .fetchOne(),
                                              create, traversed, replacements);
    }

    default UUID existential(UUID id, DSLContext create,
                             Collection<UUID> traversed,
                             Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialRecord) create.select()
                                                                        .from(EXISTENTIAL)
                                                                        .where(EXISTENTIAL.ID.equal(id))
                                                                        .fetchOne(),
                                              create, traversed, replacements);
    }

    default UUID existentialAttribute(UUID id, DSLContext create,
                                      Collection<UUID> traversed,
                                      Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialAttributeRecord) create.select()
                                                                                 .from(EXISTENTIAL_ATTRIBUTE)
                                                                                 .where(EXISTENTIAL_ATTRIBUTE.ID.equal(id))
                                                                                 .fetchOne(),
                                              create, traversed, replacements);
    }

    default UUID existentialAttributeAuthorization(UUID id, DSLContext create,
                                                   Collection<UUID> traversed,
                                                   Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialAttributeAuthorizationRecord) create.select()
                                                                                              .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                                                              .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                                                                                              .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID existentialNetwork(UUID id, DSLContext create,
                                    Collection<UUID> traversed,
                                    Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialNetworkRecord) create.select()
                                                                               .from(EXISTENTIAL_NETWORK)
                                                                               .where(EXISTENTIAL_NETWORK.ID.equal(id))
                                                                               .fetchOne(),
                                              create, traversed, replacements);
    }

    default UUID existentialNetworkAttribute(UUID id, DSLContext create,
                                             Collection<UUID> traversed,
                                             Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialNetworkRecord) create.select()
                                                                               .from(EXISTENTIAL_NETWORK_ATTRIBUTE)
                                                                               .where(EXISTENTIAL_NETWORK_ATTRIBUTE.ID.equal(id))
                                                                               .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID existentialNetworkAttributeAuthorization(UUID id,
                                                          DSLContext create,
                                                          Collection<UUID> traversed,
                                                          Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialNetworkAttributeAuthorizationRecord) create.select()
                                                                                                     .from(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                                                                                                     .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                                                                                                     .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID existentialNetworkAuthorization(UUID id, DSLContext create,
                                                 Collection<UUID> traversed,
                                                 Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ExistentialNetworkAuthorizationRecord) create.select()
                                                                                            .from(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                                            .where(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(id))
                                                                                            .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID job(UUID id, DSLContext create, Collection<UUID> traversed,
                     Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((JobRecord) create.select()
                                                                .from(JOB)
                                                                .where(JOB.ID.equal(id))
                                                                .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID jobChronology(UUID id, DSLContext create,
                               Collection<UUID> traversed,
                               Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((JobChronologyRecord) create.select()
                                                                          .from(JOB_CHRONOLOGY)
                                                                          .where(JOB_CHRONOLOGY.ID.equal(id))
                                                                          .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID metaProtocol(UUID id, DSLContext create,
                              Collection<UUID> traversed,
                              Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((MetaProtocolRecord) create.select()
                                                                         .from(META_PROTOCOL)
                                                                         .where(META_PROTOCOL.ID.equal(id))
                                                                         .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID networkInference(UUID id, DSLContext create,
                                  Collection<UUID> traversed,
                                  Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((WorkspaceAuthorizationRecord) create.select()
                                                                                   .from(NETWORK_INFERENCE)
                                                                                   .where(NETWORK_INFERENCE.ID.equal(id))
                                                                                   .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID parentSequencingAuthorization(UUID id, DSLContext create,
                                               Collection<UUID> traversed,
                                               Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ParentSequencingAuthorizationRecord) create.select()
                                                                                          .from(PARENT_SEQUENCING_AUTHORIZATION)
                                                                                          .where(PARENT_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                                                                          .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID protocol(UUID id, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((ProtocolRecord) create.select()
                                                                     .from(PROTOCOL)
                                                                     .where(PROTOCOL.ID.equal(id))
                                                                     .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID selfSequencingAuthorization(UUID id, DSLContext create,
                                             Collection<UUID> traversed,
                                             Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((SelfSequencingAuthorizationRecord) create.select()
                                                                                        .from(SELF_SEQUENCING_AUTHORIZATION)
                                                                                        .where(SELF_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                                                                        .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID siblingSequencingAuthorization(UUID id, DSLContext create,
                                                Collection<UUID> traversed,
                                                Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((SiblingSequencingAuthorizationRecord) create.select()
                                                                                           .from(SIBLING_SEQUENCING_AUTHORIZATION)
                                                                                           .where(SIBLING_SEQUENCING_AUTHORIZATION.ID.equal(id))
                                                                                           .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID statusCodeSequencing(UUID id, DSLContext create,
                                      Collection<UUID> traversed,
                                      Map<UUID, UUID> replacements) {
        if (id == null) {
            return null;
        }
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((StatusCodeSequencingRecord) create.select()
                                                                                 .from(STATUS_CODE_SEQUENCING)
                                                                                 .where(STATUS_CODE_SEQUENCING.ID.equal(id))
                                                                                 .fetchOne(),
                                              create, traversed, replacements);
    };

    default UUID traverse(AgencyExistentialGroupingRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setGroupingAgency(existential(record.getGroupingAgency(), create,
                                             traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ChildSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setNextChild(existential(record.getNextChild(), create,
                                        traversed, replacements));
        record.setNextChildStatus(existential(record.getNextChildStatus(),
                                              create, traversed, replacements));
        record.setParent(existential(record.getParent(), create, traversed,
                                     replacements));
        record.setStatusCode(existential(record.getStatusCode(), create,
                                         traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialAttributeAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAuthorizedAttribute(existential(record.getAuthorizedAttribute(),
                                                  create, traversed,
                                                  replacements));
        record.setGroupingAgency(existential(record.getGroupingAgency(), create,
                                             traversed, replacements));
        record.setNetworkAuthorization(existentialNetworkAuthorization(record.getNetworkAuthorization(),
                                                                       create,
                                                                       traversed,
                                                                       replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialAttributeRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAttribute(existential(record.getAttribute(), create,
                                        traversed, replacements));
        record.setExistential(existential(record.getExistential(), create,
                                          traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialNetworkAttributeAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAuthorizedAttribute(existential(record.getAuthorizedAttribute(),
                                                  create, traversed,
                                                  replacements));
        record.setGroupingAgency(existential(record.getGroupingAgency(), create,
                                             traversed, replacements));
        record.setNetworkAuthorization(existentialNetworkAuthorization(record.getNetworkAuthorization(),
                                                                       create,
                                                                       traversed,
                                                                       replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialNetworkAttributeRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAttribute(existential(record.getAttribute(), create,
                                        traversed, replacements));
        record.setEdge(existentialNetwork(record.getEdge(), create, traversed,
                                          replacements));
        record.setGroupingAgency(existential(record.getGroupingAgency(), create,
                                             traversed, replacements));
        record.setUnit(existential(record.getUnit(), create, traversed,
                                   replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialNetworkAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAuthorizedParent(existential(record.getAuthorizedParent(),
                                               create, traversed,
                                               replacements));
        record.setAuthorizedRelationship(existential(record.getAuthorizedRelationship(),
                                                     create, traversed,
                                                     replacements));
        record.setChildRelationship(existential(record.getChildRelationship(),
                                                create, traversed,
                                                replacements));
        record.setClassification(existential(record.getClassification(), create,
                                             traversed, replacements));
        record.setClassifier(existential(record.getClassifier(), create,
                                         traversed, replacements));
        record.setGroupingAgency(existential(record.getGroupingAgency(), create,
                                             traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialNetworkRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setChild(existential(record.getChild(), create, traversed,
                                    replacements));
        record.setInference(networkInference(record.getInference(), create,
                                             traversed, replacements));
        record.setParent(existential(record.getParent(), create, traversed,
                                     replacements));
        record.setPremise1(existentialNetwork(record.getPremise1(), create,
                                              traversed, replacements));
        record.setPremise2(existentialNetwork(record.getPremise2(), create,
                                              traversed, replacements));
        record.setRelationship(existential(record.getRelationship(), create,
                                           traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ExistentialRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setInverse(existential(record.getInverse(), create, traversed,
                                      replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(JobChronologyRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAssignTo(existential(record.getAssignTo(), create, traversed,
                                       replacements));
        record.setDeliverFrom(existential(record.getDeliverFrom(), create,
                                          traversed, replacements));
        record.setDeliverTo(existential(record.getDeliverTo(), create,
                                        traversed, replacements));
        record.setJob(job(record.getJob(), create, traversed, replacements));
        record.setProduct(existential(record.getProduct(), create, traversed,
                                      replacements));
        record.setQuantityUnit(existential(record.getQuantityUnit(), create,
                                           traversed, replacements));
        record.setRequester(existential(record.getRequester(), create,
                                        traversed, replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setStatus(existential(record.getStatus(), create, traversed,
                                     replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(JobRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAssignTo(existential(record.getAssignTo(), create, traversed,
                                       replacements));
        record.setDeliverFrom(existential(record.getDeliverFrom(), create,
                                          traversed, replacements));
        record.setDeliverTo(existential(record.getDeliverTo(), create,
                                        traversed, replacements));
        record.setParent(job(record.getParent(), create, traversed,
                             replacements));
        record.setProduct(existential(record.getProduct(), create, traversed,
                                      replacements));
        record.setProtocol(protocol(record.getProtocol(), create, traversed,
                                    replacements));
        record.setQuantityUnit(existential(record.getQuantityUnit(), create,
                                           traversed, replacements));
        record.setRequester(existential(record.getRequester(), create,
                                        traversed, replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setStatus(existential(record.getStatus(), create, traversed,
                                     replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(MetaProtocolRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAssignTo(existential(record.getAssignTo(), create, traversed,
                                       replacements));
        record.setDeliverFrom(existential(record.getDeliverFrom(), create,
                                          traversed, replacements));
        record.setDeliverTo(existential(record.getDeliverTo(), create,
                                        traversed, replacements));
        record.setProduct(existential(record.getProduct(), create, traversed,
                                      replacements));
        record.setQuantityUnit(existential(record.getQuantityUnit(), create,
                                           traversed, replacements));
        record.setRequester(existential(record.getRequester(), create,
                                        traversed, replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setServiceType(existential(record.getServiceType(), create,
                                          traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(NetworkInferenceRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setInference(existential(record.getInference(), create,
                                        traversed, replacements));
        record.setPremise1(existential(record.getPremise1(), create, traversed,
                                       replacements));
        record.setPremise2(existential(record.getPremise2(), create, traversed,
                                       replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ParentSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setParent(existential(record.getParent(), create, traversed,
                                     replacements));
        record.setParentStatusToSet(existential(record.getParentStatusToSet(),
                                                create, traversed,
                                                replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setStatusCode(existential(record.getStatusCode(), create,
                                         traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(ProtocolRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setAssignTo(existential(record.getAssignTo(), create, traversed,
                                       replacements));
        record.setChildAssignTo(existential(record.getChildAssignTo(), create,
                                            traversed, replacements));
        record.setChildDeliverFrom(existential(record.getChildDeliverFrom(),
                                               create, traversed,
                                               replacements));
        record.setChildDeliverTo(existential(record.getChildDeliverTo(), create,
                                             traversed, replacements));
        record.setChildProduct(existential(record.getChildProduct(), create,
                                           traversed, replacements));
        record.setChildQuantityUnit(existential(record.getChildQuantityUnit(),
                                                create, traversed,
                                                replacements));
        record.setChildrenRelationship(existential(record.getChildrenRelationship(),
                                                   create, traversed,
                                                   replacements));
        record.setChildService(existential(record.getChildService(), create,
                                           traversed, replacements));
        record.setDeliverFrom(existential(record.getDeliverFrom(), create,
                                          traversed, replacements));
        record.setDeliverTo(existential(record.getDeliverTo(), create,
                                        traversed, replacements));
        record.setProduct(existential(record.getProduct(), create, traversed,
                                      replacements));
        record.setQuantityUnit(existential(record.getQuantityUnit(), create,
                                           traversed, replacements));
        record.setRequester(existential(record.getRequester(), create,
                                        traversed, replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(SelfSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setStatusCode(existential(record.getStatusCode(), create,
                                         traversed, replacements));
        record.setStatusToSet(existential(record.getStatusToSet(), create,
                                          traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(SiblingSequencingAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setNextSibling(existential(record.getNextSibling(), create,
                                          traversed, replacements));
        record.setNextSiblingStatus(existential(record.getNextSiblingStatus(),
                                                create, traversed,
                                                replacements));
        record.setParent(existential(record.getParent(), create, traversed,
                                     replacements));
        record.setStatusCode(existential(record.getStatusCode(), create,
                                         traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(StatusCodeSequencingRecord record, DSLContext create,
                          Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setChild(existential(record.getChild(), create, traversed,
                                    replacements));
        record.setParent(existential(record.getParent(), create, traversed,
                                     replacements));
        record.setService(existential(record.getService(), create, traversed,
                                      replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        record.setWorkspace(workspace(record.getWorkspace(), create, traversed,
                                      replacements));
        return record.getId();
    };

    default UUID traverse(UUID id, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        UUID replaceWith = replacements.get(id);
        if (replaceWith != null) {
            return replaceWith;
        }
        if (!traversed.contains(id)) {
            traversed.add(id);
        }
        return null;
    };

    default UUID traverse(WorkspaceAuthorizationRecord record,
                          DSLContext create, Collection<UUID> traversed,
                          Map<UUID, UUID> replacements) {
        record.setDefiningProduct(existential(record.getDefiningProduct(),
                                              create, traversed, replacements));
        record.setUpdatedBy(existential(record.getUpdatedBy(), create,
                                        traversed, replacements));
        return record.getId();
    };

    default UUID workspace(UUID id, DSLContext create,
                           Collection<UUID> traversed,
                           Map<UUID, UUID> replacements) {
        UUID replaceWith = traverse(id, traversed, replacements);
        return replaceWith != null ? replaceWith
                                   : traverse((WorkspaceAuthorizationRecord) create.select()
                                                                                   .from(WORKSPACE_AUTHORIZATION)
                                                                                   .where(WORKSPACE_AUTHORIZATION.ID.equal(id))
                                                                                   .fetchOne(),
                                              create, traversed, replacements);
    };
}
