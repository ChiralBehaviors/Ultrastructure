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

import java.util.UUID;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
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
public interface RecordsFactory {
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
    static final RecordsFactory RECORDS   = new RecordsFactory() {
                                          };

    static ExistentialRuleform createExistential(DSLContext create,
                                                 UUID classification,
                                                 String name,
                                                 String description,
                                                 Agency updatedBy) {
        ExistentialRecord clazz = create.selectFrom(EXISTENTIAL)
                                        .where(EXISTENTIAL.ID.equal(classification))
                                        .fetchOne();
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(GENERATOR.generate());
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        record.setDomain(clazz.getDomain());
        return resolve(record);
    }

    static ExistentialRuleform resolve(ExistentialRecord record) {
        switch (record.getDomain()) {
            case A:
                return record.into(Agency.class);
            case I:
                return record.into(Interval.class);
            case L:
                return record.into(Location.class);
            case P:
                return record.into(Product.class);
            case R:
                return record.into(Relationship.class);
            case S:
                return record.into(StatusCode.class);
            case T:
                return record.into(Attribute.class);
            case U:
                return record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends ExistentialRuleform> T resolve(DSLContext create,
                                                     UUID id) {
        ExistentialRecord record = create.selectFrom(EXISTENTIAL)
                                         .where(EXISTENTIAL.ID.equal(id))
                                         .fetchOne();
        switch (record.getDomain()) {
            case A:
                return (T) record.into(Agency.class);
            case I:
                return (T) record.into(Interval.class);
            case L:
                return (T) record.into(Location.class);
            case P:
                return (T) record.into(Product.class);
            case R:
                return (T) record.into(Relationship.class);
            case S:
                return (T) record.into(StatusCode.class);
            case T:
                return (T) record.into(Attribute.class);
            case U:
                return (T) record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    default ExistentialRecord copy(DSLContext create, ExistentialRecord rf) {
        ExistentialRecord copy = ((ExistentialRecord) rf).copy();
        copy.setId(GENERATOR.generate());
        return copy;

    }

    default Agency newAgency(DSLContext create) {
        Agency record = create.newRecord(EXISTENTIAL)
                              .into(Agency.class);
        record.setDomain(ExistentialDomain.A);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Agency newAgency(DSLContext create, String name) {
        Agency record = newAgency(create);
        record.setName(name);
        return record;
    }

    default Agency newAgency(DSLContext create, String name, Agency updatedBy) {
        Agency record = newAgency(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Agency newAgency(DSLContext create, String name, String description,
                             Agency updatedBy) {
        Agency record = newAgency(create);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Attribute newAttribute(DSLContext create) {
        Attribute record = create.newRecord(EXISTENTIAL)
                                 .into(Attribute.class);
        record.setDomain(ExistentialDomain.T);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(DSLContext create) {
        ChildSequencingAuthorizationRecord record = create.newRecord(CHILD_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRuleform newExistential(DSLContext create,
                                               ExistentialDomain domain) {
        Class<? extends ExistentialRecord> existential;
        switch (domain) {
            case A:
                existential = Agency.class;
                break;
            case I:
                existential = Interval.class;
                break;
            case L:
                existential = Location.class;
                break;
            case P:
                existential = Product.class;
                break;
            case R:
                existential = Relationship.class;
                break;
            case S:
                existential = StatusCode.class;
                break;
            case T:
                existential = Attribute.class;
                break;
            case U:
                existential = Unit.class;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 domain));
        }
        ExistentialRecord record = create.newRecord(EXISTENTIAL)
                                         .into(existential);
        record.setDomain(domain);
        record.setId(GENERATOR.generate());
        return (ExistentialRuleform) record;
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
        record.setDomain(ExistentialDomain.I);
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
        record.setDomain(ExistentialDomain.L);
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

    default Product newProduct(DSLContext create) {
        Product record = create.newRecord(EXISTENTIAL)
                               .into(Product.class);
        record.setDomain(ExistentialDomain.P);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Product newProduct(DSLContext create, String name,
                               Agency updatedBy) {
        Product record = newProduct(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Product newProduct(DSLContext create, String name,
                               String description, Agency updatedBy) {
        Product record = newProduct(create).into(Product.class);
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

    default Relationship newRelationship(DSLContext create) {
        Relationship record = create.newRecord(EXISTENTIAL)
                                    .into(Relationship.class);
        record.setDomain(ExistentialDomain.R);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Relationship newRelationship(DSLContext create, String name,
                                         Agency updatedBy) {
        Relationship record = newRelationship(create);
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Relationship newRelationship(DSLContext create, String name,
                                         Agency updatedBy,
                                         Relationship inverse) {
        return newRelationship(create, name, null, updatedBy, inverse);
    }

    default Relationship newRelationship(DSLContext create, String name,
                                         String description, Agency updatedBy) {
        Relationship record = newRelationship(create);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Relationship newRelationship(DSLContext create, String name,
                                         String description, Agency updatedBy,
                                         Relationship inverse) {
        Relationship record = newRelationship(create, name, description,
                                              updatedBy);
        record.setInverse(inverse.getId());
        inverse.setInverse(record.getId());
        return record;
    }

    default Relationship newRelationshipy(DSLContext create, String name) {
        Relationship record = newRelationship(create);
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

    default StatusCode newStatusCode(DSLContext create) {
        StatusCode record = create.newRecord(EXISTENTIAL)
                                  .into(StatusCode.class);
        record.setDomain(ExistentialDomain.S);
        record.setId(GENERATOR.generate());
        return record;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing(DSLContext create) {
        StatusCodeSequencingRecord record = create.newRecord(STATUS_CODE_SEQUENCING);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Unit newUnit(DSLContext create) {
        Unit record = create.newRecord(EXISTENTIAL)
                            .into(Unit.class);
        record.setDomain(ExistentialDomain.U);
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(DSLContext create) {
        WorkspaceAuthorizationRecord record = create.newRecord(WORKSPACE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(DSLContext create,
                                                                   Product definingProduct,
                                                                   ExistentialRuleform reference,
                                                                   Agency updatedBy) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization(create);
        record.setDefiningProduct(definingProduct.getId());
        record.setReference(reference.getId());
        record.setUpdatedBy(updatedBy.getId());
        record.setType(ReferenceType.EXISTENTIAL.ordinal());
        return record;
    }
}
