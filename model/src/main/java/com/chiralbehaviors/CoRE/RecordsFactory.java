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
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
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
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
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

    static final NoArgGenerator GENERATOR = Generators.timeBasedGenerator();

    default ExistentialRecord copy(ExistentialRecord rf) {
        ExistentialRecord copy = ((ExistentialRecord) rf).copy();
        copy.setId(GENERATOR.generate());
        return copy;

    }

    DSLContext create();

    default ExistentialRuleform createExistential(UUID classification,
                                                  String name,
                                                  String description,
                                                  Agency updatedBy) {
        ExistentialRecord clazz = create().selectFrom(EXISTENTIAL)
                                          .where(EXISTENTIAL.ID.equal(classification))
                                          .fetchOne();
        ExistentialRecord record = create().newRecord(EXISTENTIAL);
        record.setId(GENERATOR.generate());
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        record.setDomain(clazz.getDomain());
        return resolve(record);
    }

    default FacetRecord findFacetRecord(UUID id) {
        return create().selectFrom(FACET)
                       .where(FACET.ID.equal(id))
                       .fetchOne();
    }

    default Agency newAgency() {
        Agency record = create().newRecord(EXISTENTIAL)
                                .into(Agency.class);
        record.setDomain(ExistentialDomain.Agency);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Agency newAgency(String name) {
        Agency record = newAgency();
        record.setName(name);
        return record;
    }

    default Agency newAgency(String name, Agency updatedBy) {
        Agency record = newAgency();
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Agency newAgency(String name, String description,
                             Agency updatedBy) {
        Agency record = newAgency();
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Attribute newAttribute() {
        Attribute record = create().newRecord(EXISTENTIAL)
                                   .into(Attribute.class);
        record.setDomain(ExistentialDomain.Attribute);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization() {
        ChildSequencingAuthorizationRecord record = create().newRecord(CHILD_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialRuleform newExistential(ExistentialDomain domain) {
        Class<? extends ExistentialRecord> existential;
        switch (domain) {
            case Agency:
                existential = Agency.class;
                break;
            case Interval:
                existential = Interval.class;
                break;
            case Location:
                existential = Location.class;
                break;
            case Product:
                existential = Product.class;
                break;
            case Relationship:
                existential = Relationship.class;
                break;
            case StatusCode:
                existential = StatusCode.class;
                break;
            case Attribute:
                existential = Attribute.class;
                break;
            case Unit:
                existential = Unit.class;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 domain));
        }
        ExistentialRecord record = create().newRecord(EXISTENTIAL)
                                           .into(existential);
        record.setDomain(domain);
        record.setId(GENERATOR.generate());
        return (ExistentialRuleform) record;
    }

    default ExistentialAttributeRecord newExistentialAttribute() {
        ExistentialAttributeRecord record = create().newRecord(EXISTENTIAL_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialAttributeAuthorizationRecord newExistentialAttributeAuthorization() {
        ExistentialAttributeAuthorizationRecord record = create().newRecord(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default AgencyExistentialGroupingRecord newExistentialGrouping() {
        AgencyExistentialGroupingRecord record = create().newRecord(AGENCY_EXISTENTIAL_GROUPING);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkRecord newExistentialNetwork() {
        ExistentialNetworkRecord record = create().newRecord(EXISTENTIAL_NETWORK);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAttributeRecord newExistentialNetworkAttribute() {
        ExistentialNetworkAttributeRecord record = create().newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAttributeRecord newExistentialNetworkAttribute(Attribute attribute,
                                                                             Agency updatedBy) {
        ExistentialNetworkAttributeRecord record = new ExistentialNetworkAttributeRecord();
        record.setAttribute(attribute.getId());
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialNetworkAttributeAuthorizationRecord newExistentialNetworkAttributeAuthorization() {
        ExistentialNetworkAttributeAuthorizationRecord record = create().newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAuthorizationRecord newExistentialNetworkAuthorization() {
        ExistentialNetworkAuthorizationRecord record = create().newRecord(EXISTENTIAL_NETWORK_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ExistentialNetworkAuthorizationRecord newExistentialNetworkAuthorization(Agency updatedBy) {
        ExistentialNetworkAuthorizationRecord record = newExistentialNetworkAuthorization();
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default FacetRecord newFacet(Agency updatedBy) {
        FacetRecord record = new FacetRecord();
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ExistentialRecord newInterval() {
        ExistentialRecord record = create().newRecord(EXISTENTIAL);
        record.setDomain(ExistentialDomain.Interval);
        record.setId(GENERATOR.generate());
        return record;
    }

    default JobRecord newJob() {
        JobRecord record = create().newRecord(JOB);
        record.setId(GENERATOR.generate());
        return record;
    }

    default JobRecord newJob(Agency updatedBy) {
        JobRecord record = newJob();
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default JobChronologyRecord newJobChronology() {
        JobChronologyRecord record = create().newRecord(JOB_CHRONOLOGY);
        record.setId(GENERATOR.generate());
        return record;
    }

    default JobChronologyRecord newJobChronologyRecord(JobRecord job,
                                                       String notes) {
        JobChronologyRecord record = newJobChronology();
        record.setNotes(notes);
        record.setAssignTo(job.getAssignTo());
        record.setDeliverFrom(job.getDeliverFrom());
        record.setDeliverTo(record.getDeliverTo());
        record.setJob(job.getId());
        record.setProduct(job.getProduct());
        record.setQuantity(job.getQuantity());
        record.setQuantityUnit(job.getQuantityUnit());
        record.setRequester(job.getRequester());
        record.setStatus(job.getService());
        record.setUpdatedBy(job.getUpdatedBy());
        record.setSequenceNumber(job.getVersion());
        return record;

    }

    default ExistentialRecord newLocation() {
        ExistentialRecord record = create().newRecord(EXISTENTIAL);
        record.setDomain(ExistentialDomain.Location);
        record.setId(GENERATOR.generate());
        return record;
    }

    default MetaProtocolRecord newMetaProtocol() {
        MetaProtocolRecord record = create().newRecord(META_PROTOCOL);
        record.setId(GENERATOR.generate());
        return record;
    }

    default NetworkInferenceRecord newNetworkInferrence() {
        NetworkInferenceRecord record = create().newRecord(NETWORK_INFERENCE);
        record.setId(GENERATOR.generate());
        return record;
    }

    default ParentSequencingAuthorizationRecord newParentSequencingAuthorization() {
        ParentSequencingAuthorizationRecord record = create().newRecord(PARENT_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Product newProduct() {
        Product record = create().newRecord(EXISTENTIAL)
                                 .into(Product.class);
        record.setDomain(ExistentialDomain.Product);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Product newProduct(String name, Agency updatedBy) {
        Product record = newProduct();
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Product newProduct(String name, String description,
                               Agency updatedBy) {
        Product record = newProduct().into(Product.class);
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default ProtocolRecord newProtocol() {
        ProtocolRecord record = create().newRecord(PROTOCOL);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Relationship newRelationship() {
        Relationship record = create().newRecord(EXISTENTIAL)
                                      .into(Relationship.class);
        record.setDomain(ExistentialDomain.Relationship);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Relationship newRelationship(String name, Agency updatedBy) {
        Relationship record = newRelationship();
        record.setName(name);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Relationship newRelationship(String name, Agency updatedBy,
                                         Relationship inverse) {
        return newRelationship(name, null, updatedBy, inverse);
    }

    default Relationship newRelationship(String name, String description,
                                         Agency updatedBy) {
        Relationship record = newRelationship();
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(updatedBy.getId());
        return record;
    }

    default Relationship newRelationship(String name, String description,
                                         Agency updatedBy,
                                         Relationship inverse) {
        Relationship record = newRelationship(name, description, updatedBy);
        record.setInverse(inverse.getId());
        inverse.setInverse(record.getId());
        return record;
    }

    default Relationship newRelationshipy(String name) {
        Relationship record = newRelationship();
        record.setName(name);
        return record;
    }

    default SelfSequencingAuthorizationRecord newSelfSequencingAuthorization() {
        SelfSequencingAuthorizationRecord record = create().newRecord(SELF_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default SiblingSequencingAuthorizationRecord newSiblingSequencingAuthorization() {
        SiblingSequencingAuthorizationRecord record = create().newRecord(SIBLING_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default StatusCode newStatusCode() {
        StatusCode record = create().newRecord(EXISTENTIAL)
                                    .into(StatusCode.class);
        record.setDomain(ExistentialDomain.StatusCode);
        record.setId(GENERATOR.generate());
        return record;
    }

    default StatusCode newStatusCode(String string, Agency core) {
        StatusCode code = newStatusCode();
        code.setName(string);
        code.setUpdatedBy(core.getId());
        return code;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing() {
        StatusCodeSequencingRecord record = create().newRecord(STATUS_CODE_SEQUENCING);
        record.setId(GENERATOR.generate());
        return record;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing(Product service,
                                                               StatusCode parent,
                                                               StatusCode child,
                                                               Agency core) {
        StatusCodeSequencingRecord record = newStatusCodeSequencing();
        record.setService(service.getId());
        record.setParent(parent.getId());
        record.setChild(child.getId());
        return record;
    }

    default Unit newUnit() {
        Unit record = create().newRecord(EXISTENTIAL)
                              .into(Unit.class);
        record.setDomain(ExistentialDomain.Unit);
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization() {
        WorkspaceAuthorizationRecord record = create().newRecord(WORKSPACE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   AgencyExistentialGroupingRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Agency_Existential_Grouping,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ChildSequencingAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Child_Sequencing_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialAttributeAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Attribute_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialAttributeRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Attribute, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialNetworkAttributeAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Network_Attribute_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialNetworkAttributeRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Network_Attribute,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialNetworkAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Network_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialNetworkRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Network, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Existential, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ExistentialRuleform existential,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, existential.getId(),
                                         ReferenceType.Existential, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   FacetRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Facet, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   JobChronologyRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Job_Chronology,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   JobRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Job, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   MetaProtocolRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Meta_Protocol,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   NetworkInferenceRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Network_Inference,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ParentSequencingAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Parent_Sequencing_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   ProtocolRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Protocol, updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   SelfSequencingAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Self_Sequencing_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   SiblingSequencingAuthorizationRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Sibling_Sequencing_Authorization,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   StatusCodeSequencingRecord record,
                                                                   Agency updatedBy) {
        return newWorkspaceAuthorization(definingProduct, record.getId(),
                                         ReferenceType.Status_Code_Sequencing,
                                         updatedBy);
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(Product definingProduct,
                                                                   UUID reference,
                                                                   ReferenceType referenceType,
                                                                   Agency updatedBy) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization();
        record.setDefiningProduct(definingProduct.getId());
        record.setReference(reference);
        record.setUpdatedBy(updatedBy.getId());
        record.setType(referenceType);
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   UUID referece,
                                                                   ReferenceType referenceType,
                                                                   Product definingProduct,
                                                                   Agency updatedBy) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization(definingProduct,
                                                                        referece,
                                                                        referenceType,
                                                                        updatedBy);
        record.setKey(key);
        return record;
    }

    default ExistentialRuleform resolve(ExistentialRecord record) {
        if (record == null) {
            return null;
        }
        switch (record.getDomain()) {
            case Agency:
                return record.into(Agency.class);
            case Interval:
                return record.into(Interval.class);
            case Location:
                return record.into(Location.class);
            case Product:
                return record.into(Product.class);
            case Relationship:
                return record.into(Relationship.class);
            case StatusCode:
                return record.into(StatusCode.class);
            case Attribute:
                return record.into(Attribute.class);
            case Unit:
                return record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    @SuppressWarnings("unchecked")
    default <T extends ExistentialRuleform> T resolve(UUID id) {
        ExistentialRecord record = create().selectFrom(EXISTENTIAL)
                                           .where(EXISTENTIAL.ID.equal(id))
                                           .fetchOne();
        if (record == null) {
            return null;
        }
        switch (record.getDomain()) {
            case Agency:
                return (T) record.into(Agency.class);
            case Interval:
                return (T) record.into(Interval.class);
            case Location:
                return (T) record.into(Location.class);
            case Product:
                return (T) record.into(Product.class);
            case Relationship:
                return (T) record.into(Relationship.class);
            case StatusCode:
                return (T) record.into(StatusCode.class);
            case Attribute:
                return (T) record.into(Attribute.class);
            case Unit:
                return (T) record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    default AgencyExistentialGroupingRecord resolveAgencyExistentialGrouping(UUID id) {
        return create().selectFrom(AGENCY_EXISTENTIAL_GROUPING)
                       .where(AGENCY_EXISTENTIAL_GROUPING.ID.equal(id))
                       .fetchOne();
    }

    default ChildSequencingAuthorizationRecord resolveChildSequencingAuthorization(UUID id) {
        return create().selectFrom(CHILD_SEQUENCING_AUTHORIZATION)
                       .where(CHILD_SEQUENCING_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialAttributeRecord resolveExistentialAttribute(UUID id) {
        return create().selectFrom(EXISTENTIAL_ATTRIBUTE)
                       .where(EXISTENTIAL_ATTRIBUTE.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialAttributeAuthorizationRecord resolveExistentialAttributeAuthorization(UUID id) {
        return create().selectFrom(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                       .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialNetworkRecord resolveExistentialNetwork(UUID id) {
        return create().selectFrom(EXISTENTIAL_NETWORK)
                       .where(EXISTENTIAL_NETWORK.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialNetworkAttributeRecord resolveExistentialNetworkAttribute(UUID id) {
        return create().selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE)
                       .where(EXISTENTIAL_NETWORK_ATTRIBUTE.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialNetworkAttributeAuthorizationRecord resolveExistentialNetworkAttributeAuthorization(UUID id) {
        return create().selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                       .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default ExistentialNetworkAuthorizationRecord resolveExistentialNetworkAuthorization(UUID id) {
        return create().selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                       .where(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default FacetRecord resolveFacet(UUID id) {
        return create().selectFrom(FACET)
                       .where(FACET.ID.equal(id))
                       .fetchOne();
    }

    default JobRecord resolveJob(UUID id) {
        return create().selectFrom(JOB)
                       .where(JOB.ID.equal(id))
                       .fetchOne();
    }

    default JobChronologyRecord resolveJobChronology(UUID id) {
        return create().selectFrom(JOB_CHRONOLOGY)
                       .where(JOB_CHRONOLOGY.ID.equal(id))
                       .fetchOne();
    }

    default MetaProtocolRecord resolveMetaProtocol(UUID id) {
        return create().selectFrom(META_PROTOCOL)
                       .where(META_PROTOCOL.ID.equal(id))
                       .fetchOne();
    }

    default NetworkInferenceRecord resolveNetworkInferencel(UUID id) {
        return create().selectFrom(NETWORK_INFERENCE)
                       .where(NETWORK_INFERENCE.ID.equal(id))
                       .fetchOne();
    }

    default ParentSequencingAuthorizationRecord resolveParentSequencingAuthorization(UUID id) {
        return create().selectFrom(PARENT_SEQUENCING_AUTHORIZATION)
                       .where(PARENT_SEQUENCING_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default ProtocolRecord resolveProtocolRecord(UUID id) {
        return create().selectFrom(PROTOCOL)
                       .where(PROTOCOL.ID.equal(id))
                       .fetchOne();
    }

    default SelfSequencingAuthorizationRecord resolveSelfSequencingAuthorization(UUID id) {
        return create().selectFrom(SELF_SEQUENCING_AUTHORIZATION)
                       .where(SELF_SEQUENCING_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default SiblingSequencingAuthorizationRecord resolveSiblingSequencingAuthorization(UUID id) {
        return create().selectFrom(SIBLING_SEQUENCING_AUTHORIZATION)
                       .where(SIBLING_SEQUENCING_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

    default StatusCodeSequencingRecord resolveStatusCodeSequencing(UUID id) {
        return create().selectFrom(STATUS_CODE_SEQUENCING)
                       .where(STATUS_CODE_SEQUENCING.ID.equal(id))
                       .fetchOne();
    }

    default WorkspaceAuthorizationRecord resolveWorkspaceAuthorizationRecord(UUID id) {
        return create().selectFrom(WORKSPACE_AUTHORIZATION)
                       .where(WORKSPACE_AUTHORIZATION.ID.equal(id))
                       .fetchOne();
    }

}