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

package com.chiralbehaviors.CoRE;

import static com.chiralbehaviors.CoRE.jooq.Tables.AGENCY_EXISTENTIAL_GROUPING;
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
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/**
 * @author hhildebrand
 *
 */
public interface RecordFactory {
    public static enum ReferenceType {
        AGENCY_EXISTENTIAL_GROUPING, CHILD_SEQUENCING_AUTHORIZATION,
        EXISTENTIAL, EXISTENTIAL_ATTRIBUTE, EXISTENTIAL_ATTRIBUTE_AUTHORIZATION,
        EXISTENTIAL_NETWORK, EXISTENTIAL_NETWORK_ATTRIBUTE,
        EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION,
        EXISTENTIAL_NETWORK_AUTHORIZATION, JOB, JOB_CHRONOLOGY, META_PROTOCOL,
        NETWORK_INFERENCE, PARENT_SEQUENCING_AUTHORIZATION, PROTOCOL,
        SELF_SEQUENCING_AUTHORIZATION, SIBLING_SEQUENCING_AUTHORIZATION,
        STATUS_CODE_SEQUENCING, WORKSPACE_AUTHORIZATION;
    }

    static final NoArgGenerator GENERATOR = Generators.timeBasedGenerator();
    static final RecordFactory  RECORDS   = new RecordFactory() {
                                          };

    default ExistentialRecord newAgency(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("A");
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newAgency(DSLContext create, String name) {
        ExistentialRecord record = newAgency(create);
        record.setName(name);
        return record;
    }

    default ExistentialRecord newAgency(DSLContext create, String name,
                                        ExistentialRecord updatedBy) {
        ExistentialRecord record = newAgency(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newAgency(DSLContext create, String name,
                                        String description,
                                        ExistentialRecord updatedBy) {
        ExistentialRecord record = newAgency(create);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newAttribute(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("T");
        record.setId(GENERATOR.generate());
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(DSLContext create) {
        ChildSequencingAuthorizationRecord record = create.newRecord(CHILD_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialAttributeRecord newExistentialAttribute(DSLContext create) {
        ExistentialAttributeRecord record = create.newRecord(EXISTENTIAL_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialAttributeAuthorizationRecord newExistentialAttributeAuthorization(DSLContext create) {
        ExistentialAttributeAuthorizationRecord record = create.newRecord(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default AgencyExistentialGroupingRecord newExistentialGrouping(DSLContext create) {
        AgencyExistentialGroupingRecord record = create.newRecord(AGENCY_EXISTENTIAL_GROUPING);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkRecord newExistentialNetwork(DSLContext create) {
        ExistentialNetworkRecord record = create.newRecord(EXISTENTIAL_NETWORK);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAttributeRecord newExistentialNetworkAttribute(DSLContext create) {
        ExistentialNetworkAttributeRecord record = create.newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAttributeAuthorizationRecord newExistentialNetworkAttributeAuthorization(DSLContext create) {
        ExistentialNetworkAttributeAuthorizationRecord record = create.newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAuthorizationRecord newExistentialNetworkAuthorization(DSLContext create) {
        ExistentialNetworkAuthorizationRecord record = create.newRecord(EXISTENTIAL_NETWORK_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newInterval(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("I");
        record.setId(GENERATOR.generate());
        return record;
    }

    default JobRecord newJob(DSLContext create) {
        JobRecord record = create.newRecord(JOB);
        record.setId(GENERATOR.generate());
        return record;
    }

    default JobChronologyRecord newJobChronology(DSLContext create) {
        JobChronologyRecord record = create.newRecord(JOB_CHRONOLOGY);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newLocation(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("L");
        record.setId(GENERATOR.generate());
        return record;
    }

    default MetaProtocolRecord newMetaProtocol(DSLContext create) {
        MetaProtocolRecord record = create.newRecord(META_PROTOCOL);
        record.setId(GENERATOR.generate());
        return record;
    }

    default NetworkInferenceRecord newNetworkInferrence(DSLContext create) {
        NetworkInferenceRecord record = create.newRecord(NETWORK_INFERENCE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ParentSequencingAuthorizationRecord newParentSequencingAuthorization(DSLContext create) {
        ParentSequencingAuthorizationRecord record = create.newRecord(PARENT_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newProduct(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("P");
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newProduct(DSLContext create, String name,
                                         ExistentialRecord updatedBy) {
        ExistentialRecord record = newProduct(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newProduct(DSLContext create, String name,
                                         String description,
                                         ExistentialRecord updatedBy) {
        ExistentialRecord record = newProduct(create);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ProtocolRecord newProtocol(DSLContext create) {
        ProtocolRecord record = create.newRecord(PROTOCOL);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newRelationship(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("P");
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newRelationship(DSLContext create, String name,
                                              ExistentialRecord updatedBy) {
        ExistentialRecord record = newRelationship(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newRelationship(DSLContext create, String name,
                                              ExistentialRecord updatedBy,
                                              ExistentialRecord inverse) {
        return newRelationship(create, name, null, updatedBy, inverse);
    }

    default ExistentialRecord newRelationship(DSLContext create, String name,
                                              String description,
                                              ExistentialRecord updatedBy) {
        ExistentialRecord record = newRelationship(create);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newRelationship(DSLContext create, String name,
                                              String description,
                                              ExistentialRecord updatedBy,
                                              ExistentialRecord inverse) {
        ExistentialRecord record = newRelationship(create, name, description,
                                                   updatedBy);
        record.setInverse(inverse.getId());
        inverse.setInverse(record.getId());
        return record;
    }

    default ExistentialRecord newRelationshipy(DSLContext create, String name) {
        ExistentialRecord record = newRelationship(create);
        record.setName(name);
        return record;
    }

    default SelfSequencingAuthorizationRecord newSelfSequencingAuthorization(DSLContext create) {
        SelfSequencingAuthorizationRecord record = create.newRecord(SELF_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default SiblingSequencingAuthorizationRecord newSiblingSequencingAuthorization(DSLContext create) {
        SiblingSequencingAuthorizationRecord record = create.newRecord(SIBLING_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newStatusCode(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("S");
        record.setId(GENERATOR.generate());
        return record;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing(DSLContext create) {
        StatusCodeSequencingRecord record = create.newRecord(STATUS_CODE_SEQUENCING);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRecord newUnit(DSLContext create) {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setDomain("U");
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(DSLContext create) {
        WorkspaceAuthorizationRecord record = create.newRecord(WORKSPACE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(DSLContext create,
                                                                   ExistentialRecord definingProduct,
                                                                   ExistentialRecord reference,
                                                                   ExistentialRecord updatedBy) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization(create);
        record.setDefiningProduct(definingProduct.getId());
        record.setReference(reference.getId());
        record.setUpdatedBy(updatedBy.getId());
        record.setType(ReferenceType.EXISTENTIAL.ordinal());
        return record;
    }
}
