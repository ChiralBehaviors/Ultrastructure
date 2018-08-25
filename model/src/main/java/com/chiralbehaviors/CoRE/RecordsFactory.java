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

import static com.chiralbehaviors.CoRE.jooq.Tables.CHILD_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE_PROPERTY;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET_PROPERTY;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB_CHRONOLOGY;
import static com.chiralbehaviors.CoRE.jooq.Tables.META_PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.NETWORK_INFERENCE;
import static com.chiralbehaviors.CoRE.jooq.Tables.PARENT_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.PROTOCOL;
import static com.chiralbehaviors.CoRE.jooq.Tables.SELF_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.SIBLING_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.STATUS_CODE_SEQUENCING;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_LABEL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record1;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
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
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public interface RecordsFactory {
    static NoArgGenerator GENERATOR    = Generators.timeBasedGenerator();
    static String         SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    static void clear(DSLContext create) throws SQLException {
        Connection connection = create.configuration()
                                      .connectionProvider()
                                      .acquire();
        ResultSet r = connection.createStatement()
                                .executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("TRUNCATE TABLE %s CASCADE", table);
            connection.createStatement()
                      .execute(query);
        }
        r.close();
    }

    static ExistentialRuleform resolveRecord(ExistentialRecord record) {
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
            case Unit:
                return record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    default ExistentialRecord copy(ExistentialRecord rf) {
        ExistentialRecord copy = rf.copy();
        copy.setId(GENERATOR.generate());
        return copy;

    }

    DSLContext create();

    default ExistentialRuleform createExistential(UUID classification,
                                                  String name,
                                                  String description) {
        ExistentialRecord clazz = create().selectFrom(EXISTENTIAL)
                                          .where(EXISTENTIAL.ID.equal(classification))
                                          .fetchOne();
        ExistentialRecord record = create().newRecord(EXISTENTIAL);
        record.setId(GENERATOR.generate());
        record.setName(name);
        record.setDescription(description);
        record.setUpdatedBy(currentPrincipalId());
        record.setDomain(clazz.getDomain());
        return resolve(record);
    }

    UUID currentPrincipalId();

    default String existentialName(UUID id) {
        Record1<String> existential = create().select(EXISTENTIAL.NAME)
                                              .from(EXISTENTIAL)
                                              .where(EXISTENTIAL.ID.eq(id))
                                              .fetchOne();
        if (existential == null) {
            return null;
        }
        return existential.value1();
    }

    default FacetRecord findFacetRecord(UUID id) {
        return create().selectFrom(FACET)
                       .where(FACET.ID.equal(id))
                       .fetchOne();
    }

    default Agency newAgency() {
        Agency record = create().newRecord(EXISTENTIAL)
                                .into(Agency.class);
        // special case the circularity issue with Core
        UUID p = currentPrincipalId();
        record.setUpdatedBy(p == null ? null : p);
        record.setDomain(ExistentialDomain.Agency);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Agency newAgency(String name) {
        Agency record = newAgency();
        record.setName(name);
        return record;
    }

    default Agency newAgency(String name, String description) {
        Agency record = newAgency();
        record.setName(name);
        record.setDescription(description);
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization() {
        ChildSequencingAuthorizationRecord record = create().newRecord(CHILD_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(Product service,
                                                                               StatusCode status,
                                                                               Product child,
                                                                               StatusCode next) {
        return newChildSequencingAuthorization(service.getId(), status.getId(),
                                               child.getId(), next.getId());
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(UUID service,
                                                                               UUID status,
                                                                               UUID child,
                                                                               UUID next) {
        ChildSequencingAuthorizationRecord record = newChildSequencingAuthorization();
        record.setService(service);
        record.setStatusCode(status);
        record.setNextChild(child);
        record.setNextChildStatus(next);
        return record;
    }

    default EdgePropertyRecord newEdgeProperty() {
        EdgePropertyRecord record = create().newRecord(EDGE_PROPERTY);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
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
            case Unit:
                existential = Unit.class;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 domain));
        }
        ExistentialRecord record = create().newRecord(EXISTENTIAL)
                                           .into(existential);
        record.setUpdatedBy(currentPrincipalId());
        record.setDomain(domain);
        record.setId(GENERATOR.generate());
        return (ExistentialRuleform) record;
    }

    default ExistentialRecord newExistential(ExistentialDomain domain,
                                             String name) {
        ExistentialRecord record = (ExistentialRecord) newExistential(domain);
        record.setUpdatedBy(currentPrincipalId());
        record.setName(name);
        return record;
    }

    default EdgeRecord newExistentialNetwork() {
        EdgeRecord record = create().newRecord(EDGE);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default EdgeRecord newExistentialNetwork(ExistentialRuleform parent,
                                             Relationship relationship,
                                             ExistentialRuleform child) {
        EdgeRecord record = newExistentialNetwork();
        record.setParent(parent.getId());
        record.setRelationship(relationship.getId());
        record.setChild(child.getId());
        return record;
    }

    default EdgeAuthorizationRecord newExistentialNetworkAuthorization() {
        EdgeAuthorizationRecord record = create().newRecord(EDGE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default FacetRecord newFacet() {
        FacetRecord record = create().newRecord(FACET);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default FacetRecord newFacet(Relationship classifier,
                                 ExistentialRuleform classification) {
        FacetRecord record = newFacet();
        record.setClassifier(classifier.getId());
        record.setClassification(classification.getId());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default FacetPropertyRecord newFacetProperty() {
        FacetPropertyRecord record = create().newRecord(FACET_PROPERTY);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default Interval newInterval() {
        Interval record = create().newRecord(EXISTENTIAL)
                                  .into(Interval.class);
        record.setDomain(ExistentialDomain.Interval);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default JobRecord newJob() {
        JobRecord record = create().newRecord(JOB);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default JobChronologyRecord newJobChronology() {
        JobChronologyRecord record = create().newRecord(JOB_CHRONOLOGY);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default JobChronologyRecord newJobChronology(JobRecord job, String notes) {
        JobChronologyRecord record = newJobChronology();
        record.setNotes(notes);
        record.setAssignTo(job.getAssignTo());
        record.setDeliverFrom(job.getDeliverFrom());
        record.setDeliverTo(job.getDeliverTo());
        record.setJob(job.getId());
        record.setProduct(job.getProduct());
        record.setQuantity(job.getQuantity());
        record.setQuantityUnit(job.getQuantityUnit());
        record.setRequester(job.getRequester());
        record.setStatus(job.getStatus());
        record.setUpdatedBy(job.getUpdatedBy());
        record.setService(job.getService());
        record.setUpdateDate(OffsetDateTime.now());
        return record;

    }

    default Location newLocation() {
        Location record = create().newRecord(EXISTENTIAL)
                                  .into(Location.class);
        record.setDomain(ExistentialDomain.Location);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default Location newLocation(String string) {
        Location l = newLocation();
        l.setName(string);
        return l;
    }

    default Location newLocation(String name, String description) {
        Location location = newLocation(name);
        location.setDescription(description);
        return location;

    }

    default MetaProtocolRecord newMetaProtocol() {
        MetaProtocolRecord record = create().newRecord(META_PROTOCOL);
        record.setId(GENERATOR.generate());
        return record;
    }

    default NetworkInferenceRecord newNetworkInference(Relationship premise1,
                                                       Relationship premise2,
                                                       Relationship inference) {
        NetworkInferenceRecord record = newNetworkInference(premise1.getId(),
                                                            premise2.getId(),
                                                            inference.getId());
        return record;
    }

    default NetworkInferenceRecord newNetworkInference(UUID premise1,
                                                       UUID premise2,
                                                       UUID inference) {
        NetworkInferenceRecord record = newNetworkInferrence();
        record.setPremise1(premise1);
        record.setPremise2(premise2);
        record.setInference(inference);
        return record;
    }

    default NetworkInferenceRecord newNetworkInferrence() {
        NetworkInferenceRecord record = create().newRecord(NETWORK_INFERENCE);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ParentSequencingAuthorizationRecord newParentSequencingAuthorization() {
        ParentSequencingAuthorizationRecord record = create().newRecord(PARENT_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ParentSequencingAuthorizationRecord newParentSequencingAuthorization(Product service,
                                                                                 StatusCode status,
                                                                                 Product parent,
                                                                                 StatusCode next) {
        return newParentSequencingAuthorization(service.getId(), status.getId(),
                                                parent.getId(), next.getId());
    }

    default ParentSequencingAuthorizationRecord newParentSequencingAuthorization(UUID service,
                                                                                 UUID status,
                                                                                 UUID parent,
                                                                                 UUID next) {
        ParentSequencingAuthorizationRecord record = newParentSequencingAuthorization();
        record.setService(service);
        record.setStatusCode(status);
        record.setParent(parent);
        record.setParentStatusToSet(next);
        return record;
    }

    default Product newProduct() {
        Product record = create().newRecord(EXISTENTIAL)
                                 .into(Product.class);
        record.setDomain(ExistentialDomain.Product);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default Product newProduct(String name) {
        Product record = newProduct();
        record.setName(name);
        return record;
    }

    default Product newProduct(String name, String description) {
        Product record = newProduct(name);
        record.setDescription(description);
        return record;
    }

    default ProtocolRecord newProtocol() {
        ProtocolRecord record = create().newRecord(PROTOCOL);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default Relationship newRelationship() {
        Relationship record = create().newRecord(EXISTENTIAL)
                                      .into(Relationship.class);
        record.setDomain(ExistentialDomain.Relationship);
        record.setUpdatedBy(currentPrincipalId());
        record.setId(GENERATOR.generate());
        return record;
    }

    default Relationship newRelationship(String name) {
        Relationship record = newRelationship();
        record.setName(name);
        return record;
    }

    default Relationship newRelationship(String name, Relationship inverse) {
        return newRelationship(name, null, inverse);
    }

    default Relationship newRelationship(String name, String description) {
        Relationship record = newRelationship();
        record.setName(name);
        record.setDescription(description);
        return record;
    }

    default Relationship newRelationship(String name, String description,
                                         Relationship inverse) {
        Relationship record = newRelationship(name, description);
        record.setInverse(inverse.getId());
        inverse.setInverse(record.getId());
        return record;
    }

    default Tuple<Relationship, Relationship> newRelationship(String name,
                                                              String description,
                                                              String inverseName,
                                                              String inverseDescription) {
        Relationship record = newRelationship(name, description);
        Relationship inverse = newRelationship(inverseName, inverseDescription);
        record.setInverse(inverse.getId());
        inverse.setInverse(record.getId());
        return new Tuple<>(record, inverse);
    }

    default SelfSequencingAuthorizationRecord newSelfSequencingAuthorization() {
        SelfSequencingAuthorizationRecord record = create().newRecord(SELF_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default SelfSequencingAuthorizationRecord newSelfSequencingAuthorization(Product service,
                                                                             StatusCode status,
                                                                             StatusCode next) {
        return newSelfSequencingAuthorization(service.getId(), status.getId(),
                                              next.getId());
    }

    default SelfSequencingAuthorizationRecord newSelfSequencingAuthorization(UUID service,
                                                                             UUID status,
                                                                             UUID next) {
        SelfSequencingAuthorizationRecord record = newSelfSequencingAuthorization();
        record.setService(service);
        record.setStatusCode(status);
        record.setStatusToSet(next);
        return record;
    }

    default SiblingSequencingAuthorizationRecord newSiblingSequencingAuthorization() {
        SiblingSequencingAuthorizationRecord record = create().newRecord(SIBLING_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default SiblingSequencingAuthorizationRecord newSiblingSequencingAuthorization(UUID service,
                                                                                   UUID status,
                                                                                   UUID sibling,
                                                                                   UUID next) {
        SiblingSequencingAuthorizationRecord record = newSiblingSequencingAuthorization();
        record.setService(service);
        record.setStatusCode(status);
        record.setNextSibling(sibling);
        record.setNextSiblingStatus(next);
        return record;
    }

    default StatusCode newStatusCode() {
        StatusCode record = create().newRecord(EXISTENTIAL)
                                    .into(StatusCode.class);
        record.setDomain(ExistentialDomain.StatusCode);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default StatusCode newStatusCode(String name) {
        StatusCode code = newStatusCode();
        code.setName(name);
        return code;
    }

    default StatusCode newStatusCode(String name, String description) {
        StatusCode record = newStatusCode(name);
        record.setDescription(description);
        return record;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing() {
        StatusCodeSequencingRecord record = create().newRecord(STATUS_CODE_SEQUENCING);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default StatusCodeSequencingRecord newStatusCodeSequencing(Product service,
                                                               StatusCode parent,
                                                               StatusCode child) {
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
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default Unit newUnit(String name, String description) {
        Unit unit = newUnit();
        unit.setName(name);
        unit.setDescription(description);
        return unit;
    }

    default WorkspaceLabelRecord newWorkspaceLabel() {
        WorkspaceLabelRecord record = create().newRecord(WORKSPACE_LABEL);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   ChildSequencingAuthorizationRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Child_Sequencing_Authorization);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   EdgePropertyRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Edge_Property);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   EdgeAuthorizationRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Network_Authorization);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   EdgeRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Network);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   ExistentialRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Existential);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   FacetPropertyRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Facet_Property);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   FacetRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Facet);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   JobChronologyRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Job_Chronology);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   JobRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Job);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   MetaProtocolRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Meta_Protocol);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   NetworkInferenceRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Network_Inference);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   ParentSequencingAuthorizationRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Parent_Sequencing_Authorization);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   ProtocolRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Protocol);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   SelfSequencingAuthorizationRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Self_Sequencing_Authorization);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   SiblingSequencingAuthorizationRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Sibling_Sequencing_Authorization);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   StatusCodeSequencingRecord record) {
        return newWorkspaceLabel(key, definingProduct, record.getId(),
                                 ReferenceType.Status_Code_Sequencing);
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key,
                                                   Product definingProduct,
                                                   UUID reference,
                                                   ReferenceType referenceType) {
        WorkspaceLabelRecord record = newWorkspaceLabel();
        record.setKey(key);
        record.setId(GENERATOR.generate());
        record.setWorkspace(definingProduct.getId());
        record.setReference(reference);
        record.setType(referenceType);
        return record;
    }

    default WorkspaceLabelRecord newWorkspaceLabel(String key, UUID referece,
                                                   ReferenceType referenceType,
                                                   Product definingProduct) {
        WorkspaceLabelRecord record = newWorkspaceLabel(key, definingProduct,
                                                        referece,
                                                        referenceType);
        return record;
    }

    default ExistentialRuleform resolve(ExistentialRecord record) {
        return resolveRecord(record);
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
            case Unit:
                return (T) record.into(Unit.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown domain %s",
                                                                 record.getDomain()));
        }
    }

    default JobRecord resolveJob(UUID id) {
        return create().selectFrom(JOB)
                       .where(JOB.ID.equal(id))
                       .fetchOne();
    }
}