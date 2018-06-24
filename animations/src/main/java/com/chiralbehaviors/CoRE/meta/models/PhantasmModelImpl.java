/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.fasterxml.jackson.databind.JsonNode;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelImpl implements PhantasmModel {
    private static final Integer ZERO = Integer.valueOf(0);

    private final DSLContext     create;
    private final Model          model;

    public PhantasmModelImpl(Model model) {
        this.model = model;
        create = model.create();
    }

    @Override
    public void authorize(FacetRecord facet, Attribute attribute) {
        ExistentialAttributeAuthorizationRecord record = model.records()
                                                              .newExistentialAttributeAuthorization(facet,
                                                                                                    attribute);
        record.insert();
    }

    @SafeVarargs
    @Override
    public final <T extends ExistentialRuleform> T create(ExistentialDomain domain,
                                                          String name,
                                                          String description,
                                                          FacetRecord aspect,
                                                          FacetRecord... aspects) {
        ExistentialRuleform instance = model.records()
                                            .newExistential(domain);
        model.getPhantasmModel()
             .initialize(instance, aspect);
        for (FacetRecord additional : aspects) {
            model.getPhantasmModel()
                 .initialize(instance, additional);
        }
        @SuppressWarnings("unchecked")
        T cazt = (T) instance;
        return cazt;
    }

    @Override
    public ExistentialNetworkAttributeRecord create(ExistentialNetworkRecord edge,
                                                    Attribute attribute) {
        return model.records()
                    .newExistentialNetworkAttribute(edge, attribute);
    }

    @Override
    public ExistentialAttributeRecord create(ExistentialRuleform existential,
                                             Attribute attribute) {
        ExistentialAttributeRecord value = model.records()
                                                .newExistentialAttribute(existential,
                                                                         attribute);
        return value;
    }

    @Override
    public List<? extends ExistentialRuleform> findByAttributeValue(Attribute attribute,
                                                                    Object query) {
        Condition valueEq;
        switch (attribute.getValueType()) {
            case Binary:
                valueEq = EXISTENTIAL_ATTRIBUTE.BINARY_VALUE.eq((byte[]) query);
                break;
            case Integer:
                valueEq = EXISTENTIAL_ATTRIBUTE.INTEGER_VALUE.eq((Integer) query);
                break;
            case Boolean:
                valueEq = EXISTENTIAL_ATTRIBUTE.BOOLEAN_VALUE.eq((Boolean) query);
                break;
            case JSON:
                throw new IllegalArgumentException("find by JSON value unsupported");
            case Numeric:
                valueEq = EXISTENTIAL_ATTRIBUTE.NUMERIC_VALUE.eq((BigDecimal) query);
                break;
            case Text:
                valueEq = EXISTENTIAL_ATTRIBUTE.TEXT_VALUE.eq((String) query);
                break;
            case Timestamp:
                valueEq = EXISTENTIAL_ATTRIBUTE.TIMESTAMP_VALUE.eq((OffsetDateTime) query);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown value type: %s",
                                                              attribute.getValueType()));
        }

        return model.create()
                    .selectDistinct(EXISTENTIAL.fields())
                    .from(EXISTENTIAL)
                    .join(EXISTENTIAL_ATTRIBUTE)
                    .on(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                    .and(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(EXISTENTIAL.ID))
                    .and(valueEq)
                    .fetch()
                    .into(ExistentialRecord.class)
                    .stream()
                    .map(r -> model.records()
                                   .resolve(r))
                    .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(FacetRecord aspect,
                                                                                    boolean includeGrouping) {

        SelectConditionStep<Record> and = create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                                                .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET.equal(aspect.getId()));
        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialAttributeAuthorizationRecord.class)
                  .stream()
                  .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialAttributeRecord> getAttributesClassifiedBy(ExistentialRuleform ruleform,
                                                                      FacetRecord facet) {
        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE.fields())
                     .from(EXISTENTIAL_ATTRIBUTE)
                     .join(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                     .on(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE))
                     .and(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.equal(ruleform.getId()))
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    @Override
    public ExistentialNetworkAttributeRecord getAttributeValue(ExistentialNetworkRecord edge,
                                                               Attribute attribute) {
        ExistentialNetworkAttributeRecord result = create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE)
                                                         .where(EXISTENTIAL_NETWORK_ATTRIBUTE.EDGE.eq(edge.getId()))
                                                         .and(EXISTENTIAL_NETWORK_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                                                         .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkAttributeRecord.class);
    }

    @Override
    public ExistentialAttributeRecord getAttributeValue(ExistentialRuleform ruleform,
                                                        Attribute attribute) {
        List<ExistentialAttributeRecord> values = getAttributeValues(ruleform,
                                                                     attribute);
        if (values.size() > 1) {
            throw new IllegalStateException(String.format("%s has multiple values for %s",
                                                          attribute, ruleform));
        }
        if (values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public ExistentialNetworkAttributeRecord getAttributeValue(ExistentialRuleform parent,
                                                               Relationship r,
                                                               ExistentialRuleform child,
                                                               Attribute attribute) {
        ExistentialNetworkRecord edge = getImmediateChildLink(parent, r, child);
        if (edge == null) {
            return null;
        }
        return getAttributeValue(edge, attribute);
    }

    @Override
    public List<ExistentialNetworkAttributeRecord> getAttributeValues(ExistentialNetworkRecord edge,
                                                                      Attribute attribute) {
        return create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE)
                     .where(EXISTENTIAL_NETWORK_ATTRIBUTE.EDGE.eq(edge.getId()))
                     .and(EXISTENTIAL_NETWORK_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                     .orderBy(EXISTENTIAL_NETWORK_ATTRIBUTE.SEQUENCE_NUMBER)
                     .fetch()
                     .into(ExistentialNetworkAttributeRecord.class);
    }

    @Override
    public List<ExistentialAttributeRecord> getAttributeValues(ExistentialRuleform ruleform,
                                                               Attribute attribute) {
        return create.selectFrom(EXISTENTIAL_ATTRIBUTE)
                     .where(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                     .orderBy(EXISTENTIAL_ATTRIBUTE.SEQUENCE_NUMBER)
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    @Override
    public ExistentialRuleform getChild(ExistentialRuleform parent,
                                        Relationship relationship,
                                        ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return model.records()
                    .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public List<ExistentialRuleform> getChildren(ExistentialRuleform parent,
                                                 Relationship relationship,
                                                 ExistentialDomain domain) {
        return getChildrenUuid(parent.getId(), relationship.getId(), domain);
    }

    @Override
    public List<ExistentialNetworkRecord> getChildrenLinks(ExistentialRuleform parent,
                                                           Relationship relationship) {

        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .fetch();
    }

    @Override
    public List<ExistentialRuleform> getChildrenUuid(UUID parent,
                                                     UUID relationship,
                                                     ExistentialDomain domain) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialRuleform> getConstrainedChildren(ExistentialRuleform parent,
                                                            Relationship relationship,
                                                            Relationship classifier,
                                                            ExistentialRuleform classification,
                                                            ExistentialDomain existentialDomain) {
        return getConstrainedChildren(parent.getId(), relationship.getId(),
                                      classifier.getId(),
                                      classification.getId(),
                                      existentialDomain);
    }

    @Override
    public FacetRecord getFacetDeclaration(Relationship classifier,
                                           ExistentialRuleform classification) {
        return create.selectFrom(FACET)
                     .where(FACET.CLASSIFIER.equal(classifier.getId()))
                     .and(FACET.CLASSIFICATION.equal(classification.getId()))
                     .fetchOne();
    }

    @Override
    public List<FacetRecord> getFacets(Product workspace) {
        return create.selectDistinct(FACET.fields())
                     .from(FACET)
                     .join(EXISTENTIAL)
                     .on(FACET.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .and(FACET.AUTHORITY.isNull())
                     .where(FACET.WORKSPACE.equal(workspace.getId()))
                     .fetch()
                     .into(FacetRecord.class);
    }

    @Override
    public ExistentialRuleform getImmediateChild(ExistentialRuleform parent,
                                                 Relationship relationship,
                                                 ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return model.records()
                    .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public ExistentialNetworkRecord getImmediateChildLink(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialRuleform child) {
        ExistentialNetworkRecord result = create.selectFrom(EXISTENTIAL_NETWORK)
                                                .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                                .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                                                .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                                .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialDomain domain) {
        return getImmediateChildren(parent.getId(), relationship.getId(),
                                    domain);
    }

    @Override
    public List<ExistentialNetworkRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
                                                                    Relationship relationship,
                                                                    ExistentialDomain domain) {
        Result<Record> result = create.selectDistinct(EXISTENTIAL_NETWORK.fields())
                                      .from(EXISTENTIAL_NETWORK)
                                      .join(EXISTENTIAL)
                                      .on(EXISTENTIAL.DOMAIN.equal(domain))
                                      .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                                      .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                      .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                      .fetch();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateConstrainedChildren(ExistentialRuleform parent,
                                                                     Relationship relationship,
                                                                     Relationship classifier,
                                                                     ExistentialRuleform classification,
                                                                     ExistentialDomain existentialDomain) {
        return getImmediateConstrainedChildren(parent.getId(),
                                               relationship.getId(),
                                               classifier.getId(),
                                               classification.getId(),
                                               existentialDomain);
    }

    @Override
    public ExistentialNetworkRecord getImmediateLink(ExistentialRuleform parent,
                                                     Relationship relationship,
                                                     ExistentialRuleform child) {
        ExistentialNetworkRecord result = create.selectFrom(EXISTENTIAL_NETWORK)
                                                .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                                .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                                                .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                                .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialNetworkAttributeAuthorizationRecord> getNetworkAttributeAuthorizations(ExistentialNetworkAuthorizationRecord auth) {
        return create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                     .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.eq(auth.getId()))
                     .fetch()
                     .into(ExistentialNetworkAttributeAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(FacetRecord aspect,
                                                                                boolean includeGrouping) {

        SelectConditionStep<ExistentialNetworkAuthorizationRecord> and = create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(aspect.getId()));

        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialDomain domain) {

        return create.selectFrom(EXISTENTIAL)
                     .whereNotExists(create.selectFrom(EXISTENTIAL_NETWORK)
                                           .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                           .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                           .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID)))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform> T getSingleChild(ExistentialRuleform parent,
                                                            Relationship relationship,
                                                            ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .where(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return (T) model.records()
                        .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public Object getValue(ExistentialAttributeAuthorizationRecord attributeValue) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAuthorizedAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                return attributeValue.getBinaryValue();
            case Boolean:
                return attributeValue.getBooleanValue();
            case Integer:
                return attributeValue.getIntegerValue();
            case Numeric:
                return attributeValue.getNumericValue();
            case Text:
                return attributeValue.getTextValue();
            case Timestamp:
                return attributeValue.getTimestampValue();
            case JSON:
                return attributeValue.getJsonValue();
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    @Override
    public Object getValue(ExistentialAttributeRecord attributeValue) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                return attributeValue.getBinaryValue();
            case Boolean:
                return attributeValue.getBooleanValue();
            case Integer:
                return attributeValue.getIntegerValue();
            case Numeric:
                return attributeValue.getNumericValue();
            case Text:
                return attributeValue.getTextValue();
            case Timestamp:
                return attributeValue.getTimestampValue();
            case JSON:
                return attributeValue.getJsonValue();
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    @Override
    public Object getValue(ExistentialNetworkAttributeAuthorizationRecord attributeValue) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAuthorizedAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                return attributeValue.getBinaryValue();
            case Boolean:
                return attributeValue.getBooleanValue();
            case Integer:
                return attributeValue.getIntegerValue();
            case Numeric:
                return attributeValue.getNumericValue();
            case Text:
                return attributeValue.getTextValue();
            case Timestamp:
                return attributeValue.getTimestampValue();
            case JSON:
                return attributeValue.getJsonValue();
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    @Override
    public Object getValue(ExistentialNetworkAttributeRecord attributeValue) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                return attributeValue.getBinaryValue();
            case Boolean:
                return attributeValue.getBooleanValue();
            case Integer:
                return attributeValue.getIntegerValue();
            case Numeric:
                return attributeValue.getNumericValue();
            case Text:
                return attributeValue.getTextValue();
            case Timestamp:
                return attributeValue.getTimestampValue();
            case JSON:
                return attributeValue.getJsonValue();
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect) {
        initialize(ruleform, aspect, null);
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect,
                                 EditableWorkspace workspace) {
        if (!isAccessible(ruleform.getId(), aspect.getClassifier(),
                          aspect.getClassification())) {
            UUID inverseRelationship = ((Relationship) model.records()
                                                            .resolve(aspect.getClassifier())).getInverse();
            Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> links = link(ruleform.getId(),
                                                                                   aspect.getClassifier(),
                                                                                   aspect.getClassification(),
                                                                                   inverseRelationship);
            if (workspace != null) {
                workspace.add(links.a);
                workspace.add(links.b);
            }
        }
        for (ExistentialAttributeAuthorizationRecord authorization : getAttributeAuthorizations(aspect,
                                                                                                false)) {
            Attribute authorizedAttribute = create.selectFrom(EXISTENTIAL)
                                                  .where(EXISTENTIAL.ID.equal(authorization.getAuthorizedAttribute()))
                                                  .fetchOne()
                                                  .into(Attribute.class);
            if (!authorizedAttribute.getKeyed()
                && !authorizedAttribute.getIndexed()) {
                if (getAttributeValue(ruleform, authorizedAttribute) == null) {
                    ExistentialAttributeRecord attribute = create(ruleform,
                                                                  authorizedAttribute);
                    attribute.insert();
                    setValue(authorizedAttribute, attribute, authorization);
                    if (workspace != null) {
                        workspace.add(attribute);
                    }
                }
            }
        }
    }

    @Override
    public boolean isAccessible(UUID parent, UUID relationship, UUID child) {
        assert parent != null && relationship != null && child != null;
        return !ZERO.equals(create.selectCount()
                                  .from(EXISTENTIAL_NETWORK)
                                  .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                                  .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                                  .and(EXISTENTIAL_NETWORK.CHILD.equal(child))
                                  .and(EXISTENTIAL_NETWORK.CHILD.notEqual(parent))
                                  .fetchOne()
                                  .value1());
    }

    @Override
    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(ExistentialRuleform parent,
                                                                          Relationship r,
                                                                          ExistentialRuleform child) {
        return link(parent.getId(), r.getId(), child.getId(), r.getInverse());
    }

    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(UUID parent,
                                                                          UUID r,
                                                                          UUID child,
                                                                          UUID inverseR) {
        ExistentialNetworkRecord forward = model.records()
                                                .newExistentialNetwork();
        forward.setParent(parent);
        forward.setRelationship(r);
        forward.setChild(child);
        forward.insert();

        ExistentialNetworkRecord inverse = model.records()
                                                .newExistentialNetwork();
        inverse.setParent(child);
        inverse.setRelationship(inverseR);
        inverse.setChild(parent);
        inverse.insert();
        return new Tuple<>(forward, inverse);
    }

    @Override
    public void setImmediateChild(ExistentialRuleform parent,
                                  Relationship relationship,
                                  ExistentialRuleform child) {

        unlinkImmediate(parent, relationship);
        link(parent, relationship, child);

    }

    @Override
    public void setValue(ExistentialAttributeAuthorizationRecord auth,
                         Object value) {
        Attribute attribute = model.records()
                                   .resolve(auth.getAuthorizedAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                auth.setBinaryValue((byte[]) value);
                break;
            case Boolean:
                auth.setBooleanValue((Boolean) value);
                break;
            case Integer:
                auth.setIntegerValue((Integer) value);
                break;
            case Numeric:
                auth.setNumericValue((BigDecimal) value);
                break;
            case Text:
                auth.setTextValue((String) value);
                break;
            case Timestamp:
                auth.setTimestampValue((OffsetDateTime) value);
                break;
            case JSON:
                auth.setJsonValue((JsonNode) value);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }

        auth.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal()
                               .getId());
        auth.update();
    }

    @Override
    public void setValue(ExistentialAttributeRecord attributeValue,
                         Object value) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                attributeValue.setBinaryValue((byte[]) value);
                break;
            case Boolean:
                attributeValue.setBooleanValue((Boolean) value);
                break;
            case Integer:
                attributeValue.setIntegerValue((Integer) value);
                break;
            case Numeric:
                attributeValue.setNumericValue((BigDecimal) value);
                break;
            case Text:
                attributeValue.setTextValue((String) value);
                break;
            case Timestamp:
                attributeValue.setTimestampValue((OffsetDateTime) value);
                break;
            case JSON:
                attributeValue.setJsonValue((JsonNode) value);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
        attributeValue.setUpdatedBy(model.getCurrentPrincipal()
                                         .getPrincipal()
                                         .getId());
        attributeValue.setUpdated(OffsetDateTime.now());
        attributeValue.update();
    }

    @Override
    public void setValue(ExistentialNetworkAttributeAuthorizationRecord auth,
                         Object value) {
        Attribute attribute = model.records()
                                   .resolve(auth.getAuthorizedAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                auth.setBinaryValue((byte[]) value);
                break;
            case Boolean:
                auth.setBooleanValue((Boolean) value);
                break;
            case Integer:
                auth.setIntegerValue((Integer) value);
                break;
            case Numeric:
                auth.setNumericValue((BigDecimal) value);
                break;
            case Text:
                auth.setTextValue((String) value);
                break;
            case Timestamp:
                auth.setTimestampValue((OffsetDateTime) value);
                break;
            case JSON:
                auth.setJsonValue((JsonNode) value);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }

        auth.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal()
                               .getId());
        auth.update();
    }

    @Override
    public void setValue(ExistentialNetworkAttributeRecord attributeValue,
                         Object value) {
        Attribute attribute = model.records()
                                   .resolve(attributeValue.getAttribute());
        switch (attribute.getValueType()) {
            case Binary:
                attributeValue.setBinaryValue((byte[]) value);
                break;
            case Boolean:
                attributeValue.setBooleanValue((Boolean) value);
                break;
            case Integer:
                attributeValue.setIntegerValue((Integer) value);
                break;
            case Numeric:
                attributeValue.setNumericValue((BigDecimal) value);
                break;
            case Text:
                attributeValue.setTextValue((String) value);
                break;
            case Timestamp:
                attributeValue.setTimestampValue((OffsetDateTime) value);
                break;
            case JSON:
                attributeValue.setJsonValue((JsonNode) value);
                break;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }

        attributeValue.setUpdatedBy(model.getCurrentPrincipal()
                                         .getPrincipal()
                                         .getId());
        attributeValue.setUpdated(OffsetDateTime.now());
        attributeValue.update();
    }

    @Override
    public void unlink(ExistentialRuleform parent, Relationship relationship,
                       ExistentialRuleform child) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
              .execute();
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(child.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(parent.getId()))
              .execute();
    }

    @Override
    public void unlinkImmediate(ExistentialRuleform parent,
                                Relationship relationship) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.CHILD.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
    }

    @Override
    public Class<?> valueClass(Attribute attribute) {
        switch (attribute.getValueType()) {
            case Binary:
                return byte[].class;
            case Boolean:
                return Boolean.class;
            case Integer:
                return Integer.class;
            case Numeric:
                return BigDecimal.class;
            case Text:
                return String.class;
            case Timestamp:
                return Timestamp.class;
            case JSON:
                return Map.class;
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    private List<ExistentialRuleform> getConstrainedChildren(UUID parent,
                                                             UUID relationship,
                                                             UUID classifier,
                                                             UUID classification,
                                                             ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .filter(r -> isAccessible(r.getId(), classifier,
                                               classification))
                     .collect(Collectors.toList());
    }

    private List<ExistentialRuleform> getImmediateChildren(UUID parent,
                                                           UUID relationship,
                                                           ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    private List<ExistentialRuleform> getImmediateConstrainedChildren(UUID parent,
                                                                      UUID relationship,
                                                                      UUID classifier,
                                                                      UUID classification,
                                                                      ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .filter(r -> isAccessible(r.getId(), classifier,
                                               classification))
                     .collect(Collectors.toList());
    }

    private void setValue(Attribute attribute, ExistentialAttributeRecord value,
                          ExistentialAttributeAuthorizationRecord authorization) {
        switch (attribute.getValueType()) {
            case Binary:
                value.setBinaryValue(authorization.getBinaryValue());
                break;
            case Boolean:
                value.setBooleanValue(authorization.getBooleanValue());
                break;
            case Integer:
                value.setIntegerValue(authorization.getIntegerValue());
                break;
            case JSON:
                value.setJsonValue(authorization.getJsonValue());
                break;
            case Numeric:
                value.setNumericValue(authorization.getNumericValue());
                break;
            case Text:
                value.setTextValue(authorization.getTextValue());
                break;
            case Timestamp:
                value.setTimestampValue(authorization.getTimestampValue());
                break;
            default:
                throw new IllegalStateException(String.format("Unknown value type %s",
                                                              attribute.getValueType()));
        }
        value.setUpdated(OffsetDateTime.now());
        value.update();
    }
}
