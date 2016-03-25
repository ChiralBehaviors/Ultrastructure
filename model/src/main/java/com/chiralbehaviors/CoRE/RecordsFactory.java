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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
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
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public interface RecordsFactory {
    static NoArgGenerator GENERATOR    = Generators.timeBasedGenerator();
    static String         SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    static void clear(DSLContext em) throws SQLException {
        em.transaction(c -> {
            Connection connection = c.connectionProvider()
                                     .acquire();
            ResultSet r = connection.createStatement()
                                    .executeQuery(SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                String query = String.format("TRUNCATE TABLE %s CASCADE",
                                             table);
                connection.createStatement()
                          .execute(query);
            }
            r.close();
        });
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

    default Attribute newAttribute() {
        Attribute record = create().newRecord(EXISTENTIAL)
                                   .into(Attribute.class);
        record.setUpdatedBy(currentPrincipalId());
        record.setDomain(ExistentialDomain.Attribute);
        record.setId(GENERATOR.generate());
        return record;
    }

    default Attribute newAttribute(String name, String description) {
        Attribute record = newAttribute();
        record.setName(name);
        record.setDescription(description);
        return record;
    }

    default Attribute newAttribute(String name, String description,
                                   ValueType type) {
        Attribute attr = newAttribute(name, description);
        attr.setValueType(type);
        return attr;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization() {
        ChildSequencingAuthorizationRecord record = create().newRecord(CHILD_SEQUENCING_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(Product parent,
                                                                               StatusCode status,
                                                                               Product child,
                                                                               StatusCode next) {
        return newChildSequencingAuthorization(parent.getId(), status.getId(),
                                               child.getId(), next.getId());
    }

    default ChildSequencingAuthorizationRecord newChildSequencingAuthorization(UUID parent,
                                                                               UUID status,
                                                                               UUID child,
                                                                               UUID next) {
        ChildSequencingAuthorizationRecord record = newChildSequencingAuthorization();
        record.setParent(parent);
        record.setStatusCode(status);
        record.setNextChild(child);
        record.setNextChildStatus(next);
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
        record.setUpdatedBy(currentPrincipalId());
        record.setDomain(domain);
        record.setId(GENERATOR.generate());
        return (ExistentialRuleform) record;
    }

    default ExistentialRecord newExistential(ExistentialDomain domain,
                                             String name, String description) {
        ExistentialRecord record = (ExistentialRecord) newExistential(domain);
        record.setUpdatedBy(currentPrincipalId());
        record.setName(name);
        record.setDescription(description);
        return record;
    }

    default ExistentialAttributeRecord newExistentialAttribute() {
        ExistentialAttributeRecord record = create().newRecord(EXISTENTIAL_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        record.setUpdated(new Timestamp(System.currentTimeMillis()));
        return record;
    }

    default ExistentialAttributeRecord newExistentialAttribute(Attribute attr) {
        ExistentialAttributeRecord record = newExistentialAttribute();
        record.setAttribute(attr.getId());
        return record;
    }

    default ExistentialAttributeAuthorizationRecord newExistentialAttributeAuthorization() {
        ExistentialAttributeAuthorizationRecord record = create().newRecord(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ExistentialAttributeAuthorizationRecord newExistentialAttributeAuthorization(FacetRecord facet,
                                                                                         Attribute attribute) {
        ExistentialAttributeAuthorizationRecord record = newExistentialAttributeAuthorization();
        record.setAuthorizedAttribute(attribute.getId());
        record.setFacet(facet.getId());
        return record;
    }

    default AgencyExistentialGroupingRecord newExistentialGrouping() {
        AgencyExistentialGroupingRecord record = create().newRecord(AGENCY_EXISTENTIAL_GROUPING);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ExistentialNetworkRecord newExistentialNetwork() {
        ExistentialNetworkRecord record = create().newRecord(EXISTENTIAL_NETWORK);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ExistentialNetworkRecord newExistentialNetwork(ExistentialRuleform parent,
                                                           Relationship relationship,
                                                           ExistentialRuleform child) {
        ExistentialNetworkRecord record = newExistentialNetwork();
        record.setParent(parent.getId());
        record.setRelationship(relationship.getId());
        record.setChild(child.getId());
        return record;
    }

    default ExistentialNetworkAttributeRecord newExistentialNetworkAttribute() {
        ExistentialNetworkAttributeRecord record = create().newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE);
        record.setId(GENERATOR.generate());
        record.setUpdated(new Timestamp(System.currentTimeMillis()));
        return record;
    }

    default ExistentialNetworkAttributeRecord newExistentialNetworkAttribute(Attribute attribute) {
        ExistentialNetworkAttributeRecord record = newExistentialNetworkAttribute();
        record.setAttribute(attribute.getId());
        return record;
    }

    default ExistentialNetworkAttributeAuthorizationRecord newExistentialNetworkAttributeAuthorization() {
        ExistentialNetworkAttributeAuthorizationRecord record = create().newRecord(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default ExistentialNetworkAuthorizationRecord newExistentialNetworkAuthorization() {
        ExistentialNetworkAuthorizationRecord record = create().newRecord(EXISTENTIAL_NETWORK_AUTHORIZATION);
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

    default NetworkInferenceRecord newNetworkNetworkInference(Relationship premise1,
                                                              Relationship premise2,
                                                              Relationship inference) {
        return newNetworkInference(premise1.getId(), premise2.getId(),
                                   inference.getId());

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

    default SiblingSequencingAuthorizationRecord newSiblingSequencingAuthorization(UUID parent,
                                                                                   UUID status,
                                                                                   UUID sibling,
                                                                                   UUID next) {
        SiblingSequencingAuthorizationRecord record = newSiblingSequencingAuthorization();
        record.setParent(parent);
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

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization() {
        WorkspaceAuthorizationRecord record = create().newRecord(WORKSPACE_AUTHORIZATION);
        record.setId(GENERATOR.generate());
        record.setUpdatedBy(currentPrincipalId());
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   AgencyExistentialGroupingRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Agency_Existential_Grouping);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ChildSequencingAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Child_Sequencing_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialAttributeAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Attribute_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialAttributeRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Attribute);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialNetworkAttributeAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Network_Attribute_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialNetworkAttributeRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Network_Attribute);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialNetworkAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Network_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialNetworkRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Network);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ExistentialRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Existential);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   FacetRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Facet);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   JobChronologyRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Job_Chronology);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   JobRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Job);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   MetaProtocolRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Meta_Protocol);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   NetworkInferenceRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Network_Inference);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ParentSequencingAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Parent_Sequencing_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   ProtocolRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Protocol);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   SelfSequencingAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Self_Sequencing_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   SiblingSequencingAuthorizationRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Sibling_Sequencing_Authorization);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   StatusCodeSequencingRecord record) {
        WorkspaceAuthorizationRecord auth = newWorkspaceAuthorization(key,
                                                                      definingProduct,
                                                                      record.getId(),
                                                                      ReferenceType.Status_Code_Sequencing);
        record.setWorkspace(auth.getId());
        record.update();
        return auth;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   Product definingProduct,
                                                                   UUID reference,
                                                                   ReferenceType referenceType) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization();
        record.setKey(key);
        record.setId(GENERATOR.generate());
        record.setDefiningProduct(definingProduct.getId());
        record.setReference(reference);
        record.setType(referenceType);
        return record;
    }

    default WorkspaceAuthorizationRecord newWorkspaceAuthorization(String key,
                                                                   UUID referece,
                                                                   ReferenceType referenceType,
                                                                   Product definingProduct) {
        WorkspaceAuthorizationRecord record = newWorkspaceAuthorization(key,
                                                                        definingProduct,
                                                                        referece,
                                                                        referenceType);
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